package com.cmsys.linebacker.util;

import android.text.TextUtils;
import android.widget.EditText;

import java.util.List;

/**
 * Created by cj on 18/12/15.
 */
public class CheckInputDataUtils {

    public final static boolean isValidEmail(CharSequence target) {
//        if (target == null) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static boolean areAllStringsFilled(List<String> stringList) {
        for (String iterator: stringList) {
            if (TextUtils.isEmpty(iterator)) {
                return false;
            }
        }
        return true;
    }

    public static boolean areAllFieldsFilled(List<EditText> editTextList) {
        for (EditText iterator: editTextList) {
            if (TextUtils.isEmpty(iterator.getText())) {
                return false;
            }
        }
        return true;
    }

    public static boolean fillAllFieldsSampleData(List<EditText> editTextList) {
        for (EditText iterator: editTextList) {
            if (TextUtils.isEmpty(iterator.getText())) {
                iterator.setText("Sample Data");
            }
        }
        return true;
    }
}
