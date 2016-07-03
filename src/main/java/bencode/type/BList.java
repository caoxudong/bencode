package bencode.type;

import java.util.Collection;
import java.util.LinkedList;

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
public class BList implements BType<LinkedList<BType<?>>> {

  public static final char PREFIX = 'l';
  public static final char SUFFIX = 'e';
  
  private LinkedList<BType<?>> content = new LinkedList<>();
  private int contentLength = 2;
  
  @Override
  public LinkedList<BType<?>> getContent() {
    return this.content;
  }
  
  @Override
  public void setContent(LinkedList<BType<?>> value) {
    int newValueLength = 0;
    for (BType<?> e: value) {
      newValueLength += e.getContentLength();
    }
    this.content = value;
    this.contentLength = newValueLength + 2;
  }
  
  @Override
  public int getContentLength() {
    return contentLength;
  }
  
  public void add(BType<?> bElement) {
    this.content.add(bElement);
    this.contentLength += bElement.getContentLength();
  }
  
  public void addAll(Collection<? extends BType<?>> list) {
    this.content.addAll(list);
    for (BType<?> e: content) {
      this.contentLength += e.getContentLength();
    }
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof BList)) {
      return false;
    }
    return this.content.equals(((BList)obj).content);
  }
}
