package generalcuts;

public class InfeasibilityFunctionFree extends InfeasibilityFunction
{
	public InfeasibilityFunctionFree(Instance instance, Point xbar)
	{
		super(instance, xbar);
	}

	public double get(Point x)
	{
		if( x.size() != _instance.getVars() )
			throw new RuntimeException("Size of instance and point do not match!");
		
		double ret = 0;
		for(int i=0; i<_instance.getCons(); ++i) if( isTarget(i) )
			ret -= slack(x,i);
		
		return ret;
	}
}
