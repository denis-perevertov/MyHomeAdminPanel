package com.example.myhome.home.model;

//приход/расход
public enum IncomeExpenseType {
    INCOME("Приход"), EXPENSE("Расход");

    private final String name;

    IncomeExpenseType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
