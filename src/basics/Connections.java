/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basics;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;
import java.util.Iterator;

/**
 *
 * @author dimitris
 */
public class Connections {
    private Hashtable cityHolder;//Keeps city object refferences within the class ,with their corresponding keys.
    private Hashtable connHolder;//Keeps connections with start and destination city names as keys.
    APIThreadHandler api;//A tool that calls the api as many times as it is needed.
    
    //Takes a list of 'City' objects as arguement.
    public Connections(Hashtable cities){
        //Initialize
        this.connHolder = new Hashtable<String, Object>();
        //keys are like "<Start> , <Destination>", objects are either complex or direct connections.
        this.cityHolder = cities;
        this.api = new APIThreadHandler();
    }
    
    public void addConn(City from, City to){
        //Add candidate connection.
        api.addQuery(from, to);
    }
    
    public void makeConnections(){
        api.ReadyThreads();
        api.RunThreads();
        ArrayList newConn = api.getResults();
        for(Object o : newConn){
            ArrayList<Long> longs = (ArrayList<Long> ) o;
            if(longs.size()==2){//if connection is direct... (2 IDs)
                //get city objects with these IDs
                City c1 =(City) cityHolder.get(longs.get(0));
                City c2 =(City) cityHolder.get(longs.get(1));
                //make a direct connection object.
                DirConnection dc = new DirConnection(c1,c2);
                String temp = c1.getName()+" , "+c2.getName();//key
                //add it to table.
                connHolder.put(temp, dc);
            }else if(longs.size()<2){
                System.out.println("Wrong size of ID sequence...");
            }else{//if connection is not direct... (>2 IDs)
                //Get start and destination cities.
                City c1 =(City) cityHolder.get(longs.get(0));
                if(c1==null){//If city does not exist, get it...
                    c1=APIreader.getInfo(longs.get(0));
                    cityHolder.put(c1.getID(),c1);
                }
                City c2 =(City) cityHolder.get(longs.get(longs.size()-1));
                if(c2==null){//If city does not exist, get it...
                    c2=APIreader.getInfo(longs.get(longs.size()-1));
                    cityHolder.put(c2.getID(),c2);
                }
                String temp = c1.getName()+" , "+c2.getName();//key
                //make a complex connection object.
                ComplexConn newCC = new ComplexConn(longs,cityHolder);
                //add it to the table.
                connHolder.put(temp, newCC);
                //add all the direct connections recovered from the complex to the table.
                int i=1;
                DirConnection newDC;
                while(i<longs.size()){
                    newDC = newCC.getDirConnection(i);
                    temp = newDC.getName_1()+" , "+newDC.getName_2();
                    connHolder.put(temp, newDC);
                    i++;
                }
            }
        }
    }
    
    //quickly checks for connection between 2 cities.
    public Object quickCheck(City from, City to) throws NullPointerException{
        Object o;
        //if connection already exists...
        if((o = connHolder.get(from.getName()+" , "+to.getName())) != null){
            return o;//return it.
        }//else check it on the net...
        //APIreader API = new APIreader();
        if(APIreader.isDirect(from.getID(),to.getID())){//if connection is direct...
            DirConnection dc = new DirConnection(from,to);//make object.
            connHolder.put(from.getName()+" , "+to.getName() , dc);//add it to table for later use.
            return dc;//return it.
        }else{//if connection is not direct... make object...
            ComplexConn cc = new ComplexConn(APIreader.getComplexConn(from.getID(), to.getID()),cityHolder);
            connHolder.put(from.getName()+" , "+to.getName() , cc);//add it to table for later use.
            int i=1;
            ArrayList<City> sequence = cc.getSequence();
            DirConnection newDC;
            while(i<sequence.size()){
                    newDC = cc.getDirConnection(i);
                    String temp = newDC.getName_1()+" , "+newDC.getName_2();
                    connHolder.put(temp, newDC);
                    i++;
                }
            return cc;//return it.
        }
    }
    
    //used for handling connections outside the class. 
    public Hashtable getConnections(){
        //check if the list has elements.
        if(!connHolder.isEmpty()) return connHolder;
        //If they exist ,return them, else make them...
        return null;//then return them.
    }
    
    //Used for resetting connection holder object (prefferably taken from a backup file)
    public void resetConn(Hashtable<String, Object> newHash){
        this.connHolder = newHash;
    }
    
    //Used for resetting connection holder object with ArrayList.
    public void resetConn(ArrayList<DirConnection> newAr){
        for(DirConnection con : newAr){
            String temp = con.getName_1()+" , "+con.getName_2();
            this.connHolder.put(temp ,con);
        }
    }
    
    public ArrayList<DirConnection> getDirConn(){//Prefferably used for DB backup ONLY
        String str;
        ArrayList<DirConnection> dirConn = new ArrayList<DirConnection>();//Keeps only the direct connections.
        //Iterate through the hashtable and get direct connections.
        Set<String> keys = connHolder.keySet();
        Iterator<String> itr = keys.iterator();
        while (itr.hasNext()) { 
        // Getting Key
            str = itr.next();
            Object temp = connHolder.get(str);
            if(temp instanceof DirConnection){
                DirConnection  dc = (DirConnection) temp;
                dirConn.add(dc);
            }
        }
        return dirConn;
    }
}
