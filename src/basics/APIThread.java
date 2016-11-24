/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basics;

import java.util.ArrayList;
import org.json.simple.parser.ParseException;
/**
 *
 * @author stelios
 */
public class APIThread implements Runnable{
    //Execution essentials...
    //private final APIreader reader; //Reader class to be used.
    private final boolean CityMode; //true if it creates Cities, false if it creates connections.
    private String city;      //Name of City to be created.
    private long ID1;         //ID of Start.
    private long ID2;         //ID of Destination.
    SynchronizedBuffer buf;   //Tool for object interchange between threads.             
    //Contains Object (City or Connection) to be returned.

    //Constructor for City creator Thread
    public APIThread(SynchronizedBuffer o, String city){
        this.city = city;
        //this.reader = reader;
        this.CityMode = true;
        this.buf=o;
    }
    
    //Constructor fot Connection checker Thread
    public APIThread(SynchronizedBuffer o, long ID1, long ID2){
        //this.reader = reader;
        this.CityMode = false;
        this.ID1=ID1;
        this.ID2=ID2;
        this.buf=o;
    }
    
    private synchronized void makeCity(){
        City c;//temporary City reference holder.
        try{//makes a city object
            c =(City) APIreader.getInfo(city);
            buf.set(c);//
        //}catch(ParseException ex){
            //System.out.println("City could not be made!");
        } catch (InterruptedException ex) {
            System.out.println("City not given to buffer!");
        }
        
    }
    
    //Checks if there is a direct connection between given cities.
    private synchronized void checkConnection(){
        ArrayList<Long> longs = new ArrayList<Long>();//temporary complex connection object
        try{
            if(APIreader.isDirect(ID1, ID2)){//If connection is direct...
                //add the two cities to the sequence and return them.
                longs.add(ID1);
                longs.add(ID2);
            }else{
                //otherwise get the full sequence of cities.
                longs = APIreader.getComplexConn(ID1, ID2);
            }
            buf.set(longs);//Add it to the buffer to be returned.      
        } catch (InterruptedException ex) {
            System.out.println("City not given to buffer!");
        }
        
    }
    
    //Code to be run in the thread. 
    public void run(){
        //Discriminates City from Connection Creation
        if(CityMode){
            makeCity();
        }else{
            checkConnection();
        }
    }
}
