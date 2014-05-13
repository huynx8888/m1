package org.ifi.p17.tho;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.ifi.p17.tho.data.Cell;
import org.ifi.p17.tho.io.GameStateFile;

public class GameState {
	private int state[][];
	private int weight[][];
	public static int BOARD_SIZE = 20;
	public static int NO_PLAYER = 0;
	public static int DRAW = 3;
	public static int X_PLAYER = 2;
	public static int O_PLAYER = 1;
	
	public int cols = BOARD_SIZE;
	public int rows = BOARD_SIZE;
	private List<Cell> seq = new ArrayList<Cell>();
	private int moveRow = -1;
	private int moveCol = -1;
	public boolean first = false;
	public int winner = 0;
	private int nextPlayer;
	
	public GameState(){
		initEmptyBoard();
		state = GameStateFile.loadStateFromFile();
	}
	
	public GameState(GameState gs){
		initEmptyBoard();
		for(int row = 0; row < rows; row++){
			for(int col = 0; col < cols; col++){
				state[col][row] = gs.at(col, row);
			}			
		}
	}
	
	private void initEmptyBoard(){
		state = new int[BOARD_SIZE][BOARD_SIZE];
		weight = new int[BOARD_SIZE][BOARD_SIZE];
		nextPlayer = NO_PLAYER;
		for(int row = 0; row < rows/2; row++){
			for(int col = 0; col < cols; col++){
				int z = Math.min(row, col);
				weight[row][col] = z + 1;
				weight[rows - row - 1][col] = z + 1;
			}
		}
	}
	
	public boolean endGame(int row, int col) {
		int lastPlayer = state[row][col];
		int y = row;		
		
		int count = 1;
		//row
		int x = col;
		while ( x > 0 && at(row, x - 1) == lastPlayer ){
			count++;
			x--;
		}
		x = col;
		while ( x < cols - 1 && at(row, x + 1) == lastPlayer ){
			count++;
			x++;
		}
		if(count >= Gomoku.WINING_SIZE){
			setWinner(lastPlayer);
			for(int i = 0; i < count; i++){
				//seq[i] = new Cell(row, x - i);
				seq.add(new Cell(row, x - i));
			}
			return true;
		}
		
		//col
		count = 1;
		y = row;
		while ( y > 0 && at(y - 1, col) == lastPlayer ){
			count++;
			y--;
		}
		y = row;
		while ( y < cols - 1 && at(y + 1, col) == lastPlayer ){
			count++;
			y++;
		}
		
		if(count >= Gomoku.WINING_SIZE){
			setWinner(lastPlayer);
			for(int i = 0; i < count; i++){				
				seq.add(new Cell(y - i, col));
			}			
			return true;
		}		
		
		
		//diagonal
		count = 1;
		y = row;
		x = col;		
		while(x > 0 && y > 0 && at(y - 1, x - 1) == lastPlayer){
			count++;
			x--;
			y--;
		}
		y = row;
		x = col;
		while(x < cols - 1 && y < rows - 1 && at(y + 1, x + 1) == lastPlayer){
			count++;
			x++;
			y++;
		}

		if(count >= Gomoku.WINING_SIZE){
			setWinner(lastPlayer);
			for(int i = 0; i < count; i++){				
				seq.add(new Cell(y - i, x - i));
			}						
			return true;
		}
		
		//alternative
		count = 1;
		y = row;
		x = col;		
		while(x > 0 && y < rows - 1 && at(y + 1, x - 1) == lastPlayer){
			count++;
			x--;
			y++;
		}
		y = row;
		x = col;
		while(x < cols - 1 && y > 0 && at(y - 1, x + 1) == lastPlayer){
			count++;
			x++;
			y--;
		}
		
		if(count >= Gomoku.WINING_SIZE){
			setWinner(lastPlayer);
			for(int i = 0; i < count; i++){
				seq.add(new Cell(y + i, x - i));
			}			
			return true;
		}
		return false;
	}
	public boolean endGame2(int row, int col) {		
		int lastPlayer = state[row][col];
		//int dir = 0;
		//int k;
		int count = 1;
		int x = col;
		int y = row;
		//check horizon
		while ( x > 0 && at(x - 1, y) == lastPlayer ){
			x--;
		}
		
		while ( x < cols - 1 && at(x + 1, y) == lastPlayer){
			count++;
			x++;
		}
		
		if (count >= Gomoku.WINING_SIZE){
			setWinner(lastPlayer);
			return true;
		}
		
		// then do the three counts with vertical components
		for (int dx = -1; dx <= 1; dx++) {
			x = col;
			y = row;
			while (y > 0 && x - dx >= 0 && x - dx < cols
					&& at(x - dx, y - 1) == lastPlayer) {
				x -= dx;
				y--;
			}
			count = 1;
			while (y < rows - 1 && x + dx >= 0
					&& x + dx < cols
					&& at(x + dx, y + 1) == lastPlayer) {
				count++;
				x += dx;
				y++;
			}
			
			if (count >= Gomoku.WINING_SIZE){
				setWinner(lastPlayer);
				return true;
			}
		}
		return false;
		
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		for(int row = 0; row < rows; row++){
			for(int col = 0; col < cols; col++){
				sb.append(at(row,col));
				sb.append(" ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public int[][] getState() {
		return state;
	}
	public void setState(int[][] state) {
		this.state = state;
	}	
	public int at(int row, int col){
		if(row < 0 || col < 0 || row > rows -1 || col > cols -1){
			return -1;
		}
		return state[row][col];
	}
	public void set(int row, int col, int player){
		state[row][col] = player;
		setMoveRow(row);
		setMoveCol(col);	
	}
	
	public GameState getStateAfterMove(int row, int col, int player){
		GameState gs = new GameState(this);
		gs.set(row, col, player);
		gs.setMoveRow(row);
		gs.setMoveCol(col);
		return gs;
	}
	public boolean getFirst(){
		return first;
	}

	public int getWinner() {
		return winner;
	}

	public void setWinner(int winner) {
		this.winner = winner;
	}

	public int getMoveRow() {
		return moveRow;
	}

	public void setMoveRow(int moveRow) {
		this.moveRow = moveRow;
	}

	public int getMoveCol() {
		return moveCol;
	}

	public void setMoveCol(int moveCol) {
		this.moveCol = moveCol;
	}
	public int getWeight(int row, int col){
		return weight[row][col];
	}

	public int getNextPlayer() {
		return nextPlayer;
	}

	public void setNextPlayer(int nextPlayer) {
		this.nextPlayer = nextPlayer;
	}

	public List<Cell> getWinningSequence() {
		return seq;
	}

	
}
