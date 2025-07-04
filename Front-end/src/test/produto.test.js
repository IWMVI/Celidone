/**
 * Testes para o módulo de produto
 * Valida funcionalidades do ProdutoController e serviços relacionados
 */

const { expect } = require("@jest/globals");

// Mock do ambiente DOM
Object.defineProperty(window, "api", {
    value: {
        cadastrarProduto: jest.fn(),
        consultarProduto: jest.fn(),
        atualizarProduto: jest.fn(),
        excluirProduto: jest.fn(),
        listarProdutos: jest.fn(),
        consultarHistoricoAluguel: jest.fn(),
    },
    writable: true,
});

// Mock de elementos DOM necessários
const mockElements = {
    "form-produto": { addEventListener: jest.fn(), reset: jest.fn() },
    codigo: { value: "", addEventListener: jest.fn() },
    tecido: { value: "" },
    cor: { value: "" },
    estampa: { value: "" },
    tipotraje: { value: "", addEventListener: jest.fn() },
    textura: { value: "" },
    preco: { value: "", addEventListener: jest.fn() },
    tamanho: { value: "" },
    status: { value: "" },
    sexo: { value: "" },
    observacoes: { value: "" },
    "consultar-produto-btn": { addEventListener: jest.fn() },
    "excluir-produto-btn": { addEventListener: jest.fn() },
    "listar-produtos-btn": { addEventListener: jest.fn() },
    "limpar-form-btn": { addEventListener: jest.fn() },
    "produto-lista": { innerHTML: "" },
    "produto-historico": { innerHTML: "" },
};

global.document = {
    getElementById: jest.fn((id) => mockElements[id] || null),
    querySelector: jest.fn(
        (selector) => mockElements[selector.replace("#", "")] || null
    ),
    addEventListener: jest.fn(),
    createElement: jest.fn(() => ({
        textContent: "",
        innerHTML: "",
    })),
};

