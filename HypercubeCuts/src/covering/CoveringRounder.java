package covering;

import cuts.Point;
import cuts.Rounder;

import java.util.Random;

public class CoveringRounder implements Rounder
{
	private Matrix M;
	private static double _upperRoundingProbabilityForOneHalf = 0.0;
	
	public CoveringRounder(Matrix matrix)
	{
		M = matrix;
	}
	
	public Point round(Point xstar)
	{
		Random random = new Random(0);
		Point ret = new Point(xstar.size());
		
		for(int i=0; i<xstar.size(); ++i)
		{
			if( xstar.get(i) > 0.51 )
				ret.set(i, 1);
			else if( xstar.get(i) < 0.49 )
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
		if( M.sets() != xstar.size() )
			throw new RuntimeException("Asked to round a point incompatible with the number of sets");
		
		Point ret = new Point(xstar.size());
		
		for(int i=0; i<xstar.size(); ++i) if( ret.get(i) > 0 )
			ret.set(i, 1);
		
		return ret;
	}
}
