package com.example.geektrust;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FamilyTest {

    private static final String[] BASE_FAMILY = {
        "Shan Anga Chit Male",
        "Shan Anga Ish Male",
        "Shan Anga Vich Male",
        "Shan Anga Aras Male",
        "Dummy Dummy Vyan Male",
        "Dummy Dummy Amba Female",
        "Dummy Dummy Lika Female",
        "Dummy Dummy Chitra Female",
        "Shan Anga Satya Female",
        "Chit Amba Dritha Female",
        "Chit Amba Tritha Female",
        "Chit Amba Vritha Male",
        "Dummy Dummy Jaya Male",
        "Jaya Dritha Yodhan Male",
        "Vich Lika Vila Female",
        "Vich Lika Chika Female",
        "Aras Chitra Janki Female",
        "Aras Chitra Ahit Male",
        "Dummy Dummy Arit Male",
        "Arit Janki Laki Male",
        "Arit Janki Lavnya Female",
        "Vyan Satya Asva Male",
        "Vyan Satya Vyas Male",
        "Vyan Satya Atya Female",
        "Dummy Dummy Satvy Female",
        "Asva Satvy Vasa Male",
        "Dummy Dummy Krpi Female",
        "Vyas Krpi Kriya Male",
        "Vyas Krpi Krithi Female",
        "Chit Amba",
        "Vich Lika",
        "Aras Chitra",
        "Vyan Satya",
        "Jaya Dritha",
        "Arit Janki",
        "Asva Satvy",
        "Vyas Krpi"
    };

    private Family family;

    @BeforeEach
    void resetSingleton() throws Exception {
        Field field = Family.class.getDeclaredField("family");
        field.setAccessible(true);
        field.set(null, null);
    }

    @BeforeEach
    void setUp() throws IOException {
        family = Family.getFamilyInstance(BASE_FAMILY);
    }

    // ════════════════════════════════════════════
    // BLOQUE 1 – Modelo Person
    // ════════════════════════════════════════════

    @Nested
    @DisplayName("Person – constructor y getters")
    class PersonModelTest {

        @Test
        @DisplayName("Constructor básico almacena nombre y género")
        void basicConstructor_storesNameAndGender() {
            // Arrange & Act
            Person person = new Person("TestPerson", "Male");

            // Assert
            assertEquals("TestPerson", person.getName());
            assertEquals("Male", person.getGender());
        }

        @Test
        @DisplayName("Constructor completo almacena padre y madre")
        void fullConstructor_storesFatherAndMother() {
            // Arrange & Act
            Person person = new Person("Child", "Female", "Dad", "Mom");

            // Assert
            assertEquals("Dad", person.getFatherName());
            assertEquals("Mom", person.getMotherName());
        }

        @Test
        @DisplayName("Lista de hijos inicia vacía")
        void newPerson_childrenListIsEmpty() {
            // Arrange & Act
            Person person = new Person("Solo", "Male");

            // Assert
            assertNotNull(person.getChildren());
            assertTrue(person.getChildren().isEmpty());
        }

        @Test
        @DisplayName("addChild agrega hijo correctamente")
        void addChild_appendsChildToList() {
            // Arrange
            Person parent = new Person("Parent", "Female");
            Person child  = new Person("Child", "Male");

            // Act
            boolean result = parent.addChild(child);

            // Assert
            assertTrue(result);
            assertEquals(1, parent.getChildren().size());
            assertEquals("Child", parent.getChildren().get(0).getName());
        }

        @Test
        @DisplayName("toString retorna el nombre de la persona")
        void toString_returnsName() {
            // Arrange
            Person person = new Person("Asha", "Female");

            // Act & Assert
            assertEquals("Asha", person.toString());
        }

        @Test
        @DisplayName("setters actualizan los campos correctamente")
        void setters_updateFields() {
            // Arrange
            Person person = new Person("Old", "Male");

            // Act
            person.setName("New");
            person.setGender("Female");
            person.setFatherName("Father");
            person.setMotherName("Mother");
            person.setSpouseName("Spouse");

            // Assert
            assertEquals("New",    person.getName());
            assertEquals("Female", person.getGender());
            assertEquals("Father", person.getFatherName());
            assertEquals("Mother", person.getMotherName());
            assertEquals("Spouse", person.getSpouseName());
        }
    }

    // ════════════════════════════════════════════
    // BLOQUE 2 – addMember
    // ════════════════════════════════════════════

    @Nested
    @DisplayName("addMember – agregar hijo por madre")
    class AddMemberTest {

        @Test
        @DisplayName("Agregar hijo a madre existente imprime CHILD_ADDITION_SUCCEEDED")
        void addMember_validMother_succeeds() {
            // Arrange
            ByteArrayOutputStream out = captureOutput();

            // Act
            family.addMember("Anga", "NewChild", "Male");

            // Assert
            assertTrue(out.toString().contains("CHILD_ADDITION_SUCCEEDED"));
        }

        @Test
        @DisplayName("Agregar hijo a madre inexistente imprime PERSON_NOT_FOUND")
        void addMember_unknownMother_printsPersonNotFound() {
            // Arrange
            ByteArrayOutputStream out = captureOutput();

            // Act
            family.addMember("Ghost", "NewChild", "Female");

            // Assert
            assertTrue(out.toString().contains("PERSON_NOT_FOUND"));
        }

        @Test
        @DisplayName("Agregar hijo usando un padre (género Male) imprime CHILD_ADDITION_FAILED")
        void addMember_maleAsParent_printsFailed() {
            // Arrange
            ByteArrayOutputStream out = captureOutput();

            // Act
            family.addMember("Shan", "NewChild", "Male");

            // Assert
            assertTrue(out.toString().contains("CHILD_ADDITION_FAILED"));
        }

        @Test
        @DisplayName("El hijo agregado aparece en la lista de hijos de la madre")
        void addMember_childAppearsInMothersChildren() {
            // Arrange
            family.addMember("Anga", "NewChild", "Female");

            // Act
            List<String> daughters = family.getDaughter(findPersonViaFamily("Anga"));

            // Assert
            assertTrue(daughters.contains("NewChild"));
        }

        @Test
        @DisplayName("Agregar hija (Female) y recuperarla con getDaughter")
        void addMember_femaleChild_retrievedAsDaughter() {
            // Arrange
            family.addMember("Anga", "Hija", "Female");

            // Act
            List<String> daughters = family.getDaughter(findPersonViaFamily("Anga"));

            // Assert
            assertTrue(daughters.contains("Hija"));
        }

        @Test
        @DisplayName("Agregar hijo (Male) y recuperarlo con getSon")
        void addMember_maleChild_retrievedAsSon() {
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
        void getRelationship_unknownPerson_printsNotFound(String relation) {
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
        void getSon_shan_returnsFourSons() {
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
        void getDaughter_shan_returnsSatya() {
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
        void getSon_personWithNoSons_returnsEmpty() {
            // Arrange
            Person vila = findPersonViaFamily("Vila");

            // Act
            List<String> sons = family.getSon(vila);

            // Assert
            assertTrue(sons.isEmpty());
        }

        @Test
        @DisplayName("getRelationship Son imprime los nombres correctamente")
        void getRelationship_son_printsSons() {
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
        void getRelationship_daughter_noChildren_printsNone() {
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
        void getSiblings_chit_returnsFourSiblings() {
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
        void getSiblings_personWithDummyFather_returnsEmpty() {
            // Arrange
            Person vyan = findPersonViaFamily("Vyan");

            // Act
            List<String> siblings = family.getSiblings(vyan);

            // Assert
            assertTrue(siblings.isEmpty());
        }

        @Test
        @DisplayName("Persona única hija retorna lista vacía de hermanos")
        void getSiblings_onlyChild_returnsEmpty() {
            // Arrange
            Person yodhan = findPersonViaFamily("Yodhan");

            // Act
            List<String> siblings = family.getSiblings(yodhan);

            // Assert
            assertTrue(siblings.isEmpty());
        }

        @Test
        @DisplayName("Siblings no incluye a la persona consultada")
        void getSiblings_doesNotIncludeSelf() {
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
        void getPaternalUncle_dritha_returnsChitBrothers() {
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
        void getMaternalUncle_laki_returnsAhit() {
            // Arrange
            Person laki = findPersonViaFamily("Laki");

            // Act
            List<String> uncles = family.getMaternaluncle(laki);

            // Assert
            assertTrue(uncles.contains("Ahit"));
        }

        @Test
        @DisplayName("Tío paterno de persona con padre Dummy retorna lista vacía")
        void getPaternalUncle_dummyFather_returnsEmpty() {
            // Arrange
            Person vyan = findPersonViaFamily("Vyan");

            // Act
            List<String> uncles = family.getPaternaluncle(vyan);

            // Assert
            assertTrue(uncles.isEmpty());
        }

        @Test
        @DisplayName("Tío materno de persona con madre Dummy retorna lista vacía")
        void getMaternalUncle_dummyMother_returnsEmpty() {
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
        void getPaternalAunt_dritha_returnsSatya() {
            // Arrange
            Person dritha = findPersonViaFamily("Dritha");

            // Act
            List<String> aunts = family.getPaternalaunt(dritha);

            // Assert
            assertTrue(aunts.contains("Satya"));
        }

        @Test
        @DisplayName("Tía materna de Laki no incluye a Janki (la madre)")
        void getMaternalAunt_laki_excludesJanki() {
            // Arrange
            Person laki = findPersonViaFamily("Laki");

            // Act
            List<String> aunts = family.getMaternalaunt(laki);

            // Assert
            assertFalse(aunts.contains("Janki"));
        }

        @Test
        @DisplayName("Tía paterna de persona con padre Dummy retorna lista vacía")
        void getPaternalAunt_dummyFather_returnsEmpty() {
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
        void getSisterInLaw_vich_returnsAmbaAndChitra() {
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
        void getBrotherInLaw_dritha_returnsVritha() {
            // Arrange
            Person dritha = findPersonViaFamily("Dritha");

            // Act
            List<String> brothers = family.getBrotherinlaw(dritha);

            // Assert
            assertTrue(brothers.contains("Vritha"));
        }

        @Test
        @DisplayName("Sister-In-Law de persona sin cónyuge ni hermanos devuelve vacío")
        void getSisterInLaw_noSpouseNoSiblings_returnsEmpty() {
            // Arrange
            Person yodhan = findPersonViaFamily("Yodhan");

            // Act
            List<String> sisters = family.getSisterinlaw(yodhan);

            // Assert
            assertTrue(sisters.isEmpty());
        }

        @Test
        @DisplayName("Brother-In-Law de persona sin cónyuge ni hermanos devuelve vacío")
        void getBrotherInLaw_noSpouseNoSiblings_returnsEmpty() {
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
        void getFamilyInstance_returnsSameInstance() throws IOException {
            // Arrange
            Family first = Family.getFamilyInstance(BASE_FAMILY);

            // Act
            Family second = Family.getFamilyInstance(BASE_FAMILY);

            // Assert
            assertSame(first, second);
        }

        @Test
        @DisplayName("getFamilyInstance no retorna null")
        void getFamilyInstance_notNull() throws IOException {
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
        void getRelationship_unknownRelation_doesNotThrow() {
            // Arrange & Act & Assert
            assertDoesNotThrow(() -> family.getRelationship("Shan", "Cousin"));
        }

        @Test
        @DisplayName("getDaughter con persona null no lanza excepción")
        void getDaughter_nullPerson_doesNotThrow() {
            // Arrange & Act & Assert
            assertDoesNotThrow(() -> family.getDaughter(null));
        }

        @Test
        @DisplayName("addMember con gender vacío no lanza excepción")
        void addMember_emptyGender_doesNotThrow() {
            // Arrange & Act & Assert
            assertDoesNotThrow(() -> family.addMember("Anga", "NinoX", ""));
        }

        @Test
        @DisplayName("Agregar miembro con nombre ya existente no lanza excepción")
        void addMember_duplicateName_doesNotThrow() {
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
        void childCount_parametrized(String personName, String relation, int expectedCount) {
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
            Field recordField = Family.class.getDeclaredField("record");
            recordField.setAccessible(true);
            @SuppressWarnings("unchecked")
            java.util.Map<String, Person> record =
                (java.util.Map<String, Person>) recordField.get(family);
            return record.get(name);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo acceder al record de Family: " + e.getMessage());
        }
    }
}