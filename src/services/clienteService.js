const { Cliente } = require("../models");

const { Op } = require("sequelize");

async function criarCliente(clienteData) {
    const camposObrigatorios = [
        "tipo",
        "nome",
        "nascimento",
        "cep",
        "endereco",
        "numero",
        "cidade",
        "bairro",
        "estado",
        "email",
        "cnpj",
        "natureza",
        "celular",
        "uf",
        "data_nascimento",
    ];

    for (const campo of camposObrigatorios) {
        if (!clienteData[campo]) {
            throw new Error(`O campo ${campo} é obrigatório`);
        }
    }

    if (clienteData.complemento === undefined) {
        clienteData.complemento = null;
    }

    const cliente = await Cliente.create(clienteData);
    return cliente;
}

async function atualizarCliente(id, dadosAtualizados) {
    const cliente = await Cliente.findByPk(id);
    if (!cliente) {
        throw new Error("Cliente não encontrado");
    }

    const camposPermitidos = Object.keys(Cliente.rawAttributes);
    for (const campo of camposPermitidos) {
        if (dadosAtualizados[campo] !== undefined && campo !== "id") {
            if (
                Cliente.rawAttributes[campo].allowNull === false &&
                (dadosAtualizados[campo] === null ||
                    dadosAtualizados[campo] === "")
            ) {
                throw new Error(`O campo ${campo} não pode ser vazio`);
            }
            cliente[campo] = dadosAtualizados[campo];
        }
    }

    await cliente.save();
    return cliente;
}

async function listarClientes() {
    return await Cliente.findAll();
}

async function buscarClientesPorId(id) {
    return await Cliente.findByPk(id);
}

async function buscarClientesPorNome(nome) {
    return await Cliente.findAll({
        where: {
            nome: {
                [Op.iLike]: `%${nome}%`,
            },
        },
    });
}

async function removerCliente(id) {
    const cliente = await Cliente.findByPk(id);
    if (!cliente) {
        return null;
    }
    await cliente.destroy();
    return cliente;
}

function validarEmail(email) {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
}

module.exports = {
    criarCliente,
    listarClientes,
    buscarClientesPorId,
    buscarClientesPorNome,
    atualizarCliente,
    removerCliente,
    validarEmail,
};
