package javafileexplorer;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;

public class MyFileVisitor implements FileVisitor<Path>
{	
	WatchService service;
	Populator pop;
	
	MyFileVisitor (WatchService passedService, Populator basePop) 
	{
		pop = basePop;
		service = passedService;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path folderPath, BasicFileAttributes attr) throws IOException 
	{
		pop.addPathToFolderMap(folderPath.toAbsolutePath(), service);
		return FileVisitResult.CONTINUE;
	}
	
	@Override
	public FileVisitResult visitFile(Path filePath, BasicFileAttributes fileAttrs) 
	{
		pop.addPathToFileMap(filePath.toAbsolutePath(), service);
		return FileVisitResult.CONTINUE;
	}
	
	@Override
	public FileVisitResult visitFileFailed(Path pathway, IOException io) 
	{
		if (InputValidation.validateFolder(pathway) == true) 
		{
			pop.removePathFromFolderMap(pathway.toAbsolutePath());
			return FileVisitResult.SKIP_SUBTREE;
		}
		
		else if (InputValidation.validateFile(pathway) == true) 
		{
			pop.removePathFromFileMap(pathway.toAbsolutePath());
			return FileVisitResult.CONTINUE;
		}
		
		else 
		{
			return FileVisitResult.CONTINUE;
		}
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException 
	{
		return FileVisitResult.CONTINUE;
	}
}