package javafileexplorer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashSet;
import java.util.Hashtable;

import org.apache.commons.io.FilenameUtils;

public class Populator
{
	Container popContainer;
	
	Populator(Container container) 
	{
		popContainer = container;
	}

	private boolean addToFolderDetails(Path currentPath, Path parentPath) 
	{
		if (parentPath == null) 
		{
			return false;
		}
		
		String parentPathToString = parentPath.toString();
		String parentStringFolderName = FilenameUtils.getBaseName(parentPathToString);
		
		if (InputValidation.validateFile(currentPath) == true) 
		{
			if (popContainer.allFoldersMap.containsKey(parentStringFolderName) == true)  
			{
				Hashtable<Path, Hashtable<String, HashSet<Path>>> allFolderPaths = popContainer.allFoldersMap.get(parentStringFolderName);
				
				if (allFolderPaths.containsKey(parentPath) == true) 
				{
					Hashtable<String, HashSet<Path>> folderDetails = allFolderPaths.get(parentPath);
					HashSet<Path> allFiles = folderDetails.get("Files");
					
					if (allFiles.contains(currentPath) == false) 
					{
						allFiles.add(currentPath);
						return true;
					}
				}
			}
			
			return false;
		}
		
		else if (InputValidation.validateFolder(currentPath) == true) 
		{
			if (popContainer.allFoldersMap.containsKey(parentStringFolderName) == true) 
			{
				Hashtable<Path, Hashtable<String, HashSet<Path>>> allFolderPaths = popContainer.allFoldersMap.get(parentStringFolderName);
				
				if (allFolderPaths.containsKey(parentPath) == true) 
				{
					Hashtable<String, HashSet<Path>> folderDetails = allFolderPaths.get(parentPath);
					HashSet<Path> allSubDirs = folderDetails.get("Directories");
					
					if (allSubDirs.contains(currentPath) == false) 
					{
						allSubDirs.add(currentPath);
						return true;
					}
				}
			}
			
			return false;
		}
		
		return false;
	}
	
	public boolean addPathToFileMap(Path filePath, WatchService service) 
	{
		if (InputValidation.validateFile(filePath) == false) 
		{
			return false;
		}
		
		String pathToString = filePath.toString();
		String fileBasename = FilenameUtils.getBaseName(pathToString);
		String fileExtension = FilenameUtils.getExtension(pathToString);
		
		Path parentDir = filePath.getParent();
		this.addToFolderDetails(filePath, parentDir);
		
		if (popContainer.allFilesMap.containsKey(fileExtension) == true) 
		{
			Hashtable<String, HashSet<Path>> allFileBasenames = popContainer.allFilesMap.get(fileExtension);
			
			if (allFileBasenames.containsKey(fileBasename) == true) 
			{
				HashSet<Path> filePaths = allFileBasenames.get(fileBasename);
				
				if (filePaths.contains(filePath) == true) 
				{
					return false;
				}
				
				else 
				{
					filePaths.add(filePath);
					return true;
				}
			}
			
			else 
			{
				HashSet<Path> allFilePaths = new HashSet<Path>();
				allFilePaths.add(filePath);
				allFileBasenames.put(fileBasename, allFilePaths);
				
				return true;
			}
		}
		
		else 
		{
			Hashtable<String, HashSet<Path>> allFileBasenames = new Hashtable<String, HashSet<Path>>();
			HashSet<Path> allFilePaths = new HashSet<Path>();
			
			allFilePaths.add(filePath);
			allFileBasenames.put(fileBasename, allFilePaths);
			popContainer.allFilesMap.put(fileExtension, allFileBasenames);
			
			return true;
		}
	}
	
	public boolean addPathToFolderMap(Path folderPath, WatchService service) 
	{	
		String pathToString = folderPath.toString();
		String folderName = FilenameUtils.getBaseName(pathToString);
		
		Path parentPath = folderPath.getParent();
		
		if (parentPath != null) 
		{
			parentPath = parentPath.toAbsolutePath();
		}
		
		this.addToFolderDetails(folderPath, parentPath);
		
		try 
		{
			WatchKey currentKey = folderPath.register(service, StandardWatchEventKinds.ENTRY_CREATE, 
					StandardWatchEventKinds.ENTRY_DELETE);
			
			popContainer.pathKeyMap.put(folderPath, currentKey);
		}
		
		catch (IOException io) 
		{
			io.printStackTrace();
			return false;
		}
		
		if (popContainer.allFoldersMap.containsKey(folderName) == true) 
		{
			Hashtable<Path, Hashtable<String, HashSet<Path>>> allFolderPathDetails = popContainer.allFoldersMap.get(folderName);
			
			if (allFolderPathDetails.containsKey(folderPath) == true) 
			{
				return false;
			}
			
			else 
			{
				Hashtable<String, HashSet<Path>> allFolderDetails = new Hashtable<String, HashSet<Path>>();
				HashSet<Path> emptySet = new HashSet<Path>();
				
				allFolderDetails.put("Files", emptySet);
				allFolderDetails.put("Directories", emptySet);
				
				popContainer.allFoldersMap.put(folderName, allFolderPathDetails);
				
				return true;
			}
		}
		
		else 
		{
			Hashtable<Path, Hashtable<String, HashSet<Path>>> allFolderPathDetails = 
					new Hashtable<Path, Hashtable<String, HashSet<Path>>>();
			
			Hashtable<String, HashSet<Path>> folderDetails = new Hashtable<String, HashSet<Path>>();
			
			HashSet<Path> emptyFolderSet = new HashSet<Path>();
			HashSet<Path> emptyFilesSet = new HashSet<Path>();
			
			folderDetails.put("Files", emptyFolderSet);
			folderDetails.put("Directories", emptyFilesSet);
			allFolderPathDetails.put(folderPath, folderDetails);
			popContainer.allFoldersMap.put(folderName, allFolderPathDetails);
			
			return true;
		}
	}
	
