const path = require("node:path");
const { app, BrowserWindow, nativeTheme, ipcMain } = require("electron");
const axios = require("axios");

// Configurações da API
const API_CONFIG = {
    BASE_URL: "http://localhost:8080",
    ENDPOINTS: {
        CLIENTES: "/api/clientes",
        CADASTRAR: "/api/clientes/cadastrar",
    },
    TIMEOUT: 10000,
};

// Criação de janelas
function createWindow(config) {
    const win = new BrowserWindow({
        width: config.width || 1000,
        height: config.height || 700,
        icon: path.join(__dirname, "./src/public/img/bowTie2.png"),
        autoHideMenuBar: config.autoHideMenuBar || false,
        parent: config.parent || null,
        webPreferences: {
            preload: path.join(__dirname, "preload.js"),
            contextIsolation: true,
            nodeIntegration: false,
            enableRemoteModule: false,
            sandbox: true,
        },
    });

    win.loadFile(path.join(__dirname, config.loadFile));
    if (config.devTools) win.webContents.openDevTools();
    return win;
}

// Janela principal
function createMainWindow() {
    nativeTheme.themeSource = "dark";
    return createWindow({
        loadFile: "./src/public/views/index.html",
        devTools: process.env.NODE_ENV === "development",
    });
}

// Janela de cliente
function createClienteWindow() {
    const parent = BrowserWindow.getFocusedWindow();
    return createWindow({
        loadFile: "./src/public/views/cliente.html",
        parent,
        autoHideMenuBar: true,
        devTools: process.env.NODE_ENV === "development",
    });
}

// Janela de produto (exemplo adicional)
function createProdutoWindow() {
    const parent = BrowserWindow.getFocusedWindow();
    return createWindow({
        loadFile: "./src/public/views/produto.html",
        parent,
        autoHideMenuBar: true,
        devTools: process.env.NODE_ENV === "development",
    });
}

// Serviço de API
class ApiService {
    static async buscarCEP(cep) {
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
            console.error("Erro ao buscar CEP:", error);
            throw new Error(
                error.message.includes("timeout")
                    ? "Tempo excedido ao buscar CEP"
                    : "CEP inválido ou erro na conexão"
            );
        }
    }

    static async cadastrarCliente(clienteData) {
        try {
            // Verifica se é PJ e tem CNPJ válido
            if (
                clienteData.natureza === "pessoa_juridica" &&
                !clienteData.cnpj
            ) {
                return {
                    success: false,
                    error: "CNPJ é obrigatório para Pessoa Jurídica",
                };
            }

            // Verifica se é PF e tem CPF válido
            if (clienteData.natureza === "pessoa_fisica" && !clienteData.cpf) {
                return {
                    success: false,
                    error: "CPF é obrigatório para Pessoa Física",
                };
            }

            const dadosBackend = {
                nome: clienteData.nome,
                email: clienteData.email,
                tipoPessoa:
                    clienteData.natureza === "PESSOA_FISICA"
                        ? "PESSOA_FISICA"
                        : "PESSOA_JURIDICA",
                dataNascimento: clienteData.dataNascimento,
                cep: clienteData.cep,
                endereco: clienteData.endereco,
                numero: clienteData.numero,
                cidade: clienteData.cidade,
                bairro: clienteData.bairro,
                complemento: clienteData.complemento,
                uf: clienteData.uf,
                celular: clienteData.celular,
                telefoneFixo: clienteData.telefoneFixo,
                cpf: clienteData.cpf,
                cnpj: clienteData.cnpj,
            };

            const response = await axios.post(
                `${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.CADASTRAR}`,
                dadosBackend,
                {
                    timeout: API_CONFIG.TIMEOUT,
                    headers: {
                        "Content-Type": "application/json",
                    },
                }
            );

            return {
                success: true,
                data: response.data,
            };
        } catch (error) {
            console.error("Erro ao cadastrar cliente:", error);

            let errorMessage = "Erro ao cadastrar cliente";
            if (error.response?.data) {
                if (typeof error.response.data === "string") {
                    errorMessage = error.response.data;
                } else if (error.response.data.message) {
                    errorMessage = error.response.data.message;
                } else if (error.response.data.errors) {
                    errorMessage = error.response.data.errors
                        .map((e) => e.defaultMessage)
                        .join(", ");
                }
            }

            return {
                success: false,
                error: errorMessage,
            };
        }
    }

    static async listarClientes() {
        try {
            const response = await axios.get(
                `${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.CLIENTES}`,
                {
                    timeout: API_CONFIG.TIMEOUT,
                    headers: {
                        "Content-Type": "application/json",
                    },
                }
            );

            return {
                success: true,
                data: response.data,
            };
        } catch (error) {
            console.error("Erro ao listar clientes:", error);
            return {
                success: false,
                error:
                    error.response?.data?.message ||
                    error.message ||
                    "Erro ao listar clientes",
            };
        }
    }
}

// Inicialização da aplicação
app.whenReady().then(() => {
    const mainWindow = createMainWindow();

    // Manipuladores de IPC
    ipcMain.handle("buscar-cep", async (event, cep) => {
        return ApiService.buscarCEP(cep);
    });

    ipcMain.handle("cadastrar-cliente", async (event, clienteData) => {
        return ApiService.cadastrarCliente(clienteData);
    });

    ipcMain.handle("listar-clientes", async () => {
        return ApiService.listarClientes();
    });

    ipcMain.on("open-window", (event, windowType) => {
        const windowCreators = {
            cliente: createClienteWindow,
            produto: createProdutoWindow, // Janela de produto
        };

        const creator = windowCreators[windowType];

        if (creator) {
            creator(); // Chama a função correspondente ao tipo de janela
        } else {
            console.error(`Janela não encontrada para o tipo: ${windowType}`);
        }
    });

    app.on("activate", () => {
        if (BrowserWindow.getAllWindows().length === 0) createMainWindow();
    });
});

// Encerramento da aplicação
app.on("window-all-closed", () => {
    if (process.platform !== "darwin") app.quit();
});

// Hot Reload para desenvolvimento
if (process.env.NODE_ENV === "development") {
    require("electron-reload")(__dirname, {
        electron: path.join(__dirname, "node_modules", ".bin", "electron"),
    });
}

