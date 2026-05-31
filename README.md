# UNIVERSIDAD DE LAS FUERZAS ARMADAS "ESPE"

## DEPARTAMENTO DE CIENCIAS DE LA COMPUTACIÓN

```
ESTUDIANTE:       PABLO LEONARDO DEFAZ AREQUIPA
ASIGNATURA:       PROGRAMACIÓN AVANZADA
NRC:              30405
NIVEL:            QUINTO NIVEL
PRÁCTICA:         Trabajo Autónomo
DOCENTE:          Ing. Paulo Cesar Galarza Sánchez
FECHA DE ENTREGA: 31-05-2026
```

---

# Proyecto ESPE-Hardware: Comparativa de Paradigmas de Programación

Este proyecto ha sido desarrollado como parte de la asignatura de Programación Avanzada del Departamento de Ciencias de la Computación de la ESPE. Su objetivo es gestionar el inventario de equipos tecnológicos de los laboratorios y realizar un análisis comparativo entre dos paradigmas de programación: **Imperativo** y **Funcional/Declarativo (Streams API)**.

---

## 1. Arquitectura del Proyecto

El proyecto sigue una arquitectura limpia y desacoplada en capas sobre **Spring Boot 3.4+**:

- **Capa de Entidad (`HardwareEntity`)**: Define el modelo de persistencia JPA que mapea la tabla `hardware`, gestionando las propiedades del equipo (modelo, precio, fecha de compra, categoría y estado).
- **Capa de Acceso a Datos (`HardwareRepository`)**: Interfaz JPA para realizar operaciones de base de datos de manera directa.
- **Capa de Negocio (`HardwareService`)**: Implementa el procesamiento algorítmico solicitado bajo ambos paradigmas y orquesta el cálculo de métricas.
- **Capa Web (`HardwareController`)**: Expone endpoints REST para visualizar los resultados de forma independiente o consolidada (`/api/hardware/reporte`, `/api/hardware/imperativo`, `/api/hardware/funcional`).
- **Capa AI (`AiService`)**: Integra los servicios de inteligencia artificial (API de Google Gemini) para generar un resumen ejecutivo dinámico sobre el estado actual del inventario.
- **Servicio de Carga (`DataLoaderService`)**: Inserta automáticamente un lote inicial de 10,000 registros para simular carga real y validar la eficiencia.

---

## 2. Análisis Comparativo de Paradigmas

A continuación, se detalla la comparativa técnica entre el enfoque Imperativo y el enfoque Funcional (Streams API) implementados en [HardwareService.java](src/main/java/ec/espe/hardware/service/HardwareService.java):

| Criterio                       | Paradigma Imperativo                                                                                                                                                                              | Paradigma Funcional / Declarativo                                                                                                                                                                |
| :----------------------------- | :------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | :----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Líneas de Código (LOC)**     | ~53 líneas de código.                                                                                                                                                                             | ~37 líneas de código (aproximadamente un 30% menos de verbosidad).                                                                                                                               |
| **Legibilidad**                | Más familiar para programadores tradicionales, pero requiere leer secuencialmente múltiples bucles `for`, condicionales `if-else` y declaraciones de mapas manuales para entender el flujo total. | Expresivo y centrado en el _qué_ en lugar del _cómo_. Permite comprender la lógica de negocio como una tubería (pipeline) fluida de transformaciones.                                            |
| **Complejidad Cognitiva**      | Alta. El desarrollador debe gestionar manualmente el estado intermedio (instanciar listas, manejar llaves en `HashMap`, actualizar acumuladores).                                                 | Baja a media. La lógica está encapsulada dentro de operadores de Spring/Java. Sin embargo, anidar múltiples colectores (`groupingBy` + `collectingAndThen`) requiere entender la API de Streams. |
| **Facilidad de Mantenimiento** | Baja. Añadir un nuevo filtro o métrica implica reestructurar bucles anidados o crear variables acumuladoras adicionales, lo que incrementa el riesgo de introducir bugs.                          | Alta. Extender la funcionalidad es tan sencillo como encadenar un nuevo `.filter()`, `.map()` o añadir campos al recolector final sin alterar la estructura general.                             |
| **Control de Errores e Hilos** | Propenso a errores de estado nulo (`NullPointerException`) o división por cero si la lista está vacía. La paralelización manual es compleja.                                                      | Seguro por diseño mediante el uso de `Optional` (por ejemplo, en `.max().orElseThrow()`). Soporta paralelización inmediata cambiando `.stream()` por `.parallelStream()`.                        |

