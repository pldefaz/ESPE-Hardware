package ec.espe.hardware.dto;

import ec.espe.hardware.entity.HardwareEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaResumenDTO {

    private HardwareEntity.Categoria categoria;
    private long totalEquipos;
    private BigDecimal valorTotal;
    private BigDecimal promedioPrecio;
    private String equipoMasCaroModelo;
    private BigDecimal equipoMasCaroPrecio;
}