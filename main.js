const path = require("node:path");
const axios = require("axios");
const { app, BrowserWindow, nativeTheme, ipcMain } = require("electron");

// Criação da janela principal
function createMainWindow() {
    nativeTheme.themeSource = "dark";
    const win = new BrowserWindow({
        width: 800,
        height: 600,
        icon: "./src/public/img/bowTie2.png",
        autoHideMenuBar: false,
        webPreferences: {
            preload: path.join(__dirname, "preload.js"),
            contextIsolation: true,
            nodeIntegration: false,
        },
    });

    win.loadFile("./src/public/views/index.html");
}

// Janela de cadastro de cliente
function createClienteWindow() {
    const parent = BrowserWindow.getFocusedWindow();
    if (parent) {
        const win = new BrowserWindow({
            width: 800,
            height: 600,
            parent,
            icon: "./src/public/img/bowTie2.png",
            autoHideMenuBar: true,
            webPreferences: {
                preload: path.join(__dirname, "preload.js"),
                contextIsolation: true,
                nodeIntegration: false,
            },
        });

        win.loadFile("./src/public/views/cliente.html");
        // win.webContents.openDevTools(); // Descomente para debug
    }
}

// IPC: Buscar CEP
ipcMain.handle("buscar-cep", async (event, cep) => {
    try {
        const response = await axios.get(
            `https://viacep.com.br/ws/${cep}/json/`,
            {
                timeout: 5000,
            }
        );

        if (response.data.erro) throw new Error("CEP não encontrado");
        return response.data;
    } catch (error) {
        console.error("Erro ao buscar o CEP:", error);
        throw new Error(
            error.message.includes("timeout")
                ? "Tempo excedido ao buscar CEP"
                : "CEP inválido ou erro na conexão"
        );
    }
});

// Inicialização da aplicação
app.whenReady().then(() => {
    createMainWindow();

    ipcMain.on("open-window", (event, windowType) => {
        const windows = {
            cliente: createClienteWindow,
        };

        const open = windows[windowType];
        if (open) open();
        else console.error(`Tipo de janela desconhecida: ${windowType}`);
    });

    app.on("activate", () => {
        if (BrowserWindow.getAllWindows().length === 0) {
            createMainWindow();
        }
    });
});

// Fechar app quando todas as janelas estiverem fechadas (exceto Mac)
app.on("window-all-closed", () => {
    if (process.platform !== "darwin") {
        app.quit();
    }
});

// Reload automático em desenvolvimento
require("electron-reload")(__dirname, {
    electron: require(`${__dirname}/node_modules/electron`),
});

