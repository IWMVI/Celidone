/**
 * Componente para listagem de clientes
 * Implementa funcionalidades específicas para a visualização e filtragem de clientes
 */
import NotificationService from "../../../core/services/NotificationService.js";

class ClienteListagemComponent {
    constructor() {
        this.notificationService = new NotificationService();
        this.clientes = [];
        this.clientesFiltrados = [];
        this.ordenacao = { campo: "nome", direcao: "asc" };
        this.clienteSelecionado = null;
        this.isInitialized = false;
    }

    /**
     * Inicializa o componente
     */
    async init() {
        if (this.isInitialized) return;

        try {
            this.setupEventListeners();
            await this.carregarClientes();
            this.isInitialized = true;
            console.log("Componente de listagem de clientes inicializado com sucesso");
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
            searchInput.addEventListener("keyup", () => this.filtrarClientes());
        }

        // Listener para filtro de status
        const statusFilter = document.getElementById("status-filter");
        if (statusFilter) {
            statusFilter.addEventListener("change", () => this.filtrarClientes());
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
        const atualizarBtn = document.querySelector("button[onclick='atualizarLista()']");
        if (atualizarBtn) {
            atualizarBtn.onclick = (e) => {
                e.preventDefault();
                this.carregarClientes();
            };
        }

        // Listener para botão de novo cliente
        const novoCadastroBtn = document.querySelector("button[onclick='abrirCadastro()']");
        if (novoCadastroBtn) {
            novoCadastroBtn.onclick = (e) => {
                e.preventDefault();
                this.abrirCadastro();
            };
        }

        // Sobrescrever funções globais usadas nos elementos HTML
        window.visualizarCliente = (id) => this.visualizarCliente(id);
        window.editarCliente = (id) => this.editarCliente(id);
        window.confirmarExclusao = (id) => this.confirmarExclusao(id);
        window.fecharModal = () => this.fecharModal();
        window.filtrarClientes = () => this.filtrarClientes();
        window.limparFiltros = () => this.limparFiltros();
        window.atualizarLista = () => this.carregarClientes();
        window.abrirCadastro = () => this.abrirCadastro();
    }

    /**
     * Carrega clientes da API
     */
    async carregarClientes() {
        try {
            this.mostrarLoading(true);

            // Verificar se window.api está disponível
            if (!window.api) {
                console.error("window.api não está disponível");
                this.notificationService.showError("API não está disponível");
                return;
            }

            const response = await window.api.listarClientes();

            if (response.success) {
                this.clientes = response.data || [];
                this.clientesFiltrados = [...this.clientes];
                this.renderizarClientesComoCards(this.clientes);
                this.atualizarContador();
                console.log(`${this.clientes.length} clientes carregados com sucesso`);
            } else {
                this.notificationService.showError(
                    "Erro ao carregar clientes: " + response.error
                );
                console.error("Erro ao carregar clientes:", response.error);
            }
        } catch (error) {
            console.error("Erro ao carregar clientes:", error);
            this.notificationService.showError(
                "Erro interno ao carregar clientes"
            );
        } finally {
            this.mostrarLoading(false);
        }
    }

    /**
     * Renderiza clientes como cards
     * @param {Array} clientes - Lista de clientes
     */
    renderizarClientesComoCards(clientes) {
        const container = document.getElementById("clientes-cards");
        if (!container) {
            console.error("Container de cards não encontrado");
            return;
        }

        if (!clientes || clientes.length === 0) {
            container.innerHTML = "";
            const emptyState = document.getElementById("empty-state");
            if (emptyState) {
                emptyState.style.display = "block";
            }
            return;
        }

        const emptyState = document.getElementById("empty-state");
        if (emptyState) {
            emptyState.style.display = "none";
        }

        container.innerHTML = clientes
            .map(
                (c) => `
                <div class="card cliente-card" onclick="visualizarCliente(${c.id})">
                    <div class="card-header flex justify-between items-center">
                        <span class="font-bold">${c.nome}</span>
                        <span class="status-badge ${c.status}">${c.status === "ativo" ? "Ativo" : "Inativo"}</span>
                    </div>
                    <div class="card-body">
                        <div><strong>CPF:</strong> ${this.formatarCPF(c.cpf || '')}</div>
                        <div><strong>E-mail:</strong> ${c.email || ''}</div>
                        <div><strong>Telefone:</strong> ${this.formatarTelefone(c.celular || '')}</div>
                        <div><strong>Cidade:</strong> ${c.cidade || ''}/${c.uf || ''}</div>
                    </div>
                    <div class="card-footer flex gap-1">
                        <button type="button" class="btn btn-primary btn-sm" onclick="event.stopPropagation(); visualizarCliente(${c.id})">Visualizar</button>
                        <button type="button" class="btn btn-secondary btn-sm" onclick="event.stopPropagation(); editarCliente(${c.id})">Editar</button>
                        <button type="button" class="btn btn-danger btn-sm" onclick="event.stopPropagation(); confirmarExclusao(${c.id})">Excluir</button>
                    </div>
                </div>
            `
            )
            .join("");
    }

    /**
     * Filtra clientes com base nos critérios de pesquisa
     */
    filtrarClientes() {
        const searchTerm = document.getElementById("search-input")?.value.toLowerCase() || "";
        const statusFilter = document.getElementById("status-filter")?.value || "";

        this.clientesFiltrados = this.clientes.filter((cliente) => {
            const matchesSearch =
                !searchTerm ||
                (cliente.nome && cliente.nome.toLowerCase().includes(searchTerm)) ||
                (cliente.cpf && cliente.cpf.includes(searchTerm)) ||
                (cliente.email && cliente.email.toLowerCase().includes(searchTerm));

            const matchesStatus =
                !statusFilter || cliente.status === statusFilter;

            return matchesSearch && matchesStatus;
        });

        this.aplicarOrdenacao();
        this.renderizarClientesComoCards(this.clientesFiltrados);
        this.atualizarContador();
    }

    /**
     * Aplica ordenação à lista de clientes
     */
    aplicarOrdenacao() {
        this.clientesFiltrados.sort((a, b) => {
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
     * Atualiza contador de clientes
     */
    atualizarContador() {
        const total = this.clientesFiltrados.length;
        const totalGeral = this.clientes.length;
        const elemento = document.getElementById("total-clientes");

        if (!elemento) return;

        if (total === totalGeral) {
            elemento.textContent = `${total} cliente${
                total !== 1 ? "s" : ""
            } encontrado${total !== 1 ? "s" : ""}`;
        } else {
            elemento.textContent = `${total} de ${totalGeral} cliente${
                totalGeral !== 1 ? "s" : ""
            }`;
        }
    }

    /**
     * Visualiza detalhes do cliente
     * @param {number} id - ID do cliente
     */
    visualizarCliente(id) {
        const cliente = this.clientes.find((c) => c.id === id);
        if (!cliente) return;

        this.clienteSelecionado = cliente;

        const detalhesConteudo = document.getElementById("detalhes-conteudo");
        if (!detalhesConteudo) return;

        detalhesConteudo.innerHTML = `
            <div class="details-grid">
                <div class="detail-section">
                    <h4>Dados Pessoais</h4>
                    <p><strong>Nome:</strong> ${cliente.nome || ''}</p>
                    <p><strong>CPF:</strong> ${this.formatarCPF(cliente.cpf || '')}</p>
                    <p><strong>E-mail:</strong> ${cliente.email || ''}</p>
                    <p><strong>Telefone:</strong> ${this.formatarTelefone(cliente.celular || '')}</p>
                    ${
                        cliente.dataNascimento
                            ? `<p><strong>Data de Nascimento:</strong> ${this.formatarData(
                                  cliente.dataNascimento
                              )}</p>`
                            : ""
                    }
                    ${
                        cliente.rg
                            ? `<p><strong>RG:</strong> ${cliente.rg}</p>`
                            : ""
                    }
                </div>
                
                <div class="detail-section">
                    <h4>Endereço</h4>
                    <p><strong>CEP:</strong> ${this.formatarCEP(cliente.cep || '')}</p>
                    <p><strong>Logradouro:</strong> ${
                        cliente.endereco || ''
                    }, ${cliente.numero || ''}</p>
                    ${
                        cliente.complemento
                            ? `<p><strong>Complemento:</strong> ${cliente.complemento}</p>`
                            : ""
                    }
                    <p><strong>Bairro:</strong> ${cliente.bairro || ''}</p>
                    <p><strong>Cidade:</strong> ${cliente.cidade || ''}/${
                cliente.uf || ''
            }</p>
                </div>
                
                ${
                    cliente.observacoes
                        ? `
                <div class="detail-section">
                    <h4>Observações</h4>
                    <p>${cliente.observacoes}</p>
                </div>
                `
                        : ""
                }
                
                <div class="detail-section">
                    <h4>Informações do Sistema</h4>
                    <p><strong>Status:</strong> <span class="status-badge ${
                        cliente.status || 'ativo'
                    }">${
                cliente.status === "ativo" ? "Ativo" : "Inativo"
            }</span></p>
                    ${
                        cliente.dataCadastro
                            ? `<p><strong>Cadastrado em:</strong> ${this.formatarDataHora(
                                  cliente.dataCadastro
                              )}</p>`
                            : ""
                    }
                    ${
                        cliente.dataUltimaAtualizacao
                            ? `<p><strong>Última atualização:</strong> ${this.formatarDataHora(
                                  cliente.dataUltimaAtualizacao
                              )}</p>`
                            : ""
                    }
                </div>
            </div>
        `;

        const detalhesModal = document.getElementById("detalhes-modal");
        if (detalhesModal) {
            detalhesModal.style.display = "flex";
        }
    }

    /**
     * Abre tela de edição de cliente
     * @param {number} id - ID do cliente
     */
    editarCliente(id = null) {
        const clienteId = id || (this.clienteSelecionado ? this.clienteSelecionado.id : null);
        if (!clienteId) return;

        if (window.api && window.api.openWindow) {
            window.api.openWindow("cliente-editar", { clienteId });
            this.fecharModal();
        } else {
            console.error("API para abrir janela não está disponível");
            this.notificationService.showError("Não foi possível abrir a tela de edição");
        }
    }

    /**
     * Confirma exclusão de cliente
     * @param {number} id - ID do cliente
     */
    confirmarExclusao(id) {
        const cliente = this.clientes.find((c) => c.id === id);
        if (!cliente) return;

        if (
            confirm(
                `Tem certeza que deseja excluir o cliente "${cliente.nome}"?\n\nEsta ação não pode ser desfeita.`
            )
        ) {
            this.excluirCliente(id);
        }
    }

    /**
     * Exclui cliente
     * @param {number} id - ID do cliente
     */
    async excluirCliente(id) {
        try {
            if (!window.api) {
                console.error("window.api não está disponível");
                this.notificationService.showError("API não está disponível");
                return;
            }

            const response = await window.api.removerCliente(id);

            if (response.success) {
                this.notificationService.showSuccess(
                    "Cliente excluído com sucesso!"
                );
                await this.carregarClientes(); // Recarrega a lista
            } else {
                this.notificationService.showError(
                    "Erro ao excluir cliente: " + response.error
                );
            }
        } catch (error) {
            console.error("Erro ao excluir cliente:", error);
            this.notificationService.showError(
                "Erro interno ao excluir cliente"
            );
        }
    }

    /**
     * Abre tela de cadastro de cliente
     */
    abrirCadastro() {
        if (window.api && window.api.openWindow) {
            window.api.openWindow("cliente");
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

        const statusFilter = document.getElementById("status-filter");
        if (statusFilter) {
            statusFilter.value = "";
        }

        this.filtrarClientes();
    }

    /**
     * Fecha modal de detalhes
     */
    fecharModal() {
        const detalhesModal = document.getElementById("detalhes-modal");
        if (detalhesModal) {
            detalhesModal.style.display = "none";
        }
        this.clienteSelecionado = null;
    }

    /**
     * Mostra ou esconde o indicador de carregamento
     * @param {boolean} show - Indica se deve mostrar ou esconder
     */
    mostrarLoading(show) {
        const loadingState = document.getElementById("loading-state");
        if (loadingState) {
            loadingState.style.display = show ? "block" : "none";
        }
    }

    /**
     * Formata CPF
     * @param {string} cpf - CPF a ser formatado
     * @returns {string} CPF formatado
     */
    formatarCPF(cpf) {
        if (!cpf) return '';
        return cpf.replace(
            /(\d{3})(\d{3})(\d{3})(\d{2})/,
            "$1.$2.$3-$4"
        );
    }

    /**
     * Formata telefone
     * @param {string} telefone - Telefone a ser formatado
     * @returns {string} Telefone formatado
     */
    formatarTelefone(telefone) {
        if (!telefone) return '';
        return telefone.replace(
            /(\d{2})(\d{4,5})(\d{4})/,
            "($1) $2-$3"
        );
    }

    /**
     * Formata CEP
     * @param {string} cep - CEP a ser formatado
     * @returns {string} CEP formatado
     */
    formatarCEP(cep) {
        if (!cep) return '';
        return cep.replace(/(\d{5})(\d{3})/, "$1-$2");
    }

    /**
     * Formata data
     * @param {string} data - Data a ser formatada
     * @returns {string} Data formatada
     */
    formatarData(data) {
        if (!data) return '';
        return new Date(data).toLocaleDateString("pt-BR");
    }

    /**
     * Formata data e hora
     * @param {string} data - Data e hora a ser formatada
     * @returns {string} Data e hora formatada
     */
    formatarDataHora(data) {
        if (!data) return '';
        return new Date(data).toLocaleString("pt-BR");
    }

    /**
     * Destrói o componente
     */
    destroy() {
        this.isInitialized = false;
    }
}

export default ClienteListagemComponent;