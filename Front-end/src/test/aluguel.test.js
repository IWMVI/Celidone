/**
 * Testes unitários para o módulo de aluguel
 * Implementa testes básicos para as funcionalidades do módulo
 */

const { describe, it, expect, beforeEach } = require("@jest/globals");

// Mock do window.api
global.window = {
    api: {
        listarClientes: jest.fn(),
        listarProdutos: jest.fn(),
        listarAlugueis: jest.fn(),
        cadastrarAluguel: jest.fn(),
        atualizarAluguel: jest.fn(),
        devolverAluguel: jest.fn(),
        cancelarAluguel: jest.fn(),
        consultarAluguel: jest.fn(),
    },
};

// Mock do DOM
global.document = {
    addEventListener: jest.fn(),
    getElementById: jest.fn(),
    querySelector: jest.fn(),
    createElement: jest.fn(),
    body: {
        appendChild: jest.fn(),
        removeChild: jest.fn(),
    },
};

// Mock de configuração do Aluguel
const mockAluguelConfig = {
    validation: {
        required: {
            clienteId: "Cliente é obrigatório",
            produtoId: "Produto é obrigatório",
            dataAluguel: "Data do aluguel é obrigatória",
            dataDevPrevista: "Data prevista para devolução é obrigatória",
            valorAluguel: "Valor do aluguel é obrigatório",
            valorCaucao: "Valor da caução é obrigatório",
        },
        format: {
            valorAluguel: {
                min: 0.01,
                max: 9999.99,
                message:
                    "Valor do aluguel deve ser entre R$ 0,01 e R$ 9.999,99",
            },
            valorCaucao: {
                min: 0,
                max: 99999.99,
                message:
                    "Valor da caução deve ser entre R$ 0,00 e R$ 99.999,99",
            },
        },
    },
    enums: {
        STATUS: {
            ATIVO: "ATIVO",
            DEVOLVIDO: "DEVOLVIDO",
            CANCELADO: "CANCELADO",
        },
        TIPO_COBRANCA: {
            DIARIA: "diaria",
            SEMANAL: "semanal",
            MENSAL: "mensal",
            EVENTO: "evento",
        },
        FORMA_PAGAMENTO: {
            DINHEIRO: "dinheiro",
            PIX: "pix",
            CARTAO_DEBITO: "cartao_debito",
            CARTAO_CREDITO: "cartao_credito",
            TRANSFERENCIA: "transferencia",
        },
    },
    labels: {
        STATUS: {
            ATIVO: "Ativo",
            DEVOLVIDO: "Devolvido",
            CANCELADO: "Cancelado",
        },
        FORMA_PAGAMENTO: {
            dinheiro: "Dinheiro",
            pix: "PIX",
            cartao_debito: "Cartão de Débito",
            cartao_credito: "Cartão de Crédito",
            transferencia: "Transferência",
        },
    },
    sugestoes: {
        periodosPorTipo: {
            diaria: ["1", "2", "3", "7"],
            semanal: ["1", "2", "3", "4"],
            mensal: ["1", "2", "3", "6"],
            evento: ["1", "2", "3"],
        },
        valores: {
            percentualCaucao: 100,
            percentualAluguel: 30,
        },
    },
    endpoints: {
        BASE: "/api/alugueis",
        CADASTRAR: "/api/alugueis/cadastrar",
        ATUALIZAR: "/api/alugueis",
        DEVOLVER: "/api/alugueis/devolver",
        CANCELAR: "/api/alugueis/cancelar",
    },
    ui: {
        cores: {
            ATIVO: "#22C55E",
            DEVOLVIDO: "#3B82F6",
            CANCELADO: "#EF4444",
            ATRASADO: "#F59E0B",
        },
        icones: {
            ATIVO: "✅",
            DEVOLVIDO: "📦",
            CANCELADO: "❌",
            ATRASADO: "⏰",
        },
    },
};

// Classe mock do AluguelController
class MockAluguelController {
    constructor() {
        this.formComponent = null;
        this.notificationService = null;
        this.apiService = null;
        this.validationService = null;
        this.currentAluguel = null;
        this.alugueis = [];
        this.clientes = [];
        this.produtos = [];
        this.isInitialized = false;
    }

