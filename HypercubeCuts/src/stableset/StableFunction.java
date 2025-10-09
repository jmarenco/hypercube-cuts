package stableset;

import java.util.ArrayList;

import cuts.Inequality;
import cuts.InfeasibilityFunction;
import cuts.Point;

public class StableFunction implements InfeasibilityFunction
{
	private Graph G;
	
	public StableFunction(Graph graph)
	{
		G = graph;
	}

	public double get(Point x)
	{
		if( x.size() != G.size() )
			throw new RuntimeException("Size of graph and point do not match!");
		
		double ret = 0;

		for(int i=0; i<G.size(); ++i)
		for(int j=i+1; j<G.size(); ++j) if( G.isEdge(i, j) )
			ret += Math.max(0, x.get(i) + x.get(j) - 1);
		
		return ret;
	}

	public boolean feasible(Point x)
	{
		return get(x) <= 0;
	}

	public ArrayList<Inequality> compatibilityConstraints(Point xbar)
	{
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
		double ret = 0;
		for(int i=0; i<xfeas.size(); ++i)
			ret += xfeas.get(i);
		
		return ret;
	}
}
