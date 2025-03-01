# Etapa de construcción
FROM --platform=linux/arm64 openjdk:17-jdk AS builder

WORKDIR /app

# Copiar archivos de Maven y el wrapper
COPY ./pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Corregir permisos y finales de línea
RUN sed -i 's/\r$//' ./mvnw && chmod +x ./mvnw

# Descargar dependencias
RUN ./mvnw dependency:go-offline -B

# Copiar código fuente y compilar
COPY ./src ./src
RUN ./mvnw clean package -DskipTests

# Etapa de ejecución
FROM --platform=linux/arm64 openjdk:17-jdk

WORKDIR /app

# Instalar curl y unzip (sin modificar sources.list)
RUN apt-get update && apt-get install -y curl unzip && rm -rf /var/lib/apt/lists/*

# Crear el directorio para New Relic
RUN mkdir -p /usr/local/newrelic

# Descargar y extraer New Relic
RUN curl -sSL "https://download.newrelic.com/newrelic/java-agent/newrelic-agent/current/newrelic-java.zip" -o /tmp/newrelic-java.zip \
    && unzip /tmp/newrelic-java.zip -d /tmp \
    && mv /tmp/newrelic/* /usr/local/newrelic/ \
    && rm -rf /tmp/newrelic /tmp/newrelic-java.zip

# Copiar la aplicación compilada
COPY --from=builder /app/target/wowlibre-0.0.1-SNAPSHOT.jar /app/wowlibre-0.0.1-SNAPSHOT.jar

# Configurar variables de entorno
ENV NEW_RELIC_APP_NAME="wow libre"
ENV NEW_RELIC_LICENSE_KEY="8285c673cef713f1a0a57dc28158882cFFFFNRAL"

# Exponer el puerto
EXPOSE 8091

# Iniciar la aplicación con el agente de New Relic
ENTRYPOINT ["java", "-javaagent:/usr/local/newrelic/newrelic.jar", "-jar", "/app/wowlibre-0.0.1-SNAPSHOT.jar"]
