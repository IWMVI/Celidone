import INotificationService from "../interfaces/INotificationService.js";

/**
 * Implementação do serviço de notificações
 * Implementa INotificationService seguindo o Single Responsibility Principle
 */
class NotificationService extends INotificationService {
    constructor() {
        super();
        this.notifications = [];
        this.container = null;
        this.initialized = false;
    }

    /**
     * Inicializa o serviço de notificações
     */
    init() {
        if (this.initialized) return;

        this.createContainer();
        this.initialized = true;
    }

    /**
     * Cria o container das notificações
     */
    createContainer() {
        this.container = document.createElement("div");
        this.container.id = "notification-container";
        this.container.className = "notification-container";
        document.body.appendChild(this.container);
    }

    /**
     * Exibe notificação de sucesso
     * @param {string} message - Mensagem de sucesso
     * @param {number} duration - Duração em ms (opcional)
     */
    showSuccess(message, duration = 5000) {
        this.showNotification(message, "success", duration);
    }

    /**
     * Exibe notificação de erro
     * @param {string} message - Mensagem de erro
     * @param {number} duration - Duração em ms (opcional)
     */
    showError(message, duration = 8000) {
        this.showNotification(message, "error", duration);
    }

    /**
     * Exibe notificação de informação
     * @param {string} message - Mensagem de informação
     * @param {number} duration - Duração em ms (opcional)
     */
    showInfo(message, duration = 5000) {
        this.showNotification(message, "info", duration);
    }

    /**
     * Exibe notificação de alerta
     * @param {string} message - Mensagem de alerta
     * @param {number} duration - Duração em ms (opcional)
     */
    showWarning(message, duration = 6000) {
        this.showNotification(message, "warning", duration);
    }

    /**
     * Exibe uma notificação genérica
     * @param {string} message - Mensagem
     * @param {string} type - Tipo da notificação
     * @param {number} duration - Duração em ms
     */
    showNotification(message, type, duration) {
        if (!this.initialized) {
            this.init();
        }

        const notification = this.createNotificationElement(message, type);
        this.container.appendChild(notification);
        this.notifications.push(notification);

        // Animação de entrada
        requestAnimationFrame(() => {
            notification.classList.add("show");
        });

        // Auto-remover após duração especificada
        if (duration > 0) {
            setTimeout(() => {
                this.removeNotification(notification);
            }, duration);
        }
    }

    /**
     * Cria elemento de notificação
     * @param {string} message - Mensagem
     * @param {string} type - Tipo da notificação
     * @returns {HTMLElement} Elemento da notificação
     */
    createNotificationElement(message, type) {
        const notification = document.createElement("div");
        notification.className = `notification notification-${type}`;

        const icon = this.getIcon(type);
        const closeButton = document.createElement("button");
        closeButton.className = "notification-close";
        closeButton.innerHTML = "×";
        closeButton.onclick = () => this.removeNotification(notification);

        notification.innerHTML = `
            <div class="notification-icon">${icon}</div>
            <div class="notification-content">
                <div class="notification-message">${message}</div>
            </div>
        `;

        notification.appendChild(closeButton);
        return notification;
    }

    /**
     * Obtém ícone para tipo de notificação
     * @param {string} type - Tipo da notificação
     * @returns {string} HTML do ícone
     */
    getIcon(type) {
        const icons = {
            success: "✓",
            error: "✕",
            warning: "⚠",
            info: "ℹ",
        };

        return icons[type] || icons.info;
    }

    /**
     * Remove uma notificação
     * @param {HTMLElement} notification - Elemento da notificação
     */
    removeNotification(notification) {
        if (!notification || !notification.parentNode) return;

        notification.classList.add("hide");

        setTimeout(() => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }

            const index = this.notifications.indexOf(notification);
            if (index > -1) {
                this.notifications.splice(index, 1);
            }
        }, 300);
    }

    /**
     * Oculta todas as notificações
     */
    hideAll() {
        this.notifications.forEach((notification) => {
            this.removeNotification(notification);
        });
    }

    /**
     * Limpa todas as notificações
     */
    clear() {
        this.hideAll();
        this.notifications = [];
    }

    /**
     * Obtém número de notificações ativas
     * @returns {number} Número de notificações
     */
    getActiveCount() {
        return this.notifications.length;
    }
}

export default NotificationService;
