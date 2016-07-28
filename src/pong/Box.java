package pong;

import java.awt.Point;
import java.io.Serializable;
import java.util.Random;

public class Box implements Serializable{

	//generic size
    static final int box_width = 1000; 
    static final int box_height = 500;

    Point boxUpperRight ;
    Point boxUpperLeft  ;
    Point boxLowerRight ;
    Point boxLowerLeft  ;
    
    Point rightHoleUpper     ;
    Point rightHoleLower     ;
    
    Point ballLoc       ;
    
    Point[] paddleLoc     ;

    int paddleWidth ;
    int ballRadius = 20;    
    private int ballVx, ballVy;
    private Random rand = new Random();

    int successCount=0;
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
        rightHoleUpper    = new Point(box_right, box_top +(box_bottom-box_top)/4);
        rightHoleLower    = new Point(box_right, box_top +3*(box_bottom-box_top)/4);      

        paddleWidth  = (rightHoleLower.y - rightHoleUpper.y)/3;
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
        ballVx = (-50 + (int)(100*Math.random()));
        ballVy = -50 + (int)(100*Math.random());
        
        paddleLoc = new Point[2];
        paddleLoc[0]     = new Point(box_right,( rightHoleUpper.y+rightHoleLower.y)/2);
        paddleLoc[1]    = new Point(box_left+1,( rightHoleUpper.y+rightHoleLower.y)/2);
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

    }

    public void update()
    {
        if ( !running)
            return;
        ballLoc.x = ballLoc.x + ballVx;
        ballLoc.y = ballLoc.y + ballVy;

        // check against right wall
        if (ballLoc.x + ballRadius > boxUpperRight.x)
        {
            if (ballLoc.y <= rightHoleUpper.y || ballLoc.y >= rightHoleLower.y )
            {
            	// hits wall
                ballVx *= -1;
                ballLoc.x = boxUpperRight.x - ballRadius;
            }
            else if (ballLoc.y >= paddleLoc[0].y-paddleWidth/2 &&
                    ballLoc.y <= paddleLoc[0].y + paddleWidth/2)
            {
                successCount +=1;  // In hole but bounces off right paddle
                ballVx *= -1;
                ballLoc.x = boxUpperRight.x - ballRadius;
                System.out.println("In Hole and hits paddle");
            }
            else
            {
                // In hole and missed by paddle
                running= false;
                System.out.println("In Hole and missed by paddle");
            }
        }

		 // check against the left wall
        if (ballLoc.x - ballRadius < boxUpperLeft.x)
        {
            ballVx *= -1;
            ballLoc.x = boxUpperLeft.x + ballRadius;        
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

}
