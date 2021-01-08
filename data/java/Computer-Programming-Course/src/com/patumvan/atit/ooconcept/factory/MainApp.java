package com.patumvan.atit.ooconcept.factory;

public class MainApp {

    public MainApp() {
        Factory factory = new Factory();
        Car c = factory.create("Yaris", 800000);
        System.out.println(c.name);
        System.out.println(c.price);
    }

    public static void main(String[] args) {
        MainApp mainApp = new MainApp();
    }
}
