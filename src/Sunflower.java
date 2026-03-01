import javax.swing.*;
import java.awt.event.ActionEvent;

public class Sunflower extends Plant {

    private Timer sunProduceTimer;

    public Sunflower(GamePanel parent, int x, int y) {
        super(parent, x, y);

        sunProduceTimer = new Timer(12000, (ActionEvent e) -> produceSun());
        sunProduceTimer.start();
    }

    private void produceSun() {

        if (getGp().isPaused() || getGp().isLevelFinished() || getGp().isGameOver()) {
            return;
        }

        int pixelX = 60 + getX() * 100;
        int pixelY = 110 + getY() * 120;

        Sun sun = new Sun(getGp(), pixelX, pixelY, pixelY + 20);

        getGp().getActiveSuns().add(sun);
        getGp().add(sun, 1);
    }

    @Override
    public void stop() {
        if (sunProduceTimer != null) {
            sunProduceTimer.stop();
        }
    }
    
    public void resume() {
        if (sunProduceTimer != null && !sunProduceTimer.isRunning()) {
            sunProduceTimer.start();
        }
    }
}