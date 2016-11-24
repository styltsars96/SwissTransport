/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basics;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Objects;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Στυλιανός
 */
public class APIreader {
    
    private static final JSONParser  parser = new JSONParser();
    
    private static synchronized String Connect(String theUrl){
        String page = null;//Page to be returned.
        int i =5;
        while(i>0){//In case of a connection delay, retry.
            try{
                //INTERNET CONNECTION
                URL url = new URL(theUrl);
                URLConnection urlConnection = url.openConnection();
                //Entire page put to Buffered Reader, through input stream.
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                //Everything is returned in a line.
                page = bufferedReader.readLine();
                bufferedReader.close();
                //Close buffer
                break;
            }catch(Exception ex){
                System.out.println("Problem with connection");
                i--;
                
            }
        }
        
        return page;
    }
    
    //Takes city name and retuns a city object, filled with id and coordinates.
    public static synchronized City getInfo(String input){
        String result = Connect("http://transport.opendata.ch/v1/locations?query="+input);
        //page downloded from the API.'result' to be used for other purposes.
        //initialization of coordinates.
        String name = null;
        double x;
        x =  0.000000;
        double y = 0.000000;
        long id=0 ;
        //The variable 'result' is a temp.
        try{//Get the stations array from the initial object.
            Object obj = parser.parse(result);
            JSONObject o1 = (JSONObject) obj;
            result = o1.get("stations").toString();
        }catch(ParseException e1){
            System.out.println("Problem at Parsing initial object");
        }
        try{//From stations array,get the first element.
            Object obj = parser.parse(result);
            JSONArray a1 = (JSONArray) obj;
            result = a1.get(0).toString();
        }catch(ParseException e2){
            System.out.println("Problem at Parsing 'stations' array");
        }
        try{//From the first element get 'id' number and 'coordinate' object.
            Object obj = parser.parse(result);
            JSONObject o1 = (JSONObject) obj;
            name = o1.get("name").toString();
            id = Long.parseLong(o1.get("id").toString());
            result = o1.get("coordinate").toString();
        }catch(ParseException e3){
            System.out.println("Problem at Parsing the first station.");
        }catch(NumberFormatException e31){
            System.out.println("Problem with number format.");
        }
        try{//From 'coordinate' object, get 'type' , 'x' and 'y'. 
            Object obj = parser.parse(result);
            JSONObject o1 = (JSONObject) obj;
            //'result' is the 'type' String.
            result = o1.get("type").toString();
            x=Double.parseDouble(o1.get("x").toString());
            y = Double.parseDouble(o1.get("y").toString());
            
        }catch(ParseException e4 ){
            System.out.println("Problem at Parsing 'coordinate'");
        }catch(NumberFormatException e5){
            System.out.println("Problem with number format of x or y 'coordinate'(s)");
        }
        //This the City constructor.Gets paramenters name, id, x, y, type. 
        City city = new City( name, id, x,y,result );
        return city;
    }
    
    public static synchronized City getInfo(long ID){
        String input = Objects.toString(ID);
        return getInfo(input);
    }
    
    //Checks if the connection is direct.Returns 'true' if direct, 'false' if not.
    //Gets 'id's from both cities as arguements.
    public static synchronized boolean isDirect(long ID_1 ,long ID_2){
        String result = Connect("http://transport.opendata.ch/v1/connections?from="+ID_1+"&to="+ID_2+"&direct=1");
        //page downloded from the API.'result' to be used for other purposes.
        try{//From initial object get 'connections' array.
            Object obj = parser.parse(result);
            JSONObject o1 = (JSONObject) obj;
            result = o1.get("connections").toString();
        }catch(ParseException ex){
            System.out.println("Problem at Parsing initial object.");
        }
        try{//Get every object from 'connections' Array.
            Object obj = parser.parse(result);
            JSONArray a1 = (JSONArray) obj;
            //For each possible connection...
            for(int i=0;i<a1.size();i++){
                String temp = a1.get(i).toString();
                try {//get the 'sections' array...
                    Object o = parser.parse(temp);
                    JSONObject o1 = (JSONObject) o;
                    temp = o1.get("sections").toString();
                    
                } catch (ParseException ex) {
                    System.out.println("Problem at parsing element "+i+" of 'connections'");
                }
                try {//and get the size of the 'sections' Array.
                    Object o = parser.parse(temp);
                    JSONArray a2 = (JSONArray) o;
                    //If there is at least one possible connection with only one section...
                    if(a2.size()==1) return true;
                    //Connection is direct.   
                } catch (ParseException ex) {
                    System.out.println("Problem at parsing 'sections' of a connection");
                }   
            }
        }catch(ParseException ex){
            System.out.println("Problem at parsing the 'connections' Array");
        }
        //If no possible connection has just one section, it's not direct. 
        return false;
    }
    
