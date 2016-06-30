package bencode.parse;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import bencode.exception.BEncodeFormatException;
import bencode.type.BInteger;

public class ParserTest {
  private Parser parser = new Parser();

  @DataProvider
  private Object[][] parseIntTestData() {
    return new Object[][] {
      new Object[] {
          "i".getBytes(), 0, null, null, BEncodeFormatException.class},
      new Object[] {
          "dasd-i".getBytes(), 5, null, null, BEncodeFormatException.class},
      new Object[] {"i43e".getBytes(), 0, 43, 4, null},
      new Object[] {"321-i43e".getBytes(), 4, 43, 4, null},
      new Object[] {"i0e".getBytes(), 0, 0, 3, null},
      new Object[] {
          "i000e".getBytes(), 0, null, null, BEncodeFormatException.class},
      new Object[] {
          "i032e".getBytes(), 0, null, null, BEncodeFormatException.class},
      new Object[] {
          "i32".getBytes(), 0, null, null, BEncodeFormatException.class},
          new Object[] {
          "ie".getBytes(), 0, null, null, BEncodeFormatException.class},
    };
  }
  
  @Test(enabled = false)
  public void parseDic(final byte[] content, int offset) {
    throw new RuntimeException("Test not implemented");
  }

  @Test(dataProvider = "parseIntTestData")
  public void parseInt(
      final byte[] content, int offset, 
      Integer expectedValue, Integer expectedContentLength, 
      Class<?> expectedClass) {
    ParseResultTumple<BInteger> parseResult = null;
    Class<?> threwExceptionClass = null;
    try {
      parseResult = parser.parseInt(content, offset);
    } catch (BEncodeFormatException e) {
      threwExceptionClass = e.getClass();
    }
    
    if (null != expectedClass) {
      Assert.assertEquals(threwExceptionClass, expectedClass);
    } else {
      Assert.assertNotNull(parseResult);
      Assert.assertEquals(
          expectedContentLength, Integer.valueOf(parseResult.length));
      Assert.assertEquals(expectedValue, parseResult.content.getValue());
    }
  }

  @Test(enabled = false)
  public void parseList(final byte[] content, int offset) {
    throw new RuntimeException("Test not implemented");
  }

  @Test(enabled = false)
  public void parseNext(byte[] content, int offset) {
    throw new RuntimeException("Test not implemented");
  }

  @Test(enabled = false)
  public void parseString(final byte[] content, int offset) {
    throw new RuntimeException("Test not implemented");
  }
}
