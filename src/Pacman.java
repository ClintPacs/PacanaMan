import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Pacman extends JFrame {
    private JPanel mainPanel;
    private JPanel gamePanel;
    private JButton startButton;
    private JButton restartButton;
    private JLabel scoreLabel;

    private Timer gameTimer;
    private int pacmanX = 100, pacmanY = 100;
    private int pacmanSize = 20;
    private int directionX = 0, directionY = 0;
    private int score = 0;

    private List<Rectangle> walls = new ArrayList<>();
    private Rectangle cherry;
    private List<Ghost> ghosts = new ArrayList<>();

    public Pacman() {
        setTitle("Pac-Man");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null);

        // Initialize components
        createUIComponents();
        setContentPane(mainPanel);

        gameTimer = new Timer(100, e -> updateGame());

        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();

        gamePanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP -> { directionX = 0; directionY = -5; }
                    case KeyEvent.VK_DOWN -> { directionX = 0; directionY = 5; }
                    case KeyEvent.VK_LEFT -> { directionX = -5; directionY = 0; }
                    case KeyEvent.VK_RIGHT -> { directionX = 5; directionY = 0; }
                }
            }
        });

        startButton.addActionListener(e -> startGame());
        restartButton.addActionListener(e -> resetGame());

        setVisible(true);
    }

    private void createUIComponents() {
        // Create the game panel with custom painting for Pac-Man
        gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());

                // Draw walls
                g.setColor(Color.BLUE);
                for (Rectangle wall : walls) {
                    g.fillRect(wall.x, wall.y, wall.width, wall.height);
                }

                // Draw cherry (if it exists)
                if (cherry != null) {
                    g.setColor(Color.RED);
                    g.fillOval(cherry.x, cherry.y, 10, 10); // Cherry size 10x10
                }

                // Draw Pac-Man
                g.setColor(Color.YELLOW);
                g.fillArc(pacmanX, pacmanY, pacmanSize, pacmanSize, 30, 300);

                // Draw ghosts
                for (Ghost ghost : ghosts) {
                    ghost.draw(g);
                }
            }
        };

        // Initialize main panel and set layout
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Add the game panel to the center
        mainPanel.add(gamePanel, BorderLayout.CENTER);

        // Create score label at the top
        scoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        mainPanel.add(scoreLabel, BorderLayout.NORTH);

        // Create the start button at the bottom
        startButton = new JButton("Start");
        mainPanel.add(startButton, BorderLayout.WEST);

        // Create the restart button at the bottom
        restartButton = new JButton("Restart");
        mainPanel.add(restartButton, BorderLayout.EAST);
    }

    private void startGame() {
        // Generate maze, ghosts, and cherry when the game starts
        generateMaze();
        spawnGhosts();
        spawnCherry();
        gameTimer.start();
        gamePanel.requestFocusInWindow();
    }

    private void resetGame() {
        pacmanX = 100;
        pacmanY = 100;
        directionX = 0;
        directionY = 0;
        score = 0;
        scoreLabel.setText("Score: 0");
        gamePanel.repaint();
        generateMaze();
        spawnGhosts();
        spawnCherry();
        gamePanel.requestFocusInWindow();
    }

    private void updateGame() {
        // Update Pac-Man's position
        pacmanX += directionX;
        pacmanY += directionY;

        // Collision detection with maze walls
        for (Rectangle wall : walls) {
            if (wall.contains(pacmanX + pacmanSize / 2, pacmanY + pacmanSize / 2)) {
                pacmanX -= directionX;
                pacmanY -= directionY;
            }
        }

        // Collision detection with cherry
        if (cherry != null && new Rectangle(pacmanX, pacmanY, pacmanSize, pacmanSize).intersects(cherry)) {
            cherry = null; // Eat the cherry
            score += 300; // Add points
            scoreLabel.setText("Score: " + score);
            spawnCherry(); // Respawn cherry
        }

        // Collision detection with ghosts
        for (Ghost ghost : ghosts) {
            if (ghost.getBounds().intersects(new Rectangle(pacmanX, pacmanY, pacmanSize, pacmanSize))) {
                // Reset game on Pac-Man being caught by a ghost
                resetGame();
            }
        }

        // Move ghosts
        for (Ghost ghost : ghosts) {
            ghost.move();
        }

        gamePanel.repaint();
    }

    private void generateMaze() {
        walls.clear();
        // Add walls manually (example maze layout)
        walls.add(new Rectangle(50, 50, 200, 20)); // Top wall
        walls.add(new Rectangle(50, 50, 20, 200)); // Left wall
        walls.add(new Rectangle(250, 50, 20, 200)); // Right wall
        walls.add(new Rectangle(50, 250, 200, 20)); // Bottom wall
        // You can expand this to create a full maze
    }

    private void spawnGhosts() {
        ghosts.clear();
        ghosts.add(new Ghost(200, 200, Color.RED));
        ghosts.add(new Ghost(300, 200, Color.BLUE));
        ghosts.add(new Ghost(200, 300, Color.GREEN));
    }

    private void spawnCherry() {
        Random rand = new Random();
        int x = rand.nextInt(gamePanel.getWidth() - 10); // Avoid going outside the panel
        int y = rand.nextInt(gamePanel.getHeight() - 10);
        cherry = new Rectangle(x, y, 10, 10); // Cherry size 10x10
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Pacman::new);
    }

    // Ghost class to represent ghosts in the game
    static class Ghost {
        private int x, y;
        private Color color;

        public Ghost(int x, int y, Color color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }

        public void move() {
            // Simple random movement (you can expand this to implement smarter AI)
            Random rand = new Random();
            x += rand.nextInt(3) - 1; // Move left, right, or stay
            y += rand.nextInt(3) - 1; // Move up, down, or stay
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, 20, 20); // Ghost size 20x20
        }

        public void draw(Graphics g) {
            g.setColor(color);
            g.fillOval(x, y, 20, 20); // Ghost size 20x20
        }
    }
}
