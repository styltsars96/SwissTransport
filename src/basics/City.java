/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basics;

import java.io.Serializable;

/**
 *
 * @author Στυλιανός
 */
public class City implements Serializable{
    private final String name;//from query
    private final long id;
    private final String type;
    private final double x_coordinate;
    private final double y_coordinate;
    
    public City(String name,long id, double x,double y, String type){
        this.name = name;
        this.id = id;
        this.x_coordinate = x;
        this.y_coordinate = y;
        this.type = type;
    }
    
    //GETTERS
    public String getName(){
        return this.name;
    }
    
    public long getID(){
        return this.id;
    }
    
    public double getXcoord(){
        return this.x_coordinate;
    }
    
    public double getYcoord(){
        return this.y_coordinate;
    }
    
    public String getType(){
        return this.type;
    }
    
    //City converted to String.
    @Override
    public String toString(){
        return name+"\t"+Long.toString(id)+"\t"+Double.toString(x_coordinate)+"\t"+Double.toString(y_coordinate);
    }
}
