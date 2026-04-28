---
title: Refactoring Family Java Bad Code
layout: default
nav_order: 1
---

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

**Problemática:** [Geektrust-Problems1.pdf](Geektrust-Problems1.pdf)

---

## Índice

* [README Original](docs/Original_README.md)
* [Code Smells y Propuestas de Refactoring](docs/Code_smells_Propuestas_refactoring.md)
* [Clean Code XP Practice](docs/Clean_code_XP_practice.md)
* [Testing Debt Primera Entrega](docs/Testing_debt_Primera_entrega.md)
* [DevEx Developer Productivity](docs/DevEx_DeveloperProductivity.md)
* [Procesos de CI](docs/Procesos_de_CI.md)
* [Vibe Coding vs SDD](docs/Vibe_codings_SDD.md)
* [Architectural Smells](docs/Architectural_Smells.md)

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
├── docs
│   ├── Architectural_Smells.md
│   ├── Clean_code_XP_practice.md
│   ├── Code_smells_Propuestas_refactoring.md
│   ├── DevEx_DeveloperProductivity.md
│   ├── Original_README.md
│   ├── Procesos_de_CI.md
│   ├── Testing_debt_Primera_entrega.md
│   └── Vibe_codings_SDD.md
├── Geektrust-Problems1.pdf
├── gradlew
├── gradlew.bat
├── input
│   ├── input.txt
│   ├── rootFamily.txt
│   └── sample_input
│       ├── input1.txt
│       └── input2.txt
├── LICENSE
├── README.md
├── reports
│   ├── jacocoindex.html
│   ├── jacoco.xml
│   └── spotbugmain.html
├── scripts
│   ├── run.bat
│   └── run.sh
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

## Construido Con

* [Java](https://www.java.com/) - Lenguaje de programación
* [Gradle](https://gradle.org/) - Herramienta de automatización de compilación
* [JUnit 5](https://junit.org/junit5/) - Framework de testing
* [Git y GitHub](https://git-scm.com/) - Control de versiones y alojamiento del código
