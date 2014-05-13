package org.ifi.p17.tho;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.ifi.p17.tho.data.Cell;
import org.ifi.p17.tho.data.MoveValue;
import org.ifi.p17.tho.io.GameStateFile;

public class Board extends JFrame implements ActionListener{
	private static int DIMENSION_X = 600;
	private static int DIMENSION_Y = 600;	
	private static String LABEL_PLAY = "Play";
	private static String LABEL_AUTOMATIC = "Automatic";
	private static Color X_COLOR = Color.blue;
	private static Color Y_COLOR = Color.red;
	private static Color[] COLORS = new Color[] {Color.black, X_COLOR, Y_COLOR, Color.green};
	private static int DEPTH = 1;
	private static String LABEL_MOVE_POSITION = "Last move: ";
	private static String LABEL_MOVE_TIME = "Time: ";
	private static String LABEL_MOVE_COUNT = "Move count: ";
	
	private GameState gameState;
	private JPanel gomokuBoard;
	private JPanel mainPanel;
	private JPanel eastPanel;
	private JButton buttonPlay;
	private JButton buttonAutomatic;
	
	private JLabel labelMovePosition = new JLabel(LABEL_MOVE_POSITION);
	private JLabel labelMovePositionValue = new JLabel();
	
	private JLabel labelMoveTime = new JLabel(LABEL_MOVE_TIME);
	private JLabel labelMoveTimeValue = new JLabel();

	private JLabel labelMoveCount = new JLabel(LABEL_MOVE_COUNT);
	private JLabel labelMoveCountValue = new JLabel();

	
	private boolean automatic = false;
	boolean firstPlay = false;
	
	private AIPlayer aiPlayer;
	
	int moveCount = 0;
	
	private int alpha = -Integer.MIN_VALUE;
	private int beta = Integer.MAX_VALUE;
	

	Board(GameState gameState, int player) {
		this.gameState = gameState;
		drawBoard();
		pack();
		setResizable(true);
		setLocationRelativeTo( null );
		setVisible(true);
			
		aiPlayer = new AIPlayer(player);
		//aiPlayer.setId(player);
		aiPlayer.setGameState(getBoardState());
	}
	
	Board(GameState gameState) {
		this.gameState = gameState;
		drawBoard();
		pack();
		setResizable(true);
		setLocationRelativeTo( null );
		setVisible(true);
		int player = 2;
		if(gameState.getFirst()){
			player = 1;
		}		
		aiPlayer = new AIPlayer(player);
		//aiPlayer.setId(player);
		aiPlayer.setGameState(getBoardState());
	}

	private void drawBoard() {
		Dimension boardSize = new Dimension(DIMENSION_X, DIMENSION_Y);
		
		this.setSize(boardSize);
		gomokuBoard = new JPanel();
		mainPanel = new JPanel(new BorderLayout());
		eastPanel = new JPanel(new GridBagLayout());
		this.add(mainPanel);
		mainPanel.add(gomokuBoard, BorderLayout.WEST);
		mainPanel.add(eastPanel, BorderLayout.EAST);
		//this.add(gomokuBoard);
		gomokuBoard.setLayout(new GridLayout(gameState.rows, gameState.cols));
		gomokuBoard.setPreferredSize(boardSize);
		gomokuBoard.setBounds(0, 0, boardSize.width, boardSize.height);

		for (int i = 0; i < gameState.rows * gameState.cols; i++) {
			JButton square = new JButton();
			square.setBorder(BorderFactory.createLineBorder(Color.black));
			gomokuBoard.add(square);
			square.setActionCommand(String.valueOf(i));
			square.setBackground(new Color(235, 237, 243));
			if(Gomoku.humain){
				square.addActionListener(new ActionListener() {					
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub						
						long t0 = System.currentTimeMillis();
						String command = e.getActionCommand();
						int index = Integer.parseInt(command);
						int col = index % gameState.cols;
						int row = index / gameState.cols;
						JButton button = (JButton)e.getSource();
						button.setEnabled(false);
						doMove(row, col, getHumainPlayer());
						buttonPlay.setEnabled(true);
					}
				});
			}
		}
		//JPanel panelButton = new JPanel();
		buttonAutomatic = new JButton(LABEL_AUTOMATIC);
		buttonPlay = new JButton(LABEL_PLAY);
		//panelButton.add(buttonPlay);
		//panelButton.add(buttonAutomatic);
		//labelDebug = new JLabel();
		//labelNofity = new JLabel();
		//eastPanel.add(labelNofity, BorderLayout.NORTH);
		//southPanel.add(labelDebug, BorderLayout.);
		//buttonPanel.add(buttonPlay, BorderLayout.SOUTH);
		GridBagConstraints contraints1 = new GridBagConstraints();
        contraints1.fill = GridBagConstraints.HORIZONTAL;
        contraints1.anchor = GridBagConstraints.NORTHWEST;
        contraints1.weightx = 1.0;
        contraints1.gridwidth = GridBagConstraints.REMAINDER;
        contraints1.insets = new Insets(1, 5, 1, 1);
        GridBagConstraints contraints2 = (GridBagConstraints)contraints1.clone();
        contraints2.weightx = 0.0;
        contraints2.gridwidth = 1;
        contraints2.insets = new Insets(1, 1, 1, 5);
                
