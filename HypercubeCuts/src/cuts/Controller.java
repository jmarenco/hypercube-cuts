package cuts;

public class Controller
{
	private MasterModel _master;
	private InfeasibilityFunction _f;
	private Rounder _rounder;
	
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
			
			System.out.print(_master.getSummary() + " | ");
			System.out.print("x*: " + xstar.fractionalEntries() + " fract | ");
			System.out.print("xbar: " + xbar.positiveEntries() + " nonzeros | ");
			
			// TODO: No es getPositiveEntries!
			System.out.print("xfeas: " + xfeas.positiveEntries() + ", gap = " + String.format("%.3f", (_master.getObjective() - _f.getObjective(xfeas)) * 100.0 / _master.getObjective()) + " % | ");
			
			CutGenerator cutter = new CutGenerator(xstar, xbar, _f);
			Inequality dv = cutter.generate();
	
			System.out.print(cutter.getRounds() + " its, |N| = " + cutter.getNsize() + ", ");
			System.out.print(cutter.getNinfeasibles() + " feas, avg infeas = ");
			System.out.print(String.format("%.3f", cutter.getNaverageInfeasibility()) + ", ");
			System.out.print(String.format("%.2f", cutter.getElapsedTime()) + " sec | ");
			System.out.print(dv != null ? "dv: supp = " + dv.supportSize() + ", " : "");
			System.out.print(dv != null ? "viol = " + String.format("%.3f", dv.violation(xstar)) : "");
			System.out.println();
			
			violated = dv != null ? dv.violation(xstar) > 0 : false;
			_master.add(dv);
		}
		
		_master.close();
	}

}
