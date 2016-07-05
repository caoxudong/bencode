package bencode.exception;

/**
 * B编码中字符串的编码错误错误。
 *
 * @author caoxudong
 * @since 0.1.0
 */
public class BEncodeCharsetException extends ParseBEncodeException {

  private static final long serialVersionUID = 1L;

  public BEncodeCharsetException(String msg) {
    super(msg);
  }
}
