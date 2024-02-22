import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

public class Simulation extends JPanel implements ActionListener {
    private final int WIDTH = 1000;
    private final int HEIGHT = 600;
    private final double GRAVITY = 9.81; 
    private double v0; 
    private double radians; 
    private double degrees;
    private double x, y; 
    private double t; 
    private int y0;
    private int ground = 100;
    private Timer timer;
    private int pixelsToMeters = 50;
    private JTextField velocityTextField; 
    private JTextField thetaTextField; 
    private JTextField proportionTextField; 
    private JTextField offsetTextField; 

    public Simulation() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        degrees = 45;
        radians = Math.toRadians(degrees);
        y0 = 0;
        v0 = 10;
        x = 0;
        y = y0+(ground/pixelsToMeters);
        t = 0;

        timer = new Timer(1, this);
        timer.start();

        velocityTextField = new JTextField(""+v0, 5);
        velocityTextField.addActionListener(this);
        add(new JLabel("Initial Velocity (m/s):"));
        add(velocityTextField);
   
        thetaTextField = new JTextField(""+degrees, 5);
        thetaTextField.addActionListener(this);
        add(new JLabel("Angle (degrees):"));
        add(thetaTextField);
   
        offsetTextField = new JTextField(""+y0, 5);
        offsetTextField.addActionListener(this);
        add(new JLabel("Initial Y Poition (meters):"));
        add(offsetTextField);

        proportionTextField = new JTextField(""+pixelsToMeters, 5);
        proportionTextField.addActionListener(this);
        //add(new JLabel("Pixels per meters (pixels):"));
        //add(proportionTextField);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        DecimalFormat df = new DecimalFormat("###.##");

        int projectileRadius = 5;
        int xPixel = (int) (x * pixelsToMeters);
        int yPixel = (int) (HEIGHT - y * pixelsToMeters);

        g2d.setColor(Color.BLACK);
        g2d.fillOval(xPixel - projectileRadius, yPixel - projectileRadius, 2 * projectileRadius, 2 * projectileRadius);

        g2d.setColor(Color.BLUE);
        g2d.drawString("Time: " + df.format(t) + "s", 10, 20);
        g2d.drawString("X: " + df.format(x) + "m", 10, 40);
        g2d.drawString("Y: " + df.format(y-(ground/pixelsToMeters)) + "m", 10, 60);
        g2d.drawString("Max Height: " + df.format(max_height()) + "m", 10, 80);
        g2d.drawString("Max Distance: " + df.format(max_distance()) + "m", 10, 100);
        g2d.drawString("Max Time: " + df.format(max_time()) + "s", 10, 120);

        g2d.drawLine(0, HEIGHT-ground, WIDTH, HEIGHT-ground);

        for (int i = 0; i < WIDTH/pixelsToMeters; i++) {
            int n = i*pixelsToMeters;
            g2d.drawLine(n, HEIGHT-ground-5, n, HEIGHT-ground+5);
            g2d.drawString(""+i, n, HEIGHT-ground+30);
          }

          g2d.setColor(Color.RED);

          double maxDist = max_distance()*pixelsToMeters;
          g2d.drawLine((int) maxDist, HEIGHT-ground-10, (int) maxDist, HEIGHT-ground+10);

          g2d.drawLine(0, HEIGHT-ground-(y0*pixelsToMeters), 20, HEIGHT-ground-(y0*pixelsToMeters));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == velocityTextField) {
            try {
                v0 = Double.parseDouble(velocityTextField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid velocity input", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }else if (e.getSource() == thetaTextField) {
            try {
                degrees = Double.parseDouble(thetaTextField.getText());
                radians = Math.toRadians(degrees);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid theta input", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }else if (e.getSource() == proportionTextField) {
            try {
                pixelsToMeters = (int) Double.parseDouble(proportionTextField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid pixel input", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }else if (e.getSource() == offsetTextField) {
            try {
                y0 = (int) Double.parseDouble(offsetTextField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid pixel input", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        double deltaTime = 0.01; // time increment

        double vx = v0 * Math.cos(radians); // horizontal velocity
        double vy = v0 * Math.sin(radians) - GRAVITY * t; // vertical velocity

        x += vx * deltaTime; // update horizontal position
        y += vy * deltaTime; // update vertical position

        t += deltaTime; // increment time

        if (y <= (ground/pixelsToMeters)) {
            t = 0; // reset time
            x = 0; // reset x position
            y = y0+(ground/pixelsToMeters); // reset y position
        }

        repaint();
    }

    public double max_height(){
        double height = y0+(Math.pow(v0,2)*Math.pow(Math.sin(radians),2))/(2*GRAVITY);
        return height;
    }

    public double max_distance(){
        double distance = (v0 * Math.cos(radians) * (((v0 * Math.sin(radians) + Math.sqrt(Math.pow((v0 * Math.sin(radians)),2)+(2)*(GRAVITY)*(y0))))/(GRAVITY)));
        return distance;
    }
    public double max_time(){
        double time = (2 * v0 * Math.sin(radians)) / GRAVITY;
        return time;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("projectile simulation by logan fick");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new Simulation());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
