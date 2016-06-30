package bencode.exception;

/**
 * B编码格式错误。
 *
 * @author caoxudong
 * @since 0.1.0
 */
public class ParseBEncodeException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ParseBEncodeException(String msg) {
    super(msg);
  }
}
