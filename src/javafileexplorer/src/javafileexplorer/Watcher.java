package javafileexplorer;

import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

public class Watcher implements Runnable
{	
	WatchService baseService;
	Container baseContainer;
	
	Watcher (Container container, WatchService service) 
	{
		baseContainer = container;
		baseService = service;
	}
	
	@Override
	public void run() 
	{
		this.beginWatch(baseService);
	}
	
	private boolean processWatchKey(WatchKey key) 
	{
		List<WatchEvent<?>> allEvents = key.pollEvents();
		
		for (WatchEvent<?> event: allEvents) 
		{
			WatchEvent.Kind<?> eventType = event.kind();
			Path eventPath = (Path) event.context();
			Path parentPath = eventPath.getParent();
			
			Object[] eventArr = new Object[2];
			
			eventArr[0] = eventPath;
			
			if (eventType == StandardWatchEventKinds.ENTRY_CREATE) 
			{
				eventArr[1] = 1;
			}
			
			else if (eventType == StandardWatchEventKinds.ENTRY_DELETE) 
			{
				eventArr[1] = 0;
			}
			
			else 
			{
				System.out.println(eventType + ": " + eventPath);
				continue;
			}
			
			this.baseContainer.allEvents.add(eventArr);
			
			boolean isValidKey = key.reset();
			
			if (isValidKey == false) 
			{
				this.baseContainer.inValidFolders.add(parentPath);
			}
		}
		
		return true;
	}
		
	private boolean beginWatch(WatchService service) 
	{
		WatchKey currentKey;
		
		while (true) 
		{
			try 
			{
				currentKey = service.take();
				this.processWatchKey(currentKey);
			}
			
			catch (InterruptedException intExcep) 
			{
				continue;
			}
		}
	}
}