package org.theo.depricated;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.swing.JSVGCanvas;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

public class CreatorPlus extends JFrame implements ActionListener,
        ChangeListener {

// Two Buttons to draw the shapes
private JButton btnCircle, btnSquare;

// Two Spinners to change the stroke width and the shapes' size
private JSpinner spStroke, spSize;

// The SVG generator, we know it already
private SVGGraphics2D generator;

// And this is the main thing we are going to deal with here
private JSVGCanvas canvas;

// We'll need to access this Document in a method, so we
// make it a class member
private SVGDocument doc;

// We use Spinners to change these values
private float strokeWidth = 20;
private int figureSize = 90;

// Constructor
public CreatorPlus () {
    // Everything is as usual here
    super("SVG Creator Plus");
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JPanel panel = new JPanel();

    // This is an auxilliary panel to group together Buttons,
    // Labels and Spinners
    JPanel p = new JPanel();

    // And here our canvas is instantiated
    canvas = new JSVGCanvas();

    // As before, the buttons are created and associated
    // with an Action Listener
    btnCircle = new JButton("Circle");

    btnSquare = new JButton("Square");
    btnCircle.addActionListener(this);
    btnSquare.addActionListener(this);

    // Two labels to be used with Spinners
    JLabel lblStrokeWidth = new JLabel("Stroke Width:");
    JLabel lblFigureSize = new JLabel("Figure Size:");

    // And then the Spinners themselves are created and
    // associated with a Change Listener - basic Swing
    SpinnerModel widthModel =
            new SpinnerNumberModel(20, 10, 50, 10);
    spStroke = new JSpinner(widthModel);
    spStroke.addChangeListener(this);
    SpinnerModel sizeModel =
            new SpinnerNumberModel(90, 60, 110, 10);
    spSize = new JSpinner(sizeModel);
    spSize.addChangeListener(this);

    // All the controls are added to the auxiliary panel
    p.add(lblStrokeWidth);
    p.add(spStroke);
    p.add(lblFigureSize);
    p.add(spSize);
    p.add(btnCircle);
    p.add(btnSquare);

    // And the main panel is filled with content and ready
    // to be displayed
    panel.setLayout(new BorderLayout());
    panel.add("North", p);
    panel.add("Center", canvas);
    this.setContentPane (panel);
    this.pack();
    this.setBounds (150, 150, this.getWidth(), this.getHeight ());

    // The SVGDocument and the SVG Generator are created, in
    // a slightly different way than before
    DOMImplementation dom =
            SVGDOMImplementation.getDOMImplementation ();
    String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
    doc = (SVGDocument) dom.createDocument(svgNS, "svg", null);
    generator = new SVGGraphics2D(doc);
}

public static void main(String[] args) {
    CreatorPlus creator = new CreatorPlus();
    creator.setVisible(true);
}

public void actionPerformed(ActionEvent ae) {
    Object source = ae.getSource();
    // Here we are randomly changing
    // the location of shapes
    int x = (int) (300 * Math.random());
    int y = (int) (100 * Math.random());

    // Drawing a shape depending on which button was pressed
    if (source == btnCircle) {
        drawCircle(x,y);
    }
    if (source == btnSquare) {
        drawSquare(x,y);
    }

    // And this is where the generated content is passed
    // to the JSVGCanvas
    Element root = doc.getDocumentElement();
    generator.getRoot(root);
    canvas.setSVGDocument(doc);
}

// Changing the stroke and the size, basic Spinner usage
public void stateChanged(ChangeEvent e) {
    JSpinner source = (JSpinner)e.getSource();
    if (source == spStroke) {
        SpinnerNumberModel model =
                (SpinnerNumberModel) source.getModel();
        strokeWidth = ( (Integer)model.getValue()).intValue();
    }
    if (source == spSize) {
         SpinnerNumberModel model = (SpinnerNumberModel) source.getModel();
       figureSize = ( (Integer)model.getValue()).intValue();
    }
}

// Drawing itself, only slightly different from what was
// done in the previous application
private void drawSquare(int x, int y) {
    Rectangle rect = new Rectangle(x, y, figureSize,
               figureSize);
       generator.setPaint(Color.red);
       generator.setStroke(new BasicStroke (strokeWidth));
       generator.draw (rect);
       generator.setPaint (Color.lightGray);
       generator.fill(rect);
    }

    private void drawCircle (int x, int y) {
       Ellipse2D circle = new Ellipse2D.Double (x, y, figureSize,
               figureSize);
       generator.setPaint (Color.green);
       generator.setStroke (new BasicStroke (strokeWidth));
       generator.draw(circle);
       generator.setPaint (Color.yellow);
       generator.fill (circle);
    }
}

