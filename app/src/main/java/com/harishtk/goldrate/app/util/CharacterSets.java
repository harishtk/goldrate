package com.harishtk.goldrate.app.util;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class CharacterSets {
    public static final int ANY_CHARSET = 0;
    public static final int US_ASCII = 3;
    public static final int ISO_8859_1 = 4;
    public static final int ISO_8859_2 = 5;
    public static final int ISO_8859_3 = 6;
    public static final int ISO_8859_4 = 7;
    public static final int ISO_8859_5 = 8;
    public static final int ISO_8859_6 = 9;
    public static final int ISO_8859_7 = 10;
    public static final int ISO_8859_8 = 11;
    public static final int ISO_8859_9 = 12;
    public static final int SHIFT_JIS = 17;
    public static final int UTF_8 = 106;
    public static final int BIG5 = 2026;
    public static final int UCS2 = 1000;
    public static final int UTF_16 = 1015;
    public static final int DEFAULT_CHARSET = 106;
    private static final int[] MIBENUM_NUMBERS = new int[]{0, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 17, 106, 2026, 1000, 1015};
    public static final String MIMENAME_ANY_CHARSET = "*";
    public static final String MIMENAME_US_ASCII = "us-ascii";
    public static final String MIMENAME_ISO_8859_1 = "iso-8859-1";
    public static final String MIMENAME_ISO_8859_2 = "iso-8859-2";
    public static final String MIMENAME_ISO_8859_3 = "iso-8859-3";
    public static final String MIMENAME_ISO_8859_4 = "iso-8859-4";
    public static final String MIMENAME_ISO_8859_5 = "iso-8859-5";
    public static final String MIMENAME_ISO_8859_6 = "iso-8859-6";
    public static final String MIMENAME_ISO_8859_7 = "iso-8859-7";
    public static final String MIMENAME_ISO_8859_8 = "iso-8859-8";
    public static final String MIMENAME_ISO_8859_9 = "iso-8859-9";
    public static final String MIMENAME_SHIFT_JIS = "shift_JIS";
    public static final String MIMENAME_UTF_8 = "utf-8";
    public static final String MIMENAME_BIG5 = "big5";
    public static final String MIMENAME_UCS2 = "iso-10646-ucs-2";
    public static final String MIMENAME_UTF_16 = "utf-16";
    public static final String DEFAULT_CHARSET_NAME = "utf-8";
    private static final String[] MIME_NAMES = new String[]{"*", "us-ascii", "iso-8859-1", "iso-8859-2", "iso-8859-3", "iso-8859-4", "iso-8859-5", "iso-8859-6", "iso-8859-7", "iso-8859-8", "iso-8859-9", "shift_JIS", "utf-8", "big5", "iso-10646-ucs-2", "utf-16"};
    private static final HashMap<Integer, String> MIBENUM_TO_NAME_MAP = new HashMap();
    private static final HashMap<String, Integer> NAME_TO_MIBENUM_MAP = new HashMap();

    private CharacterSets() {
    }

    public static String getMimeName(int mibEnumValue) throws UnsupportedEncodingException {
        String name = (String)MIBENUM_TO_NAME_MAP.get(mibEnumValue);
        if (name == null) {
            throw new UnsupportedEncodingException();
        } else {
            return name;
        }
    }

    public static int getMibEnumValue(String mimeName) throws UnsupportedEncodingException {
        if (null == mimeName) {
            return -1;
        } else {
            Integer mibEnumValue = (Integer)NAME_TO_MIBENUM_MAP.get(mimeName);
            if (mibEnumValue == null) {
                throw new UnsupportedEncodingException();
            } else {
                return mibEnumValue;
            }
        }
    }

    static {
        assert MIBENUM_NUMBERS.length == MIME_NAMES.length;

        int count = MIBENUM_NUMBERS.length - 1;

        for(int i = 0; i <= count; ++i) {
            MIBENUM_TO_NAME_MAP.put(MIBENUM_NUMBERS[i], MIME_NAMES[i]);
            NAME_TO_MIBENUM_MAP.put(MIME_NAMES[i], MIBENUM_NUMBERS[i]);
        }

    }
}