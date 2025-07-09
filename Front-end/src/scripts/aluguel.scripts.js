/**
 * Script principal para o módulo de aluguel
 * Conecta o AluguelController com a interface do usuário
 */

// Importa o controlador de aluguel
import AluguelController from "../src/app/modules/aluguel/AluguelController.js";

// Inicializa o controlador quando o DOM estiver carregado
document.addEventListener("DOMContentLoaded", async () => {
    try {
        // Cria instância do controlador
        const aluguelController = new AluguelController();

        // Inicializa o controlador
        await aluguelController.init();

        console.log("Módulo de aluguel inicializado com sucesso!");
    } catch (error) {
        console.error("Erro ao inicializar módulo de aluguel:", error);

        // Exibe mensagem de erro para o usuário
        const errorDiv = document.createElement("div");
        errorDiv.className = "error-message";
        errorDiv.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            background: #ff4444;
            color: white;
            padding: 15px;
            border-radius: 5px;
            z-index: 9999;
            font-family: Arial, sans-serif;
            max-width: 300px;
        `;
        errorDiv.innerHTML = `
            <strong>Erro:</strong> Não foi possível carregar o módulo de aluguel.
            <br><small>Verifique o console para mais detalhes.</small>
        `;

        document.body.appendChild(errorDiv);

        // Remove a mensagem após 5 segundos
        setTimeout(() => {
            if (errorDiv.parentNode) {
                errorDiv.parentNode.removeChild(errorDiv);
            }
        }, 5000);
    }
});

// Exporta o controlador para uso global se necessário
window.AluguelController = AluguelController;
