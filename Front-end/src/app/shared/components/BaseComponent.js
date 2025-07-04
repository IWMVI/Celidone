import DomService from "../../core/services/DomService.js";
import NotificationService from "../../core/services/NotificationService.js";

/**
 * Componente base para todos os componentes
 * Implementa funcionalidades comuns seguindo DRY principle
 */
class BaseComponent {
    constructor() {
        this.domService = new DomService();
        this.notificationService = new NotificationService();
        this.element = null;
        this.initialized = false;
    }

    /**
     * Inicializa o componente
     */
    init() {
        if (this.initialized) return;

        this.createElement();
        this.bindEvents();
        this.initialized = true;
    }

    /**
     * Cria o elemento do componente
     * Deve ser implementado pelas classes filhas
     */
    createElement() {
        throw new Error("Método createElement deve ser implementado");
    }

    /**
     * Vincula eventos do componente
     * Deve ser implementado pelas classes filhas
     */
    bindEvents() {
        throw new Error("Método bindEvents deve ser implementado");
    }

    /**
     * Renderiza o componente
     * @param {HTMLElement} container - Container onde renderizar
     */
    render(container) {
        if (!this.initialized) {
            this.init();
        }

        if (container && this.element) {
            container.appendChild(this.element);
        }
    }

    /**
     * Destrói o componente
     */
    destroy() {
        if (this.element && this.element.parentNode) {
            this.element.parentNode.removeChild(this.element);
        }

        this.initialized = false;
    }

    /**
     * Exibe o componente
     */
    show() {
        if (this.element) {
            this.domService.show(this.element);
        }
    }

    /**
     * Oculta o componente
     */
    hide() {
        if (this.element) {
            this.domService.hide(this.element);
        }
    }

    /**
     * Adiciona classe CSS
     * @param {string} className - Nome da classe
     */
    addClass(className) {
        if (this.element) {
            this.domService.addClass(this.element, className);
        }
    }

    /**
     * Remove classe CSS
     * @param {string} className - Nome da classe
     */
    removeClass(className) {
        if (this.element) {
            this.domService.removeClass(this.element, className);
        }
    }

    /**
     * Adiciona event listener
     * @param {string} event - Nome do evento
     * @param {Function} handler - Função handler
     */
    addEventListener(event, handler) {
        if (this.element) {
            this.domService.addEventListener(this.element, event, handler);
        }
    }

    /**
     * Emite evento customizado
     * @param {string} eventName - Nome do evento
     * @param {Object} detail - Detalhes do evento
     */
    emit(eventName, detail) {
        if (this.element) {
            const event = new CustomEvent(eventName, { detail });
            this.element.dispatchEvent(event);
        }
    }

    /**
     * Escuta evento customizado
     * @param {string} eventName - Nome do evento
     * @param {Function} handler - Função handler
     */
    on(eventName, handler) {
        if (this.element) {
            this.domService.addEventListener(this.element, eventName, handler);
        }
    }

    /**
     * Para de escutar evento customizado
     * @param {string} eventName - Nome do evento
     * @param {Function} handler - Função handler
     */
    off(eventName, handler) {
        if (this.element) {
            this.domService.removeEventListener(
                this.element,
                eventName,
                handler
            );
        }
    }
}

export default BaseComponent;
