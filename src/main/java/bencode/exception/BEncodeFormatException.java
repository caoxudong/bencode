package bencode.exception;

/**
 * B编码格式错误。
 *
 * @author caoxudong
 * @since 0.1.0
 */
public class BEncodeFormatException extends ParseBEncodeException {

  private static final long serialVersionUID = 1L;

  public BEncodeFormatException(String msg) {
    super(msg);
  }
}
