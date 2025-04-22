class Cliente {
    constructor(nome, telefone, email) {
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
        this.id = Math.floor(Math.random() * 1000); // Simular a criação de um ID único
    }
}

let clientes = [];

function criarCliente(nome, telefone, email) {
    const novoCliente = new Cliente(nome, telefone, email);
    clientes.push(novoCliente);
    return novoCliente;
}

function buscarClientesPorId(id) {
    return clientes.find((cliente) => cliente.id === id);
}

function buscarClientesPorNome(nome) {
    return clientes.filter((cliente) => cliente.nome.includes(nome));
}

function atualizarCliente(id, dadosAtualizados) {
    const cliente = clientes.find((cliente) => cliente.id === id);
    if (cliente) {
        cliente.nome = dadosAtualizados.nome || cliente.nome;
        cliente.telefone = dadosAtualizados.telefone || cliente.telefone;
        cliente.email = dadosAtualizados.email || cliente.email;
    }

    return cliente;
}

function removerCliente(id) {
    const index = clientes.findIndex((cliente) => cliente.id === id);

    if (index !== -1) {
        clientes.splice(index, 1);
        return true;
    }

    return false;
}

function validarEmail(email) {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
}

function listarClientes() {
    return clientes;
}

module.exports = {
    clientes,
    criarCliente,
    buscarClientesPorId,
    buscarClientesPorNome,
    atualizarCliente,
    removerCliente,
    validarEmail,
    listarClientes,
};
