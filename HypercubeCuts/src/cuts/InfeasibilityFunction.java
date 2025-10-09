package cuts;

import java.util.ArrayList;

public interface InfeasibilityFunction
{
	public double get(Point x);
	public boolean feasible(Point x);
	public ArrayList<Inequality> compatibilityConstraints(Point xbar);
	public double getObjective(Point xfeas);
}
