package bencode.type;

import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * <p>
 * B编码的字符串类型。
 * 
 * <p>
 * 格式如下：
 * <ul>
 * <li>字符串以"长度:内容"编码</li>
 * <li>长度的值和数字编码方法一样，只是不允许负数</li>
 * <li>内容就是字符串的内容，如字符串"spam"就会编码为"4:spam"</li>
 * <li>默认情况下，B编码的字符串只支持ASCII字符</li>
 * </ul>
 *
 * @author caoxudong
 * @since 0.1.0
 */
public class BString implements BType<byte[]>, Comparable<BString> {

  public static final char DELIMITER = ':';
  public static final String CHARSET_ASCII = "US-ASCII";

  public BString() {}

  public BString(byte[] content) {
    this.content = content;
    int strLength = content.length;
    this.contentLength = String.valueOf(strLength).length() + 1 + strLength;
  }

  private byte[] content;
  private int contentLength = 0;

  @Override
  public byte[] getContent() {
    return content;
  }

  @Override
  public void setContent(byte[] value) {
    this.content = value;
    int strLength = value.length;
    this.contentLength = String.valueOf(strLength).length() + 1 + strLength;
  }

  @Override
  public int getContentLength() {
    return contentLength;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof BString) {
      byte[] targetContent = ((BString) obj).content;
      return Arrays.equals(targetContent, content);
    }
    return false;
  }

  @Override
  public int compareTo(BString o) {
    String selfContentStr = 
        new String(this.content, Charset.forName(CHARSET_ASCII));
    String targetContentStr = 
        new String(o.content, Charset.forName(CHARSET_ASCII));
    return selfContentStr.compareTo(targetContentStr);
  }

  @Override
  public String toString() {
    return new String(content, Charset.forName(CHARSET_ASCII));
  }
  
  @Override
  public byte[] encode() {
    int lengthDiff = contentLength - content.length;
    byte[] result = new byte[contentLength];
    int i = contentLength - 1;
    for (; i >= lengthDiff; i--) {
      result[i] = content[i - lengthDiff];
    }
    result[i--] = DELIMITER;
    int tempContentLength = content.length;
    while (i >= 0) {
      result[i--] = (byte)('0' + (tempContentLength % 10));
      tempContentLength = tempContentLength / 10;
    }
    return result;
  }
}
