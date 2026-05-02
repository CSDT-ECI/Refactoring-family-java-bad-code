---
title: Testing Debt — Primera Entrega
nav_order: 6
---

# Testing Debt — Primera Entrega

## Integrantes:
- Juan David Martínez Méndez - [Fataltester](https://github.com/Fataltester)
- Samuel Alejandro Prieto Reyes - [AlejandroPrieto82](https://github.com/AlejandroPrieto82)
- Santiago Gualdrón Rincón - [Waldron63](https://github.com/Waldron63)
---

## Introducción

La **deuda técnica en pruebas** (*Testing Debt*) es la acumulación de trabajo pendiente en el área de verificación del software. Ocurre cuando las pruebas se omiten, posponen o implementan de forma deficiente. Al igual que la deuda financiera, si no se atiende, sus intereses crecen: cada nueva funcionalidad sin cobertura multiplica el riesgo de regresiones silenciosas y aumenta el costo de mantenimiento.

No es únicamente la ausencia de tests lo que genera esta deuda — también la generan estructuras de código que hacen que escribir buenos tests sea difícil o imposible sin workarounds.

---

## Testing Debt en pruebas

### Prácticas de Testing Debt identificadas

#### 1. Ausencia total de pruebas unitarias

El proyecto no contaba con ninguna prueba antes de esta entrega. La clase `MainTest` existía pero estaba completamente vacía:

```java
package com.example.geektrust;

public class MainTest {
    // Sin ningún test — deuda técnica total
}
```

**Impacto:** Cualquier refactorización se realiza sin red de seguridad. No hay forma de saber automáticamente si un cambio rompe funcionalidades existentes.

---

#### 2. Singleton con estado estático — tests no aislados

`Family` implementa un Singleton mediante un campo `static`. El estado persiste entre tests si no se resetea manualmente.

```java
// En Family.java
private static Family family; // estado global compartido entre TODOS los tests
```

**Impacto:** Un test que agrega un miembro a la familia contamina todos los tests que se ejecuten después. Los tests dejan de ser independientes y reproducibles.

```java
@BeforeEach
void resetSingleton() throws Exception {
    Field field = Family.class.getDeclaredField("family");
    field.setAccessible(true);
    field.set(null, null); // resetear el singleton por reflexión antes de cada test
}
```
---

#### 3. findPerson() privado — no testeable directamente

El método interno de búsqueda es `private`, lo que impide acceder a instancias de `Person` en los tests sin violar el encapsulamiento.

```java
// En Family.java — no accesible desde los tests
private Person findPerson(String name) { ... }
```

**Workaround aplicado en los tests:**

```java
private Person findPersonViaFamily(String name) {
    Field recordField = Family.class.getDeclaredField("record");
    recordField.setAccessible(true);
    Map<String, Person> record = (Map<String, Person>) recordField.get(family);
    return record.get(name);
}
```

---

#### 4. Lógica de negocio mezclada con System.out

Los métodos de `Family` imprimen directamente a consola en lugar de retornar valores. Verificar el comportamiento requiere capturar la salida estándar en cada test.

```java
// En Family.java — mezcla lógica con presentación
private void print(List<String> nameList) {
    if (nameList.size() == 0) {
        System.out.println("NONE"); // impresión directa
        return;
    }
    for (String name : nameList) {
        System.out.print(name + " ");
    }
    System.out.println();
}
```

**Consecuencia en los tests:**

```java
// Cada test que verifica salida necesita este boilerplate
ByteArrayOutputStream out = new ByteArrayOutputStream();
System.setOut(new PrintStream(out));
family.getRelationship("Shan", "Son");
assertTrue(out.toString().contains("Chit")); // verificar por texto es frágil
```

---

#### 5. Ausencia de validaciones de null

Varios métodos no validan si sus parámetros o resultados intermedios son `null`, lo que produce `NullPointerException` en casos borde completamente válidos.

```java
// En Person.java — constructor básico deja fatherName en null
public Person(String name, String gender) {
    this.name = name;
    this.gender = gender;
    this.setFatherName(null); // null explícito
    this.motherName = null;
}

// En Family.java — getSiblings() no valida null antes de llamar equals
public List<String> getSiblings(Person person) {
    String fatherName = person.getFatherName();
    if (fatherName.equals("Dummy")) // NullPointerException si fatherName es null
        return siblings;
    ...
}
```

---

### Pruebas implementadas

Se implementaron **51 tests** con JUnit 5, organizados en 10 bloques con el patrón AAA (*Arrange / Act / Assert*):

Cada uno de los Test generados tiene la estructura del nombre **Should_[ExpectedBehavior]_When_[StateUnderTest]**
para una comprensión clara de lo que se está probando.

| Bloque | Qué cubre | Tests |
|---|---|---|
| 1 — Person Model | Constructores, getters, setters, `addChild`, `toString` | 6 |
| 2 — addMember | Éxito, madre inexistente, padre como madre, hijo en árbol | 6 |
| 3 — PERSON_NOT_FOUND | 9 relaciones con persona inexistente via `@ParameterizedTest` | 9 |
| 4 — Son / Daughter | Conteos, NONE, salida por consola | 5 |
| 5 — Siblings | Normal, Dummy, hijo único, no incluye self | 4 |
| 6 — Uncle | Paternal y maternal, borde Dummy | 4 |
| 7 — Aunt | Paternal y maternal, exclusión de la madre | 3 |
| 8 — In-Laws | Sister/Brother-in-law con y sin cónyuge | 4 |
| 9 — Singleton | Misma instancia, no null | 2 |
| 10 — Edge Cases | Relación desconocida, null, gender vacío, duplicados | 6 |

**Resultado de ejecución:**

```
FamilyTest > ... PASSED  (×49)
FamilyTest > ... FAILED  (×2)

FAILURE (51 tests, 49 successes, 2 failures, 0 skipped)
```

---

### Los 2 tests que fallan — y por qué importan

Estos dos tests **no son errores en los tests**. Son errores en el **código de producción**, documentados intencionalmente para guiar el refactoring.

---

#### Fallo 1 — getDaughter_nullPerson_doesNotThrow

```java
@Test
@DisplayName("getDaughter con persona null no lanza excepción")
void getDaughter_nullPerson_doesNotThrow() {
    // Arrange & Act & Assert
    assertDoesNotThrow(() -> family.getDaughter(null));
}
```

**¿Por qué falla?**

Aunque `getDaughter()` tiene una validación `if (person != null)`, cuando se llama con `null` en ciertas rutas internas de la clase, otros métodos encadenados intentan acceder a propiedades del objeto sin verificar, produciendo un `NullPointerException`:

```java
public List<String> getDaughter(Person person) {
    List<String> daughter = new ArrayList<>();
    if (person != null) {
        for (Person child : person.getChildren()) {
            if (child.getGender().equals("Male")) // ← si child tiene gender null, NPE
                continue;
            daughter.add(child.getName());
        }
    }
    return daughter;
}
```

---

#### Fallo 2 — getSiblings con fatherName == null

```java
@Test
@DisplayName("Persona con padre Dummy retorna lista vacía de hermanos")
void getSiblings_personWithDummyFather_returnsEmpty() {
    // Arrange
    Person vyan = findPersonViaFamily("Vyan");

    // Act
    List<String> siblings = family.getSiblings(vyan);

    // Assert
    assertTrue(siblings.isEmpty());
}
```

**¿Por qué falla?**

El constructor básico de `Person` establece `fatherName = null`. Cuando `getSiblings()` recibe una persona cuyo padre no fue inicializado con `"Dummy"` sino con `null`, la comparación `.equals()` explota:

```java
// Person.java
public Person(String name, String gender) {
    this.setFatherName(null); // ← null, no "Dummy"
}

// Family.java
public List<String> getSiblings(Person person) {
    String fatherName = person.getFatherName(); // retorna null
    if (fatherName.equals("Dummy"))             // ← NullPointerException aquí
        return siblings;
}
```

El problema es una inconsistencia entre los dos constructores de `Person`: el constructor completo recibe `"Dummy"` como valor desde los datos de inicialización, pero el constructor básico guarda `null`. El código de `Family` asume que siempre llega `"Dummy"`, nunca `null`.

Estos 2 fallos son el argumento más concreto a favor del refactoring. No son casos de uso raros — son comportamientos esperables que el código actual no maneja.

---
## Modelos de Calidad

Los modelos de calidad proveen un marco sistemático para evaluar el software más allá de si "funciona". En este proyecto aplicamos tres perspectivas complementarias: un modelo conceptual (ISO/IEC 25010), herramientas de análisis estático (SonarLint, SpotBugs) y una herramienta de cobertura (JaCoCo). Además, proponemos una herramienta emergente asistida por IA.

---

## ISO/IEC 25010 — Análisis del proyecto

El estándar **ISO/IEC 25010** (también conocido como SQuaRE) define la calidad del producto software en 8 características principales. A continuación se evalúa el proyecto contra estas dimensiones:

```
┌──────────────────────────────────────────────────────────┐
│              ISO/IEC 25010 — Evaluación                  │
├────────────────────────┬───────────┬─────────────────────┤
│ Característica         │  Estado   │ Evidencia           │
├────────────────────────┼───────────┼─────────────────────┤
│ Adecuación funcional   │  OK       │ El árbol familiar   │
│                        │           │ responde a todas    │
│                        │           │ las queries         │
├────────────────────────┼───────────┼─────────────────────┤
│ Eficiencia de          │  Medio    │ O(n) lineal en      │
│ rendimiento            │           │ findPerson() para   │
│                        │           │ cada operación      │
├────────────────────────┼───────────┼─────────────────────┤
│ Compatibilidad         │  OK       │ Java estándar,      │
│                        │           │ sin dependencias    │
│                        │           │ propietarias        │
├────────────────────────┼───────────┼─────────────────────┤
│ Usabilidad             │  N/A      │ API interna;        │
│                        │           │ sin UI              │
├────────────────────────┼───────────┼─────────────────────┤
│ Fiabilidad             │  Bajo     │ NPE no controladas  │
│                        │           │ en getSiblings(),   │
│                        │           │ getDaughter(null)   │
├────────────────────────┼───────────┼─────────────────────┤
│ Seguridad              │  OK       │ Sin vectores de     │
│                        │           │ ataque relevantes   │
│                        │           │ en dominio local    │
├────────────────────────┼───────────┼─────────────────────┤
│ Mantenibilidad         │  Bajo     │ Singleton estático, │
│                        │           │ lógica mezclada con │
│                        │           │ System.out, métodos │
│                        │           │ privados no         │
│                        │           │ testeables          │
├────────────────────────┼───────────┼─────────────────────┤
│ Portabilidad           │  OK       │ Gradle + Java;      │
│                        │           │ multiplataforma     │
└────────────────────────┴───────────┴─────────────────────┘
```

### Dimensiones críticas identificadas

**Fiabilidad — nivel bajo:**
Las `NullPointerException` no controladas en `getSiblings()` y `getDaughter(null)` representan fallos predecibles en tiempo de ejecución. Según ISO 25010, la fiabilidad incluye la *tolerancia a fallos* y la *disponibilidad*; ambas se ven comprometidas cuando el sistema colapsa ante entradas perfectamente válidas.

**Mantenibilidad — nivel bajo:**
El modelo identifica cuatro subcaracterísticas afectadas:
- **Modularidad:** la clase `Family` mezcla persistencia, lógica de negocio y presentación.
- **Reusabilidad:** `findPerson()` privado impide reutilización en subclases o mocks.
- **Analizabilidad:** sin separación de responsabilidades, rastrear un bug requiere leer la clase entera.
- **Capacidad de prueba:** el Singleton estático hace que los tests interfieran entre sí sin reseteo manual.

---

## SonarLint 

**SonarLint** es una extensión de análisis estático que integra reglas de calidad directamente en el IDE (VS Code, IntelliJ). A diferencia de SonarQube (servidor), funciona sin infraestructura adicional, lo que lo hace ideal para feedback inmediato durante el desarrollo.

### Implementación

Se instaló como extensión en VS Code. El análisis se ejecuta automáticamente al abrir cada archivo Java; los resultados aparecen en la pestaña **Problems** del editor.

### Hallazgos sobre el código del proyecto

| Tipo | Regla violada | Ubicación | Severidad |
|------|---------------|-----------|-----------|
| Bug | `String.equals()` sobre posible `null` | `Family.getSiblings()` línea 128 |  Critical |
| Bug | `String.equals()` sobre posible `null` | `Family.getPaternalaunt()` línea 142 |  Critical |
| Code Smell | Variable estática mutable (`family`) en clase no thread-safe | `Family.java` línea 14 |  Major |
| Code Smell | Método con demasiadas responsabilidades (>50 líneas) | `Family.getRelationship()` |  Low |
| Code Smell | `System.out` en lógica de negocio | `Family.print()`, `addMember()` |  Low |
| Code Smell | Campo `st` estático nunca usado | `Family.java` línea 13 |  Low |

---

## SpotBugs — Detección de errores de bytecode

**SpotBugs** analiza el *bytecode* compilado (`.class`) en lugar del código fuente. Esto le permite detectar patrones de error que un análisis de texto no puede ver, como comparaciones de referencias en lugar de valores o recursos no cerrados.

### Implementación

```groovy
// build.gradle
plugins {
    id 'com.github.spotbugs' version '5.0.14'
}
```

```bash
gradle spotbugsMain
# Reporte en: build/reports/spotbugs/main.html
```

### Categorías de bugs detectados

SpotBugs clasifica los bugs en familias. Las más relevantes para este proyecto son:

| Familia | Código | Descripción | Instancias en el proyecto |
|---------|--------|-------------|--------------------------|
| **NP** | `NP_NULL_ON_SOME_PATH` | Posible desreferencia de null | `getSiblings()`, métodos de tíos/tías |
| **DM** | `DM_STRING_CTOR` | Uso innecesario de `new String()` | `Family.java` campo `st` |
| **MS** | `MS_MUTABLE_COLLECTION` | Colección mutable expuesta via getter | `Person.getChildren()` |
| **ST** | `ST_WRITE_TO_STATIC_FROM_INSTANCE` | Escritura a campo estático desde método de instancia | `Family.getFamilyInstance()` |

---

## JaCoCo — Cobertura de pruebas

**JaCoCo** (*Java Code Coverage*) instrumenta el bytecode para registrar qué instrucciones, ramas y métodos se ejecutan durante los tests.

### Implementación

```groovy
// build.gradle
plugins {
    id 'jacoco'
}

test {
    finalizedBy jacocoTestReport
}

jacocoTestReport {
    reports {
        html.required = true
    }
}
```

```bash
gradle test
# Reporte en: build/reports/jacoco/test/html/index.html
```

También se puede ver el reporte en el momento de hacer este documento [aquí](../reports/jacocoindex.html)

### Resultados obtenidos (51 tests iniciales)

| Métrica | Cubiertas | Sin cubrir | Cobertura |
|---------|-----------|------------|-----------|
| Instrucciones | 838 / 1,207 | 369 | **69%** |
| Ramas | 89 / 130 | 41 | **68%** |
| Complejidad ciclomática | 73 / 106 | 33 | 69% |
| Líneas | 208 / 262 | 54 | 79% |
| Métodos | 34 / 37 | 3 | **92%** |

### Interpretación

El 92% de métodos cubiertos indica que los tests ejercitan casi todos los caminos de entrada al sistema. Sin embargo, el 68% de ramas sugiere que dentro de esos métodos hay muchas bifurcaciones condicionales que los tests no alcanzan — en particular, las ramas de error y los casos con `"Dummy"` como padre/madre.

La brecha entre cobertura de métodos (92%) y cobertura de ramas (68%) es la señal más importante: **los tests llaman a los métodos pero no los estresan con suficientes variaciones de entrada**.

---

## Herramienta propuesta con IA: CodeClimate — Análisis asistido por IA

Como complemento a las herramientas vistas en clase, se propone **CodeClimate Quality** con su capa de análisis asistido por IA, que amplía el análisis estático clásico con capacidades de razonamiento semántico.

### ¿Qué es y por qué complementa el trabajo?

CodeClimate combina reglas de análisis estático (similar a SonarLint) con un modelo de IA que puede:

1. **Detectar deuda técnica semántica:** no solo patrones sintácticos, sino código que *funciona* pero es difícil de entender o mantener.
2. **Priorizar por impacto:** asigna puntajes de mantenibilidad basados en complejidad cognitiva, no solo ciclomática.
3. **Sugerir refactorizaciones:** propone nombres más claros, extracción de métodos, eliminación de duplicación.

### Análisis hipotético sobre el proyecto

Si se ejecutara CodeClimate sobre `Family.java`, los hallazgos esperados serían:

| Problema | Detección IA | Detección SonarLint | Detección SpotBugs |
|----------|:---:|:---:|:---:|
| `getSiblings()` NPE | ✅ | ✅ | ✅ |
| `Family` clase dios (God Class) | ✅ |  Parcial | ❌ |
| `print()` viola SRP | ✅ | ✅ | ❌ |
| Singleton no thread-safe | ✅ | ✅ | ✅ |
| `findPerson()` O(n) en HashMap | ✅ | ❌ | ❌ |
| Nombres de métodos inconsistentes (`getSon` vs `getPaternalaunt`) | ✅ | ❌ | ❌ |

### Ventaja diferencial de la IA

La columna más reveladora es la detección del problema de **`findPerson()` O(n) en HashMap**: un `HashMap` debería ofrecer O(1) de acceso, pero el código itera sobre `entrySet()` en lugar de usar `record.get(name)` directamente. Este es un error de uso de la API que ninguna herramienta estática clásica detectaría porque el código *compila y funciona* — solo es ineficiente. Un modelo de IA entrenado en patrones de uso de colecciones Java identifica este antipatrón.

```java
// Código actual — O(n) innecesario
private Person findPerson(String name) {
    for (Map.Entry<String, Person> entry : record.entrySet()) {
        if (entry.getKey().equals(name)) {
            return entry.getValue();
        }
    }
    return null;
}

// Corrección sugerida por IA — O(1) como corresponde a un HashMap
private Person findPerson(String name) {
    return record.get(name);
}
```

### Integración propuesta

```yaml
# .codeclimate.yml
version: "2"
checks:
  method-complexity:
    config:
      threshold: 10
  similar-code:
    enabled: true
  identical-code:
    enabled: true
plugins:
  pmd:
    enabled: true
  checkstyle:
    enabled: true
```

---

# Conclusiones

### Sobre Testing Debt

La deuda técnica en pruebas fue total al inicio del proyecto: un archivo de test vacío como único vestigio de intención de calidad. La implementación de 51 tests con JUnit 5 no solo aumentó la cobertura al 69%, sino que actuó como **documentación ejecutable** del comportamiento esperado del sistema — y como detector inmediato de los 2 bugs críticos de `NullPointerException` que el código de producción cargaba silenciosamente.

Los 2 tests que fallan son el argumento más concreto a favor del refactoring: demuestran que el problema no es hipotético sino reproducible y medible.

### Sobre Modelos de Calidad

La triada **ISO/IEC 25010 + SonarLint + SpotBugs + JaCoCo** ofrece perspectivas complementarias:

- **ISO/IEC 25010** es el marco conceptual que define *qué* medir.
- **SonarLint** da feedback inmediato durante el desarrollo sobre *cómo* está el código.
- **SpotBugs** actúa como segunda capa, analizando el bytecode para bugs que el análisis de texto no ve.
- **JaCoCo** responde la pregunta *¿cuánto del código realmente ejercitan los tests?*

Ninguna herramienta sola es suficiente. La convergencia de múltiples herramientas sobre los mismos bugs (los NPE en `getSiblings` y métodos relacionados) es la señal más fuerte de que esos puntos son prioridad de refactoring.

### Sobre la herramienta con IA

La propuesta de CodeClimate con análisis asistido por IA amplía la detección hacia problemas semánticos que las herramientas clásicas no ven: el uso O(n) de un HashMap, inconsistencias de nomenclatura entre métodos, y la identificación de `Family` como una *God Class* que mezcla demasiadas responsabilidades. Estas son exactamente las deudas que quedan invisibles después de que pasan los tests — y que hacen que el código sea cada vez más difícil de mantener con el tiempo.

### Relación entre herramientas y modelo de calidad

Las herramientas utilizadas en el proyecto no se emplean de forma aislada, sino como mecanismos para evaluar atributos específicos del modelo ISO/IEC 25010.

Herramienta	Característica ISO 25010	Qué mide
JaCoCo	Fiabilidad	Cobertura de pruebas y detección de código no validado
SonarLint	Mantenibilidad	Code smells, complejidad, malas prácticas
SpotBugs	Fiabilidad	Errores en tiempo de ejecución (null, bugs potenciales)
CodeClimate (IA)	Mantenibilidad / Eficiencia	Complejidad cognitiva, diseño, uso ineficiente de estructuras

Interpretación:
- Las herramientas de testing (JaCoCo) refuerzan la fiabilidad
- Las herramientas de análisis estático (SonarLint, CodeClimate) mejoran la mantenibilidad
- Las herramientas de análisis de bugs (SpotBugs) aseguran la robustez del sistema
