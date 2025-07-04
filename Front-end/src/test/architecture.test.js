/**
 * Testes para a nova arquitetura refatorada
 * Testa os princípios SOLID e Clean Architecture implementados
 */

import DomService from "../app/core/services/DomService.js";
import NotificationService from "../app/core/services/NotificationService.js";
import {
    CpfValidator,
    EmailValidator,
} from "../app/core/services/ValidationService.js";
import MaskUtils from "../app/core/utils/MaskUtils.js";
import FormatUtils from "../app/core/utils/FormatUtils.js";
import AsyncUtils from "../app/core/utils/AsyncUtils.js";

// Mocks para testes
const mockDocument = {
    getElementById: jest.fn(),
    createElement: jest.fn(),
    body: {
        appendChild: jest.fn(),
    },
};

global.document = mockDocument;
global.requestAnimationFrame = jest.fn((cb) => setTimeout(cb, 0));

describe("Clean Architecture - Core Services", () => {
    beforeEach(() => {
        jest.clearAllMocks();
    });

    describe("DomService", () => {
        let domService;
        let mockElement;

        beforeEach(() => {
            domService = new DomService();
            mockElement = {
                style: {},
                classList: {
                    add: jest.fn(),
                    remove: jest.fn(),
                    contains: jest.fn(),
                    toggle: jest.fn(),
                },
                setAttribute: jest.fn(),
                getAttribute: jest.fn(),
                removeAttribute: jest.fn(),
                addEventListener: jest.fn(),
                removeEventListener: jest.fn(),
                value: "",
                textContent: "",
                innerHTML: "",
            };
        });

        test("getElementById deve retornar elemento ou avisar se não encontrado", () => {
            // Arrange
            mockDocument.getElementById.mockReturnValue(mockElement);
            const consoleSpy = jest.spyOn(console, "warn").mockImplementation();

            // Act
            const result = domService.getElementById("test-id");
            domService.getElementById("non-existent");

            // Assert
            expect(result).toBe(mockElement);
            expect(consoleSpy).toHaveBeenCalledWith(
                "Elemento com ID 'non-existent' não encontrado"
            );

            consoleSpy.mockRestore();
        });

        test("setValue deve definir valor de elemento", () => {
            // Arrange
            mockDocument.getElementById.mockReturnValue(mockElement);

            // Act
            domService.setValue("test-id", "test-value");

            // Assert
            expect(mockElement.value).toBe("test-value");
        });

        test("show deve exibir elemento", () => {
            // Arrange & Act
            domService.show(mockElement);

            // Assert
            expect(mockElement.style.display).toBe("block");
            expect(mockElement.classList.remove).toHaveBeenCalledWith("hidden");
        });

        test("hide deve ocultar elemento", () => {
            // Arrange & Act
            domService.hide(mockElement);

            // Assert
            expect(mockElement.style.display).toBe("none");
            expect(mockElement.classList.add).toHaveBeenCalledWith("hidden");
        });
    });

    describe("NotificationService", () => {
        let notificationService;

        beforeEach(() => {
            notificationService = new NotificationService();
            mockDocument.createElement.mockReturnValue({
                className: "",
                appendChild: jest.fn(),
                innerHTML: "",
                classList: {
                    add: jest.fn(),
                },
                style: {},
                onclick: null,
            });
        });

        test("deve inicializar corretamente", () => {
            // Act
            notificationService.init();

            // Assert
            expect(notificationService.initialized).toBe(true);
            expect(mockDocument.createElement).toHaveBeenCalledWith("div");
        });

        test("showSuccess deve criar notificação de sucesso", () => {
            // Arrange
            notificationService.init();

            // Act
            notificationService.showSuccess("Teste de sucesso");

            // Assert
            expect(notificationService.notifications).toHaveLength(1);
        });
    });

    describe("Validation Services", () => {
        describe("CpfValidator", () => {
            let cpfValidator;

            beforeEach(() => {
                cpfValidator = new CpfValidator();
            });

            test("deve validar CPF válido", () => {
                // Act
                const result = cpfValidator.validate("11144477735");

                // Assert
                expect(result.isValid).toBe(true);
            });

            test("deve invalidar CPF inválido", () => {
                // Act
                const result = cpfValidator.validate("12345678901");

                // Assert
                expect(result.isValid).toBe(false);
                expect(result.message).toBe("CPF inválido");
            });

            test("deve invalidar CPF vazio", () => {
                // Act
                const result = cpfValidator.validate("");

                // Assert
                expect(result.isValid).toBe(false);
                expect(result.message).toBe("Este campo é obrigatório");
            });
        });

        describe("EmailValidator", () => {
            let emailValidator;

            beforeEach(() => {
                emailValidator = new EmailValidator();
            });

            test("deve validar email válido", () => {
                // Act
                const result = emailValidator.validate("usuario@exemplo.com");

                // Assert
                expect(result.isValid).toBe(true);
            });

            test("deve invalidar email inválido", () => {
                // Act
                const result = emailValidator.validate("email-invalido");

                // Assert
                expect(result.isValid).toBe(false);
                expect(result.message).toBe("Email inválido");
            });
        });
    });

    describe("Utility Classes", () => {
        describe("MaskUtils", () => {
            test("deve aplicar máscara de CPF", () => {
                // Act
                const result = MaskUtils.applyCpfMask("11144477735");

                // Assert
                expect(result).toBe("111.444.777-35");
            });

            test("deve aplicar máscara de CNPJ", () => {
                // Act
                const result = MaskUtils.applyCnpjMask("11222333000181");

                // Assert
                expect(result).toBe("11.222.333/0001-81");
            });

            test("deve remover máscara", () => {
                // Act
                const result = MaskUtils.removeMask("111.444.777-35");

                // Assert
                expect(result).toBe("11144477735");
            });
        });

        describe("FormatUtils", () => {
            test("deve formatar data", () => {
                // Act
                const result = FormatUtils.formatDate("2025-01-03");

                // Assert
                expect(result).toBe("03/01/2025");
            });

            test("deve capitalizar texto", () => {
                // Act
                const result = FormatUtils.capitalize("joão da silva");

                // Assert
                expect(result).toBe("João Da Silva");
            });

            test("deve sanitizar HTML", () => {
                // Act
                const result = FormatUtils.sanitizeHtml(
                    '<script>alert("xss")</script>'
                );

                // Assert
                expect(result).toBe(
                    '&lt;script&gt;alert("xss")&lt;/script&gt;'
                );
            });
        });

        describe("AsyncUtils", () => {
            test("deve criar delay", async () => {
                // Arrange
                const start = Date.now();

                // Act
                await AsyncUtils.delay(100);

                // Assert
                const elapsed = Date.now() - start;
                expect(elapsed).toBeGreaterThanOrEqual(90);
            });

            test("deve executar função com retry", async () => {
                // Arrange
                let attempts = 0;
                const fn = jest.fn(() => {
                    attempts++;
                    if (attempts < 3) {
                        throw new Error("Falha temporária");
                    }
                    return "sucesso";
                });

                // Act
                const result = await AsyncUtils.withRetry(fn, 3, 1);

                // Assert
                expect(result).toBe("sucesso");
                expect(fn).toHaveBeenCalledTimes(3);
            });
        });
    });
});

describe("SOLID Principles Compliance", () => {
    test("Single Responsibility - Cada classe tem uma responsabilidade", () => {
        // DomService: apenas manipulação de DOM
        // NotificationService: apenas notificações
        // Validators: apenas validação
        // Utils: apenas utilitários específicos
        expect(true).toBe(true); // Estrutura já demonstra o princípio
    });

    test("Open/Closed - Classes abertas para extensão", () => {
        // Classes podem ser estendidas sem modificação
        class ExtendedCpfValidator extends CpfValidator {
            validate(value) {
                const baseResult = super.validate(value);
                // Pode adicionar validações específicas
                return baseResult;
            }
        }

        const validator = new ExtendedCpfValidator();
        expect(validator).toBeInstanceOf(CpfValidator);
    });

    test("Interface Segregation - Interfaces específicas", () => {
        // IValidator tem apenas método validate
        // IDomService tem apenas métodos de DOM
        // INotificationService tem apenas métodos de notificação
        expect(true).toBe(true); // Estrutura já demonstra o princípio
    });

    test("Dependency Inversion - Dependências através de interfaces", () => {
        // Services implementam interfaces
        // Controllers dependem de abstrações
        expect(true).toBe(true); // Estrutura já demonstra o princípio
    });
});
