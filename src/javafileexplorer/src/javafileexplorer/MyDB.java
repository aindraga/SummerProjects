package javafileexplorer;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import org.apache.commons.io.FilenameUtils;

public class MyDB
{	
	// This is the main inputed directory into the DB Object. 
	Path inputtedDirectory;
	
	// These are the two main tables to query from for files and folders.
	Hashtable<String, Hashtable<String, HashSet<Path>>> allFilesMap = new Hashtable<String, Hashtable<String, HashSet<Path>>>();
	
	Hashtable<String, Hashtable<Path, Hashtable<String, HashSet<Path>>>> allFolderMap = 
			new Hashtable<String, Hashtable<Path, Hashtable<String, HashSet<Path>>>>();
	
	// Process newly created directories
	private Hashtable<String, Queue<Path>> newDirsMap = new Hashtable<String, Queue<Path>>();
	
	// Path to Key Hash table
	private Hashtable<Path, WatchKey> pathKeyMap = new Hashtable<Path, WatchKey>();
	
	
	// An overloaded constructor that takes in a String directory path as a parameter to operate on.
	MyDB (String inputtedDirectory) 
	{
		// Initializes the Path of the inputted directory
		this.inputtedDirectory = Paths.get(inputtedDirectory);
		
		// Creates an empty Queue to add to the new folder processor hash-table
		Queue<Path> emptyQueueCreate = new PriorityQueue<Path>();
		Queue<Path> emptyQueueDelete = new PriorityQueue<Path>();
		
		this.newDirsMap.put("created", emptyQueueCreate);
		this.newDirsMap.put("deleted", emptyQueueDelete);
	}
	
	// Validates the base folder input and returns the resulting boolean value
	private boolean baseFolderValidation() 
	{
		if (Files.isDirectory(inputtedDirectory) == false) 
		{
			System.out.println("The inputted pathway is not a directory. Please try again...");
			return false;
		}
		
		return true;
	}
	
	// Processes files to make sure they are regular files and not temporary/system/other unnecessary files
	public boolean isValidFile(Path currentPath) 
	{
		if (Files.isDirectory(currentPath) == false && Files.isRegularFile(currentPath) == true) 
		{
			return true;
		}
		
		return false;
	}
	
	// Method to add folder pathway to the folder hash-table
	private boolean addToFolderDetails(Path currentPath, Path parentPath) 
	{
		// C:\\ return null and therefore does not process this function
		if (parentPath == null) 
		{
			return false;
		}
		
		// String conversion of paths
		String parentPathToString = parentPath.toString();
		String parentStringFolderName = FilenameUtils.getBaseName(parentPathToString);
		
		// Validate file
		if (this.isValidFile(currentPath) == true) 
		{
			// Check if parent folder name in hash-table
			if (this.allFolderMap.containsKey(parentStringFolderName) == true)  
			{
				// Get folder name
				Hashtable<Path, Hashtable<String, HashSet<Path>>> allFolderPaths = this.allFolderMap.get(parentStringFolderName);
				
				// Check if the path is in the hash-table
				if (allFolderPaths.containsKey(parentPath) == true) 
				{
					// Get the First layer underneath the folder
					Hashtable<String, HashSet<Path>> folderDetails = allFolderPaths.get(parentPath);
					HashSet<Path> allFiles = folderDetails.get("Files");
					
					// Add to the first layer details if the file is not contained within the 
					if (allFiles.contains(currentPath) == false) 
					{
						allFiles.add(currentPath);
						return true;
					}
				}
			}
			
			// If any of the above conditions are not met, return false
			return false;
		}
		
		// Validation to avoid any mysterious corner cases and keep the project simple for now
		else if (Files.isDirectory(currentPath) == true) 
		{
			// If the parent is in the folder hash-table
			if (this.allFolderMap.containsKey(parentStringFolderName) == true) 
			{
				// Obtain the paths of that basename
				Hashtable<Path, Hashtable<String, HashSet<Path>>> allFolderPaths = this.allFolderMap.get(parentStringFolderName);
				
				// If the parent path is contained in the above map
				if (allFolderPaths.containsKey(parentPath) == true) 
				{
					// Obtain the level 1 sub directories of the folder
					Hashtable<String, HashSet<Path>> folderDetails = allFolderPaths.get(parentPath);
					HashSet<Path> allSubDirs = folderDetails.get("Directories");
					
					// If the subdirectory is not in the subdirectories set, add the path
					if (allSubDirs.contains(currentPath) == false) 
					{
						allSubDirs.add(currentPath);
						return true;
					}
				}
			}
			
			// Return false if any of the above conditions are not met
			return false;
		}
		
		// Return false on any mysterious corner cases
		return false;
	}
	
	public boolean addPathToFileMap(Path filePath, WatchService service) 
	{
		if (this.isValidFile(filePath) == false) 
		{
			return false;
		}
		
		String pathToString = filePath.toString();
		String fileBasename = FilenameUtils.getBaseName(pathToString);
		String fileExtension = FilenameUtils.getExtension(pathToString);
		
		Path parentDir = filePath.getParent();
		this.addToFolderDetails(filePath, parentDir);
		
		if (this.allFilesMap.containsKey(fileExtension) == true) 
		{
			Hashtable<String, HashSet<Path>> allFileBasenames = this.allFilesMap.get(fileExtension);
			
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
			this.allFilesMap.put(fileExtension, allFileBasenames);
			
			return true;
		}
	}
	
