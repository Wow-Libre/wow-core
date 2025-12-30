#!/bin/bash

# 🚀 Wow Libre Core - Script de Ejecución Simplificado
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

# Crear archivo .env interactivamente
create_env() {
    print_info "Configurando archivo .env..."
    echo ""
    
    # Valores por defecto
    DB_CORE_URL_DEFAULT="jdbc:mysql://localhost:3306/platform"
    DB_CORE_USERNAME_DEFAULT="root"
    DB_CORE_PASSWORD_DEFAULT=""
    CORE_SERVER_PORT_DEFAULT="8091"
    HOST_DOMAIN_DEFAULT="http://localhost:3000"
    CORE_JWT_SECRET_KEY_DEFAULT="6E4D574873506B4A72434B6A614B39786F736B7855666B4D456A6E466F785572"
    CORE_GOOGLE_USERNAME_DEFAULT=""
    CORE_GOOGLE_PASSWORD_DEFAULT=""
    CORE_GOOGLE_HOST_DEFAULT="smtp.gmail.com"
    CORE_GOOGLE_PORT_DEFAULT="587"
    GOOGLE_API_SECRET_DEFAULT="6Lcd3iArAAAAAMBZ30BN1hry_nhXsfnoHQWIfejg"
    APP_SIGNATURE_SECRET_KEY_DEFAULT="wowLibreSecret"
    
    # Función helper para leer input con valor por defecto
    read_with_default() {
        local prompt="$1"
        local default="$2"
        local secret="$3"
        local value
        
        if [ "$secret" = "true" ]; then
            read -sp "$prompt [$default]: " value
            echo ""
        else
            read -p "$prompt [$default]: " value
        fi
        
        echo "${value:-$default}"
    }
    
    echo -e "${YELLOW}═══════════════════════════════════════════════════════════════${NC}"
    echo -e "${YELLOW}  Configuración de Base de Datos${NC}"
    echo -e "${YELLOW}═══════════════════════════════════════════════════════════════${NC}"
    DB_CORE_URL=$(read_with_default "URL de Base de Datos" "$DB_CORE_URL_DEFAULT")
    DB_CORE_USERNAME=$(read_with_default "Usuario de Base de Datos" "$DB_CORE_USERNAME_DEFAULT")
    DB_CORE_PASSWORD=$(read_with_default "Contraseña de Base de Datos" "" "true")
    
    echo ""
    echo -e "${YELLOW}═══════════════════════════════════════════════════════════════${NC}"
    echo -e "${YELLOW}  Configuración del Servidor${NC}"
    echo -e "${YELLOW}═══════════════════════════════════════════════════════════════${NC}"
    CORE_SERVER_PORT=$(read_with_default "Puerto del Servidor" "$CORE_SERVER_PORT_DEFAULT")
    HOST_DOMAIN=$(read_with_default "Dominio/Host" "$HOST_DOMAIN_DEFAULT")
    
    echo ""
    echo -e "${YELLOW}═══════════════════════════════════════════════════════════════${NC}"
    echo -e "${YELLOW}  Configuración de Seguridad${NC}"
    echo -e "${YELLOW}═══════════════════════════════════════════════════════════════${NC}"
    CORE_JWT_SECRET_KEY=$(read_with_default "JWT Secret Key" "$CORE_JWT_SECRET_KEY_DEFAULT")
    APP_SIGNATURE_SECRET_KEY=$(read_with_default "App Signature Secret Key" "$APP_SIGNATURE_SECRET_KEY_DEFAULT")
    
    echo ""
    echo -e "${YELLOW}═══════════════════════════════════════════════════════════════${NC}"
    echo -e "${YELLOW}  Configuración de Email (Gmail)${NC}"
    echo -e "${YELLOW}═══════════════════════════════════════════════════════════════${NC}"
    CORE_GOOGLE_USERNAME=$(read_with_default "Email de Gmail" "$CORE_GOOGLE_USERNAME_DEFAULT")
    CORE_GOOGLE_PASSWORD=$(read_with_default "App Password de Gmail" "" "true")
    CORE_GOOGLE_HOST=$(read_with_default "Host SMTP" "$CORE_GOOGLE_HOST_DEFAULT")
    CORE_GOOGLE_PORT=$(read_with_default "Puerto SMTP" "$CORE_GOOGLE_PORT_DEFAULT")
    
    echo ""
    echo -e "${YELLOW}═══════════════════════════════════════════════════════════════${NC}"
    echo -e "${YELLOW}  Configuración de Google reCAPTCHA${NC}"
    echo -e "${YELLOW}═══════════════════════════════════════════════════════════════${NC}"
    GOOGLE_API_SECRET=$(read_with_default "Google reCAPTCHA Secret" "$GOOGLE_API_SECRET_DEFAULT")
    
    echo ""
    echo -e "${YELLOW}═══════════════════════════════════════════════════════════════${NC}"
    echo -e "${YELLOW}  Variables Opcionales${NC}"
    echo -e "${YELLOW}═══════════════════════════════════════════════════════════════${NC}"
    DD_SITE=$(read_with_default "Datadog Site (opcional)" "https://api.datadoghq.com")
    NEW_RELIC_LICENSE_KEY=$(read_with_default "New Relic License Key (opcional)" "")
    
    # Crear archivo .env
    cat > .env << EOF
# Base de Datos
DB_CORE_URL=$DB_CORE_URL
DB_CORE_USERNAME=$DB_CORE_USERNAME
DB_CORE_PASSWORD=$DB_CORE_PASSWORD

# Servidor
CORE_SERVER_PORT=$CORE_SERVER_PORT
HOST_DOMAIN=$HOST_DOMAIN

# JWT y Seguridad
CORE_JWT_SECRET_KEY=$CORE_JWT_SECRET_KEY
APP_SIGNATURE_SECRET_KEY=$APP_SIGNATURE_SECRET_KEY

# Email (Gmail)
CORE_GOOGLE_USERNAME=$CORE_GOOGLE_USERNAME
CORE_GOOGLE_PASSWORD=$CORE_GOOGLE_PASSWORD
CORE_GOOGLE_HOST=$CORE_GOOGLE_HOST
CORE_GOOGLE_PORT=$CORE_GOOGLE_PORT

# Google reCAPTCHA
GOOGLE_API_SECRET=$GOOGLE_API_SECRET

# Opcionales
DD_SITE=$DD_SITE
NEW_RELIC_LICENSE_KEY=$NEW_RELIC_LICENSE_KEY
EOF
    
    print_success "Archivo .env creado exitosamente"
    echo ""
}

