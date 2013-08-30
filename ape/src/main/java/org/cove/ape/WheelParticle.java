/*
APE (Actionscript Physics Engine) is an AS3 open source 2D physics engine
Copyright 2006, Alec Cove 

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

Contact: ape@cove.org

Converted to Java by Theo Galanakis theo.galanakis@hotmail.com

*/
package org.cove.ape;
import java.util.ArrayList;
import java.awt.geom.*;
	
	/**
	 * A particle that simulates the behavior of a wheel 
	 */ 
	public class WheelParticle extends CircleParticle  {
	
		private RimParticle rp;
		private Vector tan;	
		private Vector normSlip;
		
		private ArrayList _edgePositions = new ArrayList();
		private ArrayList _edgeParticles = new ArrayList();
		private double _traction;
		
	
		/**
		 * @param x The initial x position.
		 * @param y The initial y position.
		 * @param radius The radius of this particle.
		 * @param angle The rotation of this particle in radians.
		 * @param fixed Determines if the particle is fixed or not. Fixed particles
		 * are not affected by forces or collisions and are good to use as surfaces.
		 * Non-fixed particles move freely in response to collision and forces.
		 * @param mass The mass of the particle
		 * @param elasticity The elasticity of the particle. Higher values mean more elasticity.
		 * @param friction The surface friction of the particle. 
		 * <p>
		 * Note that WheelParticles can be fixed but still have their rotation property 
		 * changed.
		 * </p>
		 */
		public WheelParticle(
		double x, 
		double y, 
		double radius, 
		boolean fixed, 
		double mass, 
		double elasticity,
		double friction,
		double traction) {
	
			super(x,y,radius,fixed, mass, elasticity, friction);
			tan = new Vector(0,0);
			normSlip = new Vector(0,0);
			rp = new RimParticle(radius, 2); 	
			
			setTraction(traction);
			
			//TG TODO I have added these lines as in actionscript3 these are called when initialized
			_edgePositions = getEdgePositions();
			_edgeParticles = getEdgeParticles();
			
		}	
	
		
		/**
		 * The angular velocity of the WheelParticle. You can alter this value to make the 
		 * WheelParticle spin.
		 */
		public double getAngularVelocity() {
			return rp.getAngularVelocity();
		}
		
		
		/**
		 * @private
		 */		
		public void setAngularVelocity(double a) {
			rp.setAngularVelocity(a);
		}
	

		/**
		 * The amount of traction during a collision. This property controls how much traction is 
		 * applied when the WheelParticle is in contact with another particle. If the value is set
		 * to 0, there will be no traction and the WheelParticle will behave as if the 
		 * surface was totally slippery, like ice. Acceptable values are between 0 and 1. 
		 * 
		 * <p>
		 * Note that the friction property behaves differently than traction. If the surface 
		 * friction is set high during a collision, the WheelParticle will move slowly as if
		 * the surface was covered in glue.
		 * </p>
		 */		
		public double getTraction() {
			return 1-_traction;
		}
	
	
		/**
		 * @private
		 */				
		public void setTraction(double t) {
			_traction = 1 - t;
		}
		
		
		/**
		 * An Array of 4 contact particles on the rim of the wheel.  The edge particles
		 * are positioned relatively at 12, 3, 6, and 9 o'clock positions. You can attach other
		 * particles or constraints to these particles. Note this is a one-way effect, meaning the
		 * WheelParticle's motion will move objects attached to the edge particles, but the reverse
		 * is not true. 
		 * 
		 * <p>
		 * In order to access one of the 4 edge particles, you can use array notation 
		 * e.g., <code>myWheelParticle.edgeParticles[0]</code>
		 * </p>
		 */			
		public ArrayList getEdgeParticles() {
			
			if (_edgePositions.size() == 0) getEdgePositions();
			
			if (_edgeParticles.size() == 0) {
				CircleParticle cp1 = new CircleParticle(0,0,1,false,1,0.3,0);
				cp1.setCollidable(false);
				cp1.setVisible(false);
				APEngine.addParticle(cp1);
				
				CircleParticle cp2 = new CircleParticle(0,0,1,false,1,0.3,0);
				cp2.setCollidable(false);
				cp2.setVisible(false);
				APEngine.addParticle(cp2);
	
				CircleParticle cp3 = new CircleParticle(0,0,1,false,1,0.3,0);
				cp3.setCollidable(false);
				cp3.setVisible(false);
				APEngine.addParticle(cp3);
	
				CircleParticle cp4 = new CircleParticle(0,0,1,false,1,0.3,0);
				cp4.setCollidable(false);
				cp4.setVisible(false);
				APEngine.addParticle(cp4);
			
				_edgeParticles.add(cp1);
				_edgeParticles.add(cp2);
				_edgeParticles.add(cp3);
				_edgeParticles.add(cp4);
				
				updateEdgeParticles();
			}
			return _edgeParticles;
		}
	
	
		/**
		 * An Array of 4 <code>Vector</code> objects storing the location of the 4
		 * edge positions of this WheelParticle. The edge positions
		 * are located relatively at the 12, 3, 6, and 9 o'clock positions.
		 */
		public ArrayList getEdgePositions() {
					
			if (_edgePositions.size() == 0) {
				_edgePositions.add(new Vector(0,0));
				_edgePositions.add(new Vector(0,0));
				_edgePositions.add(new Vector(0,0));
				_edgePositions.add(new Vector(0,0));
						
				updateEdgePositions();
			}
			return _edgePositions;
		}
		
		
		/**
		 * The default paint method for the particle. Note that you should only use
		 * the default painting methods for quick prototyping. For anything beyond that
		 * you should always write your own classes that either extend one of the
		 * APE particle and constraint classes, or is a composite of them. Then within that 
		 * class you can define your own custom painting method.
		 */	
		public void paint(double offsetX,double offsetY) {
			
			float offsetXf = new Double(offsetX).floatValue();
			float offsetYf = new Double(offsetY).floatValue();
			
			float px = new Double(curr.x).floatValue();
			float py = new Double(curr.y).floatValue();
			float rx = new Double(rp.curr.x).floatValue();
			float ry = new Double(rp.curr.y).floatValue();
			
			if (dc == null) dc = getDefaultContainer();
			if (! getVisible()) return;
			
			GeneralPath f1 = new GeneralPath();
			
			// draw rim cross
			f1.moveTo(offsetXf+px,offsetYf+py);
			f1.lineTo(offsetXf+(rx + px),offsetYf+(ry + py));
			
			f1.moveTo(offsetXf+px, offsetYf+py);
			f1.lineTo(offsetXf+(-rx + px), offsetYf+(-ry + py));
			
			f1.moveTo(offsetXf+px, offsetYf+py);
			f1.lineTo(offsetXf+(-ry + px), offsetYf+(rx + py));
			
			f1.moveTo(offsetXf+px, offsetYf+py);
			f1.lineTo(offsetXf+(ry + px), offsetYf+(-rx + py));
			
			dc.draw(f1);
			
			// draw wheel circle
			Ellipse2D.Double circle = new Ellipse2D.Double(offsetX+(curr.x-getRadius()), offsetY+(curr.y-getRadius()), (double)getRadius()*2, (double)getRadius()*2);
			dc.draw(circle);

		}
	
	
		/**
		 * @private
		 */			
		public void update(double dt) {
			super.update(dt);
			rp.update(dt);
			
			if (_edgePositions != null) updateEdgePositions();
			if (_edgeParticles != null) updateEdgeParticles();
		}
	
	
		/**
		 * @private
		 */		
		public void resolveCollision(
				Vector mtd, 
				Vector velocity, 
				Vector normal,
				double depth,
				double order) {
					
			super.resolveCollision(mtd, velocity, normal, depth, order);
			resolve(normal.mult(sign(depth * order)));
		}
		
	
		/**
		 * simulates torque/wheel-ground interaction - n is the surface normal
		 * Origins of this code thanks to Raigan Burns, Metanet software
		 */
		private void resolve(Vector n) {
	
			tan.setTo(-rp.curr.y, rp.curr.x);
	
			tan = tan.normalize();
	
			// velocity of the wheel's surface 
			Vector wheelSurfaceVelocity = tan.mult(rp.getSpeed());
			
			// the velocity of the wheel's surface relative to the ground
			Vector combinedVelocity = getVelocity().plusEquals(wheelSurfaceVelocity);
		
			// the wheel's comb velocity projected onto the contact normal
			double cp = combinedVelocity.cross(n);
	
			// set the wheel's spinspeed to track the ground
			tan.multEquals(cp);
			rp.prev.copy(rp.curr.minus(tan));
	
			// some of the wheel's torque is removed and converted into linear displacement
			double  slipSpeed = (1 - _traction) * rp.getSpeed();
			normSlip.setTo(slipSpeed * n.y, slipSpeed * n.x);
			curr.plusEquals(normSlip);
			rp.setSpeed(rp.getSpeed() * _traction);
		}
		
		
		/**
		 *
		 */	
		private void updateEdgePositions() {
			
			double px = curr.x;
			double py = curr.y;
			double rx = rp.curr.x;
			double ry = rp.curr.y;
			
			((Vector)_edgePositions.get(0)).setTo( rx + px,  ry + py);
			((Vector)_edgePositions.get(1)).setTo(-ry + px,  rx + py);
			((Vector)_edgePositions.get(2)).setTo(-rx + px, -ry + py);
			((Vector)_edgePositions.get(3)).setTo( ry + px, -rx + py);
		}


		/**
		 *
		 */	
		private void updateEdgeParticles() {
			for (int i = 0; i < 4; i++) {
				((CircleParticle)_edgeParticles.get(i)).setpx( ((Vector)_edgePositions.get(i)).x ); 
				((CircleParticle)_edgeParticles.get(i)).setpy( ((Vector)_edgePositions.get(i)).y ); 
			}	
		}
		
		
		/**
		 * Returns 1 if the value is >= 0. Returns -1 if the value is < 0.
		 */	
		private int sign(double val) {
			if (val < 0) return -1;
			return 1;
		}
	}


