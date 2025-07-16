/**
 * Componente para cadastro de clientes
 * Implementa funcionalidades específicas para o formulário de cadastro
 */
import FormComponent from "../../../shared/components/FormComponent.js";
import NotificationService from "../../../core/services/NotificationService.js";
import {
    CpfValidator,
    CnpjValidator,
    EmailValidator,
    NameValidator,
} from "../../../core/services/ValidationService.js";
import { MaskUtils } from "../../../core/utils/MaskUtils.js";

class ClienteCadastroComponent {
    constructor() {
        this.formComponent = null;
        this.notificationService = new NotificationService();
        this.validators = this.initializeValidators();
        this.isInitialized = false;
    }

    /**
     * Inicializa o componente
     */
    async init() {
        if (this.isInitialized) return;

        try {
            this.setupFormComponent();
            this.setupValidators();
            this.setupEventListeners();
            this.setupMasks();
            this.isInitialized = true;
            console.log("Componente de cadastro de clientes inicializado com sucesso");
        } catch (error) {
            this.notificationService.showError(
                "Erro ao inicializar o componente de cadastro",
                error
            );
            console.error("Erro ao inicializar componente de cadastro:", error);
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
        console.log("Natureza selecionada:", natureza);
        
        const cpfField = document.getElementById("campo-cpf");
        const cnpjField = document.getElementById("campo-cnpj");

        if (cpfField && cnpjField) {
            if (natureza === "pessoa_fisica") {
                cpfField.style.display = "flex";
                cpfField.classList.remove("hidden");
                cnpjField.style.display = "none";
                cnpjField.classList.add("hidden");
                document.getElementById("cnpj").value = "";
                console.log("Campos alterados para Pessoa Física");
            } else if (natureza === "pessoa_juridica") {
                cpfField.style.display = "none";
                cpfField.classList.add("hidden");
                cnpjField.style.display = "flex";
                cnpjField.classList.remove("hidden");
                document.getElementById("cpf").value = "";
                console.log("Campos alterados para Pessoa Jurídica");
            } else {
                cpfField.style.display = "none";
                cpfField.classList.add("hidden");
                cnpjField.style.display = "none";
                cnpjField.classList.add("hidden");
                console.log("Campos CPF/CNPJ ocultos");
            }
        } else {
            console.warn("Campos CPF/CNPJ não encontrados");
        }
    }

    /**
     * Configura listener para busca de CEP
     */
    setupCepListener() {
        console.log("Configurando listener para busca de CEP");
        const cepButton = document.getElementById("buscar-cep-btn");

        if (cepButton) {
            cepButton.addEventListener("click", (e) => {
                e.preventDefault();
                console.log("Botão buscar CEP clicado");
                this.buscarCep();
            });
            console.log("Listener do botão buscar CEP configurado com sucesso");
        } else {
            console.warn("Botão buscar-cep-btn não encontrado no DOM");
        }
    }

    /**
     * Busca dados do CEP
     */
    async buscarCep() {
        console.log("Iniciando busca de CEP");
        
        const cepField = document.getElementById("cep");
        const cepButton = document.getElementById("buscar-cep-btn");

        if (!cepField || !cepButton) {
            console.error("Campo CEP ou botão não encontrado");
            return;
        }

        const cep = cepField.value.replace(/\D/g, "");
        console.log("CEP digitado:", cep);

        if (cep.length !== 8) {
            this.notificationService.showError("CEP deve ter 8 dígitos");
            return;
        }

        try {
            cepButton.disabled = true;
            cepButton.textContent = "Buscando...";

            console.log("Fazendo requisição para buscar CEP:", cep);
            
            // Verificar se window.api está disponível
            if (!window.api) {
                console.error("window.api não está disponível");
                this.notificationService.showError("API não está disponível");
                return;
            }

            const response = await window.api.buscarCep(cep);
            console.log("Resposta da API:", response);

            if (!response.success || response.data?.erro) {
                this.notificationService.showError("CEP não encontrado");
                return;
            }

            // Preenche os campos com os dados encontrados
            this.preencherEndereco(response.data);
            console.log("Campos preenchidos com sucesso");
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
        console.log("Preenchendo endereço com dados:", data);
        
        const fieldMap = {
            endereco: "logradouro",
            bairro: "bairro",
            cidade: "localidade",
            uf: "uf",
            complemento: "complemento",
        };

        Object.entries(fieldMap).forEach(([fieldId, dataKey]) => {
            const field = document.getElementById(fieldId);
            if (field) {
                if (data[dataKey]) {
                    field.value = data[dataKey];
                    console.log(`Campo ${fieldId} preenchido com: ${data[dataKey]}`);
                } else {
                    console.log(`Dado ${dataKey} não encontrado na resposta`);
                }
            } else {
                console.error(`Campo ${fieldId} não encontrado no DOM`);
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
            } else {
                this.notificationService.showError(
                    result.error || "Erro ao cadastrar cliente"
                );
            }
        } catch (error) {
            this.notificationService.showError(
                "Erro inesperado ao cadastrar cliente"
            );
            console.error("Erro ao cadastrar cliente:", error);
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
     * Configura máscaras nos campos
     */
    setupMasks() {
        const maskFields = [
            { id: "cpf", handler: (value) => MaskUtils.applyCpfMask(value) },
            { id: "cnpj", handler: (value) => MaskUtils.applyCnpjMask(value) },
            { id: "cep", handler: (value) => MaskUtils.applyCepMask(value) },
            { id: "celular", handler: (value) => MaskUtils.applyCelularMask(value) },
            { id: "telefoneFixo", handler: (value) => MaskUtils.applyFixoMask(value) },
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
     * Destrói o componente
     */
    destroy() {
        if (this.formComponent) {
            this.formComponent.destroy();
        }
        this.isInitialized = false;
    }
}

export default ClienteCadastroComponent;