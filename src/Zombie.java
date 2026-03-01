import javax.swing.*;

public class Zombie {

    private int health = 1000;
    private int speed = 1;
    private GamePanel gp;
    private int posX = 1000;
    private int myLane;
    private boolean isMoving = true;
    private int slowInt = 0;

    public Zombie(GamePanel parent, int lane) {
        this.gp = parent;
        this.myLane = lane;
    }

    public void advance() {

        if (health <= 0) {
            gp.addProgress(this.getScore());
            return;
        }

        if (isMoving) {

            boolean isCollides = false;
            Collider collided = null;

            for (int i = myLane * 9; i < (myLane + 1) * 9; i++) {
            if (gp.getColliders()[i].getAssignedPlant() != null
                    && gp.getColliders()[i].isInsideCollider(posX)) {
                    isCollides = true;
                    collided = gp.getColliders()[i];
                }
            }

                        if (!isCollides) {

                if (slowInt > 0) {
                    if (slowInt % 2 == 0) {
                        posX--;
                    }
                    slowInt--;
                } else {
                    posX--;
                }

            } else {

                Plant plant = collided.getAssignedPlant();

                plant.setHealth(plant.getHealth() - 10);

                if (plant.getHealth() < 0) {
                    collided.removePlant();
                }
            }

            if (posX < 0) {
                isMoving = false;
                gp.stopGame();
                gp.gameOver();
                return;
            }
        }
    }

    public int getScore() {
        return 5;
    }

    public void slow() {
        slowInt = 1000;
    }

    public static Zombie getZombie(String type, GamePanel parent, int lane) {

        switch (type) {
            case "NormalZombie":
                return new NormalZombie(parent, lane);
            case "ConeHeadZombie":
                return new ConeHeadZombie(parent, lane);
            default:
                return new Zombie(parent, lane);
        }
    }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }

    public int getSpeed() { return speed; }
    public void setSpeed(int speed) { this.speed = speed; }

    public int getPosX() { return posX; }
    public void setPosX(int posX) { this.posX = posX; }

    public int getMyLane() { return myLane; }
    public void setMyLane(int myLane) { this.myLane = myLane; }

    public boolean isMoving() { return isMoving; }
    public void setMoving(boolean moving) { isMoving = moving; }

    public int getSlowInt() { return slowInt; }
    public void setSlowInt(int slowInt) { this.slowInt = slowInt; }
}