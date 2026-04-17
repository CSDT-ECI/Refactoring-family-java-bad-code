# Architectural Debt
## Integrantes:
- Juan David Martínez Méndez - [Fataltester](https://github.com/Fataltester)
- Samuel Alejandro Prieto Reyes - [AlejandroPrieto82](https://github.com/AlejandroPrieto82)
- Santiago Gualdrón Rincón - [Waldron63](https://github.com/Waldron63)

## Introducción
En esta sección documentamos la deuda arquitectónica (Architectural Debt) dentro del proyecto,
que aumentan el costo de cambio, la fragilidad y el riesgo de regresiones a medida que el sistema evoluciona. 
A parte de listar las "Architectural smells", tambien se evidencia su impacto y relación con referencias y herramientas
(p. ej., SonarCloud, Designite o taxonomías de smells) para orientar refactors que reduzcan la deuda sin alterar el comportamiento observable del sistema.

## Architectural Smells

### 1. God Component / Feature Concentration
Aplica cuando gran parte de la lógica del sistema está “pegada” en una sola clase y se convierte en el punto obligatorio para cualquier cambio. 
Family.java concentra la creación del árbol, almacenamiento, búsqueda, ejecución de comandos, resolución de relaciones y salida por consola, 
lo que incrementa el costo de modificar/entender y favorece regresiones.

Para este caso, Se está incumpliento los atributos de calidad: 
* **modificabilidad:** la clase no se puede modificar sin dañar toda la lógica directamente.
* **escalabilidad:** no puede crecer por su alta complejidad en 1 sola clase.
* **soportabilidad:** no se le puede dar soporte a una clase tan amplia pues no se sabe qué métodos están conectados entre si.

## 2. Cyclic Dependency / Dependency Smell
Family.java y Person.java tienen una dependencia bidireccional: Family crea y manipula instancias de Person, mientras que Person expone su lista de hijos (List<Person> children) que Family itera y modifica directamente desde afuera mediante addChild(). Esto genera un acoplamiento circular entre ambas clases donde ninguna puede evolucionar de forma independiente.
Por ejemplo, si se quisiera cambiar la forma en que se almacenan los hijos (e.g., usar un Set en vez de un List), habría que modificar tanto Person como todos los métodos en Family que iteran sobre esa lista.
Atributos de calidad afectados:

- **modificabilidad:** un cambio en la estructura interna de Person obliga a revisar Family y viceversa.
- **testeabilidad:** no se puede probar Family sin instanciar Person y no se puede probar Person sin que Family lo gestione.
- **reusabilidad:** Person no puede usarse en otro contexto sin arrastrar la lógica de Family.

### 3. Hardcoded Data / Magic Values
En `Main.java`, los datos de la familia completa están hardcodeados directamente en el código fuente como un arreglo de Strings (`inputFamily`). Esto significa que cualquier cambio en la estructura familiar obliga a modificar y recompilar el código fuente, cuando idealmente debería leerse desde un archivo de configuración externo o base de datos.

Además, el uso de `"Dummy"` como valor centinela para indicar que un padre es desconocido es una convención implícita que no está documentada ni centralizada. Si en algún momento se quisiera cambiar ese valor por `"Unknown"` o `null`, habría que rastrearlo manualmente en múltiples métodos de `Family.java` (`getSiblings`, `getPaternaluncle`, `getMaternalaunt`, etc.).

Atributos de calidad afectados:
* **modificabilidad:** cambiar los datos de entrada o el valor centinela requiere tocar múltiples puntos del código.
* **mantenibilidad:** un desarrollador nuevo no tiene forma de saber qué significa `"Dummy"` sin leer toda la clase.
* **portabilidad:** el sistema no puede adaptarse a distintos conjuntos de datos sin recompilar.

## Conslusion