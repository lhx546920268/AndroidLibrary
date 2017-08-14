package com.lhx.library.util;

import android.text.InputFilter;
import android.text.InputType;
import android.text.method.NumberKeyListener;
import android.widget.EditText;

/**
 * 编辑工具类
 */

public class EditUtil {


    //英文
    public static final char[] KEYS_ALPHA = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n',
            'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E',
            'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

    //英文数字
    public static final char[] KEYS_ALPHA_NUMBER = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
            'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };


    //只允许输入英文
    public static void setInputAlpha(EditText editText) {
        editText.setKeyListener(new NumberKeyListener() {

            @Override
            public int getInputType() {
                // TODO Auto-generated method stub
                return InputType.TYPE_CLASS_TEXT;
            }

            @Override
            protected char[] getAcceptedChars() {
                // TODO Auto-generated method stub
                return KEYS_ALPHA;
            }
        });
    }

    //只允许输入数字和英文
    public static void setInputAlphaNumber(EditText editText) {
        editText.setKeyListener(new NumberKeyListener() {

            @Override
            public int getInputType() {
                // TODO Auto-generated method stub
                return InputType.TYPE_CLASS_TEXT;
            }

            @Override
            protected char[] getAcceptedChars() {
                // TODO Auto-generated method stub
                return KEYS_ALPHA_NUMBER;
            }
        });
    }

    //设置最大输入
    public static void setMaxLength(EditText editText, int maxLength) {
        InputFilter[] nFilters = new InputFilter[] { new InputFilter.LengthFilter(maxLength) };
        editText.setFilters(nFilters);
    }
}
