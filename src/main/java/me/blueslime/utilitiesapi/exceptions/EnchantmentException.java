package me.blueslime.utilitiesapi.exceptions;

public class EnchantmentException extends Exception {
    private EnchantmentException(String message) {
        super(message);
    }

    public static EnchantmentException build(String message) {
        return new EnchantmentException(message);
    }
}
