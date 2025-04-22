const path = require("node:path");
const { app, BrowserWindow, nativeTheme, ipcMain } = require("electron");
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

app.whenReady().then(() => {
    createWindow();

    // Comunicação para criar um cliente
    ipcMain.on("criar-cliente", (event, clienteData) => {
        const clienteCriado = criarCliente(
            clienteData.nome,
            clienteData.telefone,
            clienteData.email
        );
        // Resposta para o renderer process com o cliente criado
        event.reply("cliente-criado", clienteCriado);
    });

    // Comunicação para listar clientes
    ipcMain.on("listar-clientes", (event) => {
        const clientes = listarClientes();
        event.reply("clientes-listados", clientes);
    });

    // Comunicação para abrir a janela de cliente
    ipcMain.on("open-window", () => {
        clienteWindow();
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

// Template de menu customizado
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
