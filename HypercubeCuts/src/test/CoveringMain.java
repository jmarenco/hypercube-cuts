package test;

import covering.Matrix;
import covering.CoveringFunction;
import covering.CoveringModel;
import covering.CoveringRounder;

import cuts.Controller;
import cuts.Point;

public class CoveringMain
{
	public static void run(String[] args)
	{
		// Instance
		Matrix M = test();
		
		// Runs procedure
		Controller controller = new Controller(new CoveringModel(M), new CoveringFunction(M), new CoveringRounder(M));
		controller.run();
		
		// Gets integer optimal solution
		System.out.println("Integer optimal value: " + integerOptimal(M));
	}

	private static int integerOptimal(Matrix M)
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
}
