## Vibe Coding en el Contexto del Proyecto
## Integrantes:
- Juan David Martínez Méndez - [Fataltester](https://github.com/Fataltester)
- Samuel Alejandro Prieto Reyes - [AlejandroPrieto82](https://github.com/AlejandroPrieto82)
- Santiago Gualdrón Rincón - [Waldron63](https://github.com/Waldron63)

## Introducción
En este taller, se planea mostrar la diferencia entre el **Vibe coding** y **SDD (Spec-driven development)** para la refactorización y el uso de IA dentro del proyecto. Se abordan los contextos de ambos dentro del repositorio, los pros y contras de utilizarlos y sobre qué partes de código se puede aplicar. Esto ayudando a que, en un futuro, la refactorización completa del código sea automatizado, se entienda cómo se le puede pedir a la IA el desarrollo y cómo entregarle el contexto del caso.

## Vibe Coding
**Vibe coding** es una forma de programar donde uno se deja llevar por la intuición, sugerencias de IA, o simplemente por lo que "se siente bien", sin entender del todo qué está pasando por debajo. El término lo acuñó Andrej Karpathy en 2025, describiendo exactamente eso: programar con el "vibe", aceptar el código que la IA genera, y seguir adelante sin auditar a fondo.

En un proyecto de refactorización y optimización como el nuestro, esto es más fácil de lo que parece. Algunos escenarios concretos donde pudimos haber caído en esto:


### 1. Aceptar refactors de IA sin leer el diff completo

Cuando le pedimos a Copilot o ChatGPT "refactoriza esta función para que sea más limpia", la IA produce algo que *se ve* más ordenado. Si simplemente copiamos y pegamos sin revisar línea por línea, estamos vibeando. El código puede cambiar comportamiento y nosotros no nos dimos cuenta, porque no lo leímos.


### 2. Optimizar sin medir primero

"Esto parece lento, voy a usar un diccionario en vez de una lista." Si el cambio no está respaldado por un profiler o una métrica concreta, es vibe coding disfrazado de optimización. La mejora se *siente* lógica pero no hay evidencia de que ese fuera el cuello de botella real.


### 3. Cambiar la arquitectura porque "así lo hacen todos"

Migrar algo a una estructura más "moderna" (digamos, separar en más módulos o cambiar el manejo de estado) porque lo vimos en un tutorial o porque la IA lo sugirió, sin evaluar si aplica al tamaño y necesidades reales del proyecto. Eso es seguir el vibe del ecosistema.


### 4. Usar una librería nueva sin entender qué resuelve

Durante la optimización, si alguien propuso "usemos esta librería que encontré que hace X más rápido" y el equipo la integró sin revisar su mantenimiento, dependencias o si realmente atacaba el problema, eso es vibe coding a nivel de dependencias.


### 5. Confiar en que el código "ya funciona" después de que los tests pasan

Si los tests pasaron y asumimos que la refactorización está bien, sin haber revisado si los tests cubrían los casos que modificamos, estamos dejando que el vibe de "verde = correcto" nos guíe. Los tests pueden pasar y el comportamiento puede haber cambiado igual.

## SDD (Spec-Driven Development)
**Spec‑Driven Development (SDD)** es un enfoque en el cual la especificación ejecutable (entradas → salidas/efectos) guía el desarrollo y la refactorización. En lugar de refactorizar “a ojo”, primero capturas el comportamiento actual (o el esperado) en specs, y luego cambias el código con seguridad porque las specs te avisan si rompiste algo.

### Flujo de trabajo
El repositorio se ejecuta como CLI leyendo comandos desde la clase main; con lo cual se pueden refactorizar los sepcs en main generando el flujo de la siguiente forma:

#### 1. Definir el spec de comportamiento
Describe qué debe ocurrir cuando el programa recibe un archivo de entrada con comandos. Esta spec valida que Main lea el archivo, interprete comandos como ADD_CHILD o GET_RELATIONSHIP y produzca exactamente la salida esperada por consola (incluyendo casos como NONE o PERSON_NOT_FOUND),

#### 2. Agregar specs de unidad para reglas internas
Captura cómo debe comportarse la lógica del dominio dentro de Family. Además, especifica qué debe devolver cada relación (por ejemplo getSon, getSiblings, getPaternaluncle, etc.) y qué debe ocurrir al ejecutar operaciones como addMember en casos de éxito y de error

#### 3. Implementar/refactorizar
Modifica la estructura interna sin alterar el comportamiento. Esto significa poder reorganizar la clase Family (separar resolución de relaciones, evitar el switch, mejorar findPerson, etc.) manteniendo las mismas respuestas para los mismos comandos y árboles familiares.

#### 4. Refinar specs cuando aparezcan bugs
Cada defecto detectado durante el flujo, se transforma en una spec nueva. El conjunto de specs se convierte en el documento vivo del sistema: cada cambio de requerimiento se refleja como un cambio explícito en la spec, y cada bug queda prevenido por una spec que antes fallaba y ahora pasa.

### Implementaciones
1. Nueva funcionalidad: El flujo SDD ayuda a definir primero el resultado esperado y luego implementarlo sin romper lo existente
2. Refactorización de la clase "Family": Aplicado a la refactorización de el código y sus herencias o relaciones de Family.
3. Corrección de bug reproducible: Aplica cuando se encuentra un caso donde el programa responde mal. Primero se escribe la spec que reproduce el fallo y luego se corrige.
4. proteger el contrato del CLI en Main: Dado un archivo de comandos, imprime exactamente lo esperado al igual que Main actualmente.
5. Casos borde y validaciones de reglas de negocio: El comportamiento depende de condiciones (madre inexistente, madre con género inválido para la operación, etc.).

## Análisis
