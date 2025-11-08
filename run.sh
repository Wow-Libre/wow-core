#!/bin/bash

# 🚀 Wow Libre Core - Script de Ejecución
# Facilita el inicio de la aplicación Spring Boot

set -e

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Función para imprimir mensajes
print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Función para verificar si un comando existe
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Verificar Java
check_java() {
    if ! command_exists java; then
        print_error "Java no está instalado. Por favor instala Java 21 o superior."
        exit 1
    fi
    
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -lt 21 ]; then
        print_warning "Java $JAVA_VERSION detectado. Se recomienda Java 21 o superior."
    else
        print_success "Java $JAVA_VERSION detectado"
    fi
}

# Verificar Maven
check_maven() {
    if [ ! -f "./mvnw" ]; then
        print_error "Maven wrapper (mvnw) no encontrado. Asegúrate de estar en el directorio raíz del proyecto."
        exit 1
    fi
    
    chmod +x ./mvnw
    print_success "Maven wrapper encontrado"
}

# Verificar archivo .env
check_env() {
    if [ ! -f ".env" ]; then
        echo ""
        echo -e "${RED}╔════════════════════════════════════════════════════════════════╗${NC}"
        echo -e "${RED}║                    ⚠️  ADVERTENCIA IMPORTANTE ⚠️                  ║${NC}"
        echo -e "${RED}╠════════════════════════════════════════════════════════════════╣${NC}"
        echo -e "${RED}║${NC}  ${YELLOW}Archivo .env NO encontrado${NC}                                    ${RED}║${NC}"
        echo -e "${RED}║${NC}                                                              ${RED}║${NC}"
        echo -e "${RED}║${NC}  ${YELLOW}⚠️  La aplicación usará valores por DEFECTO${NC}                    ${RED}║${NC}"
        echo -e "${RED}║${NC}  ${YELLOW}⚠️  Esto puede causar errores de conexión a BD${NC}                 ${RED}║${NC}"
        echo -e "${RED}║${NC}  ${YELLOW}⚠️  y otros problemas de configuración${NC}                         ${RED}║${NC}"
        echo -e "${RED}║${NC}                                                              ${RED}║${NC}"
        echo -e "${RED}║${NC}  ${YELLOW}Recomendación:${NC}                                                ${RED}║${NC}"
        echo -e "${RED}║${NC}  1. Crea un archivo .env basado en .env.example            ${RED}║${NC}"
        echo -e "${RED}║${NC}  2. Configura tus credenciales de base de datos            ${RED}║${NC}"
        echo -e "${RED}║${NC}  3. Configura las demás variables de entorno              ${RED}║${NC}"
        echo -e "${RED}╚════════════════════════════════════════════════════════════════╝${NC}"
        echo ""
        
        if [ -f ".env.example" ]; then
            print_info "Se encontró .env.example. ¿Deseas copiarlo a .env? (S/n): "
            read -p "" -n 1 -r
            echo
            if [[ ! $REPLY =~ ^[Nn]$ ]]; then
                print_info "Copiando .env.example a .env..."
                cp .env.example .env
                echo ""
                print_warning "⚠️  IMPORTANTE: Edita el archivo .env con tus credenciales antes de continuar."
                echo ""
                read -p "¿Deseas continuar de todas formas? (s/N): " -n 1 -r
                echo
                if [[ ! $REPLY =~ ^[Ss]$ ]]; then
                    print_info "Ejecución cancelada. Configura el archivo .env y vuelve a intentar."
                    exit 1
                fi
            else
                print_warning "No se copió .env.example. La aplicación usará valores por defecto."
                echo ""
                read -p "¿Deseas continuar de todas formas? (s/N): " -n 1 -r
                echo
                if [[ ! $REPLY =~ ^[Ss]$ ]]; then
                    print_info "Ejecución cancelada."
                    exit 1
                fi
            fi
        else
            print_warning "Archivo .env.example no encontrado."
            echo ""
            print_warning "La aplicación se ejecutará con valores por defecto."
            echo ""
            read -p "¿Deseas continuar de todas formas? (s/N): " -n 1 -r
            echo
            if [[ ! $REPLY =~ ^[Ss]$ ]]; then
                print_info "Ejecución cancelada. Crea un archivo .env con tus variables de entorno."
                exit 1
            fi
        fi
        echo ""
    else
        print_success "Archivo .env encontrado"
    fi
}

