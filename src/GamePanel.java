import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Random;
import java.net.URL;
import java.awt.Shape;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import javax.swing.border.Border;
import java.awt.Color;
import javax.swing.BorderFactory;
/**
 * Created by Armin on 6/25/2016.
 */
public class GamePanel extends JPanel implements MouseMotionListener {

    private boolean finalWaveShown = false;
    private boolean showFinalWaveText = false;
    private int zombieSpawnedCount = 0;
    private final int MAX_ZOMBIES = 10;
    private boolean waitingToFinish = false;
    private long finishStartTime;
    private boolean levelFinished = false;
    private Shape finalWaveShape;
    private Shape gameOverShape;
    
    public boolean isPaused() {
    return isPaused;
    }

    public boolean isLevelFinished() {
        return levelFinished;
    }

    public boolean isGameOver() {
        return gameOver;
    }
    
    private long finalWaveStartTime;
    private JButton shovelBtn;
    private Image bgImage;
    private Image peashooterImage;
    private Image freezePeashooterImage;
    private Image sunflowerImage;
    private Image peaImage;
    private Image freezePeaImage;
    private Image shovelImage;
    private Image shovelIcon;


    private Image normalZombieImage;
    private Image coneHeadZombieImage;
    private Collider[] colliders;

    private ArrayList<ArrayList<Zombie>> laneZombies;
    private ArrayList<ArrayList<Pea>> lanePeas;
    private ArrayList<Sun> activeSuns;

    private Timer redrawTimer;
    private Timer advancerTimer;
    private Timer sunProducer;
    private Timer zombieProducer;
    
    
    private boolean isPaused = false;
    private Font pauseFont = new Font("Arial", Font.BOLD, 60);
    private boolean gameOver = false;
    private boolean showGameOverText = false;
    
    private int sunScore;
    private JLabel sunScoreboard;
    
    private Timer gameTimer;
    private JProgressBar timeBar;
    private final int LEVEL_TIME = 60000;
    private JProgressBar progressBar;
    private JLabel levelLabel;
    
    
    private int btnWidth = 100;
    private int btnHeight = 30;
    private int margin = 10;

    private GameWindow.PlantType activePlantingBrush = GameWindow.PlantType.None;

    private int mouseX, mouseY;

    

    public int getSunScore() {
        return sunScore;
    }

    public void setSunScore(int sunScore) {
        this.sunScore = sunScore;
        sunScoreboard.setText(String.valueOf(sunScore));
    }

