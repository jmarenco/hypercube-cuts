package cuts;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashSet;

import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.Status;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumExpr;

public class CutGenerator
{
	private Point _xstar;
	private Point _xbar;
	private InfeasibilityFunction _f;
	private ArrayList<Point> _N;
	private Set<Point> _C;

	private long _rounds;
	private long _start;
	private long _end;
	
	private boolean _verbose = false;
	private boolean _roundSummary = false;
	private boolean _cplexLog = false;
	private long _maxNsize = 10000;
	
	public CutGenerator(Point xstar, Point xbar, InfeasibilityFunction f)
	{
		_xstar = xstar;
		_xbar = xbar;
		_f = f;
	}
	
	public static Inequality generate(Point xstar, Point xbar, InfeasibilityFunction f)
	{
		return new CutGenerator(xstar, xbar, f).generate();
	}
	
	public Inequality generate()
	{
		initialize();
		
		if( _xstar.integer() )
			return null;
		
		propagate();
		return solve();
	}
	
	private void initialize()
	{
		_N = new ArrayList<Point>();
		_C = new HashSet<Point>();
		_N.add(_xbar);

		_rounds = 0;
		_start = System.currentTimeMillis();
		_end = _start;

		showPoint("x*", _xstar, "");
		showPoint("xbar", _xbar, "");
	}
	
	private void propagate()
	{
		boolean cont = true;
		while( cont == true && _N.size() <= _maxNsize )
		{
			cont = false;

			for(Point x: _N.stream().filter(x -> !_f.feasible(x)).collect(Collectors.toList()))
			{
				if( cont = propagate(x) ) // Hmmm ...
					break;
			}
			
			_rounds++;
			showRoundSummary();
		}

		showSets("Done!", true, false, null);
	}
	
	private boolean propagate(Point x)
	{
		showSets("Propagating!", true, true, x);

		if( _N.contains(x) == false )
			throw new RuntimeException("Asked to propagate point not in N!");
		
		if( _f.feasible(x) == true )
			throw new RuntimeException("Asked to propagate feasible point!");

		boolean ret = false;
		for(int i=0; i<x.size(); ++i)
		{
			Point y = x.flip(i);
			String result = "";
			
			if( _N.contains(y) == false && _C.contains(y) == false )
			{
				if( _f.get(y) <= _f.get(x) )
				{
					_N.add(y);
					ret = true;
					result = "added";
				}

				_C.add(y);
			}
			else
				result = "already in N or C";

			showPoint("y", y, result);
		}
		
		_N.remove(x);
		_C.add(x);
		
		return ret;
	}
	
