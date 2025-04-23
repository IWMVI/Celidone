const { Sequelize } = require("sequelize");

// Garantir que as variáveis de ambiente sejam carregadas corretamente
require("dotenv").config();

const sequelize = new Sequelize({
    username: process.env.POSTGRES_USER,
    password: process.env.POSTGRES_PASSWORD,
    database: process.env.POSTGRES_DB,
    host: "localhost",
    dialect: "postgres",
    port: 5432,
    logging: false,
});

module.exports = sequelize;
