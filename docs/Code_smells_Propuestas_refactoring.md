# Code smells y Propuestas de Refactorización.
## Integrantes:
- Juan David Martínez Méndez - [Fataltester](https://github.com/Fataltester)
- Samuel Alejandro Prieto Reyes - [AlejandroPrieto82](https://github.com/AlejandroPrieto82)
- Santiago Gualdrón Rincón - [Waldron63](https://github.com/Waldron63)

## Introducción
En este documento, se van a identificar y listar los "Code Smells" del proyecto original [family-java-bad-code](https://github.com/geektrust/family-java-bad-code); a partir de estos generaremos las propuestas de "Refactoring" que permitan reducir la deuda técnica, mejorar la mantenibilidad, legibilidad y escalabilidad del sistema para incluirla al proyecto.

## Code Smells
#### 1. BLOATERS
##### 1.1 Long Method 
* `getRelationship()` dentro de la clase `Family`
  * Utiliza un switch case para abarcar todos los miembros de la familia posibles
  * Esto hace que sea un método bastante largo y difícil de mantener
##### 1.2 Complex Methods 
* `getSisterinlaw()` y `getBrotherinlaw()` dentro de la clase `Family`
  *  Son muy similares entre sí
  * Tienen una lógica compleja y duplicada
##### 1.3 Long Parameter List 
* `addRelationship()` dentro de la clase `Family`
  * Tiene bastantes parámetros: `fatherName`, `motherName`, `name`, `gender`
  * Sería mejor enviar un único objeto que contenga toda esta información
##### 1.4 Large Class 
* Clase `Family`
  * Es bastante larga y pesada de leer
  * Tiene demasiadas responsabilidades
##### 1.5 Data Clumps (Grupos de Datos)
* Los parámetros `fatherName`, `motherName`, `name`, `gender`
  * Aparecen juntos en bastantes métodos dentro de la clase `Family`
  * Deberían encapsularse en un objeto
#### 2. OBJECT-ORIENTATION ABUSERS
##### 2.1 Switch Statements
* `getRelationship()` dentro de la clase `Family`
  * Utiliza un switch para abarcar varios tipos de relaciones
  * Se puede implementar por medio de polimorfismo para evitar el switch
##### 2.2 Dead Code (Código Muerto)
* `private static String st = new String()`; en la clase `Family`
  * Nunca se utiliza para algo importante o notorio
  * Es código muerto que debería eliminarse
##### 2.3 Refused Bequest (Herencia Rechazada)
* `addChild()` en la clase `Person`
  * Siempre retorna true
  * Debería abarcarse de una mejor manera manejando excepciones u otros casos
#### 3. CHANGE PREVENTERS
##### 3.1 Divergent Change 
  * La clase `Family` se debería modificar por múltiples razones diferentes:
  * Agregar nuevos tipos de relaciones
  * Cambiar la lógica de búsqueda de familiares
  * Modificar cómo se almacenan las personas
##### 3.2 Shotgun Surgery (Cirugía de Escopeta)
* Si cambiamos la forma de identificar a las personas (no identificarlas por nombre), deberíamos modificar:
  * Todos los métodos de búsqueda
  * Todos los Map
  * La clase `Person`
  * La clase `Family`
  * El método `findPerson()` dentro de `Family`
#### 4. DISPENSABLES
##### 4.1 Lazy Class (Clase Perezosa)
* Clase `Person`:
  * Es mayormente un contenedor de datos con getters/setters
  * Podría ser un simple DTO o record
##### 4.2 Dead Code (Código Muerto)
* En la clase `Family`:
  * `private static String st = new String()`; → Nunca se usa
  * En la clase `Person`:
  * `setChildren()` → Nunca es invocado
  * `spouseName` → Atributo con getter/setter pero nunca usado (se usa spouseRecord en su lugar)
##### 4.3 Duplicate Code (Código Duplicado)
* Lógica repetida en la clase `Family`:
  * Lógica repetida en `getSisterinlaw()` y `getBrotherinlaw()`
  * Lógica similar en todos los métodos `getPaternal/Maternal` `Uncle/Aunt`
  * Patrón repetido de chequear "Dummy" en múltiples métodos
##### 4.4 Comments (Comentarios Innecesarios)
* // TODO Auto-generated constructor stub
  * Comentario generado automáticamente sin valor
  * Debería eliminarse
#### 5. COUPLERS
##### 5.1 Feature Envy (Envidia de Características)
* Los métodos de relación en `Family` constantemente acceden a atributos internos de `Person`:
  * `getFatherName()`
  * `getMotherName()`
  * `getGender()`
  * `getChildren()`
##### 5.2 Inappropriate Intimacy (Intimidad Inapropiada)
* La clase `Family` conoce demasiado sobre la estructura interna de `Person`
  * Uso de strings "mágicos" en lugar de constantes o enums:
    * "Dummy"
    * "Male"
    * "Female"

## Refactoring
#### 1. Arquitectura
##### Problema actual
La clase `Family` concentra múltiples responsabilidades:  
* Almacenamiento de datos
* Búsqueda de personas
* Cálculo de relaciones familiares
* Presentación de resultados
Esto viola el principio de responsabilidad única.

#### Ejemplo
Si se desea cambiar el almacenamiento de memoria a una base de datos, se debe modificar la misma clase que contiene la lógica de relaciones familiares, aumentando el riesgo de introducir errores.

#### Solución
Separar responsabilidades en componentes independientes:  
* Repositorio de datos
* Lógica de negocio
* Capa de presentación
Esto permite modificar el almacenamiento sin afectar la lógica del dominio.


### 2. Manejo de Datos
#### Problema actual
El sistema permite cualquier texto para representar género o relaciones, lo que puede generar errores en tiempo de ejecución.

#### Ejemplo
Entradas inválidas como:
```
male
Femle
```
No generan errores inmediatos.

#### Solución
Uso de `Enum` para restringir los valores válidos.

Beneficios:
* Validación en tiempo de compilación
* Reducción de errores
* Mayor claridad en el modelo de datos

### 3. Código Duplicado
#### Problema actual
Existen múltiples métodos para calcular relaciones como tíos y tías que repiten la misma lógica base:
* Acceder a padres
* Obtener abuelos
* Filtrar hermanos

#### Impacto
* Mayor complejidad de mantenimiento
* Incremento en la probabilidad de errores
* Dificultad para extender funcionalidades

#### Solución
Aplicar el patrón Strategy para reutilizar la lógica común y parametrizar las diferencias.

### 4. Validaciones y Manejo de Errores
#### Problema actual
Los errores se manejan mediante impresión directa en consola.

#### Ejemplo
Cuando una persona no existe, el sistema muestra un mensaje sin permitir que el error sea procesado por otras capas.

#### Solución
Uso de excepciones controladas.

Beneficios:
* Permite manejo flexible de errores
* Facilita integración con APIs
* Mejora el registro de eventos
* Permite pruebas automatizadas más robustas

### 5. Nombres y Organización del Código
#### Problema actual
Existen variables y métodos con nombres poco descriptivos o sin uso.

#### Impacto
* Dificulta la comprensión del código
* Reduce la mantenibilidad
* Incrementa el riesgo de errores

#### Solución
Aplicar convenciones de nombres claras y eliminar código innecesario.

### 6. Patrones de Diseño
#### Problema actual
Se utiliza el patrón Singleton para manejar la familia.

#### Impacto
* Introduce estado global
* Dificulta las pruebas unitarias
* Genera dependencias implícitas

#### Solución
Uso de Inyección de Dependencias.

Beneficios:
* Permite instancias independientes
* Facilita pruebas
* Reduce acoplamiento

### 7. Performance
#### Problema actual
La búsqueda de personas se realiza recorriendo listas completas, incluso cuando podrían usarse accesos directos.

#### Impacto
El rendimiento disminuye conforme aumenta la cantidad de datos.

#### Solución
Uso de estructuras de acceso eficiente como mapas o índices.

### 8. Testeo
#### Problema actual
Las funciones dependen de consola, archivos y estado global.

#### Impacto
Las pruebas requieren construir estructuras completas de datos, dificultando el testeo aislado.

#### Solución
Separar lógica de negocio de dependencias externas, permitiendo pruebas unitarias independientes.

### Beneficios Generales
* Mejor mantenibilidad
* Mayor escalabilidad
* Pruebas más simples y confiables
* Mejor rendimiento
* Código más claro


## Impacto en la deuda técnica
La presencia de "Code smells" como clases grandes, métodos extensos, código duplicado, condicionales complejos y alto acoplamiento incrementa significativamente la deuda técnica al dificultar la comprensión, mantenimiento y evolución del sistema.
Estos problemas elevan la complejidad ciclomática, reducen la cohesión y aumentan el riesgo de introducir errores al realizar cambios. 
La aplicación de técnicas de refactorización permite mejorar la estructura del código, reducir la duplicación, desacoplar componentes y facilitar las pruebas unitarias, disminuyendo así el costo de mantenimiento a futuro.

## Conclusiones
El proceso de identificación y refactorización de los "Code smells" contribuye directamente a la reducción de la deuda técnica y al fortalecimiento de la calidad del software.
Al mejorar la legibilidad, modularidad y adherencia a principios como SOLID, el sistema se vuelve más escalable, flexible y sostenible en el tiempo,
permitiendo que futuras modificaciones se realicen con menor riesgo y mayor eficiencia.
