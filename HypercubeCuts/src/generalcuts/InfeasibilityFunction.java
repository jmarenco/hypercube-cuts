package generalcuts;

import java.util.ArrayList;
import java.util.stream.IntStream;

public abstract class InfeasibilityFunction
{
	protected Instance _instance;
	protected Point _xbar;
	
	private boolean[] _target; // Rows not satisfied by xbar
	private boolean[] _increasing; // Columns that increase the infeasibility function when approaching xbar
	private boolean[] _decreasing; // Columns that decrease the infeasibility function when approaching xbar
	
	public InfeasibilityFunction(Instance instance, Point xbar)
	{
		_instance = instance;
		_xbar = xbar;
		
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
	
	public abstract double get(Point x, Point xbar);
	public abstract boolean feasible(Point x);

	public ArrayList<Inequality> compatibilityConstraints(Point xbar)
	{
		ArrayList<Inequality> ret = new ArrayList<Inequality>();
		
		for(int j=0; j<xbar.size(); ++j) if( isIncreasing(j) || isDecreasing(j) )
		{
			Inequality constraint = new Inequality(xbar.size());
			constraint.setLHS(j, isIncreasing(j) ? -1 : 1);
			constraint.setRHS(0);
			ret.add(constraint);
		}
		
		return ret;
	}

	public double getObjective(Point xfeas)
	{
		return IntStream.range(0, _instance.getVars()).mapToDouble(j -> _instance.getC(j) * xfeas.get(j)).sum();
	}
	
	// b(row) - A(row,*) * x
	protected double slack(Point x, int row)
	{
		return _instance.getB(row) - IntStream.range(0, _instance.getVars()).mapToDouble(j -> _instance.getA(row, j) * x.get(j)).sum();
	}
	
	protected boolean isTarget(int row)
	{
		return _target == null || _target[row];
	}
	
	protected boolean isIncreasing(int column)
	{
		return _increasing[column];
	}
	
	protected boolean isDecreasing(int column)
	{
		return _decreasing[column];
	}
}
