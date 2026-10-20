package generalcuts;

import java.util.stream.IntStream;

public class Inequality
{
	private double[] _lhs;
	private double _rhs;
	
	public Inequality(int variables)
	{
		_lhs = new double[variables];
	}
	
	public void setLHS(int j, double coefficient)
	{
		_lhs[j] = coefficient;
	}
	
	public void setRHS(double value)
	{
		_rhs = value;
	}

	public double getLHS(int j)
	{
		return _lhs[j];
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
		double base = smallestNonzero();
		
		for(int j=0; j<_lhs.length; ++j) if( Math.abs(_lhs[j]) > 0.001 )
		{
			if( _lhs[j] > 0 )
				ret += " + ";
			
			double coeff = _lhs[j] / base;
			
			if( Math.abs(coeff - 1) < 0.001 )
				ret += "x(" + j + ")";
			else if( Math.abs(coeff + 1) < 0.001 )
				ret += " - x(" + j + ")";
			else
				ret += " " + coeff + " x(" + j + ")";
		}
		
		return ret + " <= " + (_rhs / base);
	}
	
	private double smallestNonzero()
	{
		double smallest = IntStream.range(0, _lhs.length).mapToDouble(i -> Math.abs(_lhs[i])).filter(a -> a > 0.001).min().orElse(_rhs);
		return Math.min(smallest, Math.abs(_rhs) > 0.001 ? Math.abs(_rhs) : smallest);
	}
}