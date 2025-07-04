import IDomService from "../interfaces/IDomService.js";

/**
 * Implementação do serviço de DOM
 * Implementa IDomService seguindo o Interface Segregation Principle
 */
class DomService extends IDomService {
    /**
     * Obtém elemento por ID
     * @param {string} id - ID do elemento
     * @returns {HTMLElement} Elemento encontrado
     */
    getElementById(id) {
        const element = document.getElementById(id);
        if (!element) {
            console.warn(`Elemento com ID '${id}' não encontrado`);
        }
        return element;
    }

    /**
     * Define valor de um elemento
     * @param {string} id - ID do elemento
     * @param {string} value - Valor a ser definido
     */
    setValue(id, value) {
        const element = this.getElementById(id);
        if (element) {
            element.value = value || "";
        }
    }

    /**
     * Obtém valor de um elemento
     * @param {string} id - ID do elemento
     * @returns {string} Valor do elemento
     */
    getValue(id) {
        const element = this.getElementById(id);
        return element ? element.value : "";
    }

    /**
     * Exibe elemento
     * @param {HTMLElement} element - Elemento a ser exibido
     */
    show(element) {
        if (element) {
            element.style.display = "block";
            element.classList.remove("hidden");
        }
    }

    /**
     * Oculta elemento
     * @param {HTMLElement} element - Elemento a ser ocultado
     */
    hide(element) {
        if (element) {
            element.style.display = "none";
            element.classList.add("hidden");
        }
    }

    /**
     * Define texto de um elemento
     * @param {HTMLElement} element - Elemento
     * @param {string} text - Texto a ser definido
     */
    setText(element, text) {
        if (element) {
            element.textContent = text || "";
        }
    }

    /**
     * Adiciona classe CSS a um elemento
     * @param {HTMLElement} element - Elemento
     * @param {string} className - Nome da classe
     */
    addClass(element, className) {
        if (element && className) {
            element.classList.add(className);
        }
    }

    /**
     * Remove classe CSS de um elemento
     * @param {HTMLElement} element - Elemento
     * @param {string} className - Nome da classe
     */
    removeClass(element, className) {
        if (element && className) {
            element.classList.remove(className);
        }
    }

    /**
     * Verifica se elemento possui classe CSS
     * @param {HTMLElement} element - Elemento
     * @param {string} className - Nome da classe
     * @returns {boolean} True se possui a classe
     */
    hasClass(element, className) {
        return element && className
            ? element.classList.contains(className)
            : false;
    }

    /**
     * Alterna classe CSS de um elemento
     * @param {HTMLElement} element - Elemento
     * @param {string} className - Nome da classe
     */
    toggleClass(element, className) {
        if (element && className) {
            element.classList.toggle(className);
        }
    }

    /**
     * Define atributo de um elemento
     * @param {HTMLElement} element - Elemento
     * @param {string} attribute - Nome do atributo
     * @param {string} value - Valor do atributo
     */
    setAttribute(element, attribute, value) {
        if (element && attribute) {
            element.setAttribute(attribute, value);
        }
    }

    /**
     * Obtém atributo de um elemento
     * @param {HTMLElement} element - Elemento
     * @param {string} attribute - Nome do atributo
     * @returns {string} Valor do atributo
     */
    getAttribute(element, attribute) {
        return element && attribute ? element.getAttribute(attribute) : null;
    }

    /**
     * Remove atributo de um elemento
     * @param {HTMLElement} element - Elemento
     * @param {string} attribute - Nome do atributo
     */
    removeAttribute(element, attribute) {
        if (element && attribute) {
            element.removeAttribute(attribute);
        }
    }

    /**
     * Adiciona event listener a um elemento
     * @param {HTMLElement} element - Elemento
     * @param {string} event - Nome do evento
     * @param {Function} handler - Função handler
     */
    addEventListener(element, event, handler) {
        if (element && event && handler) {
            element.addEventListener(event, handler);
        }
    }

    /**
     * Remove event listener de um elemento
     * @param {HTMLElement} element - Elemento
     * @param {string} event - Nome do evento
     * @param {Function} handler - Função handler
     */
    removeEventListener(element, event, handler) {
        if (element && event && handler) {
            element.removeEventListener(event, handler);
        }
    }

    /**
     * Limpa conteúdo de um elemento
     * @param {HTMLElement} element - Elemento
     */
    clearContent(element) {
        if (element) {
            element.innerHTML = "";
        }
    }

    /**
     * Define HTML interno de um elemento
     * @param {HTMLElement} element - Elemento
     * @param {string} html - HTML a ser inserido
     */
    setInnerHTML(element, html) {
        if (element) {
            element.innerHTML = html || "";
        }
    }
}

export default DomService;
