import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

// Represents a particle
class Particle {
    Point pos;
    double vx;
    double vy;
    int size;
    boolean isColliding;
    Point initialPos;

    // Constructor
    Particle(Point pos) {
        this.pos = pos;
        this.vx = 0;
        this.vy = 0;
        this.size = 10; // Initial size of the particle
        this.isColliding = false;
        this.initialPos = null; // Initial position for line drawing
    }

    // Updates the position and velocity of the particle
    void update(double gravity) {
        // Update position
        this.pos.x += (int) this.vx;
        this.pos.y += (int) this.vy;

        // Apply gravity
        this.vy += gravity;

        // Check collision with the edge of the circle
        double distanceFromCenter = Math.sqrt(Math.pow(this.pos.x - 200, 2) + Math.pow(this.pos.y - 200, 2));
        if (distanceFromCenter > 200 - this.size / 2.0) {
            if (!this.isColliding) {
                // Calculate the normal vector (from the center of the circle to the particle)
                double nx = this.pos.x - 200;
                double ny = this.pos.y - 200;
                // Normalize the normal vector
                double length = Math.sqrt(nx * nx + ny * ny);
                nx /= length;
                ny /= length;
                // Calculate the dot product of the velocity and the normal
                double dot = this.vx * nx + this.vy * ny;
                // Reflect the velocity across the normal
                this.vx -= 2 * dot * nx;
                this.vy -= 2 * dot * ny;

                // Increase particle size
                this.size += 2;
                this.isColliding = true;
            }
        } else {
            this.isColliding = false;
        }
    }

    // Draws the particle
    void draw(Graphics2D g2d) {
        g2d.setColor(Color.RED);
        g2d.fill(new Ellipse2D.Double(this.pos.x - this.size / 2.0, this.pos.y - this.size / 2.0, this.size, this.size));

        // Draw the line if the initial position is set
        if (this.initialPos != null) {
            g2d.setColor(Color.BLUE);
            g2d.setStroke(new BasicStroke(3)); // Set the line thickness to 3
            g2d.draw(new Line2D.Double(this.initialPos.x, this.initialPos.y, this.pos.x, this.pos.y));
        }
    }

    // Resets the size of the particle to its initial size
    void resetSize() {
        this.size = 10;
    }
}

// Represents the game world
class GamePanel extends JPanel implements MouseMotionListener, MouseListener, ActionListener {
    Particle particle;
    Timer timer;
    double gravity;
    JButton increaseGravityButton;
    JButton decreaseGravityButton;
    JButton resetSizeButton;

    // Constructor
    GamePanel() {
        this.particle = new Particle(new Point(200, 200));
        this.gravity = 0.2;
        this.timer = new Timer(30, this); // roughly 0.03 seconds per tick
        this.timer.start();
        this.addMouseMotionListener(this);
        this.addMouseListener(this);

        // Create buttons
        this.increaseGravityButton = new JButton("Increase Gravity");
        this.decreaseGravityButton = new JButton("Decrease Gravity");
        this.resetSizeButton = new JButton("Reset Size");

        // Add action listeners to buttons
        this.increaseGravityButton.addActionListener(e -> this.gravity += 0.1);
        this.decreaseGravityButton.addActionListener(e -> this.gravity -= 0.1);
        this.resetSizeButton.addActionListener(e -> this.particle.resetSize());

        // Add buttons to the panel
        this.setLayout(null);
        this.increaseGravityButton.setBounds(350, 420, 130, 30);
        this.decreaseGravityButton.setBounds(350, 460, 130, 30);
        this.resetSizeButton.setBounds(350, 500, 130, 30);
        this.add(increaseGravityButton);
        this.add(decreaseGravityButton);
        this.add(resetSizeButton);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Draw the circular boundary
        g2d.setColor(Color.BLACK);
        g2d.draw(new Ellipse2D.Double(0, 0, 400, 400));

        // Draw the particle
        this.particle.draw(g2d);

        // Display velocity and acceleration
        double vxRounded = Math.round(this.particle.vx * 10) / 10.0;
        double vyRounded = Math.round(this.particle.vy * 10) / 10.0 * -1;
        g2d.setColor(Color.BLACK);
        g2d.drawString("Velocity: (" + vxRounded + ", " + vyRounded + ")", 10, 420);
        g2d.drawString("Acceleration: (0, " + -this.gravity + ")", 10, 440);
        g2d.drawString("Heads up, the units are in pixels per tick. A tick here is about 0.03 seconds", 10, 460);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.particle.update(this.gravity);
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (this.particle.initialPos == null) {
            this.particle.initialPos = this.particle.pos;
        }
        this.particle.pos = e.getPoint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        double dx = this.particle.initialPos.x - e.getX(); // Reverse direction
        double dy = this.particle.initialPos.y - e.getY(); // Reverse direction
        this.particle.vx = dx / 10.0;
        this.particle.vy = dy / 10.0;
        this.particle.initialPos = null; // Reset the initial position after release
    }

    // Unused mouse event methods
    @Override
    public void mouseMoved(MouseEvent e) { }

    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mousePressed(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }
}

// Runs the game
class ParticleGame {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Particle Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 550); // Increased height to accommodate the new button
        frame.add(new GamePanel());
        frame.setVisible(true);
    }
}


