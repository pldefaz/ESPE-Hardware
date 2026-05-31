package ec.espe.hardware.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventarioReporteDTO {

    private List<CategoriaResumenDTO> resumenPorCategoria;
    private long totalEquiposFiltrados;
    private String mensajeAI;
    private long tiempoImperativoMs;
    private long tiempoFuncionalMs;
}