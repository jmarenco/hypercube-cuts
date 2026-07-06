package generalcuts;

public class InfeasibilityFunctionCardinality extends InfeasibilityFunction
{
	public InfeasibilityFunctionCardinality(Instance instance, Point xbar)
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
			ret += 0.001 * x.oneNorm();
			
		return ret;
	}
}