		eastPanel.add(buttonPlay, contraints2);
		eastPanel.add(buttonAutomatic, contraints1);
		eastPanel.add(labelMovePosition, contraints2);
		eastPanel.add(labelMovePositionValue, contraints1);
		eastPanel.add(labelMoveTime, contraints2);
		eastPanel.add(labelMoveTimeValue, contraints1);
		eastPanel.add(labelMoveCount, contraints2);
		eastPanel.add(labelMoveCountValue, contraints1);
		
		buttonPlay.addActionListener(this);
		buttonAutomatic.addActionListener(this);
	}

	public void updateBoard(){
		int state[][] = gameState.getState();
		for (int row = 0; row < gameState.rows; row++){
			for (int col = 0; col < gameState.cols; col++){
				int value = state[row][col];
				if(value == GameState.X_PLAYER){
					setX(row, col);
				}else if (value == GameState.O_PLAYER){
					setO(row, col);
				}
			}
		}
		gomokuBoard.updateUI();
	}

	public void updateCell(int row, int col){
		JButton panel = (JButton) gomokuBoard.getComponent(row * GameState.BOARD_SIZE + col);
		panel.setForeground(COLORS[gameState.at(row, col)]);
		panel.setEnabled(true);
		panel.setFont(new Font(panel.getFont().getName(), Font.BOLD, 16));
		gomokuBoard.updateUI();
	}
	public void doMove(int row, int col, int player){
		//gameState.loadStateFromFile();
		if(row == -1){
			gameState.setWinner(GameState.DRAW);
		}
		if(gameState.at(row, col) > 0){
			//labelDebug.setText("invalid move: " + row + ", " + col);		
		}else{
			gameState.set(row, col, player);
		}
		GameStateFile.updateFile(gameState.getState());
		//updateCell(row, col);
		updateBoard();
		boolean end = gameState.endGame(row, col);
		if(gameState.getWinner() > 1){
			//labelDebug.setText("Game over: ");
		}
		int nextPlayer = (player == 1) ? 2 : 1;
		gameState.setNextPlayer(nextPlayer);
	}

	public void doMove(int row, int col){
		doMove(row, col, aiPlayer.getId());
	}
	//
	public void setX(int row, int col) {
		//JLabel x = new JLabel("X");
		JButton panel = (JButton) gomokuBoard.getComponent(row * GameState.BOARD_SIZE + col);
		panel.setForeground(COLORS[gameState.at(row, col)]);
		panel.setFont(new Font(panel.getFont().getName(), Font.BOLD, 16));
//		panel.removeAll();
//		panel.add(x);
		panel.setText("X");
		//gomokuBoard.updateUI();
	}
	public void setO(int row, int col) {
		//JLabel o = new JLabel("O");
		JButton panel = (JButton) gomokuBoard.getComponent(row * GameState.BOARD_SIZE + col);
		panel.setForeground(COLORS[gameState.at(row, col)]);
		panel.setFont(new Font(panel.getFont().getName(), Font.BOLD, 16));		
		panel.setText("O");
		//gomokuBoard.updateUI();
	}

	public GameState getBoardState() {
		return gameState;
	}

	public void setBoardState(GameState gameState) {
		this.gameState = gameState;
	}
		

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		//gameState.loadStateFromFile();
        if (LABEL_PLAY.equals(command)) {
        	playButtonPressed();
        }else if(LABEL_AUTOMATIC.equals(command)){
        	automaticButtonPressed();
        }
	}
	
	private void setLabelValues(int row, int col, long t, int count){
		labelMovePositionValue.setText(row + "," + col);
		labelMoveTimeValue.setText(t + "ms");
		labelMoveCountValue.setText("" + count);
	}
	private void playButtonPressed(){
		buttonPlay.setEnabled(false);
    	long t0 = System.currentTimeMillis();
    	//labelNofity.setText("I'm thinking");
    	
		//aiPlayer.setGameState(gameState);
		MoveValue albe = aiPlayer.maxValue(gameState, alpha, beta, DEPTH, aiPlayer.getId());
		//Cell c = aiPlayer.getBestMove(gameState, alpha, beta, DEPTH, aiPlayer.getId());
		long t1 = System.currentTimeMillis();
		if(moveCount == 0){
			adjustPlayer();
		}
		
		if(albe.isValidMove() && gameState.getWinner() == GameState.NO_PLAYER){
    		doMove(albe.getRow(), albe.getCol());
    		//labelNofity.setText("Do move in " + (t1 - t0) + "ms at: " + albe[1] + ", " + albe[2] + "with score = " + albe[0]);
    		//System.out.println("Do move in " + (t1 - t0) + "ms at: " + albe[1] + ", " + albe[2] + "with score = " + albe[0]);    		
    		moveCount++;
    		setLabelValues(albe.getRow(), albe.getCol(), t1 - t0, moveCount);
		}else{
			//labelDebug.setText("Do invalid move in " + (t1 - t0) + "ms at: " + albe[1] + ", " + albe[2] + "with score = " + albe[0]);
		}
        //labelNofity.setText("Opponent's tour");
    	Runnable r = new Runnable() {			
			@Override
			public void run() {
				// TODO Auto-generated method stub
		        while(true){
		        	int[] opponentMove = getOpponentMove();
		        	if(opponentMove[0] > -1){
		        		doMove(opponentMove[0], opponentMove[1], getOpponent());
		        		buttonPlay.setEnabled(true);
		        		break;
		        	}else if(opponentMove[0] == -1){
		        		try {
							Thread.sleep(1000);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
		        	}else{
		        		//labelNofity.setText("Invalid game state file");
System.out.println("Invalid game state file");
//		        		break;
		        	}
		        }				
			}
		};
		if(gameState.getWinner() == GameState.NO_PLAYER){
			new Thread(r).start();
		}
		if(gameState.getWinner() == aiPlayer.getId()){
			highlightWin(gameState.getWinningSequence());
			JOptionPane.showMessageDialog(null, "I win");
		}else if(gameState.getWinner() == getOpponent()){
			highlightWin(gameState.getWinningSequence());
			JOptionPane.showMessageDialog(null, "Opponent win");
		}else if(gameState.getWinner() == GameState.DRAW){
			JOptionPane.showMessageDialog(null, "Game over, draw!");
		}		
	}
	private void automaticButtonPressed(){
		buttonPlay.setEnabled(false);
		buttonAutomatic.setEnabled(false);
		automatic = true;
		if(moveCount == 0){
			adjustPlayer();
		}
		Runnable r = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(gameState.getWinner() == GameState.NO_PLAYER){
					int player = gameState.getNextPlayer();
					player = (player == GameState.NO_PLAYER) ? aiPlayer.getId(): player;
					//my turn
					if(player == aiPlayer.getId()){
				    	long t0 = System.currentTimeMillis();
				    	//labelNofity.setText("I'm thinking");
				    	
						//aiPlayer.setGameState(gameState);
						MoveValue albe = aiPlayer.getBestMove(gameState, alpha, beta, DEPTH, aiPlayer.getId());
						//Cell c = aiPlayer.getBestMove(gameState, alpha, beta, DEPTH, aiPlayer.getId());
						long t1 = System.currentTimeMillis();
						if(albe.getRow() >= 0){
				    		doMove(albe.getRow(), albe.getCol());
				    		//labelNofity.setText("Do move in " + (t1 - t0) + "ms at: " + albe[1] + ", " + albe[2] + "with score = " + albe[0]);
				    		//System.out.println("Do move in " + (t1 - t0) + "ms at: " + albe[1] + ", " + albe[2] + "with score = " + albe[0]);				    		
				    		moveCount++;
				    		setLabelValues(albe.getRow(), albe.getCol(), t1 - t0, moveCount);
						}else{
							//labelDebug.setText("Do invalid move in " + (t1 - t0) + "ms at: " + albe[1] + ", " + albe[2] + "with score = " + albe[0]);
						}						
					}else{//opponent's turn
						//labelNofity.setText("Opponent's turn");
			        	int[] opponentMove = getOpponentMove();
			        	if(opponentMove[0] > -1){
			        		doMove(opponentMove[0], opponentMove[1], getOpponent());			        		
			        	}else if(opponentMove[0] == -1){
			        		try {
								Thread.sleep(100);
							} catch (InterruptedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
			        	}else{
			        		//labelNofity.setText("Invalid game state file");
			        		//break;
			        	}						
					}	
				}
				
				if(gameState.getWinner() == aiPlayer.getId()){
					highlightWin(gameState.getWinningSequence());
					JOptionPane.showMessageDialog(null, "I win");
				}else if(gameState.getWinner() == getOpponent()){
					highlightWin(gameState.getWinningSequence());
					JOptionPane.showMessageDialog(null, "Opponent win");
				}else if(gameState.getWinner() == GameState.DRAW){
					JOptionPane.showMessageDialog(null, "Game over, draw!");
				}				
			}			
		};
		if (gameState.getWinner() == GameState.NO_PLAYER){
			new Thread(r).start();
		}
	}
	private int getHumainPlayer(){
		return aiPlayer.getOpponent();
	}
	private int getOpponent(){
		return aiPlayer.getOpponent();
	}
	private int[] getOpponentMove(){
		int[][] newState = GameStateFile.loadStateFromFile();
		int changed = 0;
		int[] move = new int[]{-1, -1};
		for(int row = 0; row < GameState.BOARD_SIZE; row++){
			for(int col = 0; col < GameState.BOARD_SIZE; col++){
				if(newState[row][col] == getOpponent() && gameState.at(row, col) == GameState.NO_PLAYER){
					move[0] = row;
					move[1] = col;
					changed++;
				}
			}
		}
		if(changed <= 1){
			return move;
		}
		return new int[]{-2, -2};
	}
	private void highlightWin(List<Cell> seq){
		for(Cell cell : seq){
			if(cell.getX() > -1){
				JButton panel = (JButton) gomokuBoard.getComponent(cell.getY() * GameState.BOARD_SIZE + cell.getX());
				panel.setForeground(Color.green);
				panel.setEnabled(true);
				panel.setFont(new Font(panel.getFont().getName(), Font.BOLD, 16));
			}
		}
	}
	private void adjustPlayer(){
		int count = 0;
		for(int row = 0; row < GameState.BOARD_SIZE; row++){
			for (int col = 0; col < GameState.BOARD_SIZE; col++){
				if(gameState.at(row, col) == GameState.X_PLAYER){
					count++;
				}
			}
		}
		if(count == 1){
			aiPlayer.setId(GameState.O_PLAYER);
		}
	}
}
