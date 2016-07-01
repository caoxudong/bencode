package bencode.parse;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import bencode.exception.BEncodeFormatException;
import bencode.type.BInteger;
import bencode.type.BString;

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
      new Object[] {"i-43e".getBytes(), 0, -43, 5, null},
      new Object[] {"321-i43e".getBytes(), 4, 43, 4, null},
      new Object[] {"i0e".getBytes(), 0, 0, 3, null},
      new Object[] {"i-0e".getBytes(), 0, 0, 4, BEncodeFormatException.class},
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
  
  @DataProvider
  private Object[][] parseStringTestDate() {
    return new Object[][] {
      new Object[] {
          "9:shdyfngkc".getBytes(), 0, "shdyfngkc", 11, null
      },
      new Object[] {
          "11:djdj39f029c".getBytes(), 0, "djdj39f029c", 14, null
      },
      new Object[] {
          "sss11:djdj39f029c".getBytes(), 3, "djdj39f029c", 14, null
      },
      new Object[] {
          "4:22".getBytes(), 0, null, null, BEncodeFormatException.class
      },
      new Object[] {
          "4".getBytes(), 0, null, null, BEncodeFormatException.class
      },
      new Object[] {
          "4:".getBytes(), 0, null, null, BEncodeFormatException.class
      },
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
    BInteger parseResult = null;
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
          expectedContentLength, 
          Integer.valueOf(parseResult.getContentLength()));
      Assert.assertEquals(expectedValue, parseResult.getContent());
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

  @Test(dataProvider = "parseStringTestDate")
  public void parseString(final byte[] content, int offset, 
      String expectedValue, Integer expectedContentLength, 
      Class<?> expectedClass) {
    BString parseResult = null;
    Class<?> threwExceptionClass = null;
    try {
      parseResult = parser.parseString(content, offset);
    } catch (BEncodeFormatException e) {
      threwExceptionClass = e.getClass();
    }
    
    if (null != expectedClass) {
      Assert.assertEquals(threwExceptionClass, expectedClass);
    } else {
      Assert.assertNotNull(parseResult);
      Assert.assertEquals(
          expectedContentLength, 
          Integer.valueOf(parseResult.getContentLength()));
      Assert.assertEquals(expectedValue, parseResult.getContent());
    }
  }
}
