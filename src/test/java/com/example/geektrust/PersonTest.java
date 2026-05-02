package com.example.geektrust;

import com.example.geektrust.service.Person;
import com.example.geektrust.service.PersonImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@Nested
@DisplayName("Person – constructor y getters")
class PersonTest {

    @Test
    @DisplayName("Constructor básico almacena nombre y género")
    void Should_StoreNameAndGender_When_BasicConstructorIsUsed() {
        // Arrange
        Person person = new PersonImpl("TestPerson", "Male");

        // Assert
        assertEquals("TestPerson", person.getName());
        assertEquals("Male", person.getGender());
    }

    @Test
    @DisplayName("Constructor completo almacena padre y madre")
    void Should_StoreFatherAndMother_When_FullConstructorIsUsed() {
        // Arrange
        Person person = new PersonImpl("Child", "Female", "Dad", "Mom");

        // Assert
        assertEquals("Dad", person.getFatherName());
        assertEquals("Mom", person.getMotherName());
    }

    @Test
    @DisplayName("Lista de hijos inicia vacía")
    void Should_StartWithEmptyChildrenList_When_PersonIsCreated() {
        // Arrange
        Person person = new PersonImpl("Solo", "Male");

        // Assert
        assertNotNull(person.getChildren());
        assertTrue(person.getChildren().isEmpty());
    }

    @Test
    @DisplayName("addChild agrega hijo correctamente")
    void Should_AddChildToChildrenList_When_AddChildIsCalled() {
        // Arrange
        Person parent = new PersonImpl("Parent", "Female");
        Person child = new PersonImpl("Child", "Male");

        // Act
        boolean result = parent.addChild(child);

        // Assert
        assertTrue(result);
        assertEquals(1, parent.getChildren().size());
    }

    @Test
    @DisplayName("toString retorna el nombre de la persona")
    void Should_ReturnName_When_ToStringIsCalled() {
        // Arrange
        Person person = new PersonImpl("Asha", "Female");

        // Assert
        assertEquals("Asha", person.toString());
    }

    @Test
    @DisplayName("setters actualizan los campos correctamente")
    void Should_UpdateAllFields_When_SettersAreCalled() {
        // Arrange
        Person person = new PersonImpl("Old", "Male");

        // Act
        person.setName("New");
        person.setGender("Female");
        person.setFatherName("Father");
        person.setMotherName("Mother");
        person.setSpouseName("Spouse");

        // Assert
        assertEquals("New", person.getName());
        assertEquals("Female", person.getGender());
        assertEquals("Father", person.getFatherName());
        assertEquals("Mother", person.getMotherName());
        assertEquals("Spouse", person.getSpouseName());
    }
}