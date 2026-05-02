---
title: Clean code y XP practices
nav_order: 5
---

# Clean code y XP practices.
## Integrantes:
- Juan David Martínez Méndez - [Fataltester](https://github.com/Fataltester)
- Samuel Alejandro Prieto Reyes - [AlejandroPrieto82](https://github.com/AlejandroPrieto82)
- Santiago Gualdrón Rincón - [Waldron63](https://github.com/Waldron63)

## Introducción
En este documento, se van a identificar, listar y diferenciar, las características que se cummplen y no se cumplen del "Clean code"; los principios de programación aplicados junto con las prácticas XP vistas en la clase.
A partir de estos datos, podremos aterrizar los diferentes cambios que se deben aplicar al código, incluyendo las prácticas que se deben incluir y los principios necesarios para generar un codigo limpio.

## Clean code
En los siguientes puntos se van a indicar todas las características que se cumplen en el codigo y cuales no se cumplieron:

### Características cumplidas
- **Entendible:** Aunque el código no cumple todos los principios de programación, se puede ver después de revisar el código; que no se generaron funcionalidades innecesarias (YAGNI);
Mas los códigos de cada metodo (Aunque se pueden refactorizar), se puede comprender cual es la finalidad del método, con lo cual se puede mantener "simple de entender" (KISS) pero con una "alta complejidad" en costes del código.

- **Duplicidad:** Analizando el código, cada uno de los métodos o funciones hechas, tienen una finalidad única; con lo cual ninguno de los métodos genera duplicidad en su funcionamiento.

- **Principio menor asombro:** Todos los métodos tienen la misma estructura "get...", "set...", "add...", "create..."; las cuales son la base para entender qué es lo que va a hacer el método.
Además de que cada nombre tiene diferencias en sus palabras, indicando a qué parentezco familiar se está refiriendo y si se utiliza ese método, retorna lo esperado por el usuario a partir de leer solamente el nombre de este.

### Características no cumplidas
- **Código enfocado:** No hay un codigo limpio, cada una de las clases hechas tienen hasta 300 lineas de código; con lo cual no es un código enfocado y limpio.
  
- **Regla del Boy Scout:** Se puede notar en diferentes partes del código (especialmente en la clase main) que se pueden generar refactorizaciones que ayudarán a la pureza del código,
 dividiendolo en clases que ayudarán a un funcionamiento mas específico.

- **Escalable:** Para un desarrollador, el código está hecho a "fuerza bruta" con lo que se puede leer de manera literal para entender el funcionamiento, pero al ser tan amplios los métodos,
no es fácil ubicar cada una de las partes importantes del código; ademas de las credenciales y datos que fueron quemados en el código, lo cual genera ruido visual para el programador.

- **Abstracción:** Cada una de las clases hechas, tiene hasta 300 lineas de código. No se utilizan interfaces, abstracciones ni herencias. Con lo cual falla en esta caracterítica. Sin mencionar que no está el CRUD claro ni bien modelado.

- **Testeable:** Se puede notar (en la carpeta TEST), que no e generó ninguna prueba unitaria al código; por lo menos a modo de casos borde o funcionamiento del código. NO se puede saber con exactitud si realmente funciona o no.

## Principios de programación

De los principios SOLID evidenciamos que no se cumplen:

S -> La clase `Family` tiene múltiples responsabilidades como gestionar el árbol genealógico, crear y poblar la familia inicial, buscar personas en el registro, calcular relaciones familiares, imprimir resultados y validar datos de entrada.
Por lo que debería haber una division de clases para realizar estas tareas.
La clase `Main` tiene múltiples responsabilidades como definir datos mockeados de la familia, leer archivos, parsear comandos y ejecutar la lógica de negocio. Por lo que debería haber una división de clases para esas tareas.

O -> el método `getRelationship()` en `Family` tiene un switch case muy largo el cual al llegar a necesitar extensión se debe modificar el método y agregar el nuevo caso al switch violando este principio, por lo que se debería usar un patrón Strategy para las relaciones.

L -> No se evidencia violación directa.

I -> En la clase `Person`, `spouseName` nunca se usa, `setChildren()` nunca se invoca y los getters/setters estan expuestos innecesariamente, por lo que se deberían crear interfaces específicas si se necesitan varias vistas de `Person`.

D -> La clase `Family` depende directamente de implementaciones concretas, es decir, depende de un HashMap especificamente, no hay abstracción para el repositorio de personas, por lo que debería haber inyección de dependencias vía constructor, usar interfaces para repositorios y separar la lógica de presentación de la lógica de negocio.

Principios adicionales 

DRY -> Se evidencia código duplicado en múltiples lugares por lo que debería extrarse en métodos auxiliares únicos.

KISS -> Se evidencia complejidad innecesaria en el método `findPerson(String name)`.

YAGNI -> Hay bastantes lineas de código no utilizadas que no aportan valor real.

Law of Demeter -> El código conoce demasiado sobre la estructura interna, se deben cruzar múltiples objetos para obtener información por lo que deberían haber métodos que reduzcan esos cruces y obtener la información directamente.

Encapsulamiento -> Hay problemas de encapsulamiento dentro de la clase `Person` que expone toda su estructura interna, Magic Strings sin encapsular y logica de negocio expuesta.

Separation of Concerns -> Hay métodos que mezclan responsabilidades, que deberían estar separados en diferentes capas.

---
## Prácticas XP Aplicadas al Proyecto

Las prácticas de Extreme Programming (XP) son un conjunto de técnicas orientadas a mejorar la calidad del software y la capacidad del equipo para responder al cambio de forma sostenible. A continuación se analizan las prácticas más relevantes en el contexto de este proyecto, identificando cuáles se están aplicando, cuáles de forma parcial, y cuáles están completamente ausentes.

---

### Test-Driven Development (TDD) _No se aplica_

El proyecto no cuenta con ninguna clase de pruebas, ni unitarias ni de integración. No existe ningún archivo de test que valide el comportamiento de los métodos implementados.

**Qué falta:** Crear pruebas con JUnit para cada método de `Family`, verificando por ejemplo que `getSon()`, `getDaughter()` o `getSiblings()` retornen los valores correctos. También cubrir los casos borde como persona no encontrada (`PERSON_NOT_FOUND`) o intento de agregar un hijo a un hombre (`CHILD_ADDITION_FAILED`).

---

### Simple Design _Se aplica parcialmente_

La estructura general de tres clases (`Main`, `Family`, `Person`) es razonablemente simple. Sin embargo, la clase `Family` acumula demasiadas responsabilidades: búsqueda de personas, adición de miembros, cálculo de todas las relaciones familiares e impresión de resultados en consola, lo cual viola el principio de diseño simple al hacer la clase más compleja de lo necesario.

**Qué falta:** Separar responsabilidades, por ejemplo extrayendo la lógica de impresión o el cálculo de relaciones en clases auxiliares independientes.

---

### Refactoring _No se aplica_

Existe código duplicado evidente. Los métodos `getPaternaluncle`, `getPaternalaunt`, `getMaternaluncle` y `getMaternalaunt` siguen exactamente la misma estructura: buscar al padre o madre del sujeto, buscar al abuelo correspondiente y filtrar sus hijos por género. Esta repetición indica que nunca se realizó un proceso de refactorización para consolidar la lógica común.

**Qué falta:** Extraer un método auxiliar como `getParentSiblingsByGender(Person parent, String gender)` que elimine esa duplicación y centralice la lógica compartida entre esos cuatro métodos.

---

### Coding Standards _Se aplica parcialmente_

En términos generales el código sigue las convenciones de Java: uso de camelCase, nombres de métodos descriptivos y estructura de paquetes coherente. Sin embargo hay inconsistencias que indican ausencia de un estándar revisado: el campo `private static String st = new String()` en `Family` no tiene ningún uso aparente, el comentario `// TODO Auto-generated constructor stub` quedó del IDE sin limpiarse, y hay imports sin usar como `BufferedReader` dentro de `Family`.

**Qué falta:** Limpiar código muerto, eliminar imports innecesarios y asegurarse de que todos los miembros de las clases tengan un propósito claro y justificado.

---

### Continuous Integration (CI) _No se aplica_

No hay evidencia de ningún pipeline de integración continua que compile el proyecto y valide su comportamiento automáticamente ante cada cambio en el código.

**Qué falta:** Configurar un pipeline básico usando herramientas como GitHub Actions o Jenkins que compile el proyecto y ejecute las pruebas unitarias en cada commit, garantizando que el sistema siempre esté en un estado funcional verificable.

---

### Metaphor y Documentación Colectiva _Se aplica parcialmente_

No existe un README ni documentación de alto nivel que explique el modelo del dominio (Hay una, pero esta es un archivo que debe verse como algo "externo" al proyecto, ya que principalmente es para explicarnos la actividad a hacer, mas no uno integrado). Un desarrollador nuevo tendría que leer todo el código para entender qué representa `Family`, cómo interactúa con `Person`, o por qué se usa `spouseRecord` como estructura separada al `record` general.

**Qué falta:** Documentar la metáfora del sistema, en este caso un árbol genealógico, explicando las decisiones de diseño principales y cómo se relacionan las clases entre sí.

## Beneficios Generales

* **Mantenibilidad:** Un código limpio y bien estructurado es más fácil de modificar sin introducir errores nuevos.
* **Detección temprana de fallos:** Las pruebas unitarias con TDD permiten identificar bugs antes de que lleguen a producción.
* **Reducción de deuda técnica:** El refactoring continuo evita que el código se vuelva progresivamente más difícil de entender y modificar.
* **Incorporación de nuevos desarrolladores:** Una buena documentación y estándares claros permiten que alguien nuevo entienda el proyecto sin depender de quien lo escribió.
* **Confianza en los cambios:** Con CI y pruebas automatizadas, cada modificación puede verificarse de forma inmediata, reduciendo el miedo a romper funcionalidades existentes.
* **Colaboración más efectiva:** Coding standards y ownership colectivo permiten que cualquier miembro del equipo trabaje sobre cualquier parte del código sin fricciones.

## Impacto
Analizar este proyecto bajo los lentes de Clean Code y XP no es un ejercicio meramente académico: evidencia cómo decisiones aparentemente pequeñas, como dejar un campo estático sin uso, no escribir ninguna prueba o duplicar lógica en cuatro métodos casi idénticos, se acumulan silenciosamente hasta convertirse en una deuda técnica real que compromete la escalabilidad, la confiabilidad y la colaboración dentro del equipo. Adoptar estas prácticas de forma disciplinada transforma el código de algo que simplemente funciona en algo que puede crecer, adaptarse y ser comprendido por cualquier persona que se integre al proyecto, que es precisamente el estándar al que debería aspirar cualquier desarrollo de software profesional.

## Conclusiones
El análisis del código revela una realidad común en el desarrollo de software: un sistema que funciona, pero que no está preparado para evolucionar. Si bien cumple su propósito funcional de gestionar un árbol genealógico, presenta deficiencias estructurales críticas que comprometen su mantenibilidad y escalabilidad.

### Hallazgos Principales

* Clean Code: Clases de 300+ líneas, ausencia total de pruebas, datos hardcodeados y código muerto
* SOLID: Violación masiva de SRP (clase Family con múltiples responsabilidades), OCP (switch que obliga a modificar código existente), ISP (métodos no utilizados) y DIP (dependencias directas sin abstracciones)
* Principios adicionales: DRY violado con lógica duplicada, Law of Demeter ignorado, encapsulamiento débil con strings mágicos, etc.
* Prácticas XP: TDD ausente (0 pruebas), refactoring no aplicado, CI inexistente, documentación insuficiente

### Impacto Real

* Mantenibilidad comprometida: Cualquier cambio requiere modificaciones en múltiples lugares
* Escalabilidad limitada: Agregar funcionalidades exige refactorizar todo el sistema
* Testabilidad nula: Sin garantía de que los cambios no rompan funcionalidades existentes
* Colaboración dificultada: Curva de aprendizaje innecesariamente empinada para nuevos desarrolladores

### Reflexión Final
Este proyecto ejemplifica la diferencia entre código que funciona y código profesional. La acumulación de deuda técnica no ocurre con errores catastróficos, sino silenciosamente: un campo sin uso aquí, código duplicado allá, una validación omitida más adelante. Cada decisión aparentemente menor se suma hasta crear un sistema funcionalmente correcto pero estructuralmente frágil.
Adoptar Clean Code, SOLID y XP no es un lujo académico: es la diferencia entre un código que se convierte en lastre y uno que habilita al equipo para entregar valor sosteniblemente. Es transformar el desarrollo de software de un arte frágil en una ingeniería predecible, colaborativa y profesional.
