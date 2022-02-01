package arrays2d;

import java.util.Arrays;

public class Deck {
	private Card[] deck=new Card[52];
	public Deck() {
		String[] suits= {"clubs", "hearts", "diamonds", "spades"};
		for (int i=0; i<suits.length; i++) {
			for (int j=0; j<13; j++) {
				deck[i+j+12*i]=new Card(suits[i], j+1);
			}
		}
	}
	
	public String toString() {
		String s="";
		for (Card a:deck) 
			s+=a+", ";
		return s;
	}
	
	public Card getRandom() {
		return deck[(int) (Math.random()*52)];
	}
	
	public Card[] getFirstN(int n) {
		Card[] a=new Card[Math.max(Math.min(n, 52),0)];
		for (int i=0; i<a.length; i++) {
			a[i]=deck[i];
		}
		return a;
	}
	
	public void shuffle() {
//		Card[] temp=new Card[deck.length];
//		for (int i=temp.length-1; i>=0; i--) {
//			(int)(Math.random()*5+1)
//			temp[i]=deck[deck.length/(i%2+1)-i/2-1];
//		}
//		deck=temp;
	}
	
	public void sort() {
		for (int i=0; i<deck.length; i++) {
			int k=i;
			for (int j=i+1; j<deck.length; j++) {
				if (deck[j].getVal()<deck[i].getVal()) {
					k=j;
				}
			}
			Card temp=deck[i];
			deck[i]=deck[k];
			deck[k]=temp;
		}
	}
	
	public static void main (String[] args) {
		Deck a=new Deck();
//		System.out.println(a);
//		System.out.println(a.getRandom());
//		System.out.println(Arrays.toString(a.getFirstN(5)));
//		System.out.println(a);
		a.sort();
//		System.out.println(a);
		
	}
	
}