	public boolean removePathFromFolderMap(Path folderPath) 
	{
		String pathToString = folderPath.toString();
		String folderName = FilenameUtils.getBaseName(pathToString);
		
		if (popContainer.allFoldersMap.containsKey(folderName) == true) 
		{
			Hashtable<Path, Hashtable<String, HashSet<Path>>> allFolderPaths = popContainer.allFoldersMap.get(folderName);
			
			if (allFolderPaths.containsKey(folderPath) == true) 
			{
				HashSet<Path> allFiles = allFolderPaths.get(folderPath).get("Files");
				HashSet<Path> allSubDirectories = allFolderPaths.get(folderPath).get("Directories");
				
				for (Path filePath : allFiles) 
				{
					String filePathToString = filePath.toString();
					String filePathExtension = FilenameUtils.getExtension(filePathToString);
					String filePathBasename = FilenameUtils.getBaseName(filePathToString);
					
					if (popContainer.allFilesMap.containsKey(filePathExtension) == true) 
					{
						Hashtable<String, HashSet<Path>> allFileBasenames = popContainer.allFilesMap.get(filePathExtension);
						
						if (allFileBasenames.containsKey(filePathBasename) == true) 
						{
							HashSet<Path> allFilePaths = allFileBasenames.get(filePathBasename);
							
							if (allFilePaths.contains(filePath) == true) 
							{
								allFilePaths.remove(filePath);
							}
						}
					}
				}
				
				for (Path dirPath : allSubDirectories) 
				{
					String dirPathToString = dirPath.toString();
					String folderBasename = FilenameUtils.getBaseName(dirPathToString);
					
					this.removePathFromFolderMap(dirPath);
					
					if (popContainer.allFoldersMap.containsKey(folderBasename) == true) 
					{
						Hashtable<Path, Hashtable<String, HashSet<Path>>> folderPaths = popContainer.allFoldersMap.get(folderBasename);
						
						if (folderPaths.containsKey(dirPath) == true) 
						{
							folderPaths.remove(dirPath);
						}
					}
				}
				
				if (popContainer.pathKeyMap.containsKey(folderPath) == true) 
				{
					WatchKey key = popContainer.pathKeyMap.get(folderPath);
					key.cancel();
					popContainer.pathKeyMap.remove(folderPath);
				}
			}
			
			return true;
		}
		
		else 
		{
			return false;
		}
	}
	
	public boolean removePathFromFileMap(Path filePath) 
	{
		String pathToString = filePath.toString();
		String fileBasename = FilenameUtils.getBaseName(pathToString);
		String fileExtension = FilenameUtils.getExtension(pathToString);
		
		if (popContainer.allFilesMap.containsKey(fileExtension) == true) 
		{
			Hashtable<String, HashSet<Path>> allBasenames = popContainer.allFilesMap.get(fileExtension);
			
			if (allBasenames.containsKey(fileBasename) == true) 
			{
				HashSet<Path> allPaths = allBasenames.get(fileBasename);
				
				if (allPaths.contains(filePath) == true) 
				{
					allPaths.remove(filePath);
				}
			}
		}
		
		Path parentPath = filePath.getParent();
		String folderName = parentPath.toString();
		
		if (popContainer.allFoldersMap.containsKey(folderName) == true) 
		{
			Hashtable<Path, Hashtable<String, HashSet<Path>>> allFolderPaths = popContainer.allFoldersMap.get(folderName);
	
			if (allFolderPaths.containsKey(parentPath) == true) 
			{
				HashSet<Path> allFiles = allFolderPaths.get(parentPath).get("Files");
				
				if (allFiles.contains(filePath) == true) 
				{
					allFiles.remove(filePath);
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean walkThroughDirectory(WatchService service, Populator populator) 
	{
		System.out.println("Walk has started...");
		
		try
		{
			Files.walkFileTree(popContainer.inputDirectory, new MyFileVisitor(service, populator)); 
			System.out.println("Walk has ended...");
			
			return true;
		}
		
		catch (IOException io) 
		{
			io.printStackTrace();
			return false;
		}
	}
}