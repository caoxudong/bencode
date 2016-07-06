package bencode.type;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
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
    for (Entry<BString, BType<?>> entry: content.entrySet()) {
      BString key = entry.getKey();
      BType<?> value = entry.getValue();
      this.contentLength += key.getContentLength() + value.getContentLength();
    }
  }
  
  @Override
  public int getContentLength() {
    return this.contentLength;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof BDictionary)) {
      return false;
    }
    return this.content.equals(((BDictionary)obj).content);
  }

  public BType<?> put(BString key, BType<?> value) {
    if (!this.content.containsKey(key)) {
      this.contentLength += key.getContentLength();
    }
    BType<?> previous = this.content.put(key, value);
    if (null != previous) {
      this.contentLength -= previous.getContentLength();
    }
    if (null != value) {
      this.contentLength += value.getContentLength();    
    }
    return previous;
  }
  
  public BType<?> get(BString key) {
    return this.content.get(key);
  }
  
  public int size() {
    return this.content.size();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("{");
    Set<Entry<BString, BType<?>>> set = content.entrySet();
    Iterator<Entry<BString, BType<?>>> iterator = set.iterator();
    while (iterator.hasNext()) {
      Entry<BString, BType<?>> entry = iterator.next();
      BString key = entry.getKey();
      BType<?> value = entry.getValue();
      sb.append(key.toString()).append(":").append(value.toString());
      if (iterator.hasNext()) {
        sb.append(", ");
      }
    }
    sb.append("}");
    return sb.toString();
  }
  
  @Override
  public byte[] encode() {
    byte[] result = new byte[contentLength];
    result[0] = PREFIX;
    int pos = 1;
    for (Entry<BString, BType<?>> entry: content.entrySet()) {
      BString key = entry.getKey();
      BType<?> value = entry.getValue();
      byte[] keyEncode = key.encode();
      byte[] valueEncode = value.encode();
      for (byte b: keyEncode) {
        result[pos++] = b;
      }
      for (byte b: valueEncode) {
        result[pos++] = b;
      }
    }
    result[contentLength - 1] = SUFFIX;
    return result;
  }
}
