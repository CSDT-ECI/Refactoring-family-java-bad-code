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

---
---
## Prácticas XP Aplicadas al Proyecto

Las prácticas de Extreme Programming (XP) son un conjunto de técnicas orientadas a mejorar la calidad del software y la capacidad del equipo para responder al cambio de forma sostenible. A continuación se analizan las prácticas más relevantes en el contexto de este proyecto, identificando cuáles se están aplicando, cuáles de forma parcial, y cuáles están completamente ausentes.

---

### Test-Driven Development (TDD) _No se aplica_

El proyecto no cuenta con ninguna clase de pruebas, ni unitarias ni de integración. No existe ningún archivo de test que valide el comportamiento de los métodos implementados.

**Qué falta:** Crear pruebas con JUnit para cada método de `Family`, verificando por ejemplo que `getSon()`, `getDaughter()` o `getSiblings()` retornen los valores correctos. También cubrir los casos borde como persona no encontrada (`PERSON_NOT_FOUND`) o intento de agregar un hijo a un hombre (`CHILD_ADDITION_FAILED`).

---

## Simple Design _Se aplica parcialmente_

La estructura general de tres clases (`Main`, `Family`, `Person`) es razonablemente simple. Sin embargo, la clase `Family` acumula demasiadas responsabilidades: búsqueda de personas, adición de miembros, cálculo de todas las relaciones familiares e impresión de resultados en consola, lo cual viola el principio de diseño simple al hacer la clase más compleja de lo necesario.

**Qué falta:** Separar responsabilidades, por ejemplo extrayendo la lógica de impresión o el cálculo de relaciones en clases auxiliares independientes.

---

## Refactoring _No se aplica_

Existe código duplicado evidente. Los métodos `getPaternaluncle`, `getPaternalaunt`, `getMaternaluncle` y `getMaternalaunt` siguen exactamente la misma estructura: buscar al padre o madre del sujeto, buscar al abuelo correspondiente y filtrar sus hijos por género. Esta repetición indica que nunca se realizó un proceso de refactorización para consolidar la lógica común.

**Qué falta:** Extraer un método auxiliar como `getParentSiblingsByGender(Person parent, String gender)` que elimine esa duplicación y centralice la lógica compartida entre esos cuatro métodos.

---

## Coding Standards _Se aplica parcialmente_

En términos generales el código sigue las convenciones de Java: uso de camelCase, nombres de métodos descriptivos y estructura de paquetes coherente. Sin embargo hay inconsistencias que indican ausencia de un estándar revisado: el campo `private static String st = new String()` en `Family` no tiene ningún uso aparente, el comentario `// TODO Auto-generated constructor stub` quedó del IDE sin limpiarse, y hay imports sin usar como `BufferedReader` dentro de `Family`.

**Qué falta:** Limpiar código muerto, eliminar imports innecesarios y asegurarse de que todos los miembros de las clases tengan un propósito claro y justificado.

---

## Continuous Integration (CI) _No se aplica_

No hay evidencia de ningún pipeline de integración continua que compile el proyecto y valide su comportamiento automáticamente ante cada cambio en el código.

**Qué falta:** Configurar un pipeline básico usando herramientas como GitHub Actions o Jenkins que compile el proyecto y ejecute las pruebas unitarias en cada commit, garantizando que el sistema siempre esté en un estado funcional verificable.

---

## Metaphor y Documentación Colectiva _Se aplica parcialmente_

No existe un README ni documentación de alto nivel que explique el modelo del dominio (Hay una, pero esta es un archivo que debe verse como algo "externo" al proyecto, ya que principalmente es para explicarnos la actividad a hacer, mas no uno integrado). Un desarrollador nuevo tendría que leer todo el código para entender qué representa `Family`, cómo interactúa con `Person`, o por qué se usa `spouseRecord` como estructura separada al `record` general.

**Qué falta:** Documentar la metáfora del sistema, en este caso un árbol genealógico, explicando las decisiones de diseño principales y cómo se relacionan las clases entre sí.

### Beneficios Generales

## Impacto

## Conclusiones
