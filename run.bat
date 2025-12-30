@echo off
REM 🚀 Wow Libre Core - Script de Ejecución Simplificado (Windows)
REM Facilita el inicio de la aplicación Spring Boot

setlocal enabledelayedexpansion

REM Crear directorio de logs si no existe
if not exist "logs" mkdir logs

REM Procesar argumentos
set MODE=%1
if "%MODE%"=="" set MODE=run

if "%MODE%"=="setup" goto :setup
if "%MODE%"=="start" goto :start
if "%MODE%"=="stop" goto :stop
if "%MODE%"=="status" goto :status
if "%MODE%"=="run" goto :run
if "%MODE%"=="help" goto :help
if "%MODE%"=="--help" goto :help
if "%MODE%"=="-h" goto :help

echo ❌ Opción desconocida: %MODE%
echo.
goto :help

:setup
call :check_java
if %errorlevel% neq 0 exit /b 1
call :create_env
goto :end

:start
call :check_java
if %errorlevel% neq 0 exit /b 1
call :check_maven
if %errorlevel% neq 0 exit /b 1
call :ensure_env
if %errorlevel% neq 0 exit /b 1
call :load_env
call :run_app true
goto :end

:run
call :check_java
if %errorlevel% neq 0 exit /b 1
call :check_maven
if %errorlevel% neq 0 exit /b 1
call :ensure_env
if %errorlevel% neq 0 exit /b 1
call :load_env
call :run_app false
goto :end

:stop
call :stop_app
goto :end

:status
call :status_app
goto :end

:help
echo 🚀 Wow Libre Core - Script de Ejecución Simplificado
echo.
echo Uso: run.bat [OPCIÓN]
echo.
echo Opciones:
echo   setup        Crea/configura el archivo .env interactivamente
echo   start        Ejecuta la aplicación en segundo plano
echo   stop         Detiene la aplicación
echo   status       Muestra el estado de la aplicación
echo   help         Muestra esta ayuda
echo.
echo Variables de Entorno:
echo   El script puede usar variables de dos formas:
echo   1. Archivo .env en la raíz del proyecto (recomendado para desarrollo)
echo   2. Variables de entorno del sistema (útil para producción/Docker)
echo.
echo   Si tienes las variables definidas en el sistema, no necesitas .env
echo.
echo Ejemplos:
echo   run.bat setup        # Configurar .env
echo   run.bat start        # Iniciar aplicación (background)
echo   run.bat              # Iniciar aplicación (foreground)
echo   run.bat stop         # Detener aplicación
echo   run.bat status       # Ver estado
echo.
echo Variables críticas requeridas:
echo   DB_CORE_URL, DB_CORE_USERNAME, DB_CORE_PASSWORD
echo.
goto :end

REM ============================================
REM Funciones
REM ============================================

:check_java
where java >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java no está instalado. Por favor instala Java 21 o superior.
    exit /b 1
)
for /f "tokens=3" %%g in ('java -version 2^>^&1 ^| findstr /i "version"') do (
    set JAVA_VERSION=%%g
    set JAVA_VERSION=!JAVA_VERSION:"=!
    for /f "tokens=1 delims=." %%v in ("!JAVA_VERSION!") do set JAVA_VERSION=%%v
)
if !JAVA_VERSION! LSS 21 (
    echo ⚠️  Java !JAVA_VERSION! detectado. Se recomienda Java 21 o superior.
) else (
    echo ✅ Java !JAVA_VERSION! detectado
)
exit /b 0

:check_maven
if not exist "mvnw.cmd" (
    echo ❌ Maven wrapper (mvnw.cmd) no encontrado.
    echo Asegúrate de estar en el directorio raíz del proyecto.
    exit /b 1
)
echo ✅ Maven wrapper encontrado
exit /b 0

:create_env
echo ℹ️  Configurando archivo .env...
echo.

REM Valores por defecto
set DB_CORE_URL_DEFAULT=jdbc:mysql://localhost:3306/platform
set DB_CORE_USERNAME_DEFAULT=root
set CORE_SERVER_PORT_DEFAULT=8091
set HOST_DOMAIN_DEFAULT=http://localhost:3000
set CORE_JWT_SECRET_KEY_DEFAULT=6E4D574873506B4A72434B6A614B39786F736B7855666B4D456A6E466F785572
set CORE_GOOGLE_HOST_DEFAULT=smtp.gmail.com
set CORE_GOOGLE_PORT_DEFAULT=587
set GOOGLE_API_SECRET_DEFAULT=6Lcd3iArAAAAAMBZ30BN1hry_nhXsfnoHQWIfejg
set APP_SIGNATURE_SECRET_KEY_DEFAULT=wowLibreSecret

echo ═══════════════════════════════════════════════════════════════
echo   Configuración de Base de Datos
echo ═══════════════════════════════════════════════════════════════
set /p DB_CORE_URL="URL de Base de Datos [%DB_CORE_URL_DEFAULT%]: "
if "!DB_CORE_URL!"=="" set DB_CORE_URL=!DB_CORE_URL_DEFAULT!

