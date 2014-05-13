package org.ifi.p17.tho;

import java.util.ArrayList;
import java.util.List;

import org.ifi.p17.tho.data.Cell;
import org.ifi.p17.tho.data.MoveValue;

public class AIPlayer extends Player {

	private static int[] SCORES = {0, 2, 10, 50, 500, 10000, 1000000};
	private GameState gameState;
	private Cell lastMove;
	
	
	int bestRow = -1;
	int bestCol = -1;
	private MoveValue bestMove;

	public AIPlayer(int id) {
		super(id);
	}
	public void setGameState(GameState gs) {

		//gameState = new GameState(gs);
		bestMove = new MoveValue(Integer.MIN_VALUE, -1, -1);
		gameState = gs;
	}
	public MoveValue getBestMove(GameState gs, int alpha, int beta, int depth, int player){
		return maxValue(gs, alpha, beta, depth, player);
	}
	
	public MoveValue maxValue(GameState gs, int alpha, int beta, int depth, int player){
		int s = 0;
		List<Cell> nextMoves = generateMoves(gs);
//System.out.println("total moves:" + nextMoves.size());		
		if (nextMoves.isEmpty() || depth == 0) {
			s = evaluate(gs);
//System.out.println("max score evaluated : " + s);
			return new MoveValue( s, -1, -1 );
		}
		int bestRow = -1;
		int bestCol = -1;
		for (Cell move : nextMoves) {// current player is computer, maximize
			//GameState g = gs.getStateAfterMove(move.getY(), move.getX(), player);
			gs.set(move.getY(), move.getX(), player);
//			s = evaluate(gs);
//			if(s > SCORES[Gomoku.WINING_SIZE]){
//				return new int[] {s, move.getY(), move.getX()};
//			}
			s = minValue(gs, alpha, beta, depth - 1, getOpponent()).getValue();
			
//if(depth == 1){
//System.out.println("depth before:" + depth);			
//System.out.println("s:" + s);
//System.out.println("alpha before:" + alpha);
//}
			//undo move
			gs.set(move.getY(), move.getX(), GameState.NO_PLAYER);
			if (s > alpha) {
				alpha = s;
//System.out.println("alpha after:" + alpha);				
				bestRow = move.getY();
				bestCol = move.getX();				
			}
//System.out.println("alpha after after:" + alpha);
			if(alpha >= beta){	
				return new MoveValue( beta, bestRow, bestCol);
			}
		}
		return new MoveValue( alpha, bestRow, bestCol);
	}
	
	public MoveValue minValue(GameState gs, int alpha, int beta, int depth, int player){
		int s = 0;
		List<Cell> nextMoves = generateMoves(gs);
		if (nextMoves.isEmpty() || depth == 0) {
			s = evaluate(gs);
//System.out.println("min score evaluated : " + s);
			return new MoveValue( s, -1, -1 );
		}
		int bestRow = -1;
		int bestCol = -1;
		
		for (Cell move : nextMoves) {// current player is computer, maximize
			//GameState g = gs.getStateAfterMove(move.getY(), move.getX(), player);
			gs.set(move.getY(), move.getX(), player);
			s = maxValue(gs, alpha, beta, depth - 1, getId()).getValue();
			//undo move
			gs.set(move.getY(), move.getX(), GameState.NO_PLAYER);
//System.out.println("depth:" + depth);	
//if(depth == 1){
//System.out.println("s:" + s);
//System.out.println("beta before:" + beta);
//}
//System.out.println("beta before:" + beta);
//System.out.println("s:" + s);
			if (s < beta) {
//System.out.println("beta after:" + beta);				
				beta = s;
				bestRow = move.getY();
				bestCol = move.getX();
			}
			if(alpha >= beta){		
				return new MoveValue(alpha, bestRow, bestCol);
			}
		}
		return new MoveValue(beta, bestRow, bestCol);		
	}
	
	private int evaluate(GameState gameState) {
		int row = gameState.getMoveRow();
		int col = gameState.getMoveCol();
		if(row == -1){
			return 0;
		}
		/*if(gameState.endGame(gameState.getMoveRow(), gameState.getMoveCol())){
			if(gameState.getWinner() == getId()){
				return Integer.MAX_VALUE;
			}else{
				return Integer.MIN_VALUE;
			}
		}*/
		
		int value = 0;
		value += evaluateDiagonalAlternative(gameState);
		value += evaluateDiagonal(gameState);
		value += evaluate4x4(gameState);
		value += xxssxx(gameState);
		//value += evaluate5x5(gameState);
		value += evaluateRow(gameState);
		value += evaluateCol(gameState);	
		
		return value;
	}
	/**
	 * evaluate heuristics: N - 1 open: +1000 2* (N - 2) open: +1000 N - 3: +1
	 * 
	 * @return
	 */

