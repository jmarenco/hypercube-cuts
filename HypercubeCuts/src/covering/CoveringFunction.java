package covering;

import java.util.ArrayList;

import cuts.Inequality;
import cuts.InfeasibilityFunction;
import cuts.Point;

public class CoveringFunction implements InfeasibilityFunction
{
	private Matrix M;
	
	public CoveringFunction(Matrix matrix)
	{
		M = matrix;
	}

	public double get(Point x, Point xbar)
	{
		if( x.size() != M.sets() )
			throw new RuntimeException("Number of sets and point do not match!");
		
		double ret = 0;

		for(int i=0; i<M.elements(); ++i)
		{
			boolean covered = false;
			for(int j=0; j<M.sets(); ++j) if( M.get(i,j) && x.get(j) >= 0.99 )
				covered = true;
			
			if( covered == false )
				++ret;
		}
		
		return ret;
	}

	public boolean feasible(Point x)
	{
		return get(x, null) <= 0;
	}

	public ArrayList<Inequality> compatibilityConstraints(Point xbar)
	{
		if( xbar.size() != M.sets() )
			throw new RuntimeException("Number of sets and point do not match!");

		ArrayList<Inequality> ret = new ArrayList<Inequality>();
		
		for(int i=0; i<xbar.size(); ++i)
		{
			Inequality constraint = new Inequality(xbar.size());
			constraint.setLHS(i, xbar.get(i) > 0 ? -1 : 1);
			constraint.setRHS(0);
			ret.add(constraint);
		}
		
		return ret;
	}

	public double getObjective(Point xfeas)
	{
		if( xfeas.size() != M.sets() )
			throw new RuntimeException("Number of sets and point do not match!");

		double ret = 0;
		for(int i=0; i<xfeas.size(); ++i)
			ret += xfeas.get(i);
		
		return ret;
	}

	public void resetAggresiveness()
	{
	}

	public void moreAggresive()
	{
	}
	
	public boolean isMaximumAggresive()
	{
		return true;
	}
}
