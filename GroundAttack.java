import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;

@SuppressWarnings("serial")
public class GroundAttack extends JPanel {
//Variable Definitions
    static final int grid = 5;
    static final int w = 1350;
    static final int h = 750;
    static final int gridW = w/grid;
    static final int gridH = h/grid;
    static final Image warthogH = imageURL("https://github.com/CrazyMeowCows/GroundAttack/blob/main/WarthogH.png?raw=true");
    static final Image warthogV = imageURL("https://github.com/CrazyMeowCows/GroundAttack/blob/main/WarthogV.png?raw=true");

    static int warthogX = grid*32;
    static int warthogY = h/2;
    static Boolean[] keys = {false, false, false, false, false};
    static double[][] ground = new double[h/grid][w/grid];
    static double[] dunes = new double[30*2];
    static double[][] particles = new double[0][4];

    public static void main(String[] args) {
    //Frame and Panel Setup
        JFrame frame = new JFrame("Ground Attack");

        GroundAttack KeyboardInput = new GroundAttack();
        frame.add(KeyboardInput);

        DrawingManager panel = new DrawingManager();
        panel.setPreferredSize(new Dimension(w, h));
        frame.add(panel);

        frame.pack();
        frame.setIconImage(imageURL("https://github.com/CrazyMeowCows/GroundAttack/blob/main/DragonLogo.png?raw=true"));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
    //Initialize the dune locations
        for(int i = 0; i < dunes.length; i += 2){
            dunes[i] = Math.random()*gridW*3;
            dunes[i+1] = Math.random()*gridH*3;
        }

    //Start of Timer
        Timer timer = new Timer(40, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if(keys[0] && warthogY > grid*18){warthogY -= 2*grid;}
                if(keys[2] && warthogY < h-grid*18){warthogY += 2*grid;}
                if(keys[4]){
                    double[] bullet = {warthogX+grid*16, warthogY-grid, Math.toRadians(Math.random()-0.5), grid*10};
                    pushArray(bullet);
                }
                
                for(int i = 0; i < dunes.length; i += 2){
                    dunes[i] -= grid;
                    if(dunes[i] < -gridW){dunes[i] = Math.random()*gridW+gridW*2;}
                }

                for(int i = 0; i < particles.length; i++){
                    particles[i][0] += Math.cos(particles[i][2])*particles[i][3];
                    particles[i][1] += Math.sin(particles[i][2])*particles[i][3];
                }

                for(int x = 0; x < gridW; x++){
                    for(int y = 0; y < gridH; y++){
                        double min = Double.POSITIVE_INFINITY;
                        for(int i = 0; i < dunes.length; i += 2){
                            double dist = Math.sqrt((x-dunes[i])*(x-dunes[i]) + (y-dunes[i+1])*(y-dunes[i+1]));
                            if(dist < min){min = dist;}
                        }
                        ground[y][x] = Math.min(min, 255);
                    }
                }

                frame.repaint();
            }
        });
    //End of Timer
        timer.start();  
    }

//Drawing Manager
    static class DrawingManager extends JPanel {
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);    

            //Drawing Background
            for(int x = 0; x < gridW; x++){
                for(int y = 0; y < gridH; y++){
                    double color = (double)ground[y][x]/255;
                    color = (double)Math.round(color*50)/50;
                    g.setColor(new Color((int)((1-color)*207+color*132), (int)((1-color)*194+color*114), (int)((1-color)*155+color*63)));
                    g.fillRect(x*grid, y*grid, grid, grid);
                }
            }

            g.setColor(Color.RED);
            for(int i = 0; i < particles.length; i++){
                g.fillRect((int)particles[i][0], (int)particles[i][1], grid*2, grid*2);
            }
            System.out.println(particles.length);

            //Drawing Warthog
            g.drawImage(warthogV, warthogX-grid*32/2, warthogY-grid*32/2, grid*32, grid*32, null);
        }
    }

//Keyboard Input Setup
    public GroundAttack() {
		KeyListener listener = new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}

			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
					case 87: keys[0] = true; break;
					case 65: keys[1] = true; break;
					case 83: keys[2] = true; break;
					case 68: keys[3] = true; break;
					case 32: keys[4] = true; break;
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
					case 87: keys[0] = false; break;
					case 65: keys[1] = false; break;
					case 83: keys[2] = false; break;
					case 68: keys[3] = false; break;
					case 32: keys[4] = false; break;
				}
			}
		};
		addKeyListener(listener);
		setFocusable(true);
    }
    
//Getting Image from URl
    public static Image imageURL(String link){  
        Image finalImage = null;
        try {
            URL logoURL = new URL(link);
            Image icon = ImageIO.read(logoURL);  
            finalImage = icon;
        } catch(IOException ie) {
            ie.printStackTrace();
        }
        return finalImage;
    } 

//Drawing an Image at an Angle
    public static void drawRotatedImage(Graphics2D g2d, Image image, double angle, int x, int y){  
        AffineTransform backup = g2d.getTransform();
        AffineTransform a = AffineTransform.getRotateInstance(-angle, x, y);
        g2d.setTransform(a);
        g2d.drawImage(image, x-image.getWidth(null)/2, y-image.getHeight(null)/2, null);
        g2d.setTransform(backup);
    }  

//Drawing an Image at an Angle
public static void pushArray(double[] push){
        double[][] tempArray = new double[particles.length+1][4];
        for(int i = 0; i < particles.length; i++){
            tempArray[i] = particles[i];
        }
        tempArray[tempArray.length-1] = push;
        particles = tempArray;
    }
}