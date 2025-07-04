/**
 * Configurações específicas do módulo de produto
 */

export const PRODUTO_CONFIG = {
    // Padrões de validação
    VALIDATION: {
        CODIGO_PATTERN: /^PRD-\d{4}$/,
        TAMANHO_PATTERN:
            /^(PP|P|M|G|GG|XG|XXG|36|38|40|42|44|46|48|50|52|54|56|58|60)$/i,
        PRECO_MIN: 0.01,
        PRECO_MAX: 99999.99,
    },

    // Status disponíveis
    STATUS: {
        DISPONIVEL: "disponivel",
        INDISPONIVEL: "indisponivel",
        MANUTENCAO: "manutencao",
        ALUGADO: "alugado",
    },

    // Tipos de traje disponíveis
    TIPOS_TRAJE: {
        TERNO: "terno",
        SMOKING: "smoking",
        FRAQUE: "fraque",
        TRAJE_GALA: "traje_de_gala",
        COSTUME: "costume",
        TRAJE_PRETO: "traje_preto",
    },

    // Sexo do produto
    SEXO: {
        MASCULINO: "masculino",
        FEMININO: "feminino",
        UNISSEX: "unissex",
    },

    // Sugestões baseadas no tipo de traje
    TIPO_TRAJE_SUGESTOES: {
        terno: {
            tecido: ["Lã", "Algodão", "Linho", "Poliéster", "Viscose"],
            cor: ["Azul Marinho", "Cinza", "Preto", "Carvão", "Bege"],
            estampa: [
                "Liso",
                "Risca de Giz",
                "Xadrez Discreto",
                "Príncipe de Gales",
            ],
            textura: ["Lisa", "Canelada", "Micro Textura", "Espinha de Peixe"],
        },
        smoking: {
            tecido: ["Lã", "Seda", "Veludo", "Cetim"],
            cor: ["Preto", "Azul Marinho Escuro", "Branco"],
            estampa: ["Liso", "Jacquard Discreto"],
            textura: ["Lisa", "Brilhante", "Acetinada"],
        },
        fraque: {
            tecido: ["Lã", "Cashmere", "Seda"],
            cor: ["Preto", "Cinza Escuro", "Azul Marinho"],
            estampa: ["Liso", "Risca de Giz Fina"],
            textura: ["Lisa", "Canelada Fina"],
        },
        traje_de_gala: {
            tecido: ["Seda", "Veludo", "Cetim", "Lã Fina"],
            cor: ["Preto", "Azul Marinho", "Bordô", "Dourado"],
            estampa: ["Liso", "Bordado", "Jacquard"],
            textura: ["Brilhante", "Acetinada", "Aveludada"],
        },
        costume: {
            tecido: ["Lã", "Algodão", "Linho", "Seda", "Viscose"],
            cor: ["Preto", "Azul Marinho", "Cinza", "Bege", "Camel"],
            estampa: ["Liso", "Risca de Giz", "Xadrez", "Floral Discreto"],
            textura: ["Lisa", "Canelada", "Bouclé", "Tweed"],
        },
        traje_preto: {
            tecido: ["Lã", "Seda", "Veludo"],
            cor: ["Preto", "Preto Fosco", "Preto Brilhante"],
            estampa: ["Liso", "Jacquard Discreto"],
            textura: ["Lisa", "Acetinada", "Fosca"],
        },
    },

    // Tamanhos disponíveis
    TAMANHOS: {
        LETRAS: ["PP", "P", "M", "G", "GG", "XG", "XXG"],
        NUMEROS: [
            "36",
            "38",
            "40",
            "42",
            "44",
            "46",
            "48",
            "50",
            "52",
            "54",
            "56",
            "58",
            "60",
        ],
    },

    // Campos obrigatórios
    CAMPOS_OBRIGATORIOS: [
        "codigo",
        "tecido",
        "cor",
        "estampa",
        "tipotraje",
        "textura",
        "preco",
        "tamanho",
        "status",
        "sexo",
    ],

    // Mensagens de erro
    MENSAGENS_ERRO: {
        CODIGO_INVALIDO: "Código deve seguir o padrão PRD-0000",
        PRODUTO_NAO_ENCONTRADO: "Produto não encontrado",
        ERRO_SALVAR: "Erro ao salvar produto",
        ERRO_EXCLUIR: "Erro ao excluir produto",
        ERRO_CONSULTAR: "Erro ao consultar produto",
        CAMPOS_OBRIGATORIOS: "Preencha todos os campos obrigatórios",
        PRECO_INVALIDO: "Preço deve ser maior que zero",
    },

    // Configurações de API
    API: {
        ENDPOINTS: {
            PRODUTOS: "/api/produtos",
            CADASTRAR: "/api/produtos/cadastrar",
            CONSULTAR: "/api/produtos/consultar",
            ATUALIZAR: "/api/produtos/atualizar",
            EXCLUIR: "/api/produtos/excluir",
            HISTORICO: "/api/produtos/historico",
        },
    },
};
