package javafileexplorer;

import java.nio.file.Path;
import java.nio.file.WatchService;

public class EventProcessor implements Runnable
{	
	Container eventContainer;
	WatchService eventService;
	Populator eventPopulator;
	
	EventProcessor (Container container, WatchService service, Populator populator) 
	{
		eventContainer = container;
		eventService = service;
		eventPopulator = populator;
	}
	
	@Override
	public void run() 
	{
		this.processEvents(null);
	}
	
	private boolean processEvents(WatchService service) 
	{
		while (true) 
		{
			if (this.eventContainer.isEmpty() == true) 
			{
				continue;
			}
			
			Object[] currentEvent = this.eventContainer.allEvents.poll();
			
			Path currentPath = (Path) currentEvent[0];
			int eventNum = (int) currentEvent[1];
			
			if (eventNum == 0) 
			{
				if (InputValidation.validateFile(currentPath) == true) 
				{
					this.eventPopulator.removePathFromFileMap(currentPath);
				}
				else if (InputValidation.validateFolder(currentPath) == true) 
				{
					this.eventPopulator.removePathFromFolderMap(currentPath);
				}
			}
			
			else 
			{
				if (InputValidation.validateFile(currentPath) == true) 
				{
					this.eventPopulator.addPathToFileMap(currentPath, service);
				}
				else if (InputValidation.validateFolder(currentPath) == true) 
				{
					this.eventPopulator.addPathToFolderMap(currentPath, service);
				}
			}
		}
	}
}