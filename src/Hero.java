import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Hero extends Creature{
	public static enum HeroClass {MAGE, WARRIOR, PRIEST, ROGUE, 
		DRUID, HUNTER, WARLOCK, PALADIN, SHAMAN }
	private HeroClass mClass;
	private HeroPower mHeroPower;
	private String mName;
	private boolean mDrawHeroPower;
	public Hero(int x, int y, int width, int height, int manaCost, String name, int effect, 
			BufferedImage cardImage, BufferedImage portrait, HeroClass heroClass, int attack, 
			int health, boolean isAsleep, boolean drawHeroPower) {
		super(x, y, width, height, manaCost, name, effect, portrait, attack, health, isAsleep);
		mHealth = health;
		mClass = heroClass;
		mName = name;
		mDrawHeroPower = drawHeroPower;
		switch (mClass){
		case MAGE:
			mHeroPower = new HeroPower(x + 100, y + 80, 60, 60, HeroClass.MAGE, 2, "", 0, 
					null, 1, 0, false);
		}
	}
	
	@Override
	public void draw(Graphics g) {
		g.setColor(Color.RED);
		g.setFont(new Font("Ariel", 1, 20));
		g.fillRect(mX, mY, mWidth, mHeight);
		
		g.setColor(Color.white);
		g.drawString(mName, mX + 30, mY + 140);
		
		g.setFont(new Font("Ariel", 1, 40));
		g.drawString("" + mHealth, mX + 60, mY + 70);
		
		if(mDrawHeroPower)
			mHeroPower.draw(g);
	}

	public void move(Board.BoardState state){
		//Moves the hero depending on state
		if(state == Board.BoardState.OPPONENTSTURN)
			setLocation(500, 10);
		else
			setLocation(900, 300);
		mHeroPower.setLocation(mX + mWidth + 10, mY + 80);
	}
	
	@Override
	public Card copy() {
		//Returns a copy of the hero(used for actions)
		return new Hero(mX, mY, mWidth, mHeight, mManaCost, mName, mEffect, mCardImage, mPortrait, mClass, mAttack, mHealth, false, false);
	}
	
	public void attacked(Creature attacker){
		mHealth -= attacker.getAttack();
		attacker.sleep();
	}
	
	public HeroPower getHeroPower(){
		return mHeroPower;
	}
	
	public int getHealth(){
		return mHealth;
	}
	
	public void setHealth(int increment){
		mHealth += increment;
	}
}
