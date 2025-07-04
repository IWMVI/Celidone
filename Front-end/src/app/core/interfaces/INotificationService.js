/**
 * Interface para notificações - Single Responsibility Principle
 * Define contrato para sistema de notificações
 */
class INotificationService {
    /**
     * Exibe notificação de sucesso
     * @param {string} message - Mensagem de sucesso
     */
    showSuccess(message) {
        throw new Error("Método showSuccess deve ser implementado");
    }

    /**
     * Exibe notificação de erro
     * @param {string} message - Mensagem de erro
     */
    showError(message) {
        throw new Error("Método showError deve ser implementado");
    }

    /**
     * Exibe notificação de informação
     * @param {string} message - Mensagem de informação
     */
    showInfo(message) {
        throw new Error("Método showInfo deve ser implementado");
    }

    /**
     * Exibe notificação de alerta
     * @param {string} message - Mensagem de alerta
     */
    showWarning(message) {
        throw new Error("Método showWarning deve ser implementado");
    }

    /**
     * Oculta todas as notificações
     */
    hideAll() {
        throw new Error("Método hideAll deve ser implementado");
    }
}

export default INotificationService;