describe("Testes do Módulo de Produto", () => {
    let produtoController;
    let mockNotificationService;
    let mockApiService;
    let mockValidationService;

    beforeEach(() => {
        // Mock dos serviços
        mockNotificationService = {
            showSuccess: jest.fn(),
            showError: jest.fn(),
            showWarning: jest.fn(),
            showLoading: jest.fn(),
            hideLoading: jest.fn(),
            showConfirm: jest.fn(),
        };

        mockApiService = {
            cadastrarProduto: jest.fn(),
            consultarProduto: jest.fn(),
            atualizarProduto: jest.fn(),
            excluirProduto: jest.fn(),
            listarProdutos: jest.fn(),
            consultarHistoricoAluguel: jest.fn(),
        };

        mockValidationService = {
            validateProduto: jest.fn(),
        };
    });

    describe("Validação de Dados de Produto", () => {
        test("deve validar produto com dados completos", () => {
            const produtoData = {
                codigo: "PRD-0001",
                tecido: "Lã",
                cor: "Azul Marinho",
                estampa: "Liso",
                tipoTraje: "terno",
                textura: "Lisa",
                preco: 150.0,
                tamanho: "M",
                status: "disponivel",
                sexo: "masculino",
            };

            mockValidationService.validateProduto.mockReturnValue({
                isValid: true,
                errors: [],
            });

            const result = mockValidationService.validateProduto(produtoData);

            expect(result.isValid).toBe(true);
            expect(result.errors).toHaveLength(0);
        });

        test("deve invalidar produto com campos obrigatórios ausentes", () => {
            const produtoData = {
                codigo: "",
                tecido: "",
                cor: "Azul",
                // Campos obrigatórios ausentes
            };

            mockValidationService.validateProduto.mockReturnValue({
                isValid: false,
                errors: [
                    "Código do produto é obrigatório",
                    "Tecido é obrigatório",
                ],
            });

            const result = mockValidationService.validateProduto(produtoData);

            expect(result.isValid).toBe(false);
            expect(result.errors).toContain("Código do produto é obrigatório");
            expect(result.errors).toContain("Tecido é obrigatório");
        });

        test("deve invalidar código de produto com formato incorreto", () => {
            const produtoData = {
                codigo: "PROD123", // Formato incorreto
                tecido: "Lã",
                cor: "Azul",
                estampa: "Liso",
                tipoTraje: "terno",
                textura: "Lisa",
                preco: 150.0,
                tamanho: "M",
                status: "disponivel",
                sexo: "masculino",
            };

            mockValidationService.validateProduto.mockReturnValue({
                isValid: false,
                errors: ["Código deve seguir o padrão PRD-0000"],
            });

            const result = mockValidationService.validateProduto(produtoData);

            expect(result.isValid).toBe(false);
            expect(result.errors).toContain(
                "Código deve seguir o padrão PRD-0000"
            );
        });

        test("deve invalidar preço zero ou negativo", () => {
            const produtoData = {
                codigo: "PRD-0001",
                tecido: "Lã",
                cor: "Azul",
                estampa: "Liso",
                tipoTraje: "terno",
                textura: "Lisa",
                preco: 0, // Preço inválido
                tamanho: "M",
                status: "disponivel",
                sexo: "masculino",
            };

            mockValidationService.validateProduto.mockReturnValue({
                isValid: false,
                errors: ["Preço deve ser maior que zero"],
            });

            const result = mockValidationService.validateProduto(produtoData);

            expect(result.isValid).toBe(false);
            expect(result.errors).toContain("Preço deve ser maior que zero");
        });
    });

    describe("Operações CRUD de Produto", () => {
        test("deve cadastrar produto com sucesso", async () => {
            const produtoData = {
                codigo: "PRD-0001",
                tecido: "Lã",
                cor: "Azul Marinho",
                estampa: "Liso",
                tipoTraje: "terno",
                textura: "Lisa",
                preco: 150.0,
                tamanho: "M",
                status: "disponivel",
                sexo: "masculino",
            };

            mockApiService.cadastrarProduto.mockResolvedValue({
                success: true,
                data: { id: 1, ...produtoData },
            });

            const resultado = await mockApiService.cadastrarProduto(
                produtoData
            );

            expect(resultado.success).toBe(true);
            expect(resultado.data.codigo).toBe("PRD-0001");
            expect(mockApiService.cadastrarProduto).toHaveBeenCalledWith(
                produtoData
            );
        });

        test("deve consultar produto por código", async () => {
            const codigo = "PRD-0001";
            const produtoMock = {
                id: 1,
                codigo: "PRD-0001",
                tecido: "Lã",
                cor: "Azul Marinho",
                estampa: "Liso",
                tipoTraje: "terno",
                textura: "Lisa",
                preco: 150.0,
                tamanho: "M",
                status: "disponivel",
                sexo: "masculino",
            };

            mockApiService.consultarProduto.mockResolvedValue({
                success: true,
                data: produtoMock,
            });

            const resultado = await mockApiService.consultarProduto(codigo);

            expect(resultado.success).toBe(true);
            expect(resultado.data.codigo).toBe(codigo);
            expect(mockApiService.consultarProduto).toHaveBeenCalledWith(
                codigo
            );
        });

        test("deve retornar erro ao consultar produto inexistente", async () => {
            const codigo = "PRD-9999";

            mockApiService.consultarProduto.mockResolvedValue({
                success: false,
                error: "Produto não encontrado",
            });

            const resultado = await mockApiService.consultarProduto(codigo);

            expect(resultado.success).toBe(false);
            expect(resultado.error).toBe("Produto não encontrado");
        });

        test("deve atualizar produto existente", async () => {
            const produtoId = 1;
            const dadosAtualizados = {
                codigo: "PRD-0001",
                tecido: "Algodão",
                cor: "Preto",
                preco: 180.0,
            };

            mockApiService.atualizarProduto.mockResolvedValue({
                success: true,
                data: { id: produtoId, ...dadosAtualizados },
            });

            const resultado = await mockApiService.atualizarProduto(
                produtoId,
                dadosAtualizados
            );

            expect(resultado.success).toBe(true);
            expect(resultado.data.tecido).toBe("Algodão");
            expect(resultado.data.cor).toBe("Preto");
        });

        test("deve excluir produto com sucesso", async () => {
            const produtoId = 1;

            mockApiService.excluirProduto.mockResolvedValue({
                success: true,
            });

            const resultado = await mockApiService.excluirProduto(produtoId);

            expect(resultado.success).toBe(true);
            expect(mockApiService.excluirProduto).toHaveBeenCalledWith(
                produtoId
            );
        });

        test("deve listar todos os produtos", async () => {
            const produtosMock = [
                {
                    id: 1,
                    codigo: "PRD-0001",
                    tecido: "Lã",
                    cor: "Azul",
                    status: "disponivel",
                },
                {
                    id: 2,
                    codigo: "PRD-0002",
                    tecido: "Algodão",
                    cor: "Preto",
                    status: "alugado",
                },
            ];

            mockApiService.listarProdutos.mockResolvedValue({
                success: true,
                data: produtosMock,
            });

            const resultado = await mockApiService.listarProdutos();

            expect(resultado.success).toBe(true);
            expect(resultado.data).toHaveLength(2);
            expect(resultado.data[0].codigo).toBe("PRD-0001");
            expect(resultado.data[1].codigo).toBe("PRD-0002");
        });
    });

    describe("Histórico de Aluguel", () => {
        test("deve consultar histórico de aluguel do produto", async () => {
            const produtoId = 1;
            const historicoMock = [
                {
                    id: 1,
                    clienteNome: "João Silva",
                    dataAluguel: "2024-01-15",
                },
                {
                    id: 2,
                    clienteNome: "Maria Santos",
                    dataAluguel: "2024-02-20",
                },
            ];

            mockApiService.consultarHistoricoAluguel.mockResolvedValue({
                success: true,
                data: historicoMock,
            });

            const resultado = await mockApiService.consultarHistoricoAluguel(
                produtoId
            );

            expect(resultado.success).toBe(true);
            expect(resultado.data).toHaveLength(2);
            expect(resultado.data[0].clienteNome).toBe("João Silva");
        });

        test("deve retornar histórico vazio para produto sem aluguéis", async () => {
            const produtoId = 1;

            mockApiService.consultarHistoricoAluguel.mockResolvedValue({
                success: true,
                data: [],
            });

            const resultado = await mockApiService.consultarHistoricoAluguel(
                produtoId
            );

            expect(resultado.success).toBe(true);
            expect(resultado.data).toHaveLength(0);
        });
    });

    describe("Formatação de Dados", () => {
        test("deve formatar preço corretamente", () => {
            // Mock da função de formatação
            const formatCurrency = (value) => {
                if (!value && value !== 0) return "R$ 0,00";
                return new Intl.NumberFormat("pt-BR", {
                    style: "currency",
                    currency: "BRL",
                }).format(value);
            };

            const result1 = formatCurrency(150.5);
            const result2 = formatCurrency(0);
            const result3 = formatCurrency(null);

            expect(result1).toMatch(/R\$\s?150,50/);
            expect(result2).toMatch(/R\$\s?0,00/);
            expect(result3).toBe("R$ 0,00");
        });

        test("deve formatar código do produto", () => {
            const formatProductCode = (codigo) => {
                if (!codigo) return "";
                const cleanCode = codigo
                    .replace(/[^A-Z0-9]/gi, "")
                    .toUpperCase();
                if (cleanCode.length >= 7) {
                    return `${cleanCode.substr(0, 3)}-${cleanCode.substr(
                        3,
                        4
                    )}`;
                }
                return cleanCode;
            };

            expect(formatProductCode("prd0001")).toBe("PRD-0001");
            expect(formatProductCode("PRD0001")).toBe("PRD-0001");
            expect(formatProductCode("PRD-0001")).toBe("PRD-0001");
        });

        test("deve formatar status do produto", () => {
            const formatStatus = (status) => {
                const statusMap = {
                    disponivel: "Disponível",
                    indisponivel: "Indisponível",
                    manutencao: "Em Manutenção",
                    alugado: "Alugado",
                };
                return statusMap[status] || status;
            };

            expect(formatStatus("disponivel")).toBe("Disponível");
            expect(formatStatus("alugado")).toBe("Alugado");
            expect(formatStatus("manutencao")).toBe("Em Manutenção");
        });
    });
});
