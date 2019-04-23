import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class ActionBar extends Entity{
	private ArrayList<Action> mActions;
	private final int MAXACTIONS = 10;
	public ActionBar(int x, int y, int width, int height) {
		super(x, y, width, height);
		mActions = new ArrayList<Action>();
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(mX, mY, mWidth, mHeight);
		for(int i = 0; i < mActions.size(); i++)
			mActions.get(i).draw(g);
	}
	
	public Action clickedAction(int x, int y){
		//Returns the clicked action(if any)
		for(int i = 0; i < mActions.size(); i++)
			if(mActions.get(i).isClicked(x, y))
				return mActions.get(i);
		return null;
	}
	
	public void addAction(Action action){
		mActions.add(0, action);
		fixActions();
	}

	private void fixActions(){
		//Removes actions in the end of the list when more actions than MAXACTIONS
		while(mActions.size() > MAXACTIONS)
			mActions.remove(mActions.size() - 1);
		//Sets the correct location for all actions
		for(int i = 0; i < mActions.size(); i++)
			mActions.get(i).setLocation(mX + 5, mY + 5 + i * (mActions.get(i).getHeight() + 5));
	}
}
