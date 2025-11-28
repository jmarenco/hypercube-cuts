package cuts;

public interface Rounder
{
	public Point round(Point xstar);
	public Point roundToFeasible(Point xstar);
	
	public void resetAggresiveness();
	public void moreAggresive();
	public boolean isMaximumAggresive();
}
