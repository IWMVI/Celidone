package br.edu.fateczl.tcc.controller;

import br.edu.fateczl.tcc.enums.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/enums")
public class EnumController {

    @GetMapping
    public ResponseEntity<Map<String, List<String>>> getAllEnums() {
        return ResponseEntity.ok(Map.of(
                "tecido", getValores(TecidoTraje.class),
                "cor", getValores(CorTraje.class),
                "estampa", getValores(EstampaTraje.class),
                "tipoTraje", getValores(TipoTraje.class),
                "tamanho", getValores(TamanhoTraje.class),
                "textura", getValores(TexturaTraje.class),
                "status", getValores(StatusTraje.class),
                "genero", getValores(SexoEnum.class),
                "condicao", getValores(CondicaoTraje.class)
        ));
    }

    private <T extends Enum<T> & DisplayEnum> List<String> getValores(Class<T> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(DisplayEnum::getNomeExibicao)
                .collect(java.util.stream.Collectors.toList());
    }
}
