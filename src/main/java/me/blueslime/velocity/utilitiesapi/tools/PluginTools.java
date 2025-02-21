package me.blueslime.velocity.utilitiesapi.tools;

public class PluginTools {

    public static int toInt(String value) {
        return toInt(value, 1);
    }

    public static int toInt(String value, int defValue) {
        return isNumber(value) ? Integer.parseInt(value) : defValue;
    }

    public static double toDouble(String value) {
        return toDouble(value, 1);
    }

    public static double toDouble(String value, double defValue) {
        return isDouble(value) ? Double.parseDouble(value) : defValue;
    }

    public static float toFloat(String value) {
        return toFloat(value, 1);
    }

    public static float toFloat(String value, float defValue) {
        return isFloat(value) ? Float.parseFloat(value) : defValue;
    }

    public static boolean isNumber(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    public static boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    public static boolean isFloat(String value) {
        try {
            Float.parseFloat(value);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }




}
