package bencode.parse;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import bencode.exception.BEncodeFormatException;
import bencode.type.BDictionary;
import bencode.type.BNumber;
import bencode.type.BList;
import bencode.type.BString;

public class ParserTest {
  private Parser parser = new Parser();

  @DataProvider
  private Object[][] parseTestData() {
    return new Object[][] {
        new Object[] {
            "/demo.torrent"
        },
    };
  }
  
  @DataProvider
  private Object[][] parseIntTestData() {
    return new Object[][] {
      new Object[] {
          "i".getBytes(), 0, null, null, BEncodeFormatException.class},
      new Object[] {
          "dasd-i".getBytes(), 5, null, null, BEncodeFormatException.class},
      new Object[] {"i43e".getBytes(), 0, 43L, 4, null},
      new Object[] {"i-43e".getBytes(), 0, -43L, 5, null},
      new Object[] {"321-i43e".getBytes(), 4, 43L, 4, null},
      new Object[] {"i0e".getBytes(), 0, 0L, 3, null},
      new Object[] {"i-0e".getBytes(), 0, 0L, 4, BEncodeFormatException.class},
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
          "9:shdyfngkc".getBytes(), 0, "shdyfngkc".getBytes(), 11, null
      },
      new Object[] {
          "11:djdj39f029c".getBytes(), 0, "djdj39f029c".getBytes(), 14, null
      },
      new Object[] {
          "sss11:djdj39f029c".getBytes(), 3, "djdj39f029c".getBytes(), 14, null
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
                this.add(new BString("shdyfngkc".getBytes()));
              }
            },
            13, null
        },
        new Object[] {
            "sssl11:djdj39f029ce".getBytes(), 3, 
            new BList() {
              {
                this.add(new BString("djdj39f029c".getBytes()));
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
                this.put(
                    new BString("bar".getBytes()), 
                    new BString("spam".getBytes()));
              }
            },
            13, null
        },
        new Object[] {
            "sssd3:fooi42ee".getBytes(), 3, 
            new BDictionary() {
              {
                this.put(new BString("foo".getBytes()), new BNumber(42));
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
        new Object[] {
            "d6:lengthi66573515e4:pathl27:h-animatrix-x264-sample.mkvee"
                .getBytes(), 
            0, 
            new BDictionary() {
                {
                  this.put(
                      new BString("length".getBytes()), new BNumber(66573515));
                  this.put(
                      new BString("path".getBytes()), 
                      new BList() {
                        {
                          this.add(
                              new BString(
                                  "h-animatrix-x264-sample.mkv".getBytes()));
                        }
                      });
                }
            }, 
            58, null
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
  public void parseNumber(
      final byte[] content, int offset, 
      Long expectedValue, Integer expectedContentLength, 
      Class<?> expectedClass) {
    BNumber parseResult = null;
    Class<?> threwExceptionClass = null;
    try {
      parseResult = parser.parseNumber(content, offset);
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
      byte[] expectedValue, Integer expectedContentLength, 
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
      Assert.assertEquals(parseResult.getContent(), expectedValue);
    }
  }

  @Test(dataProvider = "parseTestData")
  public void parse(String fileLocation) 
      throws IOException, URISyntaxException {
    URL url = ParserTest.class.getResource(fileLocation);
    URI uri = url.toURI();
    Path path = Paths.get(uri);
    byte[] data = Files.readAllBytes(path);
    BList bList = parser.parse(data, 0, data.length);
    Assert.assertNotNull(bList);
  }
}
