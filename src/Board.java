import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.io.*;
public class Board {
	public static enum BoardState {MULLIGAN, CURRENTTURN, OPPONENTSTURN, CURRENTHIDDEN}
	private ArrayList<Card> mDeck, mHand;
	private Creature[] mBattleField;
	private BoardState mState;
	private int mMana, mCurrentMana;
	private Hero mHero;
	private int mFatigueCounter;
	private BufferedImage mCardBack, mCardBackRotated;
	public Board(BoardState state, Hero hero, int deck){
		mDeck = new ArrayList<Card>();
		//Gets images and cards from FileHandler
		mCardBack = FileHandler.getImage("Resources\\Images\\Cardback.png");
		mCardBackRotated = FileHandler.getImage("Resources\\Images\\Cardback(rotated).png");
		//mDeck.addAll(FileHandler.getCards("Resources\\Cards.txt"));
		mDeck.addAll(FileHandler.getCards("Resources\\Cards.txt"));

		mFatigueCounter = 0;
		mHand = new ArrayList<Card>();
		mBattleField = new Creature[7];
		mHero = hero;
		mState = state;
		mMana = 0;
		mHero.move(mState);
		
		//Draws cards depending on starting turn
		if(state == BoardState.OPPONENTSTURN){
			drawCards(4);
			mHand.add(new Spell(0, 0, 100, 150, 
						0, "The Coin",  9,
						FileHandler.getImage("Resources\\Images\\The Coin.png")));
			fixCardsInHand();
		}else {
			drawCards(3);
		}
		
	}
	
	public void paint(Graphics g){
		mHero.draw(g);
		//Paints the board depending on current turn
		//I know there is a bit of redundant code
		if(mState != BoardState.OPPONENTSTURN){
			g.drawImage(mCardBack, 1150, 500, 100, 130, null);
			g.setFont(new Font("Ariel", 0, 15));
			g.setColor(Color.BLACK);
			g.drawString(mDeck.size() == 0 ? "Fatigue: " + (int)(mFatigueCounter + 1) : "Cards left: " + mDeck.size(), 1150, 575);
			g.setColor(Color.BLACK);
			
			for(int i = 0; i < mBattleField.length; i++){
				if(mBattleField[i] != null)
					mBattleField[i].draw(g);
			}
			
			//Draws the hand visibility depending on turn
			for(int i = 0; i < mHand.size(); i++){
				if(mState == BoardState.CURRENTTURN)
					mHand.get(i).draw(g);
				else {
					g.drawImage(mCardBack, mHand.get(i).getX(), mHand.get(i).getY(), 
							mHand.get(i).getWidth(), mHand.get(i).getHeight(), null);
				}
			}
			
			g.setFont(new Font("Arial", 1, 25));
			g.setColor(Color.blue);
			g.fillOval(900 - 5, 450 - 5, 60, 60);
			g.setColor(Color.white);
			g.drawString(mCurrentMana + "/" + mMana, 900 + 7, 450 + 26  + 5);
		}else {
			g.drawImage(mCardBack, 1150, 20, 100, 130, null);
			g.setFont(new Font("Ariel", 0, 15));
			g.setColor(Color.BLACK);
			g.drawString(mDeck.size() == 0 ? "Fatigue: " + (int)(mFatigueCounter + 1) : "Cards left: " + mDeck.size(), 1150, 85);
			g.setColor(Color.BLACK);
			
			for(int i = 0; i < mBattleField.length; i++){
				if(mBattleField[i] != null)
					mBattleField[i].draw(g);
			}
			
			g.drawImage(mCardBackRotated, 100, 20, 130, 100, null);
			g.setColor(Color.BLACK);
			g.drawString("Cards in hand: " + mHand.size(), 120, 65);
			g.setFont(new Font("Arial", 1, 25));
			g.setColor(Color.blue);
			g.fillOval(900 - 5, 200 - 5, 60, 60);
			g.setColor(Color.white);
			g.drawString(mCurrentMana + "/" + mMana, 900 + 7, 200 + 26  + 5);
		}
		
	}
	
	public void startTurn(){
		mMana += mMana > 9 ? 0 : 1;
		mCurrentMana = mMana;
		setState(BoardState.CURRENTTURN);
		drawCards(1);
		
		for(int i = 0; i < mBattleField.length; i++)
			if(mBattleField[i] != null && mBattleField[i].isAsleep())
				mBattleField[i].wakeUp();
		mHero.getHeroPower().wakeUp();
	}
	
	public void endTurn(){
		setState(BoardState.OPPONENTSTURN);
	}
	
