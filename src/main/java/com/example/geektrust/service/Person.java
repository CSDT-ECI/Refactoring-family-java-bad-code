package com.example.geektrust.service;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Interfaz Person: define el contrato de una persona en la familia.
 * Las implementaciones concretas (por ejemplo PersonImpl) proveerán
 * la lógica y almacenamiento.
 */
public interface Person {
	String getName();
	void setName(String name);
	String getGender();
	void setGender(String gender);
	String getFatherName();
	void setFatherName(String fatherName);
	String getMotherName();
	void setMotherName(String motherName);
	String getSpouseName();
	void setSpouseName(String spouseName);
	List<Person> getChildren();
	boolean addChild(Person child);
}
