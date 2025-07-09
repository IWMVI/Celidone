/**
 * Controlador do módulo de aluguel
 * Implementa o padrão MVC para gerenciamento de aluguéis
 * Segue os princípios SOLID e Clean Architecture
 */

import FormComponent from "../../shared/components/FormComponent.js";
import NotificationService from "../../core/services/NotificationService.js";
import ApiService from "../../core/services/ApiService.js";
import ValidationService from "../../core/services/ValidationService.js";
import MaskUtils from "../../core/utils/MaskUtils.js";
import FormatUtils from "../../core/utils/FormatUtils.js";
import AsyncUtils from "../../core/utils/AsyncUtils.js";
import { AluguelConfig } from "./config/AluguelConfig.js";

/**
 * Controlador principal para gerenciamento de aluguéis
 * Implementa padrões SOLID e Clean Architecture
 */
class AluguelController {
    constructor() {
        this.formComponent = null;
        this.notificationService = new NotificationService();
        this.apiService = new ApiService();
        this.validationService = new ValidationService();

        this.currentAluguel = null;
        this.alugueis = [];
        this.clientes = [];
        this.produtos = [];

        this.isInitialized = false;
    }

    /**
     * Inicializa o controlador
     */
    async init() {
        if (this.isInitialized) return;

        try {
            this.setupFormComponent();
            this.setupEventListeners();
            this.setupValidators();
            this.setupMasks();
            await this.loadInitialData();

            this.isInitialized = true;
            console.log("Aluguel Controller inicializado com sucesso");
        } catch (error) {
            console.error("Erro ao inicializar Aluguel Controller:", error);
            this.notificationService.showError("Erro ao inicializar a página");
        }
    }

    /**
     * Configura componente de formulário
     */
    setupFormComponent() {
        this.formComponent = new FormComponent("form-aluguel", {
            validateOnChange: true,
            validateOnSubmit: true,
            autoFocus: true,
        });

        this.formComponent.init();
    }

    /**
     * Configura listeners de eventos
     */
    setupEventListeners() {
        // Submit do formulário
        this.formComponent.on("submit", (event) => {
            this.handleFormSubmit(event.detail);
        });

        // Botões de ação
        this.setupButton(
            "cadastrar-aluguel-btn",
            this.handleCadastrar.bind(this)
        );
        this.setupButton("listar-alugueis-btn", this.handleListar.bind(this));
        this.setupButton(
            "devolver-aluguel-btn",
            this.handleDevolver.bind(this)
        );
        this.setupButton(
            "cancelar-aluguel-btn",
            this.handleCancelar.bind(this)
        );
        this.setupButton("limpar-form-btn", this.handleLimpar.bind(this));

        // Eventos específicos
        this.setupClienteListener();
        this.setupProdutoListener();
        this.setupTipoCobrancaListener();
        this.setupCalculoListener();
    }

    /**
     * Configura um botão com event listener
     */
    setupButton(id, handler) {
        const button = document.getElementById(id);
        if (button) {
            button.addEventListener("click", handler);
        }
    }

    /**
     * Configura listener para seleção de cliente
     */
    setupClienteListener() {
        const clienteSelect = document.getElementById("cliente-id");
        if (clienteSelect) {
            clienteSelect.addEventListener("change", (e) => {
                this.handleClienteChange(e.target.value);
            });
        }
    }

    /**
     * Configura listener para seleção de produto
     */
    setupProdutoListener() {
        const produtoSelect = document.getElementById("produto-id");
        if (produtoSelect) {
            produtoSelect.addEventListener("change", (e) => {
                this.handleProdutoChange(e.target.value);
            });
        }
    }

    /**
     * Configura listener para tipo de cobrança
     */
    setupTipoCobrancaListener() {
        const tipoCobrancaSelect = document.getElementById("tipo-cobranca");
        if (tipoCobrancaSelect) {
            tipoCobrancaSelect.addEventListener("change", (e) => {
                this.handleTipoCobrancaChange(e.target.value);
            });
        }
    }

    /**
     * Configura listeners para cálculo automático
     */
    setupCalculoListener() {
        const campos = [
            "valor-aluguel",
            "data-aluguel",
            "data-dev-prevista",
            "periodo",
        ];

        campos.forEach((campoId) => {
            const campo = document.getElementById(campoId);
            if (campo) {
                campo.addEventListener(
                    "input",
                    AsyncUtils.debounce(this.calcularValores.bind(this), 300)
                );
            }
        });
    }

