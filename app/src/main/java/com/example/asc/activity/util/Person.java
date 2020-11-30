package com.example.asc.activity.util;

public class Person {

    private int id;
    private String name;
    private String phone;
    private int type;

    public final static int CONTACT_PRIVATE=1;
    public final static int CONTACT_PUBLIC=2;

    public Person(int id, String name,String phone,int type){
        this.id=id;
        this.name=name;
        this.phone=phone;
        this.type=type;
    }

    public int getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public String getPhone(){
        return this.phone;
    }

    public int getType(){
        return this.type;
    }

    public void setId(int id){
        this.id=id;
    }

    public void setName(String name){
        this.name=name;
    }

    public void setPhone(String phone){
        this.phone=phone;
    }

    public void setType(int type){
        this.type=type;
    }
}
