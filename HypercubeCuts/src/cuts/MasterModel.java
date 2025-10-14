package cuts;

import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public interface MasterModel
{
	public void create();
	public Point solve();
	public String getSummary();
	public double getObjective();
	public void add(Inequality inequality);
	public void close();
	
	public IloCplex getCplex();
	public IloNumVar getVar(int i);
}
