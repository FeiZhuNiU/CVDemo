package com.iknow;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lliyu on 2016/2/15.
 */
public class CommonUtils {
    public static int stringDistance(String a, String b, List<Map.Entry<Character,Character>> wrongList)
    {
        if(a.length() != b.length() || wrongList == null)
        {
            return -1;
        }
        int ret = 0;
        for(int i = 0; i < a.length() ; ++i )
        {
            String t1 = a.substring(i,i+1);
            String t2 = b.substring(i,i+1);
            if(!t1.toLowerCase().equals(t2.toLowerCase()))
            {
                ++ret;
                wrongList.add(new AbstractMap.SimpleEntry<>(t1.charAt(0), t2.charAt(0)));
            }
        }
        return ret;
    }
}
