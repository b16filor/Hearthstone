import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
public class Game extends JPanel implements MouseListener,
	MouseMotionListener, Runnable{
	private Board[] mBoards;
	private TurnButton mTurnButton;
	private int mFPSCounter, mFPS, mUpdateCounter;
	private final int mUPS = 100;
	private Card mCurrentCard;
	private Creature mAttackingCreature;
	private Point mAttackLocation;
	private long mLastUpdate;
	private boolean mGameover;
	private ActionBar mActionBar;
	private Action mCurrentAction;
	private int winner;
	public Game(){
		setFocusable(true);
		//Adds this class as a MouseListener/MouseMotionListener for this JPanel
		addMouseListener(this);
		addMouseMotionListener(this);
		mBoards = new Board[2];
		mTurnButton = new TurnButton(1100, 300, 100, 50);
		mFPSCounter = 0;
		mUpdateCounter = 0;
		setBackground(new Color(139,69,19));
		startGame();
		//Creates and starts the main game thread
		Thread gameThread = new Thread(this);
		gameThread.start();
	}
	
	private void startGame(){
		//Sets variables to the right start values, can also be used to restart game
		mBoards[0] = new Board(Board.BoardState.CURRENTHIDDEN, new Hero(0, 0, 
				150, 150, 2, "Player 1", 0, null, null, Hero.HeroClass.MAGE, 0, 30, false, true), 1);
		mBoards[1] = new Board(Board.BoardState.OPPONENTSTURN, new Hero(0, 0, 
				150, 150, 2, "Player 2", 0, null, null, Hero.HeroClass.MAGE, 0, 30, false, true), 2);
		
		mAttackLocation = new Point();
		mActionBar = new ActionBar(20, 130, 60, 360);
		mGameover = false;
		mTurnButton.setTurnText("Start turn!");
		winner = 0;
	}
	
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		mTurnButton.draw(g);
		
		g.setColor(Color.gray);
		g.fillRect(90, 170, 750, 330);
		if(mBoards != null)
			for(int i = 0; i < mBoards.length; i++)
				mBoards[i].paint(g);
		
		//Draws the line between the creature attacking and current cursor location
		g.setColor(Color.RED);
		if(mAttackingCreature != null)
			g.drawLine(mAttackingCreature.getX() + mAttackingCreature.getWidth() / 2, mAttackingCreature.getY() + 
				mAttackingCreature.getHeight() / 2, (int)mAttackLocation.getX(), (int)mAttackLocation.getY());
		
		g.setColor(Color.RED);
		g.setFont(new Font("Arial", 1, 25));
		g.drawString("FPS: " + mFPS, 1100, 20);
		
		//Draws the action bar and eventual action
		mActionBar.draw(g);
		if(mCurrentAction != null)
			mCurrentAction.drawAction(g);
		
		if(winner != 0){
			g.setFont(new Font("Arial", 1, 50));
			g.setColor(Color.MAGENTA);
			g.drawString("Player " + winner + " won!", 500, 300);
		}
		
		mFPSCounter++;
		repaint();
	}
	
	private void update(){
		for(int i = 0; i < mBoards.length; i++){
			//Removes any dead creatures
			if(mBoards[i].getState() == Board.BoardState.CURRENTTURN)
				mTurnButton.setActionsLeft(mBoards[i].anyActionLeft());
			mBoards[i].removeDead();
			
			//Checks if player is alive and sets a winner if anyones dead
			if(mBoards[i].isDead()){
				mGameover = true;
				mTurnButton.setTurnText("Restart!");
				winner = 2 - i;
			}
		}
	}
	
	@Override
	public void run() {
		while(true){
			//Updates 100(value of mUPS) times per second
			if(System.currentTimeMillis() - mLastUpdate >= 1000 / mUPS){
				mLastUpdate = System.currentTimeMillis();
				mUpdateCounter++;
				//Updates FPS every second
				if(mUpdateCounter == mUPS){
					mFPS = mFPSCounter;
					mFPSCounter = 0;
					mUpdateCounter = 0;
				}
				if(!mGameover)
					update();
			}
		}	
	}
	
	
	@Override
	public void mousePressed(MouseEvent e) {
		//Handles mouse clicks
		if(!mGameover){ 
			//Changes turn if turn button is pressed
			if(mTurnButton.isClicked(e.getX(), e.getY()))
				mTurnButton.changeTurn(mBoards[0], mBoards[1]);
			for(int i = 0; i < mBoards.length; i++){
				if(mBoards[i].getState() == Board.BoardState.CURRENTTURN){
					if(e.getButton() == MouseEvent.BUTTON1){
						//If player is holding a card, checks if card can be played at that location
						if(mCurrentCard != null){
							Creature target = null;
							if(mCurrentCard.getClass() == Creature.class && mBoards[i].putOnBattleField(e.getX(), e.getY(), mCurrentCard)){
								ArrayList<Creature> temp = mCurrentCard.play(mBoards[i], mBoards[(i == 1 ? 0 : 1)], null);
								mActionBar.addAction(new Action(0, 0, 50, 30, mCurrentCard.copy(), temp));
								mCurrentCard = null;
							}else if(mCurrentCard.getClass() == Spell.class && !((Spell)mCurrentCard).needTarget() || ((target = mBoards[0].getCreatureOnBattleField(e.getX(), e.getY())) != null || 
									(target = mBoards[1].getCreatureOnBattleField(e.getX(), e.getY())) != null)){
								ArrayList<Creature> temp = mBoards[i].playSpell(mCurrentCard, 
										mBoards[i], mBoards[(i == 1 ? 0 : 1)], target);
								mActionBar.addAction(new Action(0, 0, 50, 30, mCurrentCard.copy(), temp));
								mCurrentCard = null;
							}
						}
						//If player is currently attacking with creature or hero power, checks if there is a valid target at location
						else if(mAttackingCreature != null){
							Creature attackedCreature = mBoards[(i == 1 ? 0 : 1)].getCreatureOnBattleField(e.getX(), e.getY());
							if(mAttackingCreature.getClass() == HeroPower.class && attackedCreature == null)
								attackedCreature = mBoards[i].getCreatureOnBattleField(e.getX(), e.getY());
							if(attackedCreature != null){
								if(mAttackingCreature.getClass() == HeroPower.class)
									mBoards[i].spendMana(mAttackingCreature.getManaCost());
								attackedCreature.attacked(mAttackingCreature);
								ArrayList<Creature> temp = new ArrayList<Creature>();
								temp.add((Creature)attackedCreature.copy());
								mActionBar.addAction(new Action(0, 0, 50, 30, mAttackingCreature.copy(), temp));
								mAttackingCreature = null;
							}
						}
						//Checks if player pressed a playable card in hand
						else if(mBoards[i].getCardInHand(e.getX(), e.getY()) != null && 
								mBoards[i].getCardInHand(e.getX(), e.getY()).getManaCost() <= mBoards[i].getMana())
							mCurrentCard = mBoards[i].getCardInHand(e.getX(), e.getY());
						//Checks if player pressed an awake creature on the battlefield
						else if(mBoards[i].getCreatureOnBattleField(e.getX(), e.getY()) != null && 
								!mBoards[i].getCreatureOnBattleField(e.getX(), e.getY()).isAsleep() &&
								mBoards[i].getCreatureOnBattleField(e.getX(), e.getY()).getClass() != Hero.class)
							mAttackingCreature = mBoards[i].getCreatureOnBattleField(e.getX(), e.getY());
						//Checks if hero power is pressed
						else if(mBoards[i].getHero().getHeroPower().isClicked(e.getX(), e.getY()) &&
								!mBoards[i].getHero().getHeroPower().isAsleep() && mBoards[i].getHero().getHeroPower().getManaCost() <= mBoards[i].getMana())
							mAttackingCreature = mBoards[i].getHero().getHeroPower();
					}
					//If right mouse button is pressed drop eventual card
					else if(e.getButton() == MouseEvent.BUTTON3){
						Card card = mCurrentCard;
						mCurrentCard = null;
						mAttackingCreature = null;
						mBoards[i].dropCard(card);
					}
				}
			}
		}else{
			//If the game is over and turn button is pressed start a new game
			if(mTurnButton.isClicked(e.getX(), e.getY())){
				startGame();
			}
		}
		
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		//If game isn't over update locations and show eventual actions hovered
		if(!mGameover){
			mAttackLocation.setLocation(e.getX(), e.getY());
			if(mCurrentCard != null)
				mCurrentCard.setLocation(e.getX() - mCurrentCard.getWidth() / 2,
						e.getY() - mCurrentCard.getHeight() / 2);
			if(mActionBar.isClicked(e.getX(), e.getY()))
				mCurrentAction = mActionBar.clickedAction(e.getX(), e.getY());
		}
	}
	
	//Unused mouse methods
	@Override
	public void mouseDragged(MouseEvent e) {}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent arg0) {}

	
}
