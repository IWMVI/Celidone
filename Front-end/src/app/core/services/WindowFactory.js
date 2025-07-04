const { BrowserWindow } = require("electron");
const path = require("path");

/**
 * Fábrica de janelas do Electron
 * Implementa Factory Pattern para criação de janelas
 */
class WindowFactory {
    /**
     * Cria uma nova janela
     * @param {Object} config - Configuração da janela
     * @returns {BrowserWindow} Janela criada
     */
    static createWindow(config) {
        const defaultConfig = {
            width: 1000,
            height: 700,
            icon: path.join(__dirname, "../../../public/img/bowTie2.png"),
            webPreferences: {
                preload: path.join(__dirname, "../../../preload.js"),
                contextIsolation: true,
                nodeIntegration: false,
                enableRemoteModule: false,
                sandbox: true,
            },
        };

        const mergedConfig = { ...defaultConfig, ...config };
        const window = new BrowserWindow(mergedConfig);

        // Carrega o arquivo HTML se especificado
        if (config.loadFile) {
            window.loadFile(config.loadFile);
        }

        // Abre DevTools se especificado
        if (config.devTools && process.env.NODE_ENV === "development") {
            window.webContents.openDevTools();
        }

        // Configura eventos padrão
        this.setupWindowEvents(window);

        return window;
    }

    /**
     * Cria janela principal
     * @returns {BrowserWindow} Janela principal
     */
    static createMainWindow() {
        const { nativeTheme } = require("electron");
        nativeTheme.themeSource = "dark";

        return this.createWindow({
            width: 1200,
            height: 800,
            minWidth: 800,
            minHeight: 600,
            loadFile: path.join(__dirname, "../../../public/views/index.html"),
            devTools: process.env.NODE_ENV === "development",
            show: false, // Será exibida após carregamento
        });
    }

    /**
     * Cria janela de cliente
     * @param {BrowserWindow} parent - Janela pai
     * @returns {BrowserWindow} Janela de cliente
     */
    static createClienteWindow(parent = null) {
        return this.createWindow({
            width: 1100,
            height: 700,
            minWidth: 900,
            minHeight: 600,
            parent: parent || BrowserWindow.getFocusedWindow(),
            loadFile: path.join(
                __dirname,
                "../../../public/views/cliente.html"
            ),
            autoHideMenuBar: true,
            devTools: process.env.NODE_ENV === "development",
        });
    }

    /**
     * Cria janela de produto
     * @param {BrowserWindow} parent - Janela pai
     * @returns {BrowserWindow} Janela de produto
     */
    static createProdutoWindow(parent = null) {
        return this.createWindow({
            width: 1000,
            height: 650,
            minWidth: 800,
            minHeight: 500,
            parent: parent || BrowserWindow.getFocusedWindow(),
            loadFile: path.join(
                __dirname,
                "../../../public/views/produto.html"
            ),
            autoHideMenuBar: true,
            devTools: process.env.NODE_ENV === "development",
        });
    }

    /**
     * Cria janela de aluguel
     * @param {BrowserWindow} parent - Janela pai
     * @returns {BrowserWindow} Janela de aluguel
     */
    static createAluguelWindow(parent = null) {
        return this.createWindow({
            width: 1000,
            height: 650,
            minWidth: 800,
            minHeight: 500,
            parent: parent || BrowserWindow.getFocusedWindow(),
            loadFile: path.join(
                __dirname,
                "../../../public/views/aluguel.html"
            ),
            autoHideMenuBar: true,
            devTools: process.env.NODE_ENV === "development",
        });
    }

    /**
     * Cria janela modal
     * @param {BrowserWindow} parent - Janela pai
     * @param {Object} config - Configuração adicional
     * @returns {BrowserWindow} Janela modal
     */
    static createModalWindow(parent, config = {}) {
        return this.createWindow({
            width: 600,
            height: 400,
            parent: parent || BrowserWindow.getFocusedWindow(),
            modal: true,
            resizable: false,
            autoHideMenuBar: true,
            ...config,
        });
    }

    /**
     * Configura eventos padrão da janela
     * @param {BrowserWindow} window - Janela
     */
    static setupWindowEvents(window) {
        // Exibe janela após carregamento para evitar flash
        window.once("ready-to-show", () => {
            window.show();
        });

        // Log de erros
        window.webContents.on(
            "did-fail-load",
            (event, errorCode, errorDescription, validatedURL) => {
                console.error(
                    `Falha ao carregar: ${errorCode} - ${errorDescription} - ${validatedURL}`
                );
            }
        );

        // Gerencia navegação
        window.webContents.on("will-navigate", (event, navigationUrl) => {
            const parsedUrl = new URL(navigationUrl);

            // Previne navegação para URLs externas
            if (parsedUrl.origin !== "file://") {
                event.preventDefault();
                console.warn(`Navegação bloqueada para: ${navigationUrl}`);
            }
        });

        // Gerencia abertura de novas janelas
        window.webContents.setWindowOpenHandler(({ url }) => {
            console.warn(`Abertura de nova janela bloqueada: ${url}`);
            return { action: "deny" };
        });
    }

    /**
     * Obtém todas as janelas abertas
     * @returns {BrowserWindow[]} Array de janelas
     */
    static getAllWindows() {
        return BrowserWindow.getAllWindows();
    }

    /**
     * Fecha todas as janelas
     */
    static closeAllWindows() {
        const windows = this.getAllWindows();
        windows.forEach((window) => {
            if (!window.isDestroyed()) {
                window.close();
            }
        });
    }

    /**
     * Obtém janela focada
     * @returns {BrowserWindow|null} Janela focada ou null
     */
    static getFocusedWindow() {
        return BrowserWindow.getFocusedWindow();
    }
}

module.exports = WindowFactory;
