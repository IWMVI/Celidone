const IApiService = require("../interfaces/IApiService.js");
const { API_CONFIG, CEP_CONFIG } = require("../constants/apiConfig.js");
const AsyncUtils = require("../utils/AsyncUtils.js");

/**
 * Serviço de API para o processo principal do Electron
 * Implementa IApiService para uso no main process
 */
class ElectronApiService extends IApiService {
    constructor() {
        super();
        this.axios = require("axios");
        this.baseURL = API_CONFIG.BASE_URL;
        this.timeout = API_CONFIG.TIMEOUT;
        this.headers = API_CONFIG.HEADERS;
    }

    /**
     * Faz requisição HTTP
     * @param {string} url - URL da requisição
     * @param {Object} options - Opções da requisição
     * @returns {Promise<Object>} Resposta da requisição
     */
    async request(url, options = {}) {
        const config = {
            url: url.startsWith("http") ? url : `${this.baseURL}${url}`,
            method: options.method || "GET",
            headers: { ...this.headers, ...options.headers },
            timeout: this.timeout,
            ...options,
        };

        if (options.body) {
            config.data = options.body;
        }

        try {
            const response = await this.axios(config);
            return response.data;
        } catch (error) {
            console.error("Erro na requisição:", error);
            throw this.handleError(error);
        }
    }

    /**
     * Trata erros de requisição
     * @param {Error} error - Erro original
     * @returns {Error} Erro tratado
     */
    handleError(error) {
        if (error.code === "ECONNABORTED") {
            return new Error("Tempo limite da requisição excedido");
        }

        if (error.code === "ECONNREFUSED" || error.code === "ENOTFOUND") {
            return new Error(
                "Erro de conexão. Verifique se o servidor está rodando"
            );
        }

        if (error.response) {
            const message =
                error.response.data?.message ||
                error.response.data?.error ||
                `Erro HTTP ${error.response.status}`;
            return new Error(message);
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
            return { success: false, error: "CEP inválido" };
        }

        const cepLimpo = cep.replace(/\D/g, "");
        const url = `${CEP_CONFIG.API_URL}/${cepLimpo}/json/`;

        try {
            const resultado = await AsyncUtils.withRetry(
                () => this.request(url),
                CEP_CONFIG.RETRY_ATTEMPTS,
                CEP_CONFIG.RETRY_DELAY
            );

            if (resultado.erro) {
                return { success: false, error: "CEP não encontrado" };
            }

            return { success: true, data: resultado };
        } catch (error) {
            console.error("Erro ao buscar CEP:", error);
            return {
                success: false,
                error: "Erro ao buscar CEP: " + error.message,
            };
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

            return { success: true, data: response };
        } catch (error) {
            console.error("Erro ao cadastrar cliente:", error);
            return { success: false, error: error.message };
        }
    }

    /**
     * Lista todos os clientes
     * @returns {Promise<Object>} Lista de clientes
     */
    async listarClientes() {
        try {
            const response = await this.request(API_CONFIG.ENDPOINTS.CLIENTES);
            return { success: true, data: response };
        } catch (error) {
            console.error("Erro ao listar clientes:", error);
            return { success: false, error: error.message };
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

            return { success: true, data: response };
        } catch (error) {
            console.error("Erro ao atualizar cliente:", error);
            return { success: false, error: error.message };
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

            return { success: true, data: response };
        } catch (error) {
            console.error("Erro ao remover cliente:", error);
            return { success: false, error: error.message };
        }
    }

    /**
     * Valida dados do cliente
     * @param {Object} clienteData - Dados do cliente
     */
    validateClienteData(clienteData) {
        if (!clienteData.nome?.trim()) {
            throw new Error("Nome é obrigatório");
        }

        if (!clienteData.email?.trim()) {
            throw new Error("Email é obrigatório");
        }

        if (!clienteData.natureza) {
            throw new Error("Natureza é obrigatória");
        }

        if (
            clienteData.natureza === "PESSOA_JURIDICA" &&
            !clienteData.cnpj?.trim()
        ) {
            throw new Error("CNPJ é obrigatório para Pessoa Jurídica");
        }

        if (
            clienteData.natureza === "PESSOA_FISICA" &&
            !clienteData.cpf?.trim()
        ) {
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
            this.validateProdutoData(produtoData);
            const dadosFormatados = this.formatProdutoData(produtoData);

            const response = await this.request(API_CONFIG.ENDPOINTS.PRODUTOS, {
                method: "POST",
                body: dadosFormatados,
            });

            return { success: true, data: response };
        } catch (error) {
            console.error("Erro ao cadastrar produto:", error);
            return { success: false, error: error.message };
        }
    }

