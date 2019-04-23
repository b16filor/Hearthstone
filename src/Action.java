import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
public class Action extends Entity{
	private Card mSource;
	private ArrayList<Creature> mTargets;
	//Actions shows an event that has happened
	public Action(int x, int y, int width, int height, Card source, ArrayList<Creature> targets){
		super(x, y, width, height);
		mSource = source;
		mTargets = new ArrayList<Creature>();
		
		if(targets != null)
			mTargets.addAll(targets);
		mSource.setLocation(200, 100);
		for(int i = 0; i < mTargets.size(); i++)
			mTargets.get(i).setLocation(400 + i * 110, 100);
	}
	
	@Override
	public void draw(Graphics g) {
		//Draws the action in the list to the left
		g.setColor(Color.BLUE);
		if(mSource.getPortrait() != null)
			g.drawImage(mSource.getPortrait(), mX, mY, mWidth, mHeight, null);
		else 
			g.fillRect(mX, mY, mWidth, mHeight);
	}
	
	public void drawAction(Graphics g){
		//Draws the action and turn the background gray
		g.setColor(new Color(192, 192, 192, 200));
		g.fillRect(0, 0, 1300, 700);
		
		//Draws the card that triggered the action(if creature and dead draw red cross)
		mSource.draw(g);
		if(mSource.getClass() == Creature.class){
			if(((Creature)mSource).isDead()){
				g.setColor(Color.red);
				g.drawLine(mSource.getX(), mSource.getY(), mSource.getX() + mSource.getWidth(), mSource.getY() + mSource.getHeight());
				g.drawLine(mSource.getX() + mSource.getWidth(), mSource.getY(), mSource.getX(), mSource.getY() + mSource.getHeight());
			}
		}
		
		//Draws all the targets(if dead draw red cross)
		if(mTargets.size() > 0){
			g.setColor(Color.RED);
			g.fillRect(320, 150, 60, 30);
		}
		for(int i = 0; i < mTargets.size(); i++){
			mTargets.get(i).draw(g);
			if(mTargets.get(i).isDead()){
				g.setColor(Color.red);
				g.drawLine(mTargets.get(i).getX(), mTargets.get(i).getY(), mTargets.get(i).getX() + mTargets.get(i).getWidth(), mTargets.get(i).getY() + mTargets.get(i).getHeight());
				g.drawLine(mTargets.get(i).getX() + mTargets.get(i).getWidth(), mTargets.get(i).getY(), mTargets.get(i).getX(), mTargets.get(i).getY() + mTargets.get(i).getHeight());
			}
		}
	}
}
