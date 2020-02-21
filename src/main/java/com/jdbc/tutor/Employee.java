package com.jdbc.tutor;

import java.util.*;
import java.util.stream.Collectors;

public class Employee {
    private long ID;
    private String name;
    private char sex;
    private GregorianCalendar workDate;
    private String department;
    private double salary;

    public Employee() {
    }


    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public char getSex() {
        return sex;
    }

    public void setSex(char sex) {
        while (true) {
            sex = Character.toUpperCase(sex);
            if (sex != 'M' && sex != 'F') {
                System.out.println("Sex of the person should be: m(M) or f(F). Try again: ");
                sex = new Scanner(System.in).nextLine().charAt(0);
            } else {
                break;
            }
        }
        this.sex = sex;
    }

    public Calendar getWorkDate() {
        return workDate;
    }

    public void setWorkDate(String workDate) {
        String[] splitDate = workDate.split("[-|_|.|/| ]");
        if (splitDate[0].length() != 4 ||
                Integer.parseInt(splitDate[1]) < 0 && Integer.parseInt(splitDate[1]) > 12 ||
                Integer.parseInt(splitDate[2]) < 0 && Integer.parseInt(splitDate[2]) > 31) {
            throw new IllegalArgumentException("Make sure that date is in the format dddd.mm.yy");
        } else {
            int d = Integer.parseInt(splitDate[2]);
            int m = Integer.parseInt(splitDate[1]) - 1;
            int y = Integer.parseInt(splitDate[0]);
            this.workDate = new GregorianCalendar(y, m, d);
        }
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    private String calendarToString(Calendar workDate) {
        return workDate.get(Calendar.YEAR) + "." +
                (workDate.get(Calendar.MONTH) + 1) + "." +
                workDate.get(Calendar.DATE);
    }

    @Override
    public String toString() {
        return ID + "\t" + name + "\t" + sex + "\t" +
                calendarToString(workDate) +
                "\t" + department + "\t" + salary;
    }
}
