package bencode.type;

/**
 * <p>
 * B编码的整数类型。
 * 
 * <p>
 * 格式如下：
 * <ul>
 * <li>整数的基本格式为"i${ASCCI编码中的数字}e"</li>
 * <li>不允许前导零（但0依然为整数0）</li>
 * <li>负数在编码后直接加前导负号，不允许负零</li>
 * <li>示例： 42 -&gt; "i42e", 0 -&gt; "i0e", -42 -&gt; "i-42e"</li>
 * </ul>
 *
 * <p>
 * BInteger使用Integer存储实际数据，因为在比较对象的时候，
 * 应使用{@link BInteger#equals(Object)}方法。
 *
 * @author caoxudong
 * @since 0.1.0
 */
public class BInteger implements BType<Integer> {

  public static final char PREFIX = 'i';
  public static final char SUFFIX = 'e';

  private Integer value;

  public Integer getValue() {
    return value;
  }
  
  public void setValue(Integer t) {
    this.value = t;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof BInteger) {
      return value == ((BInteger) obj).value.intValue();
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Integer.hashCode(value);
  }
}
