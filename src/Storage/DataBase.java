/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Storage;

import basics.APIreader;
import basics.City;
import basics.DirConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author stelios
 */
public class DataBase {
    
    //2 tables: cities and direct_connections in the database. 
    public static Connection conn;//Database connection
    private static ArrayList<City> tempCities = new ArrayList<City>();
    //Temporary pointer to a City objects container.
    
    public static boolean isConnected() {
        try {//if connection not null and not closed...
            if (conn != null && !conn.isClosed()) {
                return true;//connection exists.
            } else {
                return false;
            }
        } catch (SQLException ex) {
            System.out.println("Problem with SQL connection.");
        }
        return false;
    }
    
    public static void connect() {
        try {
            //Check if driver exists.
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException ex) {
            System.out.println("Problem with SQL connection. Driver does not exist!");
            return;
        }
        //connection name
        String url = "jdbc:oracle:thin:@//10.100.51.123:1521/orcl";
        //connect.
        try {
            conn = DriverManager.getConnection(url, "it214106", "it214106");
        } catch (SQLException ex) {
            System.out.println("Problem with SQL connection. Could not connect to DB!");
        }
    }
    
    public static void disconnect() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();//If connection is open, close it.
            }
        } catch (SQLException ex) {
            System.out.println("Problem while disconnecting with DB!");
        }
    }
    
     
    public static void writeCitiesToDB(ArrayList<City> cities) throws DBHasDataException{
        try {
            if(checkForData("cities")){
                //if there are results, throw exception.
                throw new DBHasDataException("Cities");
            }
            //Write all cities at once, into cities table.
            int c =0;//initialize index counter.
            PreparedStatement pst = conn.prepareStatement("INSERT INTO cities VALUES(?,?,?,?,?)");
            for (City i : cities) {//for each city object..
                pst.setString(1, i.getName());//coloumn 1 is name,
                pst.setLong(2, i.getID());//coloumn 2 is ID,
                pst.setDouble(3, i.getXcoord());//coloumn 3 is X coordinate,
                pst.setDouble(4, i.getYcoord());//coloumn 4 is Y coordinate,
                pst.setInt(5, c);//coloumn 5 is the alphabetical index.
                pst.addBatch();//add a city row to statement.
                c++;//next index.
            }
            pst.executeBatch();//Insert all cities into DB. 
            pst.close();//close prepared statement.
            DataBase.tempCities = cities;//Have city buffer ready.
        } catch (SQLException ex) {
            System.out.println("Problem while preparing statement for DB!");
        }
 
    }
    
    public static ArrayList<City> readCitiesFromDB(){
        ArrayList<City> cities = new ArrayList();//City objects container.
        try {
            Statement st = conn.createStatement();
            ResultSet rs=st.executeQuery("SELECT * FROM cities");
            //Select all cities.
            while (rs.next()){//for each row in resultset...
                cities.add(new City(rs.getString(1),rs.getLong(2),rs.getDouble(3),rs.getDouble(4),"WGS84"));
                //create city object from the row's stored information
            }
            st.close();
        } catch (SQLException ex) {
            System.out.println("Problem while reading Cities from DB!");
        }
        DataBase.tempCities = cities;//Assign pointer to cities.
        return cities;
    }
    
    public static void writeConnectionsToDB(ArrayList<DirConnection> connections) throws DBHasDataException{
        try {
            if(checkForData("direct_connections")){//if there is data in the table...
                throw new DBHasDataException("Connections");//throw exception
            }
            //Write all connections at once, into direct_connections table.
            PreparedStatement pst = conn.prepareStatement("INSERT INTO direct_connections VALUES(?,?)");
            for (DirConnection i : connections) {//for each city object..
                pst.setLong(1, i.getID_1());//coloumn 1 is ID of start,
                pst.setLong(2, i.getID_2());//coloumn 2 is ID of destination,
                pst.addBatch();//add a connection row to statement.
            }
            pst.executeBatch();//Insert all connections into DB.
            pst.close();//close prepared statement. 
        } catch (SQLException ex) {
            System.out.println("Problem while preparing statement for DB!");
        }
    }
    
    public static ArrayList<DirConnection> readConnectionsFromDB(){
        ArrayList<DirConnection> connections = new ArrayList();//Connection Objects container.
        try {
            Statement st = conn.createStatement();
            ResultSet rs=st.executeQuery("SELECT * FROM direct_connections");
            //Select all connections
            while (rs.next()){//for each row in resultset...
                City city1 = readCityFromDB(rs.getLong(1));//get city with ID of start
                if(city1==null) {//if city is not in db, find it and add it.
                    city1 = APIreader.getInfo(rs.getLong(1));
                    DataBase.addCityToDB(city1);
                }
                City city2 = readCityFromDB(rs.getLong(2));//get city with ID of destination
                if(city2==null){//if city is not in db, find it and add it.
                    city2 = APIreader.getInfo(rs.getLong(2));
                    DataBase.addCityToDB(city2);
                }
                connections.add(new DirConnection(city1,city2));//New connection
                System.out.println(city1);
                System.out.println(city2);
            }
            st.close();
        } catch (SQLException ex) {
            System.out.println("Problem while reading Connections from DB!");
        }
        return connections;    
    }
    
    public static City readCityFromDB(String Cname){
         
        City city=null;
        try {     
            Statement st = conn.createStatement();
            ResultSet rs=st.executeQuery("SELECT * FROM cities WHERE cityname='"+Cname+"'");
            //Select all cities.
            while (rs.next()){//for each row in resultset...
                city = new City(rs.getString(1),rs.getLong(2),rs.getDouble(3),rs.getDouble(4),"WGS84");
                //create city object from the row's stored information
            }
            st.close();
        } catch (SQLException ex) {
            System.out.println("Problem while reading City "+Cname+" from DB!");
            System.out.println(ex.getMessage());
        }
           if(city ==null){
               
               return null;
           }
        return city;     
    }
    
    public static City readCityFromDB(long id){
        City city = null;
        try {     
            Statement st = conn.createStatement();
            ResultSet rs=st.executeQuery("SELECT * FROM cities WHERE CITYID="+id);
            //Select all cities.
            while (rs.next()){//for each row in resultset...
                city = new City(rs.getString(1),rs.getLong(2),rs.getDouble(3),rs.getDouble(4),"WGS84");
                //create city object from the row's stored information
            }
            st.close();
        } catch (SQLException ex) {
            System.out.println("Problem while reading City with ID "+id+" from DB!");
            System.out.println(ex.getMessage());
        }
        return city;
    }
     
    public static void deleteCities(){
        try {
            Statement st = conn.createStatement();
            String sql="DELETE FROM cities";
            st.execute(sql);
            st.close();
        } catch (SQLException ex) {
            System.out.println("Problem while deleting cities from DB!");
        } 
    }
    public static void deleteConnections(){
        try {
            Statement st = conn.createStatement();
            String sql="DELETE FROM direct_connections";
            st.execute(sql);
            st.close();
        } catch (SQLException ex) {
            System.out.println("Problem while deleting connections from DB!");
        }
    }
    
    public static void resetDatabase(){
        deleteConnections();
        deleteCities();
    }
    
    public static void addCityToDB(City city){
        try{
            int count = -1;
            Statement st = conn.createStatement();//Check if city exists.
            ResultSet rs=st.executeQuery("SELECT COUNT(*) FROM cities where CITYID="+city.getID());
            while(rs.next()){//read how much is written in DB!
                count =rs.getInt(1);
            }
            st.close();
            if (count == -1){//If something went wrong...
                System.out.println("Failed to check DataBase for existing Data!");
                return;
            }
            if(count >= 1){//If city exists, do nothing.
                System.out.println("City already exists!");
                return;
            }//otherwise add city to database...
            PreparedStatement pst = conn.prepareStatement("INSERT INTO cities VALUES(?,?,?,?,?)");
            pst.setString(1, city.getName());//coloumn 1 is name,
            pst.setLong(2, city.getID());//coloumn 2 is ID,
            pst.setDouble(3, city.getXcoord());//coloumn 3 is X coordinate,
            pst.setDouble(4, city.getYcoord());//coloumn 4 is Y coordinate,
            pst.setInt(5, 900);//coloumn 5 is the alphabetical index for preIndexed cities.For new cities it is 900.
            pst.addBatch();//add a city row to statement.
            pst.executeBatch();//Execute statement.
            pst.close();//close prepared statement.
 
        } catch(SQLException ex) {
            System.out.println("DataBase couldn't be Checked! City could not be added!");
        }

    }
    
    public static void addConnToDB(DirConnection con){
        try{
            int count = -1;
            Statement st = conn.createStatement();
            ResultSet rs=st.executeQuery("SELECT COUNT(*) FROM  direct_connections where CITYFROM="+con.getID_1()+" AND CITYTO="+con.getID_2());
            while(rs.next()){//read how much is written in DB!
                count =rs.getInt(1);
            }
            if (count == -1){//If something went wrong...
                System.out.println("Failed to check DataBase for existing Data!");
                return;
            }
            if(count >= 1){//If connection exists, do nothing.
                System.out.println("Connection already exists!");
                return;
            }//If connection doesn't exist add it to DB...
            PreparedStatement pst = conn.prepareStatement("INSERT INTO direct_connections VALUES(?,?)");
            pst.setLong(1, con.getID_1());//coloumn 1 is ID of start,
            pst.setLong(2, con.getID_2());//coloumn 2 is ID of destination,
            pst.addBatch();//add a connection row to statement.
            pst.executeBatch();//Insert connection into DB.
            pst.close();//close prepared statement.
        } catch(SQLException ex) {
            System.out.println("DataBase couldn't be Checked! Connection could not be added!");
        }

    }
    
    private static boolean checkForData(String table){
        //CHECK IF DB HAS DATA...
        try{
            int count=-1;
            Statement st = conn.createStatement();
            ResultSet rs=st.executeQuery("SELECT COUNT(*) FROM "+ table);
            while(rs.next()){//read how much is written in DB!
                count =rs.getInt(1);
            }
            st.close();
            if (count == -1){
                System.out.println("Failed to check DataBase for existing Data!");
                return true;
            }
            if(count!=0){//if there are results, return true.
                return true;
            }
        } catch(SQLException ex) {
            System.out.println("DataBase couldn't be Checked for existing Data!");
        }
        return false;
    }
    
    
}
