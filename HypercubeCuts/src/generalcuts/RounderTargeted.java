package generalcuts;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RounderTargeted extends Rounder
{
	private static int _maxUpperRounding = 7;
	private List<Integer> _increasing;

	public RounderTargeted(Instance instance)
	{
		super(instance);

//		_increasing = IntStream.range(0, _instance.getVars()).filter(j -> colSum(j) > 0).boxed().collect(Collectors.toList());
		_increasing = IntStream.range(0, _instance.getVars()).boxed().sorted((i,j) -> (int)Math.signum(colSum(j)-colSum(i))).collect(Collectors.toList());
	}
	
	private double colSum(int j)
	{
		return IntStream.range(0, _instance.getCons()).mapToDouble(i -> _instance.getA(i,j)).sum();
	}
	
	public Point round(Point xstar)
	{
		Random random = new Random();
		List<Integer> fractional = fractionalIncreasingIndices(xstar);
		
//		System.out.print("fr: ");
//		for(Integer i: fractional)
//			System.out.print(i + " ");
		
		Point ret = new Point(xstar);
		for(int i = 0; i < _maxUpperRounding && fractional.size() > 0; ++i)
		{
			int j = random.nextInt(fractional.size());
			ret.set(fractional.get(j), 1);
			fractional.remove(j);
		}
		
		for(Integer j: fractional)
			ret.set(j, 0);

//		System.out.println("ñññ" + ret);
		return ret;
	}
	
	private List<Integer> fractionalIncreasingIndices(Point xstar)
	{
		return _increasing.stream().filter(j -> xstar.fractional(j)).collect(Collectors.toList());
	}
	
	public static void setMaxRounding(int value)
	{
		_maxUpperRounding = value;
	}
}
