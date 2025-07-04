import IValidator from "../interfaces/IValidator.js";
import {
    VALIDATION_CONFIG,
    ERROR_MESSAGES,
} from "../constants/validationConfig.js";

/**
 * Validador de CPF
 * Single Responsibility: Apenas validação de CPF
 */
class CpfValidator extends IValidator {
    /**
     * Valida CPF
     * @param {string} cpf - CPF a ser validado
     * @returns {Object} Resultado da validação
     */
    validate(cpf) {
        if (!cpf) {
            return { isValid: false, message: ERROR_MESSAGES.REQUIRED };
        }

        const cpfLimpo = cpf.replace(VALIDATION_CONFIG.CPF.CLEAN_REGEX, "");

        if (cpfLimpo.length !== VALIDATION_CONFIG.CPF.LENGTH) {
            return { isValid: false, message: ERROR_MESSAGES.INVALID_CPF };
        }

        // Verifica se todos os dígitos são iguais
        if (/^(\d)\1+$/.test(cpfLimpo)) {
            return { isValid: false, message: ERROR_MESSAGES.INVALID_CPF };
        }

        // Validação do algoritmo do CPF
        if (!this.validateCpfAlgorithm(cpfLimpo)) {
            return { isValid: false, message: ERROR_MESSAGES.INVALID_CPF };
        }

        return { isValid: true };
    }

    /**
     * Valida algoritmo do CPF
     * @param {string} cpf - CPF limpo (apenas números)
     * @returns {boolean} True se válido
     */
    validateCpfAlgorithm(cpf) {
        // Primeiro dígito verificador
        let soma = 0;
        for (let i = 0; i < 9; i++) {
            soma += parseInt(cpf.charAt(i)) * (10 - i);
        }
        let resto = (soma * 10) % 11;
        if (resto === 10 || resto === 11) resto = 0;
        if (resto !== parseInt(cpf.charAt(9))) return false;

        // Segundo dígito verificador
        soma = 0;
        for (let i = 0; i < 10; i++) {
            soma += parseInt(cpf.charAt(i)) * (11 - i);
        }
        resto = (soma * 10) % 11;
        if (resto === 10 || resto === 11) resto = 0;
        if (resto !== parseInt(cpf.charAt(10))) return false;

        return true;
    }
}

/**
 * Validador de CNPJ
 * Single Responsibility: Apenas validação de CNPJ
 */
class CnpjValidator extends IValidator {
    /**
     * Valida CNPJ
     * @param {string} cnpj - CNPJ a ser validado
     * @returns {Object} Resultado da validação
     */
    validate(cnpj) {
        if (!cnpj) {
            return { isValid: false, message: ERROR_MESSAGES.REQUIRED };
        }

        const cnpjLimpo = cnpj.replace(VALIDATION_CONFIG.CNPJ.CLEAN_REGEX, "");

        if (cnpjLimpo.length !== VALIDATION_CONFIG.CNPJ.LENGTH) {
            return { isValid: false, message: ERROR_MESSAGES.INVALID_CNPJ };
        }

        // Verifica se todos os dígitos são iguais
        if (/^(\d)\1+$/.test(cnpjLimpo)) {
            return { isValid: false, message: ERROR_MESSAGES.INVALID_CNPJ };
        }

        // Validação do algoritmo do CNPJ
        if (!this.validateCnpjAlgorithm(cnpjLimpo)) {
            return { isValid: false, message: ERROR_MESSAGES.INVALID_CNPJ };
        }

        return { isValid: true };
    }

    /**
     * Valida algoritmo do CNPJ
     * @param {string} cnpj - CNPJ limpo (apenas números)
     * @returns {boolean} True se válido
     */
    validateCnpjAlgorithm(cnpj) {
        // Primeiro dígito verificador
        let soma = 0;
        let peso = 2;
        for (let i = 11; i >= 0; i--) {
            soma += parseInt(cnpj.charAt(i)) * peso;
            peso = peso === 9 ? 2 : peso + 1;
        }
        let resto = soma % 11;
        const digito1 = resto < 2 ? 0 : 11 - resto;
        if (digito1 !== parseInt(cnpj.charAt(12))) return false;

        // Segundo dígito verificador
        soma = 0;
        peso = 2;
        for (let i = 12; i >= 0; i--) {
            soma += parseInt(cnpj.charAt(i)) * peso;
            peso = peso === 9 ? 2 : peso + 1;
        }
        resto = soma % 11;
        const digito2 = resto < 2 ? 0 : 11 - resto;
        if (digito2 !== parseInt(cnpj.charAt(13))) return false;

        return true;
    }
}

/**
 * Validador de CEP
 * Single Responsibility: Apenas validação de CEP
 */
