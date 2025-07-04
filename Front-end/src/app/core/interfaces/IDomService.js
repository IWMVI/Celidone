/**
 * Interface para manipulação de DOM - Interface Segregation Principle
 * Define contratos específicos para diferentes tipos de manipulação do DOM
 */
class IDomService {
    /**
     * Obtém elemento por ID
     * @param {string} id - ID do elemento
     * @returns {HTMLElement} Elemento encontrado
     */
    getElementById(id) {
        throw new Error("Método getElementById deve ser implementado");
    }

    /**
     * Define valor de um elemento
     * @param {string} id - ID do elemento
     * @param {string} value - Valor a ser definido
     */
    setValue(id, value) {
        throw new Error("Método setValue deve ser implementado");
    }

    /**
     * Obtém valor de um elemento
     * @param {string} id - ID do elemento
     * @returns {string} Valor do elemento
     */
    getValue(id) {
        throw new Error("Método getValue deve ser implementado");
    }

    /**
     * Exibe elemento
     * @param {HTMLElement} element - Elemento a ser exibido
     */
    show(element) {
        throw new Error("Método show deve ser implementado");
    }

    /**
     * Oculta elemento
     * @param {HTMLElement} element - Elemento a ser ocultado
     */
    hide(element) {
        throw new Error("Método hide deve ser implementado");
    }

    /**
     * Define texto de um elemento
     * @param {HTMLElement} element - Elemento
     * @param {string} text - Texto a ser definido
     */
    setText(element, text) {
        throw new Error("Método setText deve ser implementado");
    }
}

export default IDomService;
