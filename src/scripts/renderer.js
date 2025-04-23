const { ipcRenderer } = require("electron");

function openWindow(param) {
    api.open(param);
}

function exibirClientes() {
    ipcRenderer.send("listar-clientes"); // Envia a solicitação para o main process
}

// Escuta a resposta do IPC com a lista de clientes
ipcRenderer.on("clientes", (event, clientes) => {
    const listaClientes = document.getElementById("lista-clientes");
    listaClientes.innerHTML = ""; // Limpa a lista antes de preencher

    // Adiciona cada cliente à lista
    clientes.forEach((cliente) => {
        const li = document.createElement("li");
        li.textContent = `${cliente.nome} - ${cliente.email}`;
        listaClientes.appendChild(li);
    });
});

// Chama a função ao carregar a página
window.onload = () => {
    // Chama a função de exibição de clientes
    exibirClientes();
};