	private int evaluateRow(GameState gameState) {
		int value = 0;
		int row = gameState.getMoveRow();
		int col = gameState.getMoveCol();
		int me = gameState.at(row, col);
		int oppo = (me == 1) ? 2 : 1;
		//row
		int x = col;
		int y = row;
		int leftCount = 0;
		int rightCount = 0;		
		int open = 0;
		while ( x > 0 && gameState.at(row, x - 1) == me ){
			leftCount++;
			x--;
		}
		x = col;
		while ( x < gameState.cols - 1 && gameState.at(row, x + 1) == me ){
			rightCount++;
			x++;
		}
			
		if(col - leftCount > 0 && gameState.at(row, col - 1 - leftCount) == 0){
			open++;
		}
		if(col + rightCount < gameState.cols - 1 && gameState.at(row, col + 1 + rightCount) == 0){
			open++;
		}	
		value += updateScore(leftCount + rightCount + 1, open);
		
		//check for opponent sequel if there is no my sequel	
		int oppoLeftCount = 0;
		int oppoRightCount = 0;
		if(leftCount == 0){			
			x = col;	
			while ( x > 0 && gameState.at(row, x - 1) == oppo ){
				oppoLeftCount++;
				x--;
			}
			//value += updateScore(count, 2)*2;
		}
		if(rightCount == 0){		
			x = col;	
			while ( x < gameState.cols - 1 && gameState.at(row, x + 1) == oppo ){
				oppoRightCount++;
				x++;
			}						
		}
		if(oppoLeftCount + oppoRightCount > 0){
			open = 0;
			if(col - oppoLeftCount > 0 && gameState.at(row, col - 1 - oppoLeftCount) == 0){
				open++;
			}
			if(col + oppoRightCount < gameState.cols - 1 && gameState.at(row, col + 1 + oppoRightCount) == 0){
				open++;
			}	
			value += updateScore(oppoLeftCount + oppoRightCount + 1, open);
		}				

		if(me == getId()){
			return value*gameState.getWeight(row, col);
		}else{
			return -value*gameState.getWeight(row, col);
		}
	}

	
	private int evaluateCol(GameState gameState) {
		int value = 0;
		int row = gameState.getMoveRow();
		int col = gameState.getMoveCol();
		int me = gameState.at(row, col);
		int oppo = (me == 1) ? 2 : 1;
		//row
		int x = col;
		int y = row;
		int upCount = 0;
		int downCount = 0;
		int open = 0;
		
		while ( y > 0 && gameState.at(y - 1, col) == me ){
			upCount++;
			y--;
		}
		y = row;
		while ( y < gameState.cols - 1 && gameState.at(y + 1, col) == me ){
			downCount++;
			y++;
		}
			
		if(row - upCount > 0 && gameState.at(row - 1 - upCount, col) == 0){
			open++;
		}
		if(row + downCount < gameState.cols - 1 && gameState.at(row + 1 + downCount, col) == 0){
			open++;
		}
		
		value += updateScore(upCount + downCount + 1, open);
		
		//check for opponent sequel if there is no my sequel
		int oppoUpCount = 0;
		int oppoDownCount = 0;
		if(upCount == 0){			
			y = row;	
			while ( y > 0 && gameState.at(y - 1, col) == oppo ){
				oppoUpCount++;
				y--;
			}			
		}
		if(downCount == 0){			
			y = row;	
			while ( y < gameState.cols - 1 && gameState.at(y + 1, col) == oppo ){
				oppoDownCount++;
				y++;
			}						
		}		
		if(oppoUpCount + oppoDownCount > 0){
			open = 0;
			if(row - oppoUpCount > 0 && gameState.at(row - 1 - oppoUpCount, col) == 0){
				open++;
			}
			if(row + oppoDownCount < gameState.cols - 1 && gameState.at(row + 1 + oppoDownCount, col) == 0){
				open++;
			}			
			value += updateScore(oppoUpCount + oppoDownCount + 1, open);
		}		
		if(me == getId()){
			return value*gameState.getWeight(row, col);
		}else{
			return -value*gameState.getWeight(row, col);
		}
	}

	private int evaluateDiagonal(GameState gameState) {
		int value = 0;
		int row = gameState.getMoveRow();
		int col = gameState.getMoveCol();
		int me = gameState.at(row, col);
		int oppo = (me == 1) ? 2 : 1;
		int y = row;
		int x = col;
		int leftCount = 0;
		int rightCount = 0;
		int open = 0;
		while(x > 0 && y > 0 && gameState.at(y - 1, x - 1) == me){
			leftCount++;
			x--;
			y--;
		}
		y = row;
		x = col;
		while(x < gameState.cols - 1 && y < gameState.rows - 1 && gameState.at(y + 1, x + 1) == me){
			rightCount++;
			x++;
			y++;
		}
		
		if(row - leftCount > 0 && col - leftCount > 0 && gameState.at(row - 1 - leftCount, col - 1 - leftCount) == 0){
			open++;
		}
		if(row + rightCount < gameState.cols - 1 && col + rightCount < gameState.cols - 1 && gameState.at(row + 1 + rightCount, col + 1 + rightCount) == 0){
			open++;
		}	
		value += updateScore(leftCount + rightCount + 1, open);
		
		//check for opponent sequel if there is no my sequel
		int oppoLeftCount = 0;
		int oppoRightCount = 0;
		
		if(leftCount == 0){			
			x = col;
			y = row;
			while ( x > 0 && y > 0 && gameState.at(y - 1, x - 1) == oppo ){
				oppoLeftCount++;
				x--;
				y--;
			}			
		}
		if(rightCount == 0){			
			x = col;
			y = row;
			while ( x < gameState.cols - 1 && y < gameState.rows - 1 && gameState.at(y + 1, x + 1) == oppo ){
				oppoRightCount++;
				x++;
				y++;
			}						
		}
		
		if(oppoLeftCount + oppoRightCount > 0){
			open = 0;
			if(row - oppoLeftCount > 0 && col - oppoLeftCount > 0 && gameState.at(row - 1 - oppoLeftCount, col - 1 - oppoLeftCount) == 0){
				open++;
			}
			if(row + oppoRightCount < gameState.cols - 1 && col + oppoRightCount < gameState.cols - 1 && gameState.at(row + 1 + oppoRightCount, col + 1 + oppoRightCount) == 0){
				open++;
			}	
			value += updateScore(oppoLeftCount + oppoRightCount + 1, open);
		}				
		
		if(me == getId()){
			return value*gameState.getWeight(row, col);
		}else{
			return -value*gameState.getWeight(row, col);
		}

	}	

