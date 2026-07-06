package generalcuts;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Rounder
{
	private double _maxUpperRounding = 7;

	public Rounder()
	{
	}
	
	public Point round(Point xstar)
	{
		Random random = new Random();
		List<Integer> fractional = fractionalIndices(xstar);
		
		Point ret = new Point(xstar);
		for(int i = 0; i < _maxUpperRounding && fractional.size() > 0; ++i)
		{
			int j = random.nextInt(fractional.size());
			ret.set(fractional.get(j), 1);
			fractional.remove(j);
		}
		
		for(Integer j: fractional)
			ret.set(j, 0);
		
		return ret;
	}
	
	private List<Integer> fractionalIndices(Point xstar)
	{
		return IntStream.range(0, xstar.size()).filter(j -> xstar.fractional(j)).boxed().collect(Collectors.toList());
	}
}
