# Climalert - Sistema de Monitoreo Climático y Alertas

Este proyecto consiste en el diseño y desarrollo de **Climalert**, un sistema autónomo de monitoreo climático y envío automático de alertas por correo electrónico. El diseño arquitectónico está basado estrictamente en el estilo y patrones del repositorio de referencia `smartlife`, respetando los principios **SOLID/GRASP** y las pautas y alcance indicados por la cátedra de **Diseño de Sistemas de Información (Plan 23)** de la **UTN FRBA**.

---

## 🛠️ Tecnologías y Características Principales

*   **Lenguaje**: Java 21.
*   **Framework**: Spring Boot 4.1.0 (con soporte para `@Scheduled` y `RestClient` nativo).
*   **Desacoplamiento de Infraestructura (SOLID / Hexagonal)**: La lógica de negocio no conoce detalles de proveedores externos ni del canal de notificación. Se comunica mediante puertos (interfaces de dominio).
*   **Persistencia Mock en Memoria**: En cumplimiento con las restricciones académicas de la cátedra, **no se utiliza una base de datos relacional real** (JPA / PostgreSQL / H2). La persistencia histórica se simula en memoria utilizando colecciones sincronizadas (`ArrayList`) dentro de repositorios mock thread-safe.
*   **Evitación de Alertas Duplicadas**: La lógica de negocio implementa un flag de estado `analizado` en la medición de clima. Esto garantiza que cada registro meteorológico sea evaluado por el motor de alertas exactamente una vez, evitando el spam de notificaciones por correo electrónico.
*   **Envío de Mails**: Integración con `JavaMailSender` para notificar de forma automática a los destinatarios configurados en caso de condiciones críticas de clima.

---

## 📂 Estructura del Repositorio

La arquitectura del proyecto sigue el patrón de separación de responsabilidades y capas del repositorio de referencia:

```
ar.edu.utn.ba.ddsi.climalert
│
├── ClimalertApplication.java            # Clase principal de Spring Boot
│
├── config
│   └── WeatherApiConfig.java            # Configuración del cliente REST para WeatherAPI
│
├── dtos
│   └── weatherapi
│       └── WeatherApiResponse.java      # Modelado de respuesta de la API externa (Records Java 21)
│
├── exceptions
│   ├── BusinessException.java           # Excepción para reglas de negocio
│   └── ResourceNotFoundException.java   # Excepción para búsquedas fallidas
│
├── models
│   └── entities
│       ├── clima
│       │   └── RegistroClima.java       # Entidad de dominio (Medición del clima con lógica rica)
│       └── alerta
│           └── AlertaClimatica.java     # Entidad de dominio (Alerta generada y registrada)
│
├── repositories
│   ├── RegistroClimaRepository.java     # Puerto (Interfaz) para el repositorio de clima
│   ├── AlertaRepository.java            # Puerto (Interfaz) para el repositorio de alertas
│   └── inmemory
│       ├── InMemoryRegistroClimaRepository.java  # Adaptador (ArrayList en memoria thread-safe)
│       └── InMemoryAlertaRepository.java         # Adaptador (ArrayList en memoria thread-safe)
│
├── services
│   ├── ProveedorClima.java              # Puerto (Interfaz) para el proveedor de clima externo
│   ├── NotificacionService.java         # Puerto (Interfaz) para el canal de notificaciones
│   ├── MonitoreoClimaService.java       # Servicio de Aplicación (Orquestación del negocio)
│   └── impl
│       ├── MonitoreoClimaServiceImpl.java       # Implementación del flujo de negocio
│       └── EmailNotificacionServiceImpl.java    # Adaptador de infraestructura para envío de correo
│
├── adapters
│   └── weather
│       └── WeatherApiAdapter.java       # Adaptador de infraestructura para consulta HTTP REST a WeatherAPI
│
├── schedulers
│   └── ClimaScheduler.java              # Tareas autónomas programadas (Tarea A y Tarea B)
│
└── utils
    └── GeneradorIdSecuencial.java       # Utilidad thread-safe para generación de IDs en memoria
```

---

## ⚙️ Reglas de Negocio y Tareas Programadas

El sistema opera mediante dos tareas programadas independientes y autónomas:

1.  **Tarea A (Consulta y Registro)**: Se ejecuta **cada 5 minutos** (`fixedRate = 300000`). Consulta el clima actual para la ubicación fija parametrizada (ej. `Llavallol` o `CABA`) llamando a WeatherAPI, y guarda la medición con el estado `analizado = false`.
2.  **Tarea B (Procesamiento y Alerta)**: Se ejecuta **cada 1 minuto** (`fixedRate = 60000`). Analiza la última medición disponible.
    *   Si la temperatura supera los **35°C** y la humedad supera el **60%** de forma simultánea, y el registro no ha sido analizado previamente:
        *   Instancia un evento/entidad `AlertaClimatica`.
        *   Invoca al servicio de notificaciones por correo electrónico (`NotificacionService`).
        *   Persiste la alerta generada en el repositorio.
    *   Independientemente de si supera el umbral o no, el registro de clima se actualiza a `analizado = true` para evitar que vuelva a evaluarse en el siguiente minuto.

---

## 🚀 Desarrollo Local y Ejecución

### Requisitos Previos

*   Java Development Kit (JDK) 21 instalado.
*   Conexión a Internet (para realizar las llamadas al proveedor de clima externo y descargar dependencias).

### Configuración del Entorno (`application.yaml`)

Las propiedades de la aplicación se definen en [application.yaml](src/main/resources/application.yaml). Puedes configurar las variables de entorno o sobrescribir los valores directamente:

*   `weatherapi.api-key`: Clave API de WeatherAPI.
*   `weatherapi.ubicacion`: Ubicación a monitorear (por defecto, `Llavallol`).
*   `climalert.destinatarios`: Lista de correos separados por comas a los que se enviará la alerta (por defecto: `admin@clima.com,emergencies@clima.com,meteorologia@clima.com`).

#### Ejecutar con Variable de Entorno (Recomendado)
Para evitar guardar credenciales en el archivo YAML de configuración:

```bash
# En PowerShell (Windows)
$env:WEATHER_API_KEY="tu_api_key_aqui"
.\mvnw spring-boot:run

# En cmd (Windows)
set WEATHER_API_KEY=tu_api_key_aqui
.\mvnw spring-boot:run

# En Bash (Linux/macOS)
WEATHER_API_KEY=tu_api_key_aqui ./mvnw spring-boot:run
```

---

## 🧪 Pruebas y Verificación

### Pruebas Unitarias (`Mockito` y `JUnit 5`)

Se implementó una completa suite de pruebas unitarias que simula las dependencias externas (WeatherAPI y SMTP Mail) y verifica la lógica del sistema:
*   Evaluación correcta de umbrales críticos de clima (temperatura > 35°C y humedad > 60%).
*   Generación correcta de la entidad `AlertaClimatica`.
*   Asegurar que cada medición de clima sea procesada exactamente una vez (comportamiento del flag `analizado`).
*   Registro y manejo robusto de excepciones (el fallo al enviar correos no interrumpe el flujo principal).

Para ejecutar los tests de la suite:
```bash
.\mvnw clean test
```
