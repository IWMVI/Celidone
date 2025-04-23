"use strict";

module.exports = (sequelize, DataTypes) => {
    const Cliente = sequelize.define(
        "Cliente",
        {
            id: {
                type: DataTypes.INTEGER,
                primaryKey: true,
                autoIncrement: true,
            },
            tipo: {
                type: DataTypes.STRING,
                allowNull: false,
            },
            nome: {
                type: DataTypes.STRING,
                allowNull: false,
            },
            nascimento: {
                type: DataTypes.DATEONLY,
                allowNull: false,
            },
            cep: {
                type: DataTypes.STRING,
                allowNull: false,
            },
            endereco: {
                type: DataTypes.STRING,
                allowNull: false,
            },
            numero: {
                type: DataTypes.STRING,
                allowNull: false,
            },
            cidade: {
                type: DataTypes.STRING,
                allowNull: false,
            },
            bairro: {
                type: DataTypes.STRING,
                allowNull: false,
            },
            complemento: {
                type: DataTypes.STRING,
                allowNull: true,
                defaultValue: null,
            },
            estado: {
                type: DataTypes.STRING,
                allowNull: false,
            },
            email: {
                type: DataTypes.STRING,
                allowNull: false,
                validate: {
                    isEmail: true,
                },
            },
            telefone: {
                type: DataTypes.STRING,
                allowNull: true,
            },
            cnpj: {
                type: DataTypes.STRING,
                allowNull: false,
            },
            natureza: {
                type: DataTypes.STRING,
                allowNull: false,
            },
            celular: {
                type: DataTypes.STRING,
                allowNull: false,
            },
            telefone_fixo: {
                type: DataTypes.STRING,
                allowNull: true,
            },
            uf: {
                type: DataTypes.STRING(2),
                allowNull: false,
                validate: {
                    len: [2, 2],
                },
            },
            data_nascimento: {
                type: DataTypes.DATEONLY,
                allowNull: false,
            },
        },
        {
            tableName: "clientes",
            timestamps: false,
            hooks: {
                beforeValidate: (cliente) => {
                    for (const key in cliente.dataValues) {
                        if (cliente[key] === "") {
                            cliente[key] = null;
                        }
                    }
                },
            },
        }
    );

    // Se precisar de associações, é aqui
    Cliente.associate = (models) => {
        // models.Produto.hasMany(models.Cliente), etc.
    };

    return Cliente;
};
