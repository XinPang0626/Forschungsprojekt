package com.forschung.projektdij.routeplanner;

public class Testingjs {
    protected static String TestName = "success";
    protected String Name;

    Testingjs(String ename) {
        Name = ename;
    }

    public static String getcompanyName() {
        return TestName;
    }

    public String getempName() {
        return Name;
    }

    public int calculate(int a, int b) {
        int result = a + b;
        return result;
    }

}
