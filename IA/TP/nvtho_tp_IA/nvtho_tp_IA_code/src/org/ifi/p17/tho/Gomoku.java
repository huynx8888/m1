package org.ifi.p17.tho;

import java.awt.EventQueue;

public class Gomoku implements Runnable{
	public static int WINING_SIZE = 6;
	public static int player = 2;
	public static boolean humain = false;
	//public String directory[] = new String[] {"C:\\\\IFI", ""};
    public static void main (String[] args){    	
//    	if (args.length > 0) {
//    	    try {
//    	        player = Integer.parseInt(args[0]);
//    	    } catch (NumberFormatException e) {
//    	        System.err.println("Argument" + " must be an integer");
//    	        System.exit(1);
//    	    }
//    	}
    	if (args.length > 0) {
    	    try {
    	        humain = Boolean.parseBoolean(args[0]);
    	    } catch (NumberFormatException e) {
    	        System.err.println("Argument" + " must be an integer");
    	        System.exit(1);
    	    }
    	}    	
        EventQueue.invokeLater(new Gomoku());
    }

	public void run() {		
		GameState bs = new GameState();
		Board board = new Board(bs, player);
		board.updateBoard();
	}
}
