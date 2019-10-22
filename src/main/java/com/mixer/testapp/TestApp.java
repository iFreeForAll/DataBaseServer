package com.mixer.testapp;

import com.mixer.dbserver.DB;
import com.mixer.dbserver.DBServer;
import com.mixer.raw.FileHandler;
import com.mixer.raw.Index;
import com.mixer.raw.Person;

public class TestApp {

    public static void main(String[] args) {
        try {
            String dbFileName = "Dbserver.db";
            DB db = new DBServer(dbFileName);
            db.add("Oleg", 24, "Usinsk", "X777OO", "Cool guy!");
            db.close();

            db = new DBServer(dbFileName);
            Person person = db.read(0);

            System.out.println("Total num of rows in DB: " + Index.getInstance().getTotalRowNumber());
            System.out.println(person);

            System.out.println(Index.getInstance().getTotalRowNumber());
            db.close();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}