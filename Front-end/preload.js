const { contextBridge, ipcRenderer } = require("electron");

/**
 * Preload script refatorado seguindo princípios de segurança
 * Expõe apenas APIs necessárias para o renderer process
 */

// Canais IPC permitidos
const ALLOWED_CHANNELS = {
    INVOKE: [
        "buscar-cep",
        "cadastrar-cliente",
        "listar-clientes",
        "atualizar-cliente",
        "remover-cliente",
        "cadastrar-produto",
        "consultar-produto",
        "atualizar-produto",
        "excluir-produto",
        "listar-produtos",
        "consultar-historico-aluguel",
        "cadastrar-aluguel",
        "listar-alugueis",
        "atualizar-aluguel",
        "consultar-aluguel",
        "devolver-aluguel",
        "cancelar-aluguel",
        "get-app-version",
        "get-app-path",
    ],
    SEND: ["open-window"],
    RECEIVE: [
        "clientes-updated",
        "produtos-updated",
        "alugueis-updated",
        "window-closed",
        "app-notification",
    ],
};

/**
 * Valida se o canal é permitido
 * @param {string} channel - Canal a ser validado
 * @param {string} type - Tipo do canal (invoke, send, receive)
 * @returns {boolean} True se permitido
 */
function isChannelAllowed(channel, type) {
    const allowedChannels = ALLOWED_CHANNELS[type.toUpperCase()];
    return allowedChannels && allowedChannels.includes(channel);
}

/**
 * API segura para comunicação entre processos
 */