	private int evaluateDiagonalAlternative(GameState gameState) {
		int value = 0;
		int row = gameState.getMoveRow();
		int col = gameState.getMoveCol();
		int me = gameState.at(row, col);
		int oppo = (me == 1) ? 2 : 1;
		int y = row;
		int x = col;
		int leftCount = 0;
		int rightCount = 0;
		int open = 0;
		while(x > 0 && y < gameState.rows - 1 && gameState.at(y + 1, x - 1) == me){
			leftCount++;
			x--;
			y++;
		}
		y = row;
		x = col;
		while(x < gameState.cols - 1 && y > 0 && gameState.at(y - 1, x + 1) == me){
			rightCount++;
			x++;
			y--;
		}
		
		if(col - leftCount > 0 && row + leftCount < gameState.rows - 1 && gameState.at(row + 1 + leftCount, col - 1 - leftCount) == 0){
			open++;
		}
		if(col + rightCount < gameState.cols - 1 && row - rightCount > 1 && gameState.at(row - 1 - rightCount, col + 1 + rightCount) == 0){
			open++;
		}	
		value += updateScore(leftCount + rightCount + 1, open);
		
		//check for opponent sequel if there is no my sequel
		int oppoLeftCount = 0;
		int oppoRightCount = 0;
		
		if(leftCount == 0){			
			x = col;
			y = row;
			while(x > 0 && y < gameState.rows - 1 && gameState.at(y + 1, x - 1) == oppo){
				oppoLeftCount++;
				x--;
				y++;
			}			
		}
		if(rightCount == 0){			
			x = col;
			y = row;
			while(x < gameState.cols - 1 && y > 0 && gameState.at(y - 1, x + 1) == oppo){
				oppoRightCount++;
				x++;
				y--;
			}						
		}
		if(oppoLeftCount + oppoRightCount > 0){
			open = 0;
			if(col - oppoLeftCount > 0 && row + oppoLeftCount < gameState.rows - 1 && gameState.at(row + 1 + oppoLeftCount, col - 1 - oppoLeftCount) == 0){
				open++;
			}
			if(col + oppoRightCount < gameState.cols - 1 && row - oppoRightCount > 1 && gameState.at(row - 1 - oppoRightCount, col + 1 + oppoRightCount) == 0){
				open++;
			}	
			value += updateScore(oppoLeftCount + oppoRightCount + 1, open);
		}				
		
		if(me == getId()){
			return value*gameState.getWeight(row, col);
		}else{
			return -value*gameState.getWeight(row, col);
		}

	}	

