package stableset;

import cuts.Inequality;
import cuts.MasterModel;
import cuts.Point;

import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.Status;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumExpr;

public class StableModel implements MasterModel
{
	private Graph G;
	private IloCplex cplex;
	private IloNumVar[] x;
	
	private int _rounds = 0;

	private boolean _verbose = false;
	private boolean _summary = false;
	private boolean _integer = false;
	
	public StableModel(Graph graph)
	{
		G = graph;
	}
	
	public StableModel(Graph graph, boolean integer)
	{
		G = graph;
		_integer = integer;
	}

	public void create()
	{
		try
		{
			// Variables
			cplex = new IloCplex();
			x = new IloNumVar[G.size()];
			
			for(int i=0; i<G.size(); ++i)
			{
				if( _integer == false )
					x[i] = cplex.numVar(0, 1, "x" + i);
				else
					x[i] = cplex.boolVar("x" + i);
			}
			
			// Objective function
			IloNumExpr obj = cplex.linearIntExpr();
			
			for(int i=0; i<G.size(); ++i)
				obj = cplex.sum(obj, x[i]);

			cplex.addMaximize(obj);
			
			// Constraints for each edge
			for(int i=0; i<G.size(); ++i)
			for(int j=i+1; j<G.size(); ++j) if( G.isEdge(i, j) )
			{
				IloNumExpr lhs = cplex.linearIntExpr();
				
				lhs = cplex.sum(lhs, x[i]);
				lhs = cplex.sum(lhs, x[j]);
				
				cplex.addLe(lhs, 1);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public Point solve()
	{
		Point ret = null;
		
		try
		{
			// Solve model
			if( _verbose == false )
				cplex.setOut(null);
			
			cplex.solve();
			_rounds++;
			
			// Get solution
			if( cplex.getStatus() == Status.Optimal || cplex.getStatus() == Status.Feasible )
			{
				ret = new Point(G.size());
				
				for(int i=0; i<G.size(); ++i)
					ret.set(i, cplex.getValue(x[i]));
			}
			
			if( _summary == true )
				System.out.println(getSummary());
		}
		catch(Exception e)
 		{
			e.printStackTrace();
		}
		
		return ret;
	}

	public String getSummary()
	{
		String ret = "";
		
		try
		{
			ret = "Round " + _rounds + " | "
				+ cplex.getNcols() + " vars | "
				+ cplex.getNrows() + " constr | "
				+ cplex.getStatus() + " | "
				+ "Obj: " + String.format("%.3f", cplex.getObjValue());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return ret;
	}
	
	public double getObjective()
	{
		double ret = 0;
		
		try
		{
			ret = cplex.getObjValue();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return ret;
	}
	
	public void add(Inequality inequality)
	{
		if( inequality == null )
			return;
		
		try
		{
			IloNumExpr lhs = cplex.linearIntExpr();
			
			for(int i=0; i<G.size(); ++i) if( inequality.getLHS(i) != 0 )
				lhs = cplex.sum(lhs, cplex.prod(inequality.getLHS(i), x[i]));
			
			cplex.addLe(lhs, inequality.getRHS());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void close()
	{
		try
		{
			cplex.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public IloCplex getCplex()
	{
		return cplex;
	}
	
	public IloNumVar getVar(int i)
	{
		return x[i];
	}
}
