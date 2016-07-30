package pong;

import java.io.Serializable;

public class MyGameInput implements Serializable{
	static final int CONNECTING=1;
	static final int DISCONNECTING=2;
	static final int MOUSE_PRESSED=3;
	static final int MOUSE_MOVED=4;
	static final int RESET=5;
	static final int LIMIT=6;
	
    String name;
    int y_location;
    int command;
    int scorelimit;
    
    void setName(String name)
    {
    	this.name=name;
    }
    void setCmd(int command)
    {
    	this.command=command;
    }
    
    void setLocation(int location)
    {
    	y_location = location;
    	command=MOUSE_MOVED;
    }
    
    void setScoreLimit(int score)
    {
    	this.scorelimit = score;
    }
    
    MyGameInput()
    {
    	
    }
    
   
}
