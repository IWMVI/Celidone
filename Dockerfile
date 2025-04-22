# Base com Node
FROM node:22:rapidfort/haproxy-latest-ib

RUN apt-get update && apt-get install -y \
    xvfb \
    libgtk-3-0 \
    libnss3 \
    libxss1 \
    libasound2 \
    libatk-bridge2.0-0 \
    libx11-xcb1 \
    && rm -rf /var/lib/apt/lists/*

# Definir diretório de trabalho
WORKDIR /app

# Limpar cache do npm antes de rodar o npm install
RUN npm cache clean --force

# Copiar apenas os arquivos de dependência primeiro
COPY package*.json ./

# Instalar as dependências
RUN npm ci

# Copiar o restante do código da aplicação
COPY . .

# Variável para "emular" tela para o Electron
ENV DISPLAY=:99

# Comando de inicialização
CMD ["npm", "start"]