const electronAPI = {
    /**
     * Obtém versão do Electron
     * @returns {string} Versão do Electron
     */
    getElectronVersion: () => process.versions.electron,

    /**
     * Obtém versão da aplicação
     * @returns {Promise<string>} Versão da aplicação
     */
    getAppVersion: () => ipcRenderer.invoke("get-app-version"),

    /**
     * Obtém caminho da aplicação
     * @returns {Promise<string>} Caminho da aplicação
     */
    getAppPath: () => ipcRenderer.invoke("get-app-path"),

    /**
     * Abre uma janela específica
     * @param {string} windowType - Tipo da janela
     * @param {Object} options - Opções da janela
     */
    openWindow: (windowType, options = {}) => {
        if (!windowType || typeof windowType !== "string") {
            console.error("Tipo de janela inválido");
            return;
        }
        ipcRenderer.send("open-window", windowType, options);
    },

    /**
     * Envia mensagem para o processo principal
     * @param {string} channel - Canal da mensagem
     * @param {any} data - Dados da mensagem
     */
    send: (channel, data) => {
        if (!isChannelAllowed(channel, "send")) {
            console.error(`Canal '${channel}' não permitido para send`);
            return;
        }
        ipcRenderer.send(channel, data);
    },

    /**
     * Escuta mensagens do processo principal
     * @param {string} channel - Canal da mensagem
     * @param {Function} callback - Callback para processar mensagem
     */
    on: (channel, callback) => {
        if (!isChannelAllowed(channel, "receive")) {
            console.error(`Canal '${channel}' não permitido para receive`);
            return;
        }

        if (typeof callback !== "function") {
            console.error("Callback deve ser uma função");
            return;
        }

        ipcRenderer.on(channel, (event, ...args) => {
            callback(...args);
        });
    },

    /**
     * Remove listener de mensagens
     * @param {string} channel - Canal da mensagem
     * @param {Function} callback - Callback a ser removido
     */
    off: (channel, callback) => {
        if (!isChannelAllowed(channel, "receive")) {
            console.error(`Canal '${channel}' não permitido para receive`);
            return;
        }

        if (typeof callback !== "function") {
            console.error("Callback deve ser uma função");
            return;
        }

        ipcRenderer.removeListener(channel, callback);
    },

    /**
     * Invoca método no processo principal
     * @param {string} channel - Canal do método
     * @param {...any} args - Argumentos do método
     * @returns {Promise} Resultado do método
     */
    invoke: (channel, ...args) => {
        if (!isChannelAllowed(channel, "invoke")) {
            console.error(`Canal '${channel}' não permitido para invoke`);
            return Promise.reject(new Error("Canal não permitido"));
        }

        return ipcRenderer.invoke(channel, ...args);
    },

    // APIs específicas para facilitar uso

    /**
     * Busca dados de CEP
     * @param {string} cep - CEP a ser buscado
     * @returns {Promise<Object>} Dados do CEP
     */
    buscarCep: (cep) => {
        if (!cep || typeof cep !== "string") {
            return Promise.reject(new Error("CEP inválido"));
        }
        return ipcRenderer.invoke("buscar-cep", cep);
    },

    /**
     * Cadastra um novo cliente
     * @param {Object} clienteData - Dados do cliente
     * @returns {Promise<Object>} Resultado do cadastro
     */
    cadastrarCliente: (clienteData) => {
        if (!clienteData || typeof clienteData !== "object") {
            return Promise.reject(new Error("Dados do cliente inválidos"));
        }
        return ipcRenderer.invoke("cadastrar-cliente", clienteData);
    },

    /**
     * Lista todos os clientes
     * @returns {Promise<Object>} Lista de clientes
     */
    listarClientes: () => {
        return ipcRenderer.invoke("listar-clientes");
    },

    /**
     * Atualiza um cliente existente
     * @param {string} id - ID do cliente
     * @param {Object} clienteData - Dados do cliente
     * @returns {Promise<Object>} Resultado da atualização
     */
    atualizarCliente: (id, clienteData) => {
        if (!id || typeof id !== "string") {
            return Promise.reject(new Error("ID do cliente inválido"));
        }
        if (!clienteData || typeof clienteData !== "object") {
            return Promise.reject(new Error("Dados do cliente inválidos"));
        }
        return ipcRenderer.invoke("atualizar-cliente", id, clienteData);
    },
    /**
     * Remove um cliente
     * @param {string} id - ID do cliente
     * @returns {Promise<Object>} Resultado da remoção
     */
    removerCliente: (id) => {
        if (!id || typeof id !== "string") {
            return Promise.reject(new Error("ID do cliente inválido"));
        }
        return ipcRenderer.invoke("remover-cliente", id);
    },

    // ==================== MÉTODOS DE PRODUTO ====================

    /**
     * Cadastra um novo produto
     * @param {Object} produtoData - Dados do produto
     * @returns {Promise<Object>} Resultado do cadastro
     */
    cadastrarProduto: (produtoData) => {
        if (!produtoData || typeof produtoData !== "object") {
            return Promise.reject(new Error("Dados do produto inválidos"));
        }
        return ipcRenderer.invoke("cadastrar-produto", produtoData);
    },

    /**
     * Consulta produto por código
     * @param {string} codigo - Código do produto
     * @returns {Promise<Object>} Dados do produto
     */
    consultarProduto: (codigo) => {
        if (!codigo || typeof codigo !== "string") {
            return Promise.reject(new Error("Código do produto é obrigatório"));
        }
        return ipcRenderer.invoke("consultar-produto", codigo);
    },

    /**
     * Atualiza um produto existente
     * @param {string} id - ID do produto
     * @param {Object} produtoData - Dados do produto
     * @returns {Promise<Object>} Resultado da atualização
     */
    atualizarProduto: (id, produtoData) => {
        if (!id) {
            return Promise.reject(new Error("ID do produto é obrigatório"));
        }
        if (!produtoData || typeof produtoData !== "object") {
            return Promise.reject(new Error("Dados do produto inválidos"));
        }
        return ipcRenderer.invoke("atualizar-produto", id, produtoData);
    },

    /**
     * Exclui um produto
     * @param {string} id - ID do produto
     * @returns {Promise<Object>} Resultado da exclusão
     */
    excluirProduto: (id) => {
        if (!id) {
            return Promise.reject(new Error("ID do produto é obrigatório"));
        }
        return ipcRenderer.invoke("excluir-produto", id);
    },

    /**
     * Lista todos os produtos
     * @returns {Promise<Object>} Lista de produtos
     */
    listarProdutos: () => {
        return ipcRenderer.invoke("listar-produtos");
    },

    // ==================== MÉTODOS DE ALUGUEL ====================

    /**
     * Cadastra um novo aluguel
     * @param {Object} aluguelData - Dados do aluguel
     * @returns {Promise<Object>} Resultado do cadastro
     */
    cadastrarAluguel: (aluguelData) => {
        if (!aluguelData || typeof aluguelData !== "object") {
            return Promise.reject(new Error("Dados do aluguel inválidos"));
        }
        return ipcRenderer.invoke("cadastrar-aluguel", aluguelData);
    },

    /**
     * Lista todos os aluguéis
     * @returns {Promise<Object>} Lista de aluguéis
     */
    listarAlugueis: () => {
        return ipcRenderer.invoke("listar-alugueis");
    },

    /**
     * Atualiza um aluguel existente
     * @param {string} id - ID do aluguel
     * @param {Object} aluguelData - Dados do aluguel
     * @returns {Promise<Object>} Resultado da atualização
     */
    atualizarAluguel: (id, aluguelData) => {
        if (!id) {
            return Promise.reject(new Error("ID do aluguel é obrigatório"));
        }
        if (!aluguelData || typeof aluguelData !== "object") {
            return Promise.reject(new Error("Dados do aluguel inválidos"));
        }
        return ipcRenderer.invoke("atualizar-aluguel", id, aluguelData);
    },

    /**
     * Consulta um aluguel específico
     * @param {string} id - ID do aluguel
     * @returns {Promise<Object>} Dados do aluguel
     */
    consultarAluguel: (id) => {
        if (!id) {
            return Promise.reject(new Error("ID do aluguel é obrigatório"));
        }
        return ipcRenderer.invoke("consultar-aluguel", id);
    },

    /**
     * Devolve um aluguel (marca como devolvido)
     * @param {string} id - ID do aluguel
     * @returns {Promise<Object>} Resultado da devolução
     */
    devolverAluguel: (id) => {
        if (!id) {
            return Promise.reject(new Error("ID do aluguel é obrigatório"));
        }
        return ipcRenderer.invoke("devolver-aluguel", id);
    },

    /**
     * Cancela um aluguel
     * @param {string} id - ID do aluguel
     * @returns {Promise<Object>} Resultado do cancelamento
     */
    cancelarAluguel: (id) => {
        if (!id) {
            return Promise.reject(new Error("ID do aluguel é obrigatório"));
        }
        return ipcRenderer.invoke("cancelar-aluguel", id);
    },

    /**
     * Consulta histórico de aluguel de um produto
     * @param {string} produtoId - ID do produto
     * @returns {Promise<Object>} Histórico de aluguel
     */
    consultarHistoricoAluguel: (produtoId) => {
        if (!produtoId) {
            return Promise.reject(new Error("ID do produto é obrigatório"));
        }
        return ipcRenderer.invoke("consultar-historico-aluguel", produtoId);
    },
};

