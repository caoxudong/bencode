package bencode.parse;

import bencode.type.BType;

public class ParseResultTumple<T extends BType<?>> {

  public T content;
  public int length;
  
}
