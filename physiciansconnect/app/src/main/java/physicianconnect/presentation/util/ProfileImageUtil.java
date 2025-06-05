package physicianconnect.presentation.util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class ProfileImageUtil {
    public static ImageIcon getProfileIcon(String physicianId) {
        File photoFile = new File("src/main/java/physicianconnect/src/profile_photos", physicianId + ".png");
        if (photoFile.exists()) {
            ImageIcon icon = new ImageIcon(photoFile.getAbsolutePath());
            Image img = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } else {
            BufferedImage placeholder = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = placeholder.createGraphics();
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRect(0, 0, 40, 40);
            g2.setColor(Color.DARK_GRAY);
            g2.drawString("👤", 10, 25);
            g2.dispose();
            return new ImageIcon(placeholder);
        }
    }
}