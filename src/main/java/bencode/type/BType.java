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
  T getValue();
  
  /**
   * 设置当前数据类型的值
   * @param t   当前数据类型的值
   * @since 0.1.0
   */
  void setValue(T t);
  
}
