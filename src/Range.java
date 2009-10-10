public class Range {
	double minT = 0d;
	double maxT = Double.MAX_VALUE;
	
	public Range(double minT, double maxT)
	{
		this.minT = minT;
		this.maxT = maxT;
	}

	public double getMinT() {
		return minT;
	}

	public void setMinT(double minT) {
		this.minT = minT;
	}

	public double getMaxT() {
		return maxT;
	}

	public void setMaxT(double maxT) {
		this.maxT = maxT;
	}
	
}
