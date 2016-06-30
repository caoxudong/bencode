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
public class BString implements BType<String> {

  public static final char DELIMITER = ':';
  
  private String value;
  
  @Override
  public String getValue() {
    return value;
  }
  
  @Override
  public void setValue(String value) {
    this.value = value;
  }
  
}
