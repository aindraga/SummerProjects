package javafileexplorer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

public class InValid implements Runnable
{
	Container invalidContainer;
	WatchService invalidService;
	Populator invalidPopulator;
	
	InValid(WatchService service, Container container, Populator populator) 
	{
		invalidContainer = container;
		invalidService = service;
		invalidPopulator = populator;
	}
	
	public void run() 
	{
		this.processInvalidFolders(invalidService);
	}
	
	private boolean processInvalidFolders(WatchService service) 
	{
		while (true) 
		{
			if (this.invalidContainer.inValidFolders.isEmpty() == true) 
			{
				continue;
			}
			
			Path invalidFolder = this.invalidContainer.inValidFolders.poll();
			this.invalidContainer.pathKeyMap.remove(invalidFolder);
			
			try 
			{
				if (InputValidation.validateFolder(invalidFolder) == true) 
				{
					WatchKey key = invalidFolder.register(service, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
					this.invalidContainer.pathKeyMap.put(invalidFolder, key);
				}
				
				else 
				{
					this.invalidPopulator.removePathFromFolderMap(invalidFolder);
				}
			}
			
			catch (IOException io) 
			{
				this.invalidContainer.inValidFolders.add(invalidFolder);
				continue;
			}
		}
	}

}