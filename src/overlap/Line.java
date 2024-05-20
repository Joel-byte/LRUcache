package overlap;

public class Line {

	private int start, end;
	Line self;
	Line(int start, int end) {

		if(start < end){
			this.setStart(start);
			this.setEnd(end);
		}
		
		
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}
	
	public static String overlaps(Line one, Line two) {

		if(two.getStart() < one.getEnd())
			return "they overlap";
		
		return "they don't overlap";
	}

	public void setSelf(Line line){
		this.self = line;
	}

	public Line getSelf(){
		return this.self;
	}
	
	
	

}
