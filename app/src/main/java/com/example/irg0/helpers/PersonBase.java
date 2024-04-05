package com.example.irg0.helpers;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Objects;
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

    public List<Person> toList() {
        List<Person> list = new ArrayList<>();
        Integer[] keys = base.keySet().toArray(new Integer[base.size()]);
        for (Integer key : keys) {
            list.add(base.get(key));
        }

        return list;
    }

    public List<String> toStringList() {
        List<String> list = new ArrayList<>();
        Integer[] keys = base.keySet().toArray(new Integer[base.size()]);
        for (Integer key : keys) {
            Person p = base.get(key);
            String s = Objects.requireNonNull(p.getName());
            list.add("key:" + key + "\n" + s + "\n" + p.getPhoneNumber());
        }

        return list;
    }
}
