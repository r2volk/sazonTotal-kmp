# SazónTotal

Aplicación multiplataforma para la operación de un restaurante. Permite administrar empleados y menú, registrar pedidos, seguir su preparación y consultar ventas. La interfaz y la lógica principal están compartidas entre Android e iOS.

Este documento sirve como guía del proyecto y como contexto para cualquier persona o asistente de IA que vaya a leerlo o modificarlo. Describe el comportamiento actual y distingue las decisiones intencionales de las funciones pendientes.

## Índice

1. [Alcance del proyecto](#alcance-del-proyecto)
2. [Tecnologías y configuración](#tecnologías-y-configuración)
3. [Estructura del código](#estructura-del-código)
4. [Perfiles y navegación](#perfiles-y-navegación)
5. [Flujo de pedidos](#flujo-de-pedidos)
6. [Reglas de negocio](#reglas-de-negocio)
7. [Persistencia y datos de demostración](#persistencia-y-datos-de-demostración)
8. [Accesos de demostración](#accesos-de-demostración)
9. [Compilar y ejecutar](#compilar-y-ejecutar)
10. [Pruebas](#pruebas)
11. [Decisiones vigentes y limitaciones conocidas](#decisiones-vigentes-y-limitaciones-conocidas)
12. [Orientación para futuros cambios](#orientación-para-futuros-cambios)

## Alcance del proyecto

SazónTotal representa un sistema de atención de restaurante con tres perfiles:

- **Administrador:** gestiona empleados, menú, pedidos e informes.
- **Mesero:** selecciona una mesa, prepara pedidos, los envía a cocina y consulta su historial.
- **Cocina:** revisa los pedidos pendientes y marca cuándo están listos.

El proyecto prioriza que el flujo se pueda demostrar y utilizar desde la interfaz. No depende de un servidor web ni de una API remota: los datos se guardan localmente mediante Room y SQLite en cada instalación.

## Tecnologías y configuración

| Componente | Configuración actual |
| --- | --- |
| Lenguaje | Kotlin Multiplatform, Kotlin `2.4.10` |
| Interfaz | Compose Multiplatform `1.11.1` |
| Material 3 | `1.11.0-alpha07` |
| Persistencia | Room `2.7.1` y SQLite Bundled `2.5.0` |
| Generación de código Room | KSP `2.3.10` |
| Android Gradle Plugin | `9.4.1` |
| Android compile SDK | `36` |
| Android target SDK | `36` |
| Android min SDK | `24` |
| Target JVM del módulo compartido Android | `11` |

Las versiones están centralizadas en `gradle/libs.versions.toml`. Evitar duplicar o fijar versiones en otros archivos sin una razón concreta.

El módulo `shared` genera el framework estático `Shared` para `iosArm64` y `iosSimulatorArm64`. El código de interfaz común se ejecuta dentro de Compose Multiplatform en ambas plataformas.

## Estructura del código

```text
.
├── androidApp/                         # Aplicación y punto de entrada Android
├── iosApp/iosApp.xcodeproj/             # Proyecto de Xcode
├── shared/
│   └── src/
│       ├── commonMain/kotlin/           # Interfaz, lógica y modelos compartidos
│       ├── androidMain/kotlin/          # Implementaciones específicas de Android
│       ├── iosMain/kotlin/              # Implementaciones específicas de iOS
│       └── commonTest/kotlin/            # Pruebas de lógica común
└── gradle/libs.versions.toml             # Catálogo central de versiones
```

Dentro de `shared/src/commonMain/kotlin/org/sazontotal/project/`:

| Ruta | Responsabilidad |
| --- | --- |
| `App.kt` | Crea la base y el repositorio; conserva sesión, ruta actual, mesa y carrito; conecta las pantallas. |
| `data/db/` | Entidades Room, DAO, base de datos y factories de plataforma. |
| `data/repository/SazonRepository.kt` | Acceso a datos, validaciones de negocio, cambios de estado y seed. |
| `data/CarritoCompra.kt` | Cantidades, notas, snapshots de platos y total compartido entre menú y carrito. |
| `screens/` | Dashboards y pantallas de cada perfil. |
| `screens/screensAdmin/` | Informes, pedidos, personal y edición del menú. |
| `screens/screensMesero/carrito/` | Menú, carrito y confirmación/ticket del pedido. |
| `screens/screensCocina/` | Cola de cocina y perfil. |
| `components/` | Componentes Compose reutilizados por varias pantallas. |
| `enums/` | Estados representados como enums para la interfaz. |
| `utils/` | Moneda, fecha/hora y abstracciones de navegación de plataforma. |

El nombre del paquete Kotlin común es `org.sazontotal.project`.

### Puntos de entrada por plataforma

- Android inicia en `androidApp/src/main/kotlin/org/sazontotal/project/MainActivity.kt`. Inicializa el contexto Android requerido por Room y presenta `App()`.
- iOS inicia en `iosApp/iosApp/iOSApp.swift`; `ContentView.swift` hospeda `MainViewController()` desde `shared/src/iosMain/kotlin/org/sazontotal/project/MainViewController.kt`.
- Room, fecha/hora y el comportamiento de retroceso usan declaraciones `expect` en código común y sus implementaciones `actual` en `androidMain` e `iosMain`.

La interfaz no usa una capa ViewModel separada: `App.kt` conserva el estado global con Compose, cada pantalla mantiene su estado visual local y `SazonRepository` centraliza las operaciones de datos y las validaciones principales.

## Perfiles y navegación

El punto de coordinación de la aplicación es `App.kt`. La navegación se mantiene con una cadena en `pantallaActual`; las rutas actuales son:

| Ruta | Pantalla |
| --- | --- |
| `login` | Inicio de sesión |
| `dashboardAdmin` | Dashboard de administrador |
| `gestionPersonal` | Gestión del personal |
| `editarMenu` | Menú de administración |
| `todosLosPedidos` | Pedidos de administración |
| `reportesAdmin` | Reportes |
| `dashboardMesero` | Dashboard de mesero |
| `menuMesero` | Menú para tomar pedidos |
| `carritoMesero` | Carrito y selección de mesa |
| `pedidoConcretado` | Confirmación y ticket |
| `dashboardCocina` | Dashboard de cocina |
| `asistente` | Maqueta visual del asistente |
| `cambioPin` | Cambio de PIN |

El ID determina el perfil al entrar:

- `ADM-...` abre el dashboard de administrador.
- `MSR-...` abre el dashboard de mesero.
- `CCN-...` abre el dashboard de cocina.

El campo `rol` de empleado se usa para mostrar y filtrar la información; la ruta inicial se determina por el prefijo del ID.

Los dashboards de mesero y cocina usan pestañas. El de mesero tiene **Pedidos**, **Historial** y **Perfil**; el de cocina tiene **Pedidos** y **Perfil**.

`App.kt` conserva la sesión actual, la ruta de pantalla, la mesa seleccionada y el carrito mediante estado Compose (`remember`). Los pedidos confirmados, en cambio, se persisten en Room. Si se cierra el proceso de la aplicación, no se conserva como pedido un carrito que todavía no se haya confirmado.

## Flujo de pedidos

1. El mesero agrega platos y cantidades desde `MenuScreen`.
2. El carrito conserva el detalle de los platos seleccionados, la cantidad, las notas y el precio de ese momento. Esto evita que el pedido cambie si el plato se edita o se elimina del menú mientras está en el carrito.
3. Se selecciona una mesa del **1 al 15**. La selección por defecto es la mesa `4` y se comparte entre menú y carrito.
4. Al confirmar se crea una cabecera `PedidoEntity` con el ID del mesero, la mesa, el total, la fecha y estado `PENDIENTE`; también se guardan sus `PedidoItemEntity`.
5. Cocina ve los pedidos pendientes y cambia su estado a `LISTO`.
6. El mesero marca el pedido `ENTREGADO`. Los pedidos entregados aparecen en su Historial.

El botón de confirmación queda inactivo si el carrito está vacío, el repositorio o el menú no están listos, la mesa no es válida o alguno de los platos se desactivó. El carrito conserva una copia del plato para seguir mostrándolo y permitir quitarlo. El repositorio vuelve a comprobar que haya renglones válidos y que los platos sigan activos antes de insertar.

El guardado inserta primero la cabecera y luego sus renglones. Si falla la inserción de renglones, el repositorio intenta borrar la cabecera para no dejar un pedido incompleto; Room no expone aquí una transacción multiplataforma común.

## Reglas de negocio

### Empleados

- El PIN debe tener exactamente cuatro dígitos ASCII (`0`–`9`). La regla se aplica en la interfaz y en `SazonRepository`.
- Solo un empleado con `activo = true` puede iniciar sesión.
- El estado activo/inactivo se guarda en la columna `empleados.activo`.
- No se permite desactivar al último administrador activo.
- Los IDs nuevos usan los prefijos `ADM-`, `MSR-` y `CCN-`. Un ID duplicado no debe reemplazar otro empleado.

### Estados de pedido

Los estados guardados en Room son strings y sus valores válidos son:

| Estado | Significado |
| --- | --- |
| `PENDIENTE` | Pedido enviado y pendiente de preparación. |
| `LISTO` | Cocina terminó; espera la entrega del mesero. |
| `ENTREGADO` | Pedido servido al cliente. Cuenta como venta. |
| `ANULADO` | Pedido anulado por administración. No cuenta como venta. |

Flujo habitual: `PENDIENTE` → `LISTO` → `ENTREGADO`. Administración puede anular pedidos que todavía no estén entregados ni anulados.

### Precios e IGV

- `PlatoEntity.precio` representa el precio final, **con IGV incluido**.
- El total de una comanda es la suma de `precio × cantidad`; no se vuelve a agregar 18 %.
- El ticket calcula el valor sin IGV como `total / 1.18` y el IGV incluido como `total - valor sin IGV`.
- Los precios y totales se representan como `Double` y se muestran con dos decimales mediante `utils/Moneda.kt`.
- Los informes cuentan como venta solo pedidos `ENTREGADO`.

### Reportes y fechas

- **Semana:** ventana móvil de los últimos siete días.
- **Mes:** ventana móvil de 28 días.
- **Año:** año calendario local, de enero a diciembre.
- El top de platos se calcula para el período seleccionado, igual que el resto del reporte.
- Las fechas se interpretan en la zona horaria local mediante las implementaciones de `utils/Reloj.kt` para Android e iOS.

### Notas de comanda

El modelo guarda `nota` en cada renglón del pedido. La tarjeta de cocina presenta una nota visible por pedido, que se considera la nota general de la comanda. Mantener este comportamiento; no convertirlo en una lista de notas separadas sin confirmación.

## Persistencia y datos de demostración

La base Room es `SazonDatabase`, versión `7`, con cuatro entidades:

| Entidad | Tabla | Datos principales |
| --- | --- | --- |
| `EmpleadoEntity` | `empleados` | ID, nombre, PIN, rol, teléfono y estado activo. |
| `PlatoEntity` | `platos` | Nombre, descripción, precio final, categoría, disponibilidad y etiquetas. |
| `PedidoEntity` | `pedidos` | Mesa, mesero, estado, total y fecha. |
| `PedidoItemEntity` | `pedido_items` | Plato, nombre y precio guardados al ordenar, cantidad y nota. |

Los DAO están en `data/db/`; `SazonRepository` es la capa usada por las pantallas para consultar y modificar datos. Las pantallas observan datos mediante `Flow` y `collectAsState`.

El seed de `SazonRepository.seedIfEmpty()` crea estos empleados si sus IDs no existen:

| ID | Nombre | Rol | PIN |
| --- | --- | --- | --- |
| `ADM-001` | Karina Castillo | Administrador | `1234` |
| `MSR-001` | Ricardo Sanchez | Mesero | `4321` |
| `CCN-001` | Joaquin Gutierrez | Cocina | `4321` |

Si la tabla de platos está vacía, crea cuatro platos de demostración: Ceviche clásico, Lomo saltado, Pisco sour y Suspiro limeño. Los pedidos de ejemplo se crean solo si no hay pedidos. Incluyen:

- Tres pedidos entregados: Mesa 2 (hace 10 días), Mesa 3 (hace 40 minutos) y Mesa 7 (hace 25 minutos).
- Un pedido `LISTO`: Mesa 5 (hace 12 minutos).
- Un pedido `PENDIENTE`: Mesa 1 (hace 3 minutos).

Los seeds no sustituyen los datos existentes. Cambiar su contenido no modifica automáticamente una base local ya creada.

El inicio de sesión compara ID y PIN y además exige `activo = true`. La pantalla determina el perfil por el prefijo del ID. Si un empleado se desactiva, el cambio se actualiza en Room y deja de poder iniciar sesión. La función segura de cambio impide desactivar al último administrador activo.

### Migraciones

`DatabaseFactory.kt` utiliza `fallbackToDestructiveMigration(true)`. Si se cambia la versión o el esquema sin una migración compatible, Room puede borrar la base local y volver a crearla. Considerarlo antes de modificar entidades o incrementar la versión; los datos locales de demo se pueden perder.

## Accesos de demostración

| Perfil | ID | PIN |
| --- | --- | --- |
| Administrador | `ADM-001` | `1234` |
| Mesero | `MSR-001` | `4321` |
| Cocina | `CCN-001` | `4321` |

Son credenciales de la base de demostración local.

## Compilar y ejecutar

Ejecutar los comandos desde la raíz del repositorio.

### Android

Abre el proyecto en Android Studio o ejecuta:

```bash
./gradlew :androidApp:assembleDebug
```

Para instalar la aplicación en un dispositivo o emulador Android conectado:

```bash
./gradlew :androidApp:installDebug
```

### iOS

En macOS, abre `iosApp/iosApp.xcodeproj` en Xcode, selecciona el esquema `iosApp` y ejecuta en un simulador o dispositivo configurado.

Para compilar el código compartido de iOS Simulator desde Gradle:

```bash
./gradlew :shared:compileKotlinIosSimulatorArm64
```

## Pruebas

Las pruebas comunes están en `shared/src/commonTest`. Actualmente se valida la regla de PIN de cuatro dígitos. Se pueden ejecutar en los hosts configurados con:

```bash
./gradlew :shared:testAndroidHostTest
./gradlew :shared:iosSimulatorArm64Test
```

Una compilación de verificación de los módulos compartido y Android usa:

```bash
./gradlew :shared:assembleAndroidMain :shared:compileKotlinIosSimulatorArm64 :androidApp:assembleDebug --no-configuration-cache
```

## Decisiones vigentes y limitaciones conocidas

- `AsistenteScreen.kt` es deliberadamente una **maqueta visual** de la semana 9 del curso. No está conectada a un modelo de IA y sus controles no envían consultas. La integración real se difiere a la semana 11; no implementarla ni quitar la maqueta antes de una solicitud expresa.
- Los datos de turnos y promedios de los perfiles de mesero y cocina permanecen fijos por decisión actual. No sustituirlos ni eliminarlos sin autorización.
- Los controles de cámara en los formularios de empleado y plato son marcadores visuales; no hay flujo de selección ni almacenamiento de imágenes. Mantenerlos sin cambios salvo petición expresa.
- Si la inicialización de la base falla, `App.kt` puede continuar con un repositorio nulo para permitir previews. El comportamiento de error de inicio de sesión asociado está pendiente y debe consultarse antes de cambiarlo.
- La navegación se coordina desde `App.kt` mediante `pantallaActual`. La sesión, la mesa seleccionada y el carrito se mantienen en estado Compose durante la ejecución.
- El carrito mantiene una copia local del plato seleccionado mientras dura la comanda; el estado del pedido y los renglones se guardan en Room al confirmar.

## Orientación para futuros cambios

- Priorizar los flujos visibles de la aplicación y preservar las reglas de negocio descritas arriba.
- Mantener el código directo y fácil de leer. No reducir líneas a costa de nombres claros ni hacer refactors amplios solo por estilo.
- Antes de cambiar un comportamiento, revisar el recorrido completo entre pantalla, repositorio, DAO y modelo Room.
- No tratar las maquetas, estadísticas fijas ni controles señalados como limitaciones como código accidental sin confirmar primero con el responsable del proyecto.
- No modificar credenciales, seed, esquema, migraciones, navegación o cálculos monetarios sin revisar el impacto en las pruebas y datos locales.
