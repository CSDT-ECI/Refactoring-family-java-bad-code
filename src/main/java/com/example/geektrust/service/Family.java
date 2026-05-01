package com.example.geektrust.service;

import java.util.*;
import java.util.function.Predicate;
import com.example.geektrust.exception.FamilyException;
import com.example.geektrust.exception.Message;

public class Family {

	private static final String DUMMY = "Dummy";
	private static final String MALE = "Male";
	private static final String FEMALE = "Female";

	private final Map<String, Person> people;
	private final Map<Person, Person> spouseRecord;
	private static Family family;

	private Family(String[] inputFamily) {
		Person king = new PersonImpl("Shan", MALE);
		Person queen = new PersonImpl("Anga", FEMALE);

		people = new HashMap<>();
		spouseRecord = new HashMap<>();

		people.put("Shan", king);
		people.put("Anga", queen);

		spouseRecord.put(king, queen);
		spouseRecord.put(queen, king);

		createFamily(inputFamily);
	}

	private void createFamily(String[] inputFamily) {
		for (String st : inputFamily) {
			String[] command = st.split(" ");
			if (command.length == 4) {
				addRelation(command[0], command[1], command[2], command[3]);
			} else if (command.length == 2) {
				addSpouse(command[0], command[1]);
			}
		}
	}

	public static Family getFamilyInstance(String[] inputFamily) {
		if (family == null) {
			family = new Family(inputFamily);
		}
		return family;
	}

	public void addMember(String motherName, String name, String gender) {
		Person mother = findPerson(motherName);

		if (mother == null) {
			System.out.println(Message.PERSON_NOT_FOUND);
			return;
		}

		if (MALE.equals(mother.getGender())) {
			System.out.println(Message.CHILD_ADDITION_FAILED);
			return;
		}

		Person father = spouseRecord.get(mother);
		if (father == null) {
			father = new PersonImpl(DUMMY, MALE);
		}

		Person child = new PersonImpl(name, gender, father.getName(), motherName);

		people.put(name, child);
		mother.addChild(child);
		father.addChild(child);

		System.out.println(Message.CHILD_ADDITION_SUCCEEDED);
	}

	public void addRelation(String fatherName, String motherName, String name, String gender) {
		Person father = findPerson(fatherName);
		Person mother = findPerson(motherName);

		Person child = new PersonImpl(name, gender, fatherName, motherName);

		people.put(name, child);
		people.putIfAbsent(fatherName, father);
		people.putIfAbsent(motherName, mother);

		if (father != null) father.addChild(child);
		if (mother != null) mother.addChild(child);
	}

	public void addSpouse(String husbandName, String wifeName) {
		Person husband = findPerson(husbandName);
		Person wife = findPerson(wifeName);

		if (husband == null || wife == null) return;

		spouseRecord.put(husband, wife);
		spouseRecord.put(wife, husband);
	}

	private Person findPerson(String name) {
		return people.get(name);
	}

	private Person getParent(String name) {
		if (DUMMY.equals(name)) return null;
		return findPerson(name);
	}

	private Person getGrandFather(Person person, boolean paternal) {
		Person parent = paternal
				? getParent(person.getFatherName())
				: getParent(person.getMotherName());

		if (parent == null) return null;

		return getParent(parent.getFatherName());
	}

	private List<String> filterChildren(Person parent, Predicate<Person> condition) {
		List<String> result = new ArrayList<>();
		if (parent == null) return result;

		for (Person child : parent.getChildren()) {
			if (condition.test(child)) {
				result.add(child.getName());
			}
		}
		return result;
	}

	public void getRelationship(String name, String relation) {
		Person person = findPerson(name);

		if (person == null) {
			System.out.println(Message.PERSON_NOT_FOUND);
			return;
		}

		List<String> result;

		switch (relation) {
			case "Son":
				result = getSon(person);
				break;
			case "Daughter":
				result = getDaughter(person);
				break;
			case "Siblings":
				result = getSiblings(person);
				break;
			case "Paternal-Uncle":
				result = getPaternaluncle(person);
				break;
			case "Maternal-Uncle":
				result = getMaternaluncle(person);
				break;
			case "Maternal-Aunt":
				result = getMaternalaunt(person);
				break;
			case "Paternal-Aunt":
				result = getPaternalaunt(person);
				break;
			case "Sister-In-Law":
				result = getSisterinlaw(person);
				break;
			case "Brother-In-Law":
				result = getBrotherinlaw(person);
				break;
			default:
				return;
		}

		print(result);
	}

	private void print(List<String> list) {
		if (list == null || list.isEmpty()) {
			System.out.println(Message.NONE);
			return;
		}

		System.out.println(String.join(" ", list));
	}

	public List<String> getSon(Person person) {
		return filterChildren(person,
				child -> !FEMALE.equals(child.getGender()));
	}

	public List<String> getDaughter(Person person) {
		return filterChildren(person,
				child -> !MALE.equals(child.getGender()));
	}

	public List<String> getSiblings(Person person) {
		Person father = getParent(person.getFatherName());

		return filterChildren(father,
				child -> !child.getName().equals(person.getName()));
	}

	public List<String> getPaternalaunt(Person person) {
		Person grandFather = getGrandFather(person, true);

		return filterChildren(grandFather,
				child -> FEMALE.equals(child.getGender()));
	}

	public List<String> getPaternaluncle(Person person) {
		Person grandFather = getGrandFather(person, true);

		return filterChildren(grandFather,
				child -> MALE.equals(child.getGender()) &&
						!child.getName().equals(person.getFatherName()));
	}

	public List<String> getMaternalaunt(Person person) {
		Person grandFather = getGrandFather(person, false);

		return filterChildren(grandFather,
				child -> FEMALE.equals(child.getGender()) &&
						!child.getName().equals(person.getMotherName()));
	}

	public List<String> getMaternaluncle(Person person) {
		Person grandFather = getGrandFather(person, false);

		return filterChildren(grandFather,
				child -> MALE.equals(child.getGender()));
	}

	private List<String> getInLawsFromSiblings(Person person, String gender) {
		List<String> result = new ArrayList<>();
		Person father = getParent(person.getFatherName());

		if (father == null) return result;

		for (Person sibling : father.getChildren()) {
			if (sibling == person) continue;
			if (!gender.equals(sibling.getGender())) continue;

			Person spouse = spouseRecord.get(sibling);
			if (spouse != null) {
				result.add(spouse.getName());
			}
		}

		return result;
	}

	private List<String> getInLawsFromSpouse(Person person, String gender) {
		List<String> result = new ArrayList<>();
		Person spouse = spouseRecord.get(person);

		if (spouse == null) return result;

		Person father = getParent(spouse.getFatherName());
		if (father == null) return result;

		for (Person child : father.getChildren()) {
			if (child == spouse) continue;
			if (gender.equals(child.getGender())) {
				result.add(child.getName());
			}
		}

		return result;
	}

	public List<String> getSisterinlaw(Person person) {
		List<String> result = new ArrayList<>();
		result.addAll(getInLawsFromSiblings(person, MALE));
		result.addAll(getInLawsFromSpouse(person, FEMALE));
		return result;
	}

	public List<String> getBrotherinlaw(Person person) {
		List<String> result = new ArrayList<>();
		result.addAll(getInLawsFromSiblings(person, FEMALE));
		result.addAll(getInLawsFromSpouse(person, MALE));
		return result;
	}
}