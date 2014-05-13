package org.ifi.p17.tho;

import java.util.ArrayList;
import java.util.List;

public class Node {
	private Integer heuristic;
	private GameState gameState;
	private Node parent;
	private List<Node> children;
	
	public Node(){
		gameState = new GameState();
		children = new ArrayList<Node>();
	}
	public Node(GameState gs){
		gameState = gs;
		children = new ArrayList<Node>();
	}
	
	
	public int getHeuristic() {
		if(heuristic == null){
			//heuristic = new Integer();
		}
		return heuristic.intValue();
	}
	public GameState getGameState() {
		return gameState;
	}
	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}
	public void addChild(Node child){
		children.add(child);
	}
	public Node getParent() {
		return parent;
	}
	public void setParent(Node parent) {
		this.parent = parent;
	}
	public List<Node> getChildren() {
		return children;
	}
	public void setChildren(List<Node> children) {
		this.children = children;
	}	
}
