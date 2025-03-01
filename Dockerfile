# Etapa de construcción
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

# Etapa de ejecución
FROM openjdk:17-slim

WORKDIR /app

# Copiar el archivo JAR desde la etapa de construcción (builder)
COPY --from=builder /app/target/wowlibre-0.0.1-SNAPSHOT.jar /app/wowlibre-0.0.1-SNAPSHOT.jar

# Crear el directorio para New Relic
RUN mkdir -p /usr/local/newrelic

# Copiar los archivos de configuración de New Relic
COPY ./newrelic/newrelic.jar /usr/local/newrelic/newrelic.jar
COPY ./newrelic/newrelic.yml /usr/local/newrelic/newrelic.yml

# Definir el perfil de Spring activo
ENV SPRING_PROFILES_ACTIVE=prod

# Exponer el puerto de la aplicación
EXPOSE 8091

# Configurar la aplicación con el agente de New Relic
ENTRYPOINT ["java", "-javaagent:/usr/local/newrelic/newrelic.jar", "-jar", "/app/wowlibre-0.0.1-SNAPSHOT.jar"]
