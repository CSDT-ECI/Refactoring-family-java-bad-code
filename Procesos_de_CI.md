# Procesos de CI.

## Integrantes:
- Juan David Martínez Méndez - [Fataltester](https://github.com/Fataltester)
- Samuel Alejandro Prieto Reyes - [AlejandroPrieto82](https://github.com/AlejandroPrieto82)
- Santiago Gualdrón Rincón - [Waldron63](https://github.com/Waldron63)

## Introducción
En este proyecto se implementa un proceso de Integración Continua (CI) utilizando GitHub Actions, con el objetivo de automatizar la construcción, pruebas y análisis de calidad del código.
El pipeline se ejecuta automáticamente ante push y PRs sobre las ramas main y develop; permitiendo detectar errores de forma temprana, asegurar la calidad del software con Sonar Cloud y 
mantener un flujo de desarrollo confiable.

## Github Actions
El pipeline definido se compone de un único job llamado Build, Test & Code Analysis, el cual se ejecuta sobre un entorno Linux (ubuntu-latest) 
y contiene múltiples etapas (steps) que automatizan el ciclo de validación del código.

### Build
En esta etapa se realiza la compilación del proyecto utilizando Gradle; permitiendo verificar que el proyecto compile correctamente, detecte errores de sintaxis o dependencias faltantes.

Antes del build, se configuran los siguientes elementos:
* Descargar del código fuente del repositorio.
* Configurar Java 17 mediante Temurin.
* Instalar Gradle con manejo de caché para mejorar tiempos de ejecución.

### Unit Test
En esta fase se ejecutan las pruebas unitarias del proyecto junto con la generación del reporte de cobertura de código utilizando JaCoCo.

Aspectos importantes:

* Se ejecutan todas las pruebas unitarias definidas.
* Se genera un reporte de cobertura que posteriormente es utilizado por SonarCloud.
* se publica un reporte visual de los resultados de las pruebas en la pestaña de Actions.

Esto facilita la inspección de errores sin necesidad de revisar logs manualmente.

### Code Analysis (Sonar Cloud)
En esta etapa se realiza el análisis estático del código utilizando SonarCloud, lo que permite evaluar métricas clave como:

* Cobertura de pruebas
* Code smells
* Bugs potenciales
* Vulnerabilidades de seguridad

Para su funcionamiento se utilizan credenciales almacenadas como secrets en el repositorio:

* SONAR_TOKEN
* SONAR_PROJECT_KEY
* SONAR_ORGANIZATION

Esta integración permite mantener un control continuo sobre la calidad del código y aplicar prácticas de Clean Code.

### OTRO

## Conclusión
La implementación de este pipeline de CI con GitHub Actions permite automatizar procesos críticos del desarrollo de software,
asegurando que cada cambio en el código pase por etapas de validación antes de ser integrado.

La integración con herramientas como SonarCloud fortalece el control de calidad, facilitando la detección temprana de errores y promoviendo buenas prácticas de desarrollo. 
En conjunto, este enfoque mejora la confiabilidad del proyecto, reduce la deuda técnica y sienta las bases para una futura evolución hacia prácticas más avanzadas de DevOps.