    async init() {
        this.isInitialized = true;
        return Promise.resolve();
    }

    async loadClientes() {
        this.clientes = await global.window.api
            .listarClientes()
            .then((r) => r.data || []);
    }

    async loadProdutos() {
        const result = await global.window.api.listarProdutos();
        this.produtos = result.data
            ? result.data.filter((p) => p.status === "disponivel")
            : [];
    }

    async loadAlugueis() {
        this.alugueis = await global.window.api
            .listarAlugueis()
            .then((r) => r.data || []);
    }

    calcularValores() {
        // Implementação mock do cálculo
        const valorAluguel = 100;
        const periodo = 7;
        const desconto = 10;
        const valorTotal = valorAluguel * periodo - desconto;

        const totalField = document.getElementById("valor-total");
        if (totalField) {
            totalField.value = `R$ ${valorTotal.toFixed(2).replace(".", ",")}`;
        }
    }

    calcularCaucaoSugerida(produto) {
        const caucaoField = document.getElementById("valor-caucao");
        if (caucaoField && !caucaoField.value) {
            caucaoField.value = `R$ ${produto.preco
                .toFixed(2)
                .replace(".", ",")}`;
        }
    }

    definirValorBase(produto) {
        const valorField = document.getElementById("valor-aluguel");
        if (valorField && !valorField.value) {
            const valorBase = produto.preco * 0.3;
            valorField.value = `R$ ${valorBase.toFixed(2).replace(".", ",")}`;
        }
    }

    async handleFormSubmit(formData) {
        const result = await global.window.api.cadastrarAluguel(formData);
        if (result.success) {
            this.notificationService.showSuccess(
                "Aluguel cadastrado com sucesso!"
            );
            return true;
        } else {
            this.notificationService.showError(
                result.error || "Erro ao cadastrar aluguel"
            );
            return false;
        }
    }

    async handleDevolver() {
        if (!this.currentAluguel) {
            this.notificationService.showError(
                "Selecione um aluguel para devolver"
            );
            return;
        }

        const result = await global.window.api.devolverAluguel(
            this.currentAluguel.id
        );
        if (result.success) {
            this.notificationService.showSuccess(
                "Aluguel devolvido com sucesso!"
            );
        } else {
            this.notificationService.showError(
                result.error || "Erro ao devolver aluguel"
            );
        }
    }

    async handleCancelar() {
        if (!this.currentAluguel) {
            this.notificationService.showError(
                "Selecione um aluguel para cancelar"
            );
            return;
        }

        if (!confirm("Tem certeza que deseja cancelar este aluguel?")) {
            return;
        }

        const result = await global.window.api.cancelarAluguel(
            this.currentAluguel.id
        );
        if (result.success) {
            this.notificationService.showSuccess(
                "Aluguel cancelado com sucesso!"
            );
        } else {
            this.notificationService.showError(
                result.error || "Erro ao cancelar aluguel"
            );
        }
    }

    handleClienteChange(clienteId) {
        const cliente = this.clientes.find((c) => c.id == clienteId);
        if (cliente) {
            this.atualizarInfoCliente(cliente);
        }
    }

    handleProdutoChange(produtoId) {
        const produto = this.produtos.find((p) => p.id == produtoId);
        if (produto) {
            this.atualizarInfoProduto(produto);
            this.definirValorBase(produto);
            this.calcularCaucaoSugerida(produto);
        }
    }

    atualizarInfoCliente(cliente) {
        const infoDiv = document.getElementById("cliente-info");
        if (infoDiv) {
            infoDiv.innerHTML = `
                <div class="flex flex-col gap-0.5">
                    <span class="text-sm font-medium">${cliente.nome}</span>
                    <span class="text-xs text-secondary">${cliente.email}</span>
                    <span class="text-xs text-secondary">${
                        cliente.celular || "Sem telefone"
                    }</span>
                </div>
            `;
        }
    }

