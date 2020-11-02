package lixco.com.common;


public class PagingInfo
{
  private long total_page;
  

  private long total_row;
  


  public PagingInfo() {}
  

  public long getTotalPage()
  {
    return total_page;
  }
  
  public void setTotalPage(long p) { total_page = p; }
  
  public long getTotalRow() {
    return total_row;
  }
  
  public void setTotalRow(long r) { total_row = r; }
}
