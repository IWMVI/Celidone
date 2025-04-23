const { Sequelize } = require("sequelize");
require("dotenv").config();

const environment = (process.env.NODE_ENV || "development").trim(); // Remove espaços extras

const config = {
    development: {
        username: process.env.POSTGRES_USER,
        password: process.env.POSTGRES_PASSWORD,
        database: process.env.POSTGRES_DB,
        host: "localhost",
        dialect: "postgres",
        port: 5432,
        logging: false,
    },
    test: {
        dialect: "sqlite",
        storage: ":memory:", // Banco de dados em memória para testes
        logging: false,
    },
    production: {
        username: process.env.POSTGRES_USER,
        password: process.env.POSTGRES_PASSWORD,
        database: process.env.POSTGRES_DB,
        host: "localhost",
        dialect: "postgres",
        port: 5432,
        logging: false,
    },
};

// Seleciona a configuração com base no ambiente
const currentConfig = config[environment];

// Verifica se a configuração do ambiente é válida
if (!currentConfig) {
    throw new Error(
        `Configuração para o ambiente '${environment}' não encontrada.`
    );
}

// Passa os valores da configuração para o Sequelize
let sequelize;
if (environment === "test") {
    // Para o ambiente de teste, apenas o objeto de configuração é necessário
    sequelize = new Sequelize(currentConfig);
} else {
    // Para outros ambientes, passe os parâmetros completos
    const { database, username, password, ...restConfig } = currentConfig;
    sequelize = new Sequelize(database, username, password, restConfig);
}

module.exports = { sequelize };
