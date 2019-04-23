import java.awt.image.*;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
public abstract class Card extends Entity{
	protected int mManaCost, mEffect;
	protected BufferedImage mCardImage, mPortrait;
	protected String mName;
	public Card(int x, int y, int width, int height, int manaCost, String name, int effect,
			BufferedImage portrait){
		super(x, y, width, height);
		mManaCost = manaCost;
		mName = name;
		mEffect = effect;
		mCardImage = FileHandler.getImage("Resources\\Images\\Card Background.png");
		mPortrait = portrait;
	}
	
	protected void drawBase(Graphics g){
		//Draws the basic card
		g.setColor(new Color(205,133,63));
		if(mCardImage != null)
			g.drawImage(mCardImage, mX, mY, mWidth, mHeight, null);
		else
			g.fillRect(mX, mY, mWidth, mHeight);
		
		g.setColor(Color.blue);
		if(mPortrait != null)
			g.drawImage(mPortrait, mX + 10, mY + 10, mWidth - 20, mHeight/2 - 20, null);
		else
			g.fillRect(mX + 10, mY + 10, mWidth - 20, mHeight/2 - 20);
		
		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial", 1, 12));
		g.drawString(mName, mX + 5 + (90 - g.getFontMetrics().stringWidth(mName)) / 2, mY + 85);
		
		g.setFont(new Font("Arial", 1, 20));
		g.setColor(Color.blue);
		g.fillOval(mX - 5, mY - 5, 20, 20);
		g.setColor(Color.white);
		g.drawString("" + mManaCost, mX, mY + 12);
	}
	
	protected String[] splitString(String effect, Graphics g){
		//Splits the Effect of a card into the optimal draw metrics 
		int pixelWidth = g.getFontMetrics().stringWidth(effect);
		int rows = pixelWidth / 80 + 2;
		String[] changedText = new String[rows];
		String[] words = effect.split(" ");
		int nextWord = 0;
		mainLoop : for(int i = 0; i < rows; i++){
			do{
				if(words.length == nextWord)
					break mainLoop;
				changedText[i] = (changedText[i] == null ? "" : changedText[i] + " ") + words[nextWord];
				nextWord++;
			}while(g.getFontMetrics().stringWidth(changedText[i]) < 80);
			nextWord--;
			changedText[i] = changedText[i].substring(0, words[nextWord].length() != 1 ? 
					changedText[i].indexOf(words[nextWord]) : changedText[i].indexOf(words[nextWord].charAt(0)));
		}
		//Makes the last line an empty String if null(draws null at the end of the effect otherwise)
		if(changedText[changedText.length - 1] == null)
			changedText[changedText.length - 1] = "";
		return changedText;
	}
	
	public abstract ArrayList<Creature> play(Board friendlyBoard, Board enemyBoard, Creature target);
	
	public abstract Card copy();
	
	public int getManaCost(){
		return mManaCost;
	}
	
	public BufferedImage getPortrait(){
		return mPortrait;
	}
}
