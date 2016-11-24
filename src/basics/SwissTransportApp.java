/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basics;

import GUI.MainFrame;
import Storage.DBHasDataException;
import java.util.ArrayList;
import Storage.FileUtilities;
import Storage.DataBase;
import java.io.FileNotFoundException;

/**
 *
 * @author Στυλιανός
 */
public class SwissTransportApp {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //INITIATOR PROGRAM.
        
        ArrayList<City> cities = new ArrayList();//A City objects container.
        
        //Stage 1. Attempting to collect City info from Database.
        int i = 0;
        while(!DataBase.isConnected() && i < 2){//try to connect until there is a connection...
            i++;//Two tries...
            DataBase.connect();  
        }
        if(DataBase.isConnected()) {//If database connection exists...
            cities = DataBase.readCitiesFromDB();//Get the cities.
            System.out.println(cities);//Print in console...
        }
        if(!(cities.isEmpty() || cities==null)){//If Cities exist...
            //START the GUI Program, DON'T GO to NEXT stage.
            new MainFrame(cities).setVisible(true);
        //Stage 2. Collect city info from file.
        }else{//If cities don't exist...
            String fileName = "cities.txt";//Default backup file for cities.
            try {//Get cities from default file...
                cities = FileUtilities.readCitiesFromFile(fileName);
            } catch (FileNotFoundException ex){//If cities not found...
                System.out.println("Cities not found...");//TEST...
            }
            if(!(cities.isEmpty() || cities==null)){//If Cities exist...
                //START the GUI Program, DON'T GO to NEXT stage.
                new MainFrame(cities).setVisible(true);
        //Stage 3. Collect city info from the net and keep backups.
            }else{
                Country swiss = new Country();//Get cities from the net.
                new MainFrame(swiss).setVisible(true);//START the GUI Program.
                //BEGIN BACKUP PROCESS.
                //If there is a connection to Database, add cities to database.
                if(DataBase.isConnected()) try {
                    DataBase.writeCitiesToDB(swiss.getCities());
                } catch (DBHasDataException ex) {
                    ex.handle(cities, null);
                }
                try {
                    //Add cities to backup file.
                    FileUtilities.writeCitiesToFile(fileName, true, swiss.getCities());
                } catch (FileNotFoundException ex) {
                    System.out.println("Problem while writting file...");
                }
            }
        }
        //Finish the Initiator program...
        DataBase.disconnect();
        System.out.println("This is the end for main!");
    }
    
}
