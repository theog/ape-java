package org.theo.aftokinito;


import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.cove.ape.APEngine;
import org.cove.ape.CircleParticle;
import org.cove.ape.RectangleParticle;
import org.cove.ape.SpringConstraint;
import org.cove.ape.Vector;

    /**
     * This is a simple example showing use of the WheelParticles and
     * SpringConstraints used as surfaces in the 'bridge' area. This example
     * makes use of the default drawing methods which are included for 
     * quick prototyping. 
     */
    public class carDemo extends Canvas implements Stage, KeyListener, MouseMotionListener, MouseListener {

        
        private static final long serialVersionUID = 1L;

        private BufferStrategy strategy;
        private long usedTime;

        //public APEngine APEngine = new APEngine();
        public ArrayList paintQueue;
        
        private int t;
        
        //private WheelParticle wheelParticleA;
        //private WheelParticle wheelParticleB;
        
        //private Lasso myLasso = new Lasso();
        
        //private Car myCar = new Car(200,-50);
        private Car myCar = new Car(2000,-1020);
        
        //private Bot myCar = new Bot(200,200);
        
        public Scroll Scroller = new Scroll();
        
        boolean gamePaused = false;
        
        String debug = new String();

        //TG TODO graphics mode not sure if this should be global?
        //public Graphics2D g = (Graphics2D)strategy.getDrawGraphics();

        public carDemo() {
        
            JFrame ventana = new JFrame("carDemo");
            JPanel panel = (JPanel)ventana.getContentPane();
            setBounds(0,0,Stage.SCREEN_WIDTH,Stage.SCREEN_HEIGHT);
            panel.setPreferredSize(new Dimension(Stage.SCREEN_WIDTH,Stage.SCREEN_HEIGHT));
            panel.setLayout(null);
            panel.add(this);
            ventana.setBounds(0,0,Stage.SCREEN_WIDTH,Stage.SCREEN_HEIGHT);
            ventana.setVisible(true);
            ventana.addWindowListener( new WindowAdapter() {
              public void windowClosing(WindowEvent e) {
                System.exit(0);
              }
            });
            ventana.setResizable(false);
            createBufferStrategy(2);
            strategy = getBufferStrategy();
            requestFocus();
            addKeyListener(this);
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        
        private boolean isGamePaused() {
            return gamePaused;
        }

        private void setGamePaused(boolean _gamePaused) {
            gamePaused = _gamePaused;
        }
        
        public void initWorld() {

            // the argument here is the deltaTime value. Higher values result in faster simulations.
            APEngine.init((double)1/3);
            
            // SELECTIVE is better for dealing with lots of little particles colliding, 
            APEngine.setCollisionResponseMode(APEngine.SELECTIVE);
            
            // gravity -- particles of varying masses are affected the same
            APEngine.addMasslessForce(new Vector(0,3));

            World.build();
            
            
            /*
            After adding all the particles and constraints, you can retrieve them using the 
            getXXX methods from the APEngine class. Then you can go through them and paint them
            when necessary. Alternatively you can keep track of them yourself by manually adding
            them to your own lists.
            */
            updatePaintQueue();
            
            myCar.setGameObject(this);

            Scroller.setFocusOn(myCar);
            //Scroller.setFocusOn(WheelParticle_179);
            
            
            // freeze frame settings. press f during the game to get all co-ordinates and then replace this line.
            //myCar.wheelParticleA.curr.x = 498.2920445953684;myCar.wheelParticleA.curr.y = -479.466650579958;myCar.wheelParticleA.prev.x = 488.2750591559789;myCar.wheelParticleA.prev.y = -486.059672619798;myCar.wheelParticleB.curr.x = 441.60890270789275;myCar.wheelParticleB.curr.y = -501.7370562075975;myCar.wheelParticleB.prev.x = 431.17907072841257;myCar.wheelParticleB.prev.y = -505.1285035464687;myCar.topOfCar.curr.x = 455.9898238542994;myCar.topOfCar.curr.y = -455.8182752114062;myCar.topOfCar.prev.x = 447.06457919718025;myCar.topOfCar.prev.y = -458.83439239046805;myCar.blockWeight.curr.x = 451.8240038342456;myCar.blockWeight.curr.y = -471.2035696219205;myCar.blockWeight.prev.x = 441.3748758390291;myCar.blockWeight.prev.y = -473.7041062557318;
            
            
        }

        public void updatePaintQueue() {
             paintQueue = APEngine.getAll();
         }

        public static void main(String[] args) {
            carDemo inv = new carDemo();
            inv.game();
        }

         public void paintfps(Graphics2D g) {
              g.setFont( new Font("Arial",Font.BOLD,12));
              //g.setColor(Color.black);
              if (usedTime > 0)
                  g.drawString(String.valueOf(1000/usedTime)+" fps",Stage.SCREEN_WIDTH-50,Stage.PLAY_HEIGHT);
              else
                  g.drawString("--- fps",Stage.SCREEN_WIDTH-50,Stage.PLAY_HEIGHT);
          }
         
         public void paintWorld() {
            Graphics2D g = (Graphics2D)strategy.getDrawGraphics();
            // Enable antialiasing for shapes
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                 RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (!isGamePaused()) {
                updateWorld(g);
            }
            
            Scroller.paint(g);
            
            myCar.setDefaultContainer(g);
            myCar.paint(Scroller.offsetX,Scroller.offsetY);

            // TG set the default container.
            APEngine.setDefaultContainer(g);
            
            for (int i = 0; i < paintQueue.size(); i++) {
                if (paintQueue.get(i) instanceof RectangleParticle)  
                    ((RectangleParticle)paintQueue.get(i)).paint(Scroller.offsetX,Scroller.offsetY);
                else if (paintQueue.get(i) instanceof CircleParticle) 
                    ((CircleParticle)paintQueue.get(i)).paint(Scroller.offsetX,Scroller.offsetY);
                else if (paintQueue.get(i) instanceof SpringConstraint) 
                    ((SpringConstraint)paintQueue.get(i)).paint(Scroller.offsetX,Scroller.offsetY);             
            }
            
            paintfps(g);
            
            //g.drawString("mode:" + String.valueOf(mode),Stage.SCREEN_WIDTH-100,Stage.PLAY_HEIGHT);
            
            strategy.show();
            
            //TG TODO not sure if I should be clearing the screen, otherwise the screen does not refesh properly, need to investigate the best approach.
            //g.dispose();
            g.clearRect(0,0,Stage.SCREEN_WIDTH,Stage.SCREEN_HEIGHT);
        }      
        
        public void updateWorld(Graphics2D g) {

            APEngine.step();
            
            myCar.Step();
            
            Scroller.step();
        }      

        // TG TODO private void game() {
        public void game() {
            //TG TODO change while condition later
            boolean i = true;
            
            usedTime=1000;
            t = 0;
            initWorld();
            while (i==true) {
                t++;
                long startTime = System.currentTimeMillis();
                //checkCollisions();
                paintWorld();
                //System.out.println(String.valueOf(System.currentTimeMillis()-startTime));
                usedTime = System.currentTimeMillis()-startTime;
                try { 
                    Thread.sleep(SPEED);
                } catch (InterruptedException e) {}
            }
        }


        public void keyTyped(KeyEvent e) {}

        public void keyPressed(KeyEvent e) {
                
               // toggle pause game
               if (e.getKeyCode() == KeyEvent.VK_P) {
                   System.out.println("game paused.");
                   if (isGamePaused()) {
                       setGamePaused(false);
                   } else {
                       setGamePaused(true);
                   }
               }
               
               myCar.keyPressed(e);
        }
            
        public void keyReleased(KeyEvent e) {
            myCar.keyReleased(e);
        }


        public void mouseClicked(MouseEvent e) {
            myCar.mouseClicked(e);
        }

        public void mouseEntered(MouseEvent e) {
            // TODO Auto-generated method stub
            
        }

        public void mouseExited(MouseEvent e) {
            // TODO Auto-generated method stub
            
        }


        public void mouseDragged(MouseEvent e) {
            // TODO Auto-generated method stub
            
        }


        public void mouseMoved(MouseEvent e) {
            // TODO Auto-generated method stub
            myCar.setCurrentMousePosition(e.getX(), e.getY());
        }


        public void mousePressed(MouseEvent e) {
            // TODO Auto-generated method stub
            
        }


        public void mouseReleased(MouseEvent e) {
            // TODO Auto-generated method stub
            
        }
    }
