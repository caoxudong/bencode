package bencode.type;

/**
 * <p>B编码的列表类型。
 * 
 * <p>格式如下：
 * <ul>
 *  <li>列表的基本格式为"l${contents}e"</li>
 *  <li>列表的内容可以是B编码所支持的任一中数据类型</li>
 *  <li>注意，列表中的各个元素之间是没有特定分隔符的</li>
 * </ul>
 *
 * @author caoxudong
 * @since 0.1.0
 */
public class BList implements BType {

}
