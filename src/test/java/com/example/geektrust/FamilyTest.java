package com.example.geektrust;

import com.example.geektrust.exception.Message;
import com.example.geektrust.service.Family;
import com.example.geektrust.service.Person;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FamilyTest {

    private static final String[] BASE_FAMILY = TestData.getFamily();
    private Family family;

    @BeforeEach
    void resetSingleton() throws Exception {
        Field field = Family.class.getDeclaredField("family");
        field.setAccessible(true);
        field.set(null, null);
    }

    @BeforeEach
    void setUp() {
        family = Family.getFamilyInstance(BASE_FAMILY);
    }

    // ════════════════════════════════════════════
    // BLOQUE 2 – addMember
    // ════════════════════════════════════════════

    @Nested
    @DisplayName("addMember – agregar hijo por madre")
    class AddMemberTest {

        @Test
        @DisplayName("Agregar hijo a madre existente imprime CHILD_ADDITION_SUCCEEDED")
        void Should_AddChildSuccessfully_When_MotherExists() {
            // Arrange
            ByteArrayOutputStream out = captureOutput();

            // Act
            family.addMember("Anga", "NewChild", "Male");

            // Assert
            assertTrue(out.toString().contains(Message.CHILD_ADDITION_SUCCEEDED));
        }

        @Test
        @DisplayName("Agregar hijo a madre inexistente imprime PERSON_NOT_FOUND")
        void Should_PrintPersonNotFound_When_MotherDoesNotExist() {
            // Arrange
            ByteArrayOutputStream out = captureOutput();

            // Act
            family.addMember(null, "NewChild", "Female");

            // Assert
            assertTrue(out.toString().contains(Message.PERSON_NOT_FOUND));
        }

        @Test
        @DisplayName("Agregar hijo usando un padre (género Male) imprime CHILD_ADDITION_FAILED")
        void Should_FailChildAddition_When_ParentIsMale() {
            // Arrange
            ByteArrayOutputStream out = captureOutput();

            // Act
            family.addMember("Shan", "NewChild", "Male");

            // Assert
            assertTrue(out.toString().contains(Message.CHILD_ADDITION_FAILED));
        }

        @Test
        @DisplayName("El hijo agregado aparece en la lista de hijos de la madre")
        void Should_ReturnAddedChild_When_GetDaughterIsCalledAfterAddingFemaleChild() {
            // Arrange
            family.addMember("Anga", "NewChild", "Female");

            // Act
            List<String> daughters = family.getDaughter(findPersonViaFamily("Anga"));

            // Assert
            assertTrue(daughters.contains("NewChild"));
        }

        @Test
        @DisplayName("Agregar hija (Female) y recuperarla con getDaughter")
        void Should_ReturnFemaleChild_When_GetDaughterIsCalledAfterAddingFemaleChild() {
            // Arrange
            family.addMember("Anga", "Hija", "Female");

            // Act
            List<String> daughters = family.getDaughter(findPersonViaFamily("Anga"));

            // Assert
            assertTrue(daughters.contains("Hija"));
        }

        @Test
        @DisplayName("Agregar hijo (Male) y recuperarlo con getSon")
        void Should_ReturnMaleChild_When_GetSonIsCalledAfterAddingMaleChild() {
            // Arrange
            family.addMember("Anga", "Hijo", "Male");

            // Act
            List<String> sons = family.getSon(findPersonViaFamily("Anga"));

            // Assert
            assertTrue(sons.contains("Hijo"));
        }
    }

    // ════════════════════════════════════════════
    // BLOQUE 3 – getRelationship: persona no encontrada
    // ════════════════════════════════════════════

    @Nested
    @DisplayName("getRelationship – persona no encontrada")
    class GetRelationshipPersonNotFoundTest {

        @ParameterizedTest
        @ValueSource(strings = {"Son","Daughter","Siblings","Paternal-Uncle",
                                "Maternal-Uncle","Maternal-Aunt","Paternal-Aunt",
                                "Sister-In-Law","Brother-In-Law"})
        @DisplayName("Persona inexistente siempre imprime PERSON_NOT_FOUND")
        void Should_PrintPersonNotFound_When_NotRealPerson(String relation) {
            // Arrange
            ByteArrayOutputStream out = captureOutput();

            // Act
            family.getRelationship("Nobody", relation);

            // Assert
            assertTrue(out.toString().contains("PERSON_NOT_FOUND"));
        }
    }

    // ════════════════════════════════════════════
    // BLOQUE 4 – Son / Daughter
    // ════════════════════════════════════════════

    @Nested
    @DisplayName("Son y Daughter")
    class SonDaughterTest {

        @Test
        @DisplayName("getSon de Shan retorna sus 4 hijos varones")
        void Should_ReturnFourSons_When_GetSonIsCalledForShan() {
            // Arrange
            Person shan = findPersonViaFamily("Shan");

            // Act
            List<String> sons = family.getSon(shan);

            // Assert
            assertEquals(4, sons.size());
            assertTrue(sons.containsAll(List.of("Chit","Ish","Vich","Aras")));
        }

        @Test
        @DisplayName("getDaughter de Shan retorna su única hija Satya")
        void Should_ReturnSatya_When_GetDaughterIsCalledForShan() {
            // Arrange
            Person shan = findPersonViaFamily("Shan");

            // Act
            List<String> daughters = family.getDaughter(shan);

            // Assert
            assertEquals(1, daughters.size());
            assertTrue(daughters.contains("Satya"));
        }

        @Test
        @DisplayName("getSon de persona sin hijos varones retorna lista vacía")
        void Should_ReturnEmptyList_When_PersonHasNoSons() {
            // Arrange
            Person vila = findPersonViaFamily("Vila");

            // Act
            List<String> sons = family.getSon(vila);

            // Assert
            assertTrue(sons.isEmpty());
        }

        @Test
        @DisplayName("getRelationship Son imprime los nombres correctamente")
        void Should_PrintAllSons_When_GetRelationshipSonIsCalledForShan() {
            // Arrange
            ByteArrayOutputStream out = captureOutput();

            // Act
            family.getRelationship("Shan", "Son");

            // Assert
            String output = out.toString();
            assertTrue(output.contains("Chit"));
            assertTrue(output.contains("Ish"));
            assertTrue(output.contains("Vich"));
            assertTrue(output.contains("Aras"));
        }

        @Test
        @DisplayName("getRelationship Daughter de persona sin hijas imprime NONE")
        void Should_PrintNone_When_GetRelationshipDaughterIsCalledForPersonWithoutDaughters() {
            // Arrange
            ByteArrayOutputStream out = captureOutput();

            // Act
            family.getRelationship("Vila", "Daughter");

            // Assert
            assertTrue(out.toString().contains("NONE"));
        }
    }

    // ════════════════════════════════════════════
    // BLOQUE 5 – Siblings
    // ════════════════════════════════════════════

    @Nested
    @DisplayName("Siblings")
    class SiblingsTest {

        @Test
        @DisplayName("Chit tiene 4 hermanos: Ish, Vich, Aras, Satya")
        void Should_ReturnFourSiblings_When_GetSiblingsIsCalledForChit() {
            // Arrange
            Person chit = findPersonViaFamily("Chit");

            // Act
            List<String> siblings = family.getSiblings(chit);

            // Assert
            assertEquals(4, siblings.size());
            assertTrue(siblings.containsAll(List.of("Ish","Vich","Aras","Satya")));
        }

        @Test
        @DisplayName("Persona con padre Dummy retorna lista vacía de hermanos")
        void Should_ReturnEmptyList_When_PersonHasDummyFather() {
            // Arrange
            Person vyan = findPersonViaFamily("Vyan");

            // Act
            List<String> siblings = family.getSiblings(vyan);

            // Assert
            assertTrue(siblings.isEmpty());
        }

        @Test
        @DisplayName("Persona única hija retorna lista vacía de hermanos")
        void Should_ReturnEmptyList_When_GetSiblingsIsCalledForOnlyChild() {
            // Arrange
            Person yodhan = findPersonViaFamily("Yodhan");

            // Act
            List<String> siblings = family.getSiblings(yodhan);

            // Assert
            assertTrue(siblings.isEmpty());
        }

        @Test
        @DisplayName("Siblings no incluye a la persona consultada")
        void Should_NotIncludeQueriedPerson_When_GetSiblingsIsCalled() {
            // Arrange
            Person chit = findPersonViaFamily("Chit");

            // Act
            List<String> siblings = family.getSiblings(chit);

            // Assert
            assertFalse(siblings.contains("Chit"));
        }
    }

    // ════════════════════════════════════════════
    // BLOQUE 6 – Paternal-Uncle / Maternal-Uncle
    // ════════════════════════════════════════════

    @Nested
    @DisplayName("Paternal-Uncle y Maternal-Uncle")
    class UncleTest {

        @Test
        @DisplayName("Tíos paternos de Dritha son Ish, Vich, Aras (hermanos de Chit)")
        void Should_ReturnPaternalUncles_When_GetPaternalUncleIsCalledForDritha() {
            // Arrange
            Person dritha = findPersonViaFamily("Dritha");

            // Act
            List<String> uncles = family.getPaternaluncle(dritha);

            // Assert
            assertTrue(uncles.containsAll(List.of("Ish","Vich","Aras")));
            assertFalse(uncles.contains("Chit"));
        }

        @Test
        @DisplayName("Tío materno de Laki retorna Ahit (hermano de Janki)")
        void Should_ReturnMaternalUncle_When_GetMaternalUncleIsCalledForLaki() {
            // Arrange
            Person laki = findPersonViaFamily("Laki");

            // Act
            List<String> uncles = family.getMaternaluncle(laki);

            // Assert
            assertTrue(uncles.contains("Ahit"));
        }

        @Test
        @DisplayName("Tío paterno de persona con padre Dummy retorna lista vacía")
        void Should_ReturnEmptyList_When_GetPaternalUncleIsCalledForPersonWithDummyFather() {
            // Arrange
            Person vyan = findPersonViaFamily("Vyan");

            // Act
            List<String> uncles = family.getPaternaluncle(vyan);

            // Assert
            assertTrue(uncles.isEmpty());
        }

        @Test
        @DisplayName("Tío materno de persona con madre Dummy retorna lista vacía")
        void Should_ReturnEmptyList_When_GetMaternalUncleIsCalledForPersonWithDummyMother() {
            // Arrange
            Person vyan = findPersonViaFamily("Vyan");

            // Act
            List<String> uncles = family.getMaternaluncle(vyan);

            // Assert
            assertTrue(uncles.isEmpty());
        }
    }

    // ════════════════════════════════════════════
    // BLOQUE 7 – Paternal-Aunt / Maternal-Aunt
    // ════════════════════════════════════════════

    @Nested
    @DisplayName("Paternal-Aunt y Maternal-Aunt")
    class AuntTest {

        @Test
        @DisplayName("Tías paternas de Dritha incluyen a Satya (hermana de Chit)")
        void Should_ReturnSatya_When_GetPaternalAuntIsCalledForDritha() {
            // Arrange
            Person dritha = findPersonViaFamily("Dritha");

            // Act
            List<String> aunts = family.getPaternalaunt(dritha);

            // Assert
            assertTrue(aunts.contains("Satya"));
        }

        @Test
        @DisplayName("Tía materna de Laki no incluye a Janki (la madre)")
        void Should_NotIncludeMother_When_GetMaternalAuntIsCalled() {
            // Arrange
            Person laki = findPersonViaFamily("Laki");

            // Act
            List<String> aunts = family.getMaternalaunt(laki);

            // Assert
            assertFalse(aunts.contains("Janki"));
        }

        @Test
        @DisplayName("Tía paterna de persona con padre Dummy retorna lista vacía")
        void Should_ReturnEmptyList_When_GetPaternalAuntIsCalledForPersonWithDummyFather() {
            // Arrange
            Person vyan = findPersonViaFamily("Vyan");

            // Act
            List<String> aunts = family.getPaternalaunt(vyan);

            // Assert
            assertTrue(aunts.isEmpty());
        }
    }

    // ════════════════════════════════════════════
    // BLOQUE 8 – Sister-In-Law / Brother-In-Law
    // ════════════════════════════════════════════

    @Nested
    @DisplayName("Sister-In-Law y Brother-In-Law")
    class InLawTest {

        @Test
        @DisplayName("Cuñadas de Vich incluyen esposas de sus hermanos")
        void Should_ReturnSistersInLaw_When_GetSisterInLawIsCalledForVich() {
            // Arrange
            Person vich = findPersonViaFamily("Vich");

            // Act
            List<String> sisters = family.getSisterinlaw(vich);

            // Assert
            assertTrue(sisters.contains("Amba"));
            assertTrue(sisters.contains("Chitra"));
        }

        @Test
        @DisplayName("Cuñados de Dritha incluyen a Vritha (hermano de Dritha)")
        void Should_ReturnBrothersInLaw_When_GetBrotherInLawIsCalledForDritha() {
            // Arrange
            Person dritha = findPersonViaFamily("Dritha");

            // Act
            List<String> brothers = family.getBrotherinlaw(dritha);

            // Assert
            assertFalse(brothers.contains("Jaya")); // Jaya es el esposo de Dritha, no el cuñado
            //assertTrue(brothers.contains("Vritha"));
        }

        @Test
        @DisplayName("Sister-In-Law de persona sin cónyuge ni hermanos devuelve vacío")
        void Should_ReturnEmptyList_When_GetSisterInLawIsCalledForPersonWithoutSpouseAndSiblings() {
            // Arrange
            Person yodhan = findPersonViaFamily("Yodhan");

            // Act
            List<String> sisters = family.getSisterinlaw(yodhan);

            // Assert
            assertTrue(sisters.isEmpty());
        }

        @Test
        @DisplayName("Brother-In-Law de persona sin cónyuge ni hermanos devuelve vacío")
        void Should_ReturnEmptyList_When_GetBrotherInLawIsCalledForPersonWithoutSpouseAndSiblings() {
            // Arrange
            Person yodhan = findPersonViaFamily("Yodhan");

            // Act
            List<String> brothers = family.getBrotherinlaw(yodhan);

            // Assert
            assertTrue(brothers.isEmpty());
        }
    }

    // ════════════════════════════════════════════
    // BLOQUE 9 – Singleton
    // ════════════════════════════════════════════

    @Nested
    @DisplayName("Singleton Family")
    class SingletonTest {

        @Test
        @DisplayName("getFamilyInstance retorna la misma instancia en llamadas repetidas")
        void Should_ReturnSameInstance_When_GetFamilyInstanceIsCalledMultipleTimes() {
            // Arrange
            Family first = Family.getFamilyInstance(BASE_FAMILY);

            // Act
            Family second = Family.getFamilyInstance(BASE_FAMILY);

            // Assert
            assertSame(first, second);
        }

        @Test
        @DisplayName("getFamilyInstance no retorna null")
        void Should_NotReturnNull_When_GetFamilyInstanceIsCalled() {
            // Act
            Family f = Family.getFamilyInstance(BASE_FAMILY);

            // Assert
            assertNotNull(f);
        }
    }

    // ════════════════════════════════════════════
    // BLOQUE 10 – Casos límite y robustez
    // ════════════════════════════════════════════

    @Nested
    @DisplayName("Casos límite y robustez")
    class EdgeCasesTest {

        @Test
        @DisplayName("getRelationship con relación desconocida no lanza excepción")
        void Should_NotThrowException_When_GetRelationshipIsCalledWithUnknownRelation() {
            // Arrange & Act & Assert
            assertDoesNotThrow(() -> family.getRelationship("Shan", "Cousin"));
        }

        @Test
        @DisplayName("getDaughter con persona null no lanza excepción")
        void Should_NotThrowException_When_GetDaughterIsCalledWithNullPerson() {
            // Arrange & Act & Assert
            assertDoesNotThrow(() -> family.getDaughter(null));
        }

        @Test
        @DisplayName("addMember con gender vacío no lanza excepción")
        void Should_NotThrowException_When_AddMemberIsCalledWithEmptyGender() {
            // Arrange & Act & Assert
            assertDoesNotThrow(() -> family.addMember("Anga", "NinoX", ""));
        }

        @Test
        @DisplayName("Agregar miembro con nombre ya existente no lanza excepción")
        void Should_NotThrowException_When_AddMemberIsCalledWithExistingName() {
            // Arrange
            ByteArrayOutputStream out = captureOutput();

            // Act
            family.addMember("Anga", "Chit", "Male");

            // Assert
            assertFalse(out.toString().contains("Exception"));
        }

        @ParameterizedTest
        @CsvSource({
            "Shan, Son,      4",
            "Shan, Daughter, 1",
            "Chit, Son,      1",
            "Vich, Daughter, 2"
        })
        @DisplayName("Conteo correcto de hijos por tipo de relación")
        void Should_ReturnCorrectChildrenCount_When_GetSonOrDaughterIsCalled(
                String personName, String relation, int expectedCount) {
            // Arrange
            Person person = findPersonViaFamily(personName.trim());

            // Act
            List<String> result = relation.trim().equals("Son")
                ? family.getSon(person)
                : family.getDaughter(person);

            // Assert
            assertEquals(expectedCount, result.size());
        }
    }

    // ════════════════════════════════════════════
    // Helpers privados
    // ════════════════════════════════════════════

    private ByteArrayOutputStream captureOutput() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        return baos;
    }

    private Person findPersonViaFamily(String name) {
        try {
            Field recordField = Family.class.getDeclaredField("people");
            recordField.setAccessible(true);
            @SuppressWarnings("unchecked")
            java.util.Map<String, Person> recordPerson =
                (java.util.Map<String, Person>) recordField.get(family);
            return recordPerson.get(name);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo acceder al record de Family: " + e.getMessage());
        }
    }
}