package pong;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import gameNet.GameCreator;
import gameNet.GameNet_UserInterface;
import gameNet.GamePlayer;

public class MyUserInterface extends JFrame
implements GameNet_UserInterface
{

	public static Graphics g;
	public int lastSuccessCount = 0;
	Box box = null;
	Image offScreenImage=null;
	Dimension previousSize=null;

	GamePlayer myGamePlayer;
	static String myName;
	MyGameInput myGameInput = new MyGameInput();
	int game_top, game_bottom, game_left, game_right;
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
		super("Pong");
		
		setSize(800, 400);
		setResizable(true);
		addWindowListener(new Termination());

		Mouser m = new Mouser();
		addMouseMotionListener(m);
		addMouseListener(m);
		setVisible(true); 
	}


	public void paint(Graphics theScreen)
	{
		//Set title with player name
		if(myName != null)
		{
			
			setTitle("Pong " + " Player: "+  myName);
		}
		
		//Check if reset
		reset();
		
		
		Dimension d = getSize();

		if (offScreenImage==null || !d.equals(previousSize))
		{
			offScreenImage = createImage(d.width, d.height);
			previousSize = d;
		}
		g = offScreenImage.getGraphics();

		g.setColor(Color.white);
		g.fillRect(0,0, d.width, d.height);
		g.setColor(Color.black);

		Insets insets = getInsets();
		int pad=10;
		boardDimensions.setParms(insets.top+pad, insets.left+pad, 
				d.width-insets.left-insets.right -2*pad, 
				d.height-insets.top-insets.bottom -2*pad);


		Point bur = boardDimensions.toPixels(box.boxUpperRight);
		Point bul = boardDimensions.toPixels(box.boxUpperLeft);
		Point blr = boardDimensions.toPixels(box.boxLowerRight);
		Point bll = boardDimensions.toPixels(box.boxLowerLeft);
		Point hur  = boardDimensions.toPixels(box.rightHoleUpper);
		Point hlr  = boardDimensions.toPixels(box.rightHoleLower);
		Point hul  = boardDimensions.toPixels(box.leftHoleUpper);
		Point hll = boardDimensions.toPixels(box.leftHoleLower);

		g.drawLine(bll.x, bll.y, blr.x, blr.y); // lower line
		g.drawLine(bul.x, bul.y, hul.x, hul.y);   // above hole on left
		g.drawLine(bll.x, bll.y, hll.x, hll.y);   // below hole on left
		g.drawLine(bul.x,bul.y, bur.x, bur.y);  // top side
		g.drawLine(bur.x, bur.y, hur.x, hur.y);   // above hole on right
		g.drawLine(blr.x, blr.y, hlr.x, hlr.y);   // below hole on right
		g.setColor(Color.red);
		g.fillRect(bur.x/2-2, bur.y+1, 4, blr.y-1-bur.y);	// vertical median

		// Changes font design of players' scores to be noticeable

		Font font = new Font("SansSerif", Font.BOLD, (int)(d.getWidth()*d.getHeight()*0.0005));
		FontMetrics fontMeasure = getFontMetrics(font);
		g.setFont(font);
		g.setColor(Color.lightGray);

		/* Using stringWidth routine of FontMetrics, obtained width size of score.
		 * Using getAscent routine of FontMetrics, obtained. height size of score.
		 * Both sizes, when compared to the width and height of Pong playing space,
		 * assists in centrally positioning scores in each player's half of playing space. */

		String player1Score = box.getPlayer1Score(),
				player2Score = box.getPlayer2Score();
		int xPosition_Player1Score = (blr.x/2 - 2 - fontMeasure.stringWidth(player1Score))/2,
				xPosition_Player2Score = blr.x/2 + (blr.x/2 - 2 - fontMeasure.stringWidth(player2Score))/2,
				yPosition_PlayerScores = blr.y - (blr.y - fontMeasure.getAscent())/2;

		// Displays individual player scores as part of playing space
		g.drawString(player1Score, xPosition_Player1Score, yPosition_PlayerScores);
		g.drawString(player2Score, xPosition_Player2Score, yPosition_PlayerScores);

		g.setColor(Color.black);
		Point pball = boardDimensions.toPixels(box.ballLoc);
		int r = boardDimensions.toPixels(box.ballRadius);
		g.fillOval(pball.x-r, pball.y-r, 2*r, 2*r);


		int paddleWidth = boardDimensions.toPixels(box.paddleWidth);
		for (int i=0; i < 2; i++)
		{
			Point pPaddle =boardDimensions.toPixels( box.paddleLoc[i]);
			g.setColor(Color.blue); 
			g.drawLine(pPaddle.x, pPaddle.y-paddleWidth/2,
					pPaddle.x, pPaddle.y+paddleWidth/2);
		}

		if (!box.isRunning())
		{
			Font scroll = new Font("SansSerif", Font.BOLD, (int)(d.getWidth()*d.getHeight()*0.000075));
			g.setFont(scroll);
			fontMeasure = getFontMetrics(scroll);
			g.setColor(Color.black);
			String continueMessage = "CLICK CONTINUE TO PLAY";
			int x_continueMessage = (blr.x - fontMeasure.stringWidth(continueMessage))/2,
					y_continueMessage = blr.y/4;
			
			// "CLICK CONTINUE TO PLAY" message display
			g.drawString(continueMessage, x_continueMessage, y_continueMessage);

			if(lastSuccessCount == box.successCount)
			{
				//Already ran and set
				
			}
			else
			{
				
				lastSuccessCount = box.successCount;
				System.out.println("New score");
				System.out.println("Player1: " + box.getPlayer1Score() + " Player2: "  + box.getPlayer2Score());
				
				//If the limit hasn't been set
				if(GameCreator.getScoreLimit() != 0)
				{
					if(Integer.parseInt(box.getPlayer1Score()) >= GameCreator.getScoreLimit() || Integer.parseInt(box.getPlayer2Score()) >= GameCreator.getScoreLimit())
					{
						System.out.println("Score limit!");
						
						MyGameInput reset = new MyGameInput();
						
						reset.setCmd(MyGameInput.RESET);
						
						myGamePlayer.sendMessage(reset);
						
						//exitProgram(); //What to do here??? I will just exit
					}
				}
				
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
			//Stop the user from resetting in the middle of a match
			if(box.isRunning())
			{
				return;
			}
			
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
	
	public void reset()
	{
		if(box.resetScore == true)
		{
			box.resetScore = false;
			System.out.println("Resetting score");
			exitProgram();
		}
		
	}


	//****** Done with Inner Classes ***************
}
