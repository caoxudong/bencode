package bencode.type;

/**
 * <p>B编码各种类型的公共接口。
 *
 * @author caoxudong
 * @since 0.1.0
 */
public interface BType<T> {
  
  /**
   * @return 返回当前类型的数据值。
   * @since 0.1.0
   */
  T getContent();
  
  /**
   * 设置当前数据类型的值
   * @param t   当前数据类型的值
   * @since 0.1.0
   */
  void setContent(T t);
  
  /**
   * 
   * @return        返回当前数据在B编码中的长度，包含前缀/后缀/分隔符。
   * @since 0.1.0
   */
  int getContentLength();
  
  /**
   * 
   * @return        返回当前数据的B编码
   * @since 0.1.0
   */
  byte[] encode();
}
