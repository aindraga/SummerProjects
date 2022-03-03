package javafileexplorer;

import java.nio.file.*;
import java.util.*;

public class Container
{	
	Path inputDirectory;
	
	Container (Path baseDirectory) 
	{
		if (InputValidation.validateFolder(baseDirectory) == true) 
		{
			inputDirectory = baseDirectory;
		}
		
		else 
		{
			inputDirectory = null;
		}
	}
	
	public Hashtable<String, Hashtable<String, HashSet<Path>>> allFilesMap = new Hashtable<String, Hashtable<String, HashSet<Path>>>();
	
	public Hashtable<String, Hashtable<Path, Hashtable<String, HashSet<Path>>>> allFoldersMap = 
			new Hashtable<String, Hashtable<Path, Hashtable<String, HashSet<Path>>>>();
	
	public Hashtable<Path, WatchKey> pathKeyMap = new Hashtable<Path, WatchKey>();
	
	public Queue<Object[]> allEvents = new PriorityQueue<Object[]>();
	
	public Queue<Path> inValidFolders = new PriorityQueue<Path>();
	
	public boolean isEmpty() 
	{
		if (allFilesMap.isEmpty() && allFoldersMap.isEmpty() && pathKeyMap.isEmpty() && allEvents.isEmpty() && inValidFolders.isEmpty()) 
		{
			return true;
		}
		
		return false;
	}
}