	private int evaluate4x4(GameState gameState){
		int value = 0;
		int row = gameState.getMoveRow();
		int col = gameState.getMoveCol();
		int me = gameState.at(row, col);
		int oppo = (me == 1) ? 2 : 1;
		int count = 0;
		//check for 4 horizon open left
		if(col > 3 && col < gameState.cols - 1 &&
			gameState.at(row, col + 1) ==  GameState.NO_PLAYER&&	
			gameState.at(row, col - 1) == me &&
			gameState.at(row, col - 2) == me && 
			gameState.at(row, col - 3) == me &&
			gameState.at(row, col - 4) == GameState.NO_PLAYER){
			count++;			
		}
		
		//check for 4 open left diagonal
		if(row > 3 && col > 3 && row < gameState.rows - 1 && col < gameState.cols - 1 &&
			gameState.at(row + 1, col + 1) ==  GameState.NO_PLAYER&&	
			gameState.at(row - 1, col - 1) == me &&
			gameState.at(row - 2, col - 2) == me &&
			gameState.at(row - 3, col - 3) == me &&
			gameState.at(row - 4, col - 4) == GameState.NO_PLAYER){
			
			count++;
		}		
		if(count >=2 ){
			//4x4 equal 5 open
			if(me == getId()){
				return updateScore(5, 2)*gameState.getWeight(row, col);
			}else{
				return -updateScore(5, 2)*gameState.getWeight(row, col);
			}
		}
		
		//check for 4 open right
		if(col > 0 && col < gameState.cols - 4 &&
			gameState.at(row, col - 1) ==  GameState.NO_PLAYER&&	
			gameState.at(row, col + 1) == me &&
			gameState.at(row, col + 2) == me && 
			gameState.at(row, col + 3) == me &&
			gameState.at(row, col + 4) == GameState.NO_PLAYER){
			count++;
		}
		
		if(count >=2 ){
			//4x4 equal 5 open
			if(me == getId()){
				return updateScore(5, 2)*gameState.getWeight(row, col);
			}else{
				return -updateScore(5, 2)*gameState.getWeight(row, col);
			}
		}
		

		//check for 4 open right diagonal
		if(row > 0 && col > 0 && row < gameState.rows - 4 && col < gameState.cols - 4 &&
			gameState.at(row - 1, col - 1) ==  GameState.NO_PLAYER&&	
			gameState.at(row + 1, col + 1) == me &&
			gameState.at(row + 2, col + 2) == me &&
			gameState.at(row + 3, col + 3) == me &&
			gameState.at(row + 4, col + 4) == GameState.NO_PLAYER){
			
			count++;
		}		
		if(count >=2 ){
			//4x4 equal 5 open
			if(me == getId()){
				return updateScore(5, 2)*gameState.getWeight(row, col);
			}else{
				return -updateScore(5, 2)*gameState.getWeight(row, col);
			}
		}

		//check for 4 vertical open up
		if(row > 3 && row < gameState.rows - 1 &&
			gameState.at(row + 1, col) ==  GameState.NO_PLAYER&&	
			gameState.at(row - 1, col) == me &&
			gameState.at(row - 2, col) == me && 
			gameState.at(row - 3, col) == me &&
			gameState.at(row - 4, col) == GameState.NO_PLAYER){
			count++;			
		}
		
		if(count >=2 ){
			//4x4 equal 5 open
			if(me == getId()){
				return updateScore(5, 2)*gameState.getWeight(row, col);
			}else{
				return -updateScore(5, 2)*gameState.getWeight(row, col);
			}
		}

		//check for 4 vertical open down
		if(row > 0 && row < gameState.rows - 4 &&
			gameState.at(row - 1, col) ==  GameState.NO_PLAYER&&	
			gameState.at(row + 1, col) == me &&
			gameState.at(row + 2, col) == me && 
			gameState.at(row + 3, col) == me &&
			gameState.at(row + 4, col) == GameState.NO_PLAYER){
			count++;			
		}
		
		if(count >=2 ){
			//4x4 equal 5 open
			if(me == getId()){
				return updateScore(5, 2)*gameState.getWeight(row, col);
			}else{
				return -updateScore(5, 2)*gameState.getWeight(row, col);
			}
		}
		
		//check for 4 open left diagonal alternative
		if(row > 0 && col > 3 && row < gameState.rows - 4 && col < gameState.cols - 1 &&
			gameState.at(row - 1, col + 1) ==  GameState.NO_PLAYER&&	
			gameState.at(row + 1, col - 1) == me &&
			gameState.at(row + 2, col - 2) == me &&
			gameState.at(row + 3, col - 3) == me &&
			gameState.at(row + 4, col - 4) == GameState.NO_PLAYER){
			
			count++;
		}		
		if(count >=2 ){
			//4x4 equal 5 open
			if(me == getId()){
				return updateScore(5, 2)*gameState.getWeight(row, col);
			}else{
				return -updateScore(5, 2)*gameState.getWeight(row, col);
			}
		}

		//check for 4 open right diagonal alternative
		if(row > 3 && col > 0 && row < gameState.rows - 1 && col < gameState.cols - 4 &&
			gameState.at(row + 1, col - 1) ==  GameState.NO_PLAYER&&	
			gameState.at(row - 1, col + 1) == me &&
			gameState.at(row - 2, col + 2) == me &&
			gameState.at(row - 3, col + 3) == me &&
			gameState.at(row - 4, col + 4) == GameState.NO_PLAYER){
			
			count++;
		}		
		if(count >=2 ){
			//4x4 equal 5 open
			if(me == getId()){
				return updateScore(5, 2)*gameState.getWeight(row, col);
			}else{
				return -updateScore(5, 2)*gameState.getWeight(row, col);
			}
		}
		//check for 5 horizon half open left
		if(col > 4 && col < gameState.cols - 1 &&
			gameState.at(row, col + 1) ==  GameState.NO_PLAYER&&	
			gameState.at(row, col - 1) == me &&
			gameState.at(row, col - 2) == me && 
			gameState.at(row, col - 3) == me &&
			gameState.at(row, col - 4) == me &&
			gameState.at(row, col - 5) == oppo ){
			count++;			
		}
		
		if(count >=2 ){
			//4x4 equal 5 open
			if(me == getId()){
				return updateScore(5, 2)*gameState.getWeight(row, col);
			}else{
				return -updateScore(5, 2)*gameState.getWeight(row, col);
			}
		}

		//check for 5 half open right
		if(col > 0 && col < gameState.cols - 5 &&
			gameState.at(row, col - 1) ==  GameState.NO_PLAYER&&	
			gameState.at(row, col + 1) == me &&
			gameState.at(row, col + 2) == me && 
			gameState.at(row, col + 3) == me &&
			gameState.at(row, col + 4) == me &&
			gameState.at(row, col + 5) == oppo){
			count++;
		}

		if(count >=2 ){
			//4x4 equal 5 open
			if(me == getId()){
				return updateScore(5, 2)*gameState.getWeight(row, col);
			}else{
				return -updateScore(5, 2)*gameState.getWeight(row, col);
			}
		}
		
		//check for 5 vertical half open up
		if(row > 4 && row < gameState.rows - 1 &&
			gameState.at(row + 1, col) ==  GameState.NO_PLAYER&&	
			gameState.at(row - 1, col) == me &&
			gameState.at(row - 2, col) == me && 
			gameState.at(row - 3, col) == me &&
			gameState.at(row - 4, col) == me &&
			gameState.at(row - 5, col) == oppo){
			count++;			
		}

		if(count >=2 ){
			//4x4 equal 5 open
			if(me == getId()){
				return updateScore(5, 2)*gameState.getWeight(row, col);
			}else{
				return -updateScore(5, 2)*gameState.getWeight(row, col);
			}
		}

		//check for 4 vertical open down
		if(row > 0 && row < gameState.rows - 5 &&
			gameState.at(row - 1, col) ==  GameState.NO_PLAYER&&	
			gameState.at(row + 1, col) == me &&
			gameState.at(row + 2, col) == me && 
			gameState.at(row + 3, col) == me &&
			gameState.at(row + 4, col) == me &&
			gameState.at(row + 5, col) == oppo){
			count++;			
		}
		
		if(count >=2 ){
			//4x4 equal 5 open
			if(me == getId()){
				return updateScore(5, 2)*gameState.getWeight(row, col);
			}else{
				return -updateScore(5, 2)*gameState.getWeight(row, col);
			}
		}

		//check for 5 half open left diagonal
		if(row > 4 && col > 4 && row < gameState.rows - 1 && col < gameState.cols - 1 &&
			gameState.at(row + 1, col + 1) ==  GameState.NO_PLAYER&&	
			gameState.at(row - 1, col - 1) == me &&
			gameState.at(row - 2, col - 2) == me &&
			gameState.at(row - 3, col - 3) == me &&
			gameState.at(row - 4, col - 4) == me &&
			gameState.at(row - 5, col - 5) == oppo){
			
			count++;
		}		
		if(count >=2 ){
			//4x4 equal 5 open
			if(me == getId()){
				return updateScore(5, 2)*gameState.getWeight(row, col);
			}else{
				return -updateScore(5, 2)*gameState.getWeight(row, col);
			}
		}

		//check for 5 half open right diagonal
		if(row > 0 && col > 0 && row < gameState.rows - 5 && col < gameState.cols - 5 &&
			gameState.at(row - 1, col - 1) ==  GameState.NO_PLAYER&&	
			gameState.at(row + 1, col + 1) == me &&
			gameState.at(row + 2, col + 2) == me &&
			gameState.at(row + 3, col + 3) == me &&
			gameState.at(row + 4, col + 4) == me &&
			gameState.at(row + 5, col + 5) == oppo){
			
			count++;
		}		
		if(count >=2 ){
			//4x4 equal 5 open
			if(me == getId()){
				return updateScore(5, 2)*gameState.getWeight(row, col);
			}else{
				return -updateScore(5, 2)*gameState.getWeight(row, col);
			}
		}

		//check for 5 half open left diagonal alternative
		if(row > 0 && col > 4 && row < gameState.rows - 5 && col < gameState.cols - 1 &&
			gameState.at(row - 1, col + 1) ==  GameState.NO_PLAYER&&	
			gameState.at(row + 1, col - 1) == me &&
			gameState.at(row + 2, col - 2) == me &&
			gameState.at(row + 3, col - 3) == me &&
			gameState.at(row + 4, col - 4) == me &&
			gameState.at(row + 5, col - 5) == oppo){
			
			count++;
		}		
		if(count >=2 ){
			//4x4 equal 5 open
			if(me == getId()){
				return updateScore(5, 2)*gameState.getWeight(row, col);
			}else{
				return -updateScore(5, 2)*gameState.getWeight(row, col);
			}
		}

		//check for 5 half open right diagonal alternative
		if(row > 4 && col > 0 && row < gameState.rows - 1 && col < gameState.cols - 5 &&
			gameState.at(row + 1, col - 1) ==  GameState.NO_PLAYER&&	
			gameState.at(row - 1, col + 1) == me &&
			gameState.at(row - 2, col + 2) == me &&
			gameState.at(row - 3, col + 3) == me &&
			gameState.at(row - 4, col + 4) == me &&
			gameState.at(row - 5, col + 5) == oppo){
			
			count++;
		}		
		if(count >=2 ){
			//4x4 equal 5 open
			if(me == getId()){
				return updateScore(5, 2)*gameState.getWeight(row, col);
			}else{
				return -updateScore(5, 2)*gameState.getWeight(row, col);
			}
		}
		
		//check for opponent here
		count = 0;
		//check for 4 horizon open left
		if(col > 3 && col < gameState.cols - 1 &&
			gameState.at(row, col + 1) ==  GameState.NO_PLAYER&&	
			gameState.at(row, col - 1) == oppo &&
			gameState.at(row, col - 2) == oppo && 
			gameState.at(row, col - 3) == oppo &&
			gameState.at(row, col - 4) == GameState.NO_PLAYER){
			count++;			
		}
		
		//check for 4 open left diagonal
		if(row > 3 && col > 3 && row < gameState.rows - 1 && col < gameState.cols - 1 &&
			gameState.at(row + 1, col + 1) ==  GameState.NO_PLAYER&&	
			gameState.at(row - 1, col - 1) == oppo &&
			gameState.at(row - 2, col - 2) == oppo &&
			gameState.at(row - 3, col - 3) == oppo &&
			gameState.at(row - 4, col - 4) == GameState.NO_PLAYER){
			
			count++;
		}		
		if(count >=2 ){
			//4x4 equal 5 open
			if(me == getId()){
				return updateScore(5, 2)*gameState.getWeight(row, col);
			}else{
				return -updateScore(5, 2)*gameState.getWeight(row, col);
			}
		}
		
		//check for 4 open right
		if(col > 0 && col < gameState.cols - 4 &&
			gameState.at(row, col - 1) ==  GameState.NO_PLAYER&&	
			gameState.at(row, col + 1) == oppo &&
			gameState.at(row, col + 2) == oppo && 
			gameState.at(row, col + 3) == oppo &&
			gameState.at(row, col + 4) == GameState.NO_PLAYER){
			count++;
		}
		
		if(count >=2 ){
			//4x4 equal 5 open
			if(me == getId()){
				return updateScore(5, 2)*gameState.getWeight(row, col);
			}else{
				return -updateScore(5, 2)*gameState.getWeight(row, col);
			}
		}
		

		//check for 4 open right diagonal
		if(row > 0 && col > 0 && row < gameState.rows - 4 && col < gameState.cols - 4 &&
			gameState.at(row - 1, col - 1) ==  GameState.NO_PLAYER&&	
			gameState.at(row + 1, col + 1) == oppo &&
			gameState.at(row + 2, col + 2) == oppo &&
			gameState.at(row + 3, col + 3) == oppo &&
			gameState.at(row + 4, col + 4) == GameState.NO_PLAYER){
			
			count++;
		}		
		if(count >=2 ){
			//4x4 equal 5 open
			if(me == getId()){
				return updateScore(5, 2)*gameState.getWeight(row, col);
			}else{
				return -updateScore(5, 2)*gameState.getWeight(row, col);
			}
		}

		//check for 4 vertical open up
		if(row > 3 && row < gameState.rows - 1 &&
			gameState.at(row + 1, col) ==  GameState.NO_PLAYER&&	
			gameState.at(row - 1, col) == oppo &&
			gameState.at(row - 2, col) == oppo && 
			gameState.at(row - 3, col) == oppo &&
			gameState.at(row - 4, col) == GameState.NO_PLAYER){
			count++;			
		}
		
		if(count >=2 ){
			//4x4 equal 5 open
			if(me == getId()){
				return updateScore(5, 2)*gameState.getWeight(row, col);
			}else{
				return -updateScore(5, 2)*gameState.getWeight(row, col);
			}
		}

		//check for 4 vertical open down
		if(row > 0 && row < gameState.rows - 4 &&
			gameState.at(row - 1, col) ==  GameState.NO_PLAYER&&	
			gameState.at(row + 1, col) == oppo &&
			gameState.at(row + 2, col) == oppo && 
			gameState.at(row + 3, col) == oppo &&
			gameState.at(row + 4, col) == GameState.NO_PLAYER){
			count++;			
		}
		
		if(count >=2 ){
			//4x4 equal 5 open
			if(me == getId()){
				return updateScore(5, 2)*gameState.getWeight(row, col);
			}else{
				return -updateScore(5, 2)*gameState.getWeight(row, col);
			}
		}
		
		//check for 4 open left diagonal alternative
		if(row > 0 && col > 3 && row < gameState.rows - 4 && col < gameState.cols - 1 &&
			gameState.at(row - 1, col + 1) ==  GameState.NO_PLAYER&&	
			gameState.at(row + 1, col - 1) == oppo &&
			gameState.at(row + 2, col - 2) == oppo &&
			gameState.at(row + 3, col - 3) == oppo &&
			gameState.at(row + 4, col - 4) == GameState.NO_PLAYER){
			
			count++;
		}		
		if(count >=2 ){
			//4x4 equal 5 open
			if(me == getId()){
				return updateScore(5, 2)*gameState.getWeight(row, col);
			}else{
				return -updateScore(5, 2)*gameState.getWeight(row, col);
			}
		}

		//check for 4 open right diagonal alternative
		if(row > 3 && col > 0 && row < gameState.rows - 1 && col < gameState.cols - 4 &&
			gameState.at(row + 1, col - 1) ==  GameState.NO_PLAYER&&	
			gameState.at(row - 1, col + 1) == oppo &&
			gameState.at(row - 2, col + 2) == oppo &&
			gameState.at(row - 3, col + 3) == oppo &&
			gameState.at(row - 4, col + 4) == GameState.NO_PLAYER){
			
			count++;
		}		
		if(count >=2 ){
			//4x4 equal 5 open
			if(me == getId()){
				return updateScore(5, 2)*gameState.getWeight(row, col);
			}else{
				return -updateScore(5, 2)*gameState.getWeight(row, col);
			}
		}
		//check for 5 horizon half open left
		if(col > 4 && col < gameState.cols - 1 &&
			gameState.at(row, col + 1) ==  GameState.NO_PLAYER&&	
			gameState.at(row, col - 1) == oppo &&
			gameState.at(row, col - 2) == oppo && 
			gameState.at(row, col - 3) == oppo &&
			gameState.at(row, col - 4) == oppo &&
			gameState.at(row, col - 5) == me ){
			count++;			
		}
		
		if(count >=2 ){
			//4x4 equal 5 open
			if(me == getId()){
				return updateScore(5, 2)*gameState.getWeight(row, col);
			}else{
				return -updateScore(5, 2)*gameState.getWeight(row, col);
			}
		}

		//check for 5 half open right
		if(col > 0 && col < gameState.cols - 5 &&
			gameState.at(row, col - 1) ==  GameState.NO_PLAYER&&	
			gameState.at(row, col + 1) == oppo &&
			gameState.at(row, col + 2) == oppo && 
			gameState.at(row, col + 3) == oppo &&
			gameState.at(row, col + 4) == oppo &&
			gameState.at(row, col + 5) == me){
			count++;
		}

		if(count >=2 ){
			//4x4 equal 5 open
			if(me == getId()){
				return updateScore(5, 2)*gameState.getWeight(row, col);
			}else{
				return -updateScore(5, 2)*gameState.getWeight(row, col);
			}
		}
		
		//check for 5 vertical half open up
		if(row > 4 && row < gameState.rows - 1 &&
			gameState.at(row + 1, col) ==  GameState.NO_PLAYER&&	
			gameState.at(row - 1, col) == me &&
			gameState.at(row - 2, col) == me && 
			gameState.at(row - 3, col) == me &&
			gameState.at(row - 4, col) == me &&
			gameState.at(row - 5, col) == oppo){
			count++;			
		}

		if(count >=2 ){
			//4x4 equal 5 open
			if(me == getId()){
				return updateScore(5, 2)*gameState.getWeight(row, col);
			}else{
				return -updateScore(5, 2)*gameState.getWeight(row, col);
			}
		}

		//check for 4 vertical open down
		if(row > 0 && row < gameState.rows - 5 &&
			gameState.at(row - 1, col) ==  GameState.NO_PLAYER&&	
			gameState.at(row + 1, col) == oppo &&
			gameState.at(row + 2, col) == oppo && 
			gameState.at(row + 3, col) == oppo &&
			gameState.at(row + 4, col) == oppo &&
			gameState.at(row + 5, col) == oppo){
			count++;			
		}
		
		if(count >=2 ){
			//4x4 equal 5 open
			if(me == getId()){
				return updateScore(5, 2)*gameState.getWeight(row, col);
			}else{
				return -updateScore(5, 2)*gameState.getWeight(row, col);
			}
		}

		//check for 5 half open left diagonal
		if(row > 4 && col > 4 && row < gameState.rows - 1 && col < gameState.cols - 1 &&
			gameState.at(row + 1, col + 1) ==  GameState.NO_PLAYER&&	
			gameState.at(row - 1, col - 1) == oppo &&
			gameState.at(row - 2, col - 2) == oppo &&
			gameState.at(row - 3, col - 3) == oppo &&
			gameState.at(row - 4, col - 4) == oppo &&
			gameState.at(row - 5, col - 5) == me){
			
			count++;
		}		
		if(count >=2 ){
			//4x4 equal 5 open
			if(me == getId()){
				return updateScore(5, 2)*gameState.getWeight(row, col);
			}else{
				return -updateScore(5, 2)*gameState.getWeight(row, col);
			}
		}

		//check for 5 half open right diagonal
		if(row > 0 && col > 0 && row < gameState.rows - 5 && col < gameState.cols - 5 &&
			gameState.at(row - 1, col - 1) ==  GameState.NO_PLAYER&&	
			gameState.at(row + 1, col + 1) == oppo &&
			gameState.at(row + 2, col + 2) == oppo &&
			gameState.at(row + 3, col + 3) == oppo &&
			gameState.at(row + 4, col + 4) == oppo &&
			gameState.at(row + 5, col + 5) == me){
			
			count++;
		}		
		if(count >=2 ){
			//4x4 equal 5 open
			if(me == getId()){
				return updateScore(5, 2)*gameState.getWeight(row, col);
			}else{
				return -updateScore(5, 2)*gameState.getWeight(row, col);
			}
		}

		//check for 5 half open left diagonal alternative
		if(row > 0 && col > 4 && row < gameState.rows - 5 && col < gameState.cols - 1 &&
			gameState.at(row - 1, col + 1) ==  GameState.NO_PLAYER&&	
			gameState.at(row + 1, col - 1) == oppo &&
			gameState.at(row + 2, col - 2) == oppo &&
			gameState.at(row + 3, col - 3) == oppo &&
			gameState.at(row + 4, col - 4) == oppo &&
			gameState.at(row + 5, col - 5) == me){
			
			count++;
		}		
		if(count >=2 ){
			//4x4 equal 5 open
			if(me == getId()){
				return updateScore(5, 2)*gameState.getWeight(row, col);
			}else{
				return -updateScore(5, 2)*gameState.getWeight(row, col);
			}
		}

		//check for 5 half open right diagonal alternative
		if(row > 4 && col > 0 && row < gameState.rows - 1 && col < gameState.cols - 5 &&
			gameState.at(row + 1, col - 1) ==  GameState.NO_PLAYER&&	
			gameState.at(row - 1, col + 1) == oppo &&
			gameState.at(row - 2, col + 2) == oppo &&
			gameState.at(row - 3, col + 3) == oppo &&
			gameState.at(row - 4, col + 4) == oppo &&
			gameState.at(row - 5, col + 5) == me){
			
			count++;
		}		
		if(count >=2 ){
			//4x4 equal 5 open
			if(me == getId()){
				return updateScore(5, 2)*gameState.getWeight(row, col);
			}else{
				return -updateScore(5, 2)*gameState.getWeight(row, col);
			}
		}
		return 0;
	}
	private int xxssxx(GameState gameState){
		int row = gameState.getMoveRow();
		int col = gameState.getMoveCol();
		int me = gameState.at(row, col);
		int oppo = (me == 1) ? 2 : 1;
		int count = 0;
		int countTh = 0;

		//check for row
		if(col > 1 && col < GameState.BOARD_SIZE - 2 && gameState.at(row, col - 1)* gameState.at(row, col + 1) == 0){
			int zcol = (gameState.at(row, col - 1) == 0) ? col -1 : col +1;						
			int x = zcol -2;
			int be = 0;
			int th = 0;
			while (x <= zcol + 2){
				if(gameState.at(row, x) == me){
					be++;
				}else if(gameState.at(row, x) == oppo){
					th++;
				}
				x++;
			}
			if((be == 4 || th == 4)&& (zcol > 2 && zcol < GameState.BOARD_SIZE - 3 && 
					gameState.at(row, zcol - 3) * gameState.at(row, zcol + 3) == 0 )){
				if(be == 4){
					count++;
				}else{
					countTh++;
				}
			}			
		}
		
		//check for col
		if(row > 1 && row < GameState.BOARD_SIZE - 2 && gameState.at(row - 1, col)* gameState.at(row + 1, col) == 0){
			int zrow = (gameState.at(row - 1, col) == 0) ? row -1 : row +1;						
			int y = zrow -2;
			int be = 0;
			int th = 0;
			while (y <= zrow + 2){
				if(gameState.at(y, col) == me){
					be++;
				}else if(gameState.at(y, col) == oppo){
					th++;
				}
				y++;
			}
			if((be == 4 || th == 4)&& (zrow > 2 && zrow < GameState.BOARD_SIZE - 3 && 
					gameState.at(zrow - 3, col) * gameState.at(zrow + 3, col) == 0 )){
				if(be == 4){
					count++;
				}else{
					countTh++;
				}
			}			
		}
		
		if(count >=2 || countTh >= 2){
			//4x4 equal 5 open
			if(me == getId()){
				return updateScore(5, 2)*gameState.getWeight(row, col)*4;
			}else{
				return -updateScore(5, 2)*gameState.getWeight(row, col)*4;
			}
		}		
		
		//check for diagonal
		if(row > 1 && row < GameState.BOARD_SIZE - 2 &&
				col > 1 && col < GameState.BOARD_SIZE - 2 && 
				gameState.at(row - 1, col - 1)* gameState.at(row + 1, col + 1) == 0){
			int zcol = (gameState.at(row - 1, col - 1) == 0) ? col -1 : col +1;
			int zrow = (gameState.at(row - 1, col - 1) == 0) ? row -1 : row +1;
			int y = zrow -2;
			int x = zcol - 2;
			int be = 0;
			int th = 0;
			while (y <= zrow + 2){
				if(gameState.at(y, x) == me){
					be++;
				}else if(gameState.at(y, x) == oppo){
					th++;
				}
				y++;
				x++;
			}
			if((be == 4 || th == 4)&& (zrow > 2 && zrow < GameState.BOARD_SIZE - 3 &&
					zcol > 2 && zcol < GameState.BOARD_SIZE - 3 &&
					gameState.at(zrow - 3, zcol - 3) * gameState.at(zrow + 3, zcol + 3) == 0 )){
				if(be == 4){
					count++;
				}else{
					countTh++;
				}
			}			
		}
		
		if(count >=2 || countTh >= 2){
			//4x4 equal 5 open
			if(me == getId()){
				return updateScore(5, 2)*gameState.getWeight(row, col)*4;
			}else{
				return -updateScore(5, 2)*gameState.getWeight(row, col)*4;
			}
		}
		
		//check for diagonal alternative
		if(row > 1 && row < GameState.BOARD_SIZE - 2 &&
				col > 1 && col < GameState.BOARD_SIZE - 2 && 
				gameState.at(row + 1, col - 1)* gameState.at(row - 1, col + 1) == 0){
			int zcol = (gameState.at(row + 1, col - 1) == 0) ? col - 1 : col + 1;
			int zrow = (gameState.at(row + 1, col - 1) == 0) ? row + 1 : row - 1;
			int y = zrow - 2;
			int x = zcol + 2;
			int be = 0;
			int th = 0;
			while (y <= zrow + 2){
				if(gameState.at(y, x) == me){
					be++;
				}else if(gameState.at(y, x) == oppo){
					th++;
				}
				y++;
				x--;
			}
			if((be == 4 || th == 4)&& (zrow > 2 && zrow < GameState.BOARD_SIZE - 3 &&
					zcol > 2 && zcol < GameState.BOARD_SIZE - 3 &&
					gameState.at(zrow + 3, zcol - 3) * gameState.at(zrow - 3, zcol + 3) == 0 )){
				if(be == 4){
					count++;
				}else{
					countTh++;
				}
			}			
		}
		
		if(count >=2 || countTh >= 2){
			//4x4 equal 5 open
			if(me == getId()){
				return updateScore(5, 2)*gameState.getWeight(row, col)*4;
			}else{
				return -updateScore(5, 2)*gameState.getWeight(row, col)*4;
			}
		}		
		
		return 0;
	}
	