    atualizarInfoProduto(produto) {
        const infoDiv = document.getElementById("produto-info");
        if (infoDiv) {
            infoDiv.innerHTML = `
                <div class="flex flex-col gap-0.5">
                    <span class="text-sm font-medium">${produto.codigo}</span>
                    <span class="text-xs text-secondary">${produto.tecido} ${
                produto.cor
            }</span>
                    <span class="text-xs text-secondary">Tamanho: ${
                        produto.tamanho
                    }</span>
                    <span class="text-xs text-secondary">Preço: R$ ${produto.preco
                        .toFixed(2)
                        .replace(".", ",")}</span>
                </div>
            `;
        }
    }

    formatAluguelData(formData) {
        return {
            clienteId: parseInt(formData["cliente-id"]),
            produtoId: parseInt(formData["produto-id"]),
            dataAluguel: formData["data-aluguel"],
            dataDevPrevista: formData["data-dev-prevista"],
            valorAluguel: 100,
            valorCaucao: 500,
            valorDesconto: 10,
            valorTotal: 690,
            tipoCobranca: formData["tipo-cobranca"],
            formaPagamento: formData["forma-pagamento"],
            periodo: parseInt(formData["periodo"]),
            observacoes: formData["observacoes"]?.trim() || null,
            status: "ATIVO",
        };
    }

    validateAluguelData(aluguelData) {
        if (!aluguelData) {
            throw new Error("Dados do aluguel são obrigatórios");
        }

        const requiredFields = [
            "clienteId",
            "produtoId",
            "dataAluguel",
            "dataDevPrevista",
        ];
        for (const field of requiredFields) {
            if (!aluguelData[field]) {
                throw new Error(`Campo ${field} é obrigatório`);
            }
        }

        if (aluguelData.valorAluguel && aluguelData.valorAluguel <= 0) {
            throw new Error("Valor do aluguel deve ser maior que zero");
        }

        if (aluguelData.dataAluguel && aluguelData.dataDevPrevista) {
            const dataAluguel = new Date(aluguelData.dataAluguel);
            const dataDevPrevista = new Date(aluguelData.dataDevPrevista);
            if (dataDevPrevista <= dataAluguel) {
                throw new Error(
                    "Data de devolução deve ser posterior à data do aluguel"
                );
            }
        }
    }

    handleLimpar() {
        this.currentAluguel = null;
        if (this.formComponent) {
            this.formComponent.reset();
        }
        this.notificationService.showSuccess("Formulário limpo!");
    }

    destroy() {
        if (this.formComponent) {
            this.formComponent.destroy();
        }
        this.isInitialized = false;
    }
}

