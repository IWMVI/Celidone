import { VALIDATION_CONFIG } from "../constants/validationConfig.js";

/**
 * Utilitário para aplicação de máscaras
 * Single Responsibility: Apenas aplicação de máscaras
 */
class MaskUtils {
    /**
     * Aplica máscara em valor
     * @param {string} value - Valor a ser mascarado
     * @param {string} mask - Máscara a ser aplicada
     * @returns {string} Valor mascarado
     */
    static applyMask(value, mask) {
        if (!value || !mask) return value;

        const cleanValue = value.replace(/\D/g, "");
        let maskedValue = "";
        let valueIndex = 0;

        for (
            let i = 0;
            i < mask.length && valueIndex < cleanValue.length;
            i++
        ) {
            if (mask[i] === "#") {
                maskedValue += cleanValue[valueIndex];
                valueIndex++;
            } else {
                maskedValue += mask[i];
            }
        }

        return maskedValue;
    }

    /**
     * Aplica máscara de CPF
     * @param {string} value - Valor do CPF
     * @returns {string} CPF mascarado
     */
    static applyCpfMask(value) {
        return this.applyMask(value, VALIDATION_CONFIG.CPF.MASK);
    }

    /**
     * Aplica máscara de CNPJ
     * @param {string} value - Valor do CNPJ
     * @returns {string} CNPJ mascarado
     */
    static applyCnpjMask(value) {
        return this.applyMask(value, VALIDATION_CONFIG.CNPJ.MASK);
    }

    /**
     * Aplica máscara de CEP
     * @param {string} value - Valor do CEP
     * @returns {string} CEP mascarado
     */
    static applyCepMask(value) {
        return this.applyMask(value, VALIDATION_CONFIG.CEP.MASK);
    }

    /**
     * Aplica máscara de telefone celular
     * @param {string} value - Valor do telefone
     * @returns {string} Telefone mascarado
     */
    static applyCelularMask(value) {
        return this.applyMask(value, VALIDATION_CONFIG.TELEFONE.CELULAR_MASK);
    }

    /**
     * Aplica máscara de telefone fixo
     * @param {string} value - Valor do telefone
     * @returns {string} Telefone mascarado
     */
    static applyFixoMask(value) {
        return this.applyMask(value, VALIDATION_CONFIG.TELEFONE.FIXO_MASK);
    }

    /**
     * Remove máscara de um valor
     * @param {string} value - Valor mascarado
     * @returns {string} Valor sem máscara
     */
    static removeMask(value) {
        if (!value) return value;
        return value.replace(/\D/g, "");
    }

    /**
     * Detecta tipo de máscara baseado no valor
     * @param {string} value - Valor a ser analisado
     * @returns {string} Tipo de máscara detectado
     */ static detectMaskType(value) {
        const cleanValue = this.removeMask(value);

        if (cleanValue.length === 14 && cleanValue.match(/^\d{14}$/)) {
            return "cnpj";
        } else if (cleanValue.length === 11 && cleanValue.match(/^\d{11}$/)) {
            return "cpf";
        } else if (cleanValue.length === 10 && cleanValue.match(/^\d{10}$/)) {
            return "fixo";
        } else if (cleanValue.length === 8 && cleanValue.match(/^\d{8}$/)) {
            return "cep";
        }

        return "unknown";
    }
}

export default MaskUtils;
