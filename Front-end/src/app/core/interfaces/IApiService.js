/**
 * Interface para serviços de API - Dependency Inversion Principle
 * Define contratos que devem ser implementados por serviços de API
 */
class IApiService {
    /**
     * Busca dados de CEP
     * @param {string} cep - CEP a ser buscado
     * @returns {Promise<Object>} Dados do CEP
     */
    async buscarCep(cep) {
        throw new Error("Método buscarCep deve ser implementado");
    }

    /**
     * Cadastra um novo cliente
     * @param {Object} clienteData - Dados do cliente
     * @returns {Promise<Object>} Resultado do cadastro
     */
    async cadastrarCliente(clienteData) {
        throw new Error("Método cadastrarCliente deve ser implementado");
    }

    /**
     * Lista todos os clientes
     * @returns {Promise<Object>} Lista de clientes
     */
    async listarClientes() {
        throw new Error("Método listarClientes deve ser implementado");
    }

    /**
     * Atualiza um cliente existente
     * @param {string} id - ID do cliente
     * @param {Object} clienteData - Dados do cliente
     * @returns {Promise<Object>} Resultado da atualização
     */
    async atualizarCliente(id, clienteData) {
        throw new Error("Método atualizarCliente deve ser implementado");
    }

    /**
     * Remove um cliente
     * @param {string} id - ID do cliente
     * @returns {Promise<Object>} Resultado da remoção
     */
    async removerCliente(id) {
        throw new Error("Método removerCliente deve ser implementado");
    }
}

module.exports = IApiService;
