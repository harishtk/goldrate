package com.harishtk.goldrate.app.util;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class EncodedStringValue implements Cloneable {
    private static final String TAG = "EncodedStringValue";
    private static final boolean DEBUG = false;
    private static final boolean LOCAL_LOGV = false;
    private int mCharacterSet;
    private byte[] mData;

    public EncodedStringValue(int charset, byte[] data) {
        if (null == data) {
            throw new NullPointerException("EncodedStringValue: Text-string is null.");
        } else {
            this.mCharacterSet = charset;
            this.mData = new byte[data.length];
            System.arraycopy(data, 0, this.mData, 0, data.length);
        }
    }

    public EncodedStringValue(byte[] data) {
        this(106, data);
    }

    public EncodedStringValue(String data) {
        try {
            this.mData = data.getBytes("utf-8");
            this.mCharacterSet = 106;
        } catch (UnsupportedEncodingException var3) {
            Log.e("EncodedStringValue", "Default encoding must be supported.", var3);
        }

    }

    public int getCharacterSet() {
        return this.mCharacterSet;
    }

    public void setCharacterSet(int charset) {
        this.mCharacterSet = charset;
    }

    public byte[] getTextString() {
        byte[] byteArray = new byte[this.mData.length];
        System.arraycopy(this.mData, 0, byteArray, 0, this.mData.length);
        return byteArray;
    }

    public void setTextString(byte[] textString) {
        if (null == textString) {
            throw new NullPointerException("EncodedStringValue: Text-string is null.");
        } else {
            this.mData = new byte[textString.length];
            System.arraycopy(textString, 0, this.mData, 0, textString.length);
        }
    }

    public String getString() {
        if (0 == this.mCharacterSet) {
            return new String(this.mData);
        } else {
            try {
                String name = CharacterSets.getMimeName(this.mCharacterSet);
                return new String(this.mData, name);
            } catch (UnsupportedEncodingException var4) {
                try {
                    return new String(this.mData, "iso-8859-1");
                } catch (UnsupportedEncodingException var3) {
                    return new String(this.mData);
                }
            }
        }
    }

    public void appendTextString(byte[] textString) {
        if (null == textString) {
            throw new NullPointerException("Text-string is null.");
        } else {
            if (null == this.mData) {
                this.mData = new byte[textString.length];
                System.arraycopy(textString, 0, this.mData, 0, textString.length);
            } else {
                ByteArrayOutputStream newTextString = new ByteArrayOutputStream();

                try {
                    newTextString.write(this.mData);
                    newTextString.write(textString);
                } catch (IOException var4) {
                    Log.e("EncodedStringValue", "logging error", var4);
                    var4.printStackTrace();
                    throw new NullPointerException("appendTextString: failed when write a new Text-string");
                }

                this.mData = newTextString.toByteArray();
            }

        }
    }

    public Object clone() throws CloneNotSupportedException {
        super.clone();
        int len = this.mData.length;
        byte[] dstBytes = new byte[len];
        System.arraycopy(this.mData, 0, dstBytes, 0, len);

        try {
            return new EncodedStringValue(this.mCharacterSet, dstBytes);
        } catch (Exception var4) {
            Log.e("EncodedStringValue", "logging error", var4);
            var4.printStackTrace();
            throw new CloneNotSupportedException(var4.getMessage());
        }
    }

    public EncodedStringValue[] split(String pattern) {
        String[] temp = this.getString().split(pattern);
        EncodedStringValue[] ret = new EncodedStringValue[temp.length];

        for(int i = 0; i < ret.length; ++i) {
            try {
                ret[i] = new EncodedStringValue(this.mCharacterSet, temp[i].getBytes());
            } catch (NullPointerException var6) {
                return null;
            }
        }

        return ret;
    }

    public static EncodedStringValue[] extract(String src) {
        String[] values = src.split(";");
        ArrayList<EncodedStringValue> list = new ArrayList();

        int len;
        for(len = 0; len < values.length; ++len) {
            if (values[len].length() > 0) {
                list.add(new EncodedStringValue(values[len]));
            }
        }

        len = list.size();
        if (len > 0) {
            return (EncodedStringValue[])list.toArray(new EncodedStringValue[len]);
        } else {
            return null;
        }
    }

    public static String concat(EncodedStringValue[] addr) {
        StringBuilder sb = new StringBuilder();
        int maxIndex = addr.length - 1;

        for(int i = 0; i <= maxIndex; ++i) {
            sb.append(addr[i].getString());
            if (i < maxIndex) {
                sb.append(";");
            }
        }

        return sb.toString();
    }

    public static EncodedStringValue copy(EncodedStringValue value) {
        return value == null ? null : new EncodedStringValue(value.mCharacterSet, value.mData);
    }

    public static EncodedStringValue[] encodeStrings(String[] array) {
        int count = array.length;
        if (count <= 0) {
            return null;
        } else {
            EncodedStringValue[] encodedArray = new EncodedStringValue[count];

            for(int i = 0; i < count; ++i) {
                encodedArray[i] = new EncodedStringValue(array[i]);
            }

            return encodedArray;
        }
    }
}