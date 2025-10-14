package test;

import covering.CoveringRounder;
import cuts.Controller;
import cuts.CutGenerator;
import stableset.StableRounder;

public class EntryPoint
{
	private static String _version = "0.01";
	private static ArgMap _argmap;
	
	public static void main(String[] args)
	{
		_argmap = new ArgMap(args);

		if( _argmap.containsArg("-help") )
			showParameters();
		
		Controller.setVerbose(_argmap.containsArg("-vc"));
		CutGenerator.setMaxNsize(_argmap.intArg("-mn", 0));
		StableRounder.setUpperRoundingProbabilityForOneHalf(_argmap.doubleArg("-rp", 1.0));
		CoveringRounder.setUpperRoundingProbabilityForOneHalf(_argmap.doubleArg("-rp", 0.0));
		
		if( _argmap.stringArg("-p", "xxx").equals("stab") )
			StableMain.run(_argmap);

		if( _argmap.stringArg("-p", "xxx").equals("cov") )
			CoveringMain.run(_argmap);
	}
	
	private static void showParameters()
	{
		System.out.println("Hypercube cuts v" + _version);
		System.out.println("  -p    Problem to be solved [stab|cov]");
		System.out.println("  -n    Size of instance if random");
		System.out.println("  -d    Density of instance if random");
		System.out.println("  -s    Seed of instance if random");
		System.out.println("  -m    Number of elements if random covering");
		System.out.println("  -vc   Verbose controller");
		System.out.println("  -mn   Max size for |N|");
		System.out.println("  -rp   Rounding probability for 1/2 variables");
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
