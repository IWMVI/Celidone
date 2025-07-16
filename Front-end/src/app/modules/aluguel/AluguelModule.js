/**
 * Módulo principal para funcionalidades de Aluguel
 * Responsável por inicializar os componentes adequados com base na página atual
 */
import AluguelCadastroComponent from './components/AluguelCadastroComponent.js';
import AluguelListagemComponent from './components/AluguelListagemComponent.js';

class AluguelModule {
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
            
            if (currentPath.includes('aluguel-consultar.html')) {
                console.log('Inicializando componente de listagem de aluguéis');
                this.currentComponent = new AluguelListagemComponent();
            } else {
                console.log('Inicializando componente de cadastro de aluguéis');
                this.currentComponent = new AluguelCadastroComponent();
            }
            
            // Inicializa o componente selecionado
            await this.currentComponent.init();
            
        } catch (error) {
            console.error('Erro ao inicializar módulo de aluguel:', error);
        }
    }
}

// Inicializa o módulo quando o DOM estiver pronto
document.addEventListener('DOMContentLoaded', () => {
    const aluguelModule = new AluguelModule();
    aluguelModule.init();
    
    // Torna disponível globalmente para debug
    window.aluguelModule = aluguelModule;
});

export default AluguelModule;