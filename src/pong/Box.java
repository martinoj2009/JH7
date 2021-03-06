package pong;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JOptionPane;

public class Box implements Serializable{

	//generic size
	static final int box_width = 1000; 
	static final int box_height = 500;

	private int Player1 = 0, Player2 = 0; // Keep count of each player's current score count

	Point boxUpperRight;
	Point boxUpperLeft;
	Point boxLowerRight;
	Point boxLowerLeft;

	Point rightHoleUpper;
	Point rightHoleLower;
	Point leftHoleUpper;
	Point leftHoleLower;

	Point ballLoc;

	Point[] paddleLoc;

	int paddleWidth;
	int ballRadius = 15;    
	private int ballVx, ballVy;
	private Random rand = new Random();

	int successCount=0;
	boolean resetScore;
	int scoreLimit;
	private boolean running=false;
	

	public boolean isRunning()
	{
		return running;
	}


	public Box()
	{
		int box_top =0;
		int box_bottom = box_height;
		int box_left =0;
		int box_right = box_width;

		boxUpperRight= new Point(box_right, box_top);
		boxUpperLeft = new Point(box_left, box_top);
		boxLowerRight= new Point(box_right, box_bottom);
		boxLowerLeft = new Point(box_left, box_bottom);

		// Right-side
		rightHoleUpper    = new Point(box_right, box_top);
		rightHoleLower    = new Point(box_right, box_bottom);  

		// Left-side
		leftHoleUpper    = new Point(box_left, box_top);
		leftHoleLower    = new Point(box_left, box_bottom); 

		//rightHoleUpper    = new Point(box_right, box_top +(box_bottom-box_top)/4);
		//rightHoleLower    = new Point(box_right, box_top +3*(box_bottom-box_top)/4);

		paddleWidth  = (box_bottom - box_top)/3;
		setGame(false);

	}
	void setGame(boolean startRunning)
	{
		int box_top =0;
		int box_bottom = box_height;
		int box_left =0;
		int box_right = box_width;

		// Start the ball out at a random spot
		ballLoc      = new Point(box_left+ rand.nextInt(box_right - box_left),
				box_top+ rand.nextInt(box_bottom - box_top));

		// Heuristic for generating random starting velocities ... maybe not the best
		ballVx = 40;
		ballVy = 40;
		
		//ballVx = (-50 + (int)(100*Math.random()));
		//ballVy = -50 + (int)(100*Math.random());

		paddleLoc = new Point[2];
		paddleLoc[0]     = new Point(box_right,( rightHoleUpper.y+rightHoleLower.y)/2);
		paddleLoc[1]    = new Point(box_left,( leftHoleUpper.y+leftHoleLower.y)/2);
		if (startRunning)
			running = true;
	}

	public void setPaddleY(int yLoc, int clientIndex)
	{
		paddleLoc[clientIndex].y =yLoc;
		if (paddleLoc[clientIndex].y - paddleWidth/2 < rightHoleUpper.y)
			paddleLoc[clientIndex].y = rightHoleUpper.y + paddleWidth/2;
		if (paddleLoc[clientIndex].y + paddleWidth/2 > rightHoleLower.y)
			paddleLoc[clientIndex].y = rightHoleLower.y - paddleWidth/2;
		if (paddleLoc[clientIndex].y - paddleWidth/2 < leftHoleUpper.y)
			paddleLoc[clientIndex].y = leftHoleUpper.y + paddleWidth/2;
		if (paddleLoc[clientIndex].y + paddleWidth/2 > leftHoleLower.y)
			paddleLoc[clientIndex].y = leftHoleLower.y - paddleWidth/2;

	}

	public void update()
	{
		
		if ( !running)
			return;
		ballLoc.x = ballLoc.x + ballVx;
		ballLoc.y = ballLoc.y + ballVy;

		// Check for ball status against right wall
		if (ballLoc.x + ballRadius > boxUpperRight.x)
		{
			if (ballLoc.y <= rightHoleUpper.y || ballLoc.y >= rightHoleLower.y )
			{
				// hits wall 
				ballVx *= -1;
				ballLoc.x = boxUpperRight.x - ballRadius;
				playSound("hit.wav");
			}
			else if (ballLoc.y >= paddleLoc[0].y-paddleWidth/2 &&
					ballLoc.y <= paddleLoc[0].y + paddleWidth/2)
			{
				successCount +=1;  // In hole but bounces off right paddle
				ballVx *= -1;
				ballLoc.x = boxUpperRight.x - ballRadius;
				System.out.println("In Hole and hits paddle");
				playSound("hit.wav");
			}
			else
			{
				successCount +=1;
				// In hole and missed by paddle
				playSound("point.wav");

				// Player1 is awarded a point to their current score
				this.Player1++;

				running= false;
				System.out.println("In Hole and missed by paddle");
				playSound("point.wav");
			}
		}

		// Check for ball status against the left wall
		if (ballLoc.x - ballRadius < boxUpperLeft.x)
		{
			if (ballLoc.y <= leftHoleUpper.y || ballLoc.y >= leftHoleLower.y )
			{
				// hits wall 
				ballVx *= -1;
				ballLoc.x = boxUpperLeft.x - ballRadius;
				playSound("hit.wav");
			}
			else if (ballLoc.y >= paddleLoc[1].y-paddleWidth/2 &&
					ballLoc.y <= paddleLoc[1].y + paddleWidth/2)
			{
				successCount +=1;  // In hole but bounces off left paddle
				ballVx *= -1;
				ballLoc.x = boxUpperLeft.x + ballRadius;
				System.out.println("In Hole and hits paddle");
				playSound("hit.wav");
			}
			else
			{
				// In hole and missed by paddle
				playSound("point.wav");

				// Player2 is awarded a point to their current score
				this.Player2++;

				running= false;
				System.out.println("In Hole and missed by paddle");
				playSound("point.wav");
			}
		}

		// check against the bottom wall
		if (ballLoc.y + ballRadius > boxLowerRight.y)
		{
			ballVy *= -1;
			ballLoc.y = boxLowerRight.y - ballRadius;        
		}

		// check against the top wall
		if (ballLoc.y - ballRadius < boxUpperRight.y)
		{
			ballVy *= -1;
			ballLoc.y = boxUpperRight.y + ballRadius;        
		}

	}

	// Assist with display of Player1 score in String when called from MyUserInterface paint method
	public String getPlayer1Score() {

		return String.valueOf(this.Player1);
	}
	

	// Same as previous routine, but for Player2 score
	public String getPlayer2Score() {

		return String.valueOf(this.Player2);
	}
	
	public void resetScore()
	{
		this.Player1 = 0;
		this.Player2 = 0;
		
	}

	public void playSound(String file) {
		try {
			
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(file));
			Clip clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			clip.start();
		} catch(Exception ex) {
			System.out.println("Error with playing sound.");
			ex.printStackTrace();
		}
	}
}