package br.edu.fateczl.tcc.controller;

import br.edu.fateczl.tcc.domain.Traje;
import br.edu.fateczl.tcc.exception.ResourceNotFoundException;
import br.edu.fateczl.tcc.repository.TrajeRepository;
import br.edu.fateczl.tcc.service.ImagemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/trajes/imagem")
public class ImagemController {

    private final TrajeRepository trajeRepository;
    private final ImagemService imagemService;

    public ImagemController(TrajeRepository trajeRepository, ImagemService imagemService) {
        this.trajeRepository = trajeRepository;
        this.imagemService = imagemService;
    }

    @GetMapping
    public ResponseEntity<Map<String, String>> buscarImagem(@RequestParam("trajeId") Long trajeId) {
        Traje traje = trajeRepository.findById(trajeId)
                .orElseThrow(() -> new ResourceNotFoundException("Traje", trajeId));
        
        if (traje.getImagemUrl() == null || traje.getImagemUrl().isBlank()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(Map.of("imagemUrl", traje.getImagemUrl()));
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> adicionarImagem(
            @RequestParam("trajeId") Long trajeId,
            @RequestParam("imagem") MultipartFile file) throws IOException {
        
        Traje traje = trajeRepository.findById(trajeId)
                .orElseThrow(() -> new ResourceNotFoundException("Traje", trajeId));
        
        String imagemBase64 = converterParaBase64(file);
        
        if (!imagemService.validarImagem(imagemBase64)) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Imagem inválida ou muito grande (máximo 5MB)"));
        }
        
        traje.setImagemUrl(imagemBase64);
        trajeRepository.save(traje);
        
        return ResponseEntity.ok(Map.of("imagemUrl", traje.getImagemUrl()));
    }

    @PutMapping
    public ResponseEntity<Map<String, String>> atualizarImagem(
            @RequestParam("trajeId") Long trajeId,
            @RequestParam("imagem") MultipartFile file) throws IOException {
        
        return adicionarImagem(trajeId, file);
    }

    @DeleteMapping
    public ResponseEntity<Void> removerImagem(@RequestParam("trajeId") Long trajeId) {
        Traje traje = trajeRepository.findById(trajeId)
                .orElseThrow(() -> new ResourceNotFoundException("Traje", trajeId));
        
        traje.setImagemUrl(null);
        trajeRepository.save(traje);
        
        return ResponseEntity.noContent().build();
    }

    private String converterParaBase64(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        String base64 = Base64.getEncoder().encodeToString(bytes);
        String mimeType = file.getContentType();
        if (mimeType == null) {
            mimeType = "image/png";
        }
        return "data:" + mimeType + ";base64," + base64;
    }
}
