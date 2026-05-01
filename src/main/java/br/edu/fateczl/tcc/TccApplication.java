package br.edu.fateczl.tcc;

import br.edu.fateczl.tcc.util.PortUtil;
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

        // Se porta 8080 estiver ocupada, tenta libertar e reiniciar
        try {
            PortUtil.ensurePortFree(8080);
        } catch (Exception e) {
            log.error("Não foi possível liberar porta 8080: {}", e.getMessage(), e);
            System.exit(1);
        }

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

            setarSePresente("SEEDER_ENABLED", dotenv.get("SEEDER_ENABLED", "false"));
            
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
