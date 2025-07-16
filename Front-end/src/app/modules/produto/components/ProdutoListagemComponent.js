/**
 * Componente para listagem de produtos
 * Implementa funcionalidades específicas para a visualização e filtragem de produtos
 */
import NotificationService from "../../../core/services/NotificationService.js";
import ApiService from "../../../core/services/ApiService.js";
import { FormatUtils } from "../../../core/utils/FormatUtils.js";

class ProdutoListagemComponent {
    constructor() {
        this.notificationService = new NotificationService();
        this.apiService = new ApiService();
        this.produtos = [];
        this.produtosFiltrados = [];
        this.ordenacao = { campo: "codigo", direcao: "asc" };
        this.produtoSelecionado = null;
        this.isInitialized = false;
        this.paginacao = {
            paginaAtual: 1,
            itensPorPagina: 12,
            totalPaginas: 1
        };
    }

    /**
     * Inicializa o componente
     */
    async init() {
        if (this.isInitialized) return;

        try {
            this.setupEventListeners();
            await this.carregarProdutos();
            this.isInitialized = true;
            console.log("Componente de listagem de produtos inicializado com sucesso");
        } catch (error) {
            this.notificationService.showError(
                "Erro ao inicializar o componente de listagem",
                error
            );
            console.error("Erro ao inicializar componente de listagem:", error);
        }
    }

    /**
     * Configura listeners de eventos
     */
    setupEventListeners() {
        // Listener para filtro de pesquisa
        const searchInput = document.getElementById("search-input");
        if (searchInput) {
            searchInput.addEventListener("keyup", () => this.filtrarProdutos());
        }

        // Listener para filtro de categoria
        const categoriaFilter = document.getElementById("categoria-filter");
        if (categoriaFilter) {
            categoriaFilter.addEventListener("change", () => this.filtrarProdutos());
        }

        // Listener para filtro de status
        const statusFilter = document.getElementById("status-filter");
        if (statusFilter) {
            statusFilter.addEventListener("change", () => this.filtrarProdutos());
        }

        // Listener para botão de limpar filtros
        const limparBtn = document.querySelector("button[onclick='limparFiltros()']");
        if (limparBtn) {
            limparBtn.onclick = (e) => {
                e.preventDefault();
                this.limparFiltros();
            };
        }

        // Listener para botão de atualizar
        const atualizarBtn = document.querySelector("button[onclick='carregarProdutos()']");
        if (atualizarBtn) {
            atualizarBtn.onclick = (e) => {
                e.preventDefault();
                this.carregarProdutos();
            };
        }

        // Listener para botão de exportar
        const exportarBtn = document.querySelector("button[onclick='exportarProdutos()']");
        if (exportarBtn) {
            exportarBtn.onclick = (e) => {
                e.preventDefault();
                this.exportarProdutos();
            };
        }

        // Listener para botão de novo produto
        const novoCadastroBtn = document.querySelector("button[onclick='abrirCadastro()']");
        if (novoCadastroBtn) {
            novoCadastroBtn.onclick = (e) => {
                e.preventDefault();
                this.abrirCadastro();
            };
        }

        // Sobrescrever funções globais usadas nos elementos HTML
        window.abrirDetalhesProduto = (codigo) => this.abrirDetalhesProduto(codigo);
        window.editarProduto = (codigo) => this.editarProduto(codigo);
        window.confirmarExclusao = (codigo) => this.confirmarExclusao(codigo);
        window.excluirProduto = () => this.excluirProduto();
        window.fecharModal = () => this.fecharModal();
        window.filtrarProdutos = () => this.filtrarProdutos();
        window.limparFiltros = () => this.limparFiltros();
        window.carregarProdutos = () => this.carregarProdutos();
        window.exportarProdutos = () => this.exportarProdutos();
        window.abrirCadastro = () => this.abrirCadastro();
        window.paginaAnterior = () => this.paginaAnterior();
        window.proximaPagina = () => this.proximaPagina();
    }

    /**
     * Carrega produtos da API
     */
    async carregarProdutos() {
        try {
            this.mostrarLoading(true);

            // Verificar se window.api está disponível
            if (!window.api) {
                console.error("window.api não está disponível");
                this.notificationService.showError("API não está disponível");
                return;
            }

            const response = await this.apiService.listarProdutos();

            if (response.success) {
                this.produtos = response.data || [];
                this.produtosFiltrados = [...this.produtos];
                this.aplicarOrdenacao();
                this.atualizarPaginacao();
                this.renderizarProdutosComoCards(this.getProdutosPaginaAtual());
                this.atualizarContadores();
                console.log(`${this.produtos.length} produtos carregados com sucesso`);
            } else {
                this.notificationService.showError(
                    "Erro ao carregar produtos: " + response.error
                );
                console.error("Erro ao carregar produtos:", response.error);
            }
        } catch (error) {
            console.error("Erro ao carregar produtos:", error);
            this.notificationService.showError(
                "Erro interno ao carregar produtos"
            );
        } finally {
            this.mostrarLoading(false);
        }
    }

