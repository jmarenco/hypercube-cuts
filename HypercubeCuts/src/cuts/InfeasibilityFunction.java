package cuts;

import java.util.ArrayList;

public interface InfeasibilityFunction
{
	public double get(Point x, Point xbar);
	public boolean feasible(Point x);
	public ArrayList<Inequality> compatibilityConstraints(Point xbar);
	public double getObjective(Point xfeas);

	public void resetAggresiveness();
	public void moreAggresive();
	public boolean isMaximumAggresive();
}