set /p DB_CORE_USERNAME="Usuario de Base de Datos [%DB_CORE_USERNAME_DEFAULT%]: "
if "!DB_CORE_USERNAME!"=="" set DB_CORE_USERNAME=!DB_CORE_USERNAME_DEFAULT!

set /p DB_CORE_PASSWORD="Contraseña de Base de Datos: "

echo.
echo ═══════════════════════════════════════════════════════════════
echo   Configuración del Servidor
echo ═══════════════════════════════════════════════════════════════
set /p CORE_SERVER_PORT="Puerto del Servidor [%CORE_SERVER_PORT_DEFAULT%]: "
if "!CORE_SERVER_PORT!"=="" set CORE_SERVER_PORT=!CORE_SERVER_PORT_DEFAULT!

set /p HOST_DOMAIN="Dominio/Host [%HOST_DOMAIN_DEFAULT%]: "
if "!HOST_DOMAIN!"=="" set HOST_DOMAIN=!HOST_DOMAIN_DEFAULT!

echo.
echo ═══════════════════════════════════════════════════════════════
echo   Configuración de Seguridad
echo ═══════════════════════════════════════════════════════════════
set /p CORE_JWT_SECRET_KEY="JWT Secret Key [%CORE_JWT_SECRET_KEY_DEFAULT%]: "
if "!CORE_JWT_SECRET_KEY!"=="" set CORE_JWT_SECRET_KEY=!CORE_JWT_SECRET_KEY_DEFAULT!

set /p APP_SIGNATURE_SECRET_KEY="App Signature Secret Key [%APP_SIGNATURE_SECRET_KEY_DEFAULT%]: "
if "!APP_SIGNATURE_SECRET_KEY!"=="" set APP_SIGNATURE_SECRET_KEY=!APP_SIGNATURE_SECRET_KEY_DEFAULT!

echo.
echo ═══════════════════════════════════════════════════════════════
echo   Configuración de Email (Gmail)
echo ═══════════════════════════════════════════════════════════════
set /p CORE_GOOGLE_USERNAME="Email de Gmail: "
set /p CORE_GOOGLE_PASSWORD="App Password de Gmail: "
set /p CORE_GOOGLE_HOST="Host SMTP [%CORE_GOOGLE_HOST_DEFAULT%]: "
if "!CORE_GOOGLE_HOST!"=="" set CORE_GOOGLE_HOST=!CORE_GOOGLE_HOST_DEFAULT!

set /p CORE_GOOGLE_PORT="Puerto SMTP [%CORE_GOOGLE_PORT_DEFAULT%]: "
if "!CORE_GOOGLE_PORT!"=="" set CORE_GOOGLE_PORT=!CORE_GOOGLE_PORT_DEFAULT!

echo.
echo ═══════════════════════════════════════════════════════════════
echo   Configuración de Google reCAPTCHA
echo ═══════════════════════════════════════════════════════════════
set /p GOOGLE_API_SECRET="Google reCAPTCHA Secret [%GOOGLE_API_SECRET_DEFAULT%]: "
if "!GOOGLE_API_SECRET!"=="" set GOOGLE_API_SECRET=!GOOGLE_API_SECRET_DEFAULT!

echo.
echo ═══════════════════════════════════════════════════════════════
echo   Variables Opcionales
echo ═══════════════════════════════════════════════════════════════
set /p DD_SITE="Datadog Site (opcional) [https://api.datadoghq.com]: "
if "!DD_SITE!"=="" set DD_SITE=https://api.datadoghq.com

set /p NEW_RELIC_LICENSE_KEY="New Relic License Key (opcional): "

REM Crear archivo .env
(
echo # Base de Datos
echo DB_CORE_URL=!DB_CORE_URL!
echo DB_CORE_USERNAME=!DB_CORE_USERNAME!
echo DB_CORE_PASSWORD=!DB_CORE_PASSWORD!
echo.
echo # Servidor
echo CORE_SERVER_PORT=!CORE_SERVER_PORT!
echo HOST_DOMAIN=!HOST_DOMAIN!
echo.
echo # JWT y Seguridad
echo CORE_JWT_SECRET_KEY=!CORE_JWT_SECRET_KEY!
echo APP_SIGNATURE_SECRET_KEY=!APP_SIGNATURE_SECRET_KEY!
echo.
echo # Email (Gmail)
echo CORE_GOOGLE_USERNAME=!CORE_GOOGLE_USERNAME!
echo CORE_GOOGLE_PASSWORD=!CORE_GOOGLE_PASSWORD!
echo CORE_GOOGLE_HOST=!CORE_GOOGLE_HOST!
echo CORE_GOOGLE_PORT=!CORE_GOOGLE_PORT!
echo.
echo # Google reCAPTCHA
echo GOOGLE_API_SECRET=!GOOGLE_API_SECRET!
echo.
echo # Opcionales
echo DD_SITE=!DD_SITE!
echo NEW_RELIC_LICENSE_KEY=!NEW_RELIC_LICENSE_KEY!
) > .env