    /**
     * Renderiza produtos como cards
     * @param {Array} produtos - Lista de produtos
     */
    renderizarProdutosComoCards(produtos) {
        const container = document.getElementById("produtos-cards");
        if (!container) {
            console.error("Container de cards não encontrado");
            return;
        }

        if (!produtos || produtos.length === 0) {
            container.innerHTML = `<div class="empty-state">Nenhum produto encontrado.</div>`;
            return;
        }

        container.innerHTML = produtos
            .map(
                (p) => `
                <div class="card produto-card" onclick="abrirDetalhesProduto('${p.codigo}')">
                    <div class="card-header flex justify-between items-center">
                        <span class="font-bold">${p.codigo}</span>
                        <span class="status-badge ${p.status}">${this.formatarStatus(p.status)}</span>
                    </div>
                    <div class="card-body">
                        <div><strong>Categoria:</strong> ${p.tipoTraje || p.categoria || '-'}</div>
                        <div><strong>Cor:</strong> ${p.cor || '-'}</div>
                        <div><strong>Tamanho:</strong> ${p.tamanho || '-'}</div>
                        <div><strong>Valor Aluguel:</strong> ${FormatUtils.formatCurrency(p.preco || p.valorAluguel || 0)}</div>
                    </div>
                    <div class="card-footer flex gap-1">
                        <button type="button" class="btn btn-primary btn-sm" onclick="event.stopPropagation(); abrirDetalhesProduto('${p.codigo}')">Detalhes</button>
                        <button type="button" class="btn btn-secondary btn-sm" onclick="event.stopPropagation(); editarProduto('${p.codigo}')">Editar</button>
                        <button type="button" class="btn btn-danger btn-sm" onclick="event.stopPropagation(); confirmarExclusao('${p.codigo}')">Excluir</button>
                    </div>
                </div>
            `
            )
            .join("");
    }

    /**
     * Filtra produtos com base nos critérios de pesquisa
     */
    filtrarProdutos() {
        const searchTerm = document.getElementById("search-input")?.value.toLowerCase() || "";
        const categoriaFilter = document.getElementById("categoria-filter")?.value || "";
        const statusFilter = document.getElementById("status-filter")?.value || "";

        this.produtosFiltrados = this.produtos.filter((produto) => {
            const matchesSearch =
                !searchTerm ||
                (produto.codigo && produto.codigo.toLowerCase().includes(searchTerm)) ||
                (produto.tipoTraje && produto.tipoTraje.toLowerCase().includes(searchTerm)) ||
                (produto.categoria && produto.categoria.toLowerCase().includes(searchTerm)) ||
                (produto.cor && produto.cor.toLowerCase().includes(searchTerm));

            const matchesCategoria =
                !categoriaFilter || 
                (produto.tipoTraje && produto.tipoTraje.toLowerCase() === categoriaFilter) ||
                (produto.categoria && produto.categoria.toLowerCase() === categoriaFilter);

            const matchesStatus =
                !statusFilter || produto.status === statusFilter;

            return matchesSearch && matchesCategoria && matchesStatus;
        });

        this.aplicarOrdenacao();
        this.paginacao.paginaAtual = 1;
        this.atualizarPaginacao();
        this.renderizarProdutosComoCards(this.getProdutosPaginaAtual());
        this.atualizarContadores();
    }

    /**
     * Aplica ordenação à lista de produtos
     */
    aplicarOrdenacao() {
        this.produtosFiltrados.sort((a, b) => {
            let valueA = a[this.ordenacao.campo] || "";
            let valueB = b[this.ordenacao.campo] || "";

            if (typeof valueA === "string") {
                valueA = valueA.toLowerCase();
                valueB = valueB.toLowerCase();
            }

            if (this.ordenacao.direcao === "asc") {
                return valueA > valueB ? 1 : -1;
            } else {
                return valueA < valueB ? 1 : -1;
            }
        });
    }

