package br.edu.fateczl.tcc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.ServerSocket;

public final class PortUtil {

    private static final Logger log = LoggerFactory.getLogger(PortUtil.class);

    private PortUtil() { }

    public static void ensurePortFree(int port) {
        if (isPortFree(port)) {
            log.info("Porta {} está livre", port);
            return;
        }

        log.warn("Porta {} está sendo usada. Tentando liberar...", port);
        if (killProcessOnPort(port)) {
            log.info("Porta {} liberada com sucesso", port);
            if (!waitForPortFree(port, 5_000)) {
                throw new IllegalStateException("Porta " + port + " ainda ocupada após tentativa de liberação");
            }
            return;
        }

        throw new IllegalStateException("Não foi possível liberar a porta " + port);
    }

    private static boolean isPortFree(int port) {
        try (ServerSocket ignored = new ServerSocket(port)) {
            return true;
        } catch (BindException e) {
            return false;
        } catch (IOException e) {
            log.error("Erro ao verificar porta {}: {}", port, e.getMessage(), e);
            return false;
        }
    }

    private static boolean waitForPortFree(int port, long timeoutMillis) {
        long deadline = System.currentTimeMillis() + timeoutMillis;
        while (System.currentTimeMillis() < deadline) {
            if (isPortFree(port)) {
                return true;
            }
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return false;
    }

    private static boolean killProcessOnPort(int port) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return killOnWindows(port);
        } else {
            return killOnUnix(port);
        }
    }

    private static boolean killOnWindows(int port) {
        String[] findPid = {"cmd", "/c", "netstat -ano | findstr :" + port};
        String output = runCommand(findPid);

        if (output == null || output.isBlank()) {
            log.warn("Não foi possível achar PID na porta {} no Windows", port);
            return false;
        }

        boolean killedAny = false;
        for (String line : output.split("\\r?\\n")) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }

            String[] parts = line.split("\\s+");
            if (parts.length < 5) {
                continue;
            }

            String pid = parts[parts.length - 1];
            if (!pid.matches("\\d+")) {
                continue;
            }

            log.info("Matando PID {} que ocupa a porta {}", pid, port);
            String[] kill = {"cmd", "/c", "taskkill /F /PID " + pid};
            String killOutput = runCommand(kill);
            if (killOutput != null) {
                killedAny = true;
            }
        }

        if (!killedAny) {
            log.warn("Não foi possível executar taskkill na porta {}", port);
            return false;
        }

        // Valida a porta depois da tentativa de liberação
        return isPortFree(port);
    }

    private static boolean killOnUnix(int port) {
        String[] findPid = {"sh", "-c", "lsof -ti tcp:" + port};
        String pid = runCommandAndExtractFirst(findPid);
        if (pid == null) {
            log.warn("Não foi possível achar PID na porta {} no Unix", port);
            return false;
        }
        String[] kill = {"sh", "-c", "kill -9 " + pid};
        String result = runCommand(kill);
        return result != null;
    }

    private static String runCommandAndExtractFirst(String[] command) {
        String output = runCommand(command);
        if (output == null || output.isBlank()) {
            return null;
        }
        return output.split("\\R")[0].trim();
    }

    private static String runCommand(String[] command) {
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.redirectErrorStream(true);
        try {
            Process process = builder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line).append(System.lineSeparator());
                }
                int exit = process.waitFor();
                if (exit != 0) {
                    log.warn("Comando {} retornou código {}", String.join(" ", command), exit);
                }
                return result.toString().trim();
            }
        } catch (IOException | InterruptedException e) {
            log.error("Erro ao executar comando {}: {}", String.join(" ", command), e.getMessage(), e);
            Thread.currentThread().interrupt();
            return null;
        }
    }
}
