import javax.swing.JPanel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
public class Collider extends JPanel {

    private ActionListener actionListener;
    private Plant assignedPlant;

    public Collider() {
        setOpaque(false);
        setSize(100, 120);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (actionListener != null) {
                    actionListener.actionPerformed(
                        new ActionEvent(this,
                                ActionEvent.ACTION_PERFORMED,
                                "")
                    );
                }
            }
        });
    }

    public boolean setPlant(Plant plant) {
        if (assignedPlant == null) {
            assignedPlant = plant;
            return true;
        }
        plant.stop();
        return false;
    }

    public void removePlant() {
        if (assignedPlant != null) {
            assignedPlant.stop();
            assignedPlant = null;
            repaint();
        }
    }

    public Plant getAssignedPlant() {
        return assignedPlant;
    }

    public void setAction(ActionListener al) {
        this.actionListener = al;
    }

    public boolean isInsideCollider(int tx) {
        return tx > getX() && tx < getX() + getWidth();
    }
    
}