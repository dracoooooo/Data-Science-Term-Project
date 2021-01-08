package com.patumvan.atit.ooconcept.factory;

public class Factory {
    public  Car create(String name, int price) {
        Car car = new Car();
        car.name = name;
        car.price = price;
        return car;
    }
}
