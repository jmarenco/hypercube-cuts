package cuts;

public class Inequality
{
	private double[] _lhs;
	private double _rhs;
	
	public Inequality(int variables)
	{
		_lhs = new double[variables];
	}
	
	public void setLHS(int i, double coefficient)
	{
		_lhs[i] = coefficient;
	}
	
	public void setRHS(double value)
	{
		_rhs = value;
	}

	public double getLHS(int i)
	{
		return _lhs[i];
	}
	
	public double getRHS()
	{
		return _rhs;
	}
	
	public double violation(Point x)
	{
		double ret = 0;
		for(int i=0; i<x.size(); ++i)
			ret += _lhs[i] * x.get(i);
		
		return Math.max(0, ret - _rhs);
	}
	
	public boolean violated(Point x)
	{
		return violation(x) > 0.01;
	}
	
	public int supportSize()
	{
		int ret = 0;
		for(int i=0; i<_lhs.length; ++i) if( _lhs[i] != 0 )
			++ret;
		
		return ret;
	}
	
	@Override public String toString()
	{
		String ret = "";
		
		for(int i=0; i<_lhs.length; ++i) if( Math.abs(_lhs[i]) > 0.001 )
		{
			if( _lhs[i] > 0 )
				ret += " + ";
			
			ret += _lhs[i] + " x(" + i + ")";
		}
		
		return ret + " <= " + _rhs;
	}
}