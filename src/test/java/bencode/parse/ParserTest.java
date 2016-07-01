package bencode.parse;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import bencode.exception.BEncodeFormatException;
import bencode.type.BDictionary;
import bencode.type.BInteger;
import bencode.type.BList;
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
  private Object[][] parseStringTestData() {
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
  
  @DataProvider
  private Object[][] parseListTestData() {
    return new Object[][] {
        new Object[] {
            "l9:shdyfngkce".getBytes(), 0, 
            new BList() {
              {
                this.add(new BString("shdyfngkc"));
              }
            },
            13, null
        },
        new Object[] {
            "sssl11:djdj39f029ce".getBytes(), 3, 
            new BList() {
              {
                this.add(new BString("djdj39f029c"));
              }
            }, 
            16, null
        },
        new Object[] {
            "le".getBytes(), 0, new BList(), 2, null
        },
        new Object[] {
            "l".getBytes(), 0, null, null, BEncodeFormatException.class
        },
    };
  }
  
  @DataProvider
  private Object[][] parseDictionaryTestData() {
    return new Object[][] {
        new Object[] {
            "d3:bar4:spame".getBytes(), 0, 
            new BDictionary() {
              {
                this.put(new BString("bar"), new BString("spame"));
              }
            },
            13, null
        },
        new Object[] {
            "sssd3:fooi42ee".getBytes(), 3, 
            new BDictionary() {
              {
                this.put(new BString("foo"), new BInteger(42));
              }
            }, 
            14, null
        },
        new Object[] {
            "de".getBytes(), 0, new BDictionary(), 2, null
        },
        new Object[] {
            "d".getBytes(), 0, null, null, BEncodeFormatException.class
        },
    };
  }
  
  @Test(dataProvider = "parseDictionaryTestData")
  public void parseDic(final byte[] content, int offset, 
      BDictionary expectedValue, Integer expectedContentLength, 
      Class<?> expectedClass) {
    BDictionary parseResult= null;
    Class<?> threwExceptionClass = null;
    try {
      parseResult = parser.parseDic(content, offset);
    } catch (BEncodeFormatException e) {
      threwExceptionClass = e.getClass();
    }
    
    if (null != expectedClass) {
      Assert.assertEquals(threwExceptionClass, expectedClass);
    } else {
      Assert.assertNotNull(parseResult);
      Assert.assertEquals(parseResult, expectedValue);
    }
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

  @Test(dataProvider = "parseListTestData")
  public void parseList(
      final byte[] content, int offset, 
      BList expectedValue, Integer expectedContentLength, 
      Class<?> expectedClass) {
    BList parseResult= null;
    Class<?> threwExceptionClass = null;
    try {
      parseResult = parser.parseList(content, offset);
    } catch (BEncodeFormatException e) {
      threwExceptionClass = e.getClass();
    }
    
    if (null != expectedClass) {
      Assert.assertEquals(threwExceptionClass, expectedClass);
    } else {
      Assert.assertNotNull(parseResult);
      Assert.assertEquals(parseResult, expectedValue);
    }
  }

  @Test(dataProvider = "parseStringTestData")
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
