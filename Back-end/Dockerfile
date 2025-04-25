
FROM eclipse-temurin:21-jdk-alpine

# Diretório de trabalho dentro do container
WORKDIR /app

# Copia o JAR compilado para dentro da imagem
COPY target/*.jar app.jar

# Expõe a porta padrão do Spring Boot
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
