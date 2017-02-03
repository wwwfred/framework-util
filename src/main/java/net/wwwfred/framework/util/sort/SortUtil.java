package net.wwwfred.framework.util.sort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SortUtil {
  
    public static <T extends Comparable<T>> List<T> getSortedList(List<T> list)
    {
        Collections.sort(list);
        return new ArrayList<T>(list);
    }
    
    public static <T> List<T> getSortedList(List<T> list, Comparator<T> comparator)
    {
        Collections.sort(list, comparator);
        return new ArrayList<T>(list);
    }
    
    public static <K extends Comparable<K>,V> Map<K,V> getSortedKeyMap(Map<K, V> map)
    {
        Map<K,V> result = new LinkedHashMap<K, V>();
        List<K> list = new ArrayList<K>(map.keySet());
        Collections.sort(list);
        for (K k : list) {
            V v = map.get(k);
            result.put(k, v);
        }
        return result;
    }
    
    public static <K,V> Map<K,V> getSortedKeyMap(Map<K, V> map, Comparator<K> comparator)
    {
        Map<K,V> result = new LinkedHashMap<K, V>();
        List<K> list = new ArrayList<K>(map.keySet());
        Collections.sort(list,comparator);
        for (K k : list) {
            V v = map.get(k);
            result.put(k, v);
        }
        return result;
    }
    
    public static <K,V extends Comparable<V>> Map<K,V> getSortedValueMap(final Map<K, V> map)
    {
        Map<K,V> result = new LinkedHashMap<K, V>();
        List<K> list = new ArrayList<K>(map.keySet());
        Collections.sort(list, new Comparator<K>() {
            @Override
            public int compare(K k1, K k2) {
                V v1 = map.get(k1);
                V v2 = map.get(k2);
                if(v1==null&&v2!=null)
                {
                    return -1;
                }
                else if(v1==null&&v2==null)
                {
                    return 0;
                }
                else if(v1!=null&&v2==null)
                {
                    return 1;
                }
                else
                {
                    return v1.compareTo(v2);
                }
            }
        });
        for (K k : list) {
            V v = map.get(k);
            result.put(k, v);
        }
        return result;
    }
    
    public static <K,V> Map<K,V> getSortedValueMap(final Map<K, V> map,final Comparator<V> comparator)
    {
        Map<K,V> result = new LinkedHashMap<K, V>();
        List<K> list = new ArrayList<K>(map.keySet());
        Collections.sort(list, new Comparator<K>() {
            @Override
            public int compare(K k1, K k2) {
                V v1 = map.get(k1);
                V v2 = map.get(k2);
                if(v1==null&&v2!=null)
                {
                    return -1;
                }
                else if(v1==null&&v2==null)
                {
                    return 0;
                }
                else if(v1!=null&&v2==null)
                {
                    return 1;
                }
                else
                {
                    return comparator.compare(v1, v2);
                }
            }
        });
        for (K k : list) {
            V v = map.get(k);
            result.put(k, v);
        }
        return result;
    }
    
}
