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

### 2.

### 3. 
