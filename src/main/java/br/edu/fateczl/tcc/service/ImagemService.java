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

    public boolean validarImagem(String imagemBase64) {
        if (imagemBase64 == null || imagemBase64.isBlank()) {
            return true;
        }

        String tipoMime = extrairTipoMime(imagemBase64);
        if (!isFormatoPermitido(tipoMime)) {
            return false;
        }

        byte[] decodedBytes = decodificarBase64(imagemBase64);
        return decodedBytes != null && decodedBytes.length <= MAX_IMAGE_SIZE;
    }

    public String removerPrefixoDataUrl(String imagemBase64) {
        if (imagemBase64 != null && imagemBase64.startsWith("data:")) {
            int commaIndex = imagemBase64.indexOf(',');
            return imagemBase64.substring(commaIndex + 1);
        }
        return imagemBase64;
    }

    public String extrairTipoMime(String imagemBase64) {
        if (imagemBase64 != null && imagemBase64.startsWith("data:")) {
            int semicolonIndex = imagemBase64.indexOf(';');
            int commaIndex = imagemBase64.indexOf(',');

            if (semicolonIndex > 5) {
                return imagemBase64.substring(5, semicolonIndex);
            } else if (commaIndex > 5) {
                return imagemBase64.substring(5, commaIndex);
            }
        }
        return "application/octet-stream";
    }

    public boolean validarTamanho(String imagemBase64) {
        if (imagemBase64 == null || imagemBase64.isBlank()) {
            return true;
        }

        byte[] decodedBytes = decodificarBase64(imagemBase64);
        return decodedBytes != null && decodedBytes.length <= MAX_IMAGE_SIZE;
    }

    public long obterTamanho(String imagemBase64) {
        if (imagemBase64 == null) {
            return 0;
        }

        byte[] decodedBytes = decodificarBase64(imagemBase64);
        return decodedBytes != null ? decodedBytes.length : 0;
    }

    private boolean isFormatoPermitido(String mimeType) {
        for (String formatoPermitido : ALLOWED_FORMATS) {
            if (formatoPermitido.equalsIgnoreCase(mimeType)) {
                return true;
            }
        }
        return false;
    }

    private byte[] decodificarBase64(String imagemBase64) {
        try {
            String base64 = removerPrefixoDataUrl(imagemBase64);
            return Base64.getDecoder().decode(base64);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
