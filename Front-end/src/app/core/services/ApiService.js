import IApiService from "../interfaces/IApiService.js";
import { API_CONFIG, CEP_CONFIG } from "../constants/apiConfig.js";
import AsyncUtils from "../utils/AsyncUtils.js";

/**
 * Implementação do serviço de API
 * Implementa IApiService seguindo o Dependency Inversion Principle
 */
class ApiService extends IApiService {
    constructor() {
        super();
        this.baseURL = API_CONFIG.BASE_URL;
        this.timeout = API_CONFIG.TIMEOUT;
        this.headers = API_CONFIG.HEADERS;
    }

    /**
     * Faz requisição HTTP genérica
     * @param {string} url - URL da requisição
     * @param {Object} options - Opções da requisição
     * @returns {Promise<Object>} Resposta da requisição
     */
    async request(url, options = {}) {
        const requestOptions = {
            method: options.method || "GET",
            headers: { ...this.headers, ...options.headers },
            ...options,
        };

        if (options.body && typeof options.body === "object") {
            requestOptions.body = JSON.stringify(options.body);
        }

        const fullUrl = url.startsWith("http") ? url : `${this.baseURL}${url}`;

        try {
            const response = await AsyncUtils.withTimeout(
                () => fetch(fullUrl, requestOptions),
                this.timeout
            );

            if (!response.ok) {
                throw new Error(
                    `HTTP ${response.status}: ${response.statusText}`
                );
            }

            const contentType = response.headers.get("content-type");
            if (contentType && contentType.includes("application/json")) {
                return await response.json();
            } else {
                return await response.text();
            }
        } catch (error) {
            console.error("Erro na requisição:", error);
            throw this.handleError(error);
        }
    }

    /**
     * Trata erros de requisição
     * @param {Error} error - Erro a ser tratado
     * @returns {Error} Erro tratado
     */
    handleError(error) {
        if (error.name === "AbortError" || error.message.includes("timeout")) {
            return new Error("Tempo limite da requisição excedido");
        }

        if (error.message.includes("Failed to fetch")) {
            return new Error("Erro de conexão. Verifique sua internet");
        }

        return error;
    }

    /**
     * Busca dados de CEP
     * @param {string} cep - CEP a ser buscado
     * @returns {Promise<Object>} Dados do CEP
     */
    async buscarCep(cep) {
        if (!cep || cep.length < 8) {
            throw new Error("CEP inválido");
        }

        const cepLimpo = cep.replace(/\D/g, "");
        const url = `${CEP_CONFIG.API_URL}/${cepLimpo}/json/`;

        try {
            const resultado = await AsyncUtils.withRetry(
                () =>
                    this.request(url, {
                        method: "GET",
                        headers: { Accept: "application/json" },
                    }),
                CEP_CONFIG.RETRY_ATTEMPTS,
                CEP_CONFIG.RETRY_DELAY
            );

            if (resultado.erro) {
                throw new Error("CEP não encontrado");
            }

            return resultado;
        } catch (error) {
            console.error("Erro ao buscar CEP:", error);
            throw new Error("Erro ao buscar CEP: " + error.message);
        }
    }

    /**
     * Cadastra um novo cliente
     * @param {Object} clienteData - Dados do cliente
     * @returns {Promise<Object>} Resultado do cadastro
     */
    async cadastrarCliente(clienteData) {
        try {
            this.validateClienteData(clienteData);

            const dadosFormatados = this.formatClienteData(clienteData);

            const response = await this.request(
                API_CONFIG.ENDPOINTS.CLIENTE_CADASTRAR,
                {
                    method: "POST",
                    body: dadosFormatados,
                }
            );

            return {
                success: true,
                data: response,
            };
        } catch (error) {
            console.error("Erro ao cadastrar cliente:", error);
            return {
                success: false,
                error: error.message || "Erro ao cadastrar cliente",
            };
        }
    }

    /**
     * Lista todos os clientes
     * @returns {Promise<Object>} Lista de clientes
     */
    async listarClientes() {
        try {
            const response = await this.request(API_CONFIG.ENDPOINTS.CLIENTES);

            return {
                success: true,
                data: response,
            };
        } catch (error) {
            console.error("Erro ao listar clientes:", error);
            return {
                success: false,
                error: error.message || "Erro ao listar clientes",
            };
        }
    }