	private Inequality solve()
	{
		Inequality ret = null;
		
		try
		{
			// Variables
			IloCplex cplex = new IloCplex();
			IloNumVar[] pi = new IloNumVar[_xbar.size()];
			IloNumVar[] piabs = new IloNumVar[_xbar.size()];
			IloNumVar pi0 = cplex.numVar(-1000, 1000, "pi0");
			
			for(int i=0; i<_xbar.size(); ++i)
			{
				pi[i] = cplex.numVar(-1, 1, "pi" + (i+1));
				piabs[i] = cplex.numVar(-1, 1, "piabs" + (i+1));
			}
			
			// Objective function
			IloNumExpr obj = cplex.linearIntExpr();
			
			for(int i=0; i<_xbar.size(); ++i)
				obj = cplex.sum(obj, cplex.prod(_xstar.get(i), pi[i]));
			
			obj = cplex.sum(obj, cplex.prod(-1, pi0));
			cplex.addMaximize(obj);
			
			// Constraints for points in N
			for(Point x: _N)
			{
				IloNumExpr lhs = cplex.linearIntExpr();
				
				for(int i=0; i<_xbar.size(); ++i)
					lhs = cplex.sum(lhs, cplex.prod(x.get(i), pi[i]));
				
				lhs = cplex.sum(lhs, cplex.prod(-1, pi0));
				cplex.addLe(lhs, 0, "cx" + cplex.getNrows());
			}
			
			// Constraint for xbar
			IloNumExpr lhs1 = cplex.linearIntExpr();
			
			for(int i=0; i<_xbar.size(); ++i)
				lhs1 = cplex.sum(lhs1, cplex.prod(_xbar.get(i), pi[i]));
			
			lhs1 = cplex.sum(lhs1, cplex.prod(-1, pi0));
			cplex.addGe(lhs1, 0.01, "xb");
			
			// Constraints for piabs
			for(int i=0; i<_xbar.size(); ++i)
			{
				IloNumExpr lhs2 = cplex.linearIntExpr();
				IloNumExpr lhs3 = cplex.linearIntExpr();
				
				lhs2 = cplex.sum(lhs2, piabs[i]);
				lhs3 = cplex.sum(lhs3, piabs[i]);
				lhs2 = cplex.sum(lhs2, cplex.prod(-1, pi[i]));
				lhs3 = cplex.sum(lhs3, pi[i]);
				
				cplex.addGe(lhs2, 0, "piabs" + cplex.getNrows());
				cplex.addGe(lhs3, 0, "piabs" + cplex.getNrows());
			}
			
			// Normalization constraint
			IloNumExpr lhs4 = cplex.linearIntExpr();
			
			for(int i=0; i<_xbar.size(); ++i)
				lhs4 = cplex.sum(lhs4, piabs[i]);
			
			cplex.addLe(lhs4, 1, "norm");
			
			// Compatibility constraints
			for(Inequality constraint: _f.compatibilityConstraints(_xbar))
			{
				IloNumExpr lhs5 = cplex.linearIntExpr();
				
				for(int i=0; i<_xbar.size(); ++i) if( constraint.getLHS(i) != 0 )
					lhs5 = cplex.sum(lhs5, cplex.prod(constraint.getLHS(i), pi[i]));
				
				cplex.addLe(lhs5, constraint.getRHS(), "comp" + cplex.getNrows());
			}
			
			// Solve model
			if( _cplexLog == false )
				cplex.setOut(null);

			cplex.exportModel("c:\\users\\jmarenco\\desktop\\modelo.lp");
			cplex.solve();
			
			// Get inequality
			if( cplex.getStatus() == Status.Optimal || cplex.getStatus() == Status.Feasible )
			{
				ret = new Inequality(_xbar.size());
				ret.setRHS(cplex.getValue(pi0));
				
				for(int i=0; i<_xbar.size(); ++i)
					ret.setLHS(i, cplex.getValue(pi[i]));
			}
			
			// Closes model
			cplex.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	
		_end = System.currentTimeMillis();
		return ret;
	}
	
	private void showSets(String text, boolean showN, boolean showC, Point x)
	{
		if( _verbose == false )
			return;

		System.out.println();
		System.out.println(text);
		
		if( showN == true )
			System.out.println(" N = " + _N);
		
		if( showC == true )
			System.out.println(" C = " + _C);
		
		if( x != null )
			System.out.println(" x = " + x + " - f(x) = " + _f.get(x));
	}
	
	private void showPoint(String name, Point y, String text)
	{
		if( _verbose == false )
			return;

		System.out.println(" " + name + " = " + y + " - f(x) = " + _f.get(y) + (text != null ? ", " + text : ""));
	}
	
	private void showRoundSummary()
	{
		if( _roundSummary == false )
			return;

		System.out.print("Round " + _rounds + ": |N| = " + _N.size() + ", ");
		System.out.print(this.getNfeasibles() + " feas, avg infeas = ");
		System.out.print(String.format("%.3f", this.getNaverageInfeasibility()) + ";");
		System.out.print(" |C| = " + _C.size() + "; ");
		System.out.print(String.format("%.2f", (System.currentTimeMillis() - _start) / 1000.0) + " sec");
		System.out.println();
 	}
	
	public long getRounds()
	{
		return _rounds;
	}
	
	public int getNsize()
	{
		return _N.size();
	}
	
	public double getNaverageInfeasibility()
	{
		return _N.stream().mapToDouble(x -> _f.get(x)).average().orElse(0);
	}
	
	public long getNfeasibles()
	{
		return _N.stream().filter(x -> _f.feasible(x)).count();
	}
	
	public double getElapsedTime()
	{
		return (_end - _start) / 1000.0;
	}
}
