package com.example.irg0.helpers;

import java.time.LocalDate;

public class Person {

    public static final String phoneNumberPattern =
            "^\\+7-[0-9]{3}-[0-9]{3}-[0-9]{2}-[0-9]{2}$";
    private int id = -1;
    private String name = null;

    private String phoneNumber = null;
    private LocalDate birthday = null;
    private String info = " ";

    public Person() {
    }

    public Person(String number, String name, String birthday, String info) {
        setId(number);
        this.birthday = dateFromString(birthday);
        setPhoneNumber(number);
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

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public LocalDate getBirthday() {
        return birthday;
    }

    public void setId(String number) {
        if (isPhoneNumber(number)) {
            this.id = makeId(number);
        }
    }

    public static int makeId(String s) {
        return s.hashCode();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setBirthday(String birthday) {
        try {
            this.birthday = dateFromString(birthday);
        } catch (Exception ignored) {

        }

    }

    public void setPhoneNumber(String phoneNumber) {
        if (isPhoneNumber(phoneNumber)) {
            this.phoneNumber = phoneNumber;
        }
    }

    public static boolean isPhoneNumber(String barcode) {
        if (barcode == null) {
            return false;
        }
        return barcode.matches(phoneNumberPattern);
    }
}