    /**
     * Consulta produto por código
     * @param {string} codigo - Código do produto
     * @returns {Promise<Object>} Dados do produto
     */
    async consultarProduto(codigo) {
        try {
            if (!codigo?.trim()) {
                throw new Error("Código do produto é obrigatório");
            }

            const response = await this.request(
                `${API_CONFIG.ENDPOINTS.PRODUTOS}/${codigo}`
            );
            return { success: true, data: response };
        } catch (error) {
            console.error("Erro ao consultar produto:", error);
            return { success: false, error: error.message };
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
            this.validateProdutoData(produtoData);
            const dadosFormatados = this.formatProdutoData(produtoData);

            const response = await this.request(
                `${API_CONFIG.ENDPOINTS.PRODUTOS}/${id}`,
                {
                    method: "PUT",
                    body: dadosFormatados,
                }
            );

            return { success: true, data: response };
        } catch (error) {
            console.error("Erro ao atualizar produto:", error);
            return { success: false, error: error.message };
        }
    }

    /**
     * Exclui um produto
     * @param {string} id - ID do produto
     * @returns {Promise<Object>} Resultado da exclusão
     */
    async excluirProduto(id) {
        try {
            if (!id) {
                throw new Error("ID do produto é obrigatório");
            }

            await this.request(`${API_CONFIG.ENDPOINTS.PRODUTOS}/${id}`, {
                method: "DELETE",
            });

            return { success: true };
        } catch (error) {
            console.error("Erro ao excluir produto:", error);
            return { success: false, error: error.message };
        }
    }

    /**
     * Lista todos os produtos
     * @returns {Promise<Object>} Lista de produtos
     */
    async listarProdutos() {
        try {
            const response = await this.request(API_CONFIG.ENDPOINTS.PRODUTOS);
            return { success: true, data: response };
        } catch (error) {
            console.error("Erro ao listar produtos:", error);
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
            if (!produtoId) {
                throw new Error("ID do produto é obrigatório");
            }

            const response = await this.request(
                `${API_CONFIG.ENDPOINTS.PRODUTOS}/${produtoId}/historico`
            );
            return { success: true, data: response };
        } catch (error) {
            console.error("Erro ao consultar histórico:", error);
            return { success: false, error: error.message };
        }
    }

    /**
     * Valida dados do produto
     * @param {Object} produtoData - Dados do produto
     */
    validateProdutoData(produtoData) {
        if (!produtoData) {
            throw new Error("Dados do produto são obrigatórios");
        }

        const requiredFields = [
            "codigo",
            "tecido",
            "cor",
            "estampa",
            "tipoTraje",
            "textura",
            "preco",
            "tamanho",
            "status",
            "sexo",
        ];

        for (const field of requiredFields) {
            if (
                !produtoData[field] ||
                (typeof produtoData[field] === "string" &&
                    !produtoData[field].trim())
            ) {
                throw new Error(`Campo ${field} é obrigatório`);
            }
        }

        if (produtoData.preco <= 0) {
            throw new Error("Preço deve ser maior que zero");
        }
    }

