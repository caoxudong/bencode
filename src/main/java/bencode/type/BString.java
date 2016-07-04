package bencode.type;

/**
 * <p>B编码的字符串类型。
 * 
 * <p>格式如下：
 * <ul>
 *  <li>字符串以"长度:内容"编码</li>
 *  <li>长度的值和数字编码方法一样，只是不允许负数</li>
 *  <li>内容就是字符串的内容，如字符串"spam"就会编码为"4:spam"</li>
 *  <li>默认情况下，B编码的字符串只支持ASCII字符</li>
 * </ul>
 *
 * @author caoxudong
 * @since 0.1.0
 */
public class BString implements BType<String>, Comparable<BString> {

  public static final char DELIMITER = ':';

  public BString() {
  }
  
  public BString(String content) {
    this.content = content;
    this.contentLength = content.length() + 1;
  }
  
  private String content;
  private int contentLength = 0;
  
  @Override
  public String getContent() {
    return content;
  }
  
  @Override
  public void setContent(String value) {
    this.content = value;
    int strLength = value.length();
    this.contentLength = String.valueOf(strLength).length() + 1 + strLength;
  }

  @Override
  public int getContentLength() {
    return contentLength;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof BString) {
      String targetContent = ((BString)obj).content;
      return content.equals(targetContent);
    }
    return false;
  }

  @Override
  public int compareTo(BString o) {
    return this.content.compareTo(((BString)o).content);
  }
}
