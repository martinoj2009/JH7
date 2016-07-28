package pong;

import gameNet.GameControl;
import gameNet.GameNet_CoreGame;

import java.io.Serializable;
import java.util.ArrayList;

 
public class MyGame extends GameNet_CoreGame implements Runnable, Serializable {
   
    
    private ArrayList<String> clients = new ArrayList<String>();
    public Box box = new Box();
    
    transient GameControl gameControl;
    
    public void startGame(GameControl g)
    {
        gameControl =g;
        Thread t = new Thread(this);
        t.start();
    }
    
    public int getMyIndex(String name)
    {
    	return clients.indexOf(name);
    }
    
    @Override
    public Object process(Object ob) {
        MyGameInput myGameInput = (MyGameInput)ob;
        if (myGameInput.command == MyGameInput.CONNECTING && clients.size() < 2)
        {
        	clients.add(myGameInput.name);
        }
        
        switch(myGameInput.command)
        {
        case MyGameInput.CONNECTING:
        	break;
        case MyGameInput.DISCONNECTING:
        	clients.remove(myGameInput.name);
        	break;
        case MyGameInput.MOUSE_PRESSED:
        	 box.setGame(true);
        	break;
        case MyGameInput.MOUSE_MOVED: 
            int clientIndex = getMyIndex(myGameInput.name);
            if (clientIndex >= 0)
            	box.setPaddleY(myGameInput.y_location, clientIndex);
        	break;
        }
        MyGameOutput myGameOutput = new MyGameOutput(this);
        return myGameOutput;
    }
    @Override
    public void run() {
        while(true)
        {
            try{
                Thread.sleep(60);
                if (box.isRunning())
                {
                	box.update();  
                	MyGameOutput mo = new MyGameOutput(this);
    	            gameControl.putMsgs(mo);
                }
            }
            catch (InterruptedException e){}
        }
        
    }
}
