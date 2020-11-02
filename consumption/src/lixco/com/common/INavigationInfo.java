package lixco.com.common;

public interface INavigationInfo {
	int getTotalPage();// lay tong so page
	int getNextIndex();// lay chi so next
	int getPrevIndex();// lay chi so Prev
	boolean isFirstPage(); 
	boolean isLastPage();
	int[] getIndexRange();// It uses currentPage and maxIndices (maximum number of links to be displayed in the navigation bar) to determine the start and end of page links in the navigation bar.determine the start and end of page links in the navigation bar.
	int[] getIndexList();// convenience method for generating the entire list of navigation indices from the range calculated by getIndexRange().
}
