package org.ifi.p17.tho.data;

import org.ifi.p17.tho.GameState;

public class MoveValue {
	private int value;
	private int row;
	private int col;
	
	public MoveValue(int value, int row, int col) {
		super();
		this.value = value;
		this.row = row;
		this.col = col;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public int getCol() {
		return col;
	}
	public void setCol(int col) {
		this.col = col;
	}
	
	public boolean isValidMove(){
		if(row < 0 || col < 0 || row > GameState.BOARD_SIZE - 1 || col > GameState.BOARD_SIZE -1){
			return false;
		}
		return true;
	}	
}