// Expõe API no contexto do renderer
contextBridge.exposeInMainWorld("api", electronAPI);

// Expõe informações do sistema (somente leitura)
contextBridge.exposeInMainWorld("system", {
    platform: process.platform,
    arch: process.arch,
    versions: {
        node: process.versions.node,
        chrome: process.versions.chrome,
        electron: process.versions.electron,
    },
});

// Expõe utilitários seguros
contextBridge.exposeInMainWorld("utils", {
    /**
     * Valida se uma string é um email válido
     * @param {string} email - Email a ser validado
     * @returns {boolean} True se válido
     */
    isValidEmail: (email) => {
        if (!email || typeof email !== "string") return false;
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    },

    /**
     * Valida se uma string é um CPF válido
     * @param {string} cpf - CPF a ser validado
     * @returns {boolean} True se válido
     */
    isValidCpf: (cpf) => {
        if (!cpf || typeof cpf !== "string") return false;
        const cpfLimpo = cpf.replace(/\D/g, "");
        return cpfLimpo.length === 11 && !/^(\d)\1+$/.test(cpfLimpo);
    },

    /**
     * Valida se uma string é um CNPJ válido
     * @param {string} cnpj - CNPJ a ser validado
     * @returns {boolean} True se válido
     */
    isValidCnpj: (cnpj) => {
        if (!cnpj || typeof cnpj !== "string") return false;
        const cnpjLimpo = cnpj.replace(/\D/g, "");
        return cnpjLimpo.length === 14 && !/^(\d)\1+$/.test(cnpjLimpo);
    },

    /**
     * Formata data para exibição
     * @param {string|Date} date - Data a ser formatada
     * @returns {string} Data formatada
     */
    formatDate: (date) => {
        if (!date) return "";
        const d = new Date(date);
        if (isNaN(d.getTime())) return "";
        return d.toLocaleDateString("pt-BR");
    },

    /**
     * Sanitiza string para evitar XSS
     * @param {string} str - String a ser sanitizada
     * @returns {string} String sanitizada
     */
    sanitizeString: (str) => {
        if (!str || typeof str !== "string") return "";
        return str.replace(/[<>&"']/g, (char) => {
            const entities = {
                "<": "&lt;",
                ">": "&gt;",
                "&": "&amp;",
                '"': "&quot;",
                "'": "&#39;",
            };
            return entities[char] || char;
        });
    },
});

// Log para debug (apenas em desenvolvimento)
if (process.env.NODE_ENV === "development") {
    console.log("Preload script carregado com sucesso");
    console.log("APIs disponíveis:", Object.keys(electronAPI));
}
