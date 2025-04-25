const { ipcRenderer } = require("electron");

// Função para abrir uma janela
function openWindow(param) {
    if (param) {
        api.open(param);
    } else {
        console.error("Parametro inválido ao abrir a janela.");
    }
}

// Função para solicitar a listagem de clientes
function exibirClientes() {
    ipcRenderer.send("listar-clientes");
}

// Função para renderizar a lista de clientes na página
function renderizarClientes(clientes) {
    const listaClientes = document.getElementById("lista-clientes");
    listaClientes.innerHTML = ""; // Limpa a lista antes de preencher

    if (!clientes || clientes.length === 0) {
        const li = document.createElement("li");
        li.textContent = "Nenhum cliente encontrado.";
        listaClientes.appendChild(li);
        return;
    }

    // Adiciona cada cliente à lista
    clientes.forEach((cliente) => {
        const li = document.createElement("li");
        li.textContent = `${cliente.nome} - ${cliente.email}`;
        listaClientes.appendChild(li);
    });
}

// Escuta a resposta do IPC com a lista de clientes
ipcRenderer.on("clientes", (event, clientes) => {
    renderizarClientes(clientes);
});

// Chama a função ao carregar a página
window.onload = () => {
    exibirClientes();
};

