package test;

import cuts.Controller;

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
		
		if( _argmap.stringArg("-p", "xxx").equals("stab") )
			StableMain.run(_argmap);

		if( _argmap.stringArg("-p", "xxx").equals("cov") )
			CoveringMain.run(_argmap);
	}
	
	private static void showParameters()
	{
		System.out.println("Hypercube cuts v" + _version);
		System.out.println("  -p        Problem to be solved [stab|cov]");
		System.out.println("  -n        Size of instance if random");
		System.out.println("  -d        Density of instance if random");
		System.out.println("  -s        Seed of instance if random");
		System.out.println("  -vc       Verbose controller");
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
