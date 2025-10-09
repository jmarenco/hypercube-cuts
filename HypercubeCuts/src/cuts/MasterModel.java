package cuts;

public interface MasterModel
{
	public void create();
	public Point solve();
	public String getSummary();
	public double getObjective();
	public void add(Inequality inequality);
	public void close();
}
