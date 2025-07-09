module.exports = [
    {
        files: ["**/*.js"],
        languageOptions: {
            ecmaVersion: 2021,
            sourceType: "module",
        },
        rules: {
            "no-unused-vars": "warn",
            eqeqeq: "error",
            "no-console": "off",
            semi: ["error", "always"],
            quotes: ["error", "double"],
        },
    },
];
