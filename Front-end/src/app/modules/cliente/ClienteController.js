import FormComponent from "../../shared/components/FormComponent.js";
import NotificationService from "../../core/services/NotificationService.js";
import {
    CpfValidator,
    CnpjValidator,
    EmailValidator,
    NameValidator,
} from "../../core/services/ValidationService.js";
import MaskUtils from "../../core/utils/MaskUtils.js";
import AsyncUtils from "../../core/utils/AsyncUtils.js";

/**
 * Controlador para o módulo de cliente
 * Implementa padrão MVC e princípios SOLID
 */
class ClienteController {
    constructor() {
        this.formComponent = null;
        this.notificationService = new NotificationService();
        this.validators = this.initializeValidators();
        this.isInitialized = false;
    }

    /**
     * Inicializa o controlador
     */
    async init() {
        if (this.isInitialized) return;

        try {
            this.setupFormComponent();
            this.setupValidators();
            this.setupEventListeners();
            this.setupMasks();
            this.isInitialized = true;

            console.log("Cliente Controller inicializado com sucesso");
        } catch (error) {
            console.error("Erro ao inicializar Cliente Controller:", error);
            this.notificationService.showError("Erro ao inicializar a página");
        }
    }

    /**
     * Inicializa validadores
     * @returns {Object} Validadores
     */
    initializeValidators() {
        return {
            cpf: new CpfValidator(),
            cnpj: new CnpjValidator(),
            email: new EmailValidator(),
            name: new NameValidator(),
        };
    }

    /**
     * Configura componente de formulário
     */
    setupFormComponent() {
        this.formComponent = new FormComponent("form-cliente", {
            validateOnChange: true,
            validateOnSubmit: true,
            autoFocus: true,
        });

        this.formComponent.init();
    }

    /**
     * Configura validadores nos campos
     */
    setupValidators() {
        this.formComponent.addValidator("nome", this.validators.name);
        this.formComponent.addValidator("email", this.validators.email);
        this.formComponent.addValidator("cpf", this.validators.cpf);
        this.formComponent.addValidator("cnpj", this.validators.cnpj);
    }

    /**
     * Configura listeners de eventos
     */
    setupEventListeners() {
        // Listener para mudança de natureza
        this.setupNaturezaListener();

        // Listener para busca de CEP
        this.setupCepListener();

        // Listener para submit do formulário
        this.formComponent.on("submit", (event) => {
            this.handleFormSubmit(event.detail);
        });

        // Listener para listagem de clientes
        this.setupListClientesListener();
    }

    /**
     * Configura listener para mudança de natureza
     */
    setupNaturezaListener() {
        const naturezaSelect = document.getElementById("natureza");
        if (naturezaSelect) {
            naturezaSelect.addEventListener("change", (e) => {
                this.handleNaturezaChange(e.target.value);
            });

            // Dispara evento inicial
            this.handleNaturezaChange(naturezaSelect.value);
        }
    }

    /**
     * Manipula mudança de natureza
     * @param {string} natureza - Natureza selecionada
     */
    handleNaturezaChange(natureza) {
        const cpfField = document.getElementById("campo-cpf");
        const cnpjField = document.getElementById("campo-cnpj");

        if (cpfField && cnpjField) {
            if (natureza === "pessoa_fisica") {
                cpfField.style.display = "flex";
                cnpjField.style.display = "none";
                document.getElementById("cnpj").value = "";
            } else if (natureza === "pessoa_juridica") {
                cpfField.style.display = "none";
                cnpjField.style.display = "flex";
                document.getElementById("cpf").value = "";
            } else {
                cpfField.style.display = "none";
                cnpjField.style.display = "none";
            }
        }
    }

    /**
     * Configura listener para busca de CEP
     */
    setupCepListener() {
        const cepButton = document.getElementById("buscar-cep-btn");
        if (cepButton) {
            const debouncedBuscarCep = AsyncUtils.debounce(
                this.buscarCep.bind(this),
                500
            );

            cepButton.addEventListener("click", debouncedBuscarCep);
        }
    }

    /**
     * Configura listener para listar clientes
     */
    setupListClientesListener() {
        const listButton = document.getElementById("listar-clientes-btn");
        if (listButton) {
            listButton.addEventListener("click", () => {
                this.listarClientes();
            });
        }
    }

    /**
     * Configura máscaras nos campos
     */
    setupMasks() {
        const maskFields = [
            { id: "cpf", handler: MaskUtils.applyCpfMask },
            { id: "cnpj", handler: MaskUtils.applyCnpjMask },
            { id: "cep", handler: MaskUtils.applyCepMask },
            { id: "celular", handler: MaskUtils.applyCelularMask },
            { id: "telefoneFixo", handler: MaskUtils.applyFixoMask },
        ];

        maskFields.forEach(({ id, handler }) => {
            const field = document.getElementById(id);
            if (field) {
                field.addEventListener("input", (e) => {
                    e.target.value = handler(e.target.value);
                });
            }
        });
    }

