package com.cmsys.linebacker.util;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by cj on 15/12/15.
 */
public class HashMapUtils {

    public static List<String> getIdsStrings(HashMap<String, String> hashMap){
        List<String> ids = new ArrayList<String>();
        Iterator<String> myVeryOwnIterator = hashMap.keySet().iterator();
        while(myVeryOwnIterator.hasNext()) {
            String id=(String)myVeryOwnIterator.next();
            @SuppressWarnings("unused")
            String value=(String)hashMap.get(id);
            ids.add(id);
        }
        return ids;
    }

    public static List<String> getValuesStrings(HashMap<String, String> hashMap){
        List<String> values = new ArrayList<String>();
        Iterator<String> myVeryOwnIterator = hashMap.keySet().iterator();
        while(myVeryOwnIterator.hasNext()) {
            String id=(String)myVeryOwnIterator.next();
            String value=(String)hashMap.get(id);
            values.add(value);
        }
        return values;
    }

    public static List<HashMap<String, String>> getListHashMapFromCursor(Cursor cursor) {
        List<HashMap<String, String>> list = new ArrayList<>();
        int aux = 0;
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
        //while (cursor.moveToNext()) {
            HashMap<String, String> hashMap = new HashMap<>();
            for (int j = 0; j < cursor.getColumnCount(); j++) {
                try {
                    hashMap.put(cursor.getColumnName(j), cursor.getString(j));
                } catch (Exception e) {
                    hashMap.put(cursor.getColumnName(j), e.getMessage());
                }
            }
            list.add(hashMap);
            cursor.moveToNext();
            aux ++;
        }
        return list;
    }

}
