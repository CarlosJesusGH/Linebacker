package com.cmsys.linebacker.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by @CarlosJesusGH on 21/03/16.
 */
public class ColorUtils {
    public static final int RED_1 = 0xFFC0392B;
    public static final int GREEN_1 = 0xFF1ABC9C;
    public static final int GREEN_2 = 0xFF2ECC71;
    public static final int GREEN_3 = 0xFF16A085;
    public static final int GREEN_4 = 0xFF27AE60;
    public static final int BLUE_1 = 0xFF3498DB;
    public static final int BLUE_2 = 0xFF2980B9;
    public static final int DKBLUE_1 = 0xFF34495E;
    public static final int DKBLUE_2 = 0xFF2C3E50;
    public static final int YELLOW_1 = 0xFFF1C40F;
    public static final int DKYELLOW_1 = 0xFFF39C12;
    public static final int ORANGE_1 = 0xFFE67E22;
    public static final int VIOLET_1 = 0xFF9B59B6;
    public static final int DKVIOLET_1 = 0xFF8E44AD;
    public static final int LTGRAY_1 = 0xFF95A5A6;
    public static final int LTGRAY_2 = 0xFFBDC3C7;
    public static final int DKGRAY_1 = 0xFF7F8C8D;
    public static final int TERRACOTTA_1 = 0xFFD35400;

    public enum RandomColor {
        Red_1(RED_1),
        Green_1(GREEN_1),
        Green_2(GREEN_2),
        Green_3(GREEN_3),
        Green_4(GREEN_4),
        Blue_1(BLUE_1),
        Blue_2(BLUE_2),
        DkBlue_1(DKBLUE_1),
        DkBlue_2(DKBLUE_2),
        Yellow_1(YELLOW_1),
        DkYellow_1(DKYELLOW_1),
        Orange_1(ORANGE_1),
        Violet_1(VIOLET_1),
        DkViolet_1(DKVIOLET_1),
        LtGray_1(LTGRAY_1),
        LtGray_2(LTGRAY_2),
        DkGray_1(DKGRAY_1),
        Terracotta_1(TERRACOTTA_1);

        private final int value;
        private static final List<RandomColor> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
        private static final int SIZE = VALUES.size();
        private static final Random RANDOM = new Random();

        // Constructor
        RandomColor(int newValue) {
            value = newValue;
        }

        public int getValue() {
            return value;
        }

        public static int getRandomColor() {
            return VALUES.get(RANDOM.nextInt(SIZE)).getValue();
        }
    }

}