    /**
     * Atualiza um cliente existente
     * @param {string} id - ID do cliente
     * @param {Object} clienteData - Dados do cliente
     * @returns {Promise<Object>} Resultado da atualização
     */
    async atualizarCliente(id, clienteData) {
        try {
            this.validateClienteData(clienteData);

            const dadosFormatados = this.formatClienteData(clienteData);

            const response = await this.request(
                `${API_CONFIG.ENDPOINTS.CLIENTE_ATUALIZAR}/${id}`,
                {
                    method: "PUT",
                    body: dadosFormatados,
                }
            );

            return {
                success: true,
                data: response,
            };
        } catch (error) {
            console.error("Erro ao atualizar cliente:", error);
            return {
                success: false,
                error: error.message || "Erro ao atualizar cliente",
            };
        }
    }

    /**
     * Remove um cliente
     * @param {string} id - ID do cliente
     * @returns {Promise<Object>} Resultado da remoção
     */
    async removerCliente(id) {
        try {
            if (!id) {
                throw new Error("ID do cliente é obrigatório");
            }

            const response = await this.request(
                `${API_CONFIG.ENDPOINTS.CLIENTE_REMOVER}/${id}`,
                {
                    method: "DELETE",
                }
            );

            return {
                success: true,
                data: response,
            };
        } catch (error) {
            console.error("Erro ao remover cliente:", error);
            return {
                success: false,
                error: error.message || "Erro ao remover cliente",
            };
        }
    }

    /**
     * Valida dados do cliente
     * @param {Object} clienteData - Dados do cliente
     */
    validateClienteData(clienteData) {
        if (!clienteData.nome) {
            throw new Error("Nome é obrigatório");
        }

        if (!clienteData.email) {
            throw new Error("Email é obrigatório");
        }

        if (!clienteData.natureza) {
            throw new Error("Natureza é obrigatória");
        }

        if (clienteData.natureza === "PESSOA_JURIDICA" && !clienteData.cnpj) {
            throw new Error("CNPJ é obrigatório para Pessoa Jurídica");
        }

        if (clienteData.natureza === "PESSOA_FISICA" && !clienteData.cpf) {
            throw new Error("CPF é obrigatório para Pessoa Física");
        }
    }

    /**
     * Formata dados do cliente para envio
     * @param {Object} clienteData - Dados do cliente
     * @returns {Object} Dados formatados
     */
    formatClienteData(clienteData) {
        return {
            nome: clienteData.nome?.trim(),
            email: clienteData.email?.trim(),
            tipoPessoa: clienteData.natureza,
            dataNascimento: clienteData.dataNascimento,
            cep: clienteData.cep?.replace(/\D/g, ""),
            endereco: clienteData.endereco?.trim(),
            numero: clienteData.numero?.trim(),
            cidade: clienteData.cidade?.trim(),
            bairro: clienteData.bairro?.trim(),
            complemento: clienteData.complemento?.trim() || null,
            uf: clienteData.uf?.trim().toUpperCase(),
            celular: clienteData.celular?.replace(/\D/g, ""),
            telefoneFixo: clienteData.telefoneFixo?.replace(/\D/g, "") || null,
            cpf: clienteData.cpf?.replace(/\D/g, "") || null,
            cnpj: clienteData.cnpj?.replace(/\D/g, "") || null,
        };
    }

    /**
     * ==================== MÉTODOS DE PRODUTO ====================
     */

    /**
     * Cadastra um novo produto
     * @param {Object} produtoData - Dados do produto
     * @returns {Promise<Object>} Resultado do cadastro
     */
    async cadastrarProduto(produtoData) {
        try {
            // Usa as APIs do Electron diretamente
            return await window.api.cadastrarProduto(produtoData);
        } catch (error) {
            console.error("Erro ao cadastrar produto:", error);
            return {
                success: false,
                error: error.message || "Erro ao cadastrar produto",
            };
        }
    }

    /**
     * Consulta produto por código
     * @param {string} codigo - Código do produto
     * @returns {Promise<Object>} Dados do produto
     */
    async consultarProduto(codigo) {
        try {
            return await window.api.consultarProduto(codigo);
        } catch (error) {
            console.error("Erro ao consultar produto:", error);
            return {
                success: false,
                error: error.message || "Erro ao consultar produto",
            };
        }
    }

