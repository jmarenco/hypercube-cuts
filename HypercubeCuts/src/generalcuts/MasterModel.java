package generalcuts;

import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.Status;

public class MasterModel
{
	private Instance _instance;
	private IloCplex _cplex;
	private IloNumVar[] x;
	
	private int _rounds = 0;

	private static boolean _verbose = false;
	private static boolean _summary = false;
	private boolean _integer = false;
	
	public MasterModel(Instance instance)
	{
		_instance = instance;
	}
	
	public MasterModel(Instance instance, boolean integer)
	{
		_instance = instance;
		_integer = integer;
	}

	public void create()
	{
		try
		{
			// Variables
			_cplex = new IloCplex();
			x = new IloNumVar[_instance.getVars()];
			
			for(int j=0; j<_instance.getVars(); ++j)
			{
				if( _integer == false )
					x[j] = _cplex.numVar(0, 1, "x" + j);
				else
					x[j] = _cplex.boolVar("x" + j);
			}
			
			// Objective function
			IloNumExpr obj = _cplex.linearIntExpr();
			
			for(int j=0; j<_instance.getVars(); ++j)
				obj = _cplex.sum(obj, _cplex.prod(_instance.getC(j), x[j]));

			_cplex.addMaximize(obj);
			
			// Constraints
			for(int i=0; i<_instance.getCons(); ++i)
			{
				IloNumExpr lhs = _cplex.linearIntExpr();
				
				for(int j=0; j<_instance.getVars(); ++j) if( _instance.getA(i,j) != 0 )
					lhs = _cplex.sum(lhs, _cplex.prod(_instance.getA(i,j), x[j]));
				
				_cplex.addLe(lhs, _instance.getB(i));
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
				_cplex.setOut(null);
			
			_cplex.solve();
			_rounds++;
			
			// Get solution
			if( _cplex.getStatus() == Status.Optimal || _cplex.getStatus() == Status.Feasible )
			{
				ret = new Point(_instance.getVars());
				
				for(int j=0; j<_instance.getVars(); ++j)
					ret.set(j, _cplex.getValue(x[j]));
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
				+ _cplex.getNcols() + " vars | "
				+ _cplex.getNrows() + " constr | "
				+ _cplex.getStatus() + " | "
				+ "Obj: " + String.format("%.3f", _cplex.getObjValue());
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
			ret = _cplex.getObjValue();
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
			IloNumExpr lhs = _cplex.linearIntExpr();
			
			for(int j=0; j<_instance.getVars(); ++j) if( inequality.getLHS(j) != 0 )
				lhs = _cplex.sum(lhs, _cplex.prod(inequality.getLHS(j), x[j]));
			
			_cplex.addLe(lhs, inequality.getRHS());
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
			_cplex.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public IloCplex getCplex()
	{
		return _cplex;
	}
	
	public IloNumVar getVar(int j)
	{
		return x[j];
	}
	
	public static void setVerbose(boolean valor)
	{
		_verbose = valor;
	}
}