# Cargar variables de entorno
load_env() {
    if [ -f ".env" ]; then
        print_info "Cargando variables de entorno desde .env..."
        export $(cat .env | grep -v '^#' | xargs)
    fi
}

# Compilar la aplicación
build_app() {
    local skip_tests=$1
    print_info "Compilando la aplicación..."
    
    if [ "$skip_tests" = true ]; then
        ./mvnw clean package -DskipTests
    else
        ./mvnw clean package
    fi
    
    if [ $? -eq 0 ]; then
        print_success "Compilación exitosa"
    else
        print_error "Error en la compilación"
        exit 1
    fi
}

# Ejecutar la aplicación
run_app() {
    local profile=$1
    local jar_file="target/wowlibre-0.0.1-SNAPSHOT.jar"
    
    if [ ! -f "$jar_file" ]; then
        print_warning "JAR no encontrado. Compilando..."
        build_app true
    fi
    
    print_info "Iniciando la aplicación..."
    
    if [ -n "$profile" ]; then
        print_info "Perfil activo: $profile"
        java -jar -Dspring.profiles.active="$profile" "$jar_file"
    else
        java -jar "$jar_file"
    fi
}

# Ejecutar con Maven (desarrollo)
run_dev() {
    local background=$1
    print_info "Ejecutando en modo desarrollo con Maven..."
    load_env
    
    # Verificar si necesita compilar
    local jar_file="target/wowlibre-0.0.1-SNAPSHOT.jar"
    if [ ! -f "$jar_file" ]; then
        print_warning "JAR no encontrado. Compilando primero..."
        build_app true
    else
        # Verificar si el código fuente es más reciente que el JAR
        local source_newer=false
        if [ -d "src" ]; then
            local latest_source=$(find src -type f -name "*.java" -newer "$jar_file" 2>/dev/null | head -1)
            if [ -n "$latest_source" ]; then
                source_newer=true
            fi
        fi
        
        if [ "$source_newer" = true ]; then
            print_info "Código fuente modificado. Recompilando..."
            build_app true
        fi
    fi
    
    if [ "$background" = true ]; then
        print_info "Iniciando aplicación en segundo plano..."
        nohup ./mvnw spring-boot:run > logs/app.log 2>&1 &
        local pid=$!
        echo $pid > .app.pid
        print_success "Aplicación iniciada en segundo plano (PID: $pid)"
        print_info "Logs: tail -f logs/app.log"
        print_info "Para detener: ./run.sh stop"
        echo ""
        sleep 2
        print_info "Verificando estado..."
        if ps -p $pid > /dev/null 2>&1; then
            print_success "✅ Aplicación corriendo correctamente"
        else
            print_error "❌ La aplicación se detuvo. Revisa los logs: cat logs/app.log"
        fi
    else
        ./mvnw spring-boot:run
    fi
}

# Detener la aplicación
stop_app() {
    local pid_file=".app.pid"
    
    if [ ! -f "$pid_file" ]; then
        print_warning "No se encontró archivo de PID. Buscando proceso..."
        local pid=$(pgrep -f "spring-boot:run" || pgrep -f "wowlibre-0.0.1-SNAPSHOT.jar" || echo "")
        if [ -z "$pid" ]; then
            print_warning "No se encontró proceso de la aplicación corriendo."
            return 1
        fi
    else
        local pid=$(cat "$pid_file")
    fi
    
    if [ -z "$pid" ]; then
        print_warning "No se encontró PID de la aplicación."
        rm -f "$pid_file"
        return 1
    fi
    
    if ! ps -p $pid > /dev/null 2>&1; then
        print_warning "El proceso $pid no está corriendo."
        rm -f "$pid_file"
        return 1
    fi
    
    print_info "Deteniendo aplicación (PID: $pid)..."
    kill $pid 2>/dev/null || true
    
    # Esperar un poco y verificar
    sleep 2
    if ps -p $pid > /dev/null 2>&1; then
        print_warning "El proceso no se detuvo. Forzando terminación..."
        kill -9 $pid 2>/dev/null || true
        sleep 1
    fi
    
    if ps -p $pid > /dev/null 2>&1; then
        print_error "No se pudo detener el proceso $pid"
        return 1
    else
        print_success "Aplicación detenida correctamente"
        rm -f "$pid_file"
        return 0
    fi
}

