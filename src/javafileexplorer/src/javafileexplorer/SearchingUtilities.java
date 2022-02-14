package javafileexplorer;

import java.util.List;

public class SearchingUtilities {
	public static int searchList(String targetPathway, List<String> searchList) {
		int leftIndex = 0;
		int rightIndex = searchList.size() - 1;
		
		while (leftIndex <= rightIndex) {
			String leftPath = searchList.get(leftIndex);
			String rightPath = searchList.get(rightIndex);
			if ((leftIndex == rightIndex || leftIndex < rightIndex) && (leftPath == targetPathway)) {
				return leftIndex;
			}
			else if (rightPath == targetPathway) {
				return rightIndex;
			}
			
			leftIndex++;
			rightIndex--;
		}
		
		return -1;
	}
}