---

## 3. Conclusiones

1. **Eficiencia en Mantenimiento:** El paradigma funcional reduce drásticamente el "código espagueti" y las variables mutables de estado que a menudo causan bugs de concurrencia o efectos secundarios indeseados.
2. **Productividad:** El uso de Streams API permite implementar filtros y agrupaciones complejas de forma compacta, acelerando el desarrollo de reportes analíticos de disponibilidad y valoración.
3. **Recomendación:** Se recomienda priorizar el paradigma declarativo para procesos de transformación y agregación de colecciones de datos por su robustez, facilidad de paralelización y mantenibilidad a largo plazo.

## 4. Cómo ejecutar (con XAMPP y phpMyAdmin)

### Paso 1: Levantar los servicios en XAMPP

1. Abrir el **XAMPP Control Panel**.
2. Presionar **Start** en el módulo de **MySQL** (y Apache si se desea acceder vía phpMyAdmin por navegador).
3. Asegurarse de que MySQL esté corriendo en el puerto por defecto (`3306`).

### Paso 2: Clonar y configurar la conexión de base de datos

1. Clonar el repositorio y entrar a la carpeta del proyecto:
   ```bash
   git clone https://github.com/pablog304/espe-hardware.git
   cd espe-hardware
   ```
2. Abrir el archivo [application.properties](src/main/resources/application.properties) y validar las credenciales de la base de datos de XAMPP (por defecto: `username=root` y `password=` vacío).
   _Nota: La base de datos `espe_hardware` se creará de forma automática al iniciar la aplicación gracias al parámetro `createDatabaseIfNotExist=true` en la URL de conexión._

### Paso 3: Ejecutar el servidor de Spring Boot

Se puede ejecutar la aplicación de dos formas:

#### Opción A: Desde IntelliJ IDEA (Recomendado)
1. Abrir **IntelliJ IDEA**.
2. Hacer clic en **Open** (o **Import**) y seleccionar la carpeta raíz del proyecto `espe-hardware`.
3. Esperar a que IntelliJ importe las dependencias de Maven de forma automática.
4. Navegar a `src/main/java/ec/espe/hardware/EspeHardwareApplication.java`.
5. Hacer clic derecho sobre la clase y seleccionar **Run 'EspeHardwareApplication'** (o usar el botón de play verde en el menú superior).

#### Opción B: Desde la consola (Terminal)
Ejecutar la aplicación desde la consola usando el wrapper incluido:
- **En Windows (CMD / PowerShell):**
  ```powershell
  ./mvnw spring-boot:run
  ```
- **En Linux / macOS:**
  ```bash
  ./mvnw spring-boot:run
  ```

---

## 5. Pruebas y Endpoints

Al iniciar la aplicación por primera vez, el cargador de datos ingresará automáticamente **10,000 registros** a la base de datos local en XAMPP. Se puede abrir [phpMyAdmin](http://localhost/phpmyadmin) y explorar la base de datos `espe_hardware` en la tabla `hardware` para ver los registros generados.

Para verificar la lógica algorítmica y los paradigmas, se abre el navegador o cliente REST en los siguientes endpoints:

1. **Reporte Consolidado (Tiempos de ejecución de ambos paradigmas + Mensaje AI):**
   [http://localhost:8080/api/hardware/reporte](http://localhost:8080/api/hardware/reporte)
2. **Resultado de Lógica Imperativa:**
   [http://localhost:8080/api/hardware/imperativo](http://localhost:8080/api/hardware/imperativo)
3. **Resultado de Lógica Funcional (Java Streams API):**
   [http://localhost:8080/api/hardware/funcional](http://localhost:8080/api/hardware/funcional)
