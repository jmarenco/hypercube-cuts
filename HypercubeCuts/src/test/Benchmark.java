package test;

import generalcuts.Instance;
import stableset.Graph;
import covering.Matrix;

public class Benchmark
{
	public static Instance stableSet(Graph G)
	{
		Instance ret = new Instance(G.edges(), G.size());
		
		for(int i=0, k=0; i<G.size(); ++i)
		for(int j=i+1; j<G.size(); ++j) if( G.isEdge(i,j) )
		{
			ret.setA(k, i, 1.0);
			ret.setA(k, j, 1.0);
			ret.setB(k, 1.0);
			
			++k;
		}

		for(int i=0; i<G.size(); ++i)
			ret.setC(i, 1.0);
		
		return ret;
	}
	
	public static Instance setCovering(Matrix M)
	{
		Instance ret = new Instance(M.elements(), M.sets());
		
		for(int i=0; i<M.elements(); ++i)
		{
			for(int j=0; j<M.sets(); ++j) if( M.get(i, j) )
				ret.setA(i, j, -1.0);
			
			ret.setB(i, -1.0);
		}
		
		for(int j=0; j<M.sets(); ++j)
			ret.setC(j, -1.0);
		
		return ret;
	}
}
