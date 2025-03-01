# Etapa de construcción
FROM openjdk:17-slim AS builder

WORKDIR /app

# Copiar archivos de Maven y el wrapper
COPY ./pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Corregir permisos y finales de línea
RUN sed -i 's/\r$//' ./mvnw && chmod +x ./mvnw

# Descargar dependencias (capa de caché)
RUN ./mvnw dependency:go-offline -B

# Copiar el código fuente
COPY ./src ./src

# Compilar la aplicación
RUN ./mvnw clean package -DskipTests

# Etapa de ejecución
FROM openjdk:17-slim

WORKDIR /app

# Instalar curl y unzip
RUN apt-get update && apt-get install -y curl unzip && rm -rf /var/lib/apt/lists/*

# Crear el directorio para New Relic
RUN mkdir -p /usr/local/newrelic

# Descargar y extraer New Relic directamente en el contenedor
RUN curl -sSL "https://download.newrelic.com/newrelic/java-agent/newrelic-agent/current/newrelic-java.zip" -o /tmp/newrelic-java.zip \
    && unzip /tmp/newrelic-java.zip -d /tmp \
    && mv /tmp/newrelic/* /usr/local/newrelic/ \
    && rm -rf /tmp/newrelic /tmp/newrelic-java.zip

# Copiar el archivo JAR desde la etapa de construcción
COPY --from=builder /app/target/wowlibre-0.0.1-SNAPSHOT.jar /app/wowlibre-0.0.1-SNAPSHOT.jar

# Configurar variables de entorno de New Relic
ENV NEW_RELIC_APP_NAME="MiAplicacion"
ENV NEW_RELIC_LICENSE_KEY="TU_CLAVE_DE_NEW_RELIC"

# Exponer el puerto
EXPOSE 8091

# Iniciar la aplicación con el agente de New Relic
ENTRYPOINT ["java", "-javaagent:/usr/local/newrelic/newrelic.jar", "-jar", "/app/wowlibre-0.0.1-SNAPSHOT.jar"]