# Verificar variables críticas en el sistema
check_env_vars() {
    local missing_vars=()
    local critical_vars=("DB_CORE_URL" "DB_CORE_USERNAME" "DB_CORE_PASSWORD")
    local has_critical=true
    
    for var in "${critical_vars[@]}"; do
        if [ -z "${!var}" ]; then
            missing_vars+=("$var")
            has_critical=false
        fi
    done
    
    if [ "$has_critical" = false ]; then
        return 1
    fi
    return 0
}

# Verificar y crear .env si no existe
ensure_env() {
    # Si existe .env, cargarlo
    if [ -f ".env" ]; then
        print_success "Archivo .env encontrado"
        return 0
    fi
    
    # Verificar si las variables críticas están en el sistema
    if check_env_vars; then
        print_success "Variables de entorno del sistema detectadas"
        print_info "Spring Boot usará las variables de entorno del sistema"
        return 0
    fi
    
    # Si no hay .env ni variables del sistema, sugerir crear .env
    print_warning "Archivo .env no encontrado y variables críticas no definidas en el sistema"
    echo ""
    print_info "Opciones:"
    echo "  1. Crear archivo .env (recomendado para desarrollo)"
    echo "  2. Definir variables de entorno en el sistema"
    echo "  3. Continuar de todas formas (usará valores por defecto de application.yml)"
    echo ""
    read -p "¿Deseas crear el archivo .env ahora? (S/n/c): " -n 1 -r
    echo ""
    
    if [[ $REPLY =~ ^[Cc]$ ]]; then
        print_warning "Continuando con valores por defecto..."
        return 0
    elif [[ ! $REPLY =~ ^[Nn]$ ]]; then
        create_env
    else
        print_warning "No se creó .env. Asegúrate de tener las variables definidas en el sistema."
        print_info "Variables requeridas: DB_CORE_URL, DB_CORE_USERNAME, DB_CORE_PASSWORD"
        echo ""
        read -p "¿Continuar de todas formas? (s/N): " -n 1 -r
        echo ""
        if [[ ! $REPLY =~ ^[Ss]$ ]]; then
            print_info "Ejecución cancelada."
            exit 1
        fi
    fi
}

