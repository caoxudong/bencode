package bencode.type;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class BListTest {

  @DataProvider
  private Object[][] getEncodeTestData() {
    return new Object[][] {
        new Object[] {
            new BList() {
              {
                this.add(new BString("shdyfngkc".getBytes()));
              }
            },
            "l9:shdyfngkce".getBytes(),
        },
        new Object[] {
            new BList() {
              {
                this.add(new BString("djdj39f029c".getBytes()));
              }
            }, 
            "l11:djdj39f029ce".getBytes(), 
        },
        new Object[] {
            new BList(), "le".getBytes(),
        },
    };
  }
 
  @Test(dataProvider = "getEncodeTestData")
  public void encode(BList target, byte[] expectedValue) {
    Assert.assertEquals(target.encode(), expectedValue);
  }
  
}
