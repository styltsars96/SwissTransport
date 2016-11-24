/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basics;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Στυλιανός
 */
public class APIThreadHandler {
   
    private ArrayList a;//Container for Cities or Connections.
    private ArrayList given;//Container for data needed for operation.
    private boolean cityMode;//True if it is to make cities, false if Connections.
    private ArrayList<Runnable> Threads;//Container for Threads to be executed.
    private ArrayList<SynchronizedBuffer> smallbuf;//Container of small buffers.
    private boolean Status;//true only if threads are ready.
    //private APIreader reader;//Tool that calls the API.
    ExecutorService ex;//Tool for running multiple threads.
    
    //Gets an ArrayList Created by the City container.
    //This is meant to be used for initial download of city info.
    public APIThreadHandler(ArrayList given){
        a = new ArrayList();
        this.given = given;
        this.cityMode = true;
        Threads = new ArrayList<Runnable>();
        smallbuf = new ArrayList<SynchronizedBuffer>();
        Status = false;
        //reader = new APIreader();
        
        //Gets a fixed thread pool ready...(OLD)
        //ex = Executors.newFixedThreadPool(THREADS);
        
        //Get a dynamic threadpool ready if cities are to be created...
        ex = Executors.newCachedThreadPool();
    }
    
    //Prepares the handler for introduction of cities to form a connection.
    //This is meant for download of City connections on demand.
    public APIThreadHandler(){
        a = new ArrayList();
        this.given = new ArrayList();
        this.cityMode = false;
        Threads = new ArrayList<Runnable>();
        smallbuf = new ArrayList<SynchronizedBuffer>();
        Status = false;
        //reader = new APIreader();
        //Get a dynamic threadpool ready if cities are to be created...
        ex = Executors.newCachedThreadPool();
    }
    
    private void ReadyCityThreads(){
        //For each given City name
        for(int i=0;i<given.size();i++){
            SynchronizedBuffer sb = new SynchronizedBuffer();
            smallbuf.add(sb);//Create a new buffer and connect it to the index.
            Threads.add(new APIThread(smallbuf.get(i),(String) given.get(i)));
            //add a thread to the execution list.
            //Each adds the result to the corresponiding element of a.
        }
    }
    
    public void addQuery(City from, City to){
        //Add query...: Get the shortest connection between these cities.
        String temp = from.getID()+" "+to.getID();
        this.given.add(temp);
    }
    
    private void ReadyConnThreads(){
        if(given.isEmpty()){//Check if connection queries are added.
            System.out.println("No queries queued...");
            return;
        }
        //For every possible combination of Cities...
        for(int i=0; i< given.size();i++){
            String str =(String) given.get(i);
            String[] temp = str.split(" ");
            long c1 = Long.parseLong(temp[0]);//Start
            long c2 = Long.parseLong(temp[1]);//Destination
            SynchronizedBuffer sb = new SynchronizedBuffer();
            smallbuf.add(sb);//Create a new buffer and connect it to the index.
            Threads.add(new APIThread(smallbuf.get(i),c1,c2));
            //add a thread to the execution list.
            //Each adds the result to the corresponding element of a.
        }
        
             
    }
    
    
    public void ReadyThreads(){
        if(cityMode){
            ReadyCityThreads();
        }else{
            ReadyConnThreads();
        }
        Status = true;
        System.out.println("Threads Ready!");//TEST
        given.clear();//cleanup given data.
    }
    
    public void RunThreads(){
        if(!Status){
            //Prevent misplaced call.
            System.out.println("Threads not ready for execution");
            return;
        }
        System.out.println("Starting Execution!");//TEST
        try{
        //'i' is the amount of threads already executed...
        for(int i = 0 ; i < Threads.size() ; i++){
            ex.execute(Threads.get(i));
            //Set every thread to runnable state
        }
        //Executor executes threads as many as 'THREADS' at a time...
        //and then it shuts down.
        ex.shutdown();
        //Wait for all to finish and shut down...
        ex.awaitTermination(1, TimeUnit.DAYS);
        for(SynchronizedBuffer buf : smallbuf){
            a.add(buf.get());//Collect all results from buffers.
        }
        System.out.println("Finished Execution!");//TEST
        } catch(InterruptedException e){
            System.out.println("Interrupeded... Problem while main slept...");
        }
        //cleanup
        Threads.clear();
        smallbuf.clear();
    }
    
    //Get results of the threads' execution.
    public ArrayList getResults(){
        return a;
    }
}