    /**
     * Atualiza contadores de produtos
     */
    atualizarContadores() {
        const totalElement = document.getElementById("total-produtos");
        if (totalElement) {
            totalElement.textContent = `${this.produtosFiltrados.length} produtos encontrados`;
        }

        const showingFrom = document.getElementById("showing-from");
        const showingTo = document.getElementById("showing-to");
        const totalRecords = document.getElementById("total-records");
        const currentPage = document.getElementById("current-page");

        if (showingFrom && showingTo && totalRecords && currentPage) {
            const from = (this.paginacao.paginaAtual - 1) * this.paginacao.itensPorPagina + 1;
            const to = Math.min(
                this.paginacao.paginaAtual * this.paginacao.itensPorPagina,
                this.produtosFiltrados.length
            );

            showingFrom.textContent = this.produtosFiltrados.length > 0 ? from : 0;
            showingTo.textContent = to;
            totalRecords.textContent = this.produtosFiltrados.length;
            currentPage.textContent = this.paginacao.paginaAtual;
        }
    }

    /**
     * Atualiza informações de paginação
     */
    atualizarPaginacao() {
        this.paginacao.totalPaginas = Math.ceil(
            this.produtosFiltrados.length / this.paginacao.itensPorPagina
        );

        if (this.paginacao.paginaAtual > this.paginacao.totalPaginas) {
            this.paginacao.paginaAtual = Math.max(1, this.paginacao.totalPaginas);
        }
    }

    /**
     * Obtém produtos da página atual
     * @returns {Array} Produtos da página atual
     */
    getProdutosPaginaAtual() {
        const inicio = (this.paginacao.paginaAtual - 1) * this.paginacao.itensPorPagina;
        const fim = inicio + this.paginacao.itensPorPagina;
        return this.produtosFiltrados.slice(inicio, fim);
    }

    /**
     * Navega para a página anterior
     */
    paginaAnterior() {
        if (this.paginacao.paginaAtual > 1) {
            this.paginacao.paginaAtual--;
            this.renderizarProdutosComoCards(this.getProdutosPaginaAtual());
            this.atualizarContadores();
        }
    }

    /**
     * Navega para a próxima página
     */
    proximaPagina() {
        if (this.paginacao.paginaAtual < this.paginacao.totalPaginas) {
            this.paginacao.paginaAtual++;
            this.renderizarProdutosComoCards(this.getProdutosPaginaAtual());
            this.atualizarContadores();
        }
    }

    /**
     * Abre detalhes do produto
     * @param {string} codigo - Código do produto
     */
    async abrirDetalhesProduto(codigo) {
        try {
            this.mostrarLoading(true);

            const resultado = await this.apiService.consultarProduto(codigo);

            if (resultado.success && resultado.data) {
                this.produtoSelecionado = resultado.data;
                this.preencherModalDetalhes(resultado.data);
                this.abrirModal("product-modal");
            } else {
                this.notificationService.showError("Produto não encontrado");
            }
        } catch (error) {
            console.error("Erro ao abrir detalhes do produto:", error);
            this.notificationService.showError("Erro ao carregar detalhes do produto");
        } finally {
            this.mostrarLoading(false);
        }
    }

    /**
     * Preenche modal de detalhes do produto
     * @param {Object} produto - Dados do produto
     */
    preencherModalDetalhes(produto) {
        document.getElementById("modal-codigo").textContent = produto.codigo || "-";
        document.getElementById("modal-categoria").textContent = produto.tipoTraje || produto.categoria || "-";
        document.getElementById("modal-tecido").textContent = produto.tecido || "-";
        document.getElementById("modal-cor").textContent = produto.cor || "-";
        document.getElementById("modal-tamanho").textContent = produto.tamanho || "-";
        document.getElementById("modal-estado").textContent = produto.estado || "-";
        document.getElementById("modal-status").textContent = this.formatarStatus(produto.status);
        document.getElementById("modal-valor-aluguel").textContent = FormatUtils.formatCurrency(produto.preco || produto.valorAluguel || 0);
        document.getElementById("modal-descricao").textContent = produto.observacoes || produto.descricao || "-";
    }

    /**
     * Abre tela de edição de produto
     * @param {string} codigo - Código do produto
     */
    editarProduto(codigo = null) {
        const produtoCodigo = codigo || (this.produtoSelecionado ? this.produtoSelecionado.codigo : null);
        if (!produtoCodigo) return;

        if (window.api && window.api.openWindow) {
            window.api.openWindow("produto", { codigo: produtoCodigo });
            this.fecharModal();
        } else {
            console.error("API para abrir janela não está disponível");
            this.notificationService.showError("Não foi possível abrir a tela de edição");
        }
    }