    /**
     * Busca dados do CEP
     */
    async buscarCep() {
        const cepField = document.getElementById("cep");
        const cepButton = document.getElementById("buscar-cep-btn");

        if (!cepField || !cepButton) return;

        const cep = cepField.value.replace(/\D/g, "");

        if (cep.length !== 8) {
            this.notificationService.showError("CEP deve ter 8 dígitos");
            return;
        }

        try {
            cepButton.disabled = true;
            cepButton.textContent = "Buscando...";

            const data = await window.api.buscarCep(cep);

            if (data.erro) {
                this.notificationService.showError("CEP não encontrado");
                return;
            }

            this.preencherEndereco(data);
            this.notificationService.showSuccess("CEP encontrado!");
        } catch (error) {
            console.error("Erro ao buscar CEP:", error);
            this.notificationService.showError("Erro ao buscar CEP");
        } finally {
            cepButton.disabled = false;
            cepButton.textContent = "Buscar CEP";
        }
    }

    /**
     * Preenche campos de endereço
     * @param {Object} data - Dados do CEP
     */
    preencherEndereco(data) {
        const fieldMap = {
            endereco: "logradouro",
            bairro: "bairro",
            cidade: "localidade",
            uf: "uf",
            complemento: "complemento",
        };

        Object.entries(fieldMap).forEach(([fieldId, dataKey]) => {
            const field = document.getElementById(fieldId);
            if (field && data[dataKey]) {
                field.value = data[dataKey];
            }
        });
    }

    /**
     * Manipula envio do formulário
     * @param {Object} formData - Dados do formulário
     */
    async handleFormSubmit(formData) {
        try {
            const clienteData = this.formatClienteData(formData);

            const result = await window.api.cadastrarCliente(clienteData);

            if (result.success) {
                this.notificationService.showSuccess(
                    "Cliente cadastrado com sucesso!"
                );
                this.formComponent.reset();
                this.listarClientes();
            } else {
                this.notificationService.showError(
                    result.error || "Erro ao cadastrar cliente"
                );
            }
        } catch (error) {
            console.error("Erro ao cadastrar cliente:", error);
            this.notificationService.showError(
                "Erro inesperado ao cadastrar cliente"
            );
        }
    }

    /**
     * Formata dados do cliente
     * @param {Object} formData - Dados do formulário
     * @returns {Object} Dados formatados
     */
    formatClienteData(formData) {
        const natureza =
            formData.natureza === "pessoa_fisica"
                ? "PESSOA_FISICA"
                : "PESSOA_JURIDICA";

        return {
            nome: formData.nome?.trim(),
            email: formData.email?.trim(),
            natureza,
            cpf:
                natureza === "PESSOA_FISICA"
                    ? formData.cpf?.replace(/\D/g, "")
                    : null,
            cnpj:
                natureza === "PESSOA_JURIDICA"
                    ? formData.cnpj?.replace(/\D/g, "")
                    : null,
            dataNascimento: formData.dataNascimento,
            cep: formData.cep?.replace(/\D/g, ""),
            endereco: formData.endereco?.trim(),
            numero: formData.numero?.trim(),
            cidade: formData.cidade?.trim(),
            bairro: formData.bairro?.trim(),
            complemento: formData.complemento?.trim() || null,
            uf: formData.uf?.trim().toUpperCase(),
            celular: formData.celular?.replace(/\D/g, ""),
            telefoneFixo: formData.telefoneFixo?.replace(/\D/g, "") || null,
        };
    }

    /**
     * Lista clientes
     */
    async listarClientes() {
        try {
            const result = await window.api.listarClientes();

            if (result.success) {
                this.preencherTabelaClientes(result.data);
                this.notificationService.showSuccess(
                    "Clientes carregados com sucesso!"
                );
            } else {
                this.notificationService.showError(
                    result.error || "Erro ao carregar clientes"
                );
            }
        } catch (error) {
            console.error("Erro ao listar clientes:", error);
            this.notificationService.showError(
                "Erro ao carregar lista de clientes"
            );
        }
    }

    /**
     * Preenche tabela de clientes
     * @param {Array} clientes - Lista de clientes
     */
    preencherTabelaClientes(clientes = []) {
        const tbody = document.getElementById("cliente-lista");
        if (!tbody) return;

        tbody.innerHTML = "";

        if (clientes.length === 0) {
            const row = tbody.insertRow();
            const cell = row.insertCell();
            cell.colSpan = 14;
            cell.textContent = "Nenhum cliente encontrado";
            cell.style.textAlign = "center";
            cell.style.padding = "20px";
            cell.style.color = "#999";
            return;
        }

        clientes.forEach((cliente) => {
            const row = tbody.insertRow();

            const cells = [
                cliente.id || "",
                cliente.tipoPessoa || cliente.natureza || "",
                cliente.nome || "",
                cliente.dataNascimento
                    ? new Date(cliente.dataNascimento).toLocaleDateString(
                          "pt-BR"
                      )
                    : "",
                cliente.cep || "",
                cliente.endereco || "",
                cliente.numero || "",
                cliente.cidade || "",
                cliente.bairro || "",
                cliente.complemento || "",
                cliente.uf || "",
                cliente.email || "",
                cliente.celular || "",
                cliente.telefoneFixo || "",
            ];

            cells.forEach((cellValue) => {
                const cell = row.insertCell();
                cell.textContent = cellValue;
                cell.style.padding = "8px";
                cell.style.borderBottom = "1px solid #eee";
            });
        });
    }

    /**
     * Destrói o controlador
     */
    destroy() {
        if (this.formComponent) {
            this.formComponent.destroy();
        }
        this.isInitialized = false;
    }
}

// Inicializa o controlador quando o DOM estiver pronto
document.addEventListener("DOMContentLoaded", () => {
    const clienteController = new ClienteController();
    clienteController.init();

    // Torna disponível globalmente para debug
    window.clienteController = clienteController;
});

export default ClienteController;
