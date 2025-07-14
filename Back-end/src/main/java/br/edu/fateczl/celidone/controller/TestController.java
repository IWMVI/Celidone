package br.edu.fateczl.celidone.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller de teste para verificar se o back-end está funcionando
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Back-end funcionando corretamente");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    @GetMapping("/clientes/stats")
    public Map<String, Object> testStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalClientes", 0L);
        stats.put("clientesHoje", 0L);
        stats.put("clientesMes", 0L);
        stats.put("clientesAtivos", 0L);
        stats.put("clientesInativos", 0L);
        stats.put("novos7Dias", 0L);
        stats.put("clientesPessoaFisica", 0L);
        stats.put("clientesPessoaJuridica", 0L);
        stats.put("mediaIdade", 0.0);
        stats.put("cidadeMaisClientes", "N/A");
        stats.put("clientesCidadeMaisClientes", 0L);
        return stats;
    }
}