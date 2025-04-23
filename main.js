const axios = require("axios");
const path = require("node:path");
const Cliente = require("./src/models/Cliente");
const { sequelize } = require("./config/database");
const { app, BrowserWindow, nativeTheme, ipcMain } = require("electron");

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
            contextIsolation: true,
            enableRemoteModule: false,
            nodeIntegration: false,
        },
    });

    win.loadFile("./src/public/views/index.html");
};

// Janela onde vai estar o CRUD do cliente
const clienteWindow = () => {
    const father = BrowserWindow.getFocusedWindow();
    if (father) {
        console.log("Criando janela de cliente...");
        const cliente = new BrowserWindow({
            width: 800,
            height: 600,
            icon: "./src/public/img/bowTie2.png",
            autoHideMenuBar: true,
            parent: father,
            webPreferences: {
                preload: path.join(__dirname, "preload.js"),
                contextIsolation: true,
                enableRemoteModule: false,
                nodeIntegration: false,
            },
        });
        cliente.loadFile("./src/public/views/cliente.html");
        cliente.webContents.openDevTools(); // Abre o DevTools para depuração
    }
};

// IPC para listar clientes
ipcMain.on("listar-clientes", async (event) => {
    try {
        const clientes = await Cliente.findAll(); // Busca todos os clientes no banco
        event.reply("clientes", clientes); // Envia os clientes para o renderer
    } catch (err) {
        console.error("Erro ao listar clientes:", err);
        event.reply("clientes", []); // Envia um array vazio em caso de erro
    }
});

// IPC para criar um cliente
ipcMain.on("criar-cliente", async (event, clienteData) => {
    try {
        const clienteCriado = await Cliente.create(clienteData); // Cria o cliente no banco
        event.reply("cliente-criado", {
            sucesso: true,
            cliente: clienteCriado,
        });
    } catch (err) {
        console.error("Erro ao criar cliente:", err);
        event.reply("cliente-criado", { sucesso: false, erro: err.message });
    }
});

// Sincroniza o banco de dados ao iniciar o app
sequelize
    .sync({ force: false }) // Use `force: true` apenas para recriar tabelas durante o desenvolvimento
    .then(() => {
        console.log("Banco de dados sincronizado.");
    })
    .catch((error) => {
        console.error("Erro ao sincronizar o banco de dados:", error);
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

// IPC para buscar CEP com timeout
ipcMain.handle("buscar-cep", async (event, cep) => {
    try {
        const response = await axios.get(
            `https://viacep.com.br/ws/${cep}/json/`,
            {
                timeout: 5000, // 5 segundos de timeout
            }
        );

        if (response.data.erro) {
            throw new Error("CEP não encontrado");
        }

        return response.data;
    } catch (error) {
        console.error("Erro ao buscar o CEP:", error);
        throw new Error(
            error.message.includes("timeout")
                ? "Tempo excedido ao buscar CEP"
                : "CEP não encontrado ou erro na conexão"
        );
    }
});