    //Gets city sequnece (as IDs) that can form a complex connection for two cities.
    //Best used after isDirect returns false.
    public static synchronized ArrayList<Long> getComplexConn(long ID_1 ,long ID_2) throws NullPointerException{
        String result = Connect("http://transport.opendata.ch/v1/connections?from="+ID_1+"&to="+ID_2+"&direct=0");
        //page downloded from the API.'result' to be used for other purposes.
        ArrayList<Long> ret= new ArrayList<Long>();
        long tempID1 = 0;
        long tempID2 = 0;
        try{//From initial object get 'connections' array.
            Object obj = parser.parse(result);
            JSONObject o1 = (JSONObject) obj;
            result = o1.get("connections").toString();
        }catch(ParseException ex){
            System.out.println("Problem at Parsing initial object.");
        }
        int index=0;//Initialized at beginning of 'connections' array.
        try{//Get every object from 'connections' Array.
            Object obj = parser.parse(result);
            JSONArray a1 = (JSONArray) obj;
            //For each possible connection...
            int shortest=100;//Initialized to be really big.
            for(int i=0;i<a1.size();i++){
                String temp = a1.get(i).toString();
                try {//get the 'sections' array...
                    Object o = parser.parse(temp);
                    JSONObject o1 = (JSONObject) o;
                    temp = o1.get("sections").toString();
                    
                } catch (ParseException ex) {
                    System.out.println("Problem at parsing element "+i+" of 'connections'");
                }
                try {//and get the size of the 'sections' Array.
                    Object o = parser.parse(temp);
                    JSONArray a2 = (JSONArray) o;
                    //Check which is the shortest connection and store its index in the array.
                    if(a2.size()<shortest){//If connection is shorter than the previously shortest...
                        shortest = a2.size();//set its length as shortest,
                        index = i;//Keep the current index.
                    }
                } catch (ParseException ex) {
                    System.out.println("Problem at parsing 'sections' of a connection");
                }
            }//end of loop.
            result = a1.get(index).toString();
            try{//Get the sections of the shortest connection.
                Object o = parser.parse(result);
                JSONObject o1 = (JSONObject) o;
                result = o1.get("sections").toString();
            }catch(ParseException ex){
                System.out.println("Problem at Parsing sections of the shortest connection.");
            }
            String dest = "";
            try{//Get the sections of the shortest connection.
                Object o = parser.parse(result);
                JSONArray ar = (JSONArray) o;
                for(int i = 0;i<ar.size();i++){//For each section..
                    String temp = ar.get(i).toString();
                    try{//get the journey object...
                        //Use of safeguards against empty journey objects...
                        Object ob = parser.parse(temp);
                        JSONObject o1 = (JSONObject) ob;
                        result = o1.get("journey").toString();
                        if(result==null){
                            continue;
                        }
                    }catch(ParseException ex){
                        System.out.println("Problem at parsing 'journey' of a section");
                    }catch(NullPointerException ex){
                            continue;
                    }
                    try{//get the passlist from journey...
                        Object ob = parser.parse(result);
                        JSONObject o1 = (JSONObject) ob;
                        result = o1.get("passList").toString();
                        if(result==null){
                            continue;
                        }  
                    }catch(ParseException ex){
                        System.out.println("Problem at parsing 'passList' of a journey");
                    }catch(NullPointerException ex){
                        continue;
                    }
                    try{//get the beginning and end of a journey (through the passList)
                        Object ob = parser.parse(result);
                        JSONArray o1 = (JSONArray) ob;
                        result = o1.get(0).toString();
                        temp = o1.get(o1.size() - 1).toString();
                    }catch(ParseException ex){
                        System.out.println("Problem at parsing stations of a passlist");
                    }
                    try{//get station objects
                        Object ob1 = parser.parse(result);
                        Object ob2 = parser.parse(temp);
                        JSONObject o1 = (JSONObject) ob1;
                        JSONObject o2 = (JSONObject) ob2;
                        result = o1.get("station").toString();
                        temp = o2.get("station").toString();
                    }catch(ParseException ex){
                        System.out.println("Problem at final parsing of Stations");
                    }
                    try{//get ids of stations
                        Object ob1 = parser.parse(result);
                        Object ob2 = parser.parse(temp);
                        JSONObject o1 = (JSONObject) ob1;
                        JSONObject o2 = (JSONObject) ob2;
                        tempID1 = Long.parseLong(o1.get("id").toString());
                        tempID2 = Long.parseLong(o2.get("id").toString());
                    }catch(ParseException ex){
                        System.out.println("Problem at final parsing of Stations");
                    }
                    
                    //Add IDs into the sequence...
                    Long l = new Long(tempID1);
                    if(ret.size()==0){
                        //If the arrayList is empty (which means the iterator is still zero)...
                        ret.add(l);//add it into the squence.
                    }
                    if(ret.get(ret.size()-1).doubleValue() != l.doubleValue()){
                        //if the city that is the beginning of a journey, is not in the sequence...
                        ret.add(l);//add it into the squence.
                    }
                    ret.add(new Long(tempID2));//Add city ,that is in the end of the journey, into the sequence.
                }//end of loop.
            }catch(ParseException ex){
                System.out.println("Problem at Parsing sections of the shortest connection.");
            }
            
        }catch(ParseException ex){
            System.out.println("Problem at parsing the 'connections' Array");
        }
        return ret;//Return sequence.
    }
}
