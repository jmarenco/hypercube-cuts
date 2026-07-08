package generalcuts;

import test.EntryPoint;

public class Controller
{
	private Instance _instance;
	private MasterModel _master;
	private InfeasibilityFunction _f;
	private Rounder _rounder;
	
	private int _iterations = 0;
	private int _rounds = 0;
	private int _cuts = 0;
	private double _nsize = 0;
	private double _feasibles = 0;
	private double _supportsize = 0;
	private int _minsupport = Integer.MAX_VALUE;
	private int _maxsupport = 0;
	private double _violation = 0;
	private double _firstobj = Double.POSITIVE_INFINITY;
	private double _lastobj = 0;
	private double _time;
	private long _start;
	
	private static int _attempts = 20;
	private static boolean _verbose = false;
	private static boolean _showInequalities = false;
	private static String _infeasibilityFunction = "class";
	private static String _roundingProcedure = "class";
	
	public Controller(Instance instance)
	{
		_instance = instance;
		_master = new MasterModel(_instance);
		_rounder = createRounder();
	}
	
	public void run()
	{
		_start = System.currentTimeMillis();
		_master.create();
		
		boolean violated = true;
		int triesLeft = _attempts;
		
		while( triesLeft > 0 )
		{
			Point xstar = _master.solve();
			Point xbar = _rounder.round(xstar);
			
			_f = createInfeasibilityFunction(xbar);
			_firstobj = _firstobj == Double.POSITIVE_INFINITY ? _master.getObjective() : _firstobj;
			_lastobj = _master.getObjective();

			showPoints(xstar, xbar);
			
			CutGenerator cutter = new CutGenerator(xstar, xbar, _f);
			Inequality dv = cutter.generate();
			
			showResults(xstar, cutter, dv);

			violated = dv != null ? dv.violation(xstar) > 0.001 : false;
			_master.add(dv);

			_iterations += 1;
			_rounds += cutter.getRounds();
			_cuts += violated ? 1 : 0;
			_nsize += cutter.getNsize();
			_feasibles += cutter.getNfeasibles();
			_supportsize += dv != null ? dv.supportSize() : 0;
			_minsupport = dv != null ? Math.min(_minsupport, dv.supportSize()) : _minsupport;
			_maxsupport = dv != null ? Math.max(_maxsupport, dv.supportSize()) : _maxsupport;
			_violation += dv != null ? dv.violation(xstar) : 0;
			
			triesLeft = violated ? _attempts : (triesLeft-1);
		}
		
		_master.close();
		_time = (System.currentTimeMillis() - _start) / 1000.0;
		
		showSummary();
	}
	
	private InfeasibilityFunction createInfeasibilityFunction(Point xbar)
	{
		if( _infeasibilityFunction.toLowerCase().trim().equals("class") )
			return new InfeasibilityFunctionClassical(_instance, xbar);

		if( _infeasibilityFunction.toLowerCase().trim().equals("card") )
			return new InfeasibilityFunctionCardinality(_instance, xbar);

		if( _infeasibilityFunction.toLowerCase().trim().equals("weight") )
			return new InfeasibilityFunctionWeighted(_instance, xbar);

		if( _infeasibilityFunction.toLowerCase().trim().equals("seq") )
			return new InfeasibilityFunctionSequence(_instance, xbar);
		
		throw new RuntimeException("Unknown infeasibility function: " + _infeasibilityFunction);
	}
	
	private Rounder createRounder()
	{
		if( _roundingProcedure.toLowerCase().trim().equals("class") )
			return new RounderClassical(_instance);

		if( _roundingProcedure.toLowerCase().trim().equals("upper") )
			return new RounderUpperTargeted(_instance);

		if( _roundingProcedure.toLowerCase().trim().equals("target") )
			return new RounderTargeted(_instance);

		throw new RuntimeException("Unknown rounding procedure: " + _roundingProcedure);
	}

	private void showPoints(Point xstar, Point xbar)
	{
		if( _verbose == true )
		{
			System.out.print(_master.getSummary() + " | ");
			System.out.print("x*: " + xstar.fractionalEntries() + " fract | ");
			System.out.print("xbar: " + xbar.positiveEntries() + " nonzeros | ");
		}
	}
	
	private void showResults(Point xstar, CutGenerator cutter, Inequality dv)
	{
		if( _verbose == true )
		{
			System.out.print(" | " + cutter.getRounds() + " rounds; ");
			System.out.print("|C| = " + cutter.getCsize() + "; ");
			System.out.print("|N| = " + cutter.getNsize() + "; ");
			System.out.print(cutter.getNfeasibles() + " feas; avg infeas = ");
			System.out.print(String.format("%.3f", cutter.getNaverageInfeasibility()) + "; ");
			System.out.print(String.format("%.2f", cutter.getElapsedTime()) + " sec | ");
			System.out.print(dv != null ? "dv: supp = " + dv.supportSize() + "; " : "");
			System.out.print(dv != null ? "viol = " + String.format("%.3f", dv.violation(xstar)) : "");
			System.out.print(dv != null && _showInequalities ? " | " + dv : "");
			System.out.println();
		}
	}

	private void showSummary()
	{
		System.out.print("v" + EntryPoint.version() + " | ");
		System.out.print(_iterations + " its | ");
		System.out.print(_rounds + " rounds | ");
		System.out.print(_cuts + " cuts | ");
		System.out.print("LR: " + _firstobj + " | ");
		System.out.print("cLR: " + _lastobj + " | ");
		System.out.print(String.format("%.2f", _time) + " sec. | ");
		
		if( _iterations > 1 )
		{
			System.out.print("avg(|N|): " + String.format("%.3f", _nsize / (_iterations-1)) + " | ");
			System.out.print("avg(feas): " + String.format("%.3f", _feasibles / (_iterations-1)) + " | ");
			System.out.print("min(supp): " + _minsupport + " | ");
			System.out.print("avg(supp): " + String.format("%.3f", _supportsize / (_iterations-1)) + " | ");
			System.out.print("max(supp): " + _maxsupport + " | ");
			System.out.print("avg(viol): " + String.format("%.3f", _violation / _iterations) + " | ");
		}
		else if( _iterations == 1 )
		{
			System.out.print("avg(|N|): " + String.format("%.3f", _nsize) + " | ");
			System.out.print("avg(feas): " + String.format("%.3f", _feasibles) + " | | | | | ");
		}
		else
		{
			System.out.print(" | | | | | | ");
		}

		System.out.print(EntryPoint.argmap());
		System.out.println();
	}

	public static void setVerbose(boolean value)
	{
		_verbose = value;
	}

	public static void setShowInequalities(boolean value)
	{
		_showInequalities = value;
	}
	
	public static void setCuttingAttempts(int value)
	{
		_attempts = value;
	}
	
	public static void setInfeasibilityFunction(String value)
	{
		_infeasibilityFunction = value;
	}
	
	public static void setRoundingProcedure(String value)
	{
		_roundingProcedure = value;
	}
}
