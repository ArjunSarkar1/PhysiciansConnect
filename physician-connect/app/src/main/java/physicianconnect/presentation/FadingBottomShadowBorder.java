package physicianconnect.presentation;

import javax.swing.border.AbstractBorder;
import java.awt.*;

/**
 * FadingBottomShadowBorder
 *
 * Renders a soft, fading shadow effect primarily at the bottom of a component.
 * This border provides its own insets for the shadow space.
 */
public class FadingBottomShadowBorder extends AbstractBorder {
    private static final int SHADOW_HEIGHT = 6;
    private static final int MAX_ALPHA = 45;

    public FadingBottomShadowBorder() {
        // Constructor can be empty
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(0, 0, SHADOW_HEIGHT, 0); // Only bottom space for shadow
    }

    @Override
    public Insets getBorderInsets(Component c, Insets newInsets) {
        newInsets.top = 0;
        newInsets.left = 0;
        newInsets.bottom = SHADOW_HEIGHT;
        newInsets.right = 0;
        return newInsets;
    }

    @Override
    public boolean isBorderOpaque() {
        return false; // The shadow is semi-transparent
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int componentHeight) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int shadowStartY = y + componentHeight - SHADOW_HEIGHT;

        for (int i = 0; i < SHADOW_HEIGHT; i++) {
            int currentAlpha = MAX_ALPHA - (i * (MAX_ALPHA / SHADOW_HEIGHT));
            if (currentAlpha < 0)
                currentAlpha = 0;

            g2.setColor(new Color(0, 0, 0, currentAlpha));
            g2.drawLine(x + 1, shadowStartY + i, x + width - 2, shadowStartY + i);
        }
        g2.dispose();
    }
}
