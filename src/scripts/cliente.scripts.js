// ===================== UTILITÁRIOS DE INTERFACE ===================== //

const DOM = {
    get: (id) => document.getElementById(id),
    show: (el) => (el.style.display = "block"),
    hide: (el) => (el.style.display = "none"),
    setText: (el, text) => (el.textContent = text),
    setValue: (id, value) => (DOM.get(id).value = value || ""),
};

const UI = {
    showError: (msg) => {
        DOM.setText(DOM.get("error-text"), msg);
        DOM.show(DOM.get("error-message"));
    },
    hideError: () => DOM.hide(DOM.get("error-message")),
};

// ===================== MANEJO DO FORMULÁRIO ===================== //

const formCliente = DOM.get("form-cliente");

formCliente.addEventListener("submit", (e) => {
    e.preventDefault();

    const campos = [
        "nome",
        "email",
        "telefone",
        "natureza",
        "dataNascimento",
        "cep",
        "endereco",
        "numero",
        "cidade",
        "bairro",
        "complemento",
        "uf",
        "celular",
        "telefoneFixo",
    ];

    const dados = Object.fromEntries(
        campos.map((id) => [id, DOM.get(id).value.trim()])
    );

    window.api.send("criar-cliente", dados);
});

window.api.on("cliente-criado", ({ sucesso, erro }) => {
    if (sucesso) {
        UI.alert("Cliente cadastrado com sucesso!");
        formCliente.reset();
        window.api.send("listar-clientes");
    } else {
        UI.alert(`Erro ao cadastrar cliente: ${erro}`);
    }
});

// ===================== LISTAGEM DE CLIENTES ===================== //

function preencherTabela(clientes = []) {
    const tbody = DOM.get("cliente-lista");
    tbody.innerHTML = "";

    clientes.forEach((c) => {
        tbody.innerHTML += `
            <tr>
                <td>${c.id}</td>
                <td>${c.natureza || ""}</td>
                <td>${c.nome}</td>
                <td>${c.dataNascimento || ""}</td>
                <td>${c.cep || ""}</td>
                <td>${c.endereco || ""}</td>
                <td>${c.numero || ""}</td>
                <td>${c.cidade || ""}</td>
                <td>${c.bairro || ""}</td>
                <td>${c.complemento || ""}</td>
                <td>${c.uf || ""}</td>
                <td>${c.email}</td>
                <td>${c.celular || ""}</td>
                <td>${c.telefoneFixo || ""}</td>
            </tr>`;
    });
}

DOM.get("listar-clientes-btn").addEventListener("click", () => {
    window.api.send("listar-clientes");
});

window.api.on("clientes", preencherTabela);

// ===================== BUSCA DE CEP ===================== //

const btnCep = DOM.get("buscar-cep-btn");

btnCep.addEventListener("click", async () => {
    const cep = DOM.get("cep").value.replace(/\D/g, "");
    if (cep.length !== 8)
        return UI.showError("CEP inválido. Digite 8 dígitos.");

    UI.hideError();
    btnCep.disabled = true;
    btnCep.textContent = "Buscando...";

    try {
        const data = await window.api.buscarCep(cep);
        if (!data?.logradouro) {
            UI.showError("CEP não encontrado.");
        } else {
            const map = {
                endereco: "logradouro",
                bairro: "bairro",
                cidade: "localidade",
                uf: "uf",
                complemento: "complemento",
            };
            Object.entries(map).forEach(([campo, chave]) =>
                DOM.setValue(campo, data[chave])
            );
        }
    } catch (err) {
        const msg =
            err.message?.includes("timeout") || err.name === "AbortError"
                ? "Erro de conexão. Verifique sua internet."
                : "Erro ao buscar o CEP.";
        UI.showError(msg);
    } finally {
        btnCep.disabled = false;
        btnCep.textContent = "Buscar CEP";
    }
});

// ===================== TIPO DE PESSOA ===================== //

const selectNatureza = DOM.get("natureza");

selectNatureza.addEventListener("change", () => {
    const tipo = selectNatureza.value;
    DOM.get("campo-cpf").style.display =
        tipo === "pessoa_fisica" ? "block" : "none";
    DOM.get("campo-cnpj").style.display =
        tipo === "pessoa_juridica" ? "block" : "none";
});

window.addEventListener("DOMContentLoaded", () => {
    selectNatureza.dispatchEvent(new Event("change"));
});

// ===================== MÁSCARAS ===================== //

const aplicarMascara = (valor, regex, formato) =>
    valor.replace(/\D/g, "").replace(regex, formato);

const mascaras = {
    cpf: [/^(\d{3})(\d{3})(\d{3})(\d{2})$/, "$1.$2.$3-$4"],
    cnpj: [/^(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})$/, "$1.$2.$3/$4-$5"],
    cep: [/^(\d{5})(\d{3})$/, "$1-$2"],
};

Object.entries(mascaras).forEach(([id, [regex, formato]]) => {
    DOM.get(id)?.addEventListener("input", (e) => {
        e.target.value = aplicarMascara(e.target.value, regex, formato);
    });
});