    public GamePanel(JLabel sunScoreboard) {
        setSize(1000, 752);
        setLayout(null);
        setDoubleBuffered(true);
        
        
        //them
        progressBar = new JProgressBar(0, getLevelTarget()); 
        progressBar.setValue(0);
        int barWidth = 250;
        int barHeight = 25;

        progressBar.setBounds(
                1000 - barWidth - 20,   // cách mép phải 20px
                752 - barHeight - 40,   // cách mép dưới 40px
                barWidth,
                barHeight
        );
        progressBar.setStringPainted(true);
        add(progressBar);
        progressBar.setForeground(new Color(0, 200, 0)); // màu thanh
        progressBar.setBackground(new Color(40, 40, 40)); // nền trong khung

        progressBar.setFont(new Font("Arial", Font.BOLD, 14));

        progressBar.setBorderPainted(true);
        progressBar.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        progressBar.setOpaque(true);

        progressBar.setUI(new javax.swing.plaf.basic.BasicProgressBarUI() {
            @Override
            protected Color getSelectionForeground() {
                return Color.WHITE;
            }

            @Override
            protected Color getSelectionBackground() {
                return Color.WHITE;
            }
        });
        progress = 0;
        progressBar.setValue(0);
        
        //nhan input tu chuot
        addMouseMotionListener(this);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleGridClick(e.getX(), e.getY());
            }
        });
        //Nut pause cua game
        JButton btnPause = new JButton("Pause");
        
        btnPause.setForeground(new Color(102, 255, 102)); //xanh la
        btnPause.setBounds(1000 - btnWidth - margin - 15, 1, btnWidth, btnHeight);
        
        //an di nut pause
        btnPause.setBorderPainted(false);
        btnPause.setContentAreaFilled(false);
        btnPause.setFocusPainted(false);
        btnPause.setOpaque(false);
        
        add(btnPause, new Integer(2));

        btnPause.addActionListener(e -> {
            setPaused(!isPaused);
            btnPause.setText(isPaused ? "Resume" : "Pause");
        });
        
        
        
        //them xeng
        ImageIcon icon = new ImageIcon(
        getClass().getResource("/images/shovelicon.png"));
        Image img = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        JButton shovelBtn = new JButton(new ImageIcon(img));

        shovelBtn.setText("");
        shovelBtn.setFocusPainted(false);
        shovelBtn.setBorderPainted(false);
        shovelBtn.setContentAreaFilled(false);
        shovelBtn.setOpaque(false);
        shovelBtn.setBounds(580, 13, 80, 80);
        

        add(shovelBtn, new Integer(2));

        shovelBtn.addActionListener(e -> {

           activePlantingBrush = GameWindow.PlantType.Shovel;

           Toolkit toolkit = Toolkit.getDefaultToolkit();

           // Ẩn cursor hệ điều hành
           Image image = toolkit.createImage(new byte[0]);
           Cursor blankCursor = toolkit.createCustomCursor(
                   image,
                   new Point(0, 0),
                   "blank"
           );

           setCursor(blankCursor);
       });

        //hien thi man hien tai
        levelLabel = new JLabel();
        levelLabel.setBounds(
            1000 - barWidth - 20,
            752 - barHeight - 70,
            barWidth,
            25
        );
        levelLabel.setForeground(Color.WHITE);
        levelLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(levelLabel);

        updateLevelLabel();
        
        this.sunScoreboard = sunScoreboard;
        setSunScore(150);  //pool avalie

        bgImage = new ImageIcon(this.getClass().getResource("/images/mainBG.png")).getImage();

        peashooterImage = new ImageIcon(this.getClass().getResource("/images/plants/peashooter.gif")).getImage();
        freezePeashooterImage = new ImageIcon(this.getClass().getResource("/images/plants/freezepeashooter.gif")).getImage();
        sunflowerImage = new ImageIcon(this.getClass().getResource("/images/plants/sunflower.gif")).getImage();
        peaImage = new ImageIcon(this.getClass().getResource("/images/pea.png")).getImage();
        freezePeaImage = new ImageIcon(this.getClass().getResource("/images/freezepea.png")).getImage();
        shovelImage = new ImageIcon(this.getClass().getResource("/images/shovel.png")).getImage();
        normalZombieImage = new ImageIcon(this.getClass().getResource("/images/zombies/zombie1.png")).getImage();
        coneHeadZombieImage = new ImageIcon(this.getClass().getResource("/images/zombies/zombie2.png")).getImage();

        laneZombies = new ArrayList<>();
        laneZombies.add(new ArrayList<>()); //line 1
        laneZombies.add(new ArrayList<>()); //line 2
        laneZombies.add(new ArrayList<>()); //line 3
        laneZombies.add(new ArrayList<>()); //line 4
        laneZombies.add(new ArrayList<>()); //line 5

        lanePeas = new ArrayList<>();
        lanePeas.add(new ArrayList<>()); //line 1
        lanePeas.add(new ArrayList<>()); //line 2
        lanePeas.add(new ArrayList<>()); //line 3
        lanePeas.add(new ArrayList<>()); //line 4
        lanePeas.add(new ArrayList<>()); //line 5
    
        activeSuns = new ArrayList<>();
        colliders = new Collider[45];

            colliders = new Collider[45];

            for (int i = 0; i < 45; i++) {
                colliders[i] = new Collider();
            }
        
        redrawTimer = new Timer(16, (ActionEvent e) -> {
            repaint();
        });
        redrawTimer.start();

        //colliders[0].setPlant(new FreezePeashooter(this,0,0));
