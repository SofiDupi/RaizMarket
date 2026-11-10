# Raíz Market – Interfaces gráficas (Android nativo)

Marketplace móvil de productos artesanales y agroecológicos de Colombia.
Actividad 3 · Desarrollo de Aplicaciones Móviles Nativas · Fundación Universitaria Compensar.

## Tecnología
Kotlin · Vistas XML (Activities + Fragments) · Material Components 3 · minSdk 26 / targetSdk 34 · BiometricPrompt.

## Cómo ejecutarlo
1. Abrir la carpeta con **Android Studio** (Koala o superior, JDK 17) y esperar el *Gradle Sync*.
2. Ejecutar en un emulador o dispositivo (Run ▶).
3. Generar el APK de validación: **Build > Build Bundle(s) / APK(s) > Build APK(s)**
   (o `./gradlew assembleDebug`). Queda en `app/build/outputs/apk/debug/app-debug.apk`.

## Cómo probar cada rol (modo demostración)
En el login se elige el rol con los chips **Comprador / Vendedor / Administrador** y se pulsa *Ingresar*
(correo con "@" y cualquier contraseña). En la versión con backend el rol saldrá del claim del token (RBAC).

## Pantallas implementadas
| Rol | Pantallas |
|---|---|
| Transversal | Splash, Login (correo, Google, huella con BiometricPrompt), Registro con rol y consentimientos, Recuperar contraseña |
| Comprador (Bottom Navigation) | Inicio, Catálogo/Cercanos, Detalle de producto, Carrito, Checkout y pasarela de pago, Pedidos (retracto/factura), Perfil con foto |
| Vendedor (Bottom Navigation) | Panel con indicadores, Productos (CRUD), Formulario con cámara/galería/ubicación, Pedidos recibidos, Perfil de tienda |
| Administrador (Navigation Drawer) | Dashboard, Usuarios (listar, crear, editar, suspender, eliminar), Moderación de productos, Reportes de ventas |

## Estructura de recursos
- `res/values`: `colors`, `dimens`, `strings` (es-CO), `themes` (tema base + `Seller` terracota + `Admin` azul), `styles` (botones, campos, tarjetas, chips, tipografía).
- `res/drawable`: formas (`bg_*`) e iconos vectoriales (`ic_*`).
- `res/layout`: 34 layouts (activities, fragments e ítems de RecyclerView).
- `res/menu`: barras inferiores y drawer. `res/color`: selectores de chips y navegación.

## Alcance de esta fase
Solo frontend: los datos son de ejemplo (`Data.kt`). Quedan para la fase de integración: Firebase Auth/Google Sign-In,
backend y almacenamiento de imágenes, Google Maps/FusedLocation, pasarela (Wompi/PayU/Mercado Pago) y facturación electrónica.
