# Gestión de Pedidos API

API REST para gestionar productos y pedidos de un comercio, desarrollada con Spring Boot.

## Tecnologías

- Java 21 y Spring Boot 3.5
- Spring Data JPA + MySQL
- Spring Security (HTTP Basic + BCrypt, roles ADMIN y CLIENTE)
- Spring AI con Groq (generación de mensajes para el cliente)
- Swagger / OpenAPI
- JUnit 5 + Mockito
- Docker y Docker Compose

## Funcionalidades

- Registro de clientes con contraseña encriptada
- CRUD de productos (escritura solo para ADMIN) con baja lógica
- Creación de pedidos con validación y descuento de stock en una transacción
- Cambio de estado de pedidos; al cancelar se devuelve el stock
- Cada cliente ve solo sus pedidos
- Mensaje para el cliente generado con IA, con respuesta por defecto si la IA no está disponible
- Manejo global de errores con respuestas JSON uniformes

## Cómo ejecutarlo

### Con Docker

```bash
docker compose up --build
```

### Local

1. Crear la base de datos `gestor_pedidos` en MySQL.
2. Crear `src/main/resources/application-local.properties`:
   ```properties
   spring.datasource.password=TU_CONTRASEÑA
   spring.ai.openai.api-key=TU_API_KEY_DE_GROQ
   ```
3. Ejecutar `mvn spring-boot:run`.

## Documentación

Swagger: http://localhost:8080/swagger-ui.html

Usuario admin de prueba: `admin@gestion.com` / `admin123`

## Endpoints

| Método | Ruta | Acceso |
|---|---|---|
| POST | /api/auth/registro | Público |
| GET | /api/productos | Público |
| GET | /api/productos/{id} | Público |
| POST | /api/productos | ADMIN |
| PUT | /api/productos/{id} | ADMIN |
| DELETE | /api/productos/{id} | ADMIN |
| POST | /api/pedidos | Autenticado |
| GET | /api/pedidos/mis-pedidos | Autenticado |
| GET | /api/pedidos/{id} | Dueño o ADMIN |
| GET | /api/pedidos | ADMIN |
| PATCH | /api/pedidos/{id}/estado | ADMIN |
| GET | /api/pedidos/{id}/mensaje-ia | Dueño o ADMIN |

## Pruebas

- Tests unitarios: `mvn test`
- Colección de Postman en la carpeta `postman/`

## Autor

Juan Sosa — [GitHub](https://github.com/jmemmanuelsosa-tech)