# Cargar variables de entorno
load_env() {
    if [ -f ".env" ]; then
        print_info "Cargando variables de entorno desde .env..."
        # Cargar .env de forma segura línea por línea
        while IFS= read -r line || [ -n "$line" ]; do
            # Ignorar líneas vacías y comentarios
            [[ -z "$line" || "$line" =~ ^[[:space:]]*# ]] && continue
            
            # Ignorar líneas que no son asignaciones válidas (KEY=VALUE)
            [[ ! "$line" =~ ^[[:space:]]*[A-Za-z_][A-Za-z0-9_]*= ]] && continue
            
            # Separar clave y valor en el primer =
            local key="${line%%=*}"
            key=$(echo "$key" | xargs)  # Trim espacios
            
            local value="${line#*=}"
            # Trim espacios al inicio del valor
            value="${value#"${value%%[![:space:]]*}"}"
            
            # Remover comillas externas si existen (solo si envuelven todo el valor)
            if [[ "$value" =~ ^\"(.*)\"$ ]] || [[ "$value" =~ ^\'(.*)\'$ ]]; then
                value="${BASH_REMATCH[1]}"
            fi
            
            # Exportar de forma segura usando declare -x (más seguro que eval)
            declare -x "$key"="$value" 2>/dev/null || export "$key"="$value" 2>/dev/null || true
        done < .env
    else
        print_info "Usando variables de entorno del sistema"
    fi
}

# Ejecutar la aplicación
run_app() {
    local background=$1
    print_info "Iniciando la aplicación..."
    load_env
    
    # Crear script temporal con variables exportadas para pasarlas al proceso hijo
    local env_script=$(mktemp)
    if [ -f ".env" ]; then
        # Exportar todas las variables del .env
        while IFS= read -r line || [ -n "$line" ]; do
            [[ -z "$line" || "$line" =~ ^[[:space:]]*# ]] && continue
            [[ ! "$line" =~ ^[[:space:]]*[A-Za-z_][A-Za-z0-9_]*= ]] && continue
            
            local key="${line%%=*}"
            key=$(echo "$key" | xargs)
            local value="${line#*=}"
            value="${value#"${value%%[![:space:]]*}"}"
            
            if [[ "$value" =~ ^\"(.*)\"$ ]] || [[ "$value" =~ ^\'(.*)\'$ ]]; then
                value="${BASH_REMATCH[1]}"
            fi
            
            echo "export $key=\"$value\"" >> "$env_script"
        done < .env
    fi
    
    if [ "$background" = true ]; then
        print_info "Iniciando aplicación en segundo plano..."
        # Ejecutar con las variables exportadas
        if [ -f "$env_script" ] && [ -s "$env_script" ]; then
            nohup bash -c "source $env_script && ./mvnw spring-boot:run" > logs/app.log 2>&1 &
        else
            nohup ./mvnw spring-boot:run > logs/app.log 2>&1 &
        fi
        local pid=$!
        echo $pid > .app.pid
        rm -f "$env_script"
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
        if [ -f "$env_script" ] && [ -s "$env_script" ]; then
            bash -c "source $env_script && ./mvnw spring-boot:run"
            rm -f "$env_script"
        else
            ./mvnw spring-boot:run
        fi
    fi
}

# Detener la aplicación
stop_app() {
    local pid_file=".app.pid"
    local pid=""
    
    if [ -f "$pid_file" ]; then
        pid=$(cat "$pid_file")
    fi
    
    if [ -z "$pid" ] || ! ps -p $pid > /dev/null 2>&1; then
        print_warning "Buscando proceso de la aplicación..."
        pid=$(pgrep -f "spring-boot:run" || pgrep -f "wowlibre-0.0.1-SNAPSHOT.jar" || echo "")
        if [ -z "$pid" ]; then
            print_warning "No se encontró proceso de la aplicación corriendo."
            rm -f "$pid_file"
            return 1
        fi
    fi
    
    print_info "Deteniendo aplicación (PID: $pid)..."
    kill $pid 2>/dev/null || true
    
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
    local pid=""
    
    if [ -f "$pid_file" ]; then
        pid=$(cat "$pid_file")
        if ps -p $pid > /dev/null 2>&1; then
            print_success "✅ Aplicación corriendo (PID: $pid)"
            print_info "Para ver logs: tail -f logs/app.log"
            print_info "Para detener: ./run.sh stop"
            return 0
        else
            rm -f "$pid_file"
        fi
    fi
    
    pid=$(pgrep -f "spring-boot:run" || pgrep -f "wowlibre-0.0.1-SNAPSHOT.jar" || echo "")
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
    echo "🚀 Wow Libre Core - Script de Ejecución Simplificado"
    echo ""
    echo "Uso: ./run.sh [OPCIÓN]"
    echo ""
    echo "Opciones:"
    echo "  setup        Crea/configura el archivo .env interactivamente"
    echo "  start        Ejecuta la aplicación en segundo plano"
    echo "  stop         Detiene la aplicación"
    echo "  status       Muestra el estado de la aplicación"
    echo "  help         Muestra esta ayuda"
    echo ""
    echo "Variables de Entorno:"
    echo "  El script puede usar variables de dos formas:"
    echo "  1. Archivo .env en la raíz del proyecto (recomendado para desarrollo)"
    echo "  2. Variables de entorno del sistema (útil para producción/Docker)"
    echo ""
    echo "  Si tienes las variables definidas en el sistema, no necesitas .env"
    echo ""
    echo "Ejemplos:"
    echo "  ./run.sh setup        # Configurar .env"
    echo "  ./run.sh start        # Iniciar aplicación (background)"
    echo "  ./run.sh              # Iniciar aplicación (foreground)"
    echo "  ./run.sh stop         # Detener aplicación"
    echo "  ./run.sh status       # Ver estado"
    echo ""
    echo "Variables críticas requeridas:"
    echo "  DB_CORE_URL, DB_CORE_USERNAME, DB_CORE_PASSWORD"
    echo ""
}

# Main
main() {
    # Crear directorio de logs si no existe
    mkdir -p logs
    
    case "${1:-}" in
        setup)
            create_env
            ;;
        start)
            check_java
            check_maven
            ensure_env
            run_app true
            ;;
        stop)
            stop_app
            ;;
        status)
            status_app
            ;;
        help|--help|-h)
            show_help
            ;;
        "")
            # Sin argumentos: ejecutar en foreground
            check_java
            check_maven
            ensure_env
            run_app false
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

