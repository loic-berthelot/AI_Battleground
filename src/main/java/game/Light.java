package game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Random;

public class Light {
    final private Position position;
    private int lifetime;
    private Color color;
    private double radius;
    public Light(Position position, Color color, double radius) {
        this.position = position;
        Random random = new Random();
        this.color = color.deriveColor(0,1,1, 0.3+random.nextDouble(0.5));
        lifetime = 45+random.nextInt(20);
        this.radius = (0.5+random.nextDouble(0.3))*radius;
    }
    public boolean evolve() {
        lifetime--;
        radius *= 0.982;
        color = color.deriveColor(0,1,1,0.96);
        return lifetime <= 0;
    }
    public void draw(GraphicsContext graphicsContext, Game game){
        graphicsContext.setFill(color);
        double size = game.getScreenSize(2*radius);
        graphicsContext.fillOval(game.getScreenPosX(position.getX()-radius), game.getScreenPosY(position.getY()+radius), size, size);
    }
}
