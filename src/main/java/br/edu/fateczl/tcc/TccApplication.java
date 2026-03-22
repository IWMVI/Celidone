package br.edu.fateczl.tcc;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TccApplication {

    private static final Logger log = LoggerFactory.getLogger(TccApplication.class);

    public static void main(String[] args) {
        carregarVariaveisDeAmbiente();
        
        SpringApplication.run(TccApplication.class, args);
    }

    private static void carregarVariaveisDeAmbiente() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .load();

            setarSePresente("DB_HOST", dotenv.get("DB_HOST", "localhost"));
            setarSePresente("DB_PORT", dotenv.get("DB_PORT", "3306"));
            setarSePresente("DB_NAME", dotenv.get("DB_NAME", "tcc"));
            setarSePresente("DB_USERNAME", dotenv.get("DB_USERNAME", "root"));
            setarSePresente("DB_PASSWORD", dotenv.get("DB_PASSWORD", ""));
            
            log.info("✅ Variáveis de ambiente carregadas do .env");
        } catch (Exception e) {
            log.warn("⚠️ Arquivo .env não encontrado ou erro ao carregar - usando valores padrão: {}", e.getMessage());
        }
    }

    private static void setarSePresente(String chave, String valor) {
        if (System.getProperty(chave) == null) {
            System.setProperty(chave, valor);
        }
    }
}
