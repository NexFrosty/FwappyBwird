import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

public class Bird {

    Image icon = new ImageIcon(getClass().getResource("bird.gif")).getImage(); 
    Image icon2 = new ImageIcon(getClass().getResource("birdd.gif")).getImage(); 

    int height;
    int width;
    int y;
    int x;

    Bird(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    void draw(Graphics g, int score, boolean medium) {
        g.drawImage(icon, x, y, null);
        if (score > 3 || medium) {
            g.drawImage(icon2, x, y, null);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}