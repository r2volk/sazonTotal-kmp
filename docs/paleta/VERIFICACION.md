# Paleta global con colores definidos

Revisión del 5 de noviembre de 2026. Sustituye visualmente a las capturas de `../contraste` y `../temas`, que documentan las iteraciones anteriores.

## Dirección de color

Se mantiene la composición actual y el cambio automático según el dispositivo. El esquema claro conserva el predominio blanco con superficies grises definidas; el oscuro conserva el negro y los grises oscuros. Las superficies de acento tienen colores presentes y pares de texto propios.

| Uso | Claro | Oscuro |
|---|---|---|
| Fondo general | Blanco | Negro / gris casi negro en login |
| Superficie principal | #E9EDF2 | #1C1C1E |
| Selección principal | #17683F, texto blanco | #30D158, texto negro |
| Perfil de cocina | #1764A0, texto blanco | #123652, texto blanco |
| Perfil de mesero | #17683F, texto blanco | #113D2B, texto blanco |
| Cerrar sesión | #B42332, texto blanco | #B42332, texto blanco |

La paleta compartida define superficies, tintas y acciones para verde, azul, violeta, ámbar, rojo y rosa, respetando las funciones de los colores existentes. Los fondos de estados, accesos, notas, categorías, burbujas y distintivos ya no se convierten en tintes casi blancos. Los tonos cálidos de los platos se mantienen separados del rojo de error.

Los filtros seleccionados usan un relleno sólido y un primer plano asociado. Los perfiles usan bloques de color opacos, nombre e identificadores blancos. Cerrar sesión usa rojo sólido tanto en perfiles como en el panel de administración. No se añadieron degradados, recursos ni dependencias.

## Implementación y archivos

La aplicación completa ya resuelve sus colores mediante `colorDeTema` y `SazonTheme`; modificar ese sistema aplica la paleta a todas esas pantallas, incluidos formularios, cuadros y componentes. No se copió un esquema independiente en cada pantalla.

- `shared/src/commonMain/kotlin/org/sazontotal/project/components/PerfilScreenUI.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/components/FiltroButton.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/theme/SazonTheme.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/icons/LogoutButton.kt`
- `shared/src/commonTest/kotlin/org/sazontotal/project/TemaColorTest.kt`

## Comprobaciones

- Compilación offline e instalación de la APK con conservación de datos: correctas.
- `./gradlew :androidApp:assembleDebug :shared:testAndroidHostTest --offline`: correcto; 11 pruebas, 0 fallos. Incluye 7 pruebas de colores y 4 existentes.
- Pares de superficie/tinta y acción/texto de las seis familias: contraste mínimo de 4,5:1 en ambos modos. Texto blanco de los perfiles también probado sobre avatar y chips compuestos.
- Se abrieron y se inspeccionaron las vistas de la tabla en el emulador Android de 1080 × 2400. Son 48 capturas reales de esta revisión.
- Se comprobaron los tres roles, cambio del tema en ejecución, navegación, filtros, selectores, estados vacíos y anulados, y validación del formulario de empleado vacío.
- Carrito: cantidad 1 y total S/ 12,00 conservados al cambiar el tema. No se confirmó el pedido; el cierre de sesión habitual limpió el estado en memoria.
- Delivery: la lupa mantuvo la dirección sin elegir y la continuación deshabilitada; después se eligió explícitamente la sugerencia de Av. José Larco 400. La ruta mostró 0,52 km, aproximadamente 1 minuto, tramo hasta 1 km y tarifa S/ 3,00 en ambos modos. No se creó un pedido ni una entrega.
- No se guardaron formularios, no se modificaron disponibilidades ni empleados y no se cambiaron estados de pedidos existentes.
- `git diff --check`: correcto. Sin errores AndroidRuntime observados.

No se modificaron App.kt, repositorios, servicios, DAO, entidades, migraciones, API, cálculos ni callbacks. No se detectaron cambios concurrentes incompatibles al verificar los archivos antes de editarlos. No se hicieron commits.

## Límites

La ejecución se revisó en Android; iOS no se ejecutó. El ticket hereda la paleta, pero no se regeneró ni se da por inspeccionado en esta revisión, para conservar los pedidos existentes. Tampoco se volvió a comprobar una respuesta remota de Gemini ni la superposición del teclado del sistema. No se presentan esas comprobaciones como completadas.

## Capturas reales

| Vista | Claro | Oscuro |
|---|---|---|
| Login | [Ver](01-login-claro.png) | [Ver](02-login-oscuro.png) |
| Cocina por preparar | [Ver](04-cocina-clara.png) | [Ver](03-cocina-oscura.png) |
| Perfil de cocina | [Ver](05-perfil-cocina-claro.png) | [Ver](06-perfil-cocina-oscuro.png) |
| Cambio de PIN | [Ver](08-pin-claro.png) | [Ver](07-pin-oscuro.png) |
| Cocina listos | [Ver](09-cocina-listos-claro.png) | [Ver](10-cocina-listos-oscuro.png) |
| Pedidos de mesero | [Ver](12-mesero-claro.png) | [Ver](11-mesero-oscuro.png) |
| Menú | [Ver](13-menu-claro.png) | [Ver](14-menu-oscuro.png) |
| Carrito | [Ver](16-carrito-claro.png) | [Ver](15-carrito-oscuro.png) |
| Chat | [Ver](17-chat-claro.png) | [Ver](18-chat-oscuro.png) |
| Reparto | [Ver](20-reparto-claro.png) | [Ver](19-reparto-oscuro.png) |
| Entregas | [Ver](21-entregas-claras.png) | [Ver](22-entregas-oscuras.png) |
| Historial vacío | [Ver](24-historial-claro.png) | [Ver](23-historial-oscuro.png) |
| Perfil de mesero | [Ver](25-perfil-mesero-claro.png) | [Ver](26-perfil-mesero-oscuro.png) |
| Panel de administrador | [Ver](28-admin-claro.png) | [Ver](27-admin-oscuro.png) |
| Personal | [Ver](29-personal-claro.png) | [Ver](30-personal-oscuro.png) |
| Crear empleado | [Ver](32-empleado-claro.png) | [Ver](31-empleado-oscuro.png) |
| Validación del formulario | [Ver](33-empleado-validacion-clara.png) | [Ver](34-empleado-validacion-oscura.png) |
| Administración del menú | [Ver](36-gestion-menu-claro.png) | [Ver](35-gestion-menu-oscuro.png) |
| Formulario de plato | [Ver](37-plato-claro.png) | [Ver](38-plato-oscuro.png) |
| Selector de categoría | [Ver](40-categorias-claras.png) | [Ver](39-categorias-oscuras.png) |
| Reportes | [Ver](41-reportes-claros.png) | [Ver](42-reportes-oscuros.png) |
| Todos los pedidos | [Ver](44-pedidos-claros.png) | [Ver](43-pedidos-oscuros.png) |
| Dirección sin elegir | [Ver](46-direccion-sin-elegir-claro.png) | [Ver](45-direccion-sin-elegir-oscuro.png) |
| Ruta y tarifa | [Ver](47-ruta-clara.png) | [Ver](48-ruta-oscura.png) |
