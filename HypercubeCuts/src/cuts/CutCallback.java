package cuts;

import ilog.cplex.IloCplex;
import ilog.concert.IloException;
import ilog.concert.IloNumExpr;

@Deprecated
public class CutCallback extends IloCplex.UserCutCallback
{
	private MasterModel _master;
	private InfeasibilityFunction _f;
	private Rounder _rounder;
	
	public CutCallback(MasterModel master, InfeasibilityFunction f, Rounder rounder)
	{
		_master = master;
		_f = f;
		_rounder = rounder;
	}

	@Override
	protected void main() throws IloException
	{
		Point xstar = getCurrentSolution();
		Point xbar = _rounder.round(xstar);
		
		CutGenerator cutter = new CutGenerator(xstar, xbar, _f);
		Inequality dv = cutter.generate();
		
		if( dv.violated(xstar) == true )
			add(dv);
	}
	
	private Point getCurrentSolution() throws IloException
	{
		Point ret = new Point(this.getNcols());
		
		for(int i=0; i<this.getNcols(); ++i)
			ret.set(i, this.getValue(_master.getVar(i)));
		
		return ret;
	}
	
	private void add(Inequality dv) throws IloException
	{
		IloCplex cplex = _master.getCplex();
		IloNumExpr lhs = cplex.linearIntExpr();
		
		for(int i=0; i<this.getNcols(); ++i) if( dv.getLHS(i) != 0 )
			lhs = cplex.sum(lhs, cplex.prod(dv.getLHS(i), _master.getVar(i)));
		
		this.add(cplex.le(lhs, dv.getRHS()), IloCplex.CutManagement.UseCutForce);
	}
}
