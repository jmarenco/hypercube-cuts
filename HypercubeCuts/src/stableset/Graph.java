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

	public int edges()
	{
		int ret = 0;
		
		for(int i=0; i<A.length; ++i)
		for(int j=i+1; j<A.length; ++j) if( isEdge(i,j) )
			++ret;

		return ret;
	}
}
