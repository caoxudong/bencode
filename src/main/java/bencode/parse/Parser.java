package bencode.parse;

import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bencode.exception.BEncodeFormatException;
import bencode.type.BDictionary;
import bencode.type.BInteger;
import bencode.type.BList;
import bencode.type.BString;

/**
 * <p>
 * 解析器，将一段字节数组转换为B编码的数据。https://en.wikipedia.org/wiki/Bencode
 *
 * @author caoxudong
 * @since 0.1.0
 */
public class Parser {

  private static Logger logger = LoggerFactory.getLogger(Parser.class);
  
  /**
   * <p>逐字节解析，遇到相应的类型前缀后，就调用目标类型的解析方法完成解析任务，
   * 返回解析结果，并增加相应的偏移量。
   * @param content 待解析的内容
   * @param offset 偏移量，从该偏移量开始解析
   * @return 解析出的数据
   * @since 0.1.0
   */
  public BList parseNext(final byte[] content, int offset) {
    BList result = new BList();
    for (int i = offset; i < content.length;) {
      byte current = content[i];
      switch (current) {
        case 'i': {
          // integer
          BInteger bInteger = parseInt(content, i);
          i += bInteger.getContentLength();
          result.add(bInteger);
          break;
        }

        case 'l': {
          // list
          BList bList = parseList(content, i);
          i += bList.getContentLength();
          result.add(bList);
          break;
        }

        case 'd': {
          // dictionary
          BDictionary bDictionary = parseDic(content, i);
          i += bDictionary.getContentLength();
          result.add(bDictionary);
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
          BString bString = parseString(content, i);
          i += bString.getContentLength();
          result.add(bString);
          break;
        }

        default:
          logger.error(
              "Unexpected char in bencode, char = {}, pos = {}", 
              current, i);
          throw new BEncodeFormatException(
              "Unexpected char in bencode, char = " 
                  + (char)current + ", pos = " + i);
      }
    }
    return result;
  }

  /**
   * <p>解析整数。
   * @param content     带解析的字节数组
   * @param offset      偏移量，从某个位置开始解析
   * @return            解析结果
   * @since 0.1.0
   */
  public BInteger parseInt(final byte[] content, int offset) {
    int i = offset + 1, pin = i;
    int contentLength = content.length;
    boolean isNagetive = false;

    if (i >= contentLength) {
      logger.error(
          "Parsing integer unfinished when reaching the end, starting pos = {}", 
          offset);
      throw new BEncodeFormatException(
          "Parsing integer unfinished when reaching the end, starting pos = " 
              + offset);
    }
    
    if ('-' == content[i]) {
      isNagetive = true;
      i++;
    }

    if (BInteger.SUFFIX == content[i]) {
      logger.error("Numbers not found when parsing integer, pos = {}", i);
      throw new BEncodeFormatException(
          "Numbers not found when parsing integer, pos = " + i);
    }
    
    BInteger bInteger = null;
    int value = 0;
    do {
      byte current = content[i];
      if (BInteger.SUFFIX == current) {
        bInteger = new BInteger();
        if (isNagetive) {
          if (0 == value) {
            logger.error(
                "Find invalid nagetive-zero when parsing integer, pos = {}", i); 
            throw new BEncodeFormatException(
                "Find invalid nagetive-zero when parsing integer, pos = " + i); 
          } else {
            value *= -1;
          }
        }
        bInteger.setContent(value);
        break;
      } else {
        if ((i == pin) && ('0' == current) 
            && (i < contentLength) && (BInteger.SUFFIX != content[i + 1])) {
          logger.error(
              "Find unexpected pre-zero when parsing integer, pos = {}", i);
          throw new BEncodeFormatException(
              "Find unexpected pre-zero when parsing integer, pos = " + i);
        } else {
          value = value * 10 + (current - '0');
        }
      }
      i++;
    } while(i < contentLength);
    
    if (null == bInteger) {
      logger.error(
          "Parsing integer unfinished when reaching the end, starting pos = {}", 
          offset);
      throw new BEncodeFormatException(
          "Parsing integer unfinished when reaching the end, starting post = " 
              + offset);
    }
    
    return bInteger;
  }

  /**
   * 解析字符串数据
   * @param content     带解析的字节数组
   * @param offset      偏移量，从某个位置开始解析
   * @return            解析结果
   * @since 0.1.0
   */
  public BString parseString(final byte[] content, int offset) {
    int i = offset;
    int value = 0;
    int contentLength = content.length;

    BString bString = null; 
    do {
      byte current = content[i++];
      if (BString.DELIMITER != current) {
        value = value * 10 + (current - '0');
      } else {
        if ((contentLength - i + 1) < value) {
          logger.error(
              "Parsing string unfinished when reaching the end, "
                  + "starting pos = {}", 
              offset);
          throw new BEncodeFormatException(
              "Parsing string unfinished when reaching the end, "
                  + "starting pos = " + offset);
        }
        bString = new BString();
        String str = new String(content, i, value, Charset.forName("UTF-8"));
        bString.setContent(str);
        break;
      }
    } while (i < contentLength);
    
    if (null == bString) {
      logger.error(
          "Parsing string unfinished when reaching the end, "
              + "starting pos = {}", 
          offset);
      throw new BEncodeFormatException(
          "Parsing string unfinished when reaching the end, "
              + "starting pos = " + offset);
    }
    
    return bString;
  }

  /**
   * <p>解析列表数据。由于列表数据中可能包含更复杂的结构，
   * 因此会调用{@link Parser#parseNext(byte[], int)}完成解析任务。
   * @param content     带解析的字节数组
   * @param offset      偏移量，从某个位置开始解析
   * @return            解析结果
   * @since 0.1.0
   */
  public BList parseList(final byte[] content, int offset) {
    return null;
  }

  public BDictionary parseDic(final byte[] content, int offset) {
    return null;
  }

}
