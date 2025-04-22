# TCC

Projeto de Trabalho de Conclusão de Curso, utilizando Electron, Node.js e Docker para criação de uma aplicação desktop.

## Requisitos

- [Node.js](https://nodejs.org/en/) (versão 18 ou superior)
- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)

## Instalação

Clone o repositório:

```bash
git clone https://github.com/IWMVI/TCC.git
cd TCC
```

Instale as dependências:

```bash
npm install
```

## Configuração do Ambiente

Crie um arquivo `.env` na raiz do projeto, seguindo o exemplo abaixo:

```env
# .env
POSTGRES_USER=Wallace
POSTGRES_PASSWORD=P4ssw0rd
POSTGRES_DB=Tcc
```

> **Nota**: ajuste as variáveis conforme seu ambiente de desenvolvimento.

## Uso com Docker

Para rodar o ambiente completo usando Docker:

```bash
docker-compose up --build
```

Isso irá:

- Construir a imagem da aplicação
- Subir o container da aplicação e o container do PostreSQL

## Comandos Úteis

- **Rodar a aplicação localmente (sem Docker):**

```bash
npm start
```

- **Buildar a aplicação para produção:**

```bash
npm run build
```

- **Parar os containers Docker:**

```bash
docker-compose down
```

## Estrutura do Projeto

```
TCC/
├── src/
│   ├── public/         # Frontend da aplicação
│   └── ...             # Código principal
├── .env                # Variáveis de ambiente
├── Dockerfile          # Definição da imagem da aplicação
├── docker-compose.yml  # Orquestração dos containers
├── package.json        # Dependências e scripts
└── README.md           # Documentação
```

## Tecnologias Utilizadas

- Electron
- Node.js
- PostgreSQL (via Docker)
- Docker e Docker Compose
- HTML, CSS, JavaScript

## Contribuidores

- [Lucas Siqueira](https://github.com/LuquinhasDoJava)
- [Paulino Mendes](https://github.com/wastecoder)
- [Vitor Esteves](https://github.com/TheProtogen)
- [Wallace Martins](https://github.com/IWMVI)