    /**
     * Configura validadores
     */
    setupValidators() {
        const validators = {
            "cliente-id": {
                required: true,
                message: AluguelConfig.validation.required.clienteId,
            },
            "produto-id": {
                required: true,
                message: AluguelConfig.validation.required.produtoId,
            },
            "data-aluguel": {
                required: true,
                message: AluguelConfig.validation.required.dataAluguel,
            },
            "data-dev-prevista": {
                required: true,
                message: AluguelConfig.validation.required.dataDevPrevista,
            },
            "valor-aluguel": {
                required: true,
                min: AluguelConfig.validation.format.valorAluguel.min,
                max: AluguelConfig.validation.format.valorAluguel.max,
                message: AluguelConfig.validation.format.valorAluguel.message,
            },
            "valor-caucao": {
                required: true,
                min: AluguelConfig.validation.format.valorCaucao.min,
                max: AluguelConfig.validation.format.valorCaucao.max,
                message: AluguelConfig.validation.format.valorCaucao.message,
            },
        };

        Object.entries(validators).forEach(([fieldId, rules]) => {
            this.formComponent.addValidator(fieldId, rules);
        });
    }

    /**
     * Configura máscaras nos campos
     */
    setupMasks() {
        const maskFields = [
            { id: "valor-aluguel", handler: MaskUtils.applyCurrencyMask },
            { id: "valor-caucao", handler: MaskUtils.applyCurrencyMask },
            { id: "valor-desconto", handler: MaskUtils.applyCurrencyMask },
            { id: "valor-total", handler: MaskUtils.applyCurrencyMask },
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
     * Carrega dados iniciais
     */
    async loadInitialData() {
        try {
            await Promise.all([
                this.loadClientes(),
                this.loadProdutos(),
                this.loadAlugueis(),
            ]);
        } catch (error) {
            console.error("Erro ao carregar dados iniciais:", error);
            this.notificationService.showError("Erro ao carregar dados");
        }
    }

    /**
     * Carrega lista de clientes
     */
    async loadClientes() {
        try {
            const result = await window.api.listarClientes();
            if (result.success) {
                this.clientes = result.data;
                this.populateClienteSelect();
            }
        } catch (error) {
            console.error("Erro ao carregar clientes:", error);
        }
    }

    /**
     * Carrega lista de produtos
     */
    async loadProdutos() {
        try {
            const result = await window.api.listarProdutos();
            if (result.success) {
                this.produtos = result.data.filter(
                    (p) => p.status === "disponivel"
                );
                this.populateProdutoSelect();
            }
        } catch (error) {
            console.error("Erro ao carregar produtos:", error);
        }
    }

    /**
     * Carrega lista de aluguéis
     */
    async loadAlugueis() {
        try {
            const result = await window.api.listarAlugueis();
            if (result.success) {
                this.alugueis = result.data;
                this.preencherTabelaAlugueis();
            }
        } catch (error) {
            console.error("Erro ao carregar aluguéis:", error);
        }
    }

    /**
     * Popula select de clientes
     */
    populateClienteSelect() {
        const select = document.getElementById("cliente-id");
        if (!select) return;

        select.innerHTML = '<option value="">Selecione um cliente</option>';

        this.clientes.forEach((cliente) => {
            const option = document.createElement("option");
            option.value = cliente.id;
            option.textContent = `${cliente.nome} - ${cliente.email}`;
            select.appendChild(option);
        });
    }

    /**
     * Popula select de produtos
     */
    populateProdutoSelect() {
        const select = document.getElementById("produto-id");
        if (!select) return;

        select.innerHTML = '<option value="">Selecione um produto</option>';

        this.produtos.forEach((produto) => {
            const option = document.createElement("option");
            option.value = produto.id;
            option.textContent = `${produto.codigo} - ${produto.tecido} ${produto.cor} (${produto.tamanho})`;
            select.appendChild(option);
        });
    }

    /**
     * Manipula mudança de cliente
     */
    handleClienteChange(clienteId) {
        if (!clienteId) return;

        const cliente = this.clientes.find((c) => c.id == clienteId);
        if (cliente) {
            // Atualiza informações do cliente na tela
            this.atualizarInfoCliente(cliente);

            // Aplica desconto se cliente VIP
            this.aplicarDescontoCliente(cliente);
        }
    }

    /**
     * Manipula mudança de produto
     */
    handleProdutoChange(produtoId) {
        if (!produtoId) return;

        const produto = this.produtos.find((p) => p.id == produtoId);
        if (produto) {
            // Atualiza informações do produto
            this.atualizarInfoProduto(produto);

            // Define valor base do aluguel
            this.definirValorBase(produto);

            // Calcula caução sugerida
            this.calcularCaucaoSugerida(produto);
        }
    }

    /**
     * Manipula mudança do tipo de cobrança
     */
    handleTipoCobrancaChange(tipoCobranca) {
        const periodoField = document.getElementById("periodo");
        const periodoLabel = document.querySelector("label[for='periodo']");

        if (!periodoField || !periodoLabel) return;

        const sugestoes = AluguelConfig.sugestoes.periodosPorTipo[tipoCobranca];

        if (sugestoes) {
            // Atualiza label baseado no tipo
            const labels = {
                diaria: "Período (dias)",
                semanal: "Período (semanas)",
                mensal: "Período (meses)",
                evento: "Período (dias)",
            };

            periodoLabel.textContent = labels[tipoCobranca] || "Período";

            // Atualiza placeholder com sugestões
            periodoField.placeholder = `Ex: ${sugestoes.join(", ")}`;
        }

        // Recalcula valores
        this.calcularValores();
    }

    /**
     * Atualiza informações do cliente
     */
    atualizarInfoCliente(cliente) {
        const infoDiv = document.getElementById("cliente-info");
        if (infoDiv) {
            infoDiv.innerHTML = `
                <div class="flex flex-col gap-0.5">
                    <span class="text-sm font-medium">${cliente.nome}</span>
                    <span class="text-xs text-secondary">${cliente.email}</span>
                    <span class="text-xs text-secondary">${
                        cliente.celular || "Sem telefone"
                    }</span>
                </div>
            `;
        }
    }

    /**
     * Atualiza informações do produto
     */
    atualizarInfoProduto(produto) {
        const infoDiv = document.getElementById("produto-info");
        if (infoDiv) {
            infoDiv.innerHTML = `
                <div class="flex flex-col gap-0.5">
                    <span class="text-sm font-medium">${produto.codigo}</span>
                    <span class="text-xs text-secondary">${produto.tecido} ${
                produto.cor
            }</span>
                    <span class="text-xs text-secondary">Tamanho: ${
                        produto.tamanho
                    }</span>
                    <span class="text-xs text-secondary">Preço: ${FormatUtils.formatCurrency(
                        produto.preco
                    )}</span>
                </div>
            `;
        }
    }

    /**
     * Define valor base do aluguel baseado no produto
     */
    definirValorBase(produto) {
        const valorField = document.getElementById("valor-aluguel");
        if (valorField && !valorField.value) {
            // Valor base é 30% do preço do produto
            const valorBase = produto.preco * 0.3;
            valorField.value = FormatUtils.formatCurrency(valorBase);
        }
    }

    /**
     * Calcula caução sugerida
     */
    calcularCaucaoSugerida(produto) {
        const caucaoField = document.getElementById("valor-caucao");
        if (caucaoField && !caucaoField.value) {
            // Caução padrão é 100% do preço do produto
            const caucaoSugerida = produto.preco;
            caucaoField.value = FormatUtils.formatCurrency(caucaoSugerida);
        }
    }

    /**
     * Aplica desconto baseado no tipo de cliente
     */
    aplicarDescontoCliente(cliente) {
        // Lógica para determinar tipo de cliente e aplicar desconto
        // TODO: Implementar baseado em regras de negócio
    }

    /**
     * Calcula valores automáticos
     */
    calcularValores() {
        const valorAluguel = this.getFieldNumericValue("valor-aluguel");
        const periodo = this.getFieldNumericValue("periodo");
        const desconto = this.getFieldNumericValue("valor-desconto");

        if (valorAluguel && periodo) {
            const valorTotal = valorAluguel * periodo - desconto;
            this.setFieldValue(
                "valor-total",
                FormatUtils.formatCurrency(valorTotal)
            );
        }
    }

    /**
     * Obtém valor numérico de um campo
     */
    getFieldNumericValue(fieldId) {
        const field = document.getElementById(fieldId);
        if (!field || !field.value) return 0;

        const value = field.value.replace(/[^\d,]/g, "").replace(",", ".");
        return parseFloat(value) || 0;
    }

    /**
     * Define valor em um campo
     */
    setFieldValue(fieldId, value) {
        const field = document.getElementById(fieldId);
        if (field) {
            field.value = value;
        }
    }

    /**
     * Manipula envio do formulário
     */
    async handleFormSubmit(formData) {
        try {
            const aluguelData = this.formatAluguelData(formData);

            const result = await window.api.cadastrarAluguel(aluguelData);

            if (result.success) {
                this.notificationService.showSuccess(
                    "Aluguel cadastrado com sucesso!"
                );
                this.formComponent.reset();
                await this.loadAlugueis();
                await this.loadProdutos(); // Recarrega para atualizar status
            } else {
                this.notificationService.showError(
                    result.error || "Erro ao cadastrar aluguel"
                );
            }
        } catch (error) {
            console.error("Erro ao cadastrar aluguel:", error);
            this.notificationService.showError(
                "Erro inesperado ao cadastrar aluguel"
            );
        }
    }

    /**
     * Formata dados do aluguel
     */
    formatAluguelData(formData) {
        return {
            clienteId: parseInt(formData["cliente-id"]),
            produtoId: parseInt(formData["produto-id"]),
            dataAluguel: formData["data-aluguel"],
            dataDevPrevista: formData["data-dev-prevista"],
            valorAluguel: this.getFieldNumericValue("valor-aluguel"),
            valorCaucao: this.getFieldNumericValue("valor-caucao"),
            valorDesconto: this.getFieldNumericValue("valor-desconto"),
            valorTotal: this.getFieldNumericValue("valor-total"),
            tipoCobranca: formData["tipo-cobranca"],
            formaPagamento: formData["forma-pagamento"],
            periodo: this.getFieldNumericValue("periodo"),
            observacoes: formData["observacoes"]?.trim() || null,
            status: AluguelConfig.enums.STATUS.ATIVO,
        };
    }

    /**
     * Manipula cadastro de aluguel
     */
    async handleCadastrar() {
        this.formComponent.submit();
    }

    /**
     * Manipula listagem de aluguéis
     */
    async handleListar() {
        await this.loadAlugueis();
        this.notificationService.showSuccess("Lista atualizada!");
    }

    /**
     * Manipula devolução de aluguel
     */
    async handleDevolver() {
        if (!this.currentAluguel) {
            this.notificationService.showError(
                "Selecione um aluguel para devolver"
            );
            return;
        }

        try {
            const result = await window.api.devolverAluguel(
                this.currentAluguel.id
            );

            if (result.success) {
                this.notificationService.showSuccess(
                    "Aluguel devolvido com sucesso!"
                );
                await this.loadAlugueis();
                await this.loadProdutos();
            } else {
                this.notificationService.showError(
                    result.error || "Erro ao devolver aluguel"
                );
            }
        } catch (error) {
            console.error("Erro ao devolver aluguel:", error);
            this.notificationService.showError(
                "Erro inesperado ao devolver aluguel"
            );
        }
    }

    /**
     * Manipula cancelamento de aluguel
     */
    async handleCancelar() {
        if (!this.currentAluguel) {
            this.notificationService.showError(
                "Selecione um aluguel para cancelar"
            );
            return;
        }

        if (!confirm("Tem certeza que deseja cancelar este aluguel?")) {
            return;
        }

        try {
            const result = await window.api.cancelarAluguel(
                this.currentAluguel.id
            );

            if (result.success) {
                this.notificationService.showSuccess(
                    "Aluguel cancelado com sucesso!"
                );
                await this.loadAlugueis();
                await this.loadProdutos();
            } else {
                this.notificationService.showError(
                    result.error || "Erro ao cancelar aluguel"
                );
            }
        } catch (error) {
            console.error("Erro ao cancelar aluguel:", error);
            this.notificationService.showError(
                "Erro inesperado ao cancelar aluguel"
            );
        }
    }

    /**
     * Manipula limpeza do formulário
     */
    handleLimpar() {
        this.formComponent.reset();
        this.currentAluguel = null;

        // Limpa informações adicionais
        const infoSections = ["cliente-info", "produto-info"];
        infoSections.forEach((id) => {
            const element = document.getElementById(id);
            if (element) {
                element.innerHTML = "";
            }
        });

        this.notificationService.showSuccess("Formulário limpo!");
    }

    /**
     * Preenche tabela de aluguéis
     */
    preencherTabelaAlugueis() {
        const tbody = document.getElementById("aluguel-lista");
        if (!tbody) return;

        tbody.innerHTML = "";

        if (this.alugueis.length === 0) {
            const row = tbody.insertRow();
            const cell = row.insertCell();
            cell.colSpan = 10;
            cell.textContent = "Nenhum aluguel encontrado";
            cell.style.textAlign = "center";
            cell.style.padding = "20px";
            cell.style.color = "#999";
            return;
        }

        this.alugueis.forEach((aluguel) => {
            const row = tbody.insertRow();
            row.style.cursor = "pointer";
            row.addEventListener("click", () =>
                this.selecionarAluguel(aluguel)
            );

            const cells = [
                aluguel.id || "",
                aluguel.clienteNome || "",
                aluguel.produtoCodigo || "",
                FormatUtils.formatDate(aluguel.dataAluguel) || "",
                FormatUtils.formatDate(aluguel.dataDevPrevista) || "",
                this.getStatusBadge(aluguel.status),
                FormatUtils.formatCurrency(aluguel.valorTotal) || "",
                AluguelConfig.labels.FORMA_PAGAMENTO[aluguel.formaPagamento] ||
                    "",
                this.getAcoesBotoes(aluguel),
            ];

            cells.forEach((cellValue, index) => {
                const cell = row.insertCell();
                if (index === 5 || index === 8) {
                    // Status e ações
                    cell.innerHTML = cellValue;
                } else {
                    cell.textContent = cellValue;
                }
                cell.style.padding = "8px";
                cell.style.borderBottom = "1px solid #eee";
            });
        });
    }

    /**
     * Gera badge de status
     */
    getStatusBadge(status) {
        const config = AluguelConfig.ui;
        const cor = config.cores[status] || "#6B7280";
        const icone = config.icones[status] || "";
        const label = AluguelConfig.labels.STATUS[status] || status;

        return `
            <span class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium" 
                  style="background-color: ${cor}20; color: ${cor};">
                ${icone} ${label}
            </span>
        `;
    }

    /**
     * Gera botões de ações
     */
    getAcoesBotoes(aluguel) {
        const botoes = [];

        if (aluguel.status === AluguelConfig.enums.STATUS.ATIVO) {
            botoes.push(`
                <button class="btn btn-sm btn-success" 
                        onclick="aluguelController.devolverAluguel(${aluguel.id})"
                        title="Devolver">
                    ✅
                </button>
            `);
            botoes.push(`
                <button class="btn btn-sm btn-warning" 
                        onclick="aluguelController.cancelarAluguel(${aluguel.id})"
                        title="Cancelar">
                    ❌
                </button>
            `);
        }

        return `<div class="flex gap-1">${botoes.join("")}</div>`;
    }

    /**
     * Seleciona um aluguel
     */
    selecionarAluguel(aluguel) {
        this.currentAluguel = aluguel;
        this.preencherFormularioComAluguel(aluguel);
        this.notificationService.showSuccess("Aluguel selecionado!");
    }

    /**
     * Preenche formulário com dados do aluguel
     */
    preencherFormularioComAluguel(aluguel) {
        const campos = {
            "cliente-id": aluguel.clienteId,
            "produto-id": aluguel.produtoId,
            "data-aluguel": aluguel.dataAluguel,
            "data-dev-prevista": aluguel.dataDevPrevista,
            "valor-aluguel": FormatUtils.formatCurrency(aluguel.valorAluguel),
            "valor-caucao": FormatUtils.formatCurrency(aluguel.valorCaucao),
            "valor-desconto": FormatUtils.formatCurrency(aluguel.valorDesconto),
            "valor-total": FormatUtils.formatCurrency(aluguel.valorTotal),
            "tipo-cobranca": aluguel.tipoCobranca,
            "forma-pagamento": aluguel.formaPagamento,
            periodo: aluguel.periodo,
            observacoes: aluguel.observacoes,
        };

        Object.entries(campos).forEach(([fieldId, value]) => {
            this.setFieldValue(fieldId, value || "");
        });
    }

    /**
     * Métodos públicos para uso em eventos onclick
     */
    devolverAluguel(aluguelId) {
        const aluguel = this.alugueis.find((a) => a.id === aluguelId);
        if (aluguel) {
            this.currentAluguel = aluguel;
            this.handleDevolver();
        }
    }

    cancelarAluguel(aluguelId) {
        const aluguel = this.alugueis.find((a) => a.id === aluguelId);
        if (aluguel) {
            this.currentAluguel = aluguel;
            this.handleCancelar();
        }
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
    const aluguelController = new AluguelController();
    aluguelController.init();

    // Torna disponível globalmente para debug
    window.aluguelController = aluguelController;
});

export default AluguelController;
