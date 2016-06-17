package bencode.type;

/**
 * <p>B编码的整数类型。
 * 
 * <p>格式如下：
 * <ul>
 *  <li>整数的基本格式为"i${ASCCI编码中的数字}e"</li>
 *  <li>不允许前导零（但0依然为整数0）</li>
 *  <li>负数在编码后直接加前导负号，不允许负零</li>
 *  <li>示例： 42 -> "i42e", 0 -> "i0e", -42 -> "i-42e"</li>
 * </ul>
 *
 * @author caoxudong
 * @since 0.1.0
 */
public class BInteger implements BType {

}
