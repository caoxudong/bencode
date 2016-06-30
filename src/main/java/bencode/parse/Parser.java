package bencode.parse;

import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bencode.exception.BEncodeFormatException;
import bencode.type.BDictionary;
import bencode.type.BInteger;
import bencode.type.BList;
import bencode.type.BString;
import bencode.type.BType;

/**
 * <p>
 * 解析器，将一段字节数组转换为B编码的数据。
 *
 * @author caoxudong
 * @since 0.1.0
 */
public class Parser {

  private static Logger logger = LoggerFactory.getLogger(Parser.class);
  
  /**
   * 
   * @param content 待解析的内容
   * @param offset 偏移量，从该偏移量开始解析
   * @return 解析出的数据
   * @since 0.1.0
   */
  public LinkedList<BType<?>> parseNext(final byte[] content, int offset) {
    LinkedList<BType<?>> result = new LinkedList<>();
    for (int i = offset; i < content.length;) {
      byte current = content[i];
      switch (current) {
        case 'i': {
          // integer
          ParseResultTumple<BInteger> parseResult =
              parseInt(content, i);
          i += parseResult.length;
          result.add(parseResult.content);
          break;
        }

        case 'l': {
          // list
          ParseResultTumple<BList> parseResult = 
              parseList(content, i);
          i += parseResult.length;
          result.add(parseResult.content);
          break;
        }

        case 'd': {
          // dictionary
          ParseResultTumple<BDictionary> parseResult =
              parseDic(content, i);
          i += parseResult.length;
          result.add(parseResult.content);
          break;
        }

        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9': {
          // string
          ParseResultTumple<BString> parseResult =
              parseString(content, i);
          i += parseResult.length;
          result.add(parseResult.content);
          break;
        }

        default:
          logger.error(
              "Unexpected char in bencode, char = {}, index = {}", 
              current, i);
          throw new BEncodeFormatException("Unexpected char in bencode");
      }
    }
    return result;
  }

  public ParseResultTumple<BInteger> parseInt(
      final byte[] content, int offset) {
    int i = offset + 1, pin = i;
    int contentLength = content.length;
    boolean isNagetive = false;

    if (i >= contentLength) {
      logger.error("Parsing integer unfinished when reaching the end");
      throw new BEncodeFormatException(
          "Parsing integer unfinished when reaching the end");
    }
    
    if ('-' == content[i]) {
      isNagetive = true;
      i++;
    }

    if (BInteger.SUFFIX == content[i]) {
      logger.error("Numbers not found when parsing integer.");
      throw new BEncodeFormatException(
          "Numbers not found when parsing integer.");
    }
    
    BInteger bInteger = null;
    int value = 0;
    do {
      byte current = content[i];
      if (BInteger.SUFFIX == current) {
        bInteger = new BInteger();
        if (isNagetive) {
          if (0 == value) {
            logger.error("Find invalid nagetive-zero when parsing integer.");
            throw new BEncodeFormatException(
                "Find invalid nagetive-zero when parsing integer.");
          } else {
            value *= -1;
          }
        }
        bInteger.setValue(value);
        break;
      } else {
        if ((i == pin) && ('0' == current) 
            && (i < contentLength) && (BInteger.SUFFIX != content[i + 1])) {
          logger.error("Find unexpected pre-zero when parsing integer.");
          throw new BEncodeFormatException(
              "Find unexpected pre-zero when parsing integer.");
        } else {
          value = value * 10 + (current - '0');
        }
      }
      i++;
    } while(i < contentLength);
    
    if (null == bInteger) {
      logger.error("Parsing integer unfinished when reaching the end");
      throw new BEncodeFormatException(
          "Parsing integer unfinished when reaching the end");
    }
    
    ParseResultTumple<BInteger> result = new ParseResultTumple<>();
    result.content = bInteger;
    result.length = i - offset + 1;
    return result;
  }

  public ParseResultTumple<BString> parseString(
      final byte[] content, int offset) {
    return null;
  }

  public ParseResultTumple<BList> parseList(
      final byte[] content, int offset) {
    return null;
  }

  public ParseResultTumple<BDictionary> parseDic(
      final byte[] content, int offset) {
    return null;
  }

}
