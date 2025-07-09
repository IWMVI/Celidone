/**
 * Configurações da API
 * Centraliza todas as configurações relacionadas à API
 */
const API_CONFIG = {
    BASE_URL: process.env.API_BASE_URL || "http://localhost:8080",
    TIMEOUT: parseInt(process.env.API_TIMEOUT) || 10000,
    HEADERS: {
        "Content-Type": "application/json",
        Accept: "application/json",
    },
    ENDPOINTS: {
        CLIENTES: "/api/clientes",
        CLIENTE_CADASTRAR: "/api/clientes/cadastrar",
        CLIENTE_ATUALIZAR: "/api/clientes",
        CLIENTE_REMOVER: "/api/clientes",
        PRODUTOS: "/api/produtos",
        PRODUTO_CADASTRAR: "/api/produtos/cadastrar",
        PRODUTO_CONSULTAR: "/api/produtos/consultar",
        PRODUTO_ATUALIZAR: "/api/produtos",
        PRODUTO_REMOVER: "/api/produtos",
        PRODUTO_HISTORICO: "/api/produtos/historico",
        ALUGUEIS: "/api/alugueis",
        ALUGUEL_CADASTRAR: "/api/alugueis/cadastrar",
        ALUGUEL_ATUALIZAR: "/api/alugueis",
        ALUGUEL_REMOVER: "/api/alugueis",
    },
};

/**
 * Configuração para busca de CEP
 */
const CEP_CONFIG = {
    API_URL: "https://viacep.com.br/ws",
    TIMEOUT: 5000,
    RETRY_ATTEMPTS: 3,
    RETRY_DELAY: 1000,
};

export { API_CONFIG, CEP_CONFIG };
