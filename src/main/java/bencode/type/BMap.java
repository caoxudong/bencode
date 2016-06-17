package bencode.type;

/**
 * <p>B编码的字典类型。
 * 
 * <p>格式如下：
 * <ul>
 *  <li>字典的基本格式为"d${contents}e"</li>
 *  <li>key必须是字符串类型，且按照字典顺序排列</li>
 *  <li>字典元素的key和value紧跟在一起，并无特殊分隔符</li>
 *  <li>示例： "bar->spam" -> "d3:bar4:spame", "foo:42" -> "d3:fooi42ee", 
 *  "bar->spam, foo->42" -> "d3:bar4:spam3:fooi42ee"</li>
 * </ul>
 *
 * @author caoxudong
 * @since 0.1.0
 */
public class BMap implements BType {

}
