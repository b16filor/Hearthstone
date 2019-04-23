import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class FileHandler {
	private static BufferedReader reader;
	
	public static ArrayList<Card> getCards(String path){
		ArrayList<Card> cards = new ArrayList<Card>();
		try {
			reader = new BufferedReader(new FileReader(new File(path)));
			String line;
			String[] card;
			//Reads every line in Cards.txt and convert them into Card object
			while((line = reader.readLine()) != null){
				card = line.split(";");
				System.out.println(card[0] + ";" + card[1] + ";" + card[2]);
				cards.add(card.length > 3 ? new Creature(0, 0, 100, 150, Integer.parseInt(card[1]), card[0], 
						Integer.parseInt(card[4]),
						getImage("Resources\\Images\\" + card[0] + ".png"),Integer.parseInt(card[2]), 
						Integer.parseInt(card[3]), !(Integer.parseInt(card[4]) == 1)) : new Spell(0, 0, 100, 150, 
						Integer.parseInt(card[1]), card[0],  Integer.parseInt(card[2]),
						getImage("Resources\\Images\\" + card[0] + ".png")));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e){
			e.printStackTrace();
		}
		return cards;
	}
	
	public static BufferedImage getImage(String path){
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(path));
		} catch (IOException e) {
			System.err.println("Couldn't read image file: " + path);
		}
		return image;
	}
}
