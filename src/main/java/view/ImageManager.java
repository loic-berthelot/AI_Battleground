package view;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.io.FileInputStream;
import java.util.HashMap;

public class ImageManager {
    static private HashMap<String, Image> images = new HashMap<String, Image>();
    static private HashMap<String, Image> imagesFlippedHorizontally = new HashMap<String, Image>();
    static public Image getImage(String path, boolean flippedHorizontally) {
        try {
            if (flippedHorizontally) {
                if (imagesFlippedHorizontally.containsKey(path)) {
                    return imagesFlippedHorizontally.get(path);
                } else {
                    Image image = new Image(new FileInputStream("src/main/resources/img/"+path));
                    image = flipImageHorizontally(image);
                    imagesFlippedHorizontally.put(path, image);
                    return image;
                }
            } else {
                if (images.containsKey(path)) {
                    return images.get(path);
                } else {
                    Image image = new Image(new FileInputStream("src/main/resources/img/"+path));
                    images.put(path, image);
                    return image;
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    static private Image flipImageHorizontally(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        WritableImage flippedImage = new WritableImage(width, height);
        PixelReader reader = image.getPixelReader();
        PixelWriter writer = flippedImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = reader.getArgb(x, y);
                writer.setArgb(x, height - y - 1, argb);
            }
        }

        return flippedImage;
    }
}
