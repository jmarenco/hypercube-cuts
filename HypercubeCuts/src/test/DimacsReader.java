package test;

import java.io.FileInputStream;
import java.util.Scanner;

import stableset.Graph;

public class DimacsReader
{
	public static Graph read(String file)
	{
		Graph ret = null;
		
		try
		{
			Scanner in = new Scanner(new FileInputStream(file));
			while( in.hasNextLine() )
			{
				String line = in.nextLine().trim();
				
				if( line.startsWith("p edge") )
					ret = new Graph(field(line,2));
				
				if( line.startsWith("e") )
					ret.addEdge(field(line,1)-1, field(line,2)-1);
			}
			
			in.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return ret;
	}
	
	private static int field(String line, int index)
	{
		String[] campos = line.split(" ");
		return Integer.parseInt(campos[index]);
	}
}
