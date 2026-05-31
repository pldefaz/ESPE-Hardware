package ec.espe.hardware.service;

import ec.espe.hardware.ai.AiService;
import ec.espe.hardware.dto.CategoriaResumenDTO;
import ec.espe.hardware.dto.InventarioReporteDTO;
import ec.espe.hardware.entity.HardwareEntity;
import ec.espe.hardware.repository.HardwareRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HardwareService {

    private final HardwareRepository hardwareRepository;
    private final AiService aiService;

    // =========================================================
    // PARADIGMA IMPERATIVO
    // =========================================================
    public List<CategoriaResumenDTO> procesarImperativo() {
        List<HardwareEntity> todos = hardwareRepository.findAll();
        LocalDate hace5Anios = LocalDate.now().minusYears(5);

        // Paso 1: Filtrar manualmente
        List<HardwareEntity> filtrados = new ArrayList<>();
        for (HardwareEntity hw : todos) {
            if (hw.getEstado() == HardwareEntity.Estado.ACTIVO
                    && hw.getFechaCompra().isAfter(hace5Anios)) {
                filtrados.add(hw);
            }
        }

        // Paso 2: Agrupar por categoría usando Map
        Map<HardwareEntity.Categoria, List<HardwareEntity>> agrupados = new HashMap<>();
        for (HardwareEntity hw : filtrados) {
            HardwareEntity.Categoria cat = hw.getCategoria();
            if (!agrupados.containsKey(cat)) {
                agrupados.put(cat, new ArrayList<>());
            }
            agrupados.get(cat).add(hw);
        }

        // Paso 3: Calcular métricas por categoría
        List<CategoriaResumenDTO> resultado = new ArrayList<>();
        for (Map.Entry<HardwareEntity.Categoria, List<HardwareEntity>> entry : agrupados.entrySet()) {
            List<HardwareEntity> equipos = entry.getValue();

            // Acumuladores manuales
            BigDecimal sumaTotal = BigDecimal.ZERO;
            HardwareEntity masCaro = equipos.get(0);

            for (HardwareEntity hw : equipos) {
                sumaTotal = sumaTotal.add(hw.getPrecio());
                if (hw.getPrecio().compareTo(masCaro.getPrecio()) > 0) {
                    masCaro = hw;
                }
            }

            BigDecimal promedio = sumaTotal.divide(
                    BigDecimal.valueOf(equipos.size()), 2, RoundingMode.HALF_UP);

            resultado.add(CategoriaResumenDTO.builder()
                    .categoria(entry.getKey())
                    .totalEquipos(equipos.size())
                    .valorTotal(sumaTotal)
                    .promedioPrecio(promedio)
                    .equipoMasCaroModelo(masCaro.getModelo())
                    .equipoMasCaroPrecio(masCaro.getPrecio())
                    .build());
        }

        return resultado;
    }

    // =========================================================
    // PARADIGMA FUNCIONAL / DECLARATIVO (Streams API)
    // =========================================================
    public List<CategoriaResumenDTO> procesarFuncional() {
        LocalDate hace5Anios = LocalDate.now().minusYears(5);

        return hardwareRepository.findAll().stream()
                // Paso 1: Filtrar con predicados
                .filter(hw -> hw.getEstado() == HardwareEntity.Estado.ACTIVO)
                .filter(hw -> hw.getFechaCompra().isAfter(hace5Anios))

                // Paso 2 y 3: Agrupar y calcular con Collectors
                .collect(Collectors.groupingBy(
                        HardwareEntity::getCategoria,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                equipos -> {
                                    DoubleSummaryStatistics stats = equipos.stream()
                                            .mapToDouble(hw -> hw.getPrecio().doubleValue())
                                            .summaryStatistics();

                                    BigDecimal total = equipos.stream()
                                            .map(HardwareEntity::getPrecio)
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                                    // Equipo más caro con Optional
                                    HardwareEntity masCaro = equipos.stream()
                                            .max(Comparator.comparing(HardwareEntity::getPrecio))
                                            .orElseThrow();

                                    return CategoriaResumenDTO.builder()
                                            .categoria(equipos.get(0).getCategoria())
                                            .totalEquipos(equipos.size())
                                            .valorTotal(total)
                                            .promedioPrecio(BigDecimal.valueOf(stats.getAverage())
                                                    .setScale(2, RoundingMode.HALF_UP))
                                            .equipoMasCaroModelo(masCaro.getModelo())
                                            .equipoMasCaroPrecio(masCaro.getPrecio())
                                            .build();
                                })))
                .values().stream()
                .sorted(Comparator.comparing(CategoriaResumenDTO::getValorTotal).reversed())
                .collect(Collectors.toList());
    }

    // =========================================================
    // REPORTE COMPLETO (combina ambos + AI)
    // =========================================================
    public InventarioReporteDTO generarReporteCompleto() {
        // Medir tiempo imperativo
        long t1 = System.currentTimeMillis();
        procesarImperativo();
        long tiempoImp = System.currentTimeMillis() - t1;

        // Medir tiempo funcional
        long t2 = System.currentTimeMillis();
        List<CategoriaResumenDTO> resultadoFuncional = procesarFuncional();
        long tiempoFunc = System.currentTimeMillis() - t2;

        long totalEquipos = resultadoFuncional.stream()
                .mapToLong(CategoriaResumenDTO::getTotalEquipos).sum();

        String categoriaDestacada = resultadoFuncional.isEmpty() ? "N/A"
                : resultadoFuncional.get(0).getCategoria().name();

        String mensajeAI = aiService.generarResumenInventario(
                totalEquipos, resultadoFuncional.size(), categoriaDestacada);

        return InventarioReporteDTO.builder()
                .resumenPorCategoria(resultadoFuncional)
                .totalEquiposFiltrados(totalEquipos)
                .mensajeAI(mensajeAI)
                .tiempoImperativoMs(tiempoImp)
                .tiempoFuncionalMs(tiempoFunc)
                .build();
    }
}