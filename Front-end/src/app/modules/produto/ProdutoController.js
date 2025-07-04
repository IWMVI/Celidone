/**
 * Controlador do módulo de produto
 * Implementa o padrão MVC para gerenciamento de produtos
 */

import { FormComponent } from "../../shared/components/FormComponent.js";
import { NotificationService } from "../../core/services/NotificationService.js";
import { ApiService } from "../../core/services/ApiService.js";
import { ValidationService } from "../../core/services/ValidationService.js";
import { FormatUtils } from "../../core/utils/FormatUtils.js";
import { MaskUtils } from "../../core/utils/MaskUtils.js";
import { AsyncUtils } from "../../core/utils/AsyncUtils.js";
import { PRODUTO_CONFIG } from "./config/ProdutoConfig.js";

/**
 * Controlador principal para gerenciamento de produtos
 * Segue os princípios SOLID e Clean Architecture
 */
export class ProdutoController {
    constructor() {
        this.formComponent = new FormComponent("#form-produto");
        this.notificationService = new NotificationService();
        this.apiService = new ApiService();
        this.validationService = new ValidationService();

        this.currentProduto = null;
        this.produtos = [];

        this.init();
    }

    /**
     * Inicializa o controlador
     * Configura eventos e carrega dados iniciais
     */
    async init() {
        try {
            this.setupEventListeners();
            this.setupFormValidation();
            this.setupMasks();
            await this.loadProdutos();
            this.notificationService.showSuccess(
                "Módulo de produtos carregado com sucesso!"
            );
        } catch (error) {
            console.error("Erro ao inicializar módulo de produto:", error);
            this.notificationService.showError(
                "Erro ao carregar módulo de produtos"
            );
        }
    }

    /**
     * Configura os event listeners
     */
    setupEventListeners() {
        // Formulário principal
        this.formComponent.onSubmit(this.handleSubmit.bind(this));

        // Botões de ação
        this.setupButton(
            "#consultar-produto-btn",
            this.handleConsultar.bind(this)
        );
        this.setupButton("#excluir-produto-btn", this.handleExcluir.bind(this));
        this.setupButton("#listar-produtos-btn", this.handleListar.bind(this));
        this.setupButton("#limpar-form-btn", this.handleLimpar.bind(this));

        // Eventos de campo
        this.setupFieldEvents();
    }

    /**
     * Configura eventos específicos de campos
     */
    setupFieldEvents() {
        // Código do produto - consulta automática
        const codigoField = document.getElementById("codigo");
        if (codigoField) {
            codigoField.addEventListener("blur", async (e) => {
                const codigo = e.target.value.trim();
                if (codigo && codigo !== this.currentProduto?.codigo) {
                    await this.consultarProduto(codigo);
                }
            });
        }

        // Preço - formatação automática
        const precoField = document.getElementById("preco");
        if (precoField) {
            precoField.addEventListener("input", (e) => {
                e.target.value = FormatUtils.formatCurrency(e.target.value);
            });
        }

        // Tipo de traje - atualização de campos relacionados
        const tipoTrajeField = document.getElementById("tipotraje");
        if (tipoTrajeField) {
            tipoTrajeField.addEventListener(
                "change",
                this.handleTipoTrajeChange.bind(this)
            );
        }
    }

    /**
     * Configura máscaras de entrada
     */
    setupMasks() {
        // Máscara para preço
        MaskUtils.applyMask("#preco", {
            pattern: "999.999,99",
            placeholder: "0,00",
        });

        // Máscara para código
        MaskUtils.applyMask("#codigo", {
            pattern: "AAA-9999",
            placeholder: "PRD-0000",
        });
    }

    /**
     * Configura validações do formulário
     */
    setupFormValidation() {
        const validationRules = {
            codigo: {
                required: true,
                pattern: PRODUTO_CONFIG.VALIDATION.CODIGO_PATTERN,
                message: "Código deve seguir o padrão PRD-0000",
            },
            tecido: {
                required: true,
                minLength: 2,
                message: "Tecido deve ter pelo menos 2 caracteres",
            },
            cor: {
                required: true,
                minLength: 2,
                message: "Cor deve ter pelo menos 2 caracteres",
            },
            estampa: {
                required: true,
                minLength: 2,
                message: "Estampa deve ter pelo menos 2 caracteres",
            },
            tipotraje: {
                required: true,
                message: "Tipo de traje é obrigatório",
            },
            textura: {
                required: true,
                minLength: 2,
                message: "Textura deve ter pelo menos 2 caracteres",
            },
            preco: {
                required: true,
                min: 0.01,
                message: "Preço deve ser maior que zero",
            },
            tamanho: {
                required: true,
                pattern: PRODUTO_CONFIG.VALIDATION.TAMANHO_PATTERN,
                message: "Tamanho inválido",
            },
            status: {
                required: true,
                message: "Status é obrigatório",
            },
            sexo: {
                required: true,
                message: "Sexo é obrigatório",
            },
        };

        this.formComponent.setValidationRules(validationRules);
    }

