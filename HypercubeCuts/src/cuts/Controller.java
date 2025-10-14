package cuts;

import test.EntryPoint;

public class Controller
{
	private MasterModel _master;
	private InfeasibilityFunction _f;
	private Rounder _rounder;
	
	private int _iterations = 0;
	private int _rounds = 0;
	private double _nsize = 0;
	private double _feasibles = 0;
	private double _supportsize = 0;
	private int _minsupport = Integer.MAX_VALUE;
	private int _maxsupport = 0;
	private double _violation = 0;
	
	private static boolean _verbose = false;
	
	public Controller(MasterModel masterModel, InfeasibilityFunction f, Rounder rounder)
	{
		_master = masterModel;
		_f = f;
		_rounder = rounder;
	}
	
	public void run()
	{
		_master.create();
		
		boolean violated = true;
		while( violated == true )
		{
			Point xstar = _master.solve();
			Point xbar = _rounder.round(xstar);
			Point xfeas = _rounder.roundToFeasible(xstar);
			
			showPoints(xstar, xbar, xfeas);
			
			CutGenerator cutter = new CutGenerator(xstar, xbar, _f);
			Inequality dv = cutter.generate();
			
			showResults(xstar, cutter, dv);
			
			violated = dv != null ? dv.violation(xstar) > 0 : false;
			_master.add(dv);

			_iterations += 1;
			_rounds += cutter.getRounds();
			_nsize += cutter.getNsize();
			_feasibles += cutter.getNfeasibles();
			_supportsize += dv != null ? dv.supportSize() : 0;
			_minsupport = dv != null ? Math.min(_minsupport, dv.supportSize()) : _minsupport;
			_maxsupport = dv != null ? Math.max(_maxsupport, dv.supportSize()) : _maxsupport;
			_violation += dv != null ? dv.violation(xstar) : 0;
		}
		
		_master.close();
		showSummary();
	}
	
	private void showPoints(Point xstar, Point xbar, Point xfeas)
	{
		if( _verbose == true )
		{
			System.out.print(_master.getSummary() + " | ");
			System.out.print("x*: " + xstar.fractionalEntries() + " fract | ");
			System.out.print("xbar: " + xbar.positiveEntries() + " nonzeros | ");
			System.out.print("xfeas: " + xfeas.positiveEntries() + ", gap = " + String.format("%.3f", (_master.getObjective() - _f.getObjective(xfeas)) * 100.0 / _master.getObjective()) + " % | ");
		}
	}
	
	private void showResults(Point xstar, CutGenerator cutter, Inequality dv)
	{
		if( _verbose == true )
		{
			System.out.print(cutter.getRounds() + " rounds, |N| = " + cutter.getNsize() + ", ");
			System.out.print(cutter.getNfeasibles() + " feas, avg infeas = ");
			System.out.print(String.format("%.3f", cutter.getNaverageInfeasibility()) + ", ");
			System.out.print(String.format("%.2f", cutter.getElapsedTime()) + " sec | ");
			System.out.print(dv != null ? "dv: supp = " + dv.supportSize() + ", " : "");
			System.out.print(dv != null ? "viol = " + String.format("%.3f", dv.violation(xstar)) : "");
			System.out.println();
		}
	}

	private void showSummary()
	{
		System.out.print("v" + EntryPoint.version() + " | ");
		System.out.print(_iterations + " its | ");
		System.out.print(_rounds + " rounds | ");
		
		if( _iterations > 1 )
		{
			System.out.print("avg(|N|): " + String.format("%.3f", _nsize / (_iterations-1)) + " | ");
			System.out.print("avg(feas): " + String.format("%.3f", _feasibles / (_iterations-1)) + " | ");
			System.out.print("min(supp): " + _minsupport + " | ");
			System.out.print("avg(supp): " + String.format("%.3f", _supportsize / (_iterations-1)) + " | ");
			System.out.print("max(supp): " + _maxsupport + " | ");
			System.out.print("avg(viol): " + String.format("%.3f", _violation / _iterations) + " | ");
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
}
