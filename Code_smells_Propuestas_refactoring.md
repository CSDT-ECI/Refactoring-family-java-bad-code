# Code smells y Propuestas de Refactorización.
## Integrantes:
- Juan David Martínez Méndez - [Fataltester](https://github.com/Fataltester)
- Samuel Alejandro Prieto Reyes - [AlejandroPrieto82](https://github.com/AlejandroPrieto82)
- Santiago Gualdrón Rincón - [Waldron63](https://github.com/Waldron63)

## Introducción
En este documento, se van a identificar y listar los "Code Smells" del proyecto original [family-java-bad-code](https://github.com/geektrust/family-java-bad-code); a partir de estos generaremos las propuestas de "Refactoring" que permitan reducir la deuda técnica, mejorar la mantenibilidad, legibilidad y escalabilidad del sistema para incluirla al proyecto.

## Code Smells
A continuación se documentan los principales "Code smells" encontrados en el proyecto.

## Refactoring
Perfecto 👍 Te lo dejo en un formato **README profesional, limpio y sobrio**, con muy pocos elementos visuales y enfocado a documentación técnica.

# Refactorización del Sistema Familiar
## 1. Arquitectura
### Problema actual
La clase `Family` concentra múltiples responsabilidades:  
* Almacenamiento de datos
* Búsqueda de personas
* Cálculo de relaciones familiares
* Presentación de resultados
Esto viola el principio de responsabilidad única.

### Ejemplo
Si se desea cambiar el almacenamiento de memoria a una base de datos, se debe modificar la misma clase que contiene la lógica de relaciones familiares, aumentando el riesgo de introducir errores.

### Solución
Separar responsabilidades en componentes independientes:  
* Repositorio de datos
* Lógica de negocio
* Capa de presentación
Esto permite modificar el almacenamiento sin afectar la lógica del dominio.


## 2. Manejo de Datos
### Problema actual
El sistema permite cualquier texto para representar género o relaciones, lo que puede generar errores en tiempo de ejecución.

### Ejemplo
Entradas inválidas como:
```
male
Femle
```
No generan errores inmediatos.

### Solución
Uso de `Enum` para restringir los valores válidos.

Beneficios:
* Validación en tiempo de compilación
* Reducción de errores
* Mayor claridad en el modelo de datos

## 3. Código Duplicado
### Problema actual
Existen múltiples métodos para calcular relaciones como tíos y tías que repiten la misma lógica base:
* Acceder a padres
* Obtener abuelos
* Filtrar hermanos

### Impacto
* Mayor complejidad de mantenimiento
* Incremento en la probabilidad de errores
* Dificultad para extender funcionalidades

### Solución
Aplicar el patrón Strategy para reutilizar la lógica común y parametrizar las diferencias.

## 4. Validaciones y Manejo de Errores
### Problema actual
Los errores se manejan mediante impresión directa en consola.

### Ejemplo
Cuando una persona no existe, el sistema muestra un mensaje sin permitir que el error sea procesado por otras capas.

### Solución
Uso de excepciones controladas.

Beneficios:
* Permite manejo flexible de errores
* Facilita integración con APIs
* Mejora el registro de eventos
* Permite pruebas automatizadas más robustas

## 5. Nombres y Organización del Código
### Problema actual
Existen variables y métodos con nombres poco descriptivos o sin uso.

### Impacto
* Dificulta la comprensión del código
* Reduce la mantenibilidad
* Incrementa el riesgo de errores

### Solución
Aplicar convenciones de nombres claras y eliminar código innecesario.

## 6. Patrones de Diseño
### Problema actual
Se utiliza el patrón Singleton para manejar la familia.

### Impacto
* Introduce estado global
* Dificulta las pruebas unitarias
* Genera dependencias implícitas

### Solución
Uso de Inyección de Dependencias.

Beneficios:
* Permite instancias independientes
* Facilita pruebas
* Reduce acoplamiento

## 7. Performance
### Problema actual
La búsqueda de personas se realiza recorriendo listas completas, incluso cuando podrían usarse accesos directos.

### Impacto
El rendimiento disminuye conforme aumenta la cantidad de datos.

### Solución
Uso de estructuras de acceso eficiente como mapas o índices.

## 8. Testeo
### Problema actual
Las funciones dependen de consola, archivos y estado global.

### Impacto
Las pruebas requieren construir estructuras completas de datos, dificultando el testeo aislado.

### Solución
Separar lógica de negocio de dependencias externas, permitiendo pruebas unitarias independientes.

## Beneficios Generales
* Mejor mantenibilidad
* Mayor escalabilidad
* Pruebas más simples y confiables
* Mejor rendimiento
* Código más claro

## Conclusión
La refactorización mejora significativamente la calidad del sistema al separar responsabilidades, reducir duplicación, mejorar el manejo de errores y facilitar el testeo. Estas mejoras permiten que el sistema evolucione de manera más segura y sostenible.

## Impacto en la deuda técnica
La presencia de "Code smells" como clases grandes, métodos extensos, código duplicado, condicionales complejos y alto acoplamiento incrementa significativamente la deuda técnica al dificultar la comprensión, mantenimiento y evolución del sistema.
Estos problemas elevan la complejidad ciclomática, reducen la cohesión y aumentan el riesgo de introducir errores al realizar cambios. 
La aplicación de técnicas de refactorización permite mejorar la estructura del código, reducir la duplicación, desacoplar componentes y facilitar las pruebas unitarias, disminuyendo así el costo de mantenimiento a futuro.

## Conclusiones
El proceso de identificación y refactorización de los "Code smells" contribuye directamente a la reducción de la deuda técnica y al fortalecimiento de la calidad del software.
Al mejorar la legibilidad, modularidad y adherencia a principios como SOLID, el sistema se vuelve más escalable, flexible y sostenible en el tiempo,
permitiendo que futuras modificaciones se realicen con menor riesgo y mayor eficiencia.