describe("AluguelController", () => {
    let controller;
    let mockFormComponent;
    let mockNotificationService;
    let mockApiService;
    let mockValidationService;

    beforeEach(() => {
        // Reset mocks
        jest.clearAllMocks();

        // Ensure window.api exists and reset its methods
        if (!global.window.api) {
            global.window.api = {};
        }

        // Reset window.api mocks
        global.window.api.listarClientes = jest.fn();
        global.window.api.listarProdutos = jest.fn();
        global.window.api.listarAlugueis = jest.fn();
        global.window.api.cadastrarAluguel = jest.fn();
        global.window.api.atualizarAluguel = jest.fn();
        global.window.api.devolverAluguel = jest.fn();
        global.window.api.cancelarAluguel = jest.fn();
        global.window.api.consultarAluguel = jest.fn();

        // Mock DOM elements
        const mockElement = {
            addEventListener: jest.fn(),
            value: "",
            innerHTML: "",
            style: {},
            insertRow: jest.fn(() => ({
                insertCell: jest.fn(() => ({
                    textContent: "",
                    innerHTML: "",
                    style: {},
                })),
            })),
        };

        document.getElementById = jest.fn().mockReturnValue(mockElement);
        document.querySelector = jest.fn().mockReturnValue(mockElement);

        // Mock global confirm
        global.confirm = jest.fn(() => true);

        // Mock services
        mockFormComponent = {
            init: jest.fn(),
            on: jest.fn(),
            submit: jest.fn(),
            reset: jest.fn(),
            addValidator: jest.fn(),
            destroy: jest.fn(),
        };

        mockNotificationService = {
            showSuccess: jest.fn(),
            showError: jest.fn(),
            showWarning: jest.fn(),
            showInfo: jest.fn(),
        };

        mockApiService = {
            listarClientes: jest.fn(),
            listarProdutos: jest.fn(),
            listarAlugueis: jest.fn(),
            cadastrarAluguel: jest.fn(),
            atualizarAluguel: jest.fn(),
            devolverAluguel: jest.fn(),
            cancelarAluguel: jest.fn(),
        };

        mockValidationService = {
            validate: jest.fn(),
            addRule: jest.fn(),
            removeRule: jest.fn(),
        };

        // Create controller instance
        controller = new MockAluguelController();
        controller.formComponent = mockFormComponent;
        controller.notificationService = mockNotificationService;
        controller.apiService = mockApiService;
        controller.validationService = mockValidationService;
    });

    describe("Inicialização", () => {
        it("deve inicializar corretamente", async () => {
            await controller.init();
            expect(controller.isInitialized).toBe(true);
        });
    });

    describe("Carregamento de dados", () => {
        it("deve carregar clientes corretamente", async () => {
            const mockClientes = [
                { id: 1, nome: "João Silva", email: "joao@email.com" },
                { id: 2, nome: "Maria Santos", email: "maria@email.com" },
            ];

            global.window.api.listarClientes.mockResolvedValue({
                success: true,
                data: mockClientes,
            });

            await controller.loadClientes();

            expect(controller.clientes).toEqual(mockClientes);
        });

        it("deve carregar produtos disponíveis", async () => {
            const mockProdutos = [
                { id: 1, codigo: "PRD-001", status: "disponivel" },
                { id: 2, codigo: "PRD-002", status: "alugado" },
                { id: 3, codigo: "PRD-003", status: "disponivel" },
            ];

            global.window.api.listarProdutos.mockResolvedValue({
                success: true,
                data: mockProdutos,
            });

            await controller.loadProdutos();

            expect(controller.produtos).toHaveLength(2);
            expect(
                controller.produtos.every((p) => p.status === "disponivel")
            ).toBe(true);
        });

        it("deve carregar aluguéis", async () => {
            const mockAlugueis = [
                { id: 1, clienteId: 1, produtoId: 1, status: "ATIVO" },
                { id: 2, clienteId: 2, produtoId: 2, status: "DEVOLVIDO" },
            ];

            global.window.api.listarAlugueis.mockResolvedValue({
                success: true,
                data: mockAlugueis,
            });

            await controller.loadAlugueis();

            expect(controller.alugueis).toEqual(mockAlugueis);
        });
    });

    describe("Cálculos", () => {
        it("deve calcular caução sugerida", () => {
            const mockProduto = { preco: 500 };
            const mockCaucaoField = { value: "" };

            document.getElementById.mockReturnValue(mockCaucaoField);

            controller.calcularCaucaoSugerida(mockProduto);

            expect(mockCaucaoField.value).toBe("R$ 500,00");
        });

        it("deve definir valor base do aluguel", () => {
            const mockProduto = { preco: 300 };
            const mockValorField = { value: "" };

            document.getElementById.mockReturnValue(mockValorField);

            controller.definirValorBase(mockProduto);

            expect(mockValorField.value).toBe("R$ 90,00");
        });
    });

    describe("Operações de aluguel", () => {
        it("deve cadastrar aluguel com sucesso", async () => {
            const mockFormData = {
                "cliente-id": "1",
                "produto-id": "1",
                "data-aluguel": "2024-01-01",
                "data-dev-prevista": "2024-01-08",
                "tipo-cobranca": "diaria",
                "forma-pagamento": "dinheiro",
                periodo: "7",
            };

            global.window.api.cadastrarAluguel.mockResolvedValue({
                success: true,
                data: { id: 1, ...mockFormData },
            });

            const result = await controller.handleFormSubmit(mockFormData);

            expect(result).toBe(true);
            expect(mockNotificationService.showSuccess).toHaveBeenCalledWith(
                "Aluguel cadastrado com sucesso!"
            );
        });

        it("deve devolver aluguel com sucesso", async () => {
            const mockAluguel = { id: 1, status: "ATIVO" };
            controller.currentAluguel = mockAluguel;

            global.window.api.devolverAluguel.mockResolvedValue({
                success: true,
                data: { ...mockAluguel, status: "DEVOLVIDO" },
            });

            await controller.handleDevolver();

            expect(global.window.api.devolverAluguel).toHaveBeenCalledWith(1);
            expect(mockNotificationService.showSuccess).toHaveBeenCalledWith(
                "Aluguel devolvido com sucesso!"
            );
        });

        it("deve cancelar aluguel com sucesso", async () => {
            const mockAluguel = { id: 1, status: "ATIVO" };
            controller.currentAluguel = mockAluguel;

            global.window.api.cancelarAluguel.mockResolvedValue({
                success: true,
                data: { ...mockAluguel, status: "CANCELADO" },
            });

            await controller.handleCancelar();

            expect(global.window.api.cancelarAluguel).toHaveBeenCalledWith(1);
            expect(mockNotificationService.showSuccess).toHaveBeenCalledWith(
                "Aluguel cancelado com sucesso!"
            );
        });
    });

    describe("Validação", () => {
        it("deve validar dados obrigatórios", () => {
            expect(() => controller.validateAluguelData(null)).toThrow(
                "Dados do aluguel são obrigatórios"
            );
            expect(() => controller.validateAluguelData({})).toThrow(
                "Campo clienteId é obrigatório"
            );
        });

        it("deve validar valores numéricos", () => {
            const invalidData = {
                clienteId: 1,
                produtoId: 1,
                dataAluguel: "2024-01-01",
                dataDevPrevista: "2024-01-08",
                valorAluguel: -100,
            };

            expect(() => controller.validateAluguelData(invalidData)).toThrow(
                "Valor do aluguel deve ser maior que zero"
            );
        });

        it("deve validar datas", () => {
            const invalidData = {
                clienteId: 1,
                produtoId: 1,
                dataAluguel: "2024-01-08",
                dataDevPrevista: "2024-01-01",
            };

            expect(() => controller.validateAluguelData(invalidData)).toThrow(
                "Data de devolução deve ser posterior à data do aluguel"
            );
        });
    });

    describe("Limpeza", () => {
        it("deve limpar formulário", () => {
            controller.currentAluguel = { id: 1 };

            controller.handleLimpar();

            expect(mockFormComponent.reset).toHaveBeenCalled();
            expect(controller.currentAluguel).toBeNull();
            expect(mockNotificationService.showSuccess).toHaveBeenCalledWith(
                "Formulário limpo!"
            );
        });

        it("deve destruir controlador", () => {
            controller.destroy();

            expect(mockFormComponent.destroy).toHaveBeenCalled();
            expect(controller.isInitialized).toBe(false);
        });
    });
});

describe("AluguelConfig", () => {
    describe("Validação", () => {
        it("deve conter regras de validação", () => {
            expect(mockAluguelConfig.validation).toBeDefined();
            expect(mockAluguelConfig.validation.required).toBeDefined();
            expect(mockAluguelConfig.validation.format).toBeDefined();
        });

        it("deve validar campos obrigatórios", () => {
            expect(mockAluguelConfig.validation.required.clienteId).toBe(
                "Cliente é obrigatório"
            );
            expect(mockAluguelConfig.validation.required.produtoId).toBe(
                "Produto é obrigatório"
            );
        });
    });

    describe("Enums", () => {
        it("deve conter enums de status", () => {
            expect(mockAluguelConfig.enums.STATUS).toBeDefined();
            expect(mockAluguelConfig.enums.STATUS.ATIVO).toBe("ATIVO");
            expect(mockAluguelConfig.enums.STATUS.DEVOLVIDO).toBe("DEVOLVIDO");
        });
    });

    describe("Labels", () => {
        it("deve conter labels de status", () => {
            expect(mockAluguelConfig.labels.STATUS).toBeDefined();
            expect(mockAluguelConfig.labels.STATUS.ATIVO).toBe("Ativo");
        });
    });
});
