import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Creature extends Card{
	private final String[] mEffectText = {"", "Charge", "Draw 1 card", "Summon a 1/1 boar", "Deal 3 damage to enemy hero",
			"Transform all creatures into Magic Cows"};
	protected int mAttack, mHealth;
	protected boolean mIsAsleep;
	public Creature(int x, int y, int width, int height, int manaCost, String name
			, int effect, BufferedImage portrait, 
			int attack, int health, boolean isAsleep) {
		super(x, y, width, height, manaCost, name, effect, portrait);
		mAttack = attack;
		mHealth = health;
		mIsAsleep = isAsleep;
	}
	
	public void draw(Graphics g){
		drawBase(g);
		g.setFont(new Font("Arial", 1, 20));
		
		g.setColor(Color.MAGENTA);
		g.fillOval(mX - 5, mY + mHeight - 15, 20, 20);
		g.setColor(Color.white);
		g.drawString("" + mAttack, mX, mY + mHeight + 2);
		
		g.setColor(Color.red);
		g.fillOval(mX + mWidth - 15, mY + mHeight - 15, 20, 20);
		g.setColor(Color.white);
		g.drawString("" + mHealth, mX + mWidth - (mHealth > 9 ? 15 : 10), mY + mHeight + 2);
		
		g.setColor(Color.BLACK);
		g.setFont(new Font("Arial", 0, 12));
		String[] effectTextSplit = splitString(mEffectText[mEffect], g);
		for(int i = 0; i < effectTextSplit.length; i++)
			g.drawString(effectTextSplit[i], mX + 10, mY + 110 + i * 10);
		
		if(mIsAsleep){
			g.setFont(new Font("Arial", 1, 12));
			g.setColor(Color.green);
			g.fillOval(mX + mWidth - 15, mY - 5, 20, 20);
			g.setColor(Color.white);
			g.drawString("Zzz", mX + mWidth - 13, mY + 10);
		}
	}
	
	public ArrayList<Creature> play(Board friendlyBoard, Board enemyBoard, Creature target){
		//Performs the card effect and return the creatures affected by it
		Creature[] board;
		ArrayList<Creature> affectedCreatures = new ArrayList<Creature>();
		switch(mEffect){
		case 2:
			friendlyBoard.drawCards(1);
			break;
		case 3:
			Creature creature = new Creature(0, 0, 100, 150, 1, "Boar", 0, 
					FileHandler.getImage("Resources\\Images\\" + "Boar" + ".png"),
					1, 1, true);
			friendlyBoard.forceOnBattleField(creature);
			affectedCreatures.add((Creature)creature.copy());
			break;
		case 4:
			enemyBoard.getHero().damage(3);
			affectedCreatures.add((Hero)enemyBoard.getHero().copy());
			break;
		case 5:
			//Replaces all creatures with Magic Cows
			for(int i = 0; i < friendlyBoard.getBattleField().length; i++)
				if(friendlyBoard.getBattleField()[i] != null){
					affectedCreatures.add((Creature)friendlyBoard.getBattleField()[i].copy());
					friendlyBoard.getBattleField()[i] = new Creature(0, 0, 100, 150, 5, "Magic Cow", 5,
						FileHandler.getImage("Resources\\Images\\" + "Magic Cow" + ".png"), 3, 
						2, true);
				}
			for(int i = 0; i < enemyBoard.getBattleField().length; i++)
				if(enemyBoard.getBattleField()[i] != null){
					affectedCreatures.add((Creature)enemyBoard.getBattleField()[i].copy());
					enemyBoard.getBattleField()[i] = new Creature(0, 0, 100, 150, 5, "Magic Cow", 5,
						FileHandler.getImage("Resources\\Images\\" + "Magic Cow" + ".png"), 3, 
						2, true);
				}
			friendlyBoard.fixBattleField();
			enemyBoard.fixBattleField();
		}
		return affectedCreatures;
	}
	
	public Card copy(){
		//Returns a copy of the creature(used for actions)
		return new Creature(mX, mY, mWidth, mHeight, mManaCost, mName, mEffect, mPortrait, mAttack, mHealth, false);
	}

	public void attacked(Creature creature){
		mHealth -= creature.getAttack();
		creature.damage(mAttack);
		creature.sleep();
	}
	
	public void setLocation(Board.BoardState state, int i){
		//Changes the location of the creature depending on board state
		if(state != Board.BoardState.OPPONENTSTURN)
			setLocation(110 + i * 110, 330);
		else
			setLocation(110 + i * 110, 170);
	}
	
	public void sleep(){
		mIsAsleep = true;
	}
	
	public void wakeUp(){
		mIsAsleep = false;
	}
	
	public boolean isAsleep(){
		return mIsAsleep;
	}
	
	public int getAttack(){
		return mAttack;
	}
	
	public void damage(int damage){
		mHealth -= damage;
	}
	
	public boolean isDead(){
		return mHealth < 1;
	}
}