	private int evaluate5x5(GameState gameState){
		return 0;
	}
	
	private int updateScore(int numberOfPiece, int open) {
		//System.out.println("found " + numberOfPiece + " open: " + open );
				if (numberOfPiece >= Gomoku.WINING_SIZE) {
					return SCORES[Gomoku.WINING_SIZE];
				}

				if (numberOfPiece == 0 || open == 0) {
					return 0;
				}
				if(open == 2){
					return SCORES[numberOfPiece];
				}
				if(open == 1){
				return SCORES[numberOfPiece - 1]/10;
				}
				return 0;
			}
	
	private List<Cell> generateMoves(GameState gameState) {
		List<Cell> nextMoves = new ArrayList<Cell>();
		
//		if(gameState.getWinner() > 0){
//			return nextMoves;
//		}
		
		int[] bounds = getBounds(gameState);
		
		// check endGame Here
		for (int row = bounds[0]; row < bounds[2]; row++) {
			for (int col = bounds[1]; col < bounds[3]; col++) {
				if (gameState.at(row, col) == GameState.NO_PLAYER) {
					nextMoves.add(new Cell(row, col));
				}
			}
		}
		return nextMoves;
	}
	private int[] getBounds(GameState gs){
		//return new int[] {0, 0, gs.rows, gs.cols};
		
		int[] lower = getLowerBounds(gs);
		int[] upper = getUpperBounds(gs);
		int lowerRow = lower[0];
		int lowerCol = lower[1];
		
		int upperRow = upper[0];
		int upperCol = upper[1];
//System.out.println("bounds" + lowerRow + "," + lowerCol + "," +  upperRow + "," + upperCol);		
		lowerRow = (lowerRow > 2)?lowerRow - 2: 0;
		lowerCol = (lowerCol > 2)?lowerCol - 2: 0;
		
		upperRow = (upperRow < gs.rows - 2)? upperRow + 2: gs.rows;
		upperCol = (upperCol < gs.cols - 2)? upperCol + 2: gs.cols;
//System.out.println("bounds" + lowerRow + "," + lowerCol + "," +  upperRow + "," + upperCol);		
		return new int[] {lowerRow, lowerCol, upperRow, upperCol};	
	}
	
