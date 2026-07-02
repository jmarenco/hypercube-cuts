package generalcuts;

public class Instance 
{
	private int m;
	private int n;
	private double[][] A;
	private double[] b;
	private double[] c;
	
	public Instance(int constraints, int variables)
	{
		m = constraints;
		n = variables;
		A = new double[m][n];
		b = new double[m];
		c = new double[n];
	}
	
	public void setA(int i, int j, double coeff)
	{
		A[i][j] = coeff;
	}
	
	public void setB(int i, double value)
	{
		b[i] = value;
	}
	
	public void setC(int j, double value)
	{
		c[j] = value;
	}
	
	public int getCons()
	{
		return m;
	}
	
	public int getVars()
	{
		return n;
	}
	
	public double getA(int i, int j)
	{
		return A[i][j];
	}
	
	public double getB(int i)
	{
		return b[i];
	}
	
	public double getC(int j)
	{
		return c[j];
	}
}
