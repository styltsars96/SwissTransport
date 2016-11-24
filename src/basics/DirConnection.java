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

public class DirConnection implements Serializable{
    private City From;//City 1
    private City To;  //City 2
    
    public DirConnection( City from , City to ){
        this.From = from;
        this.To = to;
    }
    
    //GETTERS
    public City getCity1(){
        return From;
    }
    public City getCity2(){
        return To;
    }
    public String getName_1(){
        return From.getName();
    }
    public String getName_2(){
        return To.getName();
    }
    public long getID_1(){
        return From.getID();
    }
    public long getID_2(){
        return To.getID();
    }
    
    //make connection a String.
    @Override
    public String toString(){
        return "CityFrom: "+getName_1()+"  FromID: "+getID_1()+"\tCityTo: "+getName_2()+"  ToID: "+getID_2();
    }
    
}
