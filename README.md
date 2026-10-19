# SazónTotal

SazónTotal es una aplicación multiplataforma para apoyar la operación de un restaurante. Permite administrar el menú y el personal, registrar pedidos y seguir su preparación desde los perfiles de administrador, mesero y cocina.

## Funcionalidades

### Administrador

- Consultar los indicadores del día.
- Administrar empleados y su estado activo o inactivo.
- Crear, editar, activar y desactivar platos del menú.
- Consultar pedidos y anular pedidos que todavía están en curso.
- Consultar reportes de ventas y platos vendidos por período.

### Mesero

- Consultar pedidos asignados a su usuario e iniciar pedidos desde el menú.
- Seleccionar una mesa entre la 1 y la 15.
- Modificar cantidades y agregar notas a los platos antes de enviar el pedido.
- Consultar su historial de pedidos entregados.
- Consultar el estado de sus pedidos.

### Cocina

- Consultar los pedidos pendientes y listos.
- Revisar los platos, cantidades y notas de cada pedido.
- Marcar pedidos como listos para que el mesero pueda entregarlos.

El flujo normal de un pedido es: el mesero lo crea, cocina lo marca como listo y el mesero lo marca como entregado. Solo los pedidos entregados cuentan como ventas.

## Tecnologías y estructura

- Kotlin Multiplatform para compartir la lógica de la aplicación entre Android e iOS.
- Compose Multiplatform para las interfaces.
- Room con SQLite para la persistencia local.
- Coroutines y `Flow` para operaciones y observación de datos.

El código compartido se encuentra en `shared/src/commonMain`. Las implementaciones específicas de Android e iOS se encuentran en `shared/src/androidMain` y `shared/src/iosMain`. La aplicación Android está en `androidApp` y el proyecto de Xcode está en `iosApp/iosApp.xcodeproj`.

La base de datos se almacena localmente en cada dispositivo. Al iniciar por primera vez, la aplicación agrega empleados, platos y pedidos de ejemplo cuando las tablas correspondientes están vacías.

## Accesos de demostración

| Perfil | ID | PIN |
| --- | --- | --- |
| Administrador | `ADM-001` | `1234` |
| Mesero | `MSR-001` | `4321` |
| Cocina | `CCN-001` | `4321` |

## Ejecutar en Android

Abre el proyecto en Android Studio y ejecuta la configuración de la aplicación Android. También puedes generar e instalar la versión de depuración desde la raíz del proyecto:

```bash
./gradlew :androidApp:assembleDebug
./gradlew :androidApp:installDebug
```

## Ejecutar en iOS

Abre `iosApp/iosApp.xcodeproj` en Xcode, selecciona el esquema `iosApp` y ejecuta la aplicación en un simulador o dispositivo iOS configurado.

## Ejecutar las pruebas

Las pruebas compartidas se pueden ejecutar en el destino Android Host o en el simulador de iOS:

```bash
./gradlew :shared:testAndroidHostTest
./gradlew :shared:iosSimulatorArm64Test
```
