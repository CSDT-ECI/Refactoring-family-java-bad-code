---
title: Procesos de CI
nav_order: 7
---

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

### Mutation Testing con PIT — Step adicional del Pipeline CI

#### ¿Qué es el Mutation Testing?

El **Mutation Testing** es una técnica de evaluación de la calidad de los tests unitarios. A diferencia de la cobertura de código tradicional (que solo mide si una línea fue ejecutada), el mutation testing responde una pregunta más exigente:

> **¿Mis tests realmente detectan errores en el código, o simplemente lo ejecutan sin verificar nada?**

La idea central es introducir pequeñas modificaciones deliberadas —llamadas **mutantes**— en el código fuente (por ejemplo, cambiar un `>` por `>=`, o eliminar un `return`), y luego correr la suite de tests para ver si alguno falla. Si los tests fallan, el mutante fue **eliminado (killed)** — señal de que ese test es efectivo. Si los tests siguen pasando, el mutante **sobrevivió (survived)** — señal de que esa lógica no está siendo verificada adecuadamente.



#### ¿Por qué agregarlo al pipeline?

Este proyecto, `family-java-bad-code`, modela un árbol genealógico. La lógica de relaciones familiares está llena de condiciones de borde que son fáciles de perder con tests superficiales:

- ¿Qué pasa si una persona no tiene padres registrados?
- ¿Qué ocurre si se consulta un ancestro en un árbol vacío?
- ¿Se valida correctamente la dirección de una relación (padre → hijo vs hijo → padre)?

La cobertura de Jacoco/SonarCloud puede mostrar un 80% de cobertura de líneas, pero eso no garantiza que esas líneas estén siendo *verificadas*. El mutation testing cierra esa brecha y complementa directamente el análisis de SonarCloud ya existente en el pipeline.


#### ¿Cómo funciona PIT?

**PIT (PITest)** es la herramienta de mutation testing más utilizada en el ecosistema Java. El proceso que sigue en cada ejecución del pipeline es:

```
1. Compila el proyecto normalmente
        ↓
2. Corre los tests una vez para establecer una línea base
        ↓
3. Para cada clase en com.example.geektrust.*:
   - Genera variantes mutadas del bytecode (los "mutantes")
   - Corre los tests completos contra cada mutante
   - Registra si algún test falló (mutante killed) o todos pasaron (mutante survived)
        ↓
4. Calcula el Mutation Score = (killed / total) × 100
        ↓
5. Genera reporte HTML + XML con el resultado por clase y por línea
        ↓
6. Falla el build si el score está por debajo del umbral configurado
```

PIT opera directamente sobre el **bytecode compilado**, no sobre el código fuente, lo que lo hace muy preciso y rápido comparado con enfoques que recompilan el código por cada mutante.


#### Mutadores utilizados

La configuración del proyecto usa el conjunto `DEFAULTS` de PIT, que incluye los mutadores más comunes y de mayor valor práctico:

| Mutador | Qué hace | Ejemplo |
|---|---|---|
| `ConditionalsBoundary` | Cambia los límites de condiciones | `>` → `>=` |
| `Increments` | Invierte incrementos/decrementos | `i++` → `i--` |
| `InvertNegativos` | Invierte negaciones matemáticas | `-x` → `x` |
| `Math` | Cambia operadores aritméticos | `+` → `-` |
| `NegateConditionals` | Niega condiciones booleanas | `==` → `!=` |
| `ReturnValues` | Altera valores de retorno | `return true` → `return false` |
| `VoidMethodCalls` | Elimina llamadas a métodos void | Borra `list.add(item)` |

Estos mutadores son especialmente reveladores en un árbol genealógico, donde la lógica de relaciones se basa en comparaciones de objetos, recorridos recursivos y valores booleanos de pertenencia.

#### Implementación

##### 1. Cambios en `build.gradle`

Se agregó el plugin de PIT y su bloque de configuración al archivo `build.gradle` existente:

```groovy
// En el bloque plugins — se agrega junto a 'java', 'jacoco' y 'org.sonarqube'
plugins {
    id 'java'
    id 'jacoco'
    id 'org.sonarqube' version '5.1.0.4882'
    id 'info.solidsoft.pitest' version '1.15.0'  // <-- nuevo
}

// Bloque de configuración de PIT — al final del archivo
pitest {
    junit5PluginVersion  = '1.2.1'              // compatibilidad con JUnit Jupiter 5
    targetClasses        = ['com.example.geektrust.*']  // clases a mutar
    targetTests          = ['com.example.geektrust.*']  // tests que se corren contra cada mutante
    mutators             = ['DEFAULTS']          // conjunto estándar de mutadores
    outputFormats        = ['HTML', 'XML']       // HTML para humanos, XML para el pipeline
    timestampedReports   = false                 // siempre en la misma ruta (facilita artifacts)
    reportsDir           = file("$buildDir/reports/pitest")
    threads              = 2                     // paralelismo dentro del runner
    mutationThreshold    = 50                    // falla el build si el score baja de 50%
    coverageThreshold    = 60                    // falla el build si la cobertura de líneas baja de 60%
}
```

