import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Spell extends Card{
	private final String[] mEffectText = {"", "Deal 3 damage", "Deal 6 damage", "Deal 2 damage to all enemies",
			"Deal 4 damage to all enemy creatures", "Draw 2 cards", "Draw 1 card and deal 1 damage", 
			"Transform a creature into a 1/1 sheep", "Deal 5 damage to enemy hero",
			"Gain 1 mana(this turn only)", "Deal 1 damage to all minions until no minions die"};
	public Spell(int x, int y, int width, int height, int manaCost, String name, int effect,
			BufferedImage portrait) {
		super(x, y, width, height, manaCost, name, effect, portrait);
	}

	@Override
	public void draw(Graphics g) {
		drawBase(g);
		
		g.setColor(Color.BLACK);
		g.setFont(new Font("Arial", 0, 12));
		String[] effectTextSplit = splitString(mEffectText[mEffect], g);
		for(int i = 0; i < effectTextSplit.length; i++)
			g.drawString(effectTextSplit[i], mX + 10, mY + 110 + i * 10);
	}
	
	public ArrayList<Creature> play(Board friendlyBoard, Board enemyBoard, Creature target){
		//Performs the card effect and return the creatures affected by it
		Creature[] board;
		ArrayList<Creature> affectedCreatures = new ArrayList<Creature>();
		switch(mEffect){
			case 1:
				target.damage(3);
				affectedCreatures.add((Creature)target.copy());
				break;
			case 2:
				target.damage(6);
				affectedCreatures.add((Creature)target.copy());
				break;
			case 3:
				//Deals two damage to all enemy creatures and hero
				board = enemyBoard.getBattleField();
				for(int i = 0; i < board.length; i++)
					if(board[i] != null){
						board[i].damage(2);
						affectedCreatures.add((Creature)board[i].copy());
					}
				enemyBoard.getHero().damage(2);
				affectedCreatures.add((Creature)enemyBoard.getHero().copy());
				break;
			case 4:
				//Deals four damage to all enemy creatures
				board = enemyBoard.getBattleField();
				for(int i = 0; i < board.length; i++)
					if(board[i] != null){
						board[i].damage(4);
						affectedCreatures.add((Creature)board[i].copy());
					}
				break;
			case 5:
				friendlyBoard.drawCards(2);
				break;
			case 6:
				friendlyBoard.drawCards(1);
				target.damage(1);
				affectedCreatures.add((Creature)target.copy());
				break;
			case 7:
				//Replaces the target creature with a sheep
				affectedCreatures.add((Creature)target.copy());
				for(int i = 0; i < friendlyBoard.getBattleField().length; i++)
					if(friendlyBoard.getBattleField()[i] == target)
						friendlyBoard.getBattleField()[i] = new Creature(0, 0, 100, 150, 1, "Sheep", 0,
								FileHandler.getImage("Resources\\Images\\" + "Sheep" + ".png"),
								1, 1, true);
				for(int i = 0; i < enemyBoard.getBattleField().length; i++)
					if(enemyBoard.getBattleField()[i] == target)
						enemyBoard.getBattleField()[i] = new Creature(0, 0, 100, 150, 1, "Sheep", 0,
								FileHandler.getImage("Resources\\Images\\" + "Sheep" + ".png"),
								1, 1, true);
				affectedCreatures.add((Creature)target.copy());
				enemyBoard.fixBattleField();
				friendlyBoard.fixBattleField();
				break;
			case 8:
				enemyBoard.getHero().damage(5);
				affectedCreatures.add((Hero)enemyBoard.getHero().copy());
				break;
			case 9:
				friendlyBoard.spendMana(-1);
				break;
			case 10:
				Creature[] friendlyMinions = friendlyBoard.getBattleField();
				Creature[] enemyMinions = enemyBoard.getBattleField();
				boolean minionDied;
				do {
					minionDied = false;
					for(int i = 0; i < friendlyMinions.length; i++){
						if(friendlyMinions[i] != null){
							friendlyMinions[i].damage(1);
							if (friendlyMinions[i].isDead()){
								minionDied = true;
							}
							affectedCreatures.add((Creature)friendlyMinions[i].copy());
						}
					}

					for(int i = 0; i < enemyMinions.length; i++){
						if(enemyMinions[i] != null){
							enemyMinions[i].damage(1);
							if (enemyMinions[i].isDead()){
								minionDied = true;
							}
							affectedCreatures.add((Creature)enemyMinions[i].copy());
						}
					}

					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} while (minionDied);

				break;
		}
		return affectedCreatures;
	}
	
	@Override
	public Card copy() {
		//Returns a copy of the spell(used for actions)
		return new Spell(mX, mY, mWidth, mHeight, mManaCost, mName, mEffect, mPortrait);
	}
	
	public boolean needTarget(){
		//Returns true if card has effect that require a target
		return mEffect == 1 || mEffect == 2 || mEffect == 6 || mEffect == 7;
	}
}
