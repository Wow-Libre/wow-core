# 📊 Guía de Configuración de Datadog

Esta guía te ayudará a configurar Datadog para monitorear tu aplicación Spring Boot.

---

## 🤔 ¿Cómo funciona Datadog?

### Arquitectura

```
┌─────────────────┐
│  Tu Aplicación   │
│  (Spring Boot)   │
└────────┬─────────┘
         │
         │ Métricas (HTTP/StatsD)
         │
    ┌────▼─────┐
    │ Datadog  │  ← Un Agent por máquina/host
    │  Agent   │
    └────┬─────┘
         │
         │ Agregación y envío
         │
    ┌────▼──────────┐
    │ Datadog Cloud │
    │  (Dashboard)  │
    └───────────────┘
```

### Respuesta a tu pregunta:

**¿Un Agent por app o por máquina?**
- ✅ **Un Agent por máquina/host** (no por aplicación)
- El Agent corre como un proceso en tu sistema operativo
- Todas las aplicaciones en esa máquina pueden enviar métricas al mismo Agent
- El Agent agrega y envía las métricas a Datadog Cloud

**Para desarrollo local tienes 2 opciones:**

1. **Opción Simple (Recomendada para empezar):** Enviar directamente a Datadog API
   - ✅ No necesitas instalar nada
   - ✅ Solo configuras variables de entorno
   - ✅ Funciona inmediatamente
   - ⚠️ Menos eficiente para producción (muchas conexiones HTTP)

2. **Opción Avanzada:** Instalar Datadog Agent local
   - ✅ Más eficiente (agregación local)
   - ✅ Mejor para producción
   - ⚠️ Requiere instalar el Agent

---

## 🚀 Opción 1: Envío Directo a Datadog API (Recomendado para Desarrollo)

### Paso 1: Obtener tu API Key de Datadog

