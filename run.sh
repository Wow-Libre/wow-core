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
        print_warning "Archivo .env no encontrado"
        if [ -f ".env.example" ]; then
            print_info "Copiando .env.example a .env..."
            cp .env.example .env
            print_warning "Por favor, edita el archivo .env con tus credenciales antes de continuar."
            read -p "¿Deseas continuar de todas formas? (s/N): " -n 1 -r
            echo
            if [[ ! $REPLY =~ ^[Ss]$ ]]; then
                exit 1
            fi
        else
            print_warning "Archivo .env.example no encontrado. Continuando sin variables de entorno..."
        fi
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
    print_info "Ejecutando en modo desarrollo con Maven..."
    load_env
    ./mvnw spring-boot:run
}

# Mostrar ayuda
show_help() {
    echo "🚀 Wow Libre Core - Script de Ejecución"
    echo ""
    echo "Uso: ./run.sh [OPCIÓN]"
    echo ""
    echo "Opciones:"
    echo "  dev          Ejecuta en modo desarrollo (spring-boot:run)"
    echo "  build        Solo compila la aplicación"
    echo "  run [perfil] Ejecuta el JAR compilado (opcional: perfil Spring)"
    echo "  check        Verifica dependencias y configuración"
    echo "  help         Muestra esta ayuda"
    echo ""
    echo "Ejemplos:"
    echo "  ./run.sh dev              # Modo desarrollo"
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
    case "${1:-dev}" in
        dev)
            check_java
            check_maven
            check_env
            load_env
            run_dev
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

