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
    echo.
    echo ╔════════════════════════════════════════════════════════════════╗
    echo ║                    ⚠️  ADVERTENCIA IMPORTANTE ⚠️                  ║
    echo ╠════════════════════════════════════════════════════════════════╣
    echo ║  Archivo .env NO encontrado                                    ║
    echo ║                                                                ║
    echo ║  ⚠️  La aplicación usará valores por DEFECTO                   ║
    echo ║  ⚠️  Esto puede causar errores de conexión a BD                 ║
    echo ║  ⚠️  y otros problemas de configuración                         ║
    echo ║                                                                ║
    echo ║  Recomendación:                                                 ║
    echo ║  1. Crea un archivo .env basado en .env.example                ║
    echo ║  2. Configura tus credenciales de base de datos                ║
    echo ║  3. Configura las demás variables de entorno                   ║
    echo ╚════════════════════════════════════════════════════════════════╝
    echo.
    
    if exist ".env.example" (
        set /p COPY_ENV="¿Deseas copiar .env.example a .env? (S/n): "
        if /i not "%COPY_ENV%"=="n" (
            echo ℹ️  Copiando .env.example a .env...
            copy .env.example .env
            echo.
            echo ⚠️  IMPORTANTE: Edita el archivo .env con tus credenciales antes de continuar.
            echo.
            set /p CONTINUE="¿Deseas continuar de todas formas? (s/N): "
            if /i not "%CONTINUE%"=="s" (
                echo Ejecución cancelada. Configura el archivo .env y vuelve a intentar.
                exit /b 1
            )
        ) else (
            echo ⚠️  No se copió .env.example. La aplicación usará valores por defecto.
            echo.
            set /p CONTINUE="¿Deseas continuar de todas formas? (s/N): "
            if /i not "%CONTINUE%"=="s" (
                echo Ejecución cancelada.
                exit /b 1
            )
        )
    ) else (
        echo ⚠️  Archivo .env.example no encontrado.
        echo.
        echo ⚠️  La aplicación se ejecutará con valores por defecto.
        echo.
        set /p CONTINUE="¿Deseas continuar de todas formas? (s/N): "
        if /i not "!CONTINUE!"=="s" (
            echo Ejecución cancelada. Crea un archivo .env con tus variables de entorno.
            exit /b 1
        )
    )
    echo.
)

REM Procesar argumentos
set MODE=%1
if "%MODE%"=="" set MODE=dev

if "%MODE%"=="dev" (
    echo ℹ️  Ejecutando en modo desarrollo (foreground)...
    if not exist "target\wowlibre-0.0.1-SNAPSHOT.jar" (
        echo ⚠️  JAR no encontrado. Compilando primero...
        call mvnw.cmd clean package -DskipTests
    )
    call mvnw.cmd spring-boot:run
    goto :end
)

if "%MODE%"=="start" (
    echo ℹ️  Iniciando aplicación en segundo plano...
    if not exist "logs" mkdir logs
    if not exist "target\wowlibre-0.0.1-SNAPSHOT.jar" (
        echo ⚠️  JAR no encontrado. Compilando primero...
        call mvnw.cmd clean package -DskipTests
    )
    start /B mvnw.cmd spring-boot:run > logs\app.log 2>&1
    timeout /t 2 /nobreak >nul
    echo ✅ Aplicación iniciada en segundo plano
    echo ℹ️  Logs: type logs\app.log
    echo ℹ️  Para detener: run.bat stop
    goto :end
)

if "%MODE%"=="stop" (
    echo ℹ️  Deteniendo aplicación...
    for /f "tokens=2" %%a in ('tasklist /FI "IMAGENAME eq java.exe" /FO LIST ^| findstr /I "PID"') do (
        set PID=%%a
    )
    if defined PID (
        taskkill /PID %PID% /F >nul 2>&1
        echo ✅ Aplicación detenida
    ) else (
        echo ⚠️  No se encontró proceso de la aplicación
    )
    goto :end
)

if "%MODE%"=="status" (
    tasklist /FI "IMAGENAME eq java.exe" /FO LIST | findstr /I "PID" >nul
    if %errorlevel%==0 (
        echo ✅ Aplicación corriendo
        echo ℹ️  Para ver logs: type logs\app.log
        echo ℹ️  Para detener: run.bat stop
    ) else (
        echo ❌ Aplicación no está corriendo
    )
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
    echo   dev          Ejecuta en modo desarrollo (foreground)
    echo   start        Ejecuta en modo desarrollo (background)
    echo   stop         Detiene la aplicación en segundo plano
    echo   status       Muestra el estado de la aplicación
    echo   build        Solo compila la aplicación
    echo   build-fast   Compila sin ejecutar tests
    echo   run [perfil] Ejecuta el JAR compilado (opcional: perfil Spring)
    echo   help         Muestra esta ayuda
    echo.
    echo Ejemplos:
    echo   run.bat dev              # Modo desarrollo (foreground)
    echo   run.bat start            # Modo desarrollo (background)
    echo   run.bat stop             # Detener aplicación
    echo   run.bat status           # Ver estado
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

