/**
 * Configurações do módulo de aluguel
 *
 * @description Configurações específicas para o módulo de aluguel incluindo
 * validações, enums, sugestões e endpoints
 *
 * @author Equipe TCC
 * @since 2025
 */

import { apiConfig } from "../../../core/constants/apiConfig.js";

/**
 * Configurações de validação para aluguel
 */
export const AluguelValidationConfig = {
    // Validações de campos obrigatórios
    required: {
        clienteId: "Cliente é obrigatório",
        produtoId: "Produto é obrigatório",
        dataAluguel: "Data do aluguel é obrigatória",
        dataDevPrevista: "Data prevista para devolução é obrigatória",
        valorAluguel: "Valor do aluguel é obrigatório",
        valorCaucao: "Valor da caução é obrigatório",
    },

    // Validações de formato
    format: {
        valorAluguel: {
            min: 0.01,
            max: 9999.99,
            message: "Valor do aluguel deve ser entre R$ 0,01 e R$ 9.999,99",
        },
        valorCaucao: {
            min: 0,
            max: 99999.99,
            message: "Valor da caução deve ser entre R$ 0,00 e R$ 99.999,99",
        },
        dataAluguel: {
            message: "Data do aluguel deve ser uma data válida",
        },
        dataDevPrevista: {
            message: "Data prevista para devolução deve ser uma data válida",
        },
        dataDevEfetiva: {
            message: "Data efetiva de devolução deve ser uma data válida",
        },
    },

    // Validações de regras de negócio
    business: {
        dataDevPrevista: {
            message:
                "Data prevista para devolução deve ser posterior à data do aluguel",
        },
        dataDevEfetiva: {
            message:
                "Data efetiva de devolução não pode ser anterior à data do aluguel",
        },
        produtoDisponivel: {
            message: "Produto não está disponível para aluguel",
        },
        clienteAtivo: {
            message: "Cliente deve estar ativo para realizar aluguel",
        },
    },
};

/**
 * Enums para status e tipos de aluguel
 */
export const AluguelEnums = {
    // Status do aluguel
    STATUS: {
        ATIVO: "ativo",
        DEVOLVIDO: "devolvido",
        ATRASADO: "atrasado",
        CANCELADO: "cancelado",
    },

    // Tipos de cobrança
    TIPO_COBRANCA: {
        DIARIA: "diaria",
        SEMANAL: "semanal",
        MENSAL: "mensal",
        EVENTO: "evento",
    },

    // Formas de pagamento
    FORMA_PAGAMENTO: {
        DINHEIRO: "dinheiro",
        CARTAO_CREDITO: "cartao_credito",
        CARTAO_DEBITO: "cartao_debito",
        PIX: "pix",
        TRANSFERENCIA: "transferencia",
        CHEQUE: "cheque",
    },

    // Tipos de desconto
    TIPO_DESCONTO: {
        PERCENTUAL: "percentual",
        VALOR_FIXO: "valor_fixo",
    },
};

/**
 * Mapeamento de labels para exibição
 */
export const AluguelLabels = {
    STATUS: {
        [AluguelEnums.STATUS.ATIVO]: "Ativo",
        [AluguelEnums.STATUS.DEVOLVIDO]: "Devolvido",
        [AluguelEnums.STATUS.ATRASADO]: "Atrasado",
        [AluguelEnums.STATUS.CANCELADO]: "Cancelado",
    },

    TIPO_COBRANCA: {
        [AluguelEnums.TIPO_COBRANCA.DIARIA]: "Diária",
        [AluguelEnums.TIPO_COBRANCA.SEMANAL]: "Semanal",
        [AluguelEnums.TIPO_COBRANCA.MENSAL]: "Mensal",
        [AluguelEnums.TIPO_COBRANCA.EVENTO]: "Evento",
    },

    FORMA_PAGAMENTO: {
        [AluguelEnums.FORMA_PAGAMENTO.DINHEIRO]: "Dinheiro",
        [AluguelEnums.FORMA_PAGAMENTO.CARTAO_CREDITO]: "Cartão de Crédito",
        [AluguelEnums.FORMA_PAGAMENTO.CARTAO_DEBITO]: "Cartão de Débito",
        [AluguelEnums.FORMA_PAGAMENTO.PIX]: "PIX",
        [AluguelEnums.FORMA_PAGAMENTO.TRANSFERENCIA]: "Transferência",
        [AluguelEnums.FORMA_PAGAMENTO.CHEQUE]: "Cheque",
    },

    TIPO_DESCONTO: {
        [AluguelEnums.TIPO_DESCONTO.PERCENTUAL]: "Percentual (%)",
        [AluguelEnums.TIPO_DESCONTO.VALOR_FIXO]: "Valor Fixo (R$)",
    },
};

/**
 * Configurações de cálculo automático
 */
