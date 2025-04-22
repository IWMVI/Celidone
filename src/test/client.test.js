const {
    criarCliente,
    clientes,
    buscarClientesPorId,
    buscarClientesPorNome,
    atualizarCliente,
    removerCliente,
    validarEmail,
    listarClientes,
} = require("../models/cliente");

describe("Gerenciamento de clientes", () => {
    it("Deve criar um novo cliente", async () => {
        const novoCliente = {
            nome: "Pedro Silva",
            telefone: "11999999999",
            email: "email@email.com",
        };

        const clienteCriado = criarCliente(
            novoCliente.nome,
            novoCliente.telefone,
            novoCliente.email
        );

        expect(clienteCriado).toHaveProperty("id");
        expect(clienteCriado.nome).toBe(novoCliente.nome);
        expect(clienteCriado.telefone).toBe(novoCliente.telefone);
        expect(clienteCriado.email).toBe(novoCliente.email);
        expect(clientes).toContain(clienteCriado);
    });
});

describe("Leitura de clientes", () => {
    it("Deve buscar um cliente por ID", () => {
        const cliente = criarCliente("Ronaldo Silva", "11", "email@email.com");
        const clientBuscado = buscarClientesPorId(cliente.id);

        expect(clientBuscado).toBe(cliente);
    });

    it("Deve buscar clientes por nome", () => {
        criarCliente("João Silva", "11", "email@email.com");
        const clientesBuscados = buscarClientesPorNome("João");

        expect(clientesBuscados).toHaveLength(1);
        expect(clientesBuscados[0].nome).toBe("João Silva");
    });

    it("Não deve encontrar clientes quando buscar por nome e não houver correspondências", () => {
        criarCliente("Fulano de Tal", "11", "email@email.com");

        const clienteBuscado = buscarClientesPorNome("Maria");

        expect(clienteBuscado).toHaveLength(0);
    });

    it("Deve buscar clientes por nome parcialmente", () => {
        const clientesBuscados = buscarClientesPorNome("Fulano");

        expect(clientesBuscados).toHaveLength(1);
        expect(clientesBuscados[0].nome).toBe("Fulano de Tal");
    });
});

describe("Atualização de clientes", () => {
    it("Deve atualizar um cliente", () => {
        const cliente = criarCliente("João Silva", "11", "email@email.com");
        const dadosAtualizados = {
            nome: "João Junior",
            telefone: "119",
        };

        const clienteAtualizado = atualizarCliente(
            cliente.id,
            dadosAtualizados
        );

        expect(clienteAtualizado.nome).toBe(dadosAtualizados.nome);
        expect(clienteAtualizado.telefone).toBe(dadosAtualizados.telefone);
        expect(clienteAtualizado.email).toBe(cliente.email);
    });
});

describe("Remoção de clientes", () => {
    it("Deve remover um cliente", () => {
        const cliente = criarCliente("João", "11", "email@email.com");
        const clienteId = cliente.id;

        const removido = removerCliente(clienteId);

        expect(removido).toBe(true);
        expect(clientes).not.toContain(cliente);
    });

    it("Não deve remover um cliente com ID inválido", () => {
        const removido = removerCliente(99990000);

        expect(removido).toBe(false);
    });
});

describe("Validação de email", () => {
    it("Não deve validar um email incorreto", () => {
        const emailInvalido = "email@email";
        const resultado = validarEmail(emailInvalido);

        expect(resultado).toBe(false);
    });
    it("Deve validar um email correto", () => {
        const emailValido = "email@email.com";
        const resultado = validarEmail(emailValido);

        expect(resultado).toBe(true);
    });
});

describe("Listagem de clientes", () => {
    beforeEach(() => {
        clientes.length = 0;
        // Limpar clientes que foram criados nos testes acima.
    });
    it("Deve listar todos os clientes", () => {
        criarCliente("João Silva", "11", "email@email.com");
        criarCliente("João Pedro", "11", "email@email.com");
        const clientesListados = listarClientes();

        expect(clientesListados).toHaveLength(2);
    });
});
