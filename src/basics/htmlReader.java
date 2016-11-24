/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basics;


import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 * @author Στυλιανός
 */
public class htmlReader {
    private Document doc;
    private boolean status;
    
    public htmlReader(){
        status = false;        
    }
    
    private boolean connect(){
        status = true;
        try{
            doc= Jsoup.connect("https://en.wikipedia.org/wiki/List_of_cities_in_Switzerland").userAgent("Mozilla").get();
        }catch(Exception ex){
            System.out.println("No connection!!!");
            status = false;
        }
        return status;
    }
    
    private ArrayList getCities(){
        if(status == true){
            ArrayList cities = new ArrayList<String>();
            Element table = doc.select("div#content").select("div#bodyContent").select("div#mw-content-text").select("table").get(1);
            int i=0;
            for (Element link : table.select("td")){
                if (i==0){
                    String string = link.select("a").text().toString();
                    //For some reason the char '-' is not allowed.
                    string=string.replace('-',' ');
                    cities.add(string);
                    
                }
               
                i++;
                if(i==6){
                    i=0;
                }
            }
            return cities;
        }
        return null;
    }
    
    public ArrayList getCityList(){
        this.connect();
        return getCities();
    }

}



