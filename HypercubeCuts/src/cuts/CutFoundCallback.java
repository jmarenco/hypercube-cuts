package cuts;

public interface CutFoundCallback
{
	public void notify(Inequality inequality, Point xstar);
}
