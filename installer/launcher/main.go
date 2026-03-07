// Launcher para Wow Libre Core: ejecuta el JAR con las variables del .env.
// Compilar con icono: ver build.bat o README en esta carpeta.
package main

import (
	"bufio"
	"fmt"
	"os"
	"os/exec"
	"path/filepath"
	"strings"
)

const jarName = "wowlibre-0.0.1-SNAPSHOT.jar"

func main() {
	exePath, err := os.Executable()
	if err != nil {
		fmt.Println("Error al obtener ruta:", err)
		pausar()
		return
	}
	dir := filepath.Dir(exePath)
	if err := os.Chdir(dir); err != nil {
		fmt.Println("Error al cambiar directorio:", err)
		pausar()
		return
	}

	fmt.Println()
	fmt.Println("[Wow Libre Core]")
	fmt.Println()

	jarPath := filepath.Join(dir, jarName)
	if _, err := os.Stat(jarPath); os.IsNotExist(err) {
		fmt.Printf("ERROR: No se encuentra %s en esta carpeta.\n", jarName)
		fmt.Println("Ruta:", dir)
		pausar()
		return
	}

	cargarEnv(filepath.Join(dir, ".env"))
	fmt.Println("Iniciando aplicacion...")
	fmt.Println()

	cmd := exec.Command("java", "-jar", jarName, "--spring.profiles.active=prod")
	cmd.Dir = dir
	cmd.Stdout = os.Stdout
	cmd.Stderr = os.Stderr
	cmd.Stdin = os.Stdin

	if err := cmd.Run(); err != nil {
		fmt.Println()
		fmt.Println("El proceso termino con error:", err)
	}
	pausar()
}

func cargarEnv(ruta string) {
	f, err := os.Open(ruta)
	if err != nil {
		return
	}
	defer f.Close()
	sc := bufio.NewScanner(f)
	for sc.Scan() {
		linea := strings.TrimSpace(sc.Text())
		if linea == "" || strings.HasPrefix(linea, "#") {
			continue
		}
		i := strings.Index(linea, "=")
		if i <= 0 {
			continue
		}
		clave := strings.TrimSpace(linea[:i])
		valor := strings.TrimSpace(linea[i+1:])
		if clave != "" {
			os.Setenv(clave, valor)
		}
	}
}

func pausar() {
	fmt.Println()
	fmt.Print("Presione Enter para cerrar...")
	bufio.NewReader(os.Stdin).ReadBytes('\n')
}
