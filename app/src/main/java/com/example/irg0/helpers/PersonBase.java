package com.example.irg0.helpers;

import java.util.Dictionary;
import java.util.TreeMap;

public class PersonBase {
    public TreeMap<Integer, Person> base = new TreeMap<>();

    public PersonBase() {

    }
    public int addToBase(Person person) {
        int id = person.getId();
        base.put(id, person);
        return id;
    }

    public Person getPerson(Integer id) {
        return base.get(id);
    }
}
