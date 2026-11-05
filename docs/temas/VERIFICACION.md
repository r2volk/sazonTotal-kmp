# Tema automático claro y oscuro

## Comportamiento

La aplicación conserva la distribución, tamaños, tipografía, bordes, navegación y funciones de la versión restaurada. Sigue el modo claro u oscuro del dispositivo, sin añadir un selector propio.

- Oscuro: fondos negros, superficies grises oscuras y colores originales de la interfaz.
- Claro: fondos blancos, superficies grises claras, texto oscuro y variantes legibles de los acentos. Los fondos de botones de acento se conservan; amarillos, verdes y otros textos claros se oscurecen manteniendo su tono cuando lo exige el contraste.
- Campos, cursores, filtros, iconos, bordes, formularios, hojas, menús y estados usan la misma adaptación.
- Android: ventana inicial y barras del sistema siguen el tema. `configChanges="uiMode"` y la actualización de barras permiten alternarlo sin reiniciar la actividad ni perder los estados en memoria.
- Mapa Android: estilo nocturno local y mapa estándar en claro. No se cambian destinos, marcadores, cámara, rutas ni cálculo de tarifa.

## Implementación

`SazonTheme.kt` contiene los esquemas Material y la adaptación de colores por uso (texto, fondo o borde). La paleta `Colores.kt` conserva sus valores originales. Los componentes leen el modo del tema durante la composición; no guardan colores derivados en constantes ni en estado global mutable.

`App.kt` solo cambia el import y la llamada del envoltorio de tema. Repositorios, DAO, entidades, migraciones, servicios, claves, cálculos y callbacks permanecen intactos. Los archivos `MapaDelivery` modificados contienen exclusivamente presentación del mapa.

## Comprobaciones

Emulador Android `emulator-5554`, 1080×2400 px. Instalación con `adb install -r` conservando los datos.

- Compilación offline de `:androidApp:assembleDebug`: correcta.
- `:shared:testAndroidHostTest`: 8 pruebas aprobadas, 0 fallos: 4 existentes y 4 nuevas sobre conservación del oscuro, superficies claras, transparencia, botones y contraste del texto.
- Contraste probado de textos y estados sobre las superficies claras principales: al menos 4,5:1 en las muestras verificadas.
- Alternancia del sistema en ejecución: login, paneles, historial, perfil, cambio de PIN, chat, carrito, reparto, entregas, personal, formularios, selector de categorías y reportes.
- Sesión y carrito conservados al alternar: mismo producto, cantidad 1 y total S/ 12,00 en ambos modos.
- Cocina revisada con filtros de pendientes y listos en claro y oscuro.
- Sin errores `AndroidRuntime` durante el recorrido.
- `git diff --check`: correcto.

No se crearon pedidos ni empleados, no se guardaron formularios y no se modificaron registros existentes. El producto utilizado en el carrito quedó solo en memoria y se limpió mediante el cierre de sesión habitual. No se hicieron commits ni se detectaron cambios concurrentes incompatibles.

## Capturas reales

| Pantalla | Claro | Oscuro |
|---|---|---|
| Acceso | [Ver](02-login-claro.png) | [Ver](01-login-oscuro.png) |
| Mesero | [Ver](03-mesero-claro.png) | [Ver](04-mesero-oscuro.png) |
| Carrito | [Ver](07-carrito-claro.png) | [Ver](06-carrito-oscuro.png) |
| Perfil | [Ver](12-perfil-claro.png) | [Ver](11-perfil-oscuro.png) |
| Chat | [Ver](16-chat-claro.png) | [Ver](15-chat-oscuro.png) |
| Mapa | [Ver](17-reparto-claro.png) | [Ver](18-reparto-oscuro.png) |
| Administrador | [Ver](21-admin-claro.png) | [Ver](22-admin-oscuro.png) |
| Crear empleado | [Ver](25-empleado-claro.png) | [Ver](26-empleado-oscuro.png) |
| Categorías | [Ver](30-categorias-claras.png) | [Ver](31-categorias-oscuras.png) |
| Reportes | [Ver](33-reportes-claros.png) | [Ver](32-reportes-oscuros.png) |
| Cocina | [Ver](37-cocina-clara.png) | [Ver](36-cocina-oscura.png) |

## Límites

Se verificó en Android; no se ejecutó iOS en un dispositivo o simulador. El tema es compartido y el contenedor de mapa de iOS también recibe sus colores adaptados. No se crearon nuevos pedidos para abrir el ticket ni se forzaron todos los errores de red o base de datos: sus componentes recibieron la adaptación de colores, pero esos estados no se presentan como inspeccionados en ejecución.

## Archivos modificados

- `androidApp/src/main/AndroidManifest.xml`
- `androidApp/src/main/kotlin/org/sazontotal/project/MainActivity.kt`
- `androidApp/src/main/res/values/themes.xml`
- `shared/src/androidMain/kotlin/org/sazontotal/project/data/maps/MapaDelivery.android.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/App.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/components/AccesoRapidoCard.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/components/AdminBottomSheet.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/components/BuscadorTextField.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/components/CampoTextoAdmin.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/components/CantidadStepper.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/components/CocinaPedidoCard.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/components/FiltroButton.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/components/FotoEmpleadoAvatar.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/components/FotoPlatoBanner.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/components/OpcionPerfilRow.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/components/PedidoCard.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/components/PerfilScreenUI.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/components/PinButton.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/components/RolOptionButton.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/components/StatCardAdmin.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/components/StatCardCentrado.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/components/SwitchButton.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/components/TabBarPill.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/icons/BackspaceIcon.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/icons/CheckIcon.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/icons/DashboardAdminIcons.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/icons/LogoutButton.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/screens/CambioPinScreen.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/screens/DashboardAdmin.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/screens/LoginScreen.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/screens/screensAdmin/ReportesScreen.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/screens/screensAdmin/TodosLosPedidosScreen.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/screens/screensAdmin/editarEmpleado/CrearEmpleado.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/screens/screensAdmin/editarEmpleado/GestionPersonalScreen.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/screens/screensAdmin/editarMenu/EditarMenuScreen.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/screens/screensAdmin/editarMenu/EditarPlato.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/screens/screensCocina/CocinaPedidosScreen.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/screens/screensCocina/CocinaPerfilScreen.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/screens/screensMesero/AsistenteScreen.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/screens/screensMesero/EntregasScreen.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/screens/screensMesero/MeseroHistorialScreen.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/screens/screensMesero/MeseroPedidosScreen.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/screens/screensMesero/MeseroPerfilScreen.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/screens/screensMesero/RepartoScreen.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/screens/screensMesero/carrito/CarritoScreen.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/screens/screensMesero/carrito/MenuScreen.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/screens/screensMesero/carrito/PedidoConcretadoScreen.kt`
- `shared/src/iosMain/kotlin/org/sazontotal/project/data/maps/MapaDelivery.ios.kt`
- `shared/src/commonMain/kotlin/org/sazontotal/project/theme/SazonTheme.kt`
- `shared/src/commonTest/kotlin/org/sazontotal/project/TemaColorTest.kt`
- `androidApp/src/main/res/values-night/themes.xml`
