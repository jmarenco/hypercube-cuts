package generalcuts;

public abstract class Rounder
{
	protected Instance _instance;

	public Rounder(Instance instance)
	{
		_instance = instance;
	}
	
	public abstract Point round(Point xstar);
}
