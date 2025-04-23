const { sequelize } = require("../../config/database");
const clienteService = require("../services/clienteService");

beforeAll(async () => {
    await sequelize.sync({ force: true }); // Reseta o banco de dados para cada rodada de testes
});

afterAll(async () => {
    await sequelize.close(); // Fecha conexão depois dos testes
});

describe("Cliente Service", () => {
    let clienteCriado;

    test("deve criar um cliente", async () => {
        const clienteData = {
            tipo: "Pessoa Física",
            nome: "João da Silva",
            nascimento: "1990-01-01",
            cep: "12345678",
            endereco: "Rua das Flores",
            numero: "100",
            cidade: "São Paulo",
            bairro: "Centro",
            estado: "SP",
            email: "joao@example.com",
            cnpj: "12345678000199",
            natureza: "Comercial",
            celular: "11999999999",
            uf: "SP",
            data_nascimento: "1990-01-01",
        };

        clienteCriado = await clienteService.criarCliente(clienteData);

        expect(clienteCriado).toBeDefined();
        expect(clienteCriado.nome).toBe("João da Silva");
    });

    test("deve buscar cliente por ID", async () => {
        const cliente = await clienteService.buscarClientesPorId(
            clienteCriado.id
        );

        expect(cliente).toBeDefined();
        expect(cliente.nome).toBe("João da Silva");
    });

    test("deve atualizar cliente", async () => {
        const dadosAtualizados = { cidade: "Rio de Janeiro" };
        const clienteAtualizado = await clienteService.atualizarCliente(
            clienteCriado.id,
            dadosAtualizados
        );

        expect(clienteAtualizado.cidade).toBe("Rio de Janeiro");
    });

    test("deve buscar clientes por nome", async () => {
        const clientes = await clienteService.buscarClientesPorNome("João");

        expect(clientes.length).toBeGreaterThan(0);
        expect(clientes[0].nome).toContain("João");
    });

    test("deve listar todos os clientes", async () => {
        const clientes = await clienteService.listarClientes();

        expect(Array.isArray(clientes)).toBe(true);
        expect(clientes.length).toBeGreaterThan(0);
    });

    test("deve remover cliente", async () => {
        const clienteRemovido = await clienteService.removerCliente(
            clienteCriado.id
        );

        expect(clienteRemovido).toBeDefined();

        const clienteDepois = await clienteService.buscarClientesPorId(
            clienteCriado.id
        );
        expect(clienteDepois).toBeNull();
    });
});
