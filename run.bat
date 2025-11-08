@echo off
REM 🚀 Wow Libre Core - Script de Ejecución (Windows)
REM Facilita el inicio de la aplicación Spring Boot

setlocal enabledelayedexpansion

REM Verificar Java
where java >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java no está instalado. Por favor instala Java 21 o superior.
    exit /b 1
)

REM Verificar Maven wrapper
if not exist "mvnw.cmd" (
    echo ❌ Maven wrapper (mvnw.cmd) no encontrado.
    echo Asegúrate de estar en el directorio raíz del proyecto.
    exit /b 1
)

REM Verificar archivo .env
if not exist ".env" (
    echo ⚠️  Archivo .env no encontrado
    if exist ".env.example" (
        echo ℹ️  Copiando .env.example a .env...
        copy .env.example .env
        echo ⚠️  Por favor, edita el archivo .env con tus credenciales.
        pause
    ) else (
        echo ⚠️  Archivo .env.example no encontrado. Continuando sin variables de entorno...
    )
)

REM Procesar argumentos
set MODE=%1
if "%MODE%"=="" set MODE=dev

if "%MODE%"=="dev" (
    echo ℹ️  Ejecutando en modo desarrollo...
    call mvnw.cmd spring-boot:run
    goto :end
)

if "%MODE%"=="build" (
    echo ℹ️  Compilando la aplicación...
    call mvnw.cmd clean package
    goto :end
)

if "%MODE%"=="build-fast" (
    echo ℹ️  Compilando la aplicación (sin tests)...
    call mvnw.cmd clean package -DskipTests
    goto :end
)

if "%MODE%"=="run" (
    set PROFILE=%2
    if not exist "target\wowlibre-0.0.1-SNAPSHOT.jar" (
        echo ⚠️  JAR no encontrado. Compilando...
        call mvnw.cmd clean package -DskipTests
    )
    if "%PROFILE%"=="" (
        echo ℹ️  Iniciando la aplicación...
        java -jar target\wowlibre-0.0.1-SNAPSHOT.jar
    ) else (
        echo ℹ️  Iniciando la aplicación con perfil: %PROFILE%
        java -jar -Dspring.profiles.active=%PROFILE% target\wowlibre-0.0.1-SNAPSHOT.jar
    )
    goto :end
)

if "%MODE%"=="help" (
    echo 🚀 Wow Libre Core - Script de Ejecución
    echo.
    echo Uso: run.bat [OPCIÓN]
    echo.
    echo Opciones:
    echo   dev          Ejecuta en modo desarrollo (spring-boot:run)
    echo   build        Solo compila la aplicación
    echo   build-fast   Compila sin ejecutar tests
    echo   run [perfil] Ejecuta el JAR compilado (opcional: perfil Spring)
    echo   help         Muestra esta ayuda
    echo.
    echo Ejemplos:
    echo   run.bat dev              # Modo desarrollo
    echo   run.bat run              # Ejecuta JAR
    echo   run.bat run prod          # Ejecuta JAR con perfil prod
    echo   run.bat build             # Solo compilar
    goto :end
)

echo ❌ Opción desconocida: %MODE%
echo.
call run.bat help

:end
endlocal

