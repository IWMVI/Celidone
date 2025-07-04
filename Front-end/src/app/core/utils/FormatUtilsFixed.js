/**
 * Utilitário para formatação de dados
 * Single Responsibility: Apenas formatação de dados
 */
class FormatUtils {
    /**
     * Formata data para exibição
     * @param {string|Date} date - Data a ser formatada
     * @returns {string} Data formatada
     */
    static formatDate(date) {
        if (!date) return "";

        const d = new Date(date);
        if (isNaN(d.getTime())) return "";

        return d.toLocaleDateString("pt-BR");
    }

    /**
     * Formata data e hora para exibição
     * @param {string|Date} date - Data e hora a ser formatada
     * @returns {string} Data e hora formatada
     */
    static formatDateTime(date) {
        if (!date) return "";

        const d = new Date(date);
        if (isNaN(d.getTime())) return "";

        return d.toLocaleString("pt-BR");
    }

    /**
     * Formata valor monetário
     * @param {number|string} value - Valor a ser formatado
     * @returns {string} Valor formatado
     */
    static formatCurrency(value) {
        if (value === null || value === undefined) return "R$ 0,00";

        // Se for string, converte para número
        let numericValue =
            typeof value === "string"
                ? parseFloat(value.replace(/[^\d,]/g, "").replace(",", "."))
                : value;

        if (isNaN(numericValue)) return "R$ 0,00";

        return new Intl.NumberFormat("pt-BR", {
            style: "currency",
            currency: "BRL",
        }).format(numericValue);
    }

    /**
     * Formata número para exibição
     * @param {number} value - Número a ser formatado
     * @param {number} decimals - Número de casas decimais
     * @returns {string} Número formatado
     */
    static formatNumber(value, decimals = 2) {
        if (value === null || value === undefined) return "";

        return new Intl.NumberFormat("pt-BR", {
            minimumFractionDigits: decimals,
            maximumFractionDigits: decimals,
        }).format(value);
    }

    /**
     * Capitaliza primeira letra
     * @param {string} str - String a ser capitalizada
     * @returns {string} String capitalizada
     */
    static capitalize(str) {
        if (!str) return "";

        return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
    }

    /**
     * Trunca texto se exceder limite
     * @param {string} text - Texto a ser truncado
     * @param {number} maxLength - Limite máximo de caracteres
     * @returns {string} Texto truncado
     */
    static truncate(text, maxLength) {
        if (!text || text.length <= maxLength) return text;

        return text.substr(0, maxLength) + "...";
    }

    /**
     * Remove acentos de uma string
     * @param {string} str - String com acentos
     * @returns {string} String sem acentos
     */
    static removeAccents(str) {
        if (!str) return "";

        return str.normalize("NFD").replace(/[\u0300-\u036f]/g, "");
    }

    /**
     * Gera slug a partir de string
     * @param {string} str - String para gerar slug
     * @returns {string} Slug gerado
     */
    static slugify(str) {
        if (!str) return "";

        return this.removeAccents(str)
            .toLowerCase()
            .replace(/[^a-z0-9 -]/g, "")
            .replace(/\s+/g, "-")
            .replace(/-+/g, "-")
            .trim();
    }

    /**
     * Formata string para exibição segura (evita XSS)
     * @param {string} str - String a ser formatada
     * @returns {string} String segura
     */
    static sanitizeHtml(str) {
        if (!str) return "";

        const div = document.createElement("div");
        div.textContent = str;
        return div.innerHTML;
    }

    /**
     * Converte preço formatado em número
     * @param {string} currencyString - String de preço formatada
     * @returns {number} Valor numérico
     */
    static parseCurrency(currencyString) {
        if (!currencyString) return 0;

        // Remove símbolos e converte vírgula em ponto
        const cleanString = currencyString
            .replace(/[^\d,]/g, "")
            .replace(",", ".");

        return parseFloat(cleanString) || 0;
    }

    /**
     * Formata código do produto
     * @param {string} codigo - Código a ser formatado
     * @returns {string} Código formatado
     */
    static formatProductCode(codigo) {
        if (!codigo) return "";

        // Remove caracteres não alfanuméricos
        const cleanCode = codigo.replace(/[^A-Z0-9]/gi, "").toUpperCase();

        // Aplica formato PRD-0000
        if (cleanCode.length >= 7) {
            return `${cleanCode.substr(0, 3)}-${cleanCode.substr(3, 4)}`;
        }

        return cleanCode;
    }

    /**
     * Formata tamanho do produto
     * @param {string} tamanho - Tamanho a ser formatado
     * @returns {string} Tamanho formatado
     */
    static formatSize(tamanho) {
        if (!tamanho) return "";

        return tamanho.toString().toUpperCase().trim();
    }

    /**
     * Formata status do produto para exibição
     * @param {string} status - Status do produto
     * @returns {string} Status formatado
     */
    static formatStatus(status) {
        const statusMap = {
            disponivel: "Disponível",
            indisponivel: "Indisponível",
            manutencao: "Em Manutenção",
            alugado: "Alugado",
        };

        return statusMap[status] || status;
    }

    /**
     * Formata tipo de traje para exibição
     * @param {string} tipoTraje - Tipo de traje
     * @returns {string} Tipo formatado
     */
    static formatTrajeType(tipoTraje) {
        const typeMap = {
            terno: "Terno",
            smoking: "Smoking",
            fraque: "Fraque",
            traje_de_gala: "Traje de Gala",
            costume: "Costume (Tailleur)",
            traje_preto: "Traje Preto (Black Tie)",
        };

        return typeMap[tipoTraje] || tipoTraje;
    }
}

export default FormatUtils;
