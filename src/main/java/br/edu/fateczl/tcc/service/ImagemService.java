package br.edu.fateczl.tcc.service;

import org.springframework.stereotype.Service;

import java.util.Base64;

/**
 * Serviço para gerenciar imagens de trajes.
 * Responsável por validação e processamento de imagens em base64.
 */
@Service
public class ImagemService {

    private static final int MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_FORMATS = { "image/jpeg", "image/png", "image/webp", "image/gif" };

    /**
     * Valida uma imagem em base64.
     *
     * @param imagemBase64 String com a imagem codificada em base64
     * @return true se a imagem é válida, false caso contrário
     */
    public boolean validarImagem(String imagemBase64) {
        if (imagemBase64 == null || imagemBase64.isBlank()) {
            return true; // Imagem é opcional
        }

        try {
            // Remove o prefixo data:image/...;base64, se existir
            String base64 = removerPrefixoDataUrl(imagemBase64);

            // Decodifica de base64 para validar
            byte[] decodedBytes = Base64.getDecoder().decode(base64);

            // Valida tamanho
            if (decodedBytes.length > MAX_IMAGE_SIZE) {
                return false;
            }

            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Remove o prefixo data URL de uma imagem em base64.
     *
     * @param imagemBase64 String com a imagem codificada em base64
     * @return String com a imagem sem o prefixo
     */
    public String removerPrefixoDataUrl(String imagemBase64) {
        if (imagemBase64 != null && imagemBase64.startsWith("data:")) {
            // Remove "data:image/...;base64,"
            int commaIndex = imagemBase64.indexOf(',');
            if (commaIndex > 0) {
                return imagemBase64.substring(commaIndex + 1);
            }
        }
        return imagemBase64;
    }

    /**
     * Obtém o tipo MIME de uma imagem em base64.
     *
     * @param imagemBase64 String com a imagem codificada em base64
     * @return String com o tipo MIME (ex: image/png)
     */
    public String extrairTipoMime(String imagemBase64) {
        if (imagemBase64 != null && imagemBase64.startsWith("data:")) {
            int separador = imagemBase64.indexOf(';');
            String tipo = imagemBase64.substring(5, separador > 0 ? separador : imagemBase64.indexOf(','));
            return tipo;
        }
        return "application/octet-stream";
    }

    /**
     * Valida o tamanho de uma imagem decodificada.
     *
     * @param imagemBase64 String com a imagem codificada em base64
     * @return true se o tamanho é válido, false caso contrário
     */
    public boolean validarTamanho(String imagemBase64) {
        if (imagemBase64 == null || imagemBase64.isBlank()) {
            return true;
        }

        try {
            String base64 = removerPrefixoDataUrl(imagemBase64);
            byte[] decodedBytes = Base64.getDecoder().decode(base64);
            return decodedBytes.length <= MAX_IMAGE_SIZE;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Obtém o tamanho da imagem em bytes.
     *
     * @param imagemBase64 String com a imagem codificada em base64
     * @return tamanho em bytes
     */
    public long obterTamanho(String imagemBase64) {
        if (imagemBase64 == null || imagemBase64.isBlank()) {
            return 0;
        }

        try {
            String base64 = removerPrefixoDataUrl(imagemBase64);
            byte[] decodedBytes = Base64.getDecoder().decode(base64);
            return decodedBytes.length;
        } catch (IllegalArgumentException e) {
            return 0;
        }
    }
}
