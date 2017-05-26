package com.dvt.undertheweather.model;

public class SpinnerNavItem {
 
    private String title;
    private int position;
    private int id;


    public SpinnerNavItem(String title, int id){
        this.title = title;
        this.position = position;
        this.id = id;
    }
     
    public String getTitle(){
        return this.title;      
    }
      
    public int getPosition(){
        return this.position;
    }

    public int getId() {
        return id;
    }
}