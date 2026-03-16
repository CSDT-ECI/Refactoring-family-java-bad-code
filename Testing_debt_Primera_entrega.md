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

## Análisis
1. Cobertura de pruebas con JaCoCo

Se utilizó JaCoCo para medir la cobertura de pruebas unitarias.

Esta herramienta permite identificar qué partes del código son ejecutadas durante las pruebas, ayudando a detectar secciones que no están siendo verificadas por tests.

### Implementación 
JaCoCo se integró en el proyecto mediante el plugin correspondiente de Gradle en el archivo build.gradle.

Posteriormente se ejecuta con 
```
gradle test
```
Y se verifica el reporte en el archivo html con la ruta
```
build/reports/jacoco/test/html/index.html
```
### Resultados
Para ver los resultados puede descargar [aquí](jacocoindex.html)

2. Análisis estático con SonarLint
Para evaluar la calidad del código durante el desarrollo se utilizó SonarLint, una herramienta que se integra directamente con el entorno de desarrollo.

SonarLint permite detectar diferentes tipos de problemas en el código:
* Bugs potenciales
* Vulnerabilidades
* Code smells
* Problemas de mantenibilidad
La herramienta utiliza reglas basadas en estándares de calidad y seguridad del software.

### Implementación
Se utiliza como una extensión en VS code o IntelliJ, una vez instalado el análisis se ejecuta automáticamente al abrir el archivo, los resultados se pueden ver dentro de la Pestaña Problems del editor.

3. Detección de errores con SpotBugs
Para complementar el análisis de calidad se utilizó SpotBugs, una herramienta que analiza el bytecode de aplicaciones Java para detectar posibles errores que podrían generar fallos en tiempo de ejecución.

SpotBugs identifica problemas como:
* posibles NullPointerException
* comparaciones incorrectas de objetos
* problemas de concurrencia
* uso incorrecto de recursos

### Implementación
Se integra al proyecto mediante un pluggin con gradle en el archivo build.gradle.
Posteriormente se ejecuta con 
```
gradle spotbugsMain
```
Y se verifica el reporte en el archivo html con la ruta
```
build/reports/spotbugs/main.html
```
### Resultados obtenidos
Para ver los resultados puede descargar [aquí](spotbugmain.html)

---
## Conclusion

La deuda técnica en pruebas durante el proyecto se evidenció inicialmente en la ausencia o insuficiencia de tests que
validaran casos límite, relaciones familiares complejas y manejo de valores nulos, lo que permitió que errores como
NullPointerException pasaran desapercibidos en etapas tempranas del desarrollo. A medida que se añadieron pruebas
unitarias utilizando JUnit 5, se incrementó la cobertura y se detectaron defectos estructurales en la lógica del sistema,
lo que implicó refactorizar código previamente escrito para hacerlo más robusto y seguro. Esta situación refleja cómo
la falta inicial de pruebas genera deuda técnica que posteriormente requiere mayor esfuerzo para corregirse,
afectando el tiempo de desarrollo, la mantenibilidad del código y la estabilidad del sistema.
