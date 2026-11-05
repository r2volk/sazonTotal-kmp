# Mejora de contraste — 5 de noviembre de 2026

Esta revisión conserva el diseño actual y complementa la implementación del tema automático documentada en `../temas/VERIFICACION.md`. Las capturas de esta carpeta corresponden al ajuste de contraste; las de `../temas` muestran la iteración anterior.

## Cambios visuales

- Barra inferior con superficie gris diferenciada y contorno visible. Selección verde oscuro con texto blanco en claro; verde con texto negro en oscuro. Iconos inactivos con contraste propio.
- Textos secundarios y placeholders más oscuros en claro y más luminosos en oscuro. Estados rojos y otros acentos se adaptan para seguir siendo legibles.
- Campos compartidos y buscadores con contornos visibles; se mantienen tamaños, distribución, callbacks y validaciones.
- Filtros de menú, etiquetas, cocina y entregas reciben sus colores originales y los adaptan una sola vez según su uso. Se corrige la aplicación de adaptación de texto a fondos.
- La lupa de Delivery usa negro sobre su botón verde. La acción verde de cocina usa texto negro en ambos modos.
- Barras de reportes más visibles: gris con contraste y verde más oscuro en modo claro. Sus valores y dimensiones no cambian.
- Interruptores apagados con pulgar visible y contorno, sin alterar su comportamiento.

## Validación

- Android, emulador 1080 × 2400. APK compilado offline e instalado con `adb install -r`, conservando la base de datos.
- `./gradlew :androidApp:assembleDebug :shared:testAndroidHostTest --offline`: correcto. 10 pruebas, cero fallos: 4 existentes y 6 de colores.
- Muestras verificadas: texto e iconos de navegación ≥ 4,5:1; contorno y selección de navegación ≥ 3:1; texto secundario oscuro sobre superficie deshabilitada ≥ 4,5:1; contornos de campos ≥ 3:1. Son comprobaciones de pares concretos, no una certificación de accesibilidad de toda la app.
- Se abrieron y revisaron las pantallas de la tabla en ambos temas. Se revisaron filtros, formularios, selectores, estados vacíos, estados anulados, scroll y navegación atrás/cierre de sesión.
- El carrito mantuvo un producto, cantidad 1 y total S/ 12,00 al alternar el tema. No se confirmó el pedido; el cierre de sesión limpió el carrito en memoria.
- Pulsar Buscar en Delivery mantuvo la dirección sin elegir, mostró la validación existente y conservó Continuar deshabilitado.
- No se guardaron formularios, no se activaron/desactivaron registros ni se cambiaron estados de pedidos. No se crearon ni eliminaron pedidos, entregas o empleados.
- No se observaron errores AndroidRuntime durante la revisión. `git diff --check`: correcto.

## Alcance y límites

No se modificaron App.kt, repositorios, DAO, entidades, migraciones, servicios, configuración de API ni cálculos. Los cambios en pantallas se limitan a colores y argumentos visuales. Se comprobó el contenido previo antes de cada edición; no se detectaron cambios concurrentes incompatibles en los archivos editados. No se hicieron commits.

iOS no se ejecutó. No se regeneró un ticket ni se completó un nuevo pedido/entrega en esta revisión. Las respuestas remotas de Gemini y la elección de una ruta nueva no se volvieron a probar. El teclado del emulador figuró activo en el sistema pero no apareció en las capturas, incluso habilitando temporalmente su visualización con teclado físico; no se da por revisada visualmente su superposición. La preferencia del emulador se restauró a su valor anterior.

## Archivos de esta revisión

- `shared/src/commonMain/kotlin/org/sazontotal/project/screens/screensAdmin/ReportesScreen.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/screens/screensAdmin/editarMenu/EditarMenuScreen.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/screens/screensAdmin/editarMenu/EditarPlato.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/screens/screensCocina/CocinaPedidosScreen.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/screens/screensMesero/RepartoScreen.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/screens/screensMesero/EntregasScreen.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/screens/screensMesero/carrito/MenuScreen.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/components/SwitchButton.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/components/BuscadorTextField.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/components/CocinaPedidoCard.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/components/TabBarPill.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/components/CampoTextoAdmin.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/theme/SazonTheme.kt`
- `shared/src/commonTest/kotlin/org/sazontotal/project/TemaColorTest.kt`

## Capturas reales

| Pantalla | Claro | Oscuro |
|---|---|---|
| Login | [Ver](01-login-claro.png) | [Ver](02-login-oscuro.png) |
| Pedidos de mesero y barra inferior | [Ver](03-mesero-claro.png) | [Ver](04-mesero-oscuro.png) |
| Perfil de mesero | [Ver](06-perfil-claro.png) | [Ver](05-perfil-oscuro.png) |
| Cambio de PIN | [Ver](07-pin-claro.png) | [Ver](08-pin-oscuro.png) |
| Menú | [Ver](10-menu-claro.png) | [Ver](09-menu-oscuro.png) |
| Carrito | [Ver](11-carrito-claro.png) | [Ver](12-carrito-oscuro.png) |
| Asistente | [Ver](14-chat-claro.png) | [Ver](13-chat-oscuro.png) |
| Reparto | [Ver](15-reparto-claro.png) | [Ver](16-reparto-oscuro.png) |
| Entregas | [Ver](18-entregas-claras.png) | [Ver](17-entregas-oscuras.png) |
| Historial vacío | [Ver](19-historial-claro.png) | [Ver](20-historial-oscuro.png) |
| Panel de administrador | [Ver](22-admin-claro.png) | [Ver](21-admin-oscuro.png) |
| Personal | [Ver](23-personal-claro.png) | [Ver](24-personal-oscuro.png) |
| Crear empleado | [Ver](26-empleado-claro.png) | [Ver](25-empleado-oscuro.png) |
| Administración del menú | [Ver](27-gestion-menu-claro.png) | [Ver](28-gestion-menu-oscuro.png) |
| Formulario de plato | [Ver](30-plato-claro.png) | [Ver](29-plato-oscuro.png) |
| Selector de categoría | [Ver](31-categorias-claras.png) | [Ver](32-categorias-oscuras.png) |
| Reportes | [Ver](34-reportes-claros.png) | [Ver](33-reportes-oscuros.png) |
| Todos los pedidos | [Ver](35-pedidos-claros.png) | [Ver](36-pedidos-oscuros.png) |
| Cocina por preparar | [Ver](38-cocina-clara.png) | [Ver](37-cocina-oscura.png) |
| Cocina listos | [Ver](39-listos-claros.png) | [Ver](40-listos-oscuros.png) |
| Perfil de cocina | [Ver](42-cocina-perfil-claro.png) | [Ver](41-cocina-perfil-oscuro.png) |
| PIN con campo enfocado | [Ver](43-pin-enfocado-claro.png) | [Ver](44-pin-enfocado-oscuro.png) |
| Validación de dirección sin elegir | [Ver](46-direccion-error-claro.png) | [Ver](45-direccion-error-oscuro.png) |
