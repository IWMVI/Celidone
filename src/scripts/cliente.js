// Função para mostrar mensagem de erro
function showError(message) {
    const errorMessageElement = document.getElementById("error-message");
    const errorText = document.getElementById("error-text");

    errorText.textContent = message;
    errorMessageElement.style.display = "block";
}

// Função para esconder a mensagem de erro
function hideError() {
    const errorMessageElement = document.getElementById("error-message");
    errorMessageElement.style.display = "none";
}

// Enviar dados do formulário
document
    .getElementById("form-cliente")
    .addEventListener("submit", function (event) {
        event.preventDefault();

        const clienteData = {
            nome: document.getElementById("nome").value,
            email: document.getElementById("email").value,
            telefone: document.getElementById("telefone").value,
            natureza: document.getElementById("natureza").value,
            dataNascimento: document.getElementById("dataNascimento").value,
            cep: document.getElementById("cep").value,
            endereco: document.getElementById("endereco").value,
            numero: document.getElementById("numero").value,
            cidade: document.getElementById("cidade").value,
            bairro: document.getElementById("bairro").value,
            complemento: document.getElementById("complemento").value,
            uf: document.getElementById("uf").value,
            celular: document.getElementById("celular").value,
            telefoneFixo: document.getElementById("telefoneFixo").value,
        };

        window.api.send("criar-cliente", clienteData);
    });

// Receber resposta de cliente criado
window.api.on("cliente-criado", (response) => {
    if (response.sucesso) {
        alert("Cliente cadastrado com sucesso!");
        document.getElementById("form-cliente").reset();
        window.api.send("listar-clientes");
    } else {
        alert(`Erro ao cadastrar cliente: ${response.erro}`);
    }
});

// Exibir lista de clientes
function exibirClientes(clientes) {
    const tabela = document.getElementById("cliente-lista");
    tabela.innerHTML = "";

    clientes.forEach((cliente) => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${cliente.id}</td>
            <td>${cliente.natureza || ""}</td>
            <td>${cliente.nome}</td>
            <td>${cliente.dataNascimento || ""}</td>
            <td>${cliente.cep || ""}</td>
            <td>${cliente.endereco || ""}</td>
            <td>${cliente.numero || ""}</td>
            <td>${cliente.cidade || ""}</td>
            <td>${cliente.bairro || ""}</td>
            <td>${cliente.complemento || ""}</td>
            <td>${cliente.uf || ""}</td>
            <td>${cliente.email}</td>
            <td>${cliente.celular || ""}</td>
            <td>${cliente.telefoneFixo || ""}</td>
        `;
        tabela.appendChild(row);
    });
}

// Botão de listar clientes
document
    .getElementById("listar-clientes-btn")
    .addEventListener("click", function () {
        window.api.send("listar-clientes");
    });

// Recebe lista de clientes
window.api.on("clientes", (clientes) => {
    exibirClientes(clientes);
});

// Função de fetch com timeout
async function fetchWithTimeout(url, timeout = 5000) {
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), timeout);

    try {
        const response = await fetch(url, {
            signal: controller.signal,
        });
        clearTimeout(timeoutId);
        return await response.json();
    } catch (error) {
        clearTimeout(timeoutId);
        throw error;
    }
}

const btnBuscarCep = document.getElementById("buscar-cep-btn");

btnBuscarCep.addEventListener("click", async function () {
    console.log('Botão "Buscar CEP" clicado');

    const cepInput = document.getElementById("cep");
    const cep = cepInput.value.replace(/\D/g, "");

    if (cep.length !== 8) {
        showError(
            "CEP inválido. Por favor, insira um CEP válido com 8 dígitos."
        );
        return; // Não faz nada se o CEP for inválido
    }

    hideError(); // Esconde qualquer erro anterior
    btnBuscarCep.disabled = true;
    btnBuscarCep.textContent = "Buscando...";

    try {
        console.log(`Buscando informações para o CEP: ${cep}`);

        const data = await window.api.buscarCep(cep);

        // Verifica se a resposta contém dados válidos
        if (!data || !data.logradouro) {
            showError(
                "CEP não encontrado. Por favor, verifique o CEP informado."
            );
        } else {
            console.log("Resposta da API ViaCEP:", data);

            // Preenche os campos com os dados do CEP
            document.getElementById("endereco").value = data.logradouro || "";
            document.getElementById("bairro").value = data.bairro || "";
            document.getElementById("cidade").value = data.localidade || "";
            document.getElementById("uf").value = data.uf || "";
            document.getElementById("complemento").value =
                data.complemento || "";
        }
    } catch (error) {
        // Exibe o erro completo no console
        console.error("Erro ao buscar o CEP:", error);

        // Verifica se o erro é devido a um problema de conexão
        if (error.name === "AbortError" || error.message.includes("fetch")) {
            showError(
                "Erro de conexão. Verifique sua internet e tente novamente."
            );
        } else {
            showError("Erro desconhecido ao buscar o CEP.");
        }
    } finally {
        btnBuscarCep.disabled = false;
        btnBuscarCep.textContent = "Buscar CEP";
    }
});

const tipoPessoaSelect = document.getElementById("natureza");
const campoCpf = document.getElementById("campo-cpf");
const campoCnpj = document.getElementById("campo-cnpj");

tipoPessoaSelect.addEventListener("change", function () {
    const tipoPessoa = tipoPessoaSelect.value;

    // Exibe ou oculta os campos com base na seleção
    if (tipoPessoa === "pessoa_fisica") {
        campoCpf.style.display = "block"; // Exibe o campo CPF
        campoCnpj.style.display = "none"; // Oculta o campo CNPJ
    } else if (tipoPessoa === "pessoa_juridica") {
        campoCnpj.style.display = "block"; // Exibe o campo CNPJ
        campoCpf.style.display = "none"; // Oculta o campo CPF
    } else {
        campoCnpj.style.display = "none"; // Oculta o campo CNPJ
        campoCpf.style.display = "none"; // Oculta o campo CPF
    }
});

// Chama a função ao carregar a página para garantir que o comportamento esteja correto
window.onload = function () {
    tipoPessoaSelect.dispatchEvent(new Event("change"));
};

// Função para aplicar a máscara de CPF
function aplicarMascaraCpf(cpf) {
    return cpf
        .replace(/\D/g, "") // Remove qualquer caractere não numérico
        .replace(/^(\d{3})(\d{3})(\d{3})(\d{2})$/, "$1.$2.$3-$4"); // Aplica a máscara
}

// Função para aplicar a máscara de CNPJ
function aplicarMascaraCnpj(cnpj) {
    return cnpj
        .replace(/\D/g, "") // Remove qualquer caractere não numérico
        .replace(/^(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})$/, "$1.$2.$3/$4-$5"); // Aplica a máscara
}

// Função para aplicar a máscara de CEP
function aplicarMascaraCep(cep) {
    return cep
        .replace(/\D/g, "") // Remove qualquer caractere não numérico
        .replace(/^(\d{5})(\d{3})$/, "$1-$2"); // Aplica a máscara
}

// Função para formatar CPF, CNPJ e CEP conforme a entrada
document.getElementById("cpf").addEventListener("input", function (event) {
    const cpf = event.target.value;
    event.target.value = aplicarMascaraCpf(cpf);
});

document.getElementById("cnpj").addEventListener("input", function (event) {
    const cnpj = event.target.value;
    event.target.value = aplicarMascaraCnpj(cnpj);
});

document.getElementById("cep").addEventListener("input", function (event) {
    const cep = event.target.value;
    event.target.value = aplicarMascaraCep(cep);
});
