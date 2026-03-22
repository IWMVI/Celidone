package br.edu.fateczl.tcc.controller;

import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaRequest;
import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaResponse;
import br.edu.fateczl.tcc.dto.masculina.MedidaMasculinaRequest;
import br.edu.fateczl.tcc.dto.masculina.MedidaMasculinaResponse;
import br.edu.fateczl.tcc.service.MedidaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/medidas")
public class MedidaController {

    private final MedidaService medidaService;

    public MedidaController(MedidaService medidaService) {
        this.medidaService = medidaService;
    }


    // ===============================
    // CREATE - Medida FEMININA
    // ===============================
    @PostMapping("/feminina")
    public ResponseEntity<MedidaFemininaResponse> criarFeminina(
            @Valid @RequestBody MedidaFemininaRequest dto) {
        MedidaFemininaResponse response = medidaService.criarFeminina(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ===============================
    // CREATE - Medida MASCULINA
    // ===============================
    @PostMapping("/masculina")
    public ResponseEntity<MedidaMasculinaResponse> criarMasculina(
            @Valid @RequestBody MedidaMasculinaRequest dto) {
        MedidaMasculinaResponse response = medidaService.criarMasculina(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}