    /**
     * Configura um botão com event listener
     */
    setupButton(selector, handler) {
        const button = document.querySelector(selector);
        if (button) {
            button.addEventListener("click", handler);
        }
    }

    /**
     * Manipula o submit do formulário
     */
    async handleSubmit(formData) {
        try {
            this.notificationService.showLoading("Salvando produto...");

            const produtoData = this.formatProdutoData(formData);

            // Valida dados antes de enviar
            const validationResult =
                this.validationService.validateProduto(produtoData);
            if (!validationResult.isValid) {
                this.notificationService.showError(
                    validationResult.errors.join(", ")
                );
                return;
            }

            let resultado;
            if (this.currentProduto && this.currentProduto.id) {
                // Atualização
                resultado = await this.apiService.atualizarProduto(
                    this.currentProduto.id,
                    produtoData
                );
            } else {
                // Criação
                resultado = await this.apiService.cadastrarProduto(produtoData);
            }

            if (resultado.success) {
                this.notificationService.showSuccess(
                    this.currentProduto
                        ? "Produto atualizado com sucesso!"
                        : "Produto cadastrado com sucesso!"
                );
                this.currentProduto = resultado.data;
                await this.loadProdutos();
            } else {
                this.notificationService.showError(
                    resultado.error || "Erro ao salvar produto"
                );
            }
        } catch (error) {
            console.error("Erro ao salvar produto:", error);
            this.notificationService.showError("Erro ao salvar produto");
        } finally {
            this.notificationService.hideLoading();
        }
    }

    /**
     * Formata os dados do produto para envio
     */
    formatProdutoData(formData) {
        return {
            codigo: formData.codigo?.trim(),
            tecido: formData.tecido?.trim(),
            cor: formData.cor?.trim(),
            estampa: formData.estampa?.trim(),
            tipoTraje: formData.tipotraje,
            textura: formData.textura?.trim(),
            preco: FormatUtils.parseCurrency(formData.preco),
            tamanho: formData.tamanho?.trim().toUpperCase(),
            status: formData.status,
            sexo: formData.sexo,
            observacoes: formData.observacoes?.trim() || null,
        };
    }

    /**
     * Manipula consulta de produto
     */
    async handleConsultar() {
        const codigo = document.getElementById("codigo").value.trim();

        if (!codigo) {
            this.notificationService.showWarning("Informe o código do produto");
            return;
        }

        await this.consultarProduto(codigo);
    }

    /**
     * Consulta produto por código
     */
    async consultarProduto(codigo) {
        try {
            this.notificationService.showLoading("Consultando produto...");

            const resultado = await this.apiService.consultarProduto(codigo);

            if (resultado.success && resultado.data) {
                this.currentProduto = resultado.data;
                this.preencherFormulario(resultado.data);
                this.notificationService.showSuccess("Produto encontrado!");
                await this.loadHistoricoAluguel(resultado.data.id);
            } else {
                this.notificationService.showWarning("Produto não encontrado");
                this.currentProduto = null;
            }
        } catch (error) {
            console.error("Erro ao consultar produto:", error);
            this.notificationService.showError("Erro ao consultar produto");
        } finally {
            this.notificationService.hideLoading();
        }
    }

    /**
     * Preenche o formulário com dados do produto
     */
    preencherFormulario(produto) {
        this.formComponent.setValues({
            codigo: produto.codigo,
            tecido: produto.tecido,
            cor: produto.cor,
            estampa: produto.estampa,
            tipotraje: produto.tipoTraje,
            textura: produto.textura,
            preco: FormatUtils.formatCurrency(produto.preco),
            tamanho: produto.tamanho,
            status: produto.status,
            sexo: produto.sexo,
            observacoes: produto.observacoes,
        });
    }

    /**
     * Manipula exclusão de produto
     */
    async handleExcluir() {
        if (!this.currentProduto || !this.currentProduto.id) {
            this.notificationService.showWarning(
                "Nenhum produto selecionado para exclusão"
            );
            return;
        }

        const confirmacao = await this.notificationService.showConfirm(
            "Tem certeza que deseja excluir este produto?",
            "Esta ação não pode ser desfeita."
        );

        if (!confirmacao) return;

        try {
            this.notificationService.showLoading("Excluindo produto...");

            const resultado = await this.apiService.excluirProduto(
                this.currentProduto.id
            );

            if (resultado.success) {
                this.notificationService.showSuccess(
                    "Produto excluído com sucesso!"
                );
                this.handleLimpar();
                await this.loadProdutos();
            } else {
                this.notificationService.showError(
                    resultado.error || "Erro ao excluir produto"
                );
            }
        } catch (error) {
            console.error("Erro ao excluir produto:", error);
            this.notificationService.showError("Erro ao excluir produto");
        } finally {
            this.notificationService.hideLoading();
        }
    }

