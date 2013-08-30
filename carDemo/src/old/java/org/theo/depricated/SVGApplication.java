package org.theo.depricated;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.svg.SVGLoadEventDispatcherAdapter;
import org.apache.batik.swing.svg.SVGLoadEventDispatcherEvent;
import org.apache.batik.script.Window;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

public class SVGApplication {

    public static void main(String[] args) {
        new SVGApplication();
    }

    JFrame frame;
    JSVGCanvas canvas;
    Document document;
    Window window;

    public SVGApplication() {
        frame = new JFrame();
        canvas = new JSVGCanvas();
        // Forces the canvas to always be dynamic even if the current
        // document does not contain scripting or animation.
        canvas.setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);
        canvas.addSVGLoadEventDispatcherListener
            (new SVGLoadEventDispatcherAdapter() {
                    public void svgLoadEventDispatchStarted
                        (SVGLoadEventDispatcherEvent e) {
                        // At this time the document is available...
                        document = canvas.getSVGDocument();
                        // ...and the window object too.
                        window = canvas.getUpdateManager().
                            getScriptingEnvironment().createWindow();
                        // Registers the listeners on the document
                        // just before the SVGLoad event is
                        // dispatched.
                        registerListeners();
                        // It is time to pack the frame.
                        frame.pack();
                    }
                });
        
        frame.addWindowListener(new WindowAdapter() {
                public void windowOpened(WindowEvent e) {
                    // The canvas is ready to load the base document
                    // now, from the AWT thread.
                    canvas.setURI("http://openclipart.org/people/johnny_automatic/johnny_automatic_sword_of_justice.svg");
                }
            });

        frame.getContentPane().add(canvas);
        frame.setSize(800, 600);
        frame.show();
    }

    public void registerListeners() {
        // Gets an element from the loaded document.
        Element elt = document.getElementById("elt-id");
        EventTarget t = (EventTarget)elt;

        // Adds a 'onload' listener
        t.addEventListener("SVGLoad", new OnLoadAction(), false);

        // Adds a 'onclick' listener
        t.addEventListener("click", new OnClickAction(), false);
    }

    public class OnLoadAction implements EventListener {
        public void handleEvent(Event evt) {
            // Perform some actions here...
            
            // ...for example start an animation loop:
            window.setInterval(new Animation(), 50);
        }
    }

    public class OnClickAction implements EventListener {
        public void handleEvent(Event evt) {
            // Perform some actions here...

            // ...for example schedule an action for later:
            window.setTimeout(new DelayedTask(), 500);
        }
    }

    public class Animation implements Runnable {
        public void run() {
            // Insert animation code here...
        }
    }

    public class DelayedTask implements Runnable {
        public void run() {
            // Perform some actions here...

            // ...for example displays an alert dialog:
            window.alert("Delayed Action invoked!");
        }
    }
}