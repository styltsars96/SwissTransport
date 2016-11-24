/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basics;
//225 cities total
import java.util.ArrayList;
import java.util.Hashtable;

/**
 *
 * @author dimitris
 */
public class Country {
    
    private ArrayList<City> swiss;//List of city objects.
    private Hashtable hash;//Alternative hash container for city objects.
    private Hashtable nameHash;//Alternative hash container for city objects.
    private ArrayList nameList;//List of city names.
    private APIThreadHandler api;//A tool that calls the api as many times as it is needed.
    private boolean status;//True if city creation successful.
    
    public Country(){//Default Constructor for initialization and downloading.
        //initialize...
        swiss = new ArrayList<City>();
        hash = new Hashtable();
        nameHash = new Hashtable();
        status = false;//Cities not filled yet...
        makeSwiss();//Create swiss from scratch.
    }
    
    public Country(ArrayList<City> cities){//Constructor for offline initialization.
                //initialize...
        swiss = new ArrayList<City>();
        hash = new Hashtable();
        nameHash = new Hashtable();
        status = false;//Cities not filled yet...
        setSwiss(cities);//Create swiss using given data.
    }
    
    public boolean makeSwiss(){//Download City objects(again).
        //Get a list of names.
        htmlReader html = new htmlReader();
        nameList = html.getCityList();
        //Tool for filling city info fast...
        api = new APIThreadHandler(nameList);
        api.ReadyThreads();
        //System.out.println("Threads Ready!");//TEST
        api.RunThreads();
        //System.out.println("Threads Finished!");//TEST
        ArrayList temp = api.getResults();
        //TEST how many cities there are
        if(temp.size()==225){
            status = true;
            if(!swiss.isEmpty()) swiss.clear();
            //If list is not empty, make it empty.
            //Then add all downloaded cities to the list.
            for(Object o : temp){
                City a = (City) o;
                swiss.add(a);
            }
            if(!hash.isEmpty()) hash.clear();//Clears hashtable , if it is not empty.
            for(City a : swiss){
                //add all cities to hashtable with IDs and names as keys respectively!
                hash.put(a.getID(), a);
                nameHash.put(a.getName(), a);
            }
        }else{
            System.out.println("Something's up with the number of cities...");
        }
        
        //If city creation sucessful, return true.
        return status;
        
    }
    
    //Set the cities ,preferably when taken from an external source (like file or DB)
    public boolean setSwiss(ArrayList<City> cities){
        
        status = true;
        if(!hash.isEmpty()) hash.clear();//Clears hashtable , if it is not empty.
        swiss = cities;
        for(City a : swiss){
            //add all cities to hashtable with IDs and names as keys respectively!
            hash.put(a.getID(), a);
            nameHash.put(a.getName(), a);
        }
        //If city creation sucessful, return true.
        return status;
    }
    
    public void addCity(City city){
        //if city already exists,do nothing.
        if(hash.get(city.getID())!= null ) return;
        //Add city to containers.
        swiss.add(city);
        hash.put(city.getID(), city );
        nameHash.put(city.getName(),city);
    }
    
    public Hashtable getCityHash(){
        //If city details are downloaded successfully...
        if(status == true) return hash;//return a hash of city objects.
        return null;
    }
    
    public ArrayList<City> getCities(){
        //If city details are downloaded successfully...
        if(status == true) return swiss;//return the list of city objects.
        return null;
    }
    
    public Hashtable getNameHash(){
        //If city details are downloaded successfully...
        if(status == true) return nameHash;//return a hash of cities, with their names as keys.
        return null;
    }
    
    public ArrayList getNames(){
        //get names of cities,if they exist in ther field.
        if(nameList != null) return nameList;
        //if not collect them, store them and return them.
        nameList = new ArrayList();
        for(City i: swiss){
            nameList.add(i.getName());
        }
        return nameList;
    }
    
    public boolean getStatus(){
        return status;
    }
    
}
