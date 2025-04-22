const path = require("node:path");
const { app, BrowserWindow, nativeTheme, ipcMain, Menu } = require("electron");
const { criarCliente, listarClientes } = require("./src/models/cliente");

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

// Janela aonde vai estar o CRUD do traje
// Placeholder for the CRUD functionality of "traje" (to be implemented later)
const trajeWindow = () => {};

// Janela aonde vai estar o CRUD do aluguel
const aluguelWindow = () => {};

app.whenReady().then(() => {
    createWindow();

    // IPC >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    // Aqui é onde o IPC vai escutar as mensagens do processo de renderização

    // Esse IPC vai ser usado para trocar / abrir janelas
    ipcMain.on("open-window", (event, param) => {
        const windowMap = {
            // Aqui é onde fica o mapeamento de janelas
            // O nome da função é o mesmo que o parâmetro que vai ser passado + Window
            cliente: clienteWindow,
            traje: trajeWindow,
            aluguel: aluguelWindow,
        };

        const openWindow = windowMap[param];
        if (openWindow) {
            openWindow();
        } else {
            console.error(`Unknown window type: ${param}`);
        }
    });

    // Comunicação genérica de mensagens do renderer process
    ipcMain.on("renderer-message", (event, message) => {
        console.log(`Main recebeu uma mensagem: ${message}`);
        event.reply("main-message", "Mensagem recebida com sucesso!");
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

// Template do menu
// Para caso seja necessário criar um menu customizado
// const menu = Menu.buildFromTemplate(template)
const template = [
    {
        label: "Arquivo",
    },
    {
        label: "Exibir",
    },
    {
        label: "Ajuda",
    },
];

const menu = Menu.buildFromTemplate(template);
Menu.setApplicationMenu(menu);
