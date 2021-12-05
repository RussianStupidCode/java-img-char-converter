package ru.netology.graphics;

import ru.netology.graphics.image.BadImageSizeException;
import ru.netology.graphics.image.TextColorSchema;
import ru.netology.graphics.image.TextGraphicsConverter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.URL;

public class StandardTextGraphicConverter implements TextGraphicsConverter {
    protected TextColorSchema schema = new StandardTextColorSchema();

    // если значения < 0, то считается что ограничения не задавались
    protected int maxHeight = -1;
    protected int maxWidth = -1;
    protected double maxRatio = -1;

    protected class ImageSize{
        private int width = 0;
        private int height = 0;

        ImageSize(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }
    }

    protected ImageSize makeNewScaledImageSize(int oldWidth, int oldHeight) throws BadImageSizeException {

        double actualRatio = (double)oldWidth / oldHeight;
        if (this.maxRatio > 0 && actualRatio > this.maxRatio) {
            throw new BadImageSizeException(actualRatio, maxRatio);
        }

        int newWidth = oldWidth;
        int newHeight = oldHeight;

        if(this.maxWidth > 0 && this.maxWidth < oldWidth || this.maxHeight > 0 && this.maxHeight < oldHeight) {
            if(newWidth > newHeight) {
                newWidth = this.maxWidth;
                newHeight = (int)(newWidth / actualRatio);
            } else {
                newHeight = this.maxHeight;
                newWidth = (int) (newHeight * actualRatio);
            }
        }
        return new ImageSize(newWidth, newHeight);
    }

    protected WritableRaster makeRasterScaledImage(BufferedImage img, ImageSize newSize) {
       Image scaledImage = img.getScaledInstance(newSize.getWidth(), newSize.getHeight(), BufferedImage.SCALE_SMOOTH);
       BufferedImage bwImg = new BufferedImage(newSize.getWidth(), newSize.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

       Graphics2D graphics = bwImg.createGraphics();
       graphics.drawImage(scaledImage, 0, 0, null);

       return bwImg.getRaster();
    }

    protected StringBuilder [] convertRasterToStrings(WritableRaster raster) {
        StringBuilder [] symbolsImage = new StringBuilder[raster.getHeight()];
        for(int h = 0; h < raster.getHeight(); ++h) {
            symbolsImage[h] = new StringBuilder();
        }

        int[] pixel = new int[3];
        for (int w = 0; w < raster.getWidth(); ++w) {
            for (int h = 0; h < raster.getHeight(); ++h) {
                int color = raster.getPixel(w, h, pixel)[0];
                char c = this.schema.convert(color);
                symbolsImage[h].append(c);
                symbolsImage[h].append(c);
            }
        }

        return symbolsImage;
    }

    public StandardTextGraphicConverter() {

    }

    @Override
    public String convert(String url) throws IOException, BadImageSizeException {
        BufferedImage img = ImageIO.read(new URL(url));

        ImageSize newSize = this.makeNewScaledImageSize(img.getWidth(), img.getHeight());

        WritableRaster raster = this.makeRasterScaledImage(img, newSize);

        StringBuilder [] symbolsImage = this.convertRasterToStrings(raster);

        String result = "";
        for (StringBuilder row: symbolsImage) {
            result += row.toString() + "\n";
        }

        return result;
    }

    @Override
    public void setTextColorSchema(TextColorSchema schema) {
        this.schema = schema;
    }

    @Override
    public void setMaxWidth(int width) {
        this.maxWidth = width;
    }

    @Override
    public void setMaxHeight(int height) {
        this.maxHeight = height;
    }

    @Override
    public void setMaxRatio(double maxRatio) {
        this.maxRatio = maxRatio;
    }
}
