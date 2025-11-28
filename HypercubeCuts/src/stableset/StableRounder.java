package stableset;

import cuts.Point;
import cuts.Rounder;

import java.util.Random;

public class StableRounder implements Rounder
{
	private Graph G;
	private double _roundingThreshold;
	private double _upperRoundingProbabilityForOneHalf;
	
	private static double _initialRoundingThreshold = 0.01;
	private static double _initialUpperRoundingProbabilityForOneHalf = 1.0;
	
	public StableRounder(Graph graph)
	{
		G = graph;
		
		_roundingThreshold = _initialRoundingThreshold;
		_upperRoundingProbabilityForOneHalf = _initialUpperRoundingProbabilityForOneHalf;
	}
	
	public Point round(Point xstar)
	{
		Random random = new Random(0);
		Point ret = new Point(xstar.size());
		
		for(int i=0; i<xstar.size(); ++i)
		{
			if( xstar.get(i) > 0.5 + _roundingThreshold )
				ret.set(i, 1);
			else if( xstar.get(i) < 0.5 - _roundingThreshold )
				ret.set(i, 0);
			else if( random.nextDouble() <= _upperRoundingProbabilityForOneHalf )
				ret.set(i, 1);
			else
				ret.set(i, 0);
		}
		
		return ret;
	}
	
	public Point roundToFeasible(Point xstar)
	{
		if( G.size() != xstar.size() )
			throw new RuntimeException("Asked to round a point incompatible with the graph");
		
		Point ret = new Point(xstar.size());
		
		for(int i=0; i<xstar.size(); ++i)
		{
			boolean anyNeighbor = false;
			for(int j=0; j<xstar.size(); ++j) if( i != j && G.isEdge(i,j) && ret.get(j) > 0 )
				anyNeighbor = true;
			
			if( xstar.get(i) > 0.25 && anyNeighbor == false )
				ret.set(i, 1);
			else
				ret.set(i, 0);
		}
		
		return ret;
	}
	
	public static void setUpperRoundingProbabilityForOneHalf(double value)
	{
		_initialUpperRoundingProbabilityForOneHalf = value;
	}
	
	public void resetAggresiveness()
	{
		_roundingThreshold = _initialRoundingThreshold;
		_upperRoundingProbabilityForOneHalf = _initialUpperRoundingProbabilityForOneHalf;
	}

	public void moreAggresive()
	{
		if( _upperRoundingProbabilityForOneHalf < 0.99 )
			_upperRoundingProbabilityForOneHalf = 1;
		else if( _roundingThreshold < 0.05 )
			_roundingThreshold = 0.1;
		else if( _roundingThreshold < 0.4 )
			_roundingThreshold += 0.1;
	}

	public boolean isMaximumAggresive()
	{
		return _roundingThreshold >= 0.39;
	}
}
