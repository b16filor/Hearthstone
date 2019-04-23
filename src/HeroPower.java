import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class HeroPower extends Creature{
	private Hero.HeroClass mHero;
	public HeroPower(int x, int y, int width, int height, Hero.HeroClass hero, int manaCost, String name, int effect, 
			BufferedImage portrait, int attack, int health, boolean isAsleep) {
		super(x, y, width, height, manaCost, name, effect, portrait, attack, health, isAsleep);
		mHero = hero;
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(mIsAsleep ? Color.GRAY : Color.RED);
		g.fillOval(mX, mY, mWidth, mHeight);
		
	}

	@Override
	public Card copy() {
		//Returns a copy of the hero power(used for actions)
		return new HeroPower(mX, mY, mWidth, mHeight, mHero, mManaCost, mName,
				mEffect, mPortrait, mAttack, mHealth, false);
	}
}
