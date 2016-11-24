/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Storage;

import basics.City;
import basics.DirConnection;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author stelios
 */
public class DBHasDataException extends Exception{
    private String type;//type depends on the table
    public DBHasDataException(String type){
        this.type = type;
    }
    
    public void handle(ArrayList<City> cities, ArrayList<DirConnection> con){
        System.out.println( type +" have already been written in DataBase");
        System.out.println("Do you want to overwritee the table?\n Yes for YES\n Press enter or write anything else for NO:");
        Scanner answer = new Scanner(System.in);
        String an = answer.next();//Await for correct answer!
        if( "YES".equals(an) || "Yes".equals(an) || "yes".equals(an)){//If prompted to rewrite db...
            if(type == "Cities"){//if cities is the table...
                DataBase.deleteCities();//Delete talbe
                try {
                    DataBase.writeCitiesToDB(cities);//Write table again.
               } catch (DBHasDataException ex) {
                    ex.handle(cities, con);
                }
            }else if(type == "Connections"){//if direct_connections is the table
                DataBase.deleteConnections();//Delete table
                try {
                    DataBase.writeConnectionsToDB(con);//Write table again.
                } catch (DBHasDataException ex) {
                    ex.handle(cities, con);
                }
            }
            return;
        }else{
            System.out.println(type+" remain as they are!");
            return;
        }
        
    }

    
}
