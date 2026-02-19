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

## XP practices

### Beneficios Generales

## Impacto

## Conclusiones
