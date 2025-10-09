package cuts;

import java.util.Arrays;

public class Point
{
	private double[] _x;
	
	public Point(int dimension)
	{
		_x = new double[dimension];
	}
	
	public Point(Point other)
	{
		_x = new double[other.size()];
		
		for(int i=0; i<other.size(); ++i)
			_x[i] = other.get(i);
	}
	
	public void set(int i, double value)
	{
		_x[i] = value;
	}
	
	public Point flip(int i)
	{
		if( this.get(i) < -0.001 )
			throw new RuntimeException("Flip(" + i + ") of non-boolean value " + _x[i]);
		
		if( this.get(i) > 1.001 )
			throw new RuntimeException("Flip(" + i + ") of non-boolean value " + _x[i]);
		
		if( this.get(i) > 0.001 && this.get(i) < 0.999 )
			throw new RuntimeException("Flip(" + i + ") of non-boolean value " + _x[i]);

		Point ret = new Point(this);
		ret.set(i, 1 - this.get(i));
		return ret;
	}
	
	public double get(int i)
	{
		return _x[i];
	}
	
	public int size()
	{
		return _x.length;
	}
	
	public int fractionalEntries()
	{
		int ret = 0;
		for(int i=0; i<_x.length; ++i) if( _x[i] > 0.001 && _x[i] < 0.999 )
			++ret;
		
		return ret;
	}
	
	public boolean integer()
	{
		return this.fractionalEntries() == 0;
	}
	
	public int positiveEntries()
	{
		int ret = 0;
		for(int i=0; i<_x.length; ++i) if( _x[i] > 0.001 )
			++ret;
		
		return ret;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(_x);
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;

		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		Point other = (Point) obj;
		return Arrays.equals(_x, other._x);
	}
	
	@Override public String toString()
	{
		String ret = "(";
		for(int i=0; i<_x.length; ++i)
			ret += (i > 0 ? ", " : "") + _x[i];
		
		return ret + ")";
	}
}