    /**
     * Formata dados do produto para envio
     * @param {Object} produtoData - Dados do produto
     * @returns {Object} Dados formatados
     */
    formatProdutoData(produtoData) {
        return {
            codigo: produtoData.codigo?.trim(),
            tecido: produtoData.tecido?.trim(),
            cor: produtoData.cor?.trim(),
            estampa: produtoData.estampa?.trim(),
            tipoTraje: produtoData.tipoTraje,
            textura: produtoData.textura?.trim(),
            preco:
                typeof produtoData.preco === "string"
                    ? parseFloat(
                          produtoData.preco
                              .replace(/[^\d,]/g, "")
                              .replace(",", ".")
                      )
                    : produtoData.preco,
            tamanho: produtoData.tamanho?.trim().toUpperCase(),
            status: produtoData.status,
            sexo: produtoData.sexo,
            observacoes: produtoData.observacoes?.trim() || null,
        };
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
            this.validateAluguelData(aluguelData);
            const formattedData = this.formatAluguelData(aluguelData);

            const response = await this.request(
                API_CONFIG.ENDPOINTS.ALUGUEL_CADASTRAR,
                {
                    method: "POST",
                    body: formattedData,
                }
            );

            return { success: true, data: response };
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
            const response = await this.request(API_CONFIG.ENDPOINTS.ALUGUEIS);
            return { success: true, data: response };
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
            if (!id) {
                throw new Error("ID do aluguel é obrigatório");
            }

            this.validateAluguelData(aluguelData);
            const formattedData = this.formatAluguelData(aluguelData);

            const response = await this.request(
                `${API_CONFIG.ENDPOINTS.ALUGUEL_ATUALIZAR}/${id}`,
                {
                    method: "PUT",
                    body: formattedData,
                }
            );

            return { success: true, data: response };
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
            if (!id) {
                throw new Error("ID do aluguel é obrigatório");
            }

            const response = await this.request(
                `${API_CONFIG.ENDPOINTS.ALUGUEIS}/${id}`
            );
            return { success: true, data: response };
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
            if (!id) {
                throw new Error("ID do aluguel é obrigatório");
            }

            const response = await this.request(
                `${API_CONFIG.ENDPOINTS.ALUGUEIS}/${id}/devolver`,
                {
                    method: "PUT",
                }
            );

            return { success: true, data: response };
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
            if (!id) {
                throw new Error("ID do aluguel é obrigatório");
            }

            const response = await this.request(
                `${API_CONFIG.ENDPOINTS.ALUGUEIS}/${id}/cancelar`,
                {
                    method: "PUT",
                }
            );

            return { success: true, data: response };
        } catch (error) {
            console.error("Erro ao cancelar aluguel:", error);
            return { success: false, error: error.message };
        }
    }

    /**
     * Valida dados do aluguel
     * @param {Object} aluguelData - Dados do aluguel
     */
    validateAluguelData(aluguelData) {
        if (!aluguelData) {
            throw new Error("Dados do aluguel são obrigatórios");
        }

        const requiredFields = [
            "clienteId",
            "produtoId",
            "dataAluguel",
            "dataDevPrevista",
            "valorAluguel",
            "valorCaucao",
            "tipoCobranca",
            "formaPagamento",
            "periodo",
        ];

        for (const field of requiredFields) {
            if (
                aluguelData[field] === undefined ||
                aluguelData[field] === null ||
                aluguelData[field] === ""
            ) {
                throw new Error(`Campo ${field} é obrigatório`);
            }
        }

        if (aluguelData.valorAluguel <= 0) {
            throw new Error("Valor do aluguel deve ser maior que zero");
        }

        if (aluguelData.valorCaucao < 0) {
            throw new Error("Valor da caução não pode ser negativo");
        }

        if (aluguelData.periodo <= 0) {
            throw new Error("Período deve ser maior que zero");
        }

        // Validar datas
        const dataAluguel = new Date(aluguelData.dataAluguel);
        const dataDevPrevista = new Date(aluguelData.dataDevPrevista);

        if (dataDevPrevista <= dataAluguel) {
            throw new Error(
                "Data de devolução deve ser posterior à data do aluguel"
            );
        }
    }

    /**
     * Formata dados do aluguel para envio
     * @param {Object} aluguelData - Dados do aluguel
     * @returns {Object} Dados formatados
     */
    formatAluguelData(aluguelData) {
        return {
            clienteId: parseInt(aluguelData.clienteId),
            produtoId: parseInt(aluguelData.produtoId),
            dataAluguel: aluguelData.dataAluguel,
            dataDevPrevista: aluguelData.dataDevPrevista,
            dataDevEfetiva: aluguelData.dataDevEfetiva || null,
            valorAluguel: parseFloat(aluguelData.valorAluguel),
            valorCaucao: parseFloat(aluguelData.valorCaucao),
            valorDesconto: parseFloat(aluguelData.valorDesconto) || 0,
            valorTotal: parseFloat(aluguelData.valorTotal),
            tipoCobranca: aluguelData.tipoCobranca,
            formaPagamento: aluguelData.formaPagamento,
            periodo: parseInt(aluguelData.periodo),
            observacoes: aluguelData.observacoes?.trim() || null,
            status: aluguelData.status || "ATIVO",
        };
    }
}

module.exports = ElectronApiService;
