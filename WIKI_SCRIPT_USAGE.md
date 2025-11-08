# 🚀 Guía Rápida: Usar el Script de Ejecución

Esta guía te ayudará a ejecutar la aplicación de forma rápida y sencilla usando los scripts incluidos.

---

## ⚡ Inicio Rápido

### 1️⃣ Verificar que todo esté listo

```bash
./run.sh check
```

Este comando verifica:
- ✅ Java instalado
- ✅ Maven disponible
- ✅ Archivo `.env` configurado

---

## 🎯 Modos de Ejecución

### Modo Desarrollo (Foreground)
La aplicación se ejecuta en tu terminal. Verás los logs en tiempo real.

```bash
./run.sh dev
```

**Cuándo usarlo:** Cuando quieres ver los logs directamente o depurar.

**Para detener:** Presiona `Ctrl + C`

---

### Modo Desarrollo (Background) ⭐ Recomendado
La aplicación se ejecuta en segundo plano. Puedes seguir usando tu terminal.

```bash
./run.sh start
```

**Cuándo usarlo:** Cuando quieres trabajar en otras cosas mientras la app corre.

**Ventajas:**
- ✅ Tu terminal queda libre
- ✅ Logs guardados en `logs/app.log`
- ✅ Fácil de detener con `./run.sh stop`

---

## 📊 Comandos Útiles

### Ver el estado de la aplicación

```bash
./run.sh status
```

Te muestra si la aplicación está corriendo o no.

---

### Ver los logs en tiempo real

```bash
tail -f logs/app.log
```

Verás los logs actualizándose en tiempo real. Presiona `Ctrl + C` para salir.

---

### Detener la aplicación

```bash
./run.sh stop
```

Detiene la aplicación que está corriendo en segundo plano.

---

## 🔧 Otros Comandos

### Compilar la aplicación

```bash
./run.sh build
```

Solo compila sin ejecutar. Útil para verificar que no hay errores.

---

### Ver ayuda completa

```bash
./run.sh help
```

Muestra todos los comandos disponibles.

---

## 📝 Ejemplo de Flujo Completo

```bash
# 1. Verificar que todo esté bien
./run.sh check

# 2. Iniciar la aplicación en segundo plano
./run.sh start

# 3. Verificar que esté corriendo
./run.sh status

# 4. Ver los logs (en otra terminal)
tail -f logs/app.log

# 5. Cuando termines, detener la aplicación
./run.sh stop
```

---

## ⚠️ Solución de Problemas

### La aplicación no inicia

1. Verifica que Java esté instalado:
   ```bash
   java -version
   ```

2. Verifica que el archivo `.env` exista:
   ```bash
   ls -la .env
   ```

3. Revisa los logs:
   ```bash
   cat logs/app.log
   ```

### No puedo detener la aplicación

Si `./run.sh stop` no funciona, puedes detenerla manualmente:

```bash
# Buscar el proceso
ps aux | grep java

# Detener por PID (reemplaza 12345 con el PID real)
kill 12345
```

---

## 🪟 Para Usuarios de Windows

Si estás en Windows, usa `run.bat` en lugar de `run.sh`:

```cmd
run.bat start    # Iniciar
run.bat stop     # Detener
run.bat status   # Ver estado
run.bat help     # Ayuda
```

---

## 💡 Consejos

- **Primera vez:** Ejecuta `./run.sh check` para asegurarte de que todo esté configurado
- **Desarrollo diario:** Usa `./run.sh start` para tener la app corriendo en segundo plano
- **Depuración:** Usa `./run.sh dev` para ver los logs en tiempo real
- **Logs:** Siempre revisa `logs/app.log` si algo no funciona

---

## 🆘 ¿Necesitas Ayuda?

Si tienes problemas:

1. Revisa los logs: `cat logs/app.log`
2. Verifica el estado: `./run.sh status`
3. Consulta la documentación completa en el README.md
4. Abre un issue en el repositorio

---

**¡Listo! Ya puedes ejecutar la aplicación fácilmente.** 🎉

