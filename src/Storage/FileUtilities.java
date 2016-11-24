/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Storage;

/**
 *
 * @author stelios
 */
import java.io.BufferedReader;
import java.io.FileReader;
import basics.City;
import basics.DirConnection;
import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Hashtable;

public class FileUtilities {
    //Static mathods for general usage...
    
    //Write cities to human readable (.txt) file
    public static void writeCitiesToFile(String fileName,boolean overwrite, ArrayList<City> cities) throws FileNotFoundException{
        PrintWriter pw=null;
        try {
            //New PrintWriter to file...
            pw = new PrintWriter(createOrRewriteFile(fileName,overwrite));
            
            //Write a city's properties in each line of the file.
            for(City i : cities){
                //TEST each written city.
                System.out.println(i);
                //Write City to file
                pw.println(i);
            }
        } catch (IOException ex) {//if there is a problem...
            System.out.println("File not written! I/O Error.");
        }catch (NullPointerException ex2){//if file is to remain unchanged...
            System.out.println("File not written! Another file with the same name exists!");
        } finally {
            pw.close();
        }
    }
    
    //Get City objects by reading a human readable (.txt) file
    public static ArrayList<City> readCitiesFromFile(String fileName) throws FileNotFoundException{
        //New buffered file reader
        BufferedReader input = new BufferedReader(new FileReader(fileName));
        String str;
        ArrayList cities =new ArrayList<City>();
        try {
            //For each line in the file...
            while((str = input.readLine()) != null){
                City tempCity;
                String[] temp = str.split("\t");
                //split it into tokens.Each one is a city's property...
                try{
                    //Coordinate type is by default: "WGS84" . Each String is converted into the corresponding type.
                    tempCity = new City(temp[0],Long.parseLong(temp[1]),Double.parseDouble(temp[2]),Double.parseDouble(temp[3]),"WGS84");
                    cities.add(tempCity);//Ceate and add a City object to the list,if written correctly in the file.
                } catch(NumberFormatException ex1){
                    System.out.println("WARNING! City not properly written! File has propably changed!");
                }
            }
            input.close();//Close input Stream.
        } catch (IOException ex) {
            System.out.println("File not read properly!");
        }
        return cities;
    }
    
    //Does what is necessary with a given file.
    private static File createOrRewriteFile(String f, boolean overwrite) throws IOException{
        File file = new File(f);//Create File Object.
        if(!file.exists()) {//If file with given name doesn't exist..
            file.createNewFile();
            return file;//create it and return it.
        }else if(overwrite){//If it exists and overwrite permission is given...
            return file;//return file to be rewritten.
        }else return null;//If it exists and overwrite permission is not given...
        //return nothing.
        
    }
    
    public static void writeConnectionsToFile(String fileName,boolean overwrite, Hashtable<String,Object> connections){
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(createOrRewriteFile(fileName,overwrite)));
            out.writeObject(connections);
            out.close();
        } catch (IOException ex) {
            System.out.println("File not written! I/O Error.");
        } catch (NullPointerException e){
            System.out.println("File not written! Another file with the same name exists!");
        }
        
    }
    
    public static Hashtable<String,Object> readConnectionsFromFile(String fileName){
        //Initailize the list of connections
        Hashtable<String,Object> connections = null;

        try {
            //Read the Connections' Container object from file.
            ObjectInputStream inFile= new ObjectInputStream(new FileInputStream(fileName));//Stream initialization
            Object o = inFile.readObject();
            connections = (Hashtable<String,Object>) o;//Cast to the right container type.
        } catch (IOException ex) {
            System.out.println("File input problem!");
        } catch (ClassNotFoundException ex) {
            System.out.println("Objects not found in the file!");
        }
        return connections;

    }
}
