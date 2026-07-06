package generalcuts;

public class InfeasibilityFunctionWeighted extends InfeasibilityFunction
{
	public InfeasibilityFunctionWeighted(Instance instance, Point xbar)
	{
		super(instance, xbar);
	}

	public double get(Point x)
	{
		if( x.size() != _instance.getVars() )
			throw new RuntimeException("Size of instance and point do not match!");
		
		double ret = 0;
		for(int i=0; i<_instance.getCons(); ++i) if( isTarget(i) )
			ret += Math.max(0, -slack(x,i));
		
		if( feasible(x) == false )
			ret += 0.001 * weights(x);
			
		return ret;
	}
	
	public double weights(Point x)
	{
		double ret = 0;
		for(int j=0; j<x.size(); ++j)
			ret += (j+1) * Math.abs(x.get(j));
		
		return ret;
	}
}
