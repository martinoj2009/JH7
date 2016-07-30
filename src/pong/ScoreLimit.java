package pong;

import java.io.Serializable;

public class ScoreLimit implements Serializable{
	
	int limit;

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}
	
	public String toString()
	{
		return "Score";
	}

}
