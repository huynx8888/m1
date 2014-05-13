
public class Sommet implements Comparable<Sommet>{
	private int name;
	private double datePlusTot;
	private double datePlusTard;
	
	public Sommet(){
		datePlusTot = 0;
		datePlusTard = Double.MAX_VALUE;
	}
	public int getName() {
		return name;
	}
	public void setName(int name) {
		this.name = name;
	}
	public double getDatePlusTot() {
		return datePlusTot;
	}
	public void setDatePlusTot(double datePlusTot) {
		this.datePlusTot = datePlusTot;
	}
	public double getDatePlusTard() {
		return datePlusTard;
	}
	public void setDatePlusTard(double datePlusTard) {
		this.datePlusTard = datePlusTard;
	}
	public String toString(){
		return "V"+ name;
	}
	@Override
	public int compareTo(Sommet o) {
		return name - o.getName();
	}	
}