1. Ve a [Datadog](https://app.datadoghq.com/)
2. Inicia sesión o crea una cuenta (tiene plan gratuito)
3. Ve a **Organization Settings** > **API Keys**
4. Crea una nueva API Key o copia una existente
5. Anota también tu **Site** (us1, us3, us5, eu, ap1, etc.)

### Paso 2: Configurar variables de entorno

Edita tu archivo `.env`:

```env
# Datadog
DD_API_KEY=tu_api_key_aqui
DD_SITE=datadoghq.com
DD_ENABLED=true
DD_ENV=local
```

### Paso 3: Reiniciar la aplicación

```bash
./run.sh stop
./run.sh start
```

### Paso 4: Verificar que funciona

1. Ve a [Datadog Metrics Explorer](https://app.datadoghq.com/metric/explorer)
2. Busca métricas que empiecen con `jvm.`, `http.`, `hikari.`, etc.
3. Deberías ver métricas apareciendo cada 10 segundos

---

## 🔧 Opción 2: Usar Datadog Agent Local (Para Producción)

### Paso 1: Instalar Datadog Agent

**macOS:**
```bash
# Instalar con Homebrew
brew install datadog-agent

# O descargar desde:
# https://app.datadoghq.com/account/settings/agent/latest?platform=macos
```

**Linux:**
```bash
# Ubuntu/Debian
DD_API_KEY=tu_api_key DD_SITE=datadoghq.com bash -c "$(curl -L https://s3.amazonaws.com/dd-agent/scripts/install_script_agent7.sh)"

# O seguir instrucciones en:
# https://app.datadoghq.com/account/settings/agent/latest?platform=linux
```

**Windows:**
1. Descarga el instalador desde: https://app.datadoghq.com/account/settings/agent/latest?platform=windows
2. Ejecuta el instalador
3. Configura tu API key durante la instalación

### Paso 2: Configurar el Agent

**macOS/Linux:**
```bash
# Editar configuración
sudo nano /etc/datadog-agent/datadog.yaml

# Configurar:
api_key: tu_api_key_aqui
site: datadoghq.com
```

**Windows:**
Editar: `C:\ProgramData\Datadog\datadog.yaml`

### Paso 3: Configurar StatsD en la aplicación

Edita `application.yml`:

```yaml
management:
  metrics:
    export:
      datadog:
        enabled: false  # Deshabilitar envío directo
        statsd:
          enabled: true
          host: localhost
          port: 8125
          protocol: UDP
```

### Paso 4: Reiniciar Agent y Aplicación

```bash
# Reiniciar Agent
sudo systemctl restart datadog-agent  # Linux
# O desde macOS: brew services restart datadog-agent

# Reiniciar aplicación
./run.sh stop
./run.sh start
```

---

## 📊 Métricas que se envían automáticamente

Con Micrometer + Datadog, Spring Boot envía automáticamente:

- **JVM Metrics**: Memoria, CPU, threads, GC
- **HTTP Metrics**: Requests, responses, latencia
- **Database Metrics**: Conexiones HikariCP, queries
- **Custom Metrics**: Las que definas con `@Timed`, `@Counted`, etc.

---

## 🧪 Verificar que funciona

### Método 1: Ver logs de la aplicación

```bash
tail -f logs/app.log | grep -i datadog
```

Deberías ver logs indicando que las métricas se están enviando.

### Método 2: Verificar en Datadog

1. Ve a [Datadog Metrics Explorer](https://app.datadoghq.com/metric/explorer)
2. Busca métricas como:
   - `jvm.memory.used`
   - `http.server.requests`
   - `hikari.connections.active`

### Método 3: Verificar endpoint de métricas

```bash
curl http://localhost:8091/core/actuator/metrics
```

---

## 🔍 Métricas Personalizadas

Puedes agregar métricas personalizadas en tu código:

```java
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;

@Service
public class MyService {
    private final Counter customCounter;
    
    public MyService(MeterRegistry registry) {
        this.customCounter = Counter.builder("wowlibre.custom.metric")
            .description("Mi métrica personalizada")
            .tag("service", "MyService")
            .register(registry);
    }
    
    public void doSomething() {
        customCounter.increment();
    }
}
```

---

## ⚙️ Configuración Avanzada

### Cambiar frecuencia de envío

En `application.yml`:
```yaml
management:
  metrics:
    export:
      datadog:
        step: 30s  # Enviar cada 30 segundos (default: 10s)
```

### Agregar tags personalizados

```yaml
management:
  metrics:
    export:
      datadog:
        tags:
          - env:local
          - service:wow-libre-core
          - team:backend
          - version:0.0.1
```

### Filtrar métricas

```yaml
management:
  metrics:
    export:
      datadog:
        enabled: true
    distribution:
      percentiles-histogram:
        http.server.requests: true
```

---

## 🐛 Solución de Problemas

### Las métricas no aparecen en Datadog

1. **Verifica tu API Key:**
   ```bash
   echo $DD_API_KEY
   ```

2. **Verifica que esté habilitado:**
   ```bash
   echo $DD_ENABLED
   ```

3. **Revisa los logs:**
   ```bash
   tail -f logs/app.log | grep -i "datadog\|metric"
   ```

4. **Verifica conectividad:**
   ```bash
   curl -X GET "https://api.datadoghq.com/api/v1/validate" \
     -H "DD-API-KEY: tu_api_key"
   ```

### Error: "Invalid API Key"

- Verifica que tu API key sea correcta
- Asegúrate de que no tenga espacios extra
- Verifica que el site sea correcto (us1, eu, etc.)

### Métricas aparecen con delay

- Normal: Datadog agrega métricas cada 10-60 segundos
- Puedes reducir el `step` en la configuración, pero aumenta el tráfico

---

## 📚 Recursos Adicionales

- [Documentación oficial de Micrometer Datadog](https://micrometer.io/docs/registry/datadog)
- [Datadog Metrics Documentation](https://docs.datadoghq.com/metrics/)
- [Spring Boot Actuator Metrics](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.metrics)

---

## 💡 Recomendación

**Para desarrollo local:**
- Usa **Opción 1** (API directa) - Es más simple y suficiente

**Para producción:**
- Usa **Opción 2** (Agent) - Es más eficiente y escalable

---

¿Necesitas ayuda? Abre un issue en el repositorio.

