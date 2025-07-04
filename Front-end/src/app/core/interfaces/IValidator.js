/**
 * Interface para validação - Single Responsibility Principle
 * Define contrato para validadores específicos
 */
class IValidator {
    /**
     * Valida um valor
     * @param {any} value - Valor a ser validado
     * @returns {Object} Resultado da validação {isValid: boolean, message?: string}
     */
    validate(value) {
        throw new Error("Método validate deve ser implementado");
    }
}

export default IValidator;
