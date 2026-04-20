package br.edu.fateczl.tcc.controller;

import br.edu.fateczl.tcc.enums.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/enums")
public class EnumController {

    @GetMapping
    public ResponseEntity<Map<String, List<String>>> getAllEnums() {
        Map<String, List<String>> enums = new HashMap<>();
        enums.put("tecido", getValores(TecidoTraje.class));
        enums.put("cor", getValores(CorTraje.class));
        enums.put("estampa", getValores(EstampaTraje.class));
        enums.put("tipoTraje", getValores(TipoTraje.class));
        enums.put("tamanho", getValores(TamanhoTraje.class));
        enums.put("textura", getValores(TexturaTraje.class));
        enums.put("status", getValores(StatusTraje.class));
        enums.put("genero", getValores(SexoEnum.class));
        enums.put("condicao", getValores(CondicaoTraje.class));
        enums.put("statusAluguel", getValores(StatusAluguel.class));
        enums.put("ocasiao", getValores(TipoOcasiao.class));
        return ResponseEntity.ok(enums);
    }

    private <T extends Enum<T> & DisplayEnum> List<String> getValores(Class<T> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(DisplayEnum::getNomeExibicao)
                .collect(java.util.stream.Collectors.toList());
    }
}
