# Documentación Técnica: Microservicio de Plazoleta

Este documento detalla la arquitectura, diseño e implementación del microservicio de plazoleta (`plazoleta-service`). Este servicio es el núcleo operativo del sistema, encargado de la gestión de restaurantes, platos y el ciclo de vida completo de los pedidos.

## Arquitectura

El servicio implementa una **Arquitectura Hexagonal (Puertos y Adaptadores)**, garantizando la independencia del dominio frente a frameworks, bases de datos y servicios externos.

La estructura se divide en tres capas fundamentales:

1.  **Dominio (Domain)**: Lógica de negocio pura.
2.  **Aplicación (Application)**: Orquestación y transformación.
3.  **Infraestructura (Infrastructure)**: Implementación técnica y adaptadores.

---

## 1. Capa de Dominio (Domain)

Es el corazón del microservicio, libre de dependencias de frameworks. Aquí residen las reglas de negocio que gobiernan la operación de la plazoleta.

### Modelos
Entidades de negocio (POJOs) que modelan la realidad del sistema:
*   **Restaurant**: Información de los establecimientos (nombre, NIT, dirección, propietario).
*   **Dish**: Platos ofrecidos por los restaurantes (nombre, precio, descripción, categoría, estado activo/inactivo).
*   **Order**: Representa un pedido, su estado (Pendiente, En Preparación, Listo, Entregado, Cancelado) y el cliente asociado.
*   **OrderItem**: Detalle de los platos y cantidades dentro de un pedido.
*   **Traceability**: Modelo para el registro de eventos de trazabilidad.

### Puertos (Ports)
Interfaces que definen los contratos de interacción.
*   **API (Inbound Ports)**: Definen los servicios que el dominio expone.
    *   `IRestaurantServicePort`, `IDishServicePort`, `IOrderServicePort`: Operaciones CRUD y lógica de negocio específica (ej. paginación, cambios de estado).
    *   `IEfficiencyServicePort`: Cálculo de métricas de eficiencia.
*   **SPI (Outbound Ports)**: Contratos requeridos por el dominio para funcionar.
    *   **Persistencia**: `IRestaurantPersistencePort`, `IDishPersistencePort`, `IOrderPersistencePort`.
    *   **Servicios Externos**:
        *   `IUserValidationPort`: Validación de roles y usuarios (comunicación con Usuarios).
        *   `ITraceabilityPort`: Registro de cambios de estado de pedidos (comunicación con Trazabilidad).
        *   `INotificationPort`: Envío de notificaciones SMS (comunicación con Mensajería).
        *   `IClientInfoPort`: Obtención de información de clientes.

### Casos de Uso (Use Cases)
Implementan la lógica de negocio definida en los puertos API.
*   **RestaurantUseCase**: Creación de restaurantes, validando que el propietario exista y tenga el rol adecuado (mediante `IUserValidationPort`).
*   **DishUseCase**: Gestión de platos, validación de precios y vinculación con restaurantes.
*   **OrderUseCase**: El componente más complejo. Maneja la máquina de estados del pedido.
    *   Valida que el cliente no tenga pedidos activos.
    *   Calcula totales.
    *   Coordina la asignación de pedidos a empleados.
    *   Genera pines de seguridad para la entrega.
    *   Invoca puertos de trazabilidad y notificaciones en cada cambio de estado relevante.

---

## 2. Capa de Aplicación (Application)

Capa intermedia encargada de la orquestación de peticiones y conversión de datos.

### Handlers
Coordinadores de flujo (`RestaurantHandler`, `DishHandler`, `OrderHandler`).
*   Reciben DTOs de la capa de infraestructura.
*   Convierten DTOs a Modelos de Dominio usando Mappers.
*   Ejecutan la lógica a través de los Casos de Uso.
*   Retornan DTOs de respuesta.

### DTOs y Mappers
*   **DTOs**: Objetos de transferencia (Request/Response) que desacoplan la API pública del modelo interno.
*   **Mappers**: Interfaces de **MapStruct** para la transformación eficiente y segura entre DTOs y Modelos.

---

## 3. Capa de Infraestructura (Infrastructure)

Implementación concreta de los puertos y exposición de servicios.

### Input (Driving Adapters)
*   **Rest Controllers**: (`RestaurantRestController`, `DishRestController`, `OrderRestController`). Exponen la API RESTful. Manejan la validación de entrada (`@Valid`) y delegan a los Handlers.
    *   Incluyen endpoints para clientes (hacer pedido), empleados (listar pedidos, cambiar estado) y propietarios (crear platos).

### Output (Driven Adapters)
*   **JPA Adapters**: Implementación de la persistencia con MySQL.
    *   Uso de `Spring Data JPA` y Repositorios.
    *   Entidades JPA (`RestaurantEntity`, `DishEntity`, `OrderEntity`) separadas del modelo de dominio.
*   **Feign Clients (Comunicación entre Microservicios)**:
    *   El servicio actúa como cliente de otros microservicios usando **Spring Cloud OpenFeign**.
    *   **Usuarios Service**: Para validar existencia de usuarios y roles (Propietario, Empleado).
    *   **Trazabilidad Service**: Para enviar registros asíncronos de la evolución del pedido.
    *   **Mensajería Service**: Para solicitar el envío de SMS con el PIN de seguridad al cliente cuando el pedido está listo.

### Configuración y Seguridad
*   **BeanConfiguration**: Configuración manual de los Beans del dominio (Casos de Uso) inyectando las implementaciones concretas de los puertos (Adaptadores JPA y Feign). Esto mantiene el dominio puro.
*   **Security**: Implementación de filtros para validar el Token JWT en las peticiones, extrayendo el ID del usuario y su rol para autorizar operaciones (ej. solo un propietario puede crear platos).

## Decisiones de Diseño Relevantes

1.  **Comunicación Síncrona (Feign)**: Se utiliza Feign para validaciones críticas en tiempo real (ej. verificar si un usuario es propietario antes de crear un restaurante).
2.  **Máquina de Estados en Pedidos**: La lógica de transición de estados de un pedido (Pendiente -> En Preparación -> Listo -> Entregado) está centralizada en el dominio (`OrderUseCase`) para asegurar la integridad del proceso.
3.  **Separación de Responsabilidades**: El uso de puertos para servicios externos (`ITraceabilityPort`, `INotificationPort`) permite cambiar la implementación de comunicación (ej. pasar de Feign a RabbitMQ) sin tocar una sola línea de la lógica de negocio.
4.  **Inyección de Dependencias**: Uso estricto de inyección por constructor para favorecer la inmutabilidad y el testing.
