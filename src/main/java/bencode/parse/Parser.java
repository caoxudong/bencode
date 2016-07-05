package bencode.parse;

import java.nio.charset.Charset;

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
   * @param parseLength 指定解析的长度，达到该长度后，则停止解析
   * @return 解析出的数据
   * @since 0.1.0
   */
  public BList parse(final byte[] content, int offset, int parseLength) {
    BList result = new BList();
    for (int i = offset; i < parseLength;) {
      BType<?> element = parseNext(content, i);
      i += element.getContentLength();
      result.add(element);
    }
    return result;
  }
  
  /**
   * <p>只解析出下一个B编码的数据类型。
   * 
   * @param content 带解析的内容
   * @param offset  偏移量，从该偏移量开始解析
   * @return        解析出的数据
   * @since 0.1.0
   */
  private BType<?> parseNext(final byte[] content, int offset) {
    byte current = content[offset];
    BType<?> result = null;
    switch (current) {
      case 'i': {
        // integer
        result = parseInt(content, offset);
        break;
      }

      case 'l': {
        // list
        result = parseList(content, offset);
        break;
      }

      case 'd': {
        // dictionary
        result = parseDic(content, offset);
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
        result = parseString(content, offset);
        break;
      }

      default: {
        logger.error(
            "Unexpected char in bencode when detemining bencode type, "
                + "char = {}, pos = {}", 
            current, offset);
        throw new BEncodeFormatException(
            "Unexpected char in bencode, when detemining bencode type, char = " 
                + (char)current + ", pos = " + offset);
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
    
    logger.debug(
        "Parsing value, pos = {}, type = {}, value = {}, length = {}",
        offset, BInteger.class, 
        bInteger.getContent(), bInteger.getContentLength());
    
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
    
    logger.debug(
        "Parsing value, pos = {}, type = {}, value = {}, length = {}",
        offset, BString.class, 
        bString.getContent(), bString.getContentLength());
    
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
    BList bList = new BList();
    int i = offset + 1;
    int contentLength = content.length;
    
    if (i >= contentLength) {
      logger.error(
          "Parsing list unfinished when reaching the end, "
              + "starting pos = {}", 
          offset);
      throw new BEncodeFormatException(
          "Parsing string unfinished when reaching the end, "
              + "starting pos = " + offset);
    }
    
    if (BList.SUFFIX == content[i]) {
      return bList;
    }
    
    while (true) {
      BType<?> bElement = parseNext(content, i);
      bList.add(bElement);
      i += bElement.getContentLength();
      if (i >= contentLength) {
        logger.error(
            "Parsing list unfinished when reaching the end, "
                + "starting pos = {}", 
            offset);
        throw new BEncodeFormatException(
            "Parsing string unfinished when reaching the end, "
                + "starting pos = " + offset);
      } else if (content[i] == BList.SUFFIX) {
        break;
      }
    }
    
    logger.debug(
        "Parsing value, pos = {}, type = {}, value = {}, length = {}",
        offset, BList.class, 
        bList.getContent(), bList.getContentLength());
    
    return bList;
  }

  public BDictionary parseDic(final byte[] content, int offset) {
    BDictionary bDictionary = new BDictionary();
    int i = offset + 1;
    int contentLength = content.length;
    
    if (i >= contentLength) {
      logger.error(
          "Parsing dictionary unfinished when reaching the end, "
              + "starting pos = {}", 
          offset);
      throw new BEncodeFormatException(
          "Parsing dictionary unfinished when reaching the end, "
              + "starting pos = " + offset);
    }
    
    if (BDictionary.SUFFIX == content[i]) {
      return bDictionary;
    }
    
    while (true) {
      // parse key
      BString key = parseString(content, i);
      i += key.getContentLength();
      
      // parse value
      BType<?> value = parseNext(content, i);
      i += value.getContentLength();
      
      bDictionary.put(key, value);
      
      if (i >= contentLength) {
        logger.error(
            "Parsing list unfinished when reaching the end, "
                + "starting pos = {}", 
            offset);
        throw new BEncodeFormatException(
            "Parsing string unfinished when reaching the end, "
                + "starting pos = " + offset);
      } else if (content[i] == BDictionary.SUFFIX) {
        break;
      }
    }
    
    logger.debug(
        "Parsing value, pos = {}, type = {}, value = {}, length = {}",
        offset, BDictionary.class, 
        bDictionary.getContent(), bDictionary.getContentLength());
    
    return bDictionary;
  }

}
