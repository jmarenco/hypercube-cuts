package generalcuts;

public class InfeasibilityFunctionSequence extends InfeasibilityFunction
{
	public InfeasibilityFunctionSequence(Instance instance, Point xbar)
	{
		super(instance, xbar);
	}

	public double get(Point x)
	{
		if( x.size() != _instance.getVars() )
			throw new RuntimeException("Size of instance and point do not match!");
		
		if( feasible(x) == true )
			return 0;
		
		for(int j=0; j<_instance.getVars(); ++j)
		{
			if( isIncreasing(j) && x.get(j) > 0.999 )
				return (x.size() + 1 - j);

			if( isDecreasing(j) && x.get(j) < 0.001 )
				return (x.size() + 1 - j);
		}
			
		return 0;
	}
}
