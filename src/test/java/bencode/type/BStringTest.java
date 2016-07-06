package bencode.type;

import java.nio.charset.Charset;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class BStringTest {

  @DataProvider
  private Object[][] getEncodeTestData() {
    return new Object[][] {
        new Object[] {
            new BString("12".getBytes()), 
            "2:12".getBytes(Charset.forName(BString.CHARSET_ASCII))
        },
    };
  }
 
  @DataProvider
  private Object[][] getSetContentTestData() {
    return new Object[][] {
        new Object[] {
            "12".getBytes(),
            "2:12".getBytes(Charset.forName(BString.CHARSET_ASCII))
        },
    };
  }
  
  @Test(dataProvider = "getEncodeTestData")
  public void encode(BString target, byte[] expectedValue) {
    Assert.assertEquals(target.encode(), expectedValue);
  }
  
  @Test(dataProvider = "getSetContentTestData")
  public void setContent(byte[] content, byte[] expectedEncodedValue) {
    BString bString = new BString();
    bString.setContent(content);
    Assert.assertEquals(bString.encode(), expectedEncodedValue);
  }
}