echo.
echo ✅ Archivo .env creado exitosamente
echo.
exit /b 0

:check_env_vars
set HAS_CRITICAL=1
if not defined DB_CORE_URL set HAS_CRITICAL=0
if not defined DB_CORE_USERNAME set HAS_CRITICAL=0
if not defined DB_CORE_PASSWORD set HAS_CRITICAL=0
exit /b !HAS_CRITICAL!

:ensure_env
if exist ".env" (
    echo ✅ Archivo .env encontrado
    exit /b 0
)

call :check_env_vars
if %errorlevel%==0 (
    echo ✅ Variables de entorno del sistema detectadas
    echo ℹ️  Spring Boot usará las variables de entorno del sistema
    exit /b 0
)

echo ⚠️  Archivo .env no encontrado y variables críticas no definidas en el sistema
echo.
echo ℹ️  Opciones:
echo   1. Crear archivo .env (recomendado para desarrollo)
echo   2. Definir variables de entorno en el sistema
echo   3. Continuar de todas formas (usará valores por defecto de application.yml)
echo.
set /p CREATE_ENV="¿Deseas crear el archivo .env ahora? (S/n/c): "

if /i "!CREATE_ENV!"=="c" (
    echo ⚠️  Continuando con valores por defecto...
    exit /b 0
)

if /i not "!CREATE_ENV!"=="n" (
    call :create_env
    exit /b 0
)

echo ⚠️  No se creó .env. Asegúrate de tener las variables definidas en el sistema.
echo ℹ️  Variables requeridas: DB_CORE_URL, DB_CORE_USERNAME, DB_CORE_PASSWORD
echo.
set /p CONTINUE="¿Continuar de todas formas? (s/N): "
if /i not "!CONTINUE!"=="s" (
    echo Ejecución cancelada.
    exit /b 1
)
exit /b 0

:load_env
if not exist ".env" (
    echo ℹ️  Usando variables de entorno del sistema
    exit /b 0
)

echo ℹ️  Cargando variables de entorno desde .env...
for /f "usebackq eol=# tokens=1,* delims==" %%a in (".env") do (
    if not "%%a"=="" (
        if not "%%b"=="" (
            set "key=%%a"
            set "value=%%b"
            
            REM Trim espacios de la clave
            for /f "tokens=*" %%k in ("!key!") do set "key=%%k"
            
            REM Remover comillas del valor si existen
            if "!value:~0,1!"=="\"" (
                set "value=!value:~1!"
                if "!value:~-1!"=="\"" set "value=!value:~0,-1!"
            )
            if "!value:~0,1!"=="'" (
                set "value=!value:~1!"
                if "!value:~-1!"=="'" set "value=!value:~0,-1!"
            )
            
            REM Exportar variable
            set "!key!=!value!"
        )
    )
)
exit /b 0

:run_app
set BACKGROUND=%1
echo ℹ️  Iniciando la aplicación...

if "%BACKGROUND%"=="true" (
    echo ℹ️  Iniciando aplicación en segundo plano...
    start /B mvnw.cmd spring-boot:run > logs\app.log 2>&1
    timeout /t 2 /nobreak >nul
    echo ✅ Aplicación iniciada en segundo plano
    echo ℹ️  Logs: type logs\app.log
    echo ℹ️  Para detener: run.bat stop
) else (
    call mvnw.cmd spring-boot:run
)
exit /b 0

:stop_app
echo ℹ️  Deteniendo aplicación...
set FOUND=0
for /f "tokens=2" %%a in ('tasklist /FI "IMAGENAME eq java.exe" /FO LIST ^| findstr /I "PID"') do (
    set PID=%%a
    tasklist /FI "PID eq !PID!" /FO LIST | findstr /I "spring-boot:run" >nul
    if !errorlevel!==0 (
        set FOUND=1
        taskkill /PID !PID! /F >nul 2>&1
        echo ✅ Aplicación detenida (PID: !PID!)
        goto :stop_done
    )
)
if !FOUND!==0 (
    echo ⚠️  No se encontró proceso de la aplicación corriendo
)
:stop_done
exit /b 0

:status_app
tasklist /FI "IMAGENAME eq java.exe" /FO LIST | findstr /I "PID" >nul
if %errorlevel%==0 (
    for /f "tokens=2" %%a in ('tasklist /FI "IMAGENAME eq java.exe" /FO LIST ^| findstr /I "PID"') do (
        set PID=%%a
        tasklist /FI "PID eq !PID!" /FO LIST | findstr /I "spring-boot:run" >nul
        if !errorlevel!==0 (
            echo ✅ Aplicación corriendo (PID: !PID!)
            echo ℹ️  Para ver logs: type logs\app.log
            echo ℹ️  Para detener: run.bat stop
            exit /b 0
        )
    )
    echo ❌ Aplicación no está corriendo
) else (
    echo ❌ Aplicación no está corriendo
)
exit /b 1

:end
endlocal
