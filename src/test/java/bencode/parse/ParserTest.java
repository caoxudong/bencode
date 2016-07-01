package bencode.parse;

import java.util.Iterator;
import java.util.LinkedList;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import bencode.exception.BEncodeFormatException;
import bencode.type.BDictionary;
import bencode.type.BInteger;
import bencode.type.BList;
import bencode.type.BString;
import bencode.type.BType;

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
      
      LinkedList<BType<?>> contentParsed = parseResult.getContent();
      LinkedList<BType<?>> contentExpected = expectedValue.getContent();
      Assert.assertEquals(contentParsed.size(), contentExpected.size());

      // BFS，检查内容列表的内容是否相同
      LinkedList<BType<?>> contentParsedCheckList = new LinkedList<>();
      contentParsedCheckList.addAll(contentParsed);
      LinkedList<BType<?>> contentExpectedCheckList = new LinkedList<>();
      contentExpectedCheckList.addAll(contentExpected);
      Iterator<BType<?>> contentParsedCheckListIterator =
          contentParsedCheckList.iterator();
      Iterator<BType<?>> contentExpectedCheckListIterator =
          contentExpectedCheckList.iterator();
      while (contentParsedCheckListIterator.hasNext() 
          && contentExpectedCheckListIterator.hasNext()) {
        BType<?> elementExpected = contentExpectedCheckListIterator.next();
        Class<?> elementExpectedClass = elementExpected.getClass();
        elementExpected.getContent();
        BType<?> elementParsed = contentParsedCheckListIterator.next();
        Class<?> elementParsedClass = elementParsed.getClass();
        
        Assert.assertEquals(elementExpectedClass, elementParsedClass);
        
        if ((elementExpected instanceof BInteger) 
            || (elementExpected instanceof BString)) {
          Assert.assertEquals(elementExpected, elementParsed);
        } else if (elementExpected instanceof BList) {
          LinkedList<BType<?>> contentExpectedList = 
              ((BList)elementExpected).getContent();
          LinkedList<BType<?>> contentParsedList = 
              ((BList)elementParsed).getContent();
          int elementExpectedSize = contentExpectedList.size();
          int elementParsedSize = contentParsedList.size();
          
          Assert.assertEquals(elementExpectedSize, elementParsedSize);
          
          contentParsedCheckList.addAll(contentParsedList);
          contentExpectedCheckList.addAll(contentExpectedList);
        } else if (elementExpected instanceof BDictionary){
          throw new RuntimeException("Test not implemented");
        }
      }
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
