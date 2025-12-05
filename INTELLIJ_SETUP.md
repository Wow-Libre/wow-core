# Configuración de Formato Automático en IntelliJ IDEA

Para configurar el formato automático al guardar con la indentación de tabs (8 espacios):

## Opción 1: Configuración Manual en IntelliJ IDEA

### 1. Configurar el Formateador de Código:

1. Ve a **File → Settings** (o `Ctrl + Alt + S`)
2. Navega a **Editor → Code Style → Java**
3. En la pestaña **Tabs and Indents**:
   - **Tab size**: 8
   - **Indent**: 8
   - **Continuation indent**: 8
   - **Use tab character**: ✅ (activado)
4. Haz clic en **Apply** y luego **OK**

### 2. Activar Formato Automático al Guardar:

1. Ve a **File → Settings**
2. Navega a **Tools → Actions on Save**
3. Activa:
   - ✅ **Reformat code**
   - ✅ **Optimize imports**
   - Selecciona: **All file types** o **Only changed files**
4. Haz clic en **Apply** y luego **OK**

### 3. Exportar/Importar Configuración (Recomendado para el equipo):

1. Ve a **File → Settings**
2. Navega a **Editor → Code Style**
3. Haz clic en el ícono de engranaje ⚙️ → **Export → IntelliJ IDEA code style XML**
4. Guarda el archivo como `wow-core-code-style.xml` en la raíz del proyecto
5. Comparte este archivo con tu equipo
6. Para importar: ⚙️ → **Import Scheme → IntelliJ IDEA code style XML**

## Opción 2: Usar Plugin EditorConfig (Recomendado)

El archivo `.editorconfig` que está en la raíz del proyecto será respetado automáticamente si tienes el plugin instalado:

1. Ve a **File → Settings → Plugins**
2. Busca **EditorConfig**
3. Instálalo si no está instalado (viene por defecto en versiones recientes)
4. El archivo `.editorconfig` se aplicará automáticamente

## Opción 3: Atajo de Teclado

Para formatear manualmente:

- **Windows/Linux**: `Ctrl + Alt + L`
- **Mac**: `Cmd + Option + L`

Para formatear solo la selección:

- **Windows/Linux**: `Ctrl + Alt + I`
- **Mac**: `Cmd + Option + I`

## Verificar Configuración

1. Abre cualquier archivo `.java`
2. Presiona `Ctrl + Alt + L` para formatear
3. Verifica que la indentación sea con tabs de 8 espacios
