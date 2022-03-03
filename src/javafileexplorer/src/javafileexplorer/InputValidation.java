package javafileexplorer;

import java.nio.file.Files;
import java.nio.file.Path;

public class InputValidation 
{
	public static boolean validateFolder(Path currentPath) 
	{
		if (Files.exists(currentPath) == false) 
		{
			System.out.println("Path does not exist: " + currentPath);
			return false;
		}
		
		if (Files.isDirectory(currentPath) == false) 
		{
			System.out.println("Path is not a folder: " + currentPath);
			return false;
		}
		
		return true;
	}
	
	public static boolean validateFile(Path currentPath) 
	{
		if (Files.exists(currentPath) == false) 
		{
			System.out.println("Path does not exist: " + currentPath);
			return false;
		}
		
		if (Files.isDirectory(currentPath) == true) 
		{
			System.out.println("Path is directory: " + currentPath);
			return false;
		}
		
		if (Files.isRegularFile(currentPath) == false) 
		{
			return false;
		}
		
		return true;
	}
}
