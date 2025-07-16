/**
 * Módulo principal para funcionalidades de Produto
 * Responsável por inicializar os componentes adequados com base na página atual
 */
import ProdutoCadastroComponent from './components/ProdutoCadastroComponent.js';
import ProdutoListagemComponent from './components/ProdutoListagemComponent.js';

class ProdutoModule {
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
            
            if (currentPath.includes('produto-consultar.html')) {
                console.log('Inicializando componente de listagem de produtos');
                this.currentComponent = new ProdutoListagemComponent();
            } else {
                console.log('Inicializando componente de cadastro de produtos');
                this.currentComponent = new ProdutoCadastroComponent();
            }
            
            // Inicializa o componente selecionado
            await this.currentComponent.init();
            
        } catch (error) {
            console.error('Erro ao inicializar módulo de produto:', error);
        }
    }
}

// Inicializa o módulo quando o DOM estiver pronto
document.addEventListener('DOMContentLoaded', () => {
    const produtoModule = new ProdutoModule();
    produtoModule.init();
    
    // Torna disponível globalmente para debug
    window.produtoModule = produtoModule;
});

export default ProdutoModule;