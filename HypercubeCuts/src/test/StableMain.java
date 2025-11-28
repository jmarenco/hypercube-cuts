package test;

import stableset.Graph;
import stableset.StableFunction;
import stableset.StableModel;
import stableset.StableRounder;
import stableset.StableCallback;

import java.util.Random;

import cuts.Controller;
import cuts.CutGenerator;
import cuts.Point;

public class StableMain
{
	public static void run(ArgMap argmap)
	{
		// Statistics
		StableCallback callback = new StableCallback(argmap);
		CutGenerator.setCallback(callback);
		
		// Runs many instances
		for(int k=0; k < argmap.intArg("-ms", 1); ++k)
		{
			// Instance
			Graph G = erdosRenyi(argmap.intArg("-n", 15), argmap.doubleArg("-d", 0.4), argmap.intArg("-s", 0) + k);
			callback.set(G);
	
			// Runs procedure
			Controller controller = new Controller(new StableModel(G), new StableFunction(G), new StableRounder(G));
			controller.run();

			System.out.println("Opt: " + integerOptimal(G));
		}
		
		// Shows statistics
		callback.show();
	}

	public static int integerOptimal(Graph G)
	{
		StableModel integer = new StableModel(G, true);
		integer.create();
		Point optimum = integer.solve();
		integer.close();
		
		return optimum.positiveEntries();
	}
	
	public static Graph fourClique()
	{
		Graph G = new Graph(4);
		
		G.addEdge(0, 1);
		G.addEdge(0, 2);
		G.addEdge(0, 3);
		G.addEdge(1, 2);
		G.addEdge(1, 3);
		G.addEdge(2, 3);
		
		return G;
	}
	
	public static Graph test()
	{
		Graph G = new Graph(7);
		
		G.addEdge(0, 1);
		G.addEdge(0, 2);
		G.addEdge(0, 3);
		G.addEdge(1, 2);
		G.addEdge(1, 3);
		G.addEdge(2, 3);
		G.addEdge(0, 5);
		G.addEdge(2, 6);
		G.addEdge(3, 6);
		G.addEdge(5, 6);
		G.addEdge(2, 4);
		G.addEdge(3, 4);
		
		return G;
	}
	
	public static Graph erdosRenyi(int vertices, double probability, int seed)
	{
		Random random = new Random(seed);
		Graph G = new Graph(vertices);
		
		for(int i=0; i<vertices; ++i)
		for(int j=i+1; j<vertices; ++j) if( random.nextDouble() <= probability )
			G.addEdge(i, j);
		
		return G;
	}
}
