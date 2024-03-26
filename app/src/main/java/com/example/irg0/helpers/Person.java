package com.example.irg0.helpers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Dictionary;

public class Person {

    private int id;
    private String name;
    private LocalDate birthday;
    private String info;

    public Person(int id, String name, LocalDate birthday, String info) {
        this.id = id;
        this.birthday = birthday;
        this.name = name;
        this.info = info;
        setId();
    }

    public Person(String id, String name, String birthday, String info) {
        this.id = Integer.parseInt(id);
        this.birthday = dateFromString(birthday);
        this.name = name;
        this.info = info;
    }

    private LocalDate dateFromString(String dateS) {
        String[] lines = dateS.split("/");
        int[] dateI = new int[3];
        for (int i = 0; i < 3; i++) {
            dateI[i] = Integer.parseInt(lines[i]);
        }

        return LocalDate.of(dateI[2], dateI[1], dateI[0]);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }

    private void setId() {
        String s = name + info + birthday.toString();
        this.id = s.hashCode();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public LocalDate getBirthday() {
        return birthday;
    }
}
