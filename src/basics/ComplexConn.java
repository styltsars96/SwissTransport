/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 *
 * @author Στυλιανός
 */
public class ComplexConn implements Serializable{
    private ArrayList<City> CitySeq;
    //Sequence of cities to form an indirect connection.
    
    public ComplexConn(ArrayList<City> CitySequence) {
       this.CitySeq = CitySequence;
    }
    
    //Automatically constructs a complex connection by using a sequence of IDs
    public ComplexConn(ArrayList<Long> longs, Hashtable hash){
        CitySeq = new ArrayList<City>();
        for(Long l : longs){
            City city = (City) hash.get(l);
            if (city==null){
                //If city is not found it hte preindexed cities, net is searched for city.
                CitySeq.add(APIreader.getInfo(l));
            }else{
                CitySeq.add( city );
            }
        }
    }
    
    //Returns the entire sequence in ArrayList form
    public ArrayList<City> getSequence(){
        return CitySeq;
    }
    
    /** Makes a direct connection.
     * Arguement destIndex is the destination City's index. Method returns the Direct Connection between the previous City and the on the given Index. 
     * @param destIndex
     * @return 
     */
    public DirConnection getDirConnection(int destIndex){
        if(destIndex > CitySeq.size()-1){//make sure it's never out of bounds.
            System.out.println("Index is bigger than the sequence's size");
            return null;
        }else if(destIndex<1){
            System.out.println("Index is too short!");
            return null;
        }else{//Return a Direct connection object containing two cities in the sequence.
            return new DirConnection(CitySeq.get(destIndex - 1),CitySeq.get(destIndex));
        }
    }
    
    @Override
    public String toString(){
        String ret = "Start: name: "+CitySeq.get(0).getName()+" ID: "+CitySeq.get(0).getID()+"\n";
        for(int i = 1;i<CitySeq.size()-1;i++){
            ret = ret + "Stop "+i+": name: "+(CitySeq.get(i).getName())+" ID: "+CitySeq.get(i).getID()+"\n";
        }
        ret = ret+"Destination: name:"+CitySeq.get(CitySeq.size()-1).getName()+" ID: "+CitySeq.get(CitySeq.size()-1).getID();
        return ret;
    }
    
}
