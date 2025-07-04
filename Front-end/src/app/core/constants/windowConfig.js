/**
 * Configurações de janelas do Electron
 */
export const WINDOW_CONFIG = {
    DEFAULT: {
        width: 1000,
        height: 700,
        autoHideMenuBar: false,
        webPreferences: {
            contextIsolation: true,
            nodeIntegration: false,
            enableRemoteModule: false,
            sandbox: true,
        },
    },
    MAIN: {
        width: 1200,
        height: 800,
        minWidth: 800,
        minHeight: 600,
        show: false, // Será exibida após carregamento
        autoHideMenuBar: true,
    },
    MODAL: {
        width: 800,
        height: 600,
        modal: true,
        resizable: false,
        autoHideMenuBar: true,
    },
    CLIENTE: {
        width: 1100,
        height: 700,
        minWidth: 900,
        minHeight: 600,
        autoHideMenuBar: true,
    },
    PRODUTO: {
        width: 1000,
        height: 650,
        minWidth: 800,
        minHeight: 500,
        autoHideMenuBar: true,
    },
};

/**
 * Configurações de desenvolvimento
 */
export const DEV_CONFIG = {
    ENABLE_DEV_TOOLS: process.env.NODE_ENV === "development",
    ENABLE_HOT_RELOAD: process.env.NODE_ENV === "development",
    LOG_LEVEL: process.env.LOG_LEVEL || "info",
};
