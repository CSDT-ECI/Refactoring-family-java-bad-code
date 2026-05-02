package com.example.geektrust.service;

import java.util.ArrayList;
import java.util.List;

public class PersonImpl implements Person {
    private String name;
    private String gender;
    private String fatherName;
    private String motherName;
    private String spouseName;
    private List<Person> children = new ArrayList<>();

    public PersonImpl() {
    }

    public PersonImpl(String name, String gender) {
        this.name = name;
        this.gender = gender;
        this.fatherName = "Dummy";
        this.motherName = "Dummy";
    }

    public PersonImpl(String name, String gender, String fatherName, String motherName) {
        this.name = name;
        this.gender = gender;
        this.fatherName = fatherName;
        this.motherName = motherName;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    @Override
    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    @Override
    public String getSpouseName() {
        return spouseName;
    }

    @Override
    public void setSpouseName(String spouseName) {
        this.spouseName = spouseName;
    }

    @Override
    public List<Person> getChildren() {
        return children;
    }

    public void setChildren(List<Person> children) {
        this.children = children;
    }

    @Override
    public boolean addChild(Person child) {
        return this.children.add(child);
    }

    @Override
    public String toString() {
        return this.name;
    }
}