	public boolean addPathToFolderMap(Path folderPath, WatchService service) 
	{	
		String pathToString = folderPath.toString();
		String folderName = FilenameUtils.getBaseName(pathToString);
		
		Path parentPath = folderPath.getParent().toAbsolutePath();
		this.addToFolderDetails(folderPath, parentPath);
		
		try 
		{
			WatchKey currentKey = folderPath.register(service, StandardWatchEventKinds.ENTRY_CREATE, 
					StandardWatchEventKinds.ENTRY_DELETE);
			
			this.pathKeyMap.put(folderPath, currentKey);
		}
		catch (IOException io) 
		{
			io.printStackTrace();
			return false;
		}
		
		if (this.allFolderMap.containsKey(folderName) == true) 
		{
			Hashtable<Path, Hashtable<String, HashSet<Path>>> allFolderPathDetails = this.allFolderMap.get(folderName);
			
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
				
				this.allFolderMap.put(folderName, allFolderPathDetails);
				
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
			this.allFolderMap.put(folderName, allFolderPathDetails);
			
			return true;
		}
	}
	
	public boolean removePathFromFolderMap(Path folderPath) 
	{
		String pathToString = folderPath.toString();
		String folderName = FilenameUtils.getBaseName(pathToString);
		
		if (this.allFolderMap.containsKey(folderName) == true) 
		{
			Hashtable<Path, Hashtable<String, HashSet<Path>>> allFolderPaths = this.allFolderMap.get(folderName);
			
			if (allFolderPaths.containsKey(folderPath) == true) 
			{
				HashSet<Path> allFiles = allFolderPaths.get(folderPath).get("Files");
				HashSet<Path> allSubDirectories = allFolderPaths.get(folderPath).get("Directories");
				
				for (Path filePath : allFiles) 
				{
					String filePathToString = filePath.toString();
					String filePathExtension = FilenameUtils.getExtension(filePathToString);
					String filePathBasename = FilenameUtils.getBaseName(filePathToString);
					
					if (this.allFilesMap.containsKey(filePathExtension) == true) 
					{
						Hashtable<String, HashSet<Path>> allFileBasenames = this.allFilesMap.get(filePathExtension);
						
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
					
					if (this.allFolderMap.containsKey(folderBasename) == true) 
					{
						Hashtable<Path, Hashtable<String, HashSet<Path>>> folderPaths = this.allFolderMap.get(folderBasename);
						
						if (folderPaths.containsKey(dirPath) == true) 
						{
							folderPaths.remove(dirPath);
						}
					}
				}
				
				if (this.pathKeyMap.containsKey(folderPath) == true) 
				{
					WatchKey key = this.pathKeyMap.get(folderPath);
					key.cancel();
					this.pathKeyMap.remove(folderPath);
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
		
		if (this.allFilesMap.containsKey(fileExtension) == true) 
		{
			Hashtable<String, HashSet<Path>> allBasenames = this.allFilesMap.get(fileExtension);
			
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
		
		if (this.allFolderMap.containsKey(folderName) == true) 
		{
			Hashtable<Path, Hashtable<String, HashSet<Path>>> allFolderPaths = this.allFolderMap.get(folderName);
			
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
	
	private static boolean walkThroughDirectory(WatchService service, MyDB db) 
	{
		try
		{
			Files.walkFileTree(db.inputtedDirectory, new SimpleFileVisitor<Path>() 
			{
				@Override 
				public FileVisitResult preVisitDirectory(Path folderPath, BasicFileAttributes attr) throws IOException 
				{
					db.addPathToFolderMap(folderPath.toAbsolutePath(), service);
					return FileVisitResult.CONTINUE;
				}
				
				@Override
				public FileVisitResult visitFile(Path filePath, BasicFileAttributes fileAttrs) 
				{
					db.addPathToFileMap(filePath.toAbsolutePath(), service);
					return FileVisitResult.CONTINUE;
				}
				
				@Override
				public FileVisitResult visitFileFailed(Path pathway, IOException io) 
				{
					if (Files.isDirectory(pathway) == true) 
					{
						db.removePathFromFolderMap(pathway.toAbsolutePath());
						return FileVisitResult.SKIP_SUBTREE;
					}
					
					else if (db.isValidFile(pathway) == true) 
					{
						db.removePathFromFileMap(pathway.toAbsolutePath());
						return FileVisitResult.CONTINUE;
					}
					
					else 
					{
						return FileVisitResult.CONTINUE;
					}
				}
			}
					);
			return true;
		}
		catch (IOException io) 
		{
			io.printStackTrace();
			return false;
		}
	}
	
	public static boolean callWalker(MyDB db) 
	{
		if (db.baseFolderValidation() == false) 
		{
			return false;
		}
		
		try (WatchService service = FileSystems.getDefault().newWatchService()) 
		{
			MyDB.walkThroughDirectory(service, db);
			return true;
		}
		catch (IOException io) 
		{
			io.printStackTrace();
			return false;
		}
	}
}