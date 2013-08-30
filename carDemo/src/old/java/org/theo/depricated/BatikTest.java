package org.theo.depricated;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;

import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.dom.GenericDOMImplementation;

import org.w3c.dom.Document;

public class BatikTest extends Canvas implements Stage, KeyListener, MouseMotionListener, MouseListener {

    private BufferStrategy strategy;
    
    public void initScreen() {
        
        JFrame ventana = new JFrame("BatikTest");
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

    public static void main(String[] args) {

        BatikTest inv = new BatikTest();
        inv.initScreen();
        inv.game();
    }

   public void game() {
        try {
            String parser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
            String uri = "http://openclipart.org/people/johnny_automatic/johnny_automatic_sword_of_justice.svg";
            Document doc = f.createDocument(uri);
            
            // Create an instance of the SVG Generator.
            SVGGraphics2D svgGenerator = new SVGGraphics2D(doc);

            // Ask the test to render into the SVG Graphics2D implementation.
            Graphics2D g = (Graphics2D)strategy.getDrawGraphics();
            // Enable antialiasing for shapes
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                 RenderingHints.VALUE_ANTIALIAS_ON);
            
            //paint(svgGenerator);
            paint(g);
            //g.paint(svgGenerator);
            
        } catch (IOException ex) {
            System.out.println("asdfsdf");
        }        
        
    }

   public void paint(Graphics2D g2d) {
       g2d.setPaint(Color.red);
       g2d.fill(new Rectangle(10, 10, 100, 100));
   }
    public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub
        
    }

    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub
        
    }

    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
        
    }

    public void mouseDragged(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    public void mouseMoved(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

}
