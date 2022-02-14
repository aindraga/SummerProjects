package javafileexplorer;

import java.nio.file.Paths;

public class Runner 
{
	public static void main(String[] args) 
	{
		MyDB db = new MyDB("C:\\Users\\Annu\\Desktop\\Dummy");
		MyDB.callWalker(db);
		String userDirectory = Paths.get("")
		        .toAbsolutePath()
		        .toString();
		
		System.out.println(userDirectory);
	}
}