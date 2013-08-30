package org.theo.depricated;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.apache.batik.swing.JSVGCanvas;

public class SVGFileViewer2 extends Canvas
        implements ActionListener {
    // A button to open the dialog
    private JButton btnOpenFile;
    // A file chooser
    private JFileChooser fcOpenFile = new JFileChooser();

    // The canvas
    private JSVGCanvas canvas = new JSVGCanvas();

    JFrame ventana = new JFrame("carDemo");

    public SVGFileViewer2() {
        // The usual Swing-specific code
        //super("SVG File Viewer");
        //this.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        btnOpenFile = new JButton("Open File...");
        btnOpenFile.addActionListener(this);
        panel.setLayout(new BorderLayout());
        panel.add("North", btnOpenFile);

        // Setting the size of the canvas
        canvas.setMySize(new Dimension(300, 300));

        // Preparing the components for displaying
        panel.add("Center", canvas);
        ventana.setContentPane (panel);
        ventana.pack ();
        ventana.setBounds(150, 150, ventana.getWidth(), ventana.getHeight() );
        
        canvas.setEnablePanInteractor(true);
    }
    public static void main (String[] args) {
        SVGFileViewer2 viewer = new SVGFileViewer2();
        viewer.setVisible(true);
    }

    public void actionPerformed (ActionEvent ae) {
        Object source = ae.getSource ();
        if (source == btnOpenFile) {
            int returnVal = fcOpenFile.showOpenDialog (ventana);
            try {
                // If after choosing a file the "Open" button
                // was pressed
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fcOpenFile. getSelectedFile();
                    canvas.setURI (file.toURL() .toString() );
                    System.out.println(file.toURL().toString());
                }
            } catch (IOException ioe) {
                System.err.print (ioe.toString());
            }
        }
    }
}

