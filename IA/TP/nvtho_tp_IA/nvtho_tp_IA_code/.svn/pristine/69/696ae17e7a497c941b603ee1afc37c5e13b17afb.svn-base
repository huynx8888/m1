package org.ifi.p17.tho.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.ifi.p17.tho.GameState;
import org.ifi.p17.tho.Gomoku;

public class GameStateFile {
	private static String fileName;
	static{
		if(isWindows()){
			fileName = "C:\\IFI\\gomoku.txt";
		}else{
			fileName = "gomoku.txt";
		}
	}
	public static synchronized int[][] loadStateFromFile(){
		int[][] state = new int[GameState.BOARD_SIZE][GameState.BOARD_SIZE];
		try{
			File file = new File(fileName);
			if (!file.exists()){
				file.createNewFile();
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(state.toString());
				bw.close();				
			}else{
				Scanner scanner = new Scanner(new File(fileName));
				int i = 0;
				while(scanner.hasNextInt()){
					//bo
					int col = i % GameState.BOARD_SIZE;
					int row = i / GameState.BOARD_SIZE;
					state[row][col] = scanner.nextInt();
					i++;
				}
				scanner.close();
//System.out.println("load:" + toString());
			}
		}catch(FileNotFoundException fe){
			System.out.println("File Not Found Exception");
		}catch(IOException ie){
			System.out.println("IO Exception when read");
		}
		return state;
	}
	
	public static synchronized void updateFile(int[][] state) {
		try {
			File file = new File(fileName);
			file.createNewFile();
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(toString(state));
			bw.close();
			fw.close();			
//System.out.println("write: " + toString());
		} catch (IOException ie) {
			System.out.println("IO Exception when write file!");
		}
	}
	
	public static String toString(int[][] state){
		StringBuffer sb = new StringBuffer();
		for(int row = 0; row < GameState.BOARD_SIZE; row++){
			for(int col = 0; col < GameState.BOARD_SIZE; col++){
				sb.append(state[row][col]);
				sb.append(" ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}	

	private static boolean isWindows() {
		String OS = System.getProperty("os.name");
System.out.println(OS);
		return OS.indexOf("win") >= 0 || OS.indexOf("Win")  >= 0? true: false;
	}
}
