package gameNet;


import java.util.Scanner;

import javax.swing.JOptionPane;




/**
 The normal class that gets the show on the road looks like the following:
 
 <pre>
 
 public class MyMain extends GameCreator{   
 
  public GameNet_CoreGame createGame()
  {
	  return new MyGame();
  }
  


  public static void main(String[] args) throws IOException 
  {   
  	MyMain myMain = new MyMain();
  	GameNet_UserInterface myUserInterface = new MyUserInterface();
    
  	myMain.enterGame( myUserInterface); 
  }// end of main
}// end of class
</pre>

<h3>Things to note:</h3>
<ul>
<li><b>GameCreator</b> is an abstract method and extending this class requires us to have the method <b>createGame</b></li>
<li>GameCreator contains the <b/>enterGame</b> which is responsible for asking the questions:
<pre>
Enter your name:
Server side of game?(y/n)
etc. 
</pre>
</li>
</ul>


 */

public abstract class GameCreator {
	
	//Max score of the game
	public static int scoreLimit; 
	/**
	 * createGame must construct and return an instance of your game.  
	 * 
	 @return GameNetCoreGame - your game must extend the GameNetCoreGame class
	 */
	public abstract GameNet_CoreGame createGame();
	
	/**
	 * <b>enterGame</b> is responsible for all of the hard work of getting the networked game up and running.
	 * 
	 @param yourUserInterface - You need to pass in an instance of your user interface.  This class 
	 * must implement the GameNet_UserInterface interface.  
	 * 
	 * <h3>Here is the sequence that happens in enterGame to start up the whole game:</h3>
	 * <ol>
	<li>Your MyMain constructs an instance of your user interface code and passes it into the enterGame Method.</li>
	<li>The enterGame method then asks the questions you have all seen that are listed above.</li>
	<li>After the questions have been asked the GameCreator knows enough to put the pieces together as follows:</li>
	<li>A GameControl class is constructed in one of 2 flavors.  If we are on the Server, 
	then the GameControl class will create a Server task and do all of the associated Server jobs. </li>
	<li>If our GameControl contains the Server task, then it will also call the  method createGame  to 
	obtain  an instance of your game.  </li> 
	<li>If we are not a Server, then the GameControl merely records the IP address and the port number so that
	we connect to the real Server as a client using sockets.</li>
	<li>At this point the enterGame constructs the <b>GamePlayer</b> class that is the glue(threads, sockets, etc.)  
	that connects your user interface to it's mygame.</li>
	<li>The GamePlayer will maintain the Player's name, his GameControl object and the user interface 
	object passed into the enterGame method.</li>
	<li>The last thing done by enterGame is to call the <b>startUserInterface</b> method in 
	your user interface to pass in a reference to your GamePlayer object and to let you know
	that everything is now connected. </li>
	
	
	</ol>

	 */
	public void enterGame(GameNet_UserInterface yourUserInterface)
	{
		String playerName = null;
		GamePlayer gamePlayer; 
		GameControl gameControl = new GameControl(this);
		
		Scanner keyboard = new Scanner(System.in);
		
		
		//Ask for server side or client
		// prompt the user to enter their name
		playerName = JOptionPane.showInputDialog(null, "What's your name?", "Player name", JOptionPane.DEFAULT_OPTION);
		
		if(playerName == null)
		{
			playerName = "Player" + 100 + (int)(Math.random() * 800000); 
		}
		
		String[] answer = { "yes", "no"};
		
		//Ask for server or client
		String str = (String) JOptionPane.showInputDialog(null, 
		        "Are you the server?",
		        "Server or client",
		        JOptionPane.QUESTION_MESSAGE, 
		        null, 
		        answer, 
		        answer[0]);
		
		String[] limit = { "5", "10", "15", "20"};
		
		scoreLimit = Integer.parseInt((String)JOptionPane.showInputDialog(null, 
		        "Are you the server?",
		        "Server or client",
		        JOptionPane.QUESTION_MESSAGE, 
		        null, 
		        limit, 
		        limit[0]));
		
		if(str == null)
		{
			//The user didn't provide input so assume client
			str = "y";
		}
		
	    //Test the server/client selection
	    if (str.charAt(0) == 'y')
	    {
	    	gameControl.startServer(); // Start a Server GameControl
	    }
	    else
	    {
	    	String ipaddr =  JOptionPane.showInputDialog(null, "Enter the IP Address?", "Connect to server", JOptionPane.DEFAULT_OPTION);
	    	int port;
	    	port = Integer.parseInt(JOptionPane.showInputDialog(null, "What is the port", "Server port", JOptionPane.DEFAULT_OPTION));
	    	gameControl.connect_to_server(ipaddr,port);
	    }
	    
	 // Connect ourselves to the GameControl
	    
	    
	    gamePlayer = new GamePlayer(playerName, gameControl, yourUserInterface);
	  
	    yourUserInterface.startUserInterface (gamePlayer);
	    keyboard.close();
	     
	}

}