export const AluguelCalculoConfig = {
    // Multa por atraso (percentual por dia)
    multaAtraso: {
        percentualDiario: 0.02, // 2% ao dia
        valorMaximo: 1000.0, // Valor máximo da multa
        diasCarencia: 1, // Dias de carência antes da multa
    },

    // Desconto por antecipação
    descontoAntecipacao: {
        percentualMaximo: 0.1, // 10% máximo
        diasMinimos: 7, // Mínimo de dias de antecedência
    },

    // Configurações de caução
    caucao: {
        percentualMinimo: 0.5, // 50% do valor do aluguel
        percentualMaximo: 2.0, // 200% do valor do aluguel
        valorMinimo: 50.0, // Valor mínimo da caução
    },
};

/**
 * Sugestões baseadas em contexto
 */
export const AluguelSugestoes = {
    // Sugestões de período por tipo de cobrança
    periodosPorTipo: {
        [AluguelEnums.TIPO_COBRANCA.DIARIA]: [1, 2, 3, 5, 7],
        [AluguelEnums.TIPO_COBRANCA.SEMANAL]: [1, 2, 3, 4],
        [AluguelEnums.TIPO_COBRANCA.MENSAL]: [1, 2, 3, 6],
        [AluguelEnums.TIPO_COBRANCA.EVENTO]: [1, 2, 3],
    },

    // Sugestões de desconto por cliente
    descontosPorCliente: {
        novo: 0.0,
        regular: 0.05,
        vip: 0.1,
        corporativo: 0.15,
    },

    // Sugestões de observações comuns
    observacoesComuns: [
        "Produto em perfeito estado",
        "Cliente orientado sobre cuidados",
        "Entrega programada",
        "Retirada na loja",
        "Produto com pequenos desgastes naturais",
        "Cliente preferencial",
        "Desconto aplicado conforme política",
    ],
};

/**
 * Endpoints específicos do módulo de aluguel
 */
export const AluguelEndpoints = {
    // CRUD básico
    CADASTRAR: apiConfig.endpoints.ALUGUEL_CADASTRAR,
    CONSULTAR: apiConfig.endpoints.ALUGUEL_CONSULTAR,
    ATUALIZAR: apiConfig.endpoints.ALUGUEL_ATUALIZAR,
    REMOVER: apiConfig.endpoints.ALUGUEL_REMOVER,
    LISTAR: apiConfig.endpoints.ALUGUEL_LISTAR,

    // Operações específicas
    DEVOLVER: "/api/alugueis/devolver",
    CANCELAR: "/api/alugueis/cancelar",
    CALCULAR_MULTA: "/api/alugueis/calcular-multa",
    CALCULAR_DESCONTO: "/api/alugueis/calcular-desconto",
    VERIFICAR_DISPONIBILIDADE: "/api/alugueis/verificar-disponibilidade",
    RELATORIO_VENCIMENTOS: "/api/alugueis/relatorio-vencimentos",
    RELATORIO_ATRASOS: "/api/alugueis/relatorio-atrasos",
    HISTORICO_CLIENTE: "/api/alugueis/historico-cliente",
    HISTORICO_PRODUTO: "/api/alugueis/historico-produto",
};

/**
 * Configurações de interface
 */
export const AluguelUIConfig = {
    // Cores por status
    cores: {
        [AluguelEnums.STATUS.ATIVO]: "#10B981", // Verde
        [AluguelEnums.STATUS.DEVOLVIDO]: "#6B7280", // Cinza
        [AluguelEnums.STATUS.ATRASADO]: "#EF4444", // Vermelho
        [AluguelEnums.STATUS.CANCELADO]: "#F59E0B", // Amarelo
    },

    // Ícones por status
    icones: {
        [AluguelEnums.STATUS.ATIVO]: "⏳",
        [AluguelEnums.STATUS.DEVOLVIDO]: "✅",
        [AluguelEnums.STATUS.ATRASADO]: "⚠️",
        [AluguelEnums.STATUS.CANCELADO]: "❌",
    },

    // Máscaras de input
    mascaras: {
        valor: "R$ 0,00",
        data: "00/00/0000",
        telefone: "(00) 00000-0000",
        cpf: "000.000.000-00",
    },

    // Formatação de exibição
    formatacao: {
        moeda: "pt-BR",
        data: "dd/MM/yyyy",
        hora: "HH:mm",
    },
};

/**
 * Configuração completa do módulo de aluguel
 */
export const AluguelConfig = {
    validation: AluguelValidationConfig,
    enums: AluguelEnums,
    labels: AluguelLabels,
    calculo: AluguelCalculoConfig,
    sugestoes: AluguelSugestoes,
    endpoints: AluguelEndpoints,
    ui: AluguelUIConfig,
};

export default AluguelConfig;
