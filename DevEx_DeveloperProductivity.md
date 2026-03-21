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
