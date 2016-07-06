package bencode.type;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class BDictionaryTest {

  @DataProvider
  private Object[][] getEncodeTestData() {
    return new Object[][] {
        new Object[] {
            new BDictionary() {
              {
                this.put(
                    new BString("bar".getBytes()), 
                    new BString("spam".getBytes()));
              }
            },
            "d3:bar4:spame".getBytes(),  
        },
        new Object[] {
            new BDictionary() {
              {
                this.put(new BString("foo".getBytes()), new BNumber(42));
              }
            }, 
            "d3:fooi42ee".getBytes(), 
        },
        new Object[] {
            new BDictionary(),
            "de".getBytes(),
        },
        new Object[] {
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
            "d6:lengthi66573515e4:pathl27:h-animatrix-x264-sample.mkvee"
                .getBytes(), 
        },
    };
  }
 
  @Test(dataProvider = "getEncodeTestData")
  public void encode(BDictionary target, byte[] expectedValue) {
    Assert.assertEquals(target.encode(), expectedValue);
  }
  
}
