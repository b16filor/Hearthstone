import java.awt.Graphics;
public abstract class Entity {
	protected int mX, mY, mWidth, mHeight;
	public Entity(int x, int y, int width, int height){
		mX = x;
		mY = y;
		mWidth = width;
		mHeight = height;
	}
	
	public abstract void draw(Graphics g);
	
	public boolean isClicked(int x, int y){
		//Returns true if x and y is within entity else false
		return (x < mX + mWidth && x > mX && y < mY + mHeight && y > mY); 
	}
	
	public int getX(){
		return mX;
	}

	public int getY(){
		return mY;
	}
	
	public int getWidth(){
		return mWidth;
	}
	
	public int getHeight(){
		return mHeight;
	}
	
	public void setLocation(int x, int y){
		mX = x;
		mY = y;
	}
}