    /**
     * Atualiza um produto existente
     * @param {string} id - ID do produto
     * @param {Object} produtoData - Dados do produto
     * @returns {Promise<Object>} Resultado da atualização
     */
    async atualizarProduto(id, produtoData) {
        try {
            return await window.api.atualizarProduto(id, produtoData);
        } catch (error) {
            console.error("Erro ao atualizar produto:", error);
            return {
                success: false,
                error: error.message || "Erro ao atualizar produto",
            };
        }
    }

    /**
     * Exclui um produto
     * @param {string} id - ID do produto
     * @returns {Promise<Object>} Resultado da exclusão
     */
    async excluirProduto(id) {
        try {
            return await window.api.excluirProduto(id);
        } catch (error) {
            console.error("Erro ao excluir produto:", error);
            return {
                success: false,
                error: error.message || "Erro ao excluir produto",
            };
        }
    }

    /**
     * Lista todos os produtos
     * @returns {Promise<Object>} Lista de produtos
     */
    async listarProdutos() {
        try {
            return await window.api.listarProdutos();
        } catch (error) {
            console.error("Erro ao listar produtos:", error);
            return {
                success: false,
                error: error.message || "Erro ao listar produtos",
            };
        }
    }

    /**
     * ==================== MÉTODOS DE ALUGUEL ====================
     */

    /**
     * Cadastra um novo aluguel
     * @param {Object} aluguelData - Dados do aluguel
     * @returns {Promise<Object>} Resultado do cadastro
     */
    async cadastrarAluguel(aluguelData) {
        try {
            return await window.api.cadastrarAluguel(aluguelData);
        } catch (error) {
            console.error("Erro ao cadastrar aluguel:", error);
            return { success: false, error: error.message };
        }
    }

    /**
     * Lista todos os aluguéis
     * @returns {Promise<Object>} Lista de aluguéis
     */
    async listarAlugueis() {
        try {
            return await window.api.listarAlugueis();
        } catch (error) {
            console.error("Erro ao listar aluguéis:", error);
            return { success: false, error: error.message };
        }
    }

    /**
     * Atualiza um aluguel existente
     * @param {string} id - ID do aluguel
     * @param {Object} aluguelData - Dados do aluguel
     * @returns {Promise<Object>} Resultado da atualização
     */
    async atualizarAluguel(id, aluguelData) {
        try {
            return await window.api.atualizarAluguel(id, aluguelData);
        } catch (error) {
            console.error("Erro ao atualizar aluguel:", error);
            return { success: false, error: error.message };
        }
    }

    /**
     * Consulta um aluguel específico
     * @param {string} id - ID do aluguel
     * @returns {Promise<Object>} Dados do aluguel
     */
    async consultarAluguel(id) {
        try {
            return await window.api.consultarAluguel(id);
        } catch (error) {
            console.error("Erro ao consultar aluguel:", error);
            return { success: false, error: error.message };
        }
    }

    /**
     * Devolve um aluguel (marca como devolvido)
     * @param {string} id - ID do aluguel
     * @returns {Promise<Object>} Resultado da devolução
     */
    async devolverAluguel(id) {
        try {
            return await window.api.devolverAluguel(id);
        } catch (error) {
            console.error("Erro ao devolver aluguel:", error);
            return { success: false, error: error.message };
        }
    }

    /**
     * Cancela um aluguel
     * @param {string} id - ID do aluguel
     * @returns {Promise<Object>} Resultado do cancelamento
     */
    async cancelarAluguel(id) {
        try {
            return await window.api.cancelarAluguel(id);
        } catch (error) {
            console.error("Erro ao cancelar aluguel:", error);
            return { success: false, error: error.message };
        }
    }

    /**
     * Consulta histórico de aluguel de um produto
     * @param {string} produtoId - ID do produto
     * @returns {Promise<Object>} Histórico de aluguel
     */
    async consultarHistoricoAluguel(produtoId) {
        try {
            return await window.api.consultarHistoricoAluguel(produtoId);
        } catch (error) {
            console.error("Erro ao consultar histórico:", error);
            return {
                success: false,
                error: error.message || "Erro ao consultar histórico",
            };
        }
    }
}

export default ApiService;
