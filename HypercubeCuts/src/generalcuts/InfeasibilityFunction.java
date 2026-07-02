package generalcuts;

import java.util.ArrayList;
import java.util.stream.IntStream;

public class InfeasibilityFunction
{
	private Instance _instance;
	private Point _cachedxbar; // Cached
	private boolean[] _target; // Rows not satisfied by xbar
	private boolean[] _increasing; // Columns that increase the infeasibility function when approaching xbar
	private boolean[] _decreasing; // Columns that decrease the infeasibility function when approaching xbar
	
	public InfeasibilityFunction(Instance instance)
	{
		_instance = instance;
	}

	public double get(Point x, Point xbar)
	{
		if( x.size() != _instance.getVars() )
			throw new RuntimeException("Size of instance and point do not match!");
		
		if( xbar != null )
			updateCache(xbar);
		
		double ret = 0;
		for(int i=0; i<_instance.getCons(); ++i) if( _target == null || _target[i] == true )
			ret += Math.max(0, -slack(x,i));
		
		return ret;
	}

	// b(row) - A(row,*) * x
	private double slack(Point x, int row)
	{
		return _instance.getB(row) - IntStream.range(0, _instance.getVars()).mapToDouble(j -> _instance.getA(row, j) * x.get(j)).sum();
	}

	public boolean feasible(Point x)
	{
		return get(x, null) <= 0;
	}

	public ArrayList<Inequality> compatibilityConstraints(Point xbar)
	{
		ArrayList<Inequality> ret = new ArrayList<Inequality>();
		
		for(int j=0; j<xbar.size(); ++j) if( _increasing[j] || _decreasing[j] )
		{
			Inequality constraint = new Inequality(xbar.size());
			constraint.setLHS(j, _increasing[j] ? -1 : 1);
			constraint.setRHS(0);
			ret.add(constraint);
		}
		
		return ret;
	}

	public double getObjective(Point xfeas)
	{
		return IntStream.range(0, _instance.getVars()).mapToDouble(j -> _instance.getC(j) * xfeas.get(j)).sum();
	}
	
	private void updateCache(Point xbar)
	{
		if( xbar.equals(_cachedxbar) )
			return;
		
		_cachedxbar = xbar;
		_target = new boolean[_instance.getCons()];
		_increasing = new boolean[_instance.getVars()];
		_decreasing = new boolean[_instance.getVars()];
		
		for(int i=0; i<_instance.getCons(); ++i)
			_target[i] = slack(xbar, i) < -0.001; // true if not satisfied
		
		for(int j=0; j<_instance.getVars(); ++j)
		{
			final int jf = j;
			double sumcol = IntStream.range(0, _instance.getCons()).mapToDouble(i -> _instance.getA(i,jf)).sum();
			
			_increasing[j] = (sumcol > 0 && xbar.get(j) > 0.999) || (sumcol < 0 && xbar.get(j) < 0.001);
			_decreasing[j] = (sumcol > 0 && xbar.get(j) < 0.001) || (sumcol < 0 && xbar.get(j) > 0.999);
		}
	}
}
