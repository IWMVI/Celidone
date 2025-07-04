/**
 * Configurações de validação
 */
export const VALIDATION_CONFIG = {
    CPF: {
        REGEX: /^\d{3}\.\d{3}\.\d{3}-\d{2}$/,
        CLEAN_REGEX: /\D/g,
        LENGTH: 11,
        MASK: "###.###.###-##",
    },
    CNPJ: {
        REGEX: /^\d{2}\.\d{3}\.\d{3}\/\d{4}-\d{2}$/,
        CLEAN_REGEX: /\D/g,
        LENGTH: 14,
        MASK: "##.###.###/####-##",
    },
    CEP: {
        REGEX: /^\d{5}-?\d{3}$/,
        CLEAN_REGEX: /\D/g,
        LENGTH: 8,
        MASK: "#####-###",
    },
    TELEFONE: {
        CELULAR_REGEX: /^\(\d{2}\) \d{5}-\d{4}$/,
        FIXO_REGEX: /^\(\d{2}\) \d{4}-\d{4}$/,
        CELULAR_MASK: "(##) #####-####",
        FIXO_MASK: "(##) ####-####",
    },
    EMAIL: {
        REGEX: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
    },
    NOME: {
        MIN_LENGTH: 2,
        MAX_LENGTH: 100,
        REGEX: /^[a-zA-ZÀ-ÿ\s]+$/,
    },
};

/**
 * Mensagens de erro padrão
 */
export const ERROR_MESSAGES = {
    REQUIRED: "Este campo é obrigatório",
    INVALID_EMAIL: "Email inválido",
    INVALID_CPF: "CPF inválido",
    INVALID_CNPJ: "CNPJ inválido",
    INVALID_CEP: "CEP inválido",
    INVALID_PHONE: "Telefone inválido",
    INVALID_NAME: "Nome deve conter apenas letras",
    MIN_LENGTH: "Deve ter pelo menos {min} caracteres",
    MAX_LENGTH: "Deve ter no máximo {max} caracteres",
    NETWORK_ERROR: "Erro de conexão. Verifique sua internet",
    TIMEOUT: "Tempo limite excedido",
    GENERIC_ERROR: "Ocorreu um erro inesperado",
};
