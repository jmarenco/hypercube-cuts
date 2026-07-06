package generalcuts;

public class InfeasibilityFunctionClassical extends InfeasibilityFunction
{
	public InfeasibilityFunctionClassical(Instance instance, Point xbar)
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
		
		return ret;
	}
}
