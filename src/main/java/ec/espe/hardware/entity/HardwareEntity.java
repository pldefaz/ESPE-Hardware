package ec.espe.hardware.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "hardware")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HardwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String modelo;

    @Enumerated(EnumType.STRING)
    private Categoria categoria;

    private BigDecimal precio;

    private LocalDate fechaCompra;

    @Enumerated(EnumType.STRING)
    private Estado estado;

    public enum Categoria {
        LAPTOP, PC, SERVIDOR
    }

    public enum Estado {
        ACTIVO, DEBAJA
    }
}