package org.pentaho.metadata.query.model.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.pentaho.metadata.model.concept.types.DataType;

public class CsvDataTypeEvaluator {
  List<String> columnValues;
  public CsvDataTypeEvaluator() {
    
  }
  public CsvDataTypeEvaluator( List<String> columnValues) {
    this.columnValues = columnValues;
  }  
  public DataType evaluateDataType(List<String> columnValues) {
    CountItemsList<DataType> columnTypes = new CountItemsList<DataType>();
    for(String value:columnValues) {
      DataType type = DataTypeDetector.getDataType(value);
      if(type == DataType.STRING) {
        return DataType.STRING;
      }
      columnTypes.add(type);
    }
    return columnTypes.getItemOfMaxCount();
  }
  
  private class CountItemsList<E> extends ArrayList<E> { 

    /**
     * 
     */
    private static final long serialVersionUID = 1923899528642995386L;
    // This is private. It is not visible from outside.
    private Map<E,Integer> count = new HashMap<E,Integer>();

    // There are several entry points to this class
    // this is just to show one of them.
    public boolean add( E element  ) { 
        if( !count.containsKey( element ) ){
            count.put( element, 1 );
        } else { 
            count.put( element, count.get( element ) + 1 );
        }
        return super.add( element );
    }

    // This method belongs to CountItemList interface ( or class ) 
    // to used you have to cast.
    public int getCount( E element ) { 
        if( ! count.containsKey( element ) ) {
            return 0;
        }
        return count.get( element );
    }
    public E getItemOfMaxCount() {
      E returnElement = null;
      Integer currentMax = -1;
      Set<Entry<E,Integer>> countEntrySet  = count.entrySet();
      for(Entry<E,Integer> entry:count.entrySet()) {
        if( entry.getValue() > currentMax) {
          currentMax = entry.getValue();
          returnElement = entry.getKey();
        }
      }
      return returnElement;
    }
  }
  public List<String> getColumnValues() {
    return columnValues;
  }
  public void setColumnValues(List<String> columnValues) {
    this.columnValues = columnValues;
  }

}
