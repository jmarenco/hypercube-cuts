package generalcuts;

import java.util.ArrayList;

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
	
	@Override
	public ArrayList<Inequality> compatibilityConstraints()
	{
		ArrayList<Inequality> ret = new ArrayList<Inequality>();
		
		for(int j=0; j<_xbar.size(); ++j)
		{
			Inequality constraint = new Inequality(_xbar.size());
			constraint.setLHS(j, _xbar.get(j) > 0.5 ? -1 : 1);
			constraint.setRHS(0);
			ret.add(constraint);
		}
		
		return ret;
	}
}
