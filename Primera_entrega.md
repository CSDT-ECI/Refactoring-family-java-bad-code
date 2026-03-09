# 🧬 CSDT — Primera Entrega

> **Proyecto:** [family-java-bad-code](https://github.com/geektrust/family-java-bad-code) — Sistema de árbol genealógico en Java  
> **Curso:** CSDT

---

**Integrantes**

| Nombre | GitHub |
|---|---|
| Juan David Martínez Méndez | [@Fataltester](https://github.com/Fataltester) |
| Samuel Alejandro Prieto Reyes | [@AlejandroPrieto82](https://github.com/AlejandroPrieto82) |
| Santiago Gualdrón Rincón | [@Waldron63](https://github.com/Waldron63) |


---

## 🔬 Deuda Técnica en Pruebas

### ¿Qué es la deuda técnica en pruebas?

La **deuda técnica en pruebas** (*Testing Debt*) es la acumulación de trabajo pendiente en el área de verificación del software. Ocurre cuando las pruebas se omiten, posponen o implementan de forma deficiente. Al igual que la deuda financiera, si no se atiende, sus intereses crecen: cada nueva funcionalidad sin cobertura multiplica el riesgo de regresiones silenciosas y aumenta el costo de mantenimiento.

No es únicamente la ausencia de tests lo que genera esta deuda — también la generan estructuras de código que hacen que escribir buenos tests sea difícil o imposible sin workarounds.

---

### Prácticas de Testing Debt identificadas

#### ❌ 1. Ausencia total de pruebas unitarias

El proyecto no contaba con ninguna prueba antes de esta entrega. La clase `MainTest` existía pero estaba completamente vacía:

```java
package com.example.geektrust;

public class MainTest {
    // Sin ningún test — deuda técnica total
}
```

**Impacto:** Cualquier refactorización se realiza sin red de seguridad. No hay forma de saber automáticamente si un cambio rompe funcionalidades existentes.

---

#### ❌ 2. Singleton con estado estático — tests no aislados

`Family` implementa un Singleton mediante un campo `static`. El estado persiste entre tests si no se resetea manualmente.

```java
// En Family.java
private static Family family; // ← estado global compartido entre TODOS los tests
```

**Impacto:** Un test que agrega un miembro a la familia contamina todos los tests que se ejecuten después. Los tests dejan de ser independientes y reproducibles.

**Workaround aplicado** *(es en sí mismo deuda técnica — se documenta como tal)*:

```java
@BeforeEach
void resetSingleton() throws Exception {
    Field field = Family.class.getDeclaredField("family");
    field.setAccessible(true);
    field.set(null, null); // resetear el singleton por reflexión antes de cada test
}
```

> ⚠️ Necesitar **reflexión** para resetear estado entre tests es una señal clara de que el diseño dificulta el testeo.

---

#### ❌ 3. `findPerson()` privado — no testeable directamente

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

> ⚠️ Acceder a campos internos por reflexión acopla los tests a la implementación. Si se renombra `record`, todos los tests que usen este helper se rompen.

---

#### ❌ 4. Lógica de negocio mezclada con `System.out`

Los métodos de `Family` imprimen directamente a consola en lugar de retornar valores. Verificar el comportamiento requiere capturar la salida estándar en cada test.

```java
// En Family.java — mezcla lógica con presentación
private void print(List<String> nameList) {
    if (nameList.size() == 0) {
        System.out.println("NONE"); // ← impresión directa
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

> ⚠️ Un espacio extra o un salto de línea puede romper el test sin que haya cambiado la lógica.

---

#### ❌ 5. Ausencia de validaciones de `null`

Varios métodos no validan si sus parámetros o resultados intermedios son `null`, lo que produce `NullPointerException` en casos borde completamente válidos.

```java
// En Person.java — constructor básico deja fatherName en null
public Person(String name, String gender) {
    this.name = name;
    this.gender = gender;
    this.setFatherName(null); // ← null explícito
    this.motherName = null;
}

// En Family.java — getSiblings() no valida null antes de llamar equals
public List<String> getSiblings(Person person) {
    String fatherName = person.getFatherName();
    if (fatherName.equals("Dummy")) // ← NullPointerException si fatherName es null
        return siblings;
    ...
}
```

---

### Pruebas implementadas

Se implementaron **51 tests** con JUnit 5, organizados en 10 bloques con el patrón AAA (*Arrange / Act / Assert*):

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

#### 🔴 Fallo 1 — `getDaughter_nullPerson_doesNotThrow`

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

#### 🔴 Fallo 2 — `getSiblings` con `fatherName == null`

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

> 💡 Estos 2 fallos son el argumento más concreto a favor del refactoring. No son casos de uso raros — son comportamientos esperables que el código actual no maneja.

---
## ANALISIS
---
## Conclusion

---
Lol no se que mas