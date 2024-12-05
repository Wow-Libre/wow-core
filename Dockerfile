# Build stage
FROM amazoncorretto:17 AS builder

WORKDIR /app

# Copiar archivos de configuración y el wrapper de Maven
COPY ./pom.xml ./
COPY .mvn .mvn
COPY mvnw .

# Configurar permisos y corregir saltos de línea
RUN sed -i 's/\r$//' ./mvnw && chmod +x ./mvnw

# Descargar dependencias
RUN ./mvnw dependency:go-offline -B

# Copiar el código fuente
COPY ./src ./src

# Construir la aplicación
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM amazoncorretto:17

WORKDIR /app

# Copiar el archivo JAR desde la etapa de construcción
COPY --from=builder /app/target/wowlibre-0.0.1-SNAPSHOT.jar .

# Configurar variables de entorno
ENV SPRING_PROFILES_ACTIVE=prod

# Exponer el puerto de la aplicación
EXPOSE 8091

# Iniciar la aplicación
ENTRYPOINT ["java", "-jar", "wowlibre-0.0.1-SNAPSHOT.jar"]
