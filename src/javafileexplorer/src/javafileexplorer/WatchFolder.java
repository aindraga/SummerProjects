package javafileexplorer;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class WatchFolder 
{
	Path folderPath;
	Hashtable<String, ArrayList<String>> updateMapper = new Hashtable<String, ArrayList<String>>();
	
	WatchFolder (String folderPath) 
	{
		this.folderPath = Paths.get(folderPath);
		
		ArrayList<String> emptyList = new ArrayList<String>();
		
		this.updateMapper.put("Created", emptyList);
		this.updateMapper.put("Deleted", emptyList);
	}
	
	private boolean validate() 
	{
		if (Files.exists(folderPath) == false) 
		{
			System.out.println("Please ensure the inputted path exists.");
			return false;
		}
		if (Files.isDirectory(folderPath) == false) 
		{
			System.out.println("Please ensure you have inputted a directory path.");
			return false;
		}
		
		return true;
	}
	
	public boolean monitorFolder() 
	{
		if (this.validate() == false) 
		{
			return false;
		}
		
		System.out.println("Valid folder. Beginning service.");
		
		try (WatchService service = FileSystems.getDefault().newWatchService()) 
		{
			WatchKey watchKey = this.folderPath.register(service, 
					StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
			
			while (true) 
			{
				for (WatchEvent<?> event : watchKey.pollEvents()) 
				{
					WatchEvent.Kind<?> kind = event.kind();
					Path eventPath = (Path) event.context();
					
					if (kind == StandardWatchEventKinds.ENTRY_CREATE) 
					{
						System.out.println("Created entry called on " + eventPath.toString());
						ArrayList<String> createdPendingPaths = this.updateMapper.get("Created");
						createdPendingPaths.add(eventPath.toString());
					}
					else if (kind == StandardWatchEventKinds.ENTRY_DELETE) 
					{
						System.out.println("Deleted entry called on " + eventPath.toString());
						ArrayList<String> deletedPendingPaths = this.updateMapper.get("Deleted");
						deletedPendingPaths.add(eventPath.toString());
					}
				}
				
				boolean isValidKey = watchKey.reset();
				
				if (isValidKey == false) { return false; }
			}
		}
		catch (IOException ioException) 
		{
			ioException.printStackTrace();
			return false;
		}
	}
}
