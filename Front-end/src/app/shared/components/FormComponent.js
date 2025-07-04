import BaseComponent from "./BaseComponent.js";

/**
 * Componente de formulário genérico
 * Implementa funcionalidades comuns de formulários
 */
class FormComponent extends BaseComponent {
    constructor(formId, options = {}) {
        super();
        this.formId = formId;
        this.options = {
            validateOnChange: true,
            validateOnSubmit: true,
            autoFocus: true,
            ...options,
        };
        this.validators = new Map();
        this.fields = new Map();
        this.isValid = false;
    }

    /**
     * Inicializa o componente
     */
    init() {
        if (this.initialized) return;

        this.element = this.domService.getElementById(this.formId);
        if (!this.element) {
            throw new Error(
                `Formulário com ID '${this.formId}' não encontrado`
            );
        }

        this.bindEvents();
        this.setupFields();
        this.initialized = true;
    }

    /**
     * Configura campos do formulário
     */
    setupFields() {
        const inputs = this.element.querySelectorAll("input, select, textarea");

        inputs.forEach((input) => {
            this.fields.set(input.id || input.name, input);

            if (this.options.validateOnChange) {
                this.domService.addEventListener(input, "blur", () => {
                    this.validateField(input);
                });
            }
        });
    }

    /**
     * Vincula eventos do formulário
     */
    bindEvents() {
        this.domService.addEventListener(this.element, "submit", (e) => {
            e.preventDefault();
            this.handleSubmit();
        });
    }

    /**
     * Manipula envio do formulário
     */
    async handleSubmit() {
        if (this.options.validateOnSubmit) {
            const isValid = await this.validateForm();
            if (!isValid) {
                this.notificationService.showError(
                    "Por favor, corrija os erros no formulário"
                );
                return;
            }
        }

        const formData = this.getFormData();
        this.emit("submit", formData);
    }

    /**
     * Adiciona validador para um campo
     * @param {string} fieldName - Nome do campo
     * @param {IValidator} validator - Validador
     */
    addValidator(fieldName, validator) {
        if (!this.validators.has(fieldName)) {
            this.validators.set(fieldName, []);
        }
        this.validators.get(fieldName).push(validator);
    }

    /**
     * Valida um campo específico
     * @param {HTMLElement} field - Campo a ser validado
     * @returns {boolean} True se válido
     */
    validateField(field) {
        const fieldName = field.id || field.name;
        const validators = this.validators.get(fieldName);

        if (!validators || validators.length === 0) {
            return true;
        }

        let isValid = true;
        let errorMessage = "";

        for (const validator of validators) {
            const result = validator.validate(field.value);
            if (!result.isValid) {
                isValid = false;
                errorMessage = result.message;
                break;
            }
        }

        this.showFieldValidation(field, isValid, errorMessage);
        return isValid;
    }

    /**
     * Exibe resultado da validação do campo
     * @param {HTMLElement} field - Campo
     * @param {boolean} isValid - Se é válido
     * @param {string} errorMessage - Mensagem de erro
     */
    showFieldValidation(field, isValid, errorMessage) {
        const errorElement = this.element.querySelector(`#${field.id}-error`);

        if (isValid) {
            this.domService.removeClass(field, "error");
            this.domService.addClass(field, "valid");
            if (errorElement) {
                this.domService.hide(errorElement);
            }
        } else {
            this.domService.removeClass(field, "valid");
            this.domService.addClass(field, "error");
            if (errorElement) {
                this.domService.setText(errorElement, errorMessage);
                this.domService.show(errorElement);
            }
        }
    }

    /**
     * Valida todo o formulário
     * @returns {Promise<boolean>} True se válido
     */
    async validateForm() {
        let isValid = true;

        for (const [fieldName, field] of this.fields) {
            const fieldValid = this.validateField(field);
            if (!fieldValid) {
                isValid = false;
            }
        }

        this.isValid = isValid;
        return isValid;
    }

    /**
     * Obtém dados do formulário
     * @returns {Object} Dados do formulário
     */
    getFormData() {
        const data = {};

        for (const [fieldName, field] of this.fields) {
            if (field.type === "checkbox") {
                data[fieldName] = field.checked;
            } else if (field.type === "radio") {
                if (field.checked) {
                    data[fieldName] = field.value;
                }
            } else {
                data[fieldName] = field.value;
            }
        }

        return data;
    }

    /**
     * Preenche formulário com dados
     * @param {Object} data - Dados para preencher
     */
    setFormData(data) {
        for (const [fieldName, value] of Object.entries(data)) {
            const field = this.fields.get(fieldName);
            if (field) {
                if (field.type === "checkbox") {
                    field.checked = Boolean(value);
                } else if (field.type === "radio") {
                    field.checked = field.value === value;
                } else {
                    field.value = value || "";
                }
            }
        }
    }

    /**
     * Limpa o formulário
     */
    reset() {
        this.element.reset();

        // Remove classes de validação
        for (const [fieldName, field] of this.fields) {
            this.domService.removeClass(field, "error");
            this.domService.removeClass(field, "valid");

            const errorElement = this.element.querySelector(
                `#${field.id}-error`
            );
            if (errorElement) {
                this.domService.hide(errorElement);
            }
        }

        this.isValid = false;
    }

    /**
     * Habilita o formulário
     */
    enable() {
        for (const [fieldName, field] of this.fields) {
            field.disabled = false;
        }
    }

    /**
     * Desabilita o formulário
     */
    disable() {
        for (const [fieldName, field] of this.fields) {
            field.disabled = true;
        }
    }
}

export default FormComponent;
