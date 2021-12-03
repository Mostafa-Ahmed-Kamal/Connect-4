import java.util.LinkedList;

public class RootState implements State{
	
	byte[][] Board = new byte[6][7];
	LinkedList<State> children = new LinkedList<State>();
	
	public RootState(byte[][] Board) {
		for(int i=0 ; i<6 ; i++) {
			for(int j=0 ; j <7 ;j++) {
				this.Board[i][j] = Board[i][j];
			}
		}
	}
	public void setChildren(){
		// implement SetChildren
	}
	
	public byte[][] getBoard() {
		return Board;
	}
	
	public LinkedList<State> getChildren() {
		return children;
	}
}
