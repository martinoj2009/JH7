package pong;

import java.io.Serializable;

public class MyGameOutput implements Serializable{
    MyGame myGame = null;
    MyGameOutput(MyGame g)
    {
        myGame =g;
    }
}