	private int[] getLowerBounds(GameState gs){
		int lowerCol = 0; 
		int lowerRow = 0;
		outter:
		for (int row = 0; row < gs.rows/2; row++) {
			for (int col = 0; col < gs.cols; col++) {
				lowerCol = col;
				lowerRow = row;	
				if(gs.at(row, col) > 0){
					break outter;
					//return new int[] {lowerRow, lowerCol};
				}
			}
		}
		outter2:
		for (int col = 0; col < gs.cols/2; col++) {
			for (int row = 0; row < gs.rows; row++) {
				if(gs.at(row, col) > 0){
					lowerRow = Math.min(lowerRow, row);
					lowerCol = Math.min(lowerCol, col);
					break outter2;
					//return new int[] {lowerRow, lowerCol};
				}				
			}
		}
		lowerRow = (lowerRow > gs.rows/2) ? gs.rows/2 : lowerRow;
		lowerCol = (lowerCol > gs.cols/2) ? gs.cols/2 : lowerCol;

		return new int[] {lowerRow, lowerCol};
	}	
	
	private int[] getUpperBounds(GameState gs){		
		int upperCol = 0; 
		int upperRow = 0;
		
		outter:
		for (int row = gs.rows - 1; row > gs.rows/2; row--) {
			for (int col = gs.cols - 1; col > 0; col--) {
				upperCol = col;
				upperRow = row;
				if(gs.at(row, col) > 0){
					break outter;
					//return new int[] {upperRow, upperCol};
				}
			}
		}
		
		outter2:
		for (int col = gs.cols - 1; col > gs.rows/2; col--) {
			for (int row = gs.rows - 1; row > 0; row--) {			
				if(gs.at(row, col) > 0){
					upperRow = Math.max(upperRow, row);
					upperCol = Math.max(upperCol, col);
					break outter2;
					//return new int[] {upperRow, upperCol};
				}
			}
		}
		upperRow = (upperRow < gs.rows/2) ? gs.rows/2 : upperRow;
		upperCol = (upperCol < gs.cols/2) ? gs.cols/2 : upperCol;
		
		return new int[] {upperRow, upperCol};
	}	
}
