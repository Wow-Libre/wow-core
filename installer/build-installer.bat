@echo off
REM Compila wow-core y copia el JAR a installer\app para el instalador.
setlocal
cd /d "%~dp0\.."
if not exist "target\wowlibre-0.0.1-SNAPSHOT.jar" (
  echo Compilando proyecto...
  call mvn clean package -DskipTests
  if errorlevel 1 ( echo Error en mvn. & exit /b 1 )
)
if not exist "installer\app" mkdir installer\app
copy /Y "target\wowlibre-0.0.1-SNAPSHOT.jar" "installer\app\"
echo JAR copiado a installer\app\
echo Abre WowLibreCore.iss en Inno Setup y compila.
endlocal
