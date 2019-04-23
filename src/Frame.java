import javax.swing.JFrame;

public class Frame extends JFrame{
	private final int mWidth = 1300, mHeight = 700;
	public static void main(String[] args){
		new Frame();
	}
	
	public Frame(){
		//Creates the window and adds game to it
		setSize(mWidth, mHeight);
		setTitle("A card game");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		add(new Game());
		setVisible(true);
	}
}
