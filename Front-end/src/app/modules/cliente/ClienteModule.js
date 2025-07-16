/**
 * Módulo principal para funcionalidades de Cliente
 * Responsável por inicializar os componentes adequados com base na página atual
 */
import ClienteCadastroComponent from './components/ClienteCadastroComponent.js';
import ClienteListagemComponent from './components/ClienteListagemComponent.js';

class ClienteModule {
    constructor() {
        this.currentComponent = null;
    }

    /**
     * Inicializa o módulo
     */
    async init() {
        try {
            // Determina qual componente inicializar com base na URL atual
            const currentPath = window.location.pathname;
            
            if (currentPath.includes('cliente-consultar.html')) {
                console.log('Inicializando componente de listagem de clientes');
                this.currentComponent = new ClienteListagemComponent();
            } else {
                console.log('Inicializando componente de cadastro de clientes');
                this.currentComponent = new ClienteCadastroComponent();
            }
            
            // Inicializa o componente selecionado
            await this.currentComponent.init();
            
        } catch (error) {
            console.error('Erro ao inicializar módulo de cliente:', error);
        }
    }
}

// Inicializa o módulo quando o DOM estiver pronto
document.addEventListener('DOMContentLoaded', () => {
    const clienteModule = new ClienteModule();
    clienteModule.init();
    
    // Torna disponível globalmente para debug
    window.clienteModule = clienteModule;
});

export default ClienteModule;