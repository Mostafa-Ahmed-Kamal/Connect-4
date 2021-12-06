import java.util.LinkedList;

public class Solution {

	private BoardState root;
	private int[] choice;

	public Solution (BoardState root, int[] choice){
		this.root = root;
		this.choice = choice;
	}

	public BoardState getRoot() {
		return root;
	}

	public int[] getChoice() {
		return choice;
	}
}
