package ec.espe.hardware.controller;

import ec.espe.hardware.dto.CategoriaResumenDTO;
import ec.espe.hardware.dto.InventarioReporteDTO;
import ec.espe.hardware.service.HardwareService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/hardware")
@RequiredArgsConstructor
public class HardwareController {

    private final HardwareService hardwareService;

    @GetMapping("/reporte")
    public ResponseEntity<InventarioReporteDTO> obtenerReporte() {
        return ResponseEntity.ok(hardwareService.generarReporteCompleto());
    }

    @GetMapping("/imperativo")
    public ResponseEntity<List<CategoriaResumenDTO>> imperativo() {
        return ResponseEntity.ok(hardwareService.procesarImperativo());
    }

    @GetMapping("/funcional")
    public ResponseEntity<List<CategoriaResumenDTO>> funcional() {
        return ResponseEntity.ok(hardwareService.procesarFuncional());
    }
}