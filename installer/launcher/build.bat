@echo off
REM Compila IniciarCore.exe con icono. Necesita Go y (una vez) go install github.com/akavel/rsrc@latest
setlocal
cd /d "%~dp0"

where go >nul 2>&1
if errorlevel 1 (
  echo ERROR: Go no esta instalado o no esta en el PATH.
  echo Instala Go desde https://go.dev/dl/
  exit /b 1
)

REM Icono: usar installer/icon.ico si existe, si no launcher/icon.ico
set ICON=
if exist "..\icon.ico" set ICON=..\icon.ico
if exist "icon.ico" set ICON=icon.ico
if "%ICON%"=="" (
  echo AVISO: No se encontro icon.ico en installer/ ni en launcher/. El exe tendra icono por defecto.
  go build -o IniciarCore.exe .
  goto :copiar
)

REM Instalar rsrc si no esta
where rsrc >nul 2>&1
if errorlevel 1 (
  echo Instalando rsrc para embeber el icono...
  go install github.com/akavel/rsrc@latest
)
rsrc -arch amd64 -ico "%ICON%"
if errorlevel 1 (
  echo ERROR al generar recursos. Compilando sin icono...
  del rsrc_amd64.syso 2>nul
  go build -o IniciarCore.exe .
  goto :copiar
)

go build -o IniciarCore.exe .
del rsrc_amd64.syso 2>nul

:copiar
if exist "..\IniciarCore.exe" del "..\IniciarCore.exe"
copy /Y IniciarCore.exe ..\ >nul
echo.
echo Listo: installer\IniciarCore.exe generado con icono.
echo Vuelve a compilar el instalador en Inno Setup para incluirlo.
endlocal
