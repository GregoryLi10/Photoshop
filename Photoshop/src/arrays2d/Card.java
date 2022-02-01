package arrays2d;

public class Card {
	private String suit;
	private int num;
	public Card(String suit, int num) {
		this.num=num;
		this.suit=suit;
	}
	
	public String toString() {
		return num+" "+suit;
	}
	
	public int getVal() {
		return num;
	}
	
}
