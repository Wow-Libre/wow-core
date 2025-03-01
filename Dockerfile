# Build stage
FROM openjdk:17-slim AS builder

WORKDIR /app

# Copiar el archivo de configuración de Maven y el wrapper
COPY ./pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Corregir los finales de línea y establecer permisos
RUN sed -i 's/\r$//' ./mvnw && chmod +x ./mvnw

# Descargar las dependencias (capa de caché)
RUN ./mvnw dependency:go-offline -B

# Copiar el código fuente de la aplicación
COPY ./src ./src

# Compilar la aplicación
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM openjdk:17-slim

WORKDIR /app

# Establecer modo no interactivo para evitar problemas en apt-get
ENV DEBIAN_FRONTEND=noninteractive

# Instalar curl y unzip sin recomendaciones extra
RUN apt-get update && \
    apt-get install -y --no-install-recommends curl unzip && \
    rm -rf /var/lib/apt/lists/*

# Descargar y descomprimir el agente de New Relic
RUN mkdir -p /usr/local/newrelic && \
    curl -O https://download.newrelic.com/newrelic/java-agent/newrelic-agent/current/newrelic-java.zip && \
    unzip newrelic-java.zip -d /usr/local/newrelic && \
    rm newrelic-java.zip

# Copiar el archivo JAR desde la etapa de construcción (builder)
COPY --from=builder /app/target/wowlibre-0.0.1-SNAPSHOT.jar .

ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8091

# Iniciar la aplicación con el agente de New Relic.
# Asegúrate de que la ruta al newrelic.jar sea la correcta según la estructura descomprimida.
ENTRYPOINT ["java", "-javaagent:/usr/local/newrelic/newrelic/newrelic.jar", "-jar", "wowlibre-0.0.1-SNAPSHOT.jar"]
