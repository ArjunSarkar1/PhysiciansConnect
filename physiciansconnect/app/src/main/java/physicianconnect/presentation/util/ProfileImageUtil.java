package physicianconnect.presentation.util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class ProfileImageUtil {
    private static final String PHOTO_DIR = "src/main/resources/profile_photos";

    public static ImageIcon getProfileIcon(String id, boolean isPhysician) {
        String prefix = isPhysician ? "p_" : "r_";
        File photoFile = new File(PHOTO_DIR, prefix + id + ".png");

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
            g2.drawString("👤", 10, 25); // Or just draw initials/text
            g2.dispose();
            return new ImageIcon(placeholder);
        }
    }

}