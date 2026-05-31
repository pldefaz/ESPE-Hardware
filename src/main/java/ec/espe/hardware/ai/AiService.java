package ec.espe.hardware.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class AiService {

    @Value("${gemini.api.key:}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generarResumenInventario(long totalEquipos, int categorias, String categoriaDestacada) {
        if (apiKey == null || apiKey.isBlank()) {
            return "Resumen AI no disponible: API Key no configurada";
        }

        String prompt = String.format(
                "Genera un resumen ejecutivo breve (máximo 2 oraciones) para el inventario " +
                        "de tecnología de laboratorios ESPE. Se procesaron %d equipos activos en %d categorías. " +
                        "La categoría con más valor es: %s. Sé profesional y conciso.",
                totalEquipos, categorias, categoriaDestacada);

        String geminiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key="
                + apiKey;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(Map.of("text", prompt)))));

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    geminiUrl, HttpMethod.POST, new HttpEntity<>(body, headers),
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    });

            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.get("candidates") instanceof List<?> candidates
                    && !candidates.isEmpty()) {
                if (candidates.get(0) instanceof Map<?, ?> candidateMap) {
                    if (candidateMap.get("content") instanceof Map<?, ?> contentMap) {
                        if (contentMap.get("parts") instanceof List<?> parts && !parts.isEmpty()) {
                            if (parts.get(0) instanceof Map<?, ?> partMap) {
                                return (String) partMap.get("text");
                            }
                        }
                    }
                }
            }
            return "Resumen AI no disponible (Gemini): Formato de respuesta inválido";
        } catch (Exception e) {
            return "Resumen AI no disponible (Gemini): " + e.getMessage();
        }
    }
}