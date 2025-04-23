const path = require("node:path");
const { app, BrowserWindow, nativeTheme, ipcMain } = require("electron");
const { listarClientes } = require("./src/models/cliente");

// Janela onde o dashboard vai ficar
const createWindow = () => {
    nativeTheme.themeSource = "dark";
    const win = new BrowserWindow({
        width: 800,
        height: 600,
        icon: "./src/public/img/bowTie2.png",
        autoHideMenuBar: false,
        webPreferences: {
            preload: path.join(__dirname, "preload.js"),
        },
    });

    win.loadFile("./src/public/views/index.html");
};

// Janela onde vai estar o CRUD do cliente
const clienteWindow = () => {
    const father = BrowserWindow.getFocusedWindow();
    if (father) {
        const cliente = new BrowserWindow({
            width: 800,
            height: 600,
            icon: "./src/public/img/bowTie2.png",
            autoHideMenuBar: true,
            parent: father,
        });
        cliente.loadFile("./src/public/views/cliente.html");
    }
};

// IPC para listar clientes
ipcMain.on("listar-clientes", async (event) => {
    try {
        const clientes = await listarClientes();
        event.reply("clientes", clientes); // Envia os clientes para o renderer
    } catch (err) {
        console.error("Erro ao listar clientes:", err);
    }
});

app.whenReady().then(() => {
    createWindow();

    // Comunicação de janelas
    ipcMain.on("open-window", (event, param) => {
        const windowMap = {
            cliente: clienteWindow,
        };

        const openWindow = windowMap[param];
        if (openWindow) {
            openWindow();
        } else {
            console.error(`Unknown window type: ${param}`);
        }
    });

    app.on("activate", () => {
        if (BrowserWindow.getAllWindows().length === 0) {
            createWindow();
        }
    });
});

app.on("window-all-closed", () => {
    if (process.platform !== "darwin") {
        app.quit();
    }
});
