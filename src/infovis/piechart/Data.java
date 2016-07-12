package infovis.piechart;

public class Data {
	
	public Pair number = new Pair();
	public Pair male = new Pair();
	public Pair female = new Pair();
	
	// birth information
	
	
	// death information
	
	
	public void setBirth(int male, int female, int number) {
		this.number.birth = number;
		this.male.birth = male;
		this.female.birth = female;
	}
	
	
	public void setDeath(int male, int female, int number) {
		this.number.death = number;
		this.male.death = male;
		this.female.death = female;
	}
	
	
	public String print() {
		String result = "number: " + number.print();
		result += "male: " + male.print();
		result += "female: " + female.print();
		return result;
	}
	
}
