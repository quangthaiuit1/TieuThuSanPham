package lixco.com.common;

import java.io.Serializable;



public class NavigationInfo implements INavigationInfo,Serializable{
	private static final long serialVersionUID = 1L;
	private int totalRecords;//tong so records vd 1000
	private int currentPage;// trang hien tai lay tu Url default =1
	private int limit;// so records hien thi o moi trang
	private int maxIndices; // so muc luc lon nhat 
	
	public int getTotalRecords() {
		return totalRecords;
	}
	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	// gioi han cua current page tu 1--> total_page(tong so trang)
	public void setCurrentPage(int currentPage) {
		if(currentPage <=1)
			this.currentPage=1;
		else if(currentPage > getTotalPage())
			this.currentPage=getTotalPage();
		else
			this.currentPage=currentPage;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public int getMaxIndices() {
		return maxIndices;
	}
	public void setMaxIndices(int maxIndices) {
		this.maxIndices = maxIndices;
	}
	@Override
	public int getTotalPage() {
		int i=(int) Math.ceil((double)this.totalRecords/this.limit);
		return i==0 ? 1 :i;
	}
	@Override
	public int getNextIndex() {
		int lastIndex=getTotalPage();
		int nextIndex=this.currentPage+1;
		return nextIndex >lastIndex ? lastIndex :nextIndex;
	}
	@Override
	public int getPrevIndex() {
		int prevIndex=this.currentPage-1;
		return prevIndex <0 ? 0: prevIndex;
	}
	@Override
	public boolean isFirstPage() {
		return this.currentPage==0;
	}
	@Override
	public boolean isLastPage() {
		return this.currentPage==getTotalPage();
	}
	// It uses currentPage and maxIndices (maximum number of links to be displayed in the navigation bar)
	//to determine the start and end of page links in the navigation bar.determine the start and end of page links in the navigation bar.
	@Override
	public int[] getIndexRange() {
		int start=this.currentPage-this.maxIndices/2;
		int end=start+this.maxIndices;
		 //di chuyen ve phia ben phai neu underflows=0
		if(start<0){
			end-=start;
			start=0;
		}
		//bay gio neu tran window totalPage
		int lastIndex=getTotalPage();
		if(end> lastIndex){
			start-=(end-lastIndex);
			end=lastIndex;
			
		}
		if(start<0){
			start=0;
		}
		return new int[]{start,end};
	}
	// convenience method for generating the entire list of navigation indices from the range calculated by getIndexRange().
	@Override
	public int[] getIndexList() {
		int[] range=getIndexRange();
		int r=range[1]-range[0];
		int[] list=new int[r];
		for(int i=0;i<list.length;i++){
			list[i]=range[0]+i+1;
		}
		return list;
	}
	
}
