package ec.espe.hardware.service;

import ec.espe.hardware.entity.HardwareEntity;
import ec.espe.hardware.repository.HardwareRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoaderService implements CommandLineRunner {

    private final HardwareRepository hardwareRepository;
    private final Random random = new Random(42);

    @Override
    public void run(String... args) {
        if (hardwareRepository.count() > 0)
            return;

        log.info("Cargando 10,000 registros de hardware...");
        List<HardwareEntity> registros = new ArrayList<>();

        String[] modelosLaptop = { "Dell XPS 15", "HP EliteBook 840", "Lenovo ThinkPad X1", "MacBook Pro M3",
                "Asus ZenBook 14" };
        String[] modelosPC = { "Dell OptiPlex 7090", "HP ProDesk 400", "Lenovo ThinkCentre", "Acer Veriton",
                "Intel NUC 13" };
        String[] modelosServidor = { "Dell PowerEdge R750", "HP ProLiant DL380", "IBM Power10",
                "Lenovo ThinkSystem SR650", "Supermicro 1U" };

        HardwareEntity.Categoria[] categorias = HardwareEntity.Categoria.values();
        HardwareEntity.Estado[] estados = HardwareEntity.Estado.values();

        for (int i = 0; i < 10_000; i++) {
            HardwareEntity.Categoria cat = categorias[random.nextInt(3)];
            String modelo = switch (cat) {
                case LAPTOP -> modelosLaptop[random.nextInt(modelosLaptop.length)];
                case PC -> modelosPC[random.nextInt(modelosPC.length)];
                case SERVIDOR -> modelosServidor[random.nextInt(modelosServidor.length)];
            };

            long diasAtras = 180 + random.nextInt(2920);
            LocalDate fecha = LocalDate.now().minusDays(diasAtras);

            BigDecimal precio = switch (cat) {
                case LAPTOP -> BigDecimal.valueOf(600 + random.nextInt(2400));
                case PC -> BigDecimal.valueOf(400 + random.nextInt(1600));
                case SERVIDOR -> BigDecimal.valueOf(2000 + random.nextInt(18000));
            };

            registros.add(HardwareEntity.builder()
                    .modelo(modelo + " #" + (i + 1))
                    .categoria(cat)
                    .precio(precio)
                    .fechaCompra(fecha)
                    .estado(estados[random.nextInt(2)])
                    .build());
        }

        hardwareRepository.saveAll(registros);
        log.info("10,000 registros cargados correctamente.");
    }
}