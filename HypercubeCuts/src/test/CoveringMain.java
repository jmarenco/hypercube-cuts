package test;

import covering.Matrix;

import java.util.Random;

import covering.CoveringFunction;
import covering.CoveringModel;
import covering.CoveringRounder;

import cuts.Controller;
import cuts.Point;

public class CoveringMain
{
	public static void run(ArgMap argmap)
	{
		// Instance
		Matrix M = randomMatrix(argmap.intArg("-m", 5), argmap.intArg("-n", 7), argmap.doubleArg("-d", 0.4), argmap.intArg("-s", 0));
		
		// Runs procedure
		Controller controller = new Controller(new CoveringModel(M), new CoveringFunction(M), new CoveringRounder(M));
		controller.run();
	}

	public static int integerOptimal(Matrix M)
	{
		CoveringModel integer = new CoveringModel(M, true);
		integer.create();
		Point optimum = integer.solve();
		integer.close();
		
		return optimum.positiveEntries();
	}
	
	public static Matrix test()
	{
		Matrix M = new Matrix(3,4);
		
		M.set(0, 0);
		M.set(0, 1);
		M.set(1, 1);
		M.set(1, 2);
		M.set(2, 2);
		M.set(0, 3);
		M.set(2, 3);
		
		return M;
	}
	
	public static Matrix randomMatrix(int elements, int sets, double probability, int seed)
	{
		Random random = new Random(seed);
		Matrix M = new Matrix(elements, sets);
		
		for(int i=0; i<elements; ++i)
			M.set(i, random.nextInt(sets));
		
		for(int i=0; i<elements; ++i)
		for(int j=0; j<sets; ++j) if( random.nextDouble() <= probability )
			M.set(i, j);
		
		return M;
	}

}
