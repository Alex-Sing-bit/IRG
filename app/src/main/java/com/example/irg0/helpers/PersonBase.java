package com.example.irg0.helpers;

import java.util.Dictionary;
import java.util.TreeMap;

public class PersonBase {
    public TreeMap<Integer, Person> base = new TreeMap<>();

    public PersonBase() {

    }
    public void addToBase(Person person) {
        base.put(person.getId(), person);
    }

    public Person getPerson(Integer id) {
        return base.get(id);
    }
}
