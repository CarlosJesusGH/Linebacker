package com.cmsys.linebacker.util;

import android.text.TextUtils;

/**
 * Created by @CarlosJesusGH on 08/03/16.
 */
public class StringUtils {
    public static String returnEmptyIfNull(String string) {
        if (!TextUtils.isEmpty(string))
            return string;
        return "";
    }
}
