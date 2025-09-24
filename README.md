Actividad Sumativa Semana 6
PRO401-9523-225081-ONL-TALLER DE APLICACIONES MÓVILES
Javier Oyarce / Mariel Basoalto

# DistribuidoraApp (Android, Kotlin)

Aplicación móvil para **distribuidora de alimentos** con:
- **Login** (Firebase Authentication, Email/Password).
- **Guardado de ubicación** en **Realtime Database** tras login.
- **Cálculo de despacho** según reglas de negocio.
- **Monitoreo de temperatura** del camión desde RTDB.

## Caso
- > Compras ≥ 50.000 CLP → despacho **gratis** (≤ 20 km).
- > 25.000–49.999 CLP → **$150/km**
- > < 25.000 CLP → **$300/km**
- Android cliente objetivo: **Oreo (API 27)**.

## Tecnologías
- Kotlin, Android Studio.
- Firebase: Authentication, Realtime Database.
- Google Play Services Location.

## Requisitos
- Android Studio Giraffe+ / JDK 17.
- app/google-services.json

## Configuración
1. Crear proyecto en Firebase y habilitar **Email/Password**.
2. Descargar google-services.json y colocarlo en app/.
3. Reglas RTDB:
   ```json
   { "rules": { ".read": "auth != null", ".write": "auth != null" } }
