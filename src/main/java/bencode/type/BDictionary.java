package bencode.type;

import java.util.TreeMap;

/**
 * <p>B编码的字典类型。
 * 
 * <p>格式如下：
 * <ul>
 *  <li>字典的基本格式为"d${contents}e"</li>
 *  <li>key必须是字符串类型，且按照字典顺序排列</li>
 *  <li>字典元素的key和value紧跟在一起，并无特殊分隔符</li>
 *  <li>示例： "bar-&gt;spam" -&gt; "d3:bar4:spame", "foo:42" -&gt; "d3:fooi42ee", 
 *  "bar-&gt;spam, foo-&gt;42" -&gt; "d3:bar4:spam3:fooi42ee"</li>
 * </ul>
 *
 * @author caoxudong
 * @since 0.1.0
 */
public class BDictionary implements BType<TreeMap<BString, BType<?>>> {

  public static final char PREFIX = 'd';
  public static final char SUFFIX = 'e';
  
  private TreeMap<BString, BType<?>> content = new TreeMap<>();
  private int contentLength = 2;
  
  @Override
  public TreeMap<BString, BType<?>> getContent() {
    return this.content;
  }
  @Override
  public void setContent(TreeMap<BString, BType<?>> content) {
    this.content = content;
  }
  
  @Override
  public int getContentLength() {
    return this.contentLength;
  }

  public BType<?> put(BString key, BType<?> value) {
    return this.content.put(key, value);
  }
  
}
