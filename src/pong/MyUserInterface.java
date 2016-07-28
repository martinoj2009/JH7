package pong;

import gameNet.GameNet_UserInterface;
import gameNet.GamePlayer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class MyUserInterface extends JFrame
implements GameNet_UserInterface
{

	Box box = null;
	Image offScreenImage=null;
	Dimension previousSize=null;

	GamePlayer myGamePlayer;
	String myName;
	MyGameInput myGameInput = new MyGameInput();
	int game_top, game_bottom, game_left, game_right;
	Color[] paddleColors = {Color.green, Color.red};
	BoardDimensions boardDimensions = new BoardDimensions();

	@Override
	public void receivedMessage(Object ob) {
		MyGameOutput myGameOutput = (MyGameOutput)ob;
		// Check to see we were accepted and connected
		if (myGamePlayer != null)
		{
			if (myGameOutput.myGame.getMyIndex(myName) < 0)
			{
				System.out.println("Not allowed to connect to the game");
				exitProgram();
			}
			else
			{		
				box = myGameOutput.myGame.box;
				repaint();
			}
		}
		else
			System.out.println("Getting outputs before we are ready");
	}


	@Override
	public void startUserInterface(GamePlayer player) {
		myGamePlayer = player;
		myName = myGamePlayer.getPlayerName();
		myGameInput.setName( myName);
		myGameInput.setCmd(MyGameInput.CONNECTING);
		myGamePlayer.sendMessage(myGameInput);
	}



	public MyUserInterface()
	{
		super("My Pong Game");
		setSize(800, 400);
		// setResizable(false);
		addWindowListener(new Termination());

		Mouser m = new Mouser();
		addMouseMotionListener(m);
		addMouseListener(m);
		setVisible(true); 
	}


	public void paint(Graphics theScreen)
	{  
		Dimension d = getSize();
		if (offScreenImage==null || !d.equals(previousSize))
		{
			offScreenImage = createImage(d.width, d.height);
			previousSize = d;
		}
		Graphics g = offScreenImage.getGraphics();

		g.setColor(Color.white);
		g.fillRect(0,0, d.width, d.height);
		g.setColor(Color.black);

		Insets insets = getInsets();
		int pad=10;
		boardDimensions.setParms(insets.top+pad, insets.left+pad, 
				d.width-insets.left-insets.right -2*pad, 
				d.height-insets.top-insets.bottom -2*pad);
		if (box == null)
		{
			g.drawString("Click Mouse to start", 100,100);
		}
		else
		{

			if (!box.isRunning())
			{
				String str ="Success count="+
						box.successCount+ " Click Mouse to restart";
				g.drawString(str, 100, 100);            
			}

			Point bur = boardDimensions.toPixels(box.boxUpperRight);
			Point bul = boardDimensions.toPixels(box.boxUpperLeft);
			Point blr = boardDimensions.toPixels(box.boxLowerRight);
			Point bll = boardDimensions.toPixels(box.boxLowerLeft);
			Point hu  = boardDimensions.toPixels(box.rightHoleUpper);
			Point hl  = boardDimensions.toPixels(box.rightHoleLower);

			g.drawLine(bll.x, bll.y, blr.x, blr.y); // lower line
			g.drawLine(bll.x, bll.y, bul.x, bul.y); // left side
			g.drawLine(bul.x,bul.y, bur.x, bur.y);  // top side
			g.drawLine(bur.x, bur.y, hu.x, hu.y);   // above hole on right
			g.drawLine(blr.x, blr.y, hl.x, hl.y);   // below hole on right

			Point pball = boardDimensions.toPixels(box.ballLoc);
			int r = boardDimensions.toPixels(box.ballRadius);
			g.fillOval(pball.x-r, pball.y-r, 2*r, 2*r);


			int paddleWidth = boardDimensions.toPixels(box.paddleWidth);
			for (int i=0; i < 2; i++)
			{
				Point pPaddle =boardDimensions.toPixels( box.paddleLoc[i]);
				g.setColor(paddleColors[i]); 
				g.drawLine(pPaddle.x, pPaddle.y-paddleWidth/2,
						pPaddle.x, pPaddle.y+paddleWidth/2);
			}
		}

		theScreen.drawImage(offScreenImage, 0,0, this);
	}    

	private void exitProgram()
	{
		if (myGamePlayer != null)
		{
			myGameInput.setCmd(MyGameInput.DISCONNECTING);
			myGamePlayer.sendMessage(myGameInput); // Let the game know that we are leaving

			myGamePlayer.doneWithGame(); // clean up sockets
		}
		System.exit(0);
	}

	//*******************************************
	// An Inner class 
	//*******************************************
	class Mouser extends MouseAdapter
	{
		public void mouseMoved(MouseEvent e)
		{
			int y= e.getY();

			if (box != null)
			{
				myGameInput.setLocation(boardDimensions.toGenericY(y));
				if (myGamePlayer != null)
					myGamePlayer.sendMessage(myGameInput);

			}

		}
		public void mousePressed(MouseEvent e)
		{
			myGameInput.setCmd(MyGameInput.MOUSE_PRESSED);
			if (myGamePlayer != null)
				myGamePlayer.sendMessage(myGameInput);

		}  

	}
	//*******************************************
	// Another Inner class 
	//*******************************************
	class Termination extends WindowAdapter
	{
		public void windowClosing(WindowEvent e)
		{
			System.out.println("Client is exitting game");
			exitProgram();
		}
	}

	//****** Done with Inner Classes ***************
}