	public void forceOnBattleField(Creature creature){
		//Puts a puts a creature on the first available spot on the battlefield
		for(int i = 0; i < mBattleField.length; i++)
			if(mBattleField[i] == null){
				creature.setLocation(mState, i);
				mBattleField[i] = creature;
				break;
			}
	}
	
	public boolean putOnBattleField(int x, int y, Card card){
		//If card is a creature puts it on marked location on battlefield
		if(card.getClass() != Creature.class)
			return false;
		Creature creature = (Creature)card;
		for(int i = 0; i < mBattleField.length; i++){
			if(mBattleField[i] == null && y > 330 && y < 490 &&
					x > 110 + i * 110 && x < 210 + i * 110){
				creature.setLocation(mState, i);
				mHand.remove(creature);
				mBattleField[i] = creature;
				fixCardsInHand();
				mCurrentMana -= card.getManaCost();
				return true;
			}
		}
		return false;
	}
	
	public void removeDead(){
		for(int i = 0; i < mBattleField.length; i++)
			if(mBattleField[i] != null && mBattleField[i].isDead())
				mBattleField[i] = null;
	}
	
	private void fixCardsInHand(){
		//Fixes the positions of cards in hand
		for(int i = 0; i < mHand.size(); i++)
			mHand.get(i).setLocation(50 + i * 105, 500);
	}
	
	public void fixBattleField(){
		//Fixes battlefield location, used after turn switch
		for(int i = 0; i < mBattleField.length; i++)
			if(mBattleField[i] != null)
				mBattleField[i].setLocation(mState, i);
		mHero.move(mState);
	}
	
	public void drawCards(int amountOfCards){
		//Draws a random card from the deck
		for(int i = 0; i < amountOfCards; i++){
			if(mDeck.size() != 0 && mHand.size() < 10){
				Card card = mDeck.get((int)(Math.random() * mDeck.size()));
				mDeck.remove(card);
				if(mHand.size() < 10){
					card.setLocation(50 + mHand.size() * 105, 500);
					mHand.add(card);
				}
			}else
				fatigue();
		}
		
	}
	
	public boolean anyActionLeft(){
		//Checks if there is any possible actions left(used for turn button)
		for(int i = 0; i < mBattleField.length; i++)
			if(mBattleField[i] != null && !mBattleField[i].isAsleep())
				return true;
		for(int i = 0; i < mHand.size(); i++)
			if(mHand.get(i).getManaCost() <= mCurrentMana)
				return true;
		if(!mHero.getHeroPower().isAsleep() && mHero.getHeroPower().getManaCost() <= mCurrentMana)
			return true;
		return false;
	}
	
	public void dropCard(Card card){
		//Puts a card back in hand
		for(int i = 0; i < mHand.size(); i++){
			if(mHand.get(i).equals(card)){
				card.setLocation(50 + i * 105, 500);
			}
		}
	}
	
	public ArrayList<Creature> playSpell(Card card, Board board1, Board board2, Creature target){
		//Returns creatures affected by card and plays card from hand
		mHand.remove(card);
		mCurrentMana -= card.getManaCost();
		fixCardsInHand();
		return card.play(board1, board2, target);
	}
	
	private void fatigue(){
		//Increase fatigue count and deals damage
		mFatigueCounter++;
		mHero.setHealth(-1 * mFatigueCounter);
	}
	
	public void spendMana(int mana){
		mCurrentMana -= mana;
	}
	
	public void setState(BoardState state){
		mState = state;
		fixBattleField();
	}
	
	public Hero clickedHero(int x, int y){
		//Return hero or null depending on if clicked
		return mHero.isClicked(x, y) ? mHero : null;
	}
	
	public Card getCardInHand(int x, int y){
		//Returns card thats clicked
		for(int i = 0; i < mHand.size(); i++){
			if(mHand.get(i).isClicked(x, y))
				return mHand.get(i);
		}
		return null;
	}
	
	public Creature getCreatureOnBattleField(int x, int y){
		//Returns a creature on battlefield thats clicked
		for(int i = 0; i < mBattleField.length; i++)
			if(mBattleField[i] != null && mBattleField[i].isClicked(x, y))
				return mBattleField[i];
		if(mHero.isClicked(x, y))
			return mHero;
		return null;
	}
	
	public Creature[] getBattleField(){
		return mBattleField;
	}
	
	public Hero getHero(){
		return mHero;
	}
	
	public BoardState getState(){
		return mState;
	}
	
	
	public boolean isDead(){
		return mHero.getHealth() < 1;
	}
	
	public int getMana(){
		return mCurrentMana;
	}
}
