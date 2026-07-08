package test;

import generalcuts.Instance;
import generalcuts.MasterModel;
import generalcuts.Controller;

public class EntryPoint
{
	private static String _version = "0.10";
	private static ArgMap _argmap;
	
	public static void main(String[] args)
	{
		_argmap = new ArgMap(args);

		if( _argmap.containsArg("-help") )
			showParameters();
		
		processParameters();
		Instance instance = getInstance(_argmap);
		
		// Runs procedure
		Controller controller = new Controller(instance);
		controller.run();

		System.out.println("Opt: " + integerOptimal(instance));
	}
	
	public static double integerOptimal(Instance instance)
	{
		double ret = Double.NEGATIVE_INFINITY;
		
		try
		{
			MasterModel integer = new MasterModel(instance, true);
			integer.create();
			integer.solve();
			ret = integer.getObjective();
			integer.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return ret;
	}
	
	private static void showParameters()
	{
		System.out.println("Hypercube cuts v" + _version);
		System.out.println("  -p    Problem to be solved [stab|cov]");
		System.out.println("  -n    Size of instance if random");
		System.out.println("  -d    Density of instance if random");
		System.out.println("  -s    Seed of instance if random");
		System.out.println("  -ms   Multiple runs with seed (-s) to (-s)+(-ms)");
		System.out.println("  -m    Number of elements if random covering");
		System.out.println("  -vc   Verbose controller");
		System.out.println("  -vg   Verbose cut generator");
		System.out.println("  -mn   Max size for |N|");
		System.out.println("  -at   Cutting attempts");
		System.out.println("  -if   Infeasibility function [class|card|weight|seq]");
		System.out.println("  -r    Rounding procedure [class|upper|target]");
		System.out.println("  -rp   Rounding probability for 1/2 variables (for classical rounding)");
		System.out.println("  -rt   Rounding target (for upper and targeted rounding)");
	}

	private static void processParameters()
	{
		generalcuts.MasterModel.setVerbose(_argmap.containsArg("-vm"));
		generalcuts.Controller.setVerbose(_argmap.containsArg("-vc"));
		generalcuts.Controller.setShowInequalities(_argmap.containsArg("-showcuts"));
		generalcuts.Controller.setCuttingAttempts(_argmap.intArg("-at", 20));
		generalcuts.Controller.setInfeasibilityFunction(_argmap.stringArg("-if", "class"));
		generalcuts.Controller.setRoundingProcedure(_argmap.stringArg("-r", "target"));
		generalcuts.CutGenerator.setMaxNsize(_argmap.intArg("-mn", 0));
		generalcuts.CutGenerator.setVerbose(_argmap.containsArg("-vg"));
		generalcuts.CutGenerator.setCplexLog(_argmap.containsArg("-cplexlog"));
		generalcuts.RounderClassical.setUpperRoundingProbabilityForOneHalf(_argmap.doubleArg("-rp", 1.0));
		generalcuts.RounderTargeted.setMaxRounding(_argmap.intArg("-rt", 7));
		generalcuts.RounderUpperTargeted.setMaxRounding(_argmap.intArg("-rt", 7));

		tailoredcuts.Controller.setVerbose(_argmap.containsArg("-vc"));
		tailoredcuts.Controller.setAggresiveWhenNotViolated(_argmap.containsArg("-awn"));
		tailoredcuts.CutGenerator.setMaxNsize(_argmap.intArg("-mn", 0));
		tailoredcuts.CutGenerator.setVerbose(_argmap.containsArg("-vg"));
		
		stableset.StableRounder.setUpperRoundingProbabilityForOneHalf(_argmap.doubleArg("-rp", 1.0));
		stableset.StableFunction.onlyRemoveVerticesWhenInfeasible(_argmap.containsArg("-rwi"));
		stableset.StableFunction.removeVerticesInOrder(_argmap.containsArg("-rio"));

		covering.CoveringRounder.setUpperRoundingProbabilityForOneHalf(_argmap.doubleArg("-rp", 0.0));

		// Tailored implementations
		if( _argmap.stringArg("-dep", "xxx").equals("stab") )
			StableMain.run(_argmap);

		if( _argmap.stringArg("-dep", "xxx").equals("cov") )
			CoveringMain.run(_argmap);
	}
	
	private static Instance getInstance(ArgMap _argmap)
	{
		if( _argmap.stringArg("-p", "xxx").equals("stab") )
			return Benchmark.stableSet(StableMain.erdosRenyi(_argmap.intArg("-n", 15), _argmap.doubleArg("-d", 0.4), _argmap.intArg("-s", 0)));
		
		throw new RuntimeException("Unkown problem type! -p " + _argmap.stringArg("-p", "xxx"));
	}
	
	public static String version()
	{
		return _version;
	}
	
	public static ArgMap argmap()
	{
		return _argmap;
	}
}