    /**
     * Carrega lista de produtos
     */
    async loadProdutos() {
        try {
            const resultado = await this.apiService.listarProdutos();

            if (resultado.success) {
                this.produtos = resultado.data || [];
                this.renderizarTabelaProdutos();
            } else {
                console.error("Erro ao carregar produtos:", resultado.error);
            }
        } catch (error) {
            console.error("Erro ao carregar produtos:", error);
        }
    }

    /**
     * Manipula listagem de produtos
     */
    async handleListar() {
        await this.loadProdutos();
        this.notificationService.showSuccess("Lista de produtos atualizada!");
    }

    /**
     * Renderiza tabela de produtos
     */
    renderizarTabelaProdutos() {
        const tbody = document.getElementById("produto-lista");
        if (!tbody) return;

        if (this.produtos.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="8" class="text-center py-1 text-muted">
                        Nenhum produto encontrado
                    </td>
                </tr>
            `;
            return;
        }

        tbody.innerHTML = this.produtos
            .map(
                (produto) => `
            <tr data-produto-id="${
                produto.id
            }" class="cursor-pointer hover:bg-gray-50">
                <td class="px-1 py-0.5">${produto.codigo || "-"}</td>
                <td class="px-1 py-0.5">${produto.tecido || "-"}</td>
                <td class="px-1 py-0.5">${produto.cor || "-"}</td>
                <td class="px-1 py-0.5">${produto.tipoTraje || "-"}</td>
                <td class="px-1 py-0.5">${produto.tamanho || "-"}</td>
                <td class="px-1 py-0.5">
                    <span class="badge ${
                        produto.status === "disponivel"
                            ? "badge-success"
                            : "badge-danger"
                    }">
                        ${
                            produto.status === "disponivel"
                                ? "Disponível"
                                : "Indisponível"
                        }
                    </span>
                </td>
                <td class="px-1 py-0.5">${FormatUtils.formatCurrency(
                    produto.preco
                )}</td>
                <td class="px-1 py-0.5">
                    <button 
                        class="btn btn-sm btn-primary" 
                        onclick="produtoController.selecionarProduto(${
                            produto.id
                        })"
                        title="Selecionar produto"
                    >
                        Selecionar
                    </button>
                </td>
            </tr>
        `
            )
            .join("");
    }

    /**
     * Seleciona um produto da tabela
     */
    async selecionarProduto(produtoId) {
        const produto = this.produtos.find((p) => p.id === produtoId);
        if (produto) {
            this.currentProduto = produto;
            this.preencherFormulario(produto);
            await this.loadHistoricoAluguel(produto.id);
            this.notificationService.showSuccess("Produto selecionado!");
        }
    }

    /**
     * Carrega histórico de aluguel do produto
     */
    async loadHistoricoAluguel(produtoId) {
        try {
            const resultado = await this.apiService.consultarHistoricoAluguel(
                produtoId
            );

            if (resultado.success) {
                this.renderizarHistoricoAluguel(resultado.data || []);
            }
        } catch (error) {
            console.error("Erro ao carregar histórico:", error);
        }
    }

    /**
     * Renderiza histórico de aluguel
     */
    renderizarHistoricoAluguel(historico) {
        const tbody = document.getElementById("produto-historico");
        if (!tbody) return;

        if (historico.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="3" class="text-center py-1 text-muted">
                        Nenhum aluguel encontrado
                    </td>
                </tr>
            `;
            return;
        }

        tbody.innerHTML = historico
            .map(
                (item) => `
            <tr>
                <td class="px-1 py-0.5">${item.id || "-"}</td>
                <td class="px-1 py-0.5">${item.clienteNome || "-"}</td>
                <td class="px-1 py-0.5">${
                    FormatUtils.formatDate(item.dataAluguel) || "-"
                }</td>
            </tr>
        `
            )
            .join("");
    }

    /**
     * Manipula mudança no tipo de traje
     */
    handleTipoTrajeChange(event) {
        const tipoTraje = event.target.value;

        // Atualiza sugestões baseadas no tipo de traje
        const sugestoes = PRODUTO_CONFIG.TIPO_TRAJE_SUGESTOES[tipoTraje];
        if (sugestoes) {
            this.atualizarSugestoes(sugestoes);
        }
    }

    /**
     * Atualiza sugestões de campos baseadas no tipo de traje
     */
    atualizarSugestoes(sugestoes) {
        // Atualiza placeholder dos campos com sugestões
        Object.entries(sugestoes).forEach(([campo, valores]) => {
            const field = document.getElementById(campo);
            if (field && valores.length > 0) {
                field.placeholder = `Ex: ${valores.join(", ")}`;
            }
        });
    }

    /**
     * Limpa o formulário
     */
    handleLimpar() {
        this.formComponent.reset();
        this.currentProduto = null;
        this.renderizarHistoricoAluguel([]);
        this.notificationService.showSuccess("Formulário limpo!");
    }
}

// Inicializa o controlador quando o DOM estiver carregado
document.addEventListener("DOMContentLoaded", () => {
    window.produtoController = new ProdutoController();
});
