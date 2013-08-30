package org.theo.depricated;
 

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashMap;

import org.cove.ape.APEngine;
import org.cove.ape.AbstractParticle;
import org.cove.ape.CircleParticle;
import org.cove.ape.Interval;
import org.cove.ape.RectangleParticle;
import org.cove.ape.Vector;
import org.theo.aftokinito.carDemo;
	
	//internal final class CollisionDetector {
	public class Designer {
		//private Interval _mousePosition;
		private Vector _mouseStartPosition = new Vector(0,0);
		private Vector _mouseEndPosition = new Vector(0,0);
		private Vector _mouseCurrentPosition = new Vector(0,0);
		public double _mouseDegrees;
		public double _mouseRadians;
		
		//private RectangleParticle _SelectedArea;
		//private CircleParticle _rotateHandler;
		
		private ArrayList _selectedObjects = new ArrayList();;

		// list of objects used to design a level, e.g.
		HashMap _designerObjects = new HashMap (); 
		
		private String _mode = "";  // drawing mode
		private int _tool = 1;  // 1.select, 2.pointer, 3.new box, 4.new circle, 5. hand, 6. rotate 

		// object references
		public APEngine _APEngine;
		public carDemo _gameObject;
		
		private static Graphics2D _defaultContainer;

		public Designer() {
		}
		
		public void setAPEngine(APEngine APEngine) {
			_APEngine = APEngine;
		}	

		public void setGameObject(carDemo gameObject) {
			_gameObject = gameObject;
		}	
		
		public void setMode(String mode) {
			_mode = mode;
		}	

		public String getMode() {
			return _mode;
		}	
		
		public void setTool(int tool) {
			_tool = tool;
		}	

		public int getTool() {
			return _tool;
		}	


		private double getAngle() {
			
			// get current position of where projector will come from
			int x = (int)(((AbstractParticle)getSelectedObjects().get(0)).getpx()+_gameObject.Scroller.offsetX);
			int y = (int)(((AbstractParticle)getSelectedObjects().get(0)).getpy()+_gameObject.Scroller.offsetY);
			
			// get mouse position
			int x1 = (int)_mouseCurrentPosition.x;
			int y1 = (int)_mouseCurrentPosition.y;

			double px = Math.abs(x1-x);
			double py = Math.abs(y1-y);
			//double theta = Math.atan2(py, px);
			double theta = Math.atan(py/px);
			double angle=0;
			   
			if(x1 >= x && y1 <= y) {
				 // quadrant I
				angle = theta;
			} else if (x1 < x && y1 <= y){
				//quadrant II
				angle = Math.PI - theta;
			} else if (x1 < x && y1 > y){
				//quadrant III
				angle = Math.PI + theta;
			} else {
				//quadrant IV
				angle = (2 * Math.PI) - theta;
			}
			return angle;
		}
		
		public double getDegrees() {
			return ((180*getAngle()) / Math.PI);
		}

		public void setDegrees(double degrees) {
			_mouseDegrees = degrees;
		}

		public double getRadians() {
			return (getDegrees() * Math.PI) / 180;
		}

		public void setRadians(double radians) {
			_mouseRadians = radians;
		}

		/*public double degreesObjectToMouse() {
			if (getSelectedObjects().size() == 1) {
				
				double px = _mouseCurrentPosition.x-((AbstractParticle)getSelectedObjects().get(0)).getpx();
				double py = _mouseCurrentPosition.y-((AbstractParticle)getSelectedObjects().get(0)).getpx();
				
				double radians = Math.atan2(py, px);
				_mouseRadians = radians;
				_mouseDegrees = Math.floor(radians*180/Math.PI);
			}
			return _mouseDegrees;
		}*/
		
		public void RectangleParticle() {
			
			int width = (int)(_mouseEndPosition.x-_mouseStartPosition.x);
			int height =(int)(_mouseEndPosition.y-_mouseStartPosition.y);
			int x = (int)_mouseStartPosition.x+(width/2);
			int y = (int)_mouseStartPosition.y+(height/2);

			_APEngine.addParticle(new RectangleParticle(x,y,width,height,0,true,1,0.3,0));
			_gameObject.updatePaintQueue();
		}
		private void setSelectedArea() {
			//if (_SelectedArea == null) {
				int width = (int)(_mouseEndPosition.x-_mouseStartPosition.x);
				int height =(int)(_mouseEndPosition.y-_mouseStartPosition.y);
				int x = (int)_mouseStartPosition.x+(width/2);
				int y = (int)_mouseStartPosition.y+(height/2);

				_designerObjects.put("SelectionArea", new RectangleParticle(x,y,width,height,0,true,1,0.3,0));
		}	
		
		public static void setDefaultContainer(Graphics2D s) {
			_defaultContainer = s;
		}

		public static Graphics2D getDefaultContainer() {
			return _defaultContainer;
		}
		
		public void setSelectedObjectPosition() {
			// set selected objects position
			int lastElement = _selectedObjects.size(); 
			if (lastElement > 0) 
				((AbstractParticle)_selectedObjects.get(lastElement-1)).setPosition(_mouseEndPosition);
		}
		
		public ArrayList getSelectedObjects() {
			return _selectedObjects;
		}
		
		public void setSelectedObjects(AbstractParticle objA) {
			_selectedObjects.add((AbstractParticle)objA);
		}

		public void setStartPosition(double x, double y) {
			_mouseStartPosition.x = x+(int)_gameObject.Scroller.offsetX;
			_mouseStartPosition.y = y+(int)_gameObject.Scroller.offsetY;  
		}
		public void setEndPosition(double x, double y) {
			_mouseEndPosition.x = x+(int)_gameObject.Scroller.offsetX;
			_mouseEndPosition.y = y+(int)_gameObject.Scroller.offsetY;  
		}
		public void setCurrentPosition(double x, double y) {
			_mouseCurrentPosition.x = x+_gameObject.Scroller.offsetX;
			_mouseCurrentPosition.y = y+_gameObject.Scroller.offsetY;  
		}

		public void findObject() {

			// create rectangle that defines selected space
			setSelectedArea();
			
			for (int j = 0; j < _APEngine.getAllParticles().size(); j++) {
				boolean found = findObjectAtPos((AbstractParticle)_APEngine.getAllParticles().get(j));
				if (found) {
					setSelectedObjects((AbstractParticle)_APEngine.getAllParticles().get(j));
				}
			}	
		}
		

		public void adjustDimensionOfSelectedObjects(double adjustWidthBy, double adjustHeightBy, double adjustRotationBy) {
			//increase size by +10 pixels
			System.out.println("increase width by :" + String.valueOf(adjustWidthBy));
			System.out.println("increase height by :" + String.valueOf(adjustHeightBy));
			System.out.println("increase rotation by :" + String.valueOf(adjustRotationBy));

			double width;
			double height;
			for (int i = 0; i < getSelectedObjects().size(); i++) {
				// get current width height
				if (getSelectedObjects().get(i) instanceof RectangleParticle) {  
					RectangleParticle objA = (RectangleParticle)getSelectedObjects().get(i);
					width = ((Double)objA.getExtents().get(0)).doubleValue();
					height = ((Double)objA.getExtents().get(1)).doubleValue();
					// set width height
					objA.getExtents().set(0, Double.valueOf(width+adjustWidthBy));
					objA.getExtents().set(1, Double.valueOf(height+adjustHeightBy));
					if (adjustRotationBy > 0)
						objA.setRotation(objA.getRotation()+adjustRotationBy);

						
				}	
			}
		}

		public boolean findObjectAtPos(AbstractParticle objA) {
			
			if (objA instanceof RectangleParticle) {
				return testOBB((RectangleParticle)objA, (RectangleParticle)_designerObjects.get("SelectionArea") );
			
			} else if (objA instanceof CircleParticle) {
				return testOBBvsCircle( (RectangleParticle)_designerObjects.get("SelectionArea") , (CircleParticle)objA);
			}
			return false;
		}
	
		public void drawDragRectangle() {
			// store original colour
			Color originalColour = _defaultContainer.getColor();
			
			// set draw colour to blue
			_defaultContainer.setColor(Color.blue);
			
			int x = (int)_mouseStartPosition.x;
			int y = (int)_mouseStartPosition.y;
			int width = (int)(_mouseEndPosition.x-_mouseStartPosition.x);
			int height =(int)(_mouseEndPosition.y-_mouseStartPosition.y);
			
			// draw rectangle
			_defaultContainer.drawRect(x, y, width, height);
			 
			// set back to original colour
			_defaultContainer.setColor(originalColour);
		}
		
		
		
		private boolean testOBBvsCircle(RectangleParticle ra, CircleParticle ca) {
			
			Vector collisionNormal = new Vector(0,0);
			double collisionDepth = Double.POSITIVE_INFINITY;
			ArrayList depths = new ArrayList(2);
			
			// first go through the axes of the rectangle
			for (int i = 0; i < 2; i++) {
	
				Vector boxAxis = (Vector)ra.getAxes().get(i);
				double depth = testIntervals(ra.getProjection(boxAxis), ca.getProjection(boxAxis));
				if (depth == 0) return false;
	
				if (Math.abs(depth) < Math.abs(collisionDepth)) {
					collisionNormal = boxAxis;
					collisionDepth = depth;
				}
				depths.add(i,Double.valueOf(depth));
			}	
			
			// determine if the circle's center is in a vertex region
			double r = ca.getRadius();
			if (Math.abs(((Double)depths.get(0)).doubleValue()) < r && Math.abs(((Double)depths.get(1)).doubleValue()) < r) {
	
				Vector vertex = closestVertexOnOBB(ca.curr, ra);
	
				// get the distance from the closest vertex on rect to circle center
				collisionNormal = vertex.minus(ca.curr);
				double mag = collisionNormal.magnitude();
				collisionDepth = r - mag;
	
				if (collisionDepth > 0) {
					// there is a collision in one of the vertex regions
					collisionNormal.divEquals(mag);
					return true;
				} else {
					// ra is in vertex region, but is not colliding
					return false;
				}
			}
			return false;
		}

		private boolean testOBB(RectangleParticle ra, RectangleParticle rb) {
			Vector collisionNormal = new Vector(0,0);
			double collisionDepth = Double.POSITIVE_INFINITY;

			for (int i = 0; i < 2; i++) {
		
				Vector axisA = (Vector)ra.getAxes().get(i);
				double depthA = testIntervals(ra.getProjection(axisA), rb.getProjection(axisA));
			    if (depthA == 0) return false;
				
			    Vector axisB =  (Vector)rb.getAxes().get(i);
			    double depthB = testIntervals(ra.getProjection(axisB), rb.getProjection(axisB));
			    if (depthB == 0) return false;
			    
			    double absA = Math.abs(depthA);
			    double absB = Math.abs(depthB);
			    
			    if (absA < Math.abs(collisionDepth) || absB < Math.abs(collisionDepth)) {
			    	boolean altb = absA < absB;
			    	collisionNormal = altb ? axisA : axisB;
			    	collisionDepth = altb ? depthA : depthB;
			    
			    	return true;
			    }
			}
			return false;
		}		
	
		private double testIntervals(Interval intervalA, Interval intervalB) {
			
			if (intervalA.max < intervalB.min) return 0;
			if (intervalB.max < intervalA.min) return 0;
			
			double lenA = intervalB.max - intervalA.min;
			double lenB = intervalB.min - intervalA.max;
			
			return (Math.abs(lenA) < Math.abs(lenB)) ? lenA : lenB;
		}
		
		/**
		 * Returns the location of the closest vertex on r to point p
		 */
		private static Vector closestVertexOnOBB(Vector p, RectangleParticle r) {
	
			Vector d = p.minus(r.curr);
			Vector q = new Vector(r.curr.x, r.curr.y);
	
			for (int i = 0; i < 2; i++) {
				double dist = d.dot((Vector)r.getAxes().get(i));
	
				if (dist >= 0) dist = ((Double)r.getExtents().get(i)).doubleValue();
				else if (dist < 0) dist = -((Double)r.getExtents().get(i)).doubleValue();
	
				q.plusEquals(((Vector)r.getAxes().get(i)).mult(dist));
			}
			return q;
		}
		
		public void paint(double offsetX, double offsetY) {

			// DEBUG info
			_defaultContainer.setFont( new Font("Arial",Font.BOLD,12));
			_defaultContainer.drawString("start :" + String.valueOf(_mouseStartPosition.x) + " x, " + String.valueOf(_mouseStartPosition.y) + " y",30,30);
			_defaultContainer.drawString("end :" + String.valueOf(_mouseEndPosition.x) + " x, " + String.valueOf(_mouseEndPosition.y) + " y",30,50);
			_defaultContainer.drawString(String.valueOf(getSelectedObjects().size())+" items selected",30,10);
			_defaultContainer.drawString("mode :" + String.valueOf(_mode),30,70);
			_defaultContainer.drawString("tool :" + String.valueOf(_tool),30,90);
			_defaultContainer.drawString("degrees :" + String.valueOf(_mouseDegrees),30,110);

			// paint selected rectangle
			//if (_mode == "drag") {
				drawDragRectangle();
			//}
			
			// paint pysics engine objects selected
			Color originalColour = _defaultContainer.getColor();
			_defaultContainer.setColor(Color.red);
			
			
			for (int i = 0; i < _selectedObjects.size(); i++) {
				if (_selectedObjects.get(i) instanceof RectangleParticle) {
					((RectangleParticle)_selectedObjects.get(i)).paint(0,0);
				}	
			}
			
			// paint objects used to design levels  
			/*System.out.println ( "The elements of HashMap are" ) ;
		    Set set = _designerObjects.keySet (  ) ; 
		    Iterator iter = set.iterator (  ) ; 
		    int i=1; 
		    while ( iter.hasNext (  )  )  {  
		    	Object _ref = iter.next();
		    	System.out.println ( " "+i+" )  "+ _designerObjects.get ( _ref  )  ) ; 
		    	if (_designerObjects.get( _ref ) instanceof RectangleParticle) 
					((RectangleParticle)_designerObjects.get( _ref )).paint(offsetX,offsetY);
				else if (_designerObjects.get( _ref ) instanceof CircleParticle) 
					((CircleParticle)_designerObjects.get( _ref )).paint(offsetX,offsetY);
		    	
				i++; 
		    }*/ 

			// paint selectedArea object
			//if ((RectangleParticle)_designerObjects.get("SelectionArea") != null)
			//	((RectangleParticle)_designerObjects.get("SelectionArea")).paint(offsetX,offsetY);
			
			if (getSelectedObjects().size() == 1) {

				if (getSelectedObjects().get(0) instanceof RectangleParticle) { 
					
					double x = ((RectangleParticle)getSelectedObjects().get(0)).getpx();
					double y = ((RectangleParticle)getSelectedObjects().get(0)).getpy();
					double width = ((Double)((RectangleParticle)getSelectedObjects().get(0)).getExtents().get(0)).doubleValue(); 
					double height = ((Double)((RectangleParticle)getSelectedObjects().get(0)).getExtents().get(1)).doubleValue(); 
					double buffer = width*.8;
					double r = Math.min(width+buffer, height+buffer);

					//draw the circle
					_defaultContainer.setColor(Color.black);
					Ellipse2D.Double circle = new Ellipse2D.Double(offsetX+(x-r), offsetY+(y-r), (double)r*2, (double)r*2);
					_defaultContainer.draw(circle);
	
				    // draw the line joining the circle
					double theta = _mouseRadians;
				    double x1 = x + (int)(r*Math.sin(theta));
				    double y1 = y - (int)(r*Math.cos(theta));
					Line2D line3 = new Line2D.Double(offsetX+x,offsetY+y,offsetX+x1,offsetY+y1);				
					_defaultContainer.draw(line3);
					
					// draw rotate handler
					//Ellipse2D.Double rotateHandler = new Ellipse2D.Double(offsetX+x1, offsetY+y1, 3, 3);
					//rotateHandler.addMouseListener(this);
					//_defaultContainer.draw(rotateHandler);
					
					
					if (_designerObjects.containsKey("rotateHandler") == false) {						
						_designerObjects.put("rotateHandler", new CircleParticle(offsetX+x1,offsetY+y1,3.5,false,1.8,0.1,0.0) );
					} else {
						((CircleParticle)_designerObjects.get("rotateHandler")).setpx(offsetX+x1);
						((CircleParticle)_designerObjects.get("rotateHandler")).setpy(offsetY+y1);
					}	
					
					((CircleParticle)_designerObjects.get("rotateHandler")).paint(offsetX, offsetY);
					
					Line2D mouseLine = new Line2D.Double(offsetX+x,offsetY+y,offsetX+_mouseCurrentPosition.x,offsetY+_mouseCurrentPosition.y);				
					_defaultContainer.draw(mouseLine);
				}
				
			}

			
			// set back to original colour
			_defaultContainer.setColor(originalColour);
				
		}
	}
  