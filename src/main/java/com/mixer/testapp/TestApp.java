package com.mixer.testapp;

import com.mixer.raw.FileHandler;
import com.mixer.raw.Person;

public class TestApp {

    public static void main(String[] args) {
        try {
            FileHandler fh = new FileHandler("Dbserver.db");
            fh.add("Oleg", 24, "Usinsk", "X777OO", "Cool guy!");
            fh.close();

            fh = new FileHandler("Dbserver.db");
            Person person = fh.readRow(0);
            fh.close();

            System.out.println(person);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}