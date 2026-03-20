## Developer Experience (DevEx)

DevEx es un framework que evalúa cómo se siente trabajar en un proyecto desde la perspectiva del desarrollador. Se centra en tres dimensiones: **feedback loops** (qué tan rápido sabes si algo funciona), **cognitive load** (cuánto esfuerzo mental cuesta entender el código) y **flow state** (qué tan fácil es trabajar sin interrupciones ni confusión).

---

### Feedback Loops

El proyecto original no tenía ninguna prueba unitaria. La única forma de saber si algo funcionaba era ejecutar el programa completo con un archivo de entrada y leer la salida en consola a mano, lo cual hace que cualquier cambio pequeño tome varios minutos en verificarse.

Como equipo agregamos pruebas con JUnit 5 en `FamilyTest` y `PersonTest`, cubriendo los métodos principales de `Family` y `Person`. Esto redujo el tiempo de verificación de minutos a segundos.

Lo que falta: un pipeline de CI (GitHub Actions por ejemplo) que corra las pruebas automáticamente en cada commit, para que el feedback sea inmediato sin que nadie tenga que acordarse de correrlas.

---

### Cognitive Load

La clase `Family` tiene más de 300 líneas y hace de todo: guarda datos, busca personas, calcula relaciones y además imprime resultados en consola. Eso obliga al desarrollador a leer y entender toda la clase antes de poder tocar cualquier cosa.

A eso se le suma:
- Magic strings como `"Dummy"`, `"Male"` y `"Female"` regados por todo el código sin ninguna constante ni enum que los explique
- El árbol familiar completo hardcodeado directamente en el `main`, mezclado con la lógica de lectura de archivos
- Código muerto visible: `private static String st = new String()` en `Family` que nunca se usa, y el comentario `// TODO Auto-generated constructor stub` que quedó del IDE
- El método `findPerson()` recorre todo el HashMap con un for-each cuando podría usar `record.get(name)` directamente, lo que hace pensar que hay alguna lógica oculta ahí cuando no la hay

Todo eso genera ruido que el desarrollador tiene que ignorar conscientemente cada vez que lee el código.

---

### Flow State

La estructura de tres clases es fácil de navegar, eso es positivo. Pero una vez dentro de `Family` el flujo se rompe seguido porque los métodos `getPaternaluncle`, `getPaternalaunt`, `getMaternaluncle` y `getMaternalaunt` hacen casi exactamente lo mismo con variaciones mínimas, entonces uno termina leyendo cuatro veces la misma lógica para entender una sola cosa.

También el patrón `if(fatherName.equals("Dummy"))` aparece en múltiples métodos sin ninguna explicación de por qué "Dummy" es un valor especial, lo que genera dudas cada vez que uno se lo encuentra.

Con las pruebas que agregamos al menos se puede verificar el comportamiento rápido, lo que reduce un poco esa sensación de incertidumbre al hacer cambios.

---

### Resumen

| Dimensión | Estado original | Con mejoras del equipo |
|---|---|---|
| Feedback Loops | Sin pruebas, verificación manual | Pruebas unitarias con JUnit 5 |
| Cognitive Load | Clase de 300+ líneas, magic strings, código muerto | Documentado, pendiente refactorizar |
| Flow State | Lógica duplicada, sin documentación interna | Sin cambio estructural aún |
| Cobertura de pruebas | 0% | ~80% métodos públicos de `Family` |

El problema más grande en términos de DevEx es el cognitive load. Una clase que hace todo es difícil de mantener, difícil de leer y difícil de modificar sin miedo a romper algo. Las pruebas unitarias son la mejora más concreta que hicimos, pero la deuda técnica estructural sigue ahí.