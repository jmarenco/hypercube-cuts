package stableset;

public class Graph
{
	private boolean[][] A;
	
	public Graph(int size)
	{
		A = new boolean[size][size];
	}
	
	public void addEdge(int i, int j)
	{
		A[i][j] = true;
		A[j][i] = true;
	}
	
	public int size()
	{
		return A.length;
	}
	
	public boolean isEdge(int i, int j)
	{
		return A[i][j];
	}
}
