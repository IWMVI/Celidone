const { app, ipcMain } = require("electron");
const path = require("path");
const WindowFactory = require("./src/app/core/services/WindowFactory");
const ElectronApiService = require("./src/app/core/services/ElectronApiService");

/**
 * Aplicação principal do Electron
 * Refatorada seguindo os princípios SOLID e Clean Architecture
 */
class ElectronApp {
    constructor() {
        this.mainWindow = null;
        this.apiService = new ElectronApiService();
        this.windowFactories = new Map();
        this.initializeApp();
    }

    /**
     * Inicializa a aplicação
     */
    initializeApp() {
        this.setupWindowFactories();
        this.setupAppEvents();
        this.setupIpcHandlers();
    }

    /**
     * Configura as fábricas de janelas
     */
    setupWindowFactories() {
        this.windowFactories.set("main", WindowFactory.createMainWindow);
        this.windowFactories.set("cliente", WindowFactory.createClienteWindow);
        this.windowFactories.set("produto", WindowFactory.createProdutoWindow);
        this.windowFactories.set("aluguel", WindowFactory.createAluguelWindow);
    }

    /**
     * Configura eventos da aplicação
     */
    setupAppEvents() {
        app.whenReady().then(() => {
            this.createMainWindow();
            this.setupAppActivation();
        });

        app.on("window-all-closed", () => {
            if (process.platform !== "darwin") {
                app.quit();
            }
        });

        app.on("before-quit", () => {
            this.cleanup();
        });
    }

    /**
     * Configura ativação da aplicação (macOS)
     */
    setupAppActivation() {
        app.on("activate", () => {
            if (WindowFactory.getAllWindows().length === 0) {
                this.createMainWindow();
            }
        });
    }

    /**
     * Cria janela principal
     */
    createMainWindow() {
        this.mainWindow = WindowFactory.createMainWindow();

        this.mainWindow.on("closed", () => {
            this.mainWindow = null;
        });
    }

    /**
     * Configura manipuladores IPC
     */
    setupIpcHandlers() {
        // Manipulador para abertura de janelas
        ipcMain.on("open-window", (event, windowType, options = {}) => {
            console.log(
                `Recebida solicitação para abrir janela: ${windowType}`
            );
            this.openWindow(windowType, options);
        });

        // Manipuladores de API
        ipcMain.handle("buscar-cep", async (event, cep) => {
            return this.handleApiCall(() => this.apiService.buscarCep(cep));
        });

        ipcMain.handle("cadastrar-cliente", async (event, clienteData) => {
            return this.handleApiCall(() =>
                this.apiService.cadastrarCliente(clienteData)
            );
        });
        ipcMain.handle("listar-clientes", async () => {
            return this.handleApiCall(() => this.apiService.listarClientes());
        });

        // Manipuladores de produto
        ipcMain.handle("cadastrar-produto", async (event, produtoData) => {
            return this.handleApiCall(() =>
                this.apiService.cadastrarProduto(produtoData)
            );
        });

        ipcMain.handle("consultar-produto", async (event, codigo) => {
            return this.handleApiCall(() =>
                this.apiService.consultarProduto(codigo)
            );
        });

        ipcMain.handle("atualizar-produto", async (event, id, produtoData) => {
            return this.handleApiCall(() =>
                this.apiService.atualizarProduto(id, produtoData)
            );
        });

        ipcMain.handle("excluir-produto", async (event, id) => {
            return this.handleApiCall(() => this.apiService.excluirProduto(id));
        });

        ipcMain.handle("listar-produtos", async () => {
            return this.handleApiCall(() => this.apiService.listarProdutos());
        });

        ipcMain.handle(
            "consultar-historico-aluguel",
            async (event, produtoId) => {
                return this.handleApiCall(() =>
                    this.apiService.consultarHistoricoAluguel(produtoId)
                );
            }
        );

        // Manipuladores de aluguel
        ipcMain.handle("cadastrar-aluguel", async (event, aluguelData) => {
            return this.handleApiCall(() =>
                this.apiService.cadastrarAluguel(aluguelData)
            );
        });

        ipcMain.handle("listar-alugueis", async () => {
            return this.handleApiCall(() => this.apiService.listarAlugueis());
        });

        ipcMain.handle("atualizar-aluguel", async (event, id, aluguelData) => {
            return this.handleApiCall(() =>
                this.apiService.atualizarAluguel(id, aluguelData)
            );
        });

        ipcMain.handle("devolver-aluguel", async (event, id) => {
            return this.handleApiCall(() =>
                this.apiService.devolverAluguel(id)
            );
        });

        ipcMain.handle("cancelar-aluguel", async (event, id) => {
            return this.handleApiCall(() =>
                this.apiService.cancelarAluguel(id)
            );
        });

        ipcMain.handle("consultar-aluguel", async (event, id) => {
            return this.handleApiCall(() =>
                this.apiService.consultarAluguel(id)
            );
        });

        // Manipuladores de cliente
        ipcMain.handle("atualizar-cliente", async (event, id, clienteData) => {
            return this.handleApiCall(() =>
                this.apiService.atualizarCliente(id, clienteData)
            );
        });

        ipcMain.handle("remover-cliente", async (event, id) => {
            return this.handleApiCall(() => this.apiService.removerCliente(id));
        });

        // Manipuladores de sistema
        ipcMain.handle("get-app-version", () => {
            return app.getVersion();
        });

        ipcMain.handle("get-app-path", () => {
            return app.getAppPath();
        });
    }

    /**
     * Abre janela específica
     * @param {string} windowType - Tipo da janela
     * @param {Object} options - Opções da janela
     */
    openWindow(windowType, options = {}) {
        console.log(`Tentando abrir janela do tipo: ${windowType}`);
        const factory = this.windowFactories.get(windowType);

        if (!factory) {
            console.error(`Tipo de janela não encontrado: ${windowType}`);
            return;
        }

        console.log(`Factory encontrada para ${windowType}, criando janela...`);
        try {
            const window = factory(options.parent);
            console.log(`Janela ${windowType} criada com sucesso`);

            if (options.onCreated) {
                options.onCreated(window);
            }

            return window;
        } catch (error) {
            console.error(`Erro ao criar janela ${windowType}:`, error);
        }
    }

    /**
     * Manipula chamadas de API
     * @param {Function} apiCall - Função da API a ser chamada
     * @returns {Promise} Resultado da chamada
     */
    async handleApiCall(apiCall) {
        try {
            return await apiCall();
        } catch (error) {
            console.error("Erro na chamada da API:", error);
            return {
                success: false,
                error: error.message || "Erro interno do servidor",
            };
        }
    }

    /**
     * Limpeza antes de encerrar a aplicação
     */
    cleanup() {
        console.log("Limpando recursos da aplicação...");
        // Aqui você pode adicionar limpeza de recursos específicos
    }
}

// Inicializa a aplicação
const electronApp = new ElectronApp();

// Hot Reload para desenvolvimento
if (process.env.NODE_ENV === "development") {
    try {
        require("electron-reload")(__dirname, {
            electron: require("path").join(
                __dirname,
                "node_modules",
                ".bin",
                process.platform === "win32" ? "electron.cmd" : "electron"
            ),
            hardResetMethod: "exit",
            // Observa também arquivos HTML, CSS e JS fora da pasta raiz
            forceHardReset: true,
            awaitWriteFinish: true,
            // Inclua outros diretórios relevantes se necessário
            // Exemplo: watch: [__dirname, 'src', 'public']
        });
        console.log("Hot reload ativado!");
    } catch (error) {
        console.error("Electron reload não disponível:", error);
    }
}

module.exports = ElectronApp;