**Notas clave de la configuración:**

- `junit5PluginVersion` es obligatorio porque el proyecto usa JUnit Jupiter. Sin él, PIT no encuentra los tests.
- `timestampedReports = false` garantiza que el reporte siempre se publique en `build/reports/pitest/`, lo que permite que el paso de upload de artifacts siempre encuentre el directorio.
- Los umbrales (`mutationThreshold`, `coverageThreshold`) se pueden ajustar a medida que se agregan más tests. Se recomienda empezar en 0 y subir progresivamente.



##### 2. Job en el pipeline CI (`.github/workflows/ci.yml`)

Se agregó un segundo job llamado `mutation-testing` que corre **después** del job principal `build-test-analyze`, aprovechando la dependencia `needs` para no gastar minutos de Actions si el código ni siquiera compila:

**Descripción de cada step:**

| Step | Propósito |
|---|---|
| Checkout + Java + Gradle | Prepara el entorno de ejecución idéntico al job principal |
| Cache PIT dependencies | Evita descargar los jars de mutadores en cada ejecución |
| Compile & run tests | Requisito de PIT: necesita una ejecución limpia de tests antes de mutar |
| Run PIT Mutation Testing | Genera los mutantes y corre los tests contra cada uno |
| Upload PIT HTML report | Publica el reporte visual como artifact descargable en la pestaña Actions |
| Publish mutation score summary | Muestra el score directamente en el resumen del workflow/PR |



##### 3. Cómo interpretar el reporte

El reporte HTML descargable desde **Actions → Artifacts → pitest-report-{N}** muestra:

- **Vista de resumen**: mutation score total y por clase.
- **Vista por clase**: el código fuente con líneas coloreadas.
  - 🟢 **Verde**: todos los mutantes en esa línea fueron eliminados. Los tests verifican esa lógica.
  - 🔴 **Rojo**: al menos un mutante sobrevivió. Esa lógica no está siendo validada por ningún test.
  - ⚪ **Sin color**: la línea no fue alcanzada por PIT (no tiene lógica mutable).

El resumen también aparece directamente en la pestaña **Summary** del workflow en GitHub, sin necesidad de descargar nada.



#### Resultados esperados

El objetivo es usar el reporte para guiar la escritura de nuevos tests, priorizando las líneas en rojo.

A medida que se refactoriza el código y se agregan tests, el score debería subir. Se recomienda la siguiente escala de madurez:

| Score | Interpretación |
|---|---|
| 0–40% | Tests insuficientes, muchas rutas sin verificar |
| 40–60% | Cobertura básica, aún hay lógica crítica expuesta |
| 60–80% | Buena calidad de tests, cobertura de casos principales |
| 80–100% | Tests robustos, alta confianza en el código |



#### Ejemplo de reporte

Al abrir el reporte HTML, la vista por clase se ve así:

```
com/example/geektrust/Family.java

  12  |  public boolean addChild(Person parent, Person child) {
  13  |      if (parent == null || child == null) {        ← ROJO: mutante sobrevivió
  14  |          return false;                             ← VERDE: killed
  15  |      }
  16  |      members.add(child);                          ← VERDE: killed
  17  |      return true;                                 ← ROJO: mutante sobrevivió
  18  |  }
```

En este ejemplo, la línea 13 tiene un mutante sobrevivido porque probablemente no existe un test que llame a `addChild(null, someChild)` y verifique que retorna `false`. El reporte señala exactamente dónde agregar el test.


## Conclusión
La implementación de este pipeline de CI con GitHub Actions permite automatizar procesos críticos del desarrollo de software,
asegurando que cada cambio en el código pase por etapas de validación antes de ser integrado.

La integración con herramientas como SonarCloud fortalece el control de calidad, facilitando la detección temprana de errores y promoviendo buenas prácticas de desarrollo. 
En conjunto, este enfoque mejora la confiabilidad del proyecto, reduce la deuda técnica y sienta las bases para una futura evolución hacia prácticas más avanzadas de DevOps.
Finalmente la implementación del Mutation Testing en el pipeline permite evaluar si nuestros test realmente cubren casos grandes o específicos que se pueden presentar en nuestras implementaciones y si estos no son verificados pueden traer problemas a futuro o afectar la credibilidad de nuestros test.
