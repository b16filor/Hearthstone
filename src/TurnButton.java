import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class TurnButton extends Entity{
	private String mTurnText;
	private boolean mAnyActionLeft;
	public TurnButton(int x, int y, int width, int height){
		super(x, y, width, height);
		mAnyActionLeft = false;
	}

	public void draw(Graphics g) {
		//Changes color depending on if there is any actions left
		g.setColor(mAnyActionLeft ? Color.YELLOW : Color.GREEN);
		g.fillRect(mX, mY, mWidth, mHeight);
		g.setColor(Color.BLACK);
		g.setFont(new Font("Ariel", 1, 18));
		g.drawString(mTurnText, mX + 5, mY + 30);
	}
	
	public void changeTurn(Board boardOne, Board boardTwo){
		//Changes the state of both board depending on the current states
		if(boardOne.getState() == Board.BoardState.CURRENTTURN){
			boardOne.endTurn();
			boardTwo.setState(Board.BoardState.CURRENTHIDDEN);
			mTurnText = "Start turn!";
		}else if(boardOne.getState() == Board.BoardState.CURRENTHIDDEN){
			boardOne.startTurn();
			mTurnText = "End turn!";
		}else if(boardTwo.getState() == Board.BoardState.CURRENTTURN){
			boardTwo.endTurn();
			boardOne.setState(Board.BoardState.CURRENTHIDDEN);
			mTurnText = "Start turn!";
		}else if(boardTwo.getState() == Board.BoardState.CURRENTHIDDEN){
			boardTwo.startTurn();
			mTurnText = "End turn!";
		}
	}
	
	public void setTurnText(String text){
		mTurnText = text;
	}
	
	public void setActionsLeft(boolean anyActionLeft){
		mAnyActionLeft = anyActionLeft;
	}
}
