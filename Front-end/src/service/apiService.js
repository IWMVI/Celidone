const API_BASE_URL = "http://localhost:8080";

/*
    const API_CONFIG = {
        BASE_URL: API_BASE_URL,
        ENDPOINTS: {
            CADASTRAR: "/clientes", // ajuste conforme sua API
        },
        TIMEOUT: 5000,
    };
*/
class ApiService {
    static async cadastrarCliente(clienteData) {
        try {
            const naturezaMap = {
                pessoa_fisica: "PESSOA_FISICA",
                pessoa_juridica: "PESSOA_JURIDICA",
            };

            const dadosBackend = {
                nome: clienteData.nome,
                email: clienteData.email,
                tipoPessoa: naturezaMap[clienteData.natureza] || null,
                dataNascimento: clienteData.dataNascimento,
                cep: clienteData.cep,
                endereco: clienteData.endereco,
                numero: clienteData.numero,
                cidade: clienteData.cidade,
                bairro: clienteData.bairro,
                complemento: clienteData.complemento,
                uf: clienteData.uf,
                celular: clienteData.celular,
                telefoneFixo: clienteData.telefoneFixo,
                cpf:
                    clienteData.natureza === "PESSOA_FISICA"
                        ? clienteData.cpf
                        : null,
                cnpj:
                    clienteData.natureza === "PESSOA_JURIDICA"
                        ? clienteData.cnpj
                        : null,
            };

            const response = await axios.post(
                `${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.CADASTRAR}`,
                dadosBackend,
                {
                    timeout: API_CONFIG.TIMEOUT,
                    headers: {
                        "Content-Type": "application/json",
                    },
                }
            );

            return {
                success: true,
                data: response.data,
            };
        } catch (error) {
            console.error("Erro ao cadastrar cliente:", error);

            let errorMessage = "Erro ao cadastrar cliente";
            if (error.response?.data?.errors) {
                errorMessage = error.response.data.errors
                    .map((e) => e.defaultMessage)
                    .join(", ");
            } else if (error.response?.data?.message) {
                errorMessage = error.response.data.message;
            }

            return {
                success: false,
                error: errorMessage,
            };
        }
    }
}

const apiService = new ApiService();
export default apiService;

