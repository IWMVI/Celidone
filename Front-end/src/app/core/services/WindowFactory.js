import { BrowserWindow, nativeTheme } from "electron";
import path from "path";
import { fileURLToPath } from "url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
/**
 * Cria janela modal de cadastro de cliente
 * @param {BrowserWindow} parent - Janela pai
 * @returns {BrowserWindow} Janela modal de cadastro
 */
/* Fábrica de janelas do Electron
 * Implementa Factory Pattern para criação de janelas
 */
class WindowFactory {
    /**
     * Cria janela modal de cadastro de cliente
     * @param {BrowserWindow} parent - Janela pai
     * @returns {BrowserWindow} Janela modal de cadastro
     */
    static createClienteCadastroWindow(parent = null) {
        return WindowFactory.createWindow({
            width: 1000,
            height: 700,
            minWidth: 800,
            minHeight: 600,
            parent: parent || BrowserWindow.getFocusedWindow(),
            modal: true,
            loadFile: path.join(
                __dirname,
                "../../../public/views/cliente-cadastrar.html"
            ),
            autoHideMenuBar: true,
            resizable: true,
            devTools: process.env.NODE_ENV === "development",
        });
    }

    /**
     * Cria janela modal de consulta de clientes
     * @param {BrowserWindow} parent - Janela pai
     * @returns {BrowserWindow} Janela modal de consulta
     */
    static createClienteConsultaWindow(parent = null) {
        return WindowFactory.createWindow({
            width: 1200,
            height: 800,
            minWidth: 1000,
            minHeight: 600,
            parent: parent || BrowserWindow.getFocusedWindow(),
            modal: true,
            loadFile: path.join(
                __dirname,
                "../../../public/views/cliente-consultar.html"
            ),
            autoHideMenuBar: true,
            resizable: true,
            devTools: process.env.NODE_ENV === "development",
        });
    }

    /**
     * Cria janela modal de cadastro de produto
     * @param {BrowserWindow} parent - Janela pai
     * @returns {BrowserWindow} Janela modal de cadastro de produto
     */
    static createProdutoCadastroWindow(parent = null) {
        return WindowFactory.createWindow({
            width: 1000,
            height: 700,
            minWidth: 800,
            minHeight: 600,
            parent: parent || BrowserWindow.getFocusedWindow(),
            modal: true,
            loadFile: path.join(
                __dirname,
                "../../../public/views/produto-cadastrar.html"
            ),
            autoHideMenuBar: true,
            resizable: true,
            devTools: process.env.NODE_ENV === "development",
        });
    }

    /**
     * Cria janela modal de consulta de produtos
     * @param {BrowserWindow} parent - Janela pai
     * @returns {BrowserWindow} Janela modal de consulta de produtos
     */
    static createProdutoConsultaWindow(parent = null) {
        return WindowFactory.createWindow({
            width: 1200,
            height: 800,
            minWidth: 1000,
            minHeight: 600,
            parent: parent || BrowserWindow.getFocusedWindow(),
            modal: true,
            loadFile: path.join(
                __dirname,
                "../../../public/views/produto-consultar.html"
            ),
            autoHideMenuBar: true,
            resizable: true,
            devTools: process.env.NODE_ENV === "development",
        });
    }

    /**
     * Cria uma nova janela
     * @param {Object} config - Configuração da janela
     * @returns {BrowserWindow} Janela criada
     */
    static createWindow(config) {
        const defaultConfig = {
            width: 1000,
            height: 700,
            icon: path.join(__dirname, "../../../../public/img/bowTie2.png"),
            webPreferences: {
                preload: path.join(__dirname, "../../../../preload.js"),
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
        WindowFactory.setupWindowEvents(window);

        return window;
    }

    /**
     * Cria janela principal
     * @returns {BrowserWindow} Janela principal
     */
    static createMainWindow() {
        nativeTheme.themeSource = "dark";

        return WindowFactory.createWindow({
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
     * @param {BrowserWindow} parent - Janela pai (opcional)
     * @returns {BrowserWindow} Janela de cliente
     */
    static createClienteWindow(parent = null) {
        return WindowFactory.createWindow({
            width: 1100,
            height: 700,
            minWidth: 900,
            minHeight: 600,
            parent: parent, // Remove auto-detecção de parent
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
     * @param {BrowserWindow} parent - Janela pai (opcional)
     * @returns {BrowserWindow} Janela de produto
     */
    static createProdutoWindow(parent = null) {
        return WindowFactory.createWindow({
            width: 1000,
            height: 650,
            minWidth: 800,
            minHeight: 500,
            parent: parent, // Remove auto-detecção de parent
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
     * @param {BrowserWindow} parent - Janela pai (opcional)
     * @returns {BrowserWindow} Janela de aluguel
     */
    static createAluguelWindow(parent = null) {
        return WindowFactory.createWindow({
            width: 1000,
            height: 650,
            minWidth: 800,
            minHeight: 500,
            parent: parent, // Remove auto-detecção de parent
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
        return WindowFactory.createWindow({
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

        // Auto-fechamento após 10 minutos de inatividade (apenas para janelas filhas)
        if (window.getParentWindow()) {
            const TIMEOUT_MINUTES = 10;
            const TIMEOUT_MS = TIMEOUT_MINUTES * 60 * 1000;

            let timeoutId = setTimeout(() => {
                if (!window.isDestroyed()) {
                    console.log(
                        `Fechando janela por inatividade após ${TIMEOUT_MINUTES} minutos`
                    );
                    window.close();
                }
            }, TIMEOUT_MS);

            // Reset do timeout em caso de atividade
            const resetTimeout = () => {
                clearTimeout(timeoutId);
                timeoutId = setTimeout(() => {
                    if (!window.isDestroyed()) {
                        console.log(
                            `Fechando janela por inatividade após ${TIMEOUT_MINUTES} minutos`
                        );
                        window.close();
                    }
                }, TIMEOUT_MS);
            };

            // Eventos que resetam o timeout
            window.on("focus", resetTimeout);
            window.webContents.on("dom-ready", resetTimeout);
            window.webContents.on("did-navigate", resetTimeout);

            // Limpa timeout quando janela é fechada
            window.on("closed", () => {
                clearTimeout(timeoutId);
            });
        }

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
        const windows = WindowFactory.getAllWindows();
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

export default WindowFactory;
