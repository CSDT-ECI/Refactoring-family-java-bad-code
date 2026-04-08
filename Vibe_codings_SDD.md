## Vibe Coding en el Contexto del Proyecto

**Vibe coding** es una forma de programar donde uno se deja llevar por la intuición, sugerencias de IA, o simplemente por lo que "se siente bien", sin entender del todo qué está pasando por debajo. El término lo acuñó Andrej Karpathy en 2025, describiendo exactamente eso: programar con el "vibe", aceptar el código que la IA genera, y seguir adelante sin auditar a fondo.

En un proyecto de refactorización y optimización como el nuestro, esto es más fácil de lo que parece. Algunos escenarios concretos donde pudimos haber caído en esto:

---

### 1. Aceptar refactors de IA sin leer el diff completo

Cuando le pedimos a Copilot o ChatGPT "refactoriza esta función para que sea más limpia", la IA produce algo que *se ve* más ordenado. Si simplemente copiamos y pegamos sin revisar línea por línea, estamos vibeando. El código puede cambiar comportamiento y nosotros no nos dimos cuenta, porque no lo leímos.

---

### 2. Optimizar sin medir primero

"Esto parece lento, voy a usar un diccionario en vez de una lista." Si el cambio no está respaldado por un profiler o una métrica concreta, es vibe coding disfrazado de optimización. La mejora se *siente* lógica pero no hay evidencia de que ese fuera el cuello de botella real.

---

### 3. Cambiar la arquitectura porque "así lo hacen todos"

Migrar algo a una estructura más "moderna" (digamos, separar en más módulos o cambiar el manejo de estado) porque lo vimos en un tutorial o porque la IA lo sugirió, sin evaluar si aplica al tamaño y necesidades reales del proyecto. Eso es seguir el vibe del ecosistema.

---

### 4. Usar una librería nueva sin entender qué resuelve

Durante la optimización, si alguien propuso "usemos esta librería que encontré que hace X más rápido" y el equipo la integró sin revisar su mantenimiento, dependencias o si realmente atacaba el problema, eso es vibe coding a nivel de dependencias.

---

### 5. Confiar en que el código "ya funciona" después de que los tests pasan

Si los tests pasaron y asumimos que la refactorización está bien, sin haber revisado si los tests cubrían los casos que modificamos, estamos dejando que el vibe de "verde = correcto" nos guíe. Los tests pueden pasar y el comportamiento puede haber cambiado igual.