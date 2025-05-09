const { expect } = require("@jest/globals");

jest.useFakeTimers();

// Mocks de utilitários DOM e UI
const DOM = {
  get: (id) => document.getElementById(id),
  show: (el) => (el.style.display = "block"),
  hide: (el) => (el.style.display = "none"),
  setText: (el, text) => (el.textContent = text),
  setValue: (id, value) => (DOM.get(id).value = value || "")
};

const UI = {
  showError: (msg) => {
    DOM.setText(DOM.get("error-text"), msg);
    DOM.show(DOM.get("error-message"));
  },
  hideError: () => DOM.hide(DOM.get("error-message")),
  alert: (msg) => {
    const toast = DOM.get("toast");
    DOM.setText(toast, msg);
    toast.style.display = "block";
    toast.style.opacity = "1";
    setTimeout(() => {
      toast.style.opacity = "0";
      setTimeout(() => {
        toast.style.display = "none";
      }, 500);
    }, 5000);
  }
};

// Função de utilidade
const nullIfEmpty = (val) => (val?.trim() === "" ? null : val);

// Função de máscara genérica
const aplicarMascara = (valor, regex, formato) =>
  valor.replace(/\D/g, "").replace(regex, formato);

describe("Testes Frontend Completos", () => {
  beforeEach(() => {
    document.body.innerHTML = `
      <div id="toast"></div>
      <div id="error-message" style="display: none;">
        <span id="error-text"></span>
      </div>
      <div id="msg"></div>
      <input id="input" />
    `;
  });

  test("DOM.get retorna o elemento correto", () => {
    expect(DOM.get("msg")).toBeInTheDocument();
  });

  test("DOM.show exibe um elemento", () => {
    const el = DOM.get("msg");
    DOM.show(el);
    expect(el).toHaveStyle("display: block");
  });

  test("DOM.hide esconde um elemento", () => {
    const el = DOM.get("msg");
    DOM.hide(el);
    expect(el).toHaveStyle("display: none");
  });

  test("DOM.setText define texto do elemento", () => {
    const el = DOM.get("msg");
    DOM.setText(el, "Texto teste");
    expect(el).toHaveTextContent("Texto teste");
  });

  test("DOM.setValue define valor do input", () => {
    DOM.setValue("input", "abc123");
    expect(DOM.get("input").value).toBe("abc123");
  });

  test("UI.showError exibe mensagem de erro", () => {
    UI.showError("Erro ocorreu");
    expect(DOM.get("error-text")).toHaveTextContent("Erro ocorreu");
    expect(DOM.get("error-message")).toHaveStyle("display: block");
  });

  test("UI.hideError esconde mensagem de erro", () => {
    DOM.show(DOM.get("error-message"));
    UI.hideError();
    expect(DOM.get("error-message")).toHaveStyle("display: none");
  });

  test("UI.alert exibe e esconde toast após 5s", () => {
    UI.alert("Alerta");
    const toast = DOM.get("toast");

    expect(toast).toHaveTextContent("Alerta");
    expect(toast.style.display).toBe("block");
    expect(toast.style.opacity).toBe("1");

    jest.advanceTimersByTime(5000);
    expect(toast.style.opacity).toBe("0");

    jest.advanceTimersByTime(500);
    expect(toast.style.display).toBe("none");
  });

  test("nullIfEmpty retorna null para vazio e valor para preenchido", () => {
    expect(nullIfEmpty("")).toBeNull();
    expect(nullIfEmpty("   ")).toBeNull();
    expect(nullIfEmpty("abc")).toBe("abc");
  });

  test("aplica máscara CPF corretamente", () => {
    const cpf = aplicarMascara("12345678901", /^(\d{3})(\d{3})(\d{3})(\d{2})$/, "$1.$2.$3-$4");
    expect(cpf).toBe("123.456.789-01");
  });

  test("aplica máscara CNPJ corretamente", () => {
    const cnpj = aplicarMascara("12345678000199", /^(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})$/, "$1.$2.$3/$4-$5");
    expect(cnpj).toBe("12.345.678/0001-99");
  });

  test("aplica máscara CEP corretamente", () => {
    const cep = aplicarMascara("12345678", /^(\d{5})(\d{3})$/, "$1-$2");
    expect(cep).toBe("12345-678");
  });
});
