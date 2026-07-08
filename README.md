# Sistema de Monitoreo Climático “Climalert”

Este proyecto es una aplicación Java Spring Boot que simula el monitoreo de condiciones climáticas en una ubicación específica. Recopila datos del clima periódicamente y envía alertas por correo electrónico cuando las condiciones superan umbrales críticos predefinidos (temperatura y humedad).

## Características Principales

- **Monitoreo Automático**: Se ejecuta automáticamente cada 10 segundos (configurable) mediante programación (`@Scheduled`).
- **Integración con API Externa**: Utiliza la API [WeatherAPI](https://www.weatherapi.com/) para obtener datos del clima actual.
- **Gestión de Alertas**: Genera alertas cuando la temperatura es superior a 35°C y/o la humedad supera el 60%.
- **Notificaciones por Email**: Envía correos electrónicos con la información de la alerta a destinatarios configurados.
- **Persistencia de Datos**: Guarda los registros climáticos y las alertas en una base de datos H2 en memoria.
- **Testing con Mockito**: Incluye pruebas unitarias que simulan el comportamiento de los servicios externos.

---

## Requisitos Previos

- Java 21 o superior.
- Maven 3.6 o superior.

---

## Configuración

La configuración del sistema se gestiona a través del archivo `src/main/resources/application.yaml`.

### Propiedades de Configuración

| Propiedad | Descripción | Default |
|-----------|-------------|---------|
| `spring.mail.host` | Host del servidor de correo. | `localhost` |
| `spring.mail.port` | Puerto del servidor de correo. | `587` |
| `spring.mail.username` | Usuario del servidor de correo. | (vacío) |
| `spring.mail.password` | Contraseña del servidor de correo. | (vacío) |
| `spring.mail.properties.mail.smtp.auth` | Habilita autenticación SMTP. | `false` |
| `spring.mail.properties.mail.smtp.starttls.enable` | Habilita STARTTLS. | `false` |
| `weatherapi.url-base` | URL base de WeatherAPI. | `https://api.weatherapi.com/v1` |
| `weatherapi.api-key` | Clave API de WeatherAPI. | `key_dummy` |
| `weatherapi.ubicacion` | Ubicación para consultar el clima. | `Llavallol` |
| `climalert.destinatarios` | Lista de correos separados por coma. | (vacío) |

---

## Ejecución

1. **Compilar el proyecto**:

   ```bash
   mvn clean install
   ```

2. **Ejecutar la aplicación**:

   ```bash
   mvn spring-boot:run
   ```

La aplicación iniciará y comenzará a monitorear el clima cada 10 segundos. Puedes ver los logs en la consola.

---

## Ver Datos en la Consola

Para observar los datos que se están generando, puedes acceder al portal de Spring Boot Actuator:

1. Abre tu navegador y ve a: `http://localhost:8080/actuator`
2. Accede a los health checks: `http://localhost:8080/actuator/health`
3. Para ver los registros climáticos y alertas, necesitarás configurar el acceso a la base de datos H2:
   - Agrega las siguientes propiedades a `application.yaml` para habilitar la consola H2:
     ```yaml
     spring:
       h2:
         console:
           enabled: true
           path: /h2
     ```
   - Reinicia la aplicación.
   - Accede a: `http://localhost:8080/h2`
   - Driver: `org.h2.Driver`
   - URL: `jdbc:h2:~/test`
   - Usuario: `sa`
   - Contraseña: (vacía)

---

## Comandos Útiles

- **Detener la aplicación**: Presiona `Ctrl + C` en la terminal.

---

## Integraciones

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [WeatherAPI](https://www.weatherapi.com/)
- [H2 Database](https://www.h2database.com/)
- [Mockito](https://site.mockito.org/)
- [Lombok](https://projectlombok.org/)

---

