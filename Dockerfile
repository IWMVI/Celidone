FROM node:22-bookworm

# Instala dependências com tratamento robusto de erros
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    xvfb \
    libgtk-3-0 \
    libnss3 \
    libxss1 \
    libasound2 \
    libatk-bridge2.0-0 \
    libx11-xcb1 \
    libgbm1 \         
    libgl1-mesa-glx \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY package*.json ./
RUN npm install --production
COPY . .
ENV DISPLAY=:99
CMD ["sh", "-c", "Xvfb :99 -screen 0 1024x768x16 & exec npm start"]