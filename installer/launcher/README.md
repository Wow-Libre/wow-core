# Launcher .exe para Wow Libre Core

Pequeño programa en Go que hace lo mismo que `IniciarCore.bat`: carga el `.env`, ejecuta el JAR con `java -jar` y muestra la salida. Al compilarlo como **.exe** puedes asignarle un **icono** (el .bat no puede tener icono en Windows).

## Requisitos

- **Go 1.21+** instalado: [Descargar](https://go.dev/dl/)

## Compilar IniciarCore.exe (con icono)

1. Asegúrate de tener un `icon.ico` en la carpeta **installer/** (el mismo que usas para el instalador) o en **installer/launcher/**.
2. (Solo la primera vez) Instala la herramienta para embeber el icono:
   ```bash
   go install github.com/akavel/rsrc@latest
   ```
   Asegúrate de que `%GOPATH%\bin` (o `%USERPROFILE%\go\bin`) esté en el PATH.
3. En esta carpeta (`installer/launcher/`) ejecuta:
   ```bash
   build.bat
   ```
   O a mano:
   ```bash
   rsrc -arch amd64 -ico ..\icon.ico
   go build -o IniciarCore.exe .
   copy IniciarCore.exe ..
   ```
4. Se generará **installer/IniciarCore.exe**. Vuelve a compilar el instalador en Inno Setup; el script detectará el .exe y lo usará en lugar del .bat en los accesos directos.

El .exe tendrá tu icono tanto en la carpeta de instalación como en el menú y el escritorio.
