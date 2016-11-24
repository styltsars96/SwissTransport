/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basics;

/**
 *
 * @author Στυλιανός
 */
public class SynchronizedBuffer{
    //Tool for synchronising access to resources of various types.
    private Object shared;//Shared Resource
    private boolean occupied = false;//Indicates if resource is occupied...
    //when empty, it is not occupied.
    
    public synchronized void set(Object value) throws InterruptedException{
        //Used for changing or assigning the resource...
        while(occupied) wait(); //waits if the resource is occupied.
        //At first write the getter is waiting until value is assigned.(See get() below)
        shared = value; //assigns the object.
        occupied = true;//buffer is occupied...
        //resource can't be reset before it is repossessed.
        notifyAll();//Makes the awating thread runnable again.
        
    }
    
    public synchronized Object get() throws InterruptedException{
        //Used for getting a refference to the resource...
        while(!occupied) wait();//Waits untill value is set.
        occupied = false;//Indicates that resource can be reset, now that it is repossessed.
        notifyAll();//Makes the awating thread runnable again.
        return shared;//return the resourse!
    }
    
    public synchronized void EmergencyClose() throws InterruptedException{
        shared = null;
        occupied = true;
        notifyAll();
    }
}
