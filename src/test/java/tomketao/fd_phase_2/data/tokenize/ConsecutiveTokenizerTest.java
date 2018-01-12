package tomketao.fd_phase_2.data.tokenize;

import org.testng.annotations.Test;

import tomketao.fd_phase_2.data.tokenize.ConsecutiveTokenizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;

public class ConsecutiveTokenizerTest {
	private List<String> itemList;
	private ConsecutiveTokenizer<String> tkr, tkr2, tkr3, tkr4, tkr5;
	
  @BeforeTest
  public void beforeTest() {
	  itemList = new ArrayList<String>(Arrays.asList("a", "b", "c", "d", "e", "f", "g"));
	  tkr = new ConsecutiveTokenizer<String>(3, 5);
	  tkr2 = new ConsecutiveTokenizer<String>(1, 1);
	  tkr3 = new ConsecutiveTokenizer<String>(0, 1);
	  tkr4 = new ConsecutiveTokenizer<String>(3, 1);
	  tkr5 = new ConsecutiveTokenizer<String>(1, 2);
  }


  @Test
  public void tokenize() {
	  Set<List<String>> returned = tkr.tokenize(itemList);
	  Assert.assertEquals(returned.size(), 12);
	  
	  for (List<String> token : returned) {
		  Assert.assertFalse(token.size() > tkr.getMaximum_size());
		  Assert.assertFalse(token.size() < tkr.getMinimum_size());
	  }
  }
  
  @Test
  public void tokenize2() {
	  Set<List<String>> returned = tkr2.tokenize(itemList);
	  Assert.assertEquals(returned.size(), 7);
	  
	  for (List<String> token : returned) {
		  Assert.assertFalse(token.size() > tkr2.getMaximum_size());
		  Assert.assertFalse(token.size() < tkr2.getMinimum_size());
	  }
  }
  
  @Test
  public void tokenize3() {
	  Set<List<String>> returned = tkr3.tokenize(itemList);
	  Assert.assertEquals(returned.size(), 7);
	  
	  for (List<String> token : returned) {
		  Assert.assertFalse(token.size() > tkr3.getMaximum_size());
		  Assert.assertFalse(token.size() < tkr3.getMinimum_size());
	  }
  }
  
  @Test
  public void tokenize4() {
	  Set<List<String>> returned = tkr4.tokenize(itemList);
	  
	  for (List<String> token : returned) {
		  Assert.assertFalse(token.size() > tkr4.getMaximum_size());
		  Assert.assertFalse(token.size() < tkr4.getMinimum_size());
	  }
  }
  
  @Test
  public void tokenize5() {
	  Set<List<String>> returned = tkr5.tokenize(itemList);
	  
	  for (List<String> token : returned) {
		  Assert.assertFalse(token.size() > tkr5.getMaximum_size());
		  Assert.assertFalse(token.size() < tkr5.getMinimum_size());
	  }
  }
}
