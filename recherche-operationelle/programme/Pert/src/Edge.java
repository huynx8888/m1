
public class Edge {
	private String nom;
	private double dure;
	private float datePlusTot;
	private float datePlusTard;
	private double marge;
	private boolean isTrueEdge;
	
	public Edge(){
		datePlusTot = 0;
		datePlusTard = 0;
		nom = "";
		marge = 0;
		isTrueEdge = false;
		//marge =
	}
	
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public double getDure() {
		return dure;
	}
	public void setDure(double dure) {
		this.dure = dure;
	}
	public float getDatePlusTot() {
		return datePlusTot;
	}
	public void setDatePlusTot(float datePlusTot) {
		this.datePlusTot = datePlusTot;
	}
	public float getDatePlusTard() {
		return datePlusTard;
	}
	public void setDatePlusTard(float datePlusTard) {
		this.datePlusTard = datePlusTard;
	}
	public double getMarge() {
		return marge;
	}
	public void setMarge(double marge) {
		this.marge = marge;
	}
	
	public boolean isTrueEdge() {
		return isTrueEdge;
	}
	public void setTrueEdge(boolean isTrueEdge) {
		this.isTrueEdge = isTrueEdge;
	}
	public boolean isInCritiquePath(){
		if(!isTrueEdge){
			return false;
		}else if (marge == 0){
			return true;
		}
		return false;
	}
	public String toString(){
		return nom;
	}
}