class CepValidator extends IValidator {
    /**
     * Valida CEP
     * @param {string} cep - CEP a ser validado
     * @returns {Object} Resultado da validação
     */
    validate(cep) {
        if (!cep) {
            return { isValid: false, message: ERROR_MESSAGES.REQUIRED };
        }

        const cepLimpo = cep.replace(VALIDATION_CONFIG.CEP.CLEAN_REGEX, "");

        if (cepLimpo.length !== VALIDATION_CONFIG.CEP.LENGTH) {
            return { isValid: false, message: ERROR_MESSAGES.INVALID_CEP };
        }

        if (!/^\d{8}$/.test(cepLimpo)) {
            return { isValid: false, message: ERROR_MESSAGES.INVALID_CEP };
        }

        return { isValid: true };
    }
}

/**
 * Validador de Email
 * Single Responsibility: Apenas validação de email
 */
class EmailValidator extends IValidator {
    /**
     * Valida email
     * @param {string} email - Email a ser validado
     * @returns {Object} Resultado da validação
     */
    validate(email) {
        if (!email) {
            return { isValid: false, message: ERROR_MESSAGES.REQUIRED };
        }

        if (!VALIDATION_CONFIG.EMAIL.REGEX.test(email)) {
            return { isValid: false, message: ERROR_MESSAGES.INVALID_EMAIL };
        }

        return { isValid: true };
    }
}

/**
 * Validador de Nome
 * Single Responsibility: Apenas validação de nome
 */
class NameValidator extends IValidator {
    /**
     * Valida nome
     * @param {string} name - Nome a ser validado
     * @returns {Object} Resultado da validação
     */
    validate(name) {
        if (!name) {
            return { isValid: false, message: ERROR_MESSAGES.REQUIRED };
        }

        const trimmedName = name.trim();

        if (trimmedName.length < VALIDATION_CONFIG.NOME.MIN_LENGTH) {
            return {
                isValid: false,
                message: ERROR_MESSAGES.MIN_LENGTH.replace(
                    "{min}",
                    VALIDATION_CONFIG.NOME.MIN_LENGTH
                ),
            };
        }

        if (trimmedName.length > VALIDATION_CONFIG.NOME.MAX_LENGTH) {
            return {
                isValid: false,
                message: ERROR_MESSAGES.MAX_LENGTH.replace(
                    "{max}",
                    VALIDATION_CONFIG.NOME.MAX_LENGTH
                ),
            };
        }

        if (!VALIDATION_CONFIG.NOME.REGEX.test(trimmedName)) {
            return { isValid: false, message: ERROR_MESSAGES.INVALID_NAME };
        }

        return { isValid: true };
    }
}

/**
 * Validador de Telefone
 * Single Responsibility: Apenas validação de telefone
 */
class PhoneValidator extends IValidator {
    /**
     * Valida telefone
     * @param {string} phone - Telefone a ser validado
     * @param {string} type - Tipo do telefone ('celular' ou 'fixo')
     * @returns {Object} Resultado da validação
     */
    validate(phone, type = "celular") {
        if (!phone) {
            return { isValid: false, message: ERROR_MESSAGES.REQUIRED };
        }

        const regex =
            type === "celular"
                ? VALIDATION_CONFIG.TELEFONE.CELULAR_REGEX
                : VALIDATION_CONFIG.TELEFONE.FIXO_REGEX;

        if (!regex.test(phone)) {
            return { isValid: false, message: ERROR_MESSAGES.INVALID_PHONE };
        }

        return { isValid: true };
    }
}

/**
 * Validador de produtos
 * Single Responsibility: Apenas validação de dados de produto
 */
class ProdutoValidator extends IValidator {
    /**
     * Valida dados completos do produto
     * @param {Object} produtoData - Dados do produto
     * @returns {Object} Resultado da validação
     */
    validate(produtoData) {
        const errors = [];

        if (!produtoData) {
            return {
                isValid: false,
                errors: ["Dados do produto são obrigatórios"],
            };
        }

        // Validação de código
        if (!produtoData.codigo || !produtoData.codigo.trim()) {
            errors.push("Código do produto é obrigatório");
        } else if (!/^PRD-\d{4}$/.test(produtoData.codigo)) {
            errors.push("Código deve seguir o padrão PRD-0000");
        }

        // Validação de campos obrigatórios
        const requiredFields = [
            { field: "tecido", message: "Tecido é obrigatório" },
            { field: "cor", message: "Cor é obrigatória" },
            { field: "estampa", message: "Estampa é obrigatória" },
            { field: "tipoTraje", message: "Tipo de traje é obrigatório" },
            { field: "textura", message: "Textura é obrigatória" },
            { field: "tamanho", message: "Tamanho é obrigatório" },
            { field: "status", message: "Status é obrigatório" },
            { field: "sexo", message: "Sexo é obrigatório" },
        ];

        requiredFields.forEach(({ field, message }) => {
            if (!produtoData[field] || !produtoData[field].toString().trim()) {
                errors.push(message);
            }
        });

        // Validação de preço
        if (!produtoData.preco || produtoData.preco <= 0) {
            errors.push("Preço deve ser maior que zero");
        }

        // Validação de tamanho
        if (produtoData.tamanho) {
            const tamanhoRegex =
                /^(PP|P|M|G|GG|XG|XXG|36|38|40|42|44|46|48|50|52|54|56|58|60)$/i;
            if (!tamanhoRegex.test(produtoData.tamanho)) {
                errors.push("Tamanho inválido");
            }
        }

        // Validação de status
        const statusValidos = [
            "disponivel",
            "indisponivel",
            "manutencao",
            "alugado",
        ];
        if (produtoData.status && !statusValidos.includes(produtoData.status)) {
            errors.push("Status inválido");
        }

        // Validação de sexo
        const sexoValidos = ["masculino", "feminino", "unissex"];
        if (produtoData.sexo && !sexoValidos.includes(produtoData.sexo)) {
            errors.push("Sexo inválido");
        }

        return { isValid: errors.length === 0, errors };
    }
}

