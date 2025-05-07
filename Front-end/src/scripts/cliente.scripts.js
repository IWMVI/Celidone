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
        }, 5000); // exibe por 5 segundos
    },
};

// ===================== MANEJO DO FORMULÁRIO ===================== //

// Função utilitária para tratar campos vazios como null
const nullIfEmpty = (val) => (val?.trim() === "" ? null : val);

const formCliente = DOM.get("form-cliente");

formCliente.addEventListener("submit", async (e) => {
    e.preventDefault();

    const natureza = DOM.get("natureza").value;

    // Usa nullIfEmpty para tratar string vazia como null
    let cpf = nullIfEmpty(DOM.get("cpf").value);
    let cnpj = nullIfEmpty(DOM.get("cnpj").value);

    // Garante que apenas um dos campos esteja preenchido conforme a natureza
    if (natureza === "pessoa_fisica") {
        cnpj = null;
    } else if (natureza === "pessoa_juridica") {
        cpf = null;
    }

    const clienteData = {
        nome: DOM.get("nome").value,
        email: DOM.get("email").value,
        natureza:
            natureza === "pessoa_fisica" ? "PESSOA_FISICA" : "PESSOA_JURIDICA",
        cpf,
        cnpj,
        dataNascimento: DOM.get("data_nascimento").value,
        cep: DOM.get("cep").value,
        endereco: DOM.get("endereco").value,
        numero: DOM.get("numero").value,
        cidade: DOM.get("cidade").value,
        bairro: DOM.get("bairro").value,
        complemento: DOM.get("complemento").value,
        uf: DOM.get("uf").value,
        celular: DOM.get("celular").value,
        telefoneFixo: nullIfEmpty(DOM.get("telefoneFixo").value),
    };

    try {
        const resultado = await window.api.cadastrarCliente(clienteData);

        if (resultado.success) {
            UI.alert("Cliente cadastrado com sucesso!");
            formCliente.reset();
            const lista = await window.api.listarClientes();
            preencherTabela(lista.data);
        } else {
            UI.alert(`Erro: ${resultado.error}`);
        }
    } catch (error) {
        UI.alert(`Erro ao cadastrar: ${error.message}`);
    }

    console.log(JSON.stringify(clienteData, null, 2));
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
        tipo === "pessoa_fisica" ? "flex" : "none";
    DOM.get("campo-cnpj").style.display =
        tipo === "pessoa_juridica" ? "flex" : "none";
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

