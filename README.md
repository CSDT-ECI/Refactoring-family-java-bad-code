# Refactoring Family Java Bad Code

> **Proyecto:** [family-java-bad-code](https://github.com/geektrust/family-java-bad-code) — Sistema de árbol genealógico en Java  
> **Curso:** CSDT

---

## Integrantes

| Nombre | GitHub |
|---|---|
| Juan David Martínez Méndez | [@Fataltester](https://github.com/Fataltester) |
| Samuel Alejandro Prieto Reyes | [@AlejandroPrieto82](https://github.com/AlejandroPrieto82) |
| Santiago Gualdrón Rincón | [@Waldron63](https://github.com/Waldron63) |

---

## Introducción

Este repositorio contiene el código refactorizado de un proyecto Java que implementa la lógica de un árbol genealógico. El objetivo principal es aplicar buenas prácticas de codificación y principios de diseño de software para mejorar la calidad del código original, que presentaba varios "code smells".

Este repositorio se generó a partir de un proyecto existente con código que necesitaba mejoras significativas. El proceso de refactorización se centró en identificar y solucionar problemas de diseño, estructura y legibilidad, aplicando técnicas de refactorización y principios de código limpio.

---

## Repositorio Original

**Organización:** [@Geektrust](https://github.com/geektrust)

**Repositorio:** [family-java-bad-code](https://github.com/geektrust/family-java-bad-code)

---

## Índice

* [README Original](./Original_README.md)
* [Code Smells y Propuestas de Refactoring](./Code_smells_Propuestas_refactoring.md)
* [Clean Code XP Practice](./Clean_code_XP_practice.md)
* [Testing Debt Primera Entrega](./Testing_debt_Primera_entrega.md)

---

## Prerrequisitos

Asegúrate de contar con los siguientes elementos antes de ejecutar el proyecto:

| Herramienta | Versión requerida | Comando de verificación |
|---|-------------------|------------------------|
| Java (JDK) | 11 o superior | `java -version`        |
| Gradle | 7.0 o superior | `/gradlew -v`          |

---

## Instalación

1. Clona el repositorio:
   ```sh
   git clone https://github.com/tu-usuario/Refactoring-family-java-bad-code.git
   ```
2. Navega al directorio del proyecto:
   ```sh
   cd Refactoring-family-java-bad-code
   ```
3. Construye el proyecto con Gradle:
   ```sh
   ./gradlew build
   ```

---

## Entorno

La estructura del proyecto es la siguiente:

### Antes de la refactorización:
```
.
├── build.gradle
├── Geektrust-Problems1.pdf
├── LICENSE
├── ReadMe.md
├── input.txt
├── rootFamily.txt
├── gradlew
├── gradlew.bat
├── run.sh
├── run.bat
│
├── sample_input
│   ├── input1.txt
│   └── input2.txt
│
└── src
    ├── main/java/com/example/geektrust
    │                          ├── Family.java
    │                          ├── Main.java
    │                          └── Person.java
    │
    └── test/java/com/example/geektrust
                              └── MainTest.java
```

### Después de la refactorización:
```
.
├── build.gradle
├── Clean_code_XP_practice.md
├── Code_smells_Propuestas_refactoring.md
├── Geektrust-Problems1.pdf
├── LICENSE
├── Original_README.md
├── README.md
├── Testing_debt_Primera_entrega.md
├── input.txt
├── rootFamily.txt
├── jacoco.xml
├── gradlew
├── gradlew.bat
├── run.sh
├── run.bat
│
├── sample_input
│   ├── input1.txt
│   └── input2.txt
│
└── src
    ├── main/java/com/example/geektrust
    │                          ├── Family.java
    │                          ├── Main.java
    │                          └── Person.java
    │
    └── test/java/com/example/geektrust
                               ├── FamilyTest.java
                               ├── PersonTest.java
                               └── TestData.java
```

___

## Ejecución

Por definir cuando se complete la refactorización.

___

## Construido Con

* [Java](https://www.java.com/) - Lenguaje de programación
* [Gradle](https://gradle.org/) - Herramienta de automatización de compilación
* [JUnit 5](https://junit.org/junit5/) - Framework de testing
* [Git y GitHub](https://git-scm.com/) - Control de versiones y alojamiento del código