export {
    CpfValidator,
    CnpjValidator,
    CepValidator,
    EmailValidator,
    NameValidator,
    PhoneValidator,
    ProdutoValidator,
};

/**
 * Serviço principal de validação
 * Dependency Inversion: Depende de abstrações (IValidator)
 * Single Responsibility: Apenas agregação de validadores
 */
export class ValidationService {
    constructor() {
        this.cpfValidator = new CpfValidator();
        this.cnpjValidator = new CnpjValidator();
        this.cepValidator = new CepValidator();
        this.emailValidator = new EmailValidator();
        this.nameValidator = new NameValidator();
        this.phoneValidator = new PhoneValidator();
        this.produtoValidator = new ProdutoValidator();
    }

    /**
     * Valida CPF
     * @param {string} cpf - CPF a ser validado
     * @returns {Object} Resultado da validação
     */
    validateCpf(cpf) {
        return this.cpfValidator.validate(cpf);
    }

    /**
     * Valida CNPJ
     * @param {string} cnpj - CNPJ a ser validado
     * @returns {Object} Resultado da validação
     */
    validateCnpj(cnpj) {
        return this.cnpjValidator.validate(cnpj);
    }

    /**
     * Valida CEP
     * @param {string} cep - CEP a ser validado
     * @returns {Object} Resultado da validação
     */
    validateCep(cep) {
        return this.cepValidator.validate(cep);
    }

    /**
     * Valida email
     * @param {string} email - Email a ser validado
     * @returns {Object} Resultado da validação
     */
    validateEmail(email) {
        return this.emailValidator.validate(email);
    }

    /**
     * Valida nome
     * @param {string} name - Nome a ser validado
     * @returns {Object} Resultado da validação
     */
    validateName(name) {
        return this.nameValidator.validate(name);
    }

    /**
     * Valida telefone
     * @param {string} phone - Telefone a ser validado
     * @param {string} type - Tipo do telefone
     * @returns {Object} Resultado da validação
     */
    validatePhone(phone, type = "celular") {
        return this.phoneValidator.validate(phone, type);
    }

    /**
     * Valida produto
     * @param {Object} produtoData - Dados do produto
     * @returns {Object} Resultado da validação
     */
    validateProduto(produtoData) {
        return this.produtoValidator.validate(produtoData);
    }

    /**
     * Valida dados completos do cliente
     * @param {Object} clienteData - Dados do cliente
     * @returns {Object} Resultado da validação
     */
    validateCliente(clienteData) {
        const errors = [];

        if (!clienteData) {
            return {
                isValid: false,
                errors: ["Dados do cliente são obrigatórios"],
            };
        }

        // Nome obrigatório
        const nameValidation = this.validateName(clienteData.nome);
        if (!nameValidation.isValid) {
            errors.push(nameValidation.message);
        }

        // Email obrigatório
        const emailValidation = this.validateEmail(clienteData.email);
        if (!emailValidation.isValid) {
            errors.push(emailValidation.message);
        }

        // Validação de CPF/CNPJ baseada na natureza
        if (clienteData.natureza === "PESSOA_FISICA") {
            const cpfValidation = this.validateCpf(clienteData.cpf);
            if (!cpfValidation.isValid) {
                errors.push(cpfValidation.message);
            }
        } else if (clienteData.natureza === "PESSOA_JURIDICA") {
            const cnpjValidation = this.validateCnpj(clienteData.cnpj);
            if (!cnpjValidation.isValid) {
                errors.push(cnpjValidation.message);
            }
        }

        // CEP obrigatório
        if (clienteData.cep) {
            const cepValidation = this.validateCep(clienteData.cep);
            if (!cepValidation.isValid) {
                errors.push(cepValidation.message);
            }
        }

        // Telefone obrigatório
        if (clienteData.celular) {
            const phoneValidation = this.validatePhone(
                clienteData.celular,
                "celular"
            );
            if (!phoneValidation.isValid) {
                errors.push(phoneValidation.message);
            }
        }

        return { isValid: errors.length === 0, errors };
    }
}
