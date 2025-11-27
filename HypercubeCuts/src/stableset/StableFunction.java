package stableset;

import java.util.ArrayList;

import cuts.Inequality;
import cuts.InfeasibilityFunction;
import cuts.Point;

public class StableFunction implements InfeasibilityFunction
{
	private Graph G;
	private static boolean _onlyRemoveVerticesWhenInfeasible = false;
	private static boolean _removeVerticesInOrder = false;
	
	public StableFunction(Graph graph)
	{
		G = graph;
	}

	public double get(Point x, Point xbar)
	{
		if( x.size() != G.size() )
			throw new RuntimeException("Size of graph and point do not match!");
		
		double ret = 0;

		for(int i=0; i<G.size(); ++i)
		for(int j=i+1; j<G.size(); ++j) if( G.isEdge(i, j) )
			ret += Math.max(0, x.get(i) + x.get(j) - 1);
		
		if( ret > 0 && xbar != null && (_onlyRemoveVerticesWhenInfeasible == true || _removeVerticesInOrder == true) )
			ret = this.get(xbar, null);
		
		if( ret > 0 && xbar != null && _onlyRemoveVerticesWhenInfeasible == true )
		{
			for(int i=0; i<G.size(); ++i) if( x.get(i) > 0 && xbar.get(i) == 0 )
				ret += 1.0 / G.size();
		}
		
		if( ret > 0 && xbar != null && _removeVerticesInOrder == true )
		{
			for(int i=0; i<G.size(); ++i) if( x.get(i) == 0 && xbar.get(i) > 0 && conflictBefore(i,x) )
				ret += 1.0 / G.size();
		}
		
		return ret;
	}
	
	private boolean conflictBefore(int i, Point x)
	{
		for(int j=0; j<i-1; ++j)
		for(int k=j+1; k<i; ++k) if( G.isEdge(j,k) && x.get(j) > 0 && x.get(k) > 0 )
			return true;
				
		return false;
	}

	public boolean feasible(Point x)
	{
		return get(x, null) <= 0;
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
	
	public static void onlyRemoveVerticesWhenInfeasible(boolean value)
	{
		_onlyRemoveVerticesWhenInfeasible = value;
	}
	
	public static void removeVerticesInOrder(boolean value)
	{
		_removeVerticesInOrder = value;
	}
}
