# Instalador Wow Libre Core (Windows)

Instalador para **Wow Libre Core** con asistente que configura base de datos, puerto, JWT y CORS, y genera el archivo `.env` y el launcher `IniciarCore.bat`.

## Requisitos

- **Inno Setup 6** (o superior): [Descargar](https://jrsoftware.org/isdl.php)
- **JDK 21** instalado en el equipo donde se ejecutará el instalador (el usuario final necesita Java para ejecutar el JAR)

## Cómo generar el instalador

1. **Compilar el proyecto y preparar el JAR:**
   ```batch
   cd C:\ruta\a\wow-core
   installer\build-installer.bat
   ```
   Esto ejecuta `mvn clean package -DskipTests` si no existe el JAR y copia `wowlibre-0.0.1-SNAPSHOT.jar` a `installer\app\`.

2. **Compilar el instalador con Inno Setup:**
   - Abre **Inno Setup Compiler**
   - Carga `installer\WowLibreCore.iss`
   - Menú **Build** → **Compile**

3. El instalador se generará en `installer\output\WowLibreCore-Setup-0.0.1.exe`.

## Flujo del asistente

- **Directorio de instalación**
- **Base de datos:** URL JDBC (`DB_CORE_URL`), usuario (`DB_CORE_USERNAME`) y contraseña (`DB_CORE_PASSWORD`) de MySQL
- **Servidor:** Puerto del Core (`CORE_SERVER_PORT`, ej. 8091), Secret JWT (`CORE_JWT_SECRET_KEY`), dominio público (`HOST_DOMAIN`)
- **CORS:** Orígenes permitidos (`APP_CORS_ALLOWED_ORIGINS`)

Al finalizar, en la carpeta de instalación quedan: el JAR, `.env` y `IniciarCore.bat`. El usuario ejecuta el `.bat` o el acceso directo para arrancar con `--spring.profiles.active=prod`.

## Variables escritas en .env

Coinciden con `application.yml` (perfil `prod`):

| Variable               | Descripción                          |
|------------------------|--------------------------------------|
| `DB_CORE_URL`          | URL JDBC de la base de datos         |
| `DB_CORE_USERNAME`     | Usuario MySQL                        |
| `DB_CORE_PASSWORD`     | Contraseña MySQL                     |
| `CORE_SERVER_PORT`     | Puerto del servidor (ej. 8091)      |
| `CORE_JWT_SECRET_KEY`  | Clave secreta para JWT               |
| `HOST_DOMAIN`          | Dominio del frontend                 |
| `APP_CORS_ALLOWED_ORIGINS` | Orígenes CORS permitidos (opcional) |

Otras variables (mail, Telegram, etc.) se pueden añadir manualmente en `.env` después de instalar.

## Icono opcional

Coloca `icon.ico` en la carpeta `installer\` para que el instalador y los accesos directos usen tu icono. Si no existe, se usan los iconos por defecto de Windows.

## Launcher .exe con icono (opcional)

Por defecto el launcher es `IniciarCore.bat`. Si quieres un **.exe con icono** (en la carpeta, menú y escritorio):

1. Instala **Go** (https://go.dev/dl/) si no lo tienes.
2. En una terminal:
   ```batch
   cd installer\launcher
   build.bat
   ```
3. Eso genera `installer\IniciarCore.exe` con el icono de `installer\icon.ico` (o `launcher\icon.ico`). Coloca tu `icon.ico` en `installer\` antes de ejecutar `build.bat`.
4. Vuelve a compilar el instalador en Inno Setup; usará el .exe en los accesos directos.

Más detalles en `installer\launcher\README.md`.

## Estructura esperada antes de compilar

```
installer/
├── WowLibreCore.iss
├── IniciarCore.bat
├── build-installer.bat
├── README.md
├── icon.ico              ← opcional: icono del instalador y accesos directos
├── IniciarCore.exe       ← opcional: generado con launcher\build.bat (con icono)
├── launcher/
│   ├── main.go
│   ├── go.mod
│   ├── build.bat
│   └── README.md
├── app/
│   └── wowlibre-0.0.1-SNAPSHOT.jar   ← generado por build-installer.bat
└── output/                            ← generado por Inno Setup
    └── WowLibreCore-Setup-0.0.1.exe
```
