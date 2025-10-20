package stableset;

import cuts.CutFoundCallback;
import cuts.Inequality;
import cuts.Point;
import test.ArgMap;
import test.EntryPoint;

public class StableCallback implements CutFoundCallback
{
	private Graph G;
	private ArgMap _argmap;
	
	private int _generated = 0;
	private int _cliques = 0;
	private int _cycles = 0;
	private int _violated = 0;
	private int _totalSupport = 0;
	private int _minSupport = 1000;
	private int _maxSupport = 0;
	private int _totalDegree = 0;
	private int _minDegree = 1000;
	private int _maxDegree = 0;
	private double _totalRelativeSupport = 0;
	private double _totalViolation = 0;
	private double _totalDensity = 0;
	
	public StableCallback(ArgMap argmap)
	{
		_argmap = argmap;
	}

	public void set(Graph graph)
	{
		G = graph;
	}
	
	public void notify(Inequality inequality, Point xstar)
	{
		if( inequality == null )
			return;
		
		int[] d = degrees(inequality);
		
		_generated += 1;
		_violated += inequality.violated(xstar) ? 1 : 0;
		_totalViolation += inequality.violation(xstar);
		_totalSupport += inequality.supportSize();
		_minSupport = Math.min(_minSupport, inequality.supportSize());
		_maxSupport = Math.max(_maxSupport, inequality.supportSize());
		_totalRelativeSupport += inequality.supportSize() / (double)xstar.size();
		_totalDegree += sumNonzero(d);
		_minDegree = Math.min(_minDegree, minNonzero(d));
		_maxDegree = Math.max(_maxDegree, maxNonzero(d));
		_cliques += minNonzero(d) == inequality.supportSize()-1 ? 1 : 0;
		_cycles += maxNonzero(d) == 2 ? 1 : 0;
		_totalDensity += density(inequality, d);
	}
	
	private int[] degrees(Inequality inequality)
	{
		int[] ret = new int[G.size()];
		
		for(int i=0; i<G.size(); ++i)
		for(int j=i+1; j<G.size(); ++j) if( i != j && inequality.getLHS(i) != 0 && inequality.getLHS(j) != 0 && G.isEdge(i,j) )
		{
			ret[i] += 1;
			ret[j] += 1;
		}
		
		return ret;
	}
	
	private int maxNonzero(int[] degrees)
	{
		int ret = 0;
		for(int i=0; i<degrees.length; ++i) if( degrees[i] != 0 )
			ret = Math.max(ret, degrees[i]);
		
		return ret;
	}
	
	private int minNonzero(int[] degrees)
	{
		int ret = G.size();
		for(int i=0; i<degrees.length; ++i) if( degrees[i] != 0 )
			ret = Math.min(ret, degrees[i]);
		
		return ret;
	}

	private double sumNonzero(int[] degrees)
	{
		int sum = 0;
		for(int i=0; i<degrees.length; ++i) if( degrees[i] != 0 )
			sum += degrees[i];
		
		return sum;
	}
	
	private double density(Inequality inequality, int[] degrees)
	{
		int max = (inequality.supportSize() * (inequality.supportSize() - 1)) / 2;
		return sumNonzero(degrees) / (double)max;
	}
	
	public void show()
	{
		System.out.print("SC " + EntryPoint.version() + " | " + _generated + " gen, ");
		System.out.print(_cliques + " clq, ");
		System.out.print(_cycles + " cycles | ");
		System.out.print(_violated + " violated, ");
		System.out.print("Total: " + _totalViolation + " | ");
		System.out.print("Total support: " + _totalSupport + ", ");
		System.out.print("min: " + _minSupport + ", ");
		System.out.print("max: " + _maxSupport + ", ");
		System.out.print("total rel: " + _totalRelativeSupport + " | ");
		System.out.print("Total degree: " + _totalDegree + ", ");
		System.out.print("min: " + _minDegree + ", ");
		System.out.print("max: " + _maxDegree + " | ");
		System.out.print("Total density: " + _totalDensity + " | ");
		System.out.println(_argmap);
	}
}
