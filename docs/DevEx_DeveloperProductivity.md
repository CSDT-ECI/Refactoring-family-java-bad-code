# DevEx y Developer Productivity.
## Integrantes:
- Juan David Martínez Méndez - [Fataltester](https://github.com/Fataltester)
- Samuel Alejandro Prieto Reyes - [AlejandroPrieto82](https://github.com/AlejandroPrieto82)
- Santiago Gualdrón Rincón - [Waldron63](https://github.com/Waldron63)

## Introducción
En este documento, se van a identificar, listar y diferenciar, las características que se cummplen y no se cumplen del "Clean code"; los principios de programación aplicados junto con las prácticas XP vistas en la clase.
A partir de estos datos, podremos aterrizar los diferentes cambios que se deben aplicar al código, incluyendo las prácticas que se deben incluir y los principios necesarios para generar un codigo limpio.

## Developer Experience (DevEx)

DevEx es un framework que evalúa cómo se siente trabajar en un proyecto desde la perspectiva del desarrollador. Se centra en tres dimensiones: **feedback loops** (qué tan rápido sabes si algo funciona), **cognitive load** (cuánto esfuerzo mental cuesta entender el código) y **flow state** (qué tan fácil es trabajar sin interrupciones ni confusión).


### Feedback Loops

El proyecto original no tenía ninguna prueba unitaria. La única forma de saber si algo funcionaba era ejecutar el programa completo con un archivo de entrada y leer la salida en consola a mano, lo cual hace que cualquier cambio pequeño tome varios minutos en verificarse.

Como equipo agregamos pruebas con JUnit 5 en `FamilyTest` y `PersonTest`, cubriendo los métodos principales de `Family` y `Person`. Esto redujo el tiempo de verificación de minutos a segundos.

Lo que falta: un pipeline de CI (GitHub Actions por ejemplo) que corra las pruebas automáticamente en cada commit, para que el feedback sea inmediato sin que nadie tenga que acordarse de correrlas.


### Cognitive Load

La clase `Family` tiene más de 300 líneas y hace de todo: guarda datos, busca personas, calcula relaciones y además imprime resultados en consola. Eso obliga al desarrollador a leer y entender toda la clase antes de poder tocar cualquier cosa.

A eso se le suma:
- Magic strings como `"Dummy"`, `"Male"` y `"Female"` regados por todo el código sin ninguna constante ni enum que los explique
- El árbol familiar completo hardcodeado directamente en el `main`, mezclado con la lógica de lectura de archivos
- Código muerto visible: `private static String st = new String()` en `Family` que nunca se usa, y el comentario `// TODO Auto-generated constructor stub` que quedó del IDE
- El método `findPerson()` recorre todo el HashMap con un for-each cuando podría usar `record.get(name)` directamente, lo que hace pensar que hay alguna lógica oculta ahí cuando no la hay

Todo eso genera ruido que el desarrollador tiene que ignorar conscientemente cada vez que lee el código.


### Flow State

La estructura de tres clases es fácil de navegar, eso es positivo. Pero una vez dentro de `Family` el flujo se rompe seguido porque los métodos `getPaternaluncle`, `getPaternalaunt`, `getMaternaluncle` y `getMaternalaunt` hacen casi exactamente lo mismo con variaciones mínimas, entonces uno termina leyendo cuatro veces la misma lógica para entender una sola cosa.

También el patrón `if(fatherName.equals("Dummy"))` aparece en múltiples métodos sin ninguna explicación de por qué "Dummy" es un valor especial, lo que genera dudas cada vez que uno se lo encuentra.

Con las pruebas que agregamos al menos se puede verificar el comportamiento rápido, lo que reduce un poco esa sensación de incertidumbre al hacer cambios.


### Pros y Contras

| Dimensión | Estado original | Con mejoras del equipo |
|---|---|---|
| Feedback Loops | Sin pruebas, verificación manual | Pruebas unitarias con JUnit 5 |
| Cognitive Load | Clase de 300+ líneas, magic strings, código muerto | Documentado, pendiente refactorizar |
| Flow State | Lógica duplicada, sin documentación interna | Sin cambio estructural aún |
| Cobertura de pruebas | 0% | ~80% métodos públicos de `Family` |

El problema más grande en términos de DevEx es el cognitive load. Una clase que hace todo es difícil de mantener, difícil de leer y difícil de modificar sin miedo a romper algo. Las pruebas unitarias son la mejora más concreta que hicimos, pero la deuda técnica estructural sigue ahí.

## SPACE
## Framework SPACE

### S — Satisfaction & Well-being

Mide cómo se sienten los desarrolladores con su trabajo, las herramientas y la cultura del equipo.

#### Puntos positivos
- Los mensajes de salida del sistema (`CHILD_ADDITION_SUCCEEDED`, `PERSON_NOT_FOUND`, `CHILD_ADDITION_FAILED`) son claros, consistentes y comunican el resultado de cada operación de forma explícita.
- Los tests incluyen un mecanismo de reset del Singleton con `@BeforeEach`, lo que muestra conciencia del problema y voluntad de mitigarlo.

#### Puntos negativos
- El patrón **Singleton con estado estático** en `Family` obliga a los tests a usar `Field.setAccessible(true)` para reiniciar el estado entre pruebas. Esta fricción genera frustración y es señal de un diseño que dificulta la experiencia del desarrollador.
- El uso de `"Dummy"` como nombre ficticio para padres desconocidos es una solución funcional pero que contamina semánticamente el modelo de dominio, generando confusión a quien lee el código por primera vez.

#### Métricas identificables
| Métrica | Estado |
|---|---|
| Mensajes de error descriptivos | Presentes |
| Fricción en setup de tests | Alta (reflection manual) |
| Coherencia del modelo de dominio | Baja ("Dummy" como valor especial) |

---

### P — Performance

Mide el valor generado por el desarrollador hacia el cliente final (outcomes).

#### Puntos positivos
- La lógica de todas las relaciones familiares (tíos, cuñados, hermanos, hijos) funciona correctamente y los tests lo verifican con aserciones precisas.
- Los tests parametrizados con `@CsvSource` validan conteos exactos de hijos por tipo de relación, garantizando que el comportamiento esperado se mantiene.
- Los casos límite están cubiertos (personas sin hijos, padres `Dummy`, listas vacías).

#### Puntos negativos
- **`findPerson` recorre todo el mapa con un `for` en O(n)**, desaprovechando completamente la ventaja del `HashMap` que permitiría un acceso directo en O(1) con `record.get(name)`. Todas las operaciones de relación heredan este costo innecesario.
- Los datos de la familia están **hardcodeados directamente en `Main`**, haciendo imposible cambiar el árbol familiar sin recompilar el proyecto.
- `TestData` es una copia exacta del array de `Main`, lo que implica duplicación y riesgo de inconsistencia.

#### Métricas identificables
| Métrica | Estado |
|---|---|
| Complejidad algorítmica de `findPerson` |  O(n) — debería ser O(1) |
| Datos de configuración externalizados |  Hardcoded en `Main` |
| Duplicación de código (`Main` vs `TestData`) |  Alta |

---

### A — Activity

Mide las acciones y resultados finalizados por el desarrollador relacionados con su trabajo (codificación, diseño, CI/CD, tareas operativas).

#### Puntos positivos
- La suite de tests es el punto más fuerte del proyecto: **36 casos** distribuidos en **9 bloques `@Nested`** claramente organizados por responsabilidad.
- Se usan `@ParameterizedTest` con `@ValueSource` y `@CsvSource`, cubriendo múltiples escenarios sin duplicar código de test.
- La convención de nombres `Should_[resultado]_When_[condición]` es consistente en toda la suite y comunica la intención de cada prueba sin ambigüedad.
- Se aplica consistentemente el patrón **AAA (Arrange-Act-Assert)** con comentarios explícitos.

#### Puntos negativos
- **No existe pipeline de CI/CD**. Los tests solo aportan valor si alguien los ejecuta manualmente, eliminando la retroalimentación automática ante cada cambio.
- **No hay métricas de cobertura** configuradas (ausencia de JaCoCo u otro plugin en el `pom.xml`), lo que impide saber con precisión qué porcentaje del código está cubierto.
- `TestData` está acoplada a `Main` (duplicación exacta del array de familia).

#### Métricas identificables
| Métrica | Valor estimado |
|---|---|
| Total de casos de prueba | 36 tests / 3 clases |
| Cobertura estimada | ~80% (sin medición automática) |
| Pipeline CI/CD | Ausente |
| Reporte de cobertura automatizado | Ausente |

---

### C — Communication & Collaboration

Mide cómo los equipos se comunican y trabajan colaborativamente de manera coordinada y fluida.

#### Puntos positivos
- El uso de `@DisplayName` en todos los tests facilita comunicar la intención de cada caso a otros miembros del equipo sin necesidad de leer el cuerpo del test.
- `TestData` centraliza los datos de familia para toda la suite, estableciendo una fuente única de verdad para los tests (aunque duplicada con `Main`).
- La convención `Should_When_` es un contrato implícito de nomenclatura que facilita la incorporación de nuevos colaboradores.

#### Puntos negativos
- El **Singleton con estado estático** dificulta el trabajo en paralelo: si dos desarrolladores trabajan en tests distintos y el reset falla, el estado de uno contamina al otro.
- **No hay separación de capas**: toda la lógica de negocio vive en `Family` (consulta, mutación, impresión a consola), lo que genera conflictos constantes al trabajar sobre la misma clase.
- La ausencia de una interfaz o contrato formal (`IFamilyRepository`, por ejemplo) dificulta que distintos desarrolladores trabajen en implementaciones alternativas.

#### Métricas identificables
| Métrica | Estado |
|---|---|
| Claridad de intención en tests (`@DisplayName`) | Presente |
| Separación de responsabilidades (SRP) | Ausente en `Family` |
| Riesgo de conflictos por Singleton | Alto |

---

### E — Efficiency & Flow

Mide la capacidad de finalizar el trabajo con un mínimo de interrupciones, retrabajo o actividades sin valor.

#### Puntos positivos
- El patrón Singleton evita recrear el árbol familiar en cada operación, lo que tiene sentido para un objeto costoso de construir.
- `addChild` retorna un `boolean`, lo que lo hace extensible para validaciones futuras.
- `toString()` en `Person` facilita el debug rápido en consola sin configuración adicional.

#### Puntos negativos
- Existe un campo **`private static String st = new String()`** en `Family` que nunca se usa — código muerto que genera ruido.
- El método `addRelation` puede lanzar `NullPointerException` si `father` o `mother` son nulos, ya que llama `father.addChild(child)` sin validar previamente, a diferencia de `addMember` que sí valida. Inconsistencia que genera retrabajo al depurar.
- El uso de **reflection** en los tests para acceder al campo privado `record` es frágil: si se renombra ese campo, los tests rompen silenciosamente con un error de runtime, no de compilación — introduciendo interrupciones difíciles de rastrear.
- El `switch` en `getRelationship` no tiene `default` que imprima un error útil para relaciones desconocidas, aunque el test de caso límite confirma que al menos no lanza excepción.

#### Métricas identificables
| Métrica | Estado |
|---|---|
| Código muerto | `st` sin usar en `Family` |
| Consistencia de validación de nulos | Parcial (`addMember` vs `addRelation`) |
| Fragilidad de tests por reflection | Alta |
| Complejidad ciclomática de `Family` | Alta (switch + múltiples métodos de relación) |

---

## Oportunidades de Mejora

### 1) Onboarding y “ruta feliz” para ejecutar el proyecto
El repositorio ya ofrece scripts (run.sh o run.bat) y Gradle Wrapper, lo cual es un buen inicio de DevEx porque reduce pasos manuales. Se puede convertir en una ruta de inicio estandarizada y explícita: dejar en el README un apartado donde se indique versión de Java (en el build.gradle se ve compatibilidad 1.8), comandos exactos para compilar, correr con un archivo de ejemplo y ejecución de pruebas unitarias. Esto impacta SPACE en Satisfaction (menos frustración) y en Efficiency (menos tiempo perdido en configuración).

### 2) Feedback rápido pero confiable: no saltarse pruebas por defecto
Los scripts actuales construyen el proyecto con -x test, lo que acelera la ejecución, pero facilita que los cambios rompan funcionalidad sin que nadie se entere. Una oportunidad es proponer dos modos: uno rápido (solo build) y uno seguro (build + test).
En términos SPACE, esto mejora Performance y Efficiency porque reduce regresiones y retrabajo; y en DevEx mejora la sensación de “puedo cambiar sin miedo” gracias a un ciclo de feedback más confiable.

### 4) Separación de responsabilidades (refactor del Main)
En Main.java se observa mezcla de responsabilidades: datos iniciales “quemados”, lectura de archivo, parseo de comandos y ejecución de acciones sobre el dominio. Esto es típico de “bad code”, pero afecta DevEx porque dificulta comprender qué parte cambiar sin romper otra. La oportunidad es extraer componentes; reduciendo la carga cognitiva y facilitando pruebas unitarias. En SPACE esto se traduce en mayor Efficiency (menos retrabajo) y mayor Performance (cambios más rápidos de implementar y validar).

### 5) Automatización en CI para que el repo “se defienda solo”
Actualmente no hay workflows ni pipelines, así que una mejora concreta es agregar un pipeline de CI (GitHub Actions) que ejecute build, test y reporte de cobertura. Para SPACE, esto mejora Performance (ciclo de entrega más predecible), Efficiency (menos bugs post-merge) y Satisfaction (mayor confianza del equipo en el estado del proyecto).

### 6) Mejorar colaboración: plantillas y Definition of Done
Una oportunidad de mejora es agregar una plantilla de PR con checklist (cómo probar, qué cambió, riesgos) y, si usan issues, una plantilla con criterios de aceptación. Esto acelera la comunicación y reduce el “ida y vuelta” en revisión. En SPACE se refleja directamente en Communication & Collaboration y también en Activity.

## Métricas identificables

### 1) Métricas de experiencia de arranque (DevEx: onboarding)
Time-to-First-Success (TTFS): cuánto tarda alguien nuevo desde clonar el repo hasta lograr ejecutar un caso de ejemplo con salida válida. Se puede medir con un cronómetro y repetirlo entre miembros del equipo antes/después de mejorar el README y el flujo de ejecución. Si TTFS baja, la mejora en DevEx es tangible.

### 2) Métricas de “feedback loop” (DevEx: velocidad y confianza)
Medir el tiempo de ejecución local de los comandos más comunes: ./gradlew test y ./gradlew build; además, medir la tasa de fallos de tests al introducir cambios. Estos datos muestran si el equipo tiene un ciclo de feedback rápido (DevEx) y si ese feedback es confiable (SPACE: Efficiency).

### 3) Métricas de calidad técnica que ya se pueden obtener (Cobertura con JaCoCo)
Dado que el proyecto ya tiene JaCoCo generando jacoco.xml, se puede reportar cobertura de líneas. Esta métrica sirve como indicador de “red de seguridad” durante el refactor: si sube la cobertura y bajan defectos, se evidencia mejora real. Para SPACE se conecta con Efficiency (menos regresiones) y Performance (cambios más seguros y rápidos).

### 4) Métricas de retrabajo y estabilidad (SPACE: Efficiency)
Para evidenciar productividad real, una métrica potente es el porcentaje de retrabajo: cuántos commits posteriores al review fueron para corregir fallos (tests rotos, estilos, bugs) versus commits por funcionalidad/refactor planeado. También se puede medir defectos post-merge (issues o bugs reportados después de integrar). 

## Conclusión

El proyecto tiene una base útil para trabajar refactorización (Gradle, wrapper, scripts de ejecución y JaCoCo), pero su DevEx y productividad SPACE están limitadas por dos factores principales: poca estandarización de calidad/CI, y alto acoplamiento en el Main con datos y lógica hardcodeada, lo que incrementa la carga cognitiva y el riesgo de regresiones. 
La oportunidad más rentable es crear primero una red de seguridad (CI + cobertura visible) y luego refactorizar por capas (generando APIs de ser necesario), midiendo la mejora con métricas simples (cobertura, tiempo de feedback, lead time de PRs y tiempos de review). Con esto, el equipo puede demostrar no solo que “se movió código”, sino que mejoró la experiencia del desarrollador y la capacidad de entregar cambios confiables de forma consistente.