# Ver estado de la aplicación
status_app() {
    local pid_file=".app.pid"
    
    if [ -f "$pid_file" ]; then
        local pid=$(cat "$pid_file")
        if ps -p $pid > /dev/null 2>&1; then
            print_success "✅ Aplicación corriendo (PID: $pid)"
            print_info "Para ver logs: tail -f logs/app.log"
            print_info "Para detener: ./run.sh stop"
            return 0
        else
            print_warning "El PID $pid no está activo. Limpiando archivo..."
            rm -f "$pid_file"
        fi
    fi
    
    # Buscar proceso manualmente
    local pid=$(pgrep -f "spring-boot:run" || pgrep -f "wowlibre-0.0.1-SNAPSHOT.jar" || echo "")
    if [ -n "$pid" ]; then
        print_success "✅ Aplicación corriendo (PID: $pid)"
        print_info "Para detener: ./run.sh stop"
        return 0
    else
        print_warning "❌ Aplicación no está corriendo"
        return 1
    fi
}

# Mostrar ayuda
show_help() {
    echo "🚀 Wow Libre Core - Script de Ejecución"
    echo ""
    echo "Uso: ./run.sh [OPCIÓN]"
    echo ""
    echo "Opciones:"
    echo "  dev          Ejecuta en modo desarrollo (foreground)"
    echo "  start        Ejecuta en modo desarrollo (background)"
    echo "  stop         Detiene la aplicación en segundo plano"
    echo "  status       Muestra el estado de la aplicación"
    echo "  build        Solo compila la aplicación"
    echo "  run [perfil] Ejecuta el JAR compilado (opcional: perfil Spring)"
    echo "  check        Verifica dependencias y configuración"
    echo "  help         Muestra esta ayuda"
    echo ""
    echo "Ejemplos:"
    echo "  ./run.sh dev              # Modo desarrollo (foreground)"
    echo "  ./run.sh start            # Modo desarrollo (background)"
    echo "  ./run.sh stop             # Detener aplicación"
    echo "  ./run.sh status           # Ver estado"
    echo "  ./run.sh run              # Ejecuta JAR"
    echo "  ./run.sh run prod         # Ejecuta JAR con perfil prod"
    echo "  ./run.sh build            # Solo compilar"
    echo "  ./run.sh check            # Verificar configuración"
    echo ""
}

# Verificar todo
check_all() {
    print_info "Verificando dependencias y configuración..."
    echo ""
    check_java
    check_maven
    check_env
    echo ""
    print_success "Todas las verificaciones completadas"
}

# Main
main() {
    # Crear directorio de logs si no existe
    mkdir -p logs
    
    case "${1:-dev}" in
        dev)
            check_java
            check_maven
            check_env
            load_env
            run_dev false
            ;;
        start)
            check_java
            check_maven
            check_env
            load_env
            run_dev true
            ;;
        stop)
            stop_app
            ;;
        status)
            status_app
            ;;
        build)
            check_java
            check_maven
            build_app false
            ;;
        build-fast)
            check_java
            check_maven
            build_app true
            ;;
        run)
            check_java
            run_app "$2"
            ;;
        check)
            check_all
            ;;
        help|--help|-h)
            show_help
            ;;
        *)
            print_error "Opción desconocida: $1"
            echo ""
            show_help
            exit 1
            ;;
    esac
}

# Ejecutar
main "$@"

