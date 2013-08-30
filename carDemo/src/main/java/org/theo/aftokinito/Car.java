package org.theo.aftokinito;
   
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

import org.cove.ape.APEngine;
import org.cove.ape.CircleParticle;
import org.cove.ape.RectangleParticle;
import org.cove.ape.SpringConstraint;
import org.cove.ape.Vector;
import org.cove.ape.WheelParticle;

    
   public class Car {
	   public WheelParticle wheelParticleA;
	   public WheelParticle wheelParticleB;
	   public CircleParticle topOfCar;
	   public RectangleParticle blockWeight;
	   
	   public ArrayList<Lasso> lassoArray = new ArrayList<Lasso>();
	   public int maxLasso = 5;
	   public int lassoPointer = 0;
	   public int lastlassoPointer = 0;
	   
	   // centre of vehicle, used for screen scroller and initialization
	   public double _x = 0;
	   public double _y = 0;
	   
	   public carDemo _gameObject;
	   
	   private Vector _mouseCurrentPosition = new Vector(0,0);
	   private Vector lassoVel = new Vector(0,0);	   

	   private Vector flyingVel = new Vector(0,0);	   
	   private Vector newVel = new Vector(0,0);
	   private double maxVelocity = 3;
	   private Vector boundX = new Vector(0,0);
	   private Vector boundY = new Vector(0,0);
	   
	   //private List<Vector> frozenState = new ArrayList<Vector>();;
	   
	   private static Graphics2D _defaultContainer;
	   
	   public Car(double x, double y) {
			
		   //TG TODO use getters and setters
		   _x = x;
		   _y = y;
		   
		   	//left wheel
		   	wheelParticleA = new WheelParticle(_x-30,_y,20,false,1,0.3,0,1);
			wheelParticleA.setMass(1);
			APEngine.addParticle(wheelParticleA);
			
		   	//right wheel
			wheelParticleB = new WheelParticle(_x+30,_y,20,false,1,0.3,0,1);
			wheelParticleB.setMass(1);
			APEngine.addParticle(wheelParticleB);
			
			// attach two wheels together with a constrant
			SpringConstraint wheelConnector = new SpringConstraint(wheelParticleA, wheelParticleB,0.5);
			wheelConnector.setCollidable(true);
			wheelConnector.setCollisionRectWidth((double)10);
			wheelConnector.setCollisionRectScale((double)0.9);
			APEngine.addConstraint(wheelConnector);

			// top node of car used to fire rope/lasso projectiles
			topOfCar = new CircleParticle(_x, _y-40,3,false,1,0.3,0);
			APEngine.addParticle(topOfCar);
			
			// attach wheel1 to top of car
			SpringConstraint spring1 = new SpringConstraint(topOfCar, wheelParticleA,0.5);
			APEngine.addConstraint(spring1);

			// attach wheel3 to top of car
			SpringConstraint spring2 = new SpringConstraint(topOfCar, wheelParticleB,0.5);
			APEngine.addConstraint(spring2);
			
			// centre weight attached to top node of car
			blockWeight = new RectangleParticle(_x,_y-25,5,5,0,false,1,0.3,0);
			blockWeight.setMass(0.2);
			APEngine.addParticle(blockWeight);

			// attach centre weight to top node of car
			SpringConstraint spring3 = new SpringConstraint(topOfCar, blockWeight,0.5);
			APEngine.addConstraint(spring3);
		   
			// create an array of lassoPointer pointers 
			for (int i=0; i <= maxLasso;i++) {
				lassoArray.add(new Lasso());
				((Lasso)lassoArray.get(lassoArray.size()-1)).setFixedObject(topOfCar);
			}
			
	   }

	   // function is used to scroll on object, lets use wheel particleA.
	   public Vector getVelocity() {
			return wheelParticleA.getVelocity();
		}
	   
		public double getpx() {	
			return _x;
		}

		public double getpy() {	
			return _y;
		}


	   public static void setDefaultContainer(Graphics2D s) {
			_defaultContainer = s;
		}

		public static Graphics2D getDefaultContainer() {
			return _defaultContainer;
		}
		
	   public void setGameObject(carDemo gameObject) {
			_gameObject = gameObject;
			for (int i=0; i < lassoArray.size();i++) {
				((Lasso)lassoArray.get(i)).setGameObject(_gameObject);
			}	
		}	
	   
		public void setCurrentMousePosition(double x, double y) {
			_mouseCurrentPosition.x = x;
			_mouseCurrentPosition.y = y;  
		}
		
		public void determineVelocity() {
			
			//boundX.x = 300 - (topOfCar.getVelocity().x * 20);
			//boundX.y = 400 - (topOfCar.getVelocity().x * 20);
			boundX.x = _gameObject.Scroller.offsetX+100;
			boundX.y = boundX.x+300;
			
			boundY.x = _gameObject.Scroller.offsetY-60;
			boundY.y = boundY.x+300;
			
			// if the focused object is within the bounded box area, apply pos,neg velocity :
		   // if focus.x-(x-y)/2 e.g. (300-400)/2 = 50 
		   // centre point is 350 current possition 301,so its 1 for 50. 
		   // 1/50 = 1/50*100 = 2% = so begin to reduce the velocity by 2 %
		   double PosX = _gameObject.Scroller.offsetX + topOfCar.getpx();
		   double PosY = _gameObject.Scroller.offsetY + topOfCar.getpy();

		   if (PosY > boundY.x && PosY < boundY.y) {
			   //if it has found the central position
			   if (PosY < boundY.y+(boundY.y-boundY.x)/2 ) {
				   flyingVel.y = - (maxVelocity * Math.abs((PosY-boundY.x) / ((boundY.y-boundY.x)/2)-1));
			   } else {
				   flyingVel.y = (maxVelocity * Math.abs((PosY-boundY.x) / ((boundY.y-boundY.x)/2)-1));
			   }
		   } else {
			   flyingVel.y = 0;
		   }
		   /*if (PosX > boundX.x && PosX < boundX.y) {
			   //if it has found the central position
			   if ( PosX < boundX.y+(boundX.y-boundX.x)/2 ) {
				   flyingVel.x = (maxVelocity * Math.abs((PosX-boundX.x) / ((boundX.y-boundX.x)/2)-1));
			   } else {
				   flyingVel.x = - (maxVelocity * Math.abs((PosX-boundX.x) / ((boundX.y-boundX.x)/2)-1));
			   }
		   } else {
			   flyingVel.x = 0;
		   }*/
		}
		
		public void Step() {
			// centre of the car is defined as the centre point between the two wheels;
		    _x = (wheelParticleA.getpx() + wheelParticleB.getpx()) / 2;
			_y =  (wheelParticleA.getpy() + wheelParticleB.getpy()) / 2;
			
		   	for (int i=0; i < lassoArray.size(); i++) {
				((Lasso)lassoArray.get(i)).step();
			}
		   	
			// maintan flying distance by compensating velocity
		   	/*if (topOfCar.getpy() > 0) {
				if (flyingVel.y <= 1) {
					flyingVel.y = flyingVel.y - .01;
				}	
				System.out.println("gt x," + topOfCar.getpy() + "vel x" + topOfCar.getVelocity().x + "vel y" + topOfCar.getVelocity().y);
		   	} else if (topOfCar.getpy() < -100) {
				if (flyingVel.y >= 1) {
					flyingVel.y = flyingVel.y + .01;
				}	
				System.out.println("lt x," + topOfCar.getpy() + "vel x" + topOfCar.getVelocity().x + "vel y" + topOfCar.getVelocity().y);
		   	} else {
				flyingVel.y = -.8;
				System.out.println("middle," + topOfCar.getpy() + "vel x" + topOfCar.getVelocity().x + "vel y" + topOfCar.getVelocity().y);
		   	}*/
		   	//determineVelocity();
			//System.out.println("vel x" + flyingVel.x + "vel y" + flyingVel.y);
			//System.out.println("car px:" + topOfCar.getpy() + "vel x" + topOfCar.getVelocity().x + "vel y" + topOfCar.getVelocity().y);
		   	
		   	//newVel = topOfCar.getVelocity().plus(flyingVel);
			//topOfCar.setVelocity(newVel);

	   }

	   public void keyPressed(KeyEvent e) {
			double keySpeed = 0.3;
			
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				wheelParticleA.setAngularVelocity(-keySpeed);
				wheelParticleB.setAngularVelocity(-keySpeed);

				//Vector vel = new Vector(-2,0);
				//topOfCar.setVelocity(vel);
				
			} 
			
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				wheelParticleA.setAngularVelocity(keySpeed);
				wheelParticleB.setAngularVelocity(keySpeed);
			} 
			
			if (e.getKeyCode() == KeyEvent.VK_UP) {
				//_defaultContainer.drawString("y" + topOfCar.getVelocity().y ,100,100);
					
					if (flyingVel.y <= 1) {
						flyingVel.y = flyingVel.y - .05;
					}	
			} 
			
			if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					
					//if (flyingVel.y >= 1) {
						flyingVel.y = flyingVel.y + .05;
					//}	
			} 
			
		   
			// 
			System.out.println(e.getKeyCode());
			
			// freeze frame
		   if (e.getKeyCode() == KeyEvent.VK_F) {
			   this.getFreezeState();
		   }
		   
		   	//			 freeze frame
		   if (e.getKeyCode() == KeyEvent.VK_U) {
			   this.setFreezeState();
		   }
			
		}
		
		public void keyReleased(KeyEvent e) {
			wheelParticleA.setAngularVelocity(0);
			wheelParticleB.setAngularVelocity(0);
			
			flyingVel.y = 0;
			flyingVel.x = 0;
			//flyingVel(0,0);
			//topOfCar.setVelocity(flyingVel);
		}
		
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == 1) {
				fire();
			} else if (e.getButton() == 3)  {
				// right button
				release();
			}	
		}
		
		private double getAngle() {
			
			// get current position of where projector will come from
			int x = (int)(topOfCar.getpx()+_gameObject.Scroller.offsetX);
			int y = (int)(topOfCar.getpy()+_gameObject.Scroller.offsetY);
			
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
				_defaultContainer.drawString("q1",90,150);
				angle = theta;
			} else if (x1 < x && y1 <= y){
				//quadrant II
				_defaultContainer.drawString("q2",90,150);
				angle = Math.PI - theta;
			} else if (x1 < x && y1 > y){
				//quadrant III
				_defaultContainer.drawString("q3",90,150);
				angle = Math.PI + theta;
			} else {
				//quadrant IV
				_defaultContainer.drawString("q4",90,150);
				angle = (2 * Math.PI) - theta;
			}
			return angle;
		}
		
		private double getDegrees() {
			return ((180*getAngle()) / Math.PI);
		}

		private double getRadians() {
			return (getDegrees() * Math.PI) / 180;
		}
		
		private Vector getLassoVelocity() {
			double radians = getRadians();
			lassoVel.y = - 50*Math.sin(radians);
			lassoVel.x = 50*Math.cos(radians);
			_defaultContainer.drawString("x" + String.valueOf(lassoVel.x) + "y" + String.valueOf(lassoVel.y) ,90,200);
			return lassoVel;
		}

		public void fire() {
			getLassoVelocity();
			// fire the most recent item
			((Lasso)lassoArray.get(lassoPointer)).fire(lassoVel);
			
			if (lassoPointer == maxLasso) {
				lassoPointer =  0;
			} else {
				lassoPointer = lassoPointer + 1;	
			}

			if (maxLasso == lassoArray.size()) {
				if (lastlassoPointer == maxLasso) {
					lastlassoPointer =  0;	
				} else {
					lastlassoPointer = lastlassoPointer + 1;	
				}
			}
		}
		
		public void release() {
			//delete the last lasso pointer
			((Lasso)lassoArray.get(lastlassoPointer)).delete();

			if (lastlassoPointer == maxLasso) {
				lastlassoPointer =  0;	
			} else {
				lastlassoPointer = lastlassoPointer + 1;	
			}
		
		}				

		public void setFreezeState() {
			/*wheelParticleA.curr = frozenState.get(0);
			wheelParticleB.curr = frozenState.get(1);
			topOfCar.curr = frozenState.get(2);
			blockWeight.curr = frozenState.get(3);
			
			wheelParticleA.prev = frozenState.get(4);
			wheelParticleB.prev = frozenState.get(5);
			topOfCar.prev = frozenState.get(6);
			blockWeight.prev = frozenState.get(7);*/
		}
		
		// TG TODO A method that can print all the important properties of the car, to be able to use to set the position of the vehicle with.
		public void getFreezeState() {
			/*frozenState.add(wheelParticleA.curr);
			frozenState.add(wheelParticleB.curr);
			frozenState.add(topOfCar.curr);
			frozenState.add(blockWeight.curr);
			frozenState.add(wheelParticleA.prev);
			frozenState.add(wheelParticleB.prev);
			frozenState.add(topOfCar.prev);
			frozenState.add(blockWeight.prev);*/
			
			System.out.print("myCar.wheelParticleA.curr.x = " + wheelParticleA.curr.x + ";");	
			System.out.print("myCar.wheelParticleA.curr.y = " + wheelParticleA.curr.y + ";");	
			System.out.print("myCar.wheelParticleA.prev.x = " + wheelParticleA.prev.x + ";");	
			System.out.print("myCar.wheelParticleA.prev.y = " + wheelParticleA.prev.y + ";");	

			System.out.print("myCar.wheelParticleB.curr.x = " + wheelParticleB.curr.x + ";");	
			System.out.print("myCar.wheelParticleB.curr.y = " + wheelParticleB.curr.y + ";");	
			System.out.print("myCar.wheelParticleB.prev.x = " + wheelParticleB.prev.x + ";");	
			System.out.print("myCar.wheelParticleB.prev.y = " + wheelParticleB.prev.y + ";");
			
			System.out.print("myCar.topOfCar.curr.x = " + topOfCar.curr.x + ";");	
			System.out.print("myCar.topOfCar.curr.y = " + topOfCar.curr.y + ";");	
			System.out.print("myCar.topOfCar.prev.x = " + topOfCar.prev.x + ";");	
			System.out.print("myCar.topOfCar.prev.y = " + topOfCar.prev.y + ";");

			System.out.print("myCar.blockWeight.curr.x = " + blockWeight.curr.x + ";");	
			System.out.print("myCar.blockWeight.curr.y = " + blockWeight.curr.y + ";");	
			System.out.print("myCar.blockWeight.prev.x = " + blockWeight.prev.x + ";");	
			System.out.print("myCar.blockWeight.prev.y = " + blockWeight.prev.y + ";");
			
		}

		public void paint(double offsetX, double offsetY) {
			
			// get current position of where projector will come from
			int x = (int)topOfCar.getpx();
			int y = (int)topOfCar.getpy();
			
			// get mouse position
			int x1 = (int)_mouseCurrentPosition.x;
			int y1 = (int)_mouseCurrentPosition.y;
			
			_defaultContainer.drawString("degrees :" + String.valueOf(getDegrees()),30,110);

			_defaultContainer.drawString("now :" + String.valueOf(lassoPointer) + "last :" + String.valueOf(lastlassoPointer),150,300);
			
			Line2D line3 = new Line2D.Double(x+offsetX,y+offsetY,x1,y1);				
		    _defaultContainer.draw(line3);
		    
		    
		    _defaultContainer.drawString("car px:" + String.valueOf(topOfCar.getpx()),200,200);
		    _defaultContainer.drawString("vel x" + String.valueOf(topOfCar.getVelocity().x),200,220);
		    _defaultContainer.drawString("vel y" + String.valueOf(topOfCar.getVelocity().y),200,240);
		    
		    _defaultContainer.drawString("car py:" + String.valueOf(topOfCar.getpy()),0,200);
		    _defaultContainer.drawString("vel x" + String.valueOf(flyingVel.x),0,220);
		    _defaultContainer.drawString("vel y" + String.valueOf(flyingVel.y),0,240);
		    
			// DEBUG INFORMATION 
		    _defaultContainer.drawRect((int)boundX.x,(int)boundY.x,(int)(boundX.y-boundX.x),(int)(boundY.y-boundY.x));

		    //_defaultContainer.drawLine((int)boundX.x,(int)boundY.x,(int)(boundX.y-boundX.x),(int)(boundY.y-boundY.x));
		    
		    //boundX.y+(boundX.y-boundX.x)/2
			
		}
      
   }