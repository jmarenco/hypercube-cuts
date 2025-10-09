package covering;

public class Matrix
{
	private int _elements;
	private int _sets;
	private boolean[][] _A;
	
	public Matrix(int elements, int sets)
	{
		_elements = elements;
		_sets = sets;
		_A = new boolean[elements][sets];
	}
	
	public void set(int i, int j)
	{
		_A[i][j] = true;
	}
	
	public boolean get(int i, int j)
	{
		return _A[i][j];
	}
	
	public int elements()
	{
		return _elements;
	}
	
	public int sets()
	{
		return _sets;
	}
}
