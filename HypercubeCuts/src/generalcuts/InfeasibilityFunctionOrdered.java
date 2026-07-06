package generalcuts;

public class InfeasibilityFunctionOrdered extends InfeasibilityFunction
{
	public InfeasibilityFunctionOrdered(Instance instance, Point xbar)
	{
		super(instance, xbar);
	}

	public double get(Point x, Point xbar)
	{
		if( x.size() != _instance.getVars() )
			throw new RuntimeException("Size of instance and point do not match!");
		
		double ret = 0;
		for(int i=0; i<_instance.getCons(); ++i) if( isTarget(i) )
			ret += Math.max(0, -slack(x,i));
		
		return ret;
	}
	
	public double get(Point x, Point xpar, Point xbar)
	{
		if( feasible(x) )
			return 0.0;
		
		int j = firstDifferingPositive(x, xbar);
		return x.get(j) != xpar.get(j) ? get(x,xbar) : get(xpar,xbar) + 0.001;
	}
	
	private int firstDifferingPositive(Point x, Point xbar)
	{
		for(int j=0; j<_instance.getVars(); ++j) if( positive(x,xbar,j) && x.get(j) != xbar.get(j) )
			return j;
		
		return -1;
	}

	private boolean positive(Point x, Point xbar, int j)
	{
		return isIncreasing(j) && x.get(j) != xbar.get(j);
	}

	public boolean feasible(Point x)
	{
		return get(x, null) <= 0;
	}
}
