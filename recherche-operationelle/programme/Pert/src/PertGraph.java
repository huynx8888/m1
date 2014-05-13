
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.StringTokenizer;

import org.apache.commons.collections15.map.HashedMap;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;

public class PertGraph {
	Graph<Sommet, String> graph;
	public Map<String, Edge> mEdges;
	private static final int INFINITY = Integer.MAX_VALUE;
	public Sommet start;
	private Sommet end;
	private int countVertices;
	
	private List<String> critiquePath = new ArrayList<String>();
	public PertGraph(){
		graph = new DirectedSparseGraph<Sommet, String>();
		mEdges = new HashedMap<String, Edge>();
	}
	
	public void createGraph(String filename){		
		FileReader file;
		
		Sommet sommet = new Sommet();
		countVertices = 1;
		sommet.setName(countVertices);
		sommet.setDatePlusTot(0);		
		start = sommet;
		graph.addVertex(sommet);
		
		try {
			file = new FileReader(filename);

			BufferedReader read = new BufferedReader(file);
			while (read.ready()) {
				String line = read.readLine();
				StringTokenizer tokens = new StringTokenizer(line);
				String name = tokens.nextToken();

				double dure = new Double(tokens.nextToken()).doubleValue();
				//graph.getE
				if(!tokens.hasMoreTokens()){
					countVertices += 1;
					Sommet s = new Sommet();
					s.setName(countVertices);
					s.setDatePlusTot(0);					
					graph.addVertex(s);
					Edge edge = new Edge();					
					edge.setDure(dure);
					edge.setMarge(INFINITY);
					edge.setTrueEdge(true);
					graph.addEdge(name, sommet, s);
					mEdges.put(name, edge);
				}else{					
					if (tokens.countTokens() == 1) {
						String preNodeName =  tokens.nextToken();
						Pair<Sommet> endPoint = graph.getEndpoints(preNodeName);
						Sommet endSommet = endPoint.getSecond();
						Sommet s = new Sommet();
						countVertices += 1;
						s.setName(countVertices);
						//s.setDatePlusTot(0);
						//s.setDatePlusTard(0);
						graph.addVertex(s);
						graph.addEdge(name, endSommet, s);
						Edge edge = new Edge();			
						edge.setDure(dure);
						edge.setMarge(INFINITY);
						edge.setTrueEdge(true);						
						mEdges.put(name, edge);
						// g.addEdge(tokens.nextToken(), name, cost);
					}else{						
						Sommet s = new Sommet();
						countVertices ++;
						s.setName(countVertices);
						Sommet s2 = new Sommet();
						countVertices ++;
						s2.setName(countVertices);
						
						graph.addVertex(s);
						graph.addVertex(s2);
						Edge edge = new Edge();			
						edge.setDure(dure);
						edge.setMarge(INFINITY);
						edge.setTrueEdge(true);
						mEdges.put(name, edge);
						graph.addEdge(name, s, s2);
						
						while (tokens.hasMoreTokens()){
							String preNodeName =  tokens.nextToken();
							Pair<Sommet> endPoint = graph.getEndpoints(preNodeName);
							Sommet endSommet = endPoint.getSecond();	
							// s.setDatePlusTot(0);
							// s.setDatePlusTard(0);
							graph.addVertex(s);
							name = preNodeName + "gia";
							graph.addEdge(name, endSommet, s);
							edge = new Edge();
							edge.setDure(0);
							edge.setMarge(INFINITY);
							edge.setTrueEdge(false);
							mEdges.put(name, edge);
							//graph.getPredecessors(endSommet);
						}
					}
				}
			}
			createEndPoint();
			read.close();
			//System.out.println(graph.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
	}
	
	private void createEndPoint(){
		Sommet sommet = new Sommet();
		countVertices++;
		sommet.setName(countVertices);
		sommet.setDatePlusTot(0);
		sommet.setDatePlusTard(0);
		end = sommet;
		graph.addVertex(sommet);
		
		Collection<Sommet> sommets = graph.getVertices();
		Iterator<Sommet> it = sommets.iterator();
		
		while(it.hasNext()){
			Sommet s = it.next();
			if(graph.getSuccessorCount(s) == 0 && !s.equals(end)){
				String name = s+"end";
				graph.addEdge(name, s, end);
				Edge edge = new Edge();
				edge.setDure(0);
				edge.setMarge(INFINITY);
				edge.setTrueEdge(false);
				mEdges.put(name, edge);			
			}
		}
	}
		
	public void BFS(){
		Sommet currentSommet = start;
		Queue<Sommet> queue = new LinkedList<Sommet>();
		queue.add(currentSommet);
		while (!queue.isEmpty()){
			Sommet curr = queue.poll();
			Collection<Sommet> successors = graph.getSuccessors(curr);
			Iterator<Sommet> it = successors.iterator();
			while(it.hasNext()){				
				Sommet s = it.next();
				double plusTot = s.getDatePlusTot();
				Collection<String> inEdges = graph.getInEdges(s);
				if (inEdges != null) {
					Iterator<String> itInEdges = inEdges.iterator();
					while (itInEdges.hasNext()) {
						String edgeName = itInEdges.next();
						Edge e = mEdges.get(edgeName);
						if (e.getDure() + curr.getDatePlusTot() > plusTot) {
							plusTot = e.getDure() + curr.getDatePlusTot();
						}
						s.setDatePlusTot(plusTot);
					}
				}
				
				queue.add(s);
			}			
		}
	}
	
	public void BFSInvert(){
		Sommet currentSommet = end;
		end.setDatePlusTard(end.getDatePlusTot());
		Queue<Sommet> queue = new LinkedList<Sommet>();
		queue.add(currentSommet);
		while (!queue.isEmpty()){
			Sommet curr = queue.poll();
			Collection<Sommet> preDeccessors = graph.getPredecessors(curr);
			Iterator<Sommet> it = preDeccessors.iterator();
			while(it.hasNext()){			
				Sommet s = it.next();
				double plusTard = s.getDatePlusTard();
				Collection<String> outEdges = graph.getOutEdges(s);
				Edge currentEdge = null;
				if (outEdges != null) {
					Iterator<String> itOutEdges = outEdges.iterator();
					while (itOutEdges.hasNext()) {												
						String edgeName = itOutEdges.next();
//System.out.println("Current: " + s.toString() + " " + s.getDatePlusTard());						
						Sommet parrent = graph.getEndpoints(edgeName).getSecond();
//System.out.println("Parrent: " + parrent.toString()  + " " + parrent.getDatePlusTard());						
						Edge e = mEdges.get(edgeName);
//System.out.println("Edge: " + e.toString() + " dure: " + e.getDure());						
						if (parrent.getDatePlusTard() - e.getDure() < plusTard) {
							plusTard = parrent.getDatePlusTard() - e.getDure() ;
						}
						if(graph.getEndpoints(edgeName).getSecond().equals(curr)){
							currentEdge = e;
						}
//System.out.println("plusTard: " + plusTard);						
						s.setDatePlusTard(plusTard);
						if(e.isTrueEdge() && (curr.getDatePlusTard() - s.getDatePlusTot() - e.getDure() == 0) && !critiquePath.contains(edgeName)){
							critiquePath.add(edgeName);
						}						
					}
					currentEdge.setMarge(curr.getDatePlusTard() - s.getDatePlusTot() - currentEdge.getDure());
				}
				queue.add(s);
			}			
		}
	}
	
	public void print(){
		//Collection<Sommet> coSommets = graph.getVertices();
		List<Sommet> sommets = new ArrayList<Sommet>(graph.getVertices());
		Iterator<Sommet> it = sommets.iterator();
		Collections.sort(sommets);
		while(it.hasNext()){
			Sommet sommet = it.next();
			System.out.println(sommet.toString() + "\t date au plus tôt: " + sommet.getDatePlusTot() + "\tdate au plus tard: " + sommet.getDatePlusTard());
			//pt += sommet.toString() + ":" + sommet.getDatePlusTot() + " ";
			//plusTard += sommet.toString() + ":" + sommet.getDatePlusTard() + " ";
			//System.out.println();
		}
		
//		System.out.println("Critique Path");
//		for (String edgeName : critiquePath) {
//			System.out.println(edgeName.toString());
//		}
		System.out.println();
		System.out.println("Chemin critique");
		for (int i = critiquePath.size() - 1; i >= 0; i--){
			System.out.println(critiquePath.get(i).toString());
		}
		System.out.println();
		System.out.println("Marges:");
		//Iterator<String> itEdge = mEdges.keySet().iterator();
		
		List <String> listEdge = new ArrayList<String>(mEdges.keySet());
		//listEdge.addAll(mEdges.keySet());
		Collections.sort(listEdge);
		Iterator<String> itEdge = listEdge.iterator();
		while (itEdge.hasNext()) {
			String edgeName = itEdge.next();
			Edge e = mEdges.get(edgeName);
			if(e.isTrueEdge()){
				System.out.println(edgeName + ": " + e.getMarge());
			}
		}
		
		//System.out.println(pt);
		//System.out.println(plusTard);
	}
	public static void main (String[] args){
		String filename = "case1.dat";
		if(args.length != 0){
			filename = args[0];
		}
		
		PertGraph pertGraph = new PertGraph();
		pertGraph.createGraph(filename);
		//pertGraph.print();
		pertGraph.BFS();
		pertGraph.BFSInvert();
		
//		for (String e : pertGraph.mEdges.keySet()) {
//			System.out.println(e);
//		}
		pertGraph.print();
//		SimpleGraphDraw sgv = new SimpleGraphDraw();
//		Layout<Sommet, String> layout = new CircleLayout<Sommet, String>(pertGraph.graph);
//		layout.setSize(new Dimension(300,300)); // sets the initial size of the space
//		// The BasicVisualizationServer<V,E> is parameterized by the edge types
//		BasicVisualizationServer<Sommet,String> vv = new BasicVisualizationServer<Sommet,String>(layout);
//		vv.setPreferredSize(new Dimension(350,350)); //Sets the viewing area size
//		
//		JFrame frame = new JFrame("Simple Graph View");
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.getContentPane().add(vv);
//		frame.pack();
//		frame.setVisible(true);		
	}
}
