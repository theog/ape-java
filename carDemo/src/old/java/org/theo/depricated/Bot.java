package org.theo.depricated;
   
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import org.cove.ape.APEngine;
import org.cove.ape.CircleParticle;
import org.cove.ape.SpringConstraint;
import org.cove.ape.Vector;
import org.cove.ape.WheelParticle;
import org.theo.aftokinito.carDemo;
    
   public class Bot {
	   public WheelParticle wheelParticleMiddleLegTop;
	   //public WheelParticle wheelParticleLeftLegTop;
	   public WheelParticle wheelParticleRightLegTop;

	   // centre of vehicle, used for screen scroller and initialization
	   public double _x = 0;
	   public double _y = 0;
	   
	   public carDemo _gameObject;
	   
	   private static Graphics2D _defaultContainer;
	   
	   public Bot(double x, double y) {
			
		   boolean _fixed = false;
		   //TG TODO use getters and setters
		   _x = x;
		   _y = y;
		   
		   	//middle leg
		   wheelParticleMiddleLegTop = new WheelParticle(_x,_y,10,_fixed,1,0.3,0,1);
		   wheelParticleMiddleLegTop.setMass(1);
			APEngine.addParticle(wheelParticleMiddleLegTop);
			
		   	// left leg
			//wheelParticleLeftLegTop = new WheelParticle(_x-70,_y,10,_fixed,1,0.3,0,1);
			//wheelParticleLeftLegTop.setMass(1);
			//APEngine.addParticle(wheelParticleLeftLegTop);

			// attach two wheels together with a constrant
			//SpringConstraint scMidToRightLeg = new SpringConstraint(wheelParticleMiddleLegTop, wheelParticleLeftLegTop,0.5);
			//scMidToRightLeg.setCollidable(true);
			//scMidToRightLeg.setCollisionRectWidth((double)1);
			//scMidToRightLeg.setCollisionRectScale((double)0.9);
			//APEngine.addConstraint(scMidToRightLeg);

			// right leg
			wheelParticleRightLegTop = new WheelParticle(_x+30,_y,10,false,1,0.3,0,1);
			wheelParticleRightLegTop.setMass(1);
			APEngine.addParticle(wheelParticleRightLegTop);
				
			// attach two wheels together with a constrant
			SpringConstraint scMidToLeftLeg = new SpringConstraint(wheelParticleMiddleLegTop, wheelParticleRightLegTop,0.5);
			scMidToLeftLeg.setCollidable(false);
			scMidToLeftLeg.setCollisionRectWidth((double)1);
			scMidToLeftLeg.setCollisionRectScale((double)0.9);
			APEngine.addConstraint(scMidToLeftLeg);
			
			
		   CircleParticle BodyLeft1 = new CircleParticle(_x-10, _y-10,3,_fixed,1,0.3,0);
			
			// left wheel to left1 
			//SpringConstraint scLeftLegLeft1 = new SpringConstraint(BodyLeft1, wheelParticleLeftLegTop,0.5);
			//scLeftLegLeft1.setCollidable(true);
			//scLeftLegLeft1.setCollisionRectWidth((double)1);
			//scLeftLegLeft1.setCollisionRectScale((double)0.9);
			//APEngine.addConstraint(scLeftLegLeft1);

		   CircleParticle BodyLeft2 = new CircleParticle(_x, _y-10,3,_fixed,1,0.3,0);
				
			// left wheel to left1 
			SpringConstraint scLeft1Left2 = new SpringConstraint(BodyLeft1, BodyLeft2,0.5);
			scLeft1Left2.setCollidable(true);
			scLeft1Left2.setCollisionRectWidth((double)1);
			scLeft1Left2.setCollisionRectScale((double)0.9);
			APEngine.addConstraint(scLeft1Left2);
	   
			// left wheel to left1 
			SpringConstraint muscle1 = new SpringConstraint( ((CircleParticle)wheelParticleMiddleLegTop.getEdgeParticles().get(1)) , ((CircleParticle)wheelParticleRightLegTop),0.5);
			muscle1.setCollidable(true);
			muscle1.setCollisionRectWidth((double)1);
			muscle1.setCollisionRectScale((double)0.9);
			APEngine.addConstraint(muscle1);

			SpringConstraint muscle2 = new SpringConstraint( ((CircleParticle)wheelParticleMiddleLegTop.getEdgeParticles().get(3)) , ((CircleParticle)wheelParticleRightLegTop),0.5);
			muscle2.setCollidable(true);
			muscle2.setCollisionRectWidth((double)1);
			muscle2.setCollisionRectScale((double)0.9);
			APEngine.addConstraint(muscle2);
			
			wheelParticleMiddleLegTop.setAngularVelocity(.001);

			
	   }

	   // function is used to scroll on object, lets use wheel particleA.
	   public Vector getVelocity() {
			return wheelParticleMiddleLegTop.getVelocity();
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
		}	
		
		public void Step() {
			// centre of the car is defined as the centre point between the two wheels;
		    _x = wheelParticleMiddleLegTop.getpx();
			_y =  wheelParticleMiddleLegTop.getpy();
			wheelParticleMiddleLegTop.setAngularVelocity(0);
			
	   }

	   public void keyPressed(KeyEvent e) {
		}
		
		public void keyReleased(KeyEvent e) {
		}
		
		public void mouseClicked(MouseEvent e) {
		}
		
		public void paint(double offsetX, double offsetY) {
		}
      
   }