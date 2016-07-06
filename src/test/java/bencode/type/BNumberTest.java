package bencode.type;

import java.nio.charset.Charset;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class BNumberTest {

  @DataProvider
  private Object[][] getEncodeTestData() {
    return new Object[][] {
        new Object[] {
            new BNumber(12L), 
            "i12e".getBytes(Charset.forName(BString.CHARSET_ASCII))
        },
    };
  }

  @DataProvider
  private Object[][] getSetContentTestData() {
    return new Object[][] {
        new Object[] {
            12L, 4
        },
    };
  }
  
  @Test(dataProvider = "getEncodeTestData")
  public void encode(BNumber target, byte[] expectedValue) {
    Assert.assertEquals(target.encode(), expectedValue);
  }
  
  @Test(dataProvider = "getSetContentTestData")
  public void setContent(Long content, int expectedContentLength) {
    BNumber bNumber = new BNumber();
    bNumber.setContent(content);
    Assert.assertEquals(bNumber.getContentLength(), expectedContentLength);
  }
}
