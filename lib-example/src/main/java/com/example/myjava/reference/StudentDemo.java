package com.example.myjava.reference;

/**
 * 注意：hashcode()方法的重写规则
 * Created by Administrator on 2018/5/16.
 */

public class StudentDemo {

    private int age;
    private String name;

    public StudentDemo(int age, String name) {
        this.age = age;
        this.name = name;
    }

    // 重写规则
    @Override
    public int hashCode() {
        return name.hashCode() + age * 27;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof StudentDemo)) {
            return false;
        }
        StudentDemo student = (StudentDemo) o;
        return (this.age == student.age) && (this.name.equals(student.name));
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("StudentDemo{");
        sb.append("age=").append(age);
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
