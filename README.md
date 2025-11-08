<div align="center">

# 🎮 Wow Libre Core

**Plataforma de gestión completa para servidores World of Warcraft privados**

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Maven](https://img.shields.io/badge/Maven-3.6+-red.svg)](https://maven.apache.org/)

*API REST robusta y escalable para la administración de servidores WoW privados*

[🚀 Quick Start](#-quick-start) • [📚 Documentación](#-documentación) • [🛠️ Tecnologías](#️-tecnologías) • [🤝 Contribuir](#-contribuir)

</div>

---

## ✨ Características Principales

### 🔐 Autenticación y Seguridad
- **Autenticación JWT** con refresh tokens
- **Roles y permisos** (Admin, Client, Support)
- **Verificación de email** con códigos OTP
- **Recuperación de contraseñas** segura
- **Google reCAPTCHA** integrado

### 🎯 Gestión de Servidores
- **Multi-realm support** - Gestiona múltiples servidores
- **Configuración flexible** de realms (TrinityCore, etc.)
- **API Key management** para integración con servidores
- **Dashboard administrativo** completo

### 💰 Sistema Bancario
- **Préstamos en oro** con planes configurables
- **Gestión de deudas** y pagos
- **Múltiples planes** de préstamo (Inicial, Starter, Pro)
- **Sistema de intereses** configurable

### 👥 Gestión de Usuarios
- **Registro y autenticación** de usuarios
- **Perfiles de usuario** completos
- **Gestión de cuentas de juego** vinculadas
- **Sistema de máquinas/puntos**

### 🏰 Sistema de Guilds
- **Gestión completa de guilds**
- **Beneficios de guild** configurables
- **Sistema de reclamos** de beneficios
- **Integración con servidores** de juego

### 📢 Contenido y Marketing
- **Sistema de noticias** con secciones
- **Banners publicitarios** (imágenes y videos)
- **Promociones** por realm y nivel
- **Publicidad de realms** multiidioma

### 🎁 Promociones y Beneficios
- **Sistema de promociones** avanzado
- **Items y recompensas** configurables
- **Filtros por nivel y clase**
- **Seguimiento de reclamos**

### 🌐 Internacionalización
- **Multiidioma** (Español, Inglés, Portugués)
- **Contenido localizado** por idioma
- **FAQs** por idioma y tipo

### 📊 Dashboard y Analytics
- **Métricas en tiempo real**
- **Gestión de préstamos** y pagos
- **Estadísticas de usuarios** y realms
- **Gráficos y reportes**

### 🔌 Integración
- **API REST** completa y documentada
- **Swagger/OpenAPI** integrado
- **Integración con servidores** de juego
- **Webhooks** y callbacks

---

## 🚀 Quick Start

### Prerrequisitos

- ☕ **Java 21** o superior
- 🗄️ **MySQL 8.0+** o **MariaDB 10.5+**
- 📦 **Maven 3.6+** (o usar el wrapper incluido)

### 📦 Instalación de Prerrequisitos

#### ☕ Instalar Java 21

**macOS (usando Homebrew):**
```bash
# Instalar Homebrew si no lo tienes
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Instalar Java 21
brew install openjdk@21

# Configurar JAVA_HOME (agregar a ~/.zshrc o ~/.bash_profile)
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 21)' >> ~/.zshrc
echo 'export PATH="$JAVA_HOME/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

# Verificar instalación
java -version
```

**Linux (Ubuntu/Debian):**
```bash
# Actualizar repositorios
sudo apt update

# Instalar Java 21
sudo apt install openjdk-21-jdk

# Verificar instalación
java -version

# Configurar JAVA_HOME (opcional)
echo 'export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64' >> ~/.bashrc
source ~/.bashrc
```

**Linux (CentOS/RHEL/Fedora):**
```bash
# Instalar Java 21
sudo dnf install java-21-openjdk-devel

# Verificar instalación
java -version
```

**Windows:**
1. Descargar Java 21 desde [Oracle](https://www.oracle.com/java/technologies/downloads/#java21) o [Adoptium](https://adoptium.net/)
2. Ejecutar el instalador y seguir las instrucciones
3. Verificar instalación:
   ```cmd
   java -version
   ```
4. Configurar variables de entorno (opcional):
   - Ir a `Configuración del sistema` > `Variables de entorno`
   - Agregar `JAVA_HOME` apuntando a la carpeta de instalación (ej: `C:\Program Files\Java\jdk-21`)
   - Agregar `%JAVA_HOME%\bin` al `PATH`

#### 📦 Instalar Maven

**Nota:** El proyecto incluye Maven Wrapper (`mvnw`), por lo que **no es necesario instalar Maven** si usas los scripts de ejecución. Sin embargo, si prefieres instalar Maven globalmente:

**macOS (usando Homebrew):**
```bash
brew install maven

# Verificar instalación
mvn -version
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt update
sudo apt install maven

# Verificar instalación
mvn -version
```

**Linux (CentOS/RHEL/Fedora):**
```bash
sudo dnf install maven

# Verificar instalación
mvn -version
```

**Windows:**
1. Descargar Maven desde [Apache Maven](https://maven.apache.org/download.cgi)
2. Extraer el archivo ZIP (ej: `C:\Program Files\Apache\maven`)
3. Configurar variables de entorno:
   - Agregar `MAVEN_HOME` apuntando a la carpeta de Maven
   - Agregar `%MAVEN_HOME%\bin` al `PATH`
4. Verificar instalación:
   ```cmd
   mvn -version
   ```

**Alternativa: Usar Maven Wrapper (Recomendado)**
El proyecto incluye Maven Wrapper, por lo que puedes usar `./mvnw` (Linux/macOS) o `mvnw.cmd` (Windows) sin instalar Maven globalmente. Los scripts `run.sh` y `run.bat` lo usan automáticamente.

### Instalación Rápida

```bash
# 1. Clonar el repositorio
git clone https://github.com/wowlibre/wow-core.git
cd wow-core

# 2. Configurar base de datos
mysql -u root -p < src/main/resources/static/scripts/scripts.sql

# 3. Configurar variables de entorno
cp .env.example .env
# Editar .env con tus credenciales

# 4. Ejecutar la aplicación (método fácil)
./run.sh dev          # Linux/macOS
run.bat dev            # Windows

# O manualmente:
./mvnw clean install -DskipTests
./mvnw spring-boot:run
```

### 🐳 Con Docker

```bash
# Construir imagen
docker build -t wow-libre-core:latest .

# Ejecutar contenedor
docker run -d \
  -p 8091:8091 \
  --env-file .env \
  wow-libre-core:latest
```

### ✅ Verificar Instalación

```bash
# Health check
curl http://localhost:8091/core/actuator/health

# Swagger UI
open http://localhost:8091/core/swagger-ui/index.html
```

### 🎯 Script de Ejecución Rápida

Hemos incluido scripts para facilitar la ejecución de la aplicación:

**Linux/macOS:**
```bash
./run.sh dev          # Modo desarrollo (foreground)
./run.sh start        # Modo desarrollo (background) ⭐ NUEVO
./run.sh stop         # Detener aplicación en segundo plano ⭐ NUEVO
./run.sh status       # Ver estado de la aplicación ⭐ NUEVO
./run.sh build        # Solo compilar
./run.sh run          # Ejecutar JAR compilado
./run.sh run prod     # Ejecutar con perfil de producción
./run.sh check        # Verificar dependencias
./run.sh help         # Ver ayuda
```

**Windows:**
```cmd
run.bat dev           # Modo desarrollo (foreground)
run.bat start         # Modo desarrollo (background) ⭐ NUEVO
run.bat stop          # Detener aplicación ⭐ NUEVO
run.bat status        # Ver estado ⭐ NUEVO
run.bat build         # Solo compilar
run.bat run           # Ejecutar JAR compilado
run.bat run prod      # Ejecutar con perfil de producción
run.bat help          # Ver ayuda
```

#### ✨ Características del Script

**Verificaciones automáticas:**
- ✅ Java instalado (versión 21+)
- ✅ Maven wrapper disponible
- ✅ Archivo .env configurado (con advertencia llamativa si falta)
- ✅ Carga variables de entorno
- ✅ Compilación automática si es necesario

**Ejecución en segundo plano:**
- 🚀 `start` - Ejecuta la aplicación en segundo plano
- 📝 Logs guardados automáticamente en `logs/app.log`
- 🛑 `stop` - Detiene la aplicación de forma segura
- 📊 `status` - Verifica si la aplicación está corriendo

**Ejemplos de uso:**
```bash
# Iniciar en segundo plano
./run.sh start

# Ver logs en tiempo real
tail -f logs/app.log

# Verificar estado
./run.sh status

# Detener aplicación
./run.sh stop
```

---

## 📚 Documentación

### 📖 Guías Disponibles

- 📘 [Guía de Instalación Completa](wiki/Setup-Guide) - Configuración paso a paso
- 🗄️ [Guía de Scripts SQL](wiki/Database-Scripts-Guide) - Configuración de base de datos
- 🔧 [Variables de Entorno](wiki/Setup-Guide#variables-de-entorno) - Configuración completa
- 🐳 [Docker Setup](wiki/Setup-Guide#instalación-con-docker) - Despliegue con contenedores

### 🔗 Enlaces Útiles

- **Swagger UI**: `http://localhost:8091/core/swagger-ui/index.html`
- **Health Check**: `http://localhost:8091/core/actuator/health`
- **API Base**: `http://localhost:8091/core/api`

---

## 🛠️ Tecnologías

### Backend
- **Java 21** - Lenguaje de programación
- **Spring Boot 3.5.3** - Framework principal
- **Spring Security** - Autenticación y autorización
- **Spring Data JPA** - Persistencia de datos
- **Spring Web** - API REST

### Base de Datos
- **MySQL 8.0+** - Base de datos relacional
- **HikariCP** - Connection pooling
- **JPA/Hibernate** - ORM

### Seguridad
- **JWT** - JSON Web Tokens
- **BCrypt** - Encriptación de contraseñas
- **Spring Security** - Framework de seguridad

### Documentación
- **Swagger/OpenAPI 3** - Documentación de API
- **SpringDoc** - Integración Swagger

### Herramientas
- **Maven** - Gestión de dependencias
- **Docker** - Contenedores
- **New Relic** - Monitoreo (opcional)

---

## 📁 Estructura del Proyecto

```
wow-core/
├── src/
│   ├── main/
│   │   ├── java/com/register/wowlibre/
│   │   │   ├── application/          # Capa de aplicación (servicios)
│   │   │   ├── domain/               # Capa de dominio
│   │   │   │   ├── dto/              # Data Transfer Objects
│   │   │   │   ├── enums/            # Enumeraciones
│   │   │   │   ├── exception/        # Excepciones personalizadas
│   │   │   │   ├── model/            # Modelos de dominio
│   │   │   │   ├── port/             # Puertos (interfaces)
│   │   │   │   └── security/         # Configuración de seguridad
│   │   │   └── infrastructure/        # Capa de infraestructura
│   │   │       ├── client/           # Clientes HTTP
│   │   │       ├── config/          # Configuraciones
│   │   │       ├── controller/       # Controladores REST
│   │   │       ├── entities/         # Entidades JPA
│   │   │       ├── repositories/     # Adaptadores de repositorio
│   │   │       └── security/         # Filtros y configuración de seguridad
│   │   └── resources/
│   │       ├── application.yml       # Configuración de Spring
│   │       ├── static/              # Archivos estáticos
│   │       │   └── scripts/         # Scripts SQL
│   │       └── i18n/                # Internacionalización
│   └── test/                        # Tests unitarios e integración
├── Dockerfile                       # Configuración Docker
├── pom.xml                          # Configuración Maven
└── README.md                        # Este archivo
```

### 🏗️ Arquitectura

El proyecto sigue una **arquitectura hexagonal (Ports & Adapters)**:

- **Domain Layer**: Lógica de negocio pura
- **Application Layer**: Casos de uso y servicios
- **Infrastructure Layer**: Implementaciones técnicas

---

## 🔑 Variables de Entorno

Crea un archivo `.env` en la raíz del proyecto:

```env
# Base de Datos
DB_CORE_URL=jdbc:mysql://localhost:3306/platform
DB_CORE_USERNAME=root
DB_CORE_PASSWORD=tu_contraseña

# Servidor
CORE_SERVER_PORT=8091
HOST_DOMAIN=http://localhost:3000

# JWT
CORE_JWT_SECRET_KEY=tu_jwt_secret_key

# Email (Gmail)
CORE_GOOGLE_USERNAME=tu_email@gmail.com
CORE_GOOGLE_PASSWORD=tu_app_password
CORE_GOOGLE_HOST=smtp.gmail.com
CORE_GOOGLE_PORT=587

# Google reCAPTCHA
GOOGLE_API_SECRET=tu_recaptcha_secret

# New Relic (Opcional)
NEW_RELIC_LICENSE_KEY=tu_license_key
```

> 📝 Ver la [documentación completa](wiki/Setup-Guide#variables-de-entorno) para más detalles.

---

## 🧪 Testing

```bash
# Ejecutar todos los tests
./mvnw test

# Ejecutar tests específicos
./mvnw test -Dtest=AccountGameServiceTest

# Con cobertura
./mvnw test jacoco:report
```

### 📊 Cobertura de Tests

- ✅ **Services**: Cobertura completa
- ✅ **Controllers**: Tests unitarios
- ✅ **Repositories**: Tests de adaptadores
- ✅ **Integración**: Tests end-to-end

---

## 🚢 Despliegue

### Producción

```bash
# Compilar para producción
./mvnw clean package -DskipTests -Pprod

# Ejecutar JAR
java -jar target/wowlibre-0.0.1-SNAPSHOT.jar
```

### Docker

```bash
# Construir imagen
docker build -t wow-libre-core:latest .

# Ejecutar con variables de entorno
docker run -d \
  -p 8091:8091 \
  --env-file .env \
  --name wow-core \
  wow-libre-core:latest
```

### Docker Compose (Próximamente)

```yaml
# docker-compose.yml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8091:8091"
    environment:
      - DB_CORE_URL=jdbc:mysql://db:3306/platform
    depends_on:
      - db
  
  db:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: password
      MYSQL_DATABASE: platform
```

---

## 📈 Roadmap

### ✅ Completado
- [x] Autenticación y autorización JWT
- [x] Gestión de usuarios y realms
- [x] Sistema bancario de préstamos
- [x] Gestión de guilds
- [x] Sistema de promociones
- [x] API REST completa
- [x] Documentación Swagger
- [x] Tests unitarios

### 🚧 En Progreso
- [ ] Docker Compose completo
- [ ] Más tests de integración
- [ ] Métricas avanzadas

### 📋 Planeado
- [ ] Cache con Redis
- [ ] WebSockets para notificaciones
- [ ] Sistema de logs avanzado
- [ ] CI/CD pipeline

---

## 🤝 Contribuir

Las contribuciones son bienvenidas! Por favor:

1. 🌿 Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
2. 💾 Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
3. 📤 Push a la rama (`git push origin feature/AmazingFeature`)
4. 🔀 Abre un Pull Request

### 📝 Guías de Contribución

- Sigue las convenciones de código existentes
- Añade tests para nuevas funcionalidades
- Actualiza la documentación según sea necesario
- Asegúrate de que todos los tests pasen

---

## 📄 Licencia

Este proyecto está bajo la **Licencia MIT** - una licencia de código abierto completamente libre.

### ✅ ¿Qué permite esta licencia?

La Licencia MIT es una de las licencias de código abierto más permisivas y ampliamente utilizadas. Permite:

- ✅ **Uso comercial y no comercial** - Puedes usar este software en proyectos comerciales sin restricciones
- ✅ **Modificación** - Puedes modificar el código fuente según tus necesidades
- ✅ **Distribución** - Puedes distribuir el software original o modificado
- ✅ **Uso privado y público** - Sin restricciones de uso
- ✅ **Incorporación en proyectos propios** - Puedes integrarlo en tus propios proyectos
- ✅ **Venta del software** - Puedes vender el software o servicios basados en él
- ✅ **Sublicenciar** - Puedes usar una licencia diferente para tus modificaciones

### 📋 Única condición

**Debes incluir el aviso de copyright y la licencia completa** en todas las copias o partes sustanciales del software.

### 📖 Más información

- Ver el archivo [LICENSE](LICENSE) para el texto completo de la licencia (disponible en inglés y español)
- [Más información sobre la Licencia MIT](https://opensource.org/licenses/MIT)

---

## 👥 Equipo

Desarrollado con ❤️ por el equipo de **Wow Libre**

---

## 🙏 Agradecimientos

- **Spring Boot** - Framework increíble
- **MySQL** - Base de datos confiable
- **Comunidad WoW** - Por la inspiración

---

<div align="center">

### ⭐ Si este proyecto te resulta útil, ¡dale una estrella!

[⬆ Volver arriba](#-wow-libre-core)

**Hecho con ❤️ para la comunidad WoW**

</div>