    /**
     * Confirma exclusão de produto
     * @param {string} codigo - Código do produto
     */
    confirmarExclusao(codigo = null) {
        const produtoCodigo = codigo || (this.produtoSelecionado ? this.produtoSelecionado.codigo : null);
        if (!produtoCodigo) return;

        const produto = this.produtos.find(p => p.codigo === produtoCodigo);
        if (!produto) return;

        this.produtoSelecionado = produto;
        this.fecharModal();
        this.abrirModal("confirm-delete-modal");
    }

    /**
     * Exclui produto
     */
    async excluirProduto() {
        if (!this.produtoSelecionado || !this.produtoSelecionado.id) {
            this.notificationService.showError("Nenhum produto selecionado para exclusão");
            return;
        }

        try {
            this.mostrarLoading(true);
            this.fecharModal();

            const resultado = await this.apiService.excluirProduto(this.produtoSelecionado.id);

            if (resultado.success) {
                this.notificationService.showSuccess("Produto excluído com sucesso!");
                await this.carregarProdutos();
            } else {
                this.notificationService.showError(resultado.error || "Erro ao excluir produto");
            }
        } catch (error) {
            console.error("Erro ao excluir produto:", error);
            this.notificationService.showError("Erro ao excluir produto");
        } finally {
            this.mostrarLoading(false);
        }
    }

    /**
     * Exporta produtos para CSV
     */
    exportarProdutos() {
        try {
            if (this.produtosFiltrados.length === 0) {
                this.notificationService.showWarning("Não há produtos para exportar");
                return;
            }

            const headers = [
                "Código",
                "Categoria",
                "Tecido",
                "Cor",
                "Tamanho",
                "Status",
                "Valor Aluguel"
            ];

            const csvContent = [
                headers.join(","),
                ...this.produtosFiltrados.map(p => [
                    p.codigo || "",
                    p.tipoTraje || p.categoria || "",
                    p.tecido || "",
                    p.cor || "",
                    p.tamanho || "",
                    this.formatarStatus(p.status),
                    (p.preco || p.valorAluguel || 0).toString().replace(".", ",")
                ].join(","))
            ].join("\n");

            const blob = new Blob([csvContent], { type: "text/csv;charset=utf-8;" });
            const link = document.createElement("a");
            const url = URL.createObjectURL(blob);
            
            link.setAttribute("href", url);
            link.setAttribute("download", `produtos_${new Date().toISOString().split("T")[0]}.csv`);
            link.style.visibility = "hidden";
            
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            
            this.notificationService.showSuccess("Produtos exportados com sucesso!");
        } catch (error) {
            console.error("Erro ao exportar produtos:", error);
            this.notificationService.showError("Erro ao exportar produtos");
        }
    }

    /**
     * Abre tela de cadastro de produto
     */
    abrirCadastro() {
        if (window.api && window.api.openWindow) {
            window.api.openWindow("produto");
        } else {
            console.error("API para abrir janela não está disponível");
            this.notificationService.showError("Não foi possível abrir a tela de cadastro");
        }
    }

    /**
     * Limpa filtros de pesquisa
     */
    limparFiltros() {
        const searchInput = document.getElementById("search-input");
        if (searchInput) {
            searchInput.value = "";
        }

        const categoriaFilter = document.getElementById("categoria-filter");
        if (categoriaFilter) {
            categoriaFilter.value = "";
        }

        const statusFilter = document.getElementById("status-filter");
        if (statusFilter) {
            statusFilter.value = "";
        }

        this.filtrarProdutos();
    }

    /**
     * Abre um modal
     * @param {string} modalId - ID do modal a ser aberto
     */
    abrirModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.style.display = "flex";
        }
    }

    /**
     * Fecha todos os modais
     */
    fecharModal() {
        const modais = document.querySelectorAll(".modal");
        modais.forEach(modal => {
            modal.style.display = "none";
        });
    }

    /**
     * Mostra ou esconde o indicador de carregamento
     * @param {boolean} show - Indica se deve mostrar ou esconder
     */
    mostrarLoading(show) {
        const loading = document.getElementById("loading");
        if (loading) {
            loading.style.display = show ? "flex" : "none";
        }
    }

    /**
     * Formata status do produto
     * @param {string} status - Status a ser formatado
     * @returns {string} Status formatado
     */
    formatarStatus(status) {
        if (!status) return "Desconhecido";
        
        const statusMap = {
            "disponivel": "Disponível",
            "alugado": "Alugado",
            "manutencao": "Em Manutenção",
            "fora_linha": "Fora de Linha",
            "indisponivel": "Indisponível"
        };
        
        return statusMap[status] || status;
    }

    /**
     * Destrói o componente
     */
    destroy() {
        this.isInitialized = false;
    }
}

export default ProdutoListagemComponent;