/*
        colliders[9].setPlant(new Peashooter(this,0,1));
        laneZombies.get(1).add(new NormalZombie(this,1));*/

        redrawTimer = new Timer(16, (ActionEvent e) -> {
            repaint();
        });
        redrawTimer.start();

        advancerTimer = new Timer(60, (ActionEvent e) -> advance());
        advancerTimer.start();

        sunProducer = new Timer(5000, (ActionEvent e) -> {
            if (levelFinished || gameOver) return;
            Random rnd = new Random();
            Sun sta = new Sun(this, rnd.nextInt(800) + 100, 0, rnd.nextInt(300) + 200);
            activeSuns.add(sta);
            add(sta, new Integer(1));
        });
        sunProducer.start();

        zombieProducer = new Timer(7000, (ActionEvent e) -> {
            if (zombieSpawnedCount >= MAX_ZOMBIES) {
                return; // Zombie dừng spawn
            }

            Random rnd = new Random();

            updateLevelLabel();
            progressBar.setMaximum(getLevelTarget());

            String[] Level = LevelData.LEVEL_CONTENT[Integer.parseInt(LevelData.LEVEL_NUMBER) - 1];
            int[][] LevelValue = LevelData.LEVEL_VALUE[Integer.parseInt(LevelData.LEVEL_NUMBER) - 1];

            int l = rnd.nextInt(5);
            int t = rnd.nextInt(100);
            Zombie z = null;

            for (int i = 0; i < LevelValue.length; i++) {
                if (t >= LevelValue[i][0] && t <= LevelValue[i][1]) {
                    z = Zombie.getZombie(Level[i], GamePanel.this, l);
                    break;
                }
            }

            if (z != null) {
                laneZombies.get(l).add(z);
                zombieSpawnedCount++;
                addProgress(getLevelTarget() / MAX_ZOMBIES);
            }
        });
        zombieProducer.start();
    }
    
    private void handleGridClick(int mx, int my) {

        int col = (mx - 44) / 100;
        int row = (my - 109) / 120;

        if (col < 0 || col >= 9 || row < 0 || row >= 5) return;

        Collider current = colliders[col + row * 9];

        // SHOVEL
        if (activePlantingBrush == GameWindow.PlantType.Shovel) {
            current.removePlant();
            activePlantingBrush = GameWindow.PlantType.None;
            setCursor(Cursor.getDefaultCursor());
            repaint();
            return;
        }

        if (current.getAssignedPlant() != null) return;

        if (activePlantingBrush == GameWindow.PlantType.Sunflower && getSunScore() >= 50) {
            current.setPlant(new Sunflower(this, col, row));
            setSunScore(getSunScore() - 50);
        }

        else if (activePlantingBrush == GameWindow.PlantType.Peashooter && getSunScore() >= 100) {
            current.setPlant(new Peashooter(this, col, row));
            setSunScore(getSunScore() - 100);
        }

        else if (activePlantingBrush == GameWindow.PlantType.FreezePeashooter && getSunScore() >= 175) {
            current.setPlant(new FreezePeashooter(this, col, row));
            setSunScore(getSunScore() - 175);
        }

        activePlantingBrush = GameWindow.PlantType.None;
        setCursor(Cursor.getDefaultCursor());
        repaint();
    }

    private void advance() {

        // Zombies + Peas theo lane
        for (int i = 0; i < 5; i++) {

            // Zombies
            for (int j = laneZombies.get(i).size() - 1; j >= 0; j--) {
                Zombie z = laneZombies.get(i).get(j);
                z.advance();

                if (z.getHealth() <= 0) {
                    laneZombies.get(i).remove(j);
                    continue;
                }
            }

            // Peas
            for (int j = lanePeas.get(i).size() - 1; j >= 0; j--) {
                Pea p = lanePeas.get(i).get(j);
                p.advance();

                if (p.getPosX() > 1000) {
                    lanePeas.get(i).remove(j);
                }
            }
        }

        //mat troi
        for (int i = 0; i < activeSuns.size(); i++) {
            activeSuns.get(i).advance();
        }
        
        // Kiểm tra win
        if (!levelFinished && zombieProducer.isRunning() == false) {

            boolean noZombieLeft = true;

            for (int i = 0; i < 5; i++) {
                if (!laneZombies.get(i).isEmpty()) {
                    noZombieLeft = false;
                    break;
                }
            }

            if (noZombieLeft) {
                levelFinished = true;

                repaint();

                SwingUtilities.invokeLater(() -> {
                    finishLevel();
                });
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bgImage, 0, 0, null);

        //Draw Plants
        for (int i = 0; i < 45; i++) {
            Collider c = colliders[i];
            if (c.getAssignedPlant() != null){
                Plant p = c.getAssignedPlant();
                if (p instanceof Peashooter) {
                    g.drawImage(peashooterImage, 60 + (i % 9) * 100, 129 + (i / 9) * 120, null);
                }
                if (p instanceof FreezePeashooter) {
                    g.drawImage(freezePeashooterImage, 60 + (i % 9) * 100, 129 + (i / 9) * 120, null);
                }
                if (p instanceof Sunflower) {
                    g.drawImage(sunflowerImage, 60 + (i % 9) * 100, 129 + (i / 9) * 120, null);
                }
            }
        }

        for (int i = 0; i < 5; i++) {
            for (Zombie z : laneZombies.get(i)) {
                if (z instanceof NormalZombie) {
                    g.drawImage(normalZombieImage, z.getPosX(), 109 + (i * 120), null);
                } else if (z instanceof ConeHeadZombie) {
                    g.drawImage(coneHeadZombieImage, z.getPosX(), 109 + (i * 120), null);
                }
            }

            for (int j = 0; j < lanePeas.get(i).size(); j++) {
                Pea pea = lanePeas.get(i).get(j);
                if (pea instanceof FreezePea) {
                    g.drawImage(freezePeaImage, pea.getPosX(), 130 + (i * 120), null);
                } else {
                    g.drawImage(peaImage, pea.getPosX(), 130 + (i * 120), null);
                }
            }

        }
        //them
        if (activePlantingBrush == GameWindow.PlantType.Shovel) {
            g.drawImage(shovelImage, mouseX - 40, mouseY - 40, 80, 80, null);
        }
        
        //them: hieu ung cho man hinh pause
        if (isPaused) {
            Graphics2D g2 = (Graphics2D) g;

            g2.setColor(new Color(0, 0, 0, 120));
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setFont(pauseFont);
            g2.setColor(new Color(102, 255, 102));

            String text = "PAUSED";

            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getHeight();

            int x = (getWidth() - textWidth) / 2;
            int y = (getHeight() - textHeight) / 2 + fm.getAscent();

            g2.drawString(text, x, y);
        }
        if (showFinalWaveText && finalWaveShape != null) {

            Graphics2D g2 = (Graphics2D) g;

            g2.setStroke(new BasicStroke(6f));
            g2.setColor(Color.BLACK);
            g2.draw(finalWaveShape);

            g2.setColor(new Color(200, 0, 0));
            g2.fill(finalWaveShape);

            if (System.currentTimeMillis() - finalWaveStartTime > 4000) {
                showFinalWaveText = false;
            }
        }
        
        if (showGameOverText) {

            Graphics2D g2 = (Graphics2D) g.create();

            String text = "ZOMBIES ATE YOUR BRAINS!";

            Font font = new Font("Arial", Font.BOLD, 60);
            g2.setFont(font);

            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(text);

            int lawnStartX = 60;
            int lawnWidth = 9 * 100;
            int lawnStartY = 110;
            int lawnHeight = 5 * 120;

            int x = lawnStartX + lawnWidth / 2 - textWidth / 2;
            int y = lawnStartY + lawnHeight / 2;

            TextLayout tl = new TextLayout(text, font, g2.getFontRenderContext());
            Shape shape = tl.getOutline(
                    AffineTransform.getTranslateInstance(x, y));

            g2.setStroke(new BasicStroke(6f));
            g2.setColor(Color.BLACK);
            g2.draw(shape);

            g2.setColor(new Color(180, 0, 0));
            g2.fill(shape);

            g2.dispose();
        }
    }
    
    

    private class PlantActionListener implements ActionListener {

        int x, y;

        public PlantActionListener(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            Collider current = colliders[x + y * 9];

            // 1️⃣ Nếu là shovel → xóa cây
            if (activePlantingBrush == GameWindow.PlantType.Shovel) {
                current.removePlant();
                activePlantingBrush = GameWindow.PlantType.None;
                return;
            }

            // 2️⃣ Nếu ô đã có cây → không cho đặt
            if (current.getAssignedPlant() != null) {
                activePlantingBrush = GameWindow.PlantType.None;
                return;
            }

            // 3️⃣ Đặt cây nếu đủ sun
            if (activePlantingBrush == GameWindow.PlantType.Sunflower) {
                if (getSunScore() >= 50) {
                    current.setPlant(new Sunflower(GamePanel.this, x, y));
                    setSunScore(getSunScore() - 50);
                }
            }

            else if (activePlantingBrush == GameWindow.PlantType.Peashooter) {
                if (getSunScore() >= 100) {
                    current.setPlant(new Peashooter(GamePanel.this, x, y));
                    setSunScore(getSunScore() - 100);
                }
            }

            else if (activePlantingBrush == GameWindow.PlantType.FreezePeashooter) {
                if (getSunScore() >= 175) {
                    current.setPlant(new FreezePeashooter(GamePanel.this, x, y));
                    setSunScore(getSunScore() - 175);
                }
            }

            activePlantingBrush = GameWindow.PlantType.None;
            
            setCursor(Cursor.getDefaultCursor());
            return;
        }
    }
    
    public void gameOver() {
        setCursor(Cursor.getDefaultCursor());
        if (gameOver) return;

        gameOver = true;
        showGameOverText = true;

        redrawTimer.stop();
        advancerTimer.stop();
        sunProducer.stop();
        zombieProducer.stop();

        repaint(); // vẽ chữ trước

        JOptionPane.showMessageDialog(this,
                "ZOMBIES ATE YOUR BRAIN!\nRestarting level");

        // Sau khi bấm OK mới restart
        GameWindow.gw.dispose();
        GameWindow.gw = new GameWindow();
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    private int progress = 0;

    public void addProgress(int num) {
        progress += num;
        if (progress > getLevelTarget()) {
            progress = getLevelTarget();
        }
        progressBar.setValue(progress);

        if (!finalWaveShown && progress >= getLevelTarget() * 0.8) {
            finalWaveShown = true;
            showFinalWaveText = true;
            finalWaveStartTime = System.currentTimeMillis();

            createFinalWaveShape();
        }
        if (progress >= getLevelTarget()) {
            zombieProducer.stop();
        }
    }
    
    private void createFinalWaveShape() {

        String text = "FINAL WAVE";
        Font font = new Font("Arial", Font.BOLD, 90);

        BufferedImage img = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setFont(font);

        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);

        int lawnStartX = 60;
        int lawnWidth = 9 * 100;
        int lawnStartY = 110;
        int lawnHeight = 5 * 120;

        int x = lawnStartX + lawnWidth / 2 - textWidth / 2;
        int y = lawnStartY + lawnHeight / 2;

        TextLayout tl = new TextLayout(text, font, g2.getFontRenderContext());
        finalWaveShape = tl.getOutline(
                AffineTransform.getTranslateInstance(x, y));

        g2.dispose();
    }

    public GameWindow.PlantType getActivePlantingBrush() {
        return activePlantingBrush;
    }

    public void setActivePlantingBrush(GameWindow.PlantType activePlantingBrush) {
        this.activePlantingBrush = activePlantingBrush;
    }

    public ArrayList<ArrayList<Zombie>> getLaneZombies() {
        return laneZombies;
    }

    public void setLaneZombies(ArrayList<ArrayList<Zombie>> laneZombies) {
        this.laneZombies = laneZombies;
    }

    public ArrayList<ArrayList<Pea>> getLanePeas() {
        return lanePeas;
    }

    public void setLanePeas(ArrayList<ArrayList<Pea>> lanePeas) {
        this.lanePeas = lanePeas;
    }

    public ArrayList<Sun> getActiveSuns() {
        return activeSuns;
    }

    public void setActiveSuns(ArrayList<Sun> activeSuns) {
        this.activeSuns = activeSuns;
    }

    public Collider[] getColliders() {
        return colliders;
    }

    public void setColliders(Collider[] colliders) {
        this.colliders = colliders;
    }
    
    //them
    public void setPaused(boolean paused) {
        isPaused = paused;

        for (int i = 0; i < 45; i++) {
            Collider c = colliders[i];
            if (c.getAssignedPlant() instanceof Sunflower) {
                if (paused) {
                    ((Sunflower) c.getAssignedPlant()).stop();
                } else {
                    ((Sunflower) c.getAssignedPlant()).resume();
                }
            }
        }

        if (paused) {
            setCursor(Cursor.getDefaultCursor()); 
            
            redrawTimer.stop();
            advancerTimer.stop();
            sunProducer.stop();
            zombieProducer.stop();
        } else {
            redrawTimer.start();
            advancerTimer.start();
            sunProducer.start();
            zombieProducer.start();
        }

        repaint();
    }
    
    //them
    private void updateLevelLabel() {
        levelLabel.setText("LEVEL " + LevelData.LEVEL_NUMBER);
    }
    
    private int getLevelTarget() {
        if ("1".equals(LevelData.LEVEL_NUMBER)) {
            return 150;
        } else if ("2".equals(LevelData.LEVEL_NUMBER)) {
            return 250;
        }
        return 150;
    }
    public void stopGame() {
    if (redrawTimer != null) redrawTimer.stop();
    if (advancerTimer != null) advancerTimer.stop();
    if (zombieProducer != null) zombieProducer.stop();
    if (sunProducer != null) sunProducer.stop();
    for (int i = 0; i < 45; i++) {
        Collider c = colliders[i];
        if (c.getAssignedPlant() instanceof Sunflower) {
            ((Sunflower) c.getAssignedPlant()).stop();
        }  
    }
    
    }
    
    //Finish game
    private void finishLevel() {
        setCursor(Cursor.getDefaultCursor());
        stopGame();

        if ("1".equals(LevelData.LEVEL_NUMBER)) {

            JOptionPane.showMessageDialog(this,
                    "LEVEL 1 Completed !!!\nStarting LEVEL 2");

            LevelData.write("2");
            progress = 0;
            zombieSpawnedCount = 0;
            
            GameWindow.gw.dispose();
            GameWindow.gw = new GameWindow();

        } else if ("2".equals(LevelData.LEVEL_NUMBER)) {

            JOptionPane.showMessageDialog(this,
                    "LEVEL 2 Completed !!!\nYOU WIN 🎉");

            LevelData.write("1");
            progress = 0;
            zombieSpawnedCount = 0;
            
            GameWindow.gw.dispose();
            GameWindow.gw = new GameWindow();
        }
    }
}