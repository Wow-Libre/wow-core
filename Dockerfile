# Build stage
FROM openjdk:17-slim AS builder

WORKDIR /app

# Copiar el archivo de configuración de Maven y el wrapper
COPY ./pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Verificar permisos y existencia del archivo antes de modificarlo
RUN ls -l ./mvnw

# Corregir los finales de línea y establecer permisos
RUN sed 's/\r$//' ./mvnw > ./mvnw.new && mv ./mvnw.new ./mvnw && chmod +x ./mvnw

# Descargar las dependencias (capa de caché)
RUN ./mvnw dependency:go-offline -B

# Copiar el código fuente de la aplicación
COPY ./src ./src

# Compilar la aplicación
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM openjdk:17-slim

WORKDIR /app

# Copiar el archivo JAR desde la etapa de construcción (builder)
COPY --from=builder /app/target/wowlibre-0.0.1-SNAPSHOT.jar .

ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8091

ENTRYPOINT ["java", "-jar", "wowlibre-0.0.1-SNAPSHOT.jar"]
