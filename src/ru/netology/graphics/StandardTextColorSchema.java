package ru.netology.graphics;

import ru.netology.graphics.image.TextColorSchema;

public class StandardTextColorSchema implements TextColorSchema {
    protected String graphicSymbols = "#$@%*+-'";
    public final static int maxValueColor = 255;

    public StandardTextColorSchema() {

    }

    @Override
    public char convert(int color) throws IllegalArgumentException {
        if (color < 0 || color > StandardTextColorSchema.maxValueColor) {
            throw new IllegalArgumentException("Error value color: " + color + " max value :" + StandardTextColorSchema.maxValueColor);
        }

        int stepSymbol = (StandardTextColorSchema.maxValueColor + 1) / this.graphicSymbols.length();
        int symbolPosition = color / stepSymbol;
        return this.graphicSymbols.charAt(symbolPosition);
    }
}
