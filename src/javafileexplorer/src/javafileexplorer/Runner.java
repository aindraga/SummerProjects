package javafileexplorer;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchService;

public class Runner 
{
	public static void main(String[] args) 
	{
		String baseDir = "C:\\";
		Path convertDir = Paths.get(baseDir);
		
		Container baseContainer = new Container(convertDir);
		
		if (baseContainer.isEmpty() && baseContainer.inputDirectory != null) 
		{
			try (WatchService baseService = FileSystems.getDefault().newWatchService()) 
			{
				Populator basePopulator = new Populator(baseContainer);
				
				boolean validWalk = basePopulator.walkThroughDirectory(baseService, basePopulator);
				
				if (validWalk == true) 
				{
					Runnable baseWatcher = new Watcher(baseContainer, baseService);
					Runnable baseEventProcessor = new EventProcessor(baseContainer, baseService, basePopulator);
					Runnable baseInvalidProcessor = new InValid(baseService, baseContainer, basePopulator);
					
					baseWatcher.run();
					baseEventProcessor.run();
					baseInvalidProcessor.run();
				}
			}
			
			catch (Exception io) 
			{
				System.out.println("Walk failed for some reason. Please read stack trace...");
				io.printStackTrace();
			}
		}
	}
}