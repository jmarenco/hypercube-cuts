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
		
		if( xpar == null )
			return get(x, xbar);
		
		int j = firstDecreasing(xpar);
		
//		System.out.println(" x = " + x + ", FD = " + j);
//		System.out.println(" xpar = " + xpar);
//		System.out.println(" xbar = " + xbar);
//		System.out.println(" get(xpar,xbar) = " + get(xpar,xbar));
		return x.get(j) != xpar.get(j) ? get(x,xbar) : get(xpar,xbar) + 0.001;
	}
	
	private int firstDecreasing(Point xpar)
	{
		for(int j=0; j<_instance.getVars(); ++j)
		{
			if( isIncreasing(j) && xpar.get(j) > 0.999 )
				return j;

			if( isDecreasing(j) && xpar.get(j) < 0.001 )
				return j;
		}
		
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
