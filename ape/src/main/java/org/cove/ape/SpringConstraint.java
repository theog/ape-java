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

import java.awt.geom.Line2D;
	
	
	/**
	 * A Spring-like constraint that connects two particles
	 */
	public class SpringConstraint extends AbstractConstraint {
		
		private AbstractParticle p1;
		private AbstractParticle p2;
		
		private double restLen;

		private Vector delta;

		private double deltaLength;
		
		private double _collisionRectWidth;
		private double _collisionRectScale;
		private boolean _collidable;
		private SpringConstraintParticle collisionRect;
		
		
		/**
		 * @param p1 The first particle this constraint is connected to.
		 * @param p2 The second particle this constraint is connected to.
		 * @param stiffness The strength of the spring. Valid values are between 0 and 1. Lower values
		 * result in softer springs. Higher values result in stiffer, stronger springs.
		 */
		// TG TODO DEFAULTS
		public SpringConstraint(
				AbstractParticle p1, 
				AbstractParticle p2, 
				double stiffness) {
			
			super(stiffness);
			this.p1 = p1;
			this.p2 = p2;
			checkParticlesLocation();
			
			_collisionRectWidth = 1;
			_collisionRectScale = 1;
			_collidable = false;
			
			delta = p1.curr.minus(p2.curr);
			deltaLength = p1.curr.distance(p2.curr);
			restLen = deltaLength; 
		}
		
		
		/**
		 * The rotational value created by the positions of the two particles attached to this
		 * SpringConstraint. You can use this property to in your own painting methods, along with the 
		 * center property.
		 */			
		public double getRotation() {
			return Math.atan2(delta.y, delta.x);
		}
		
		
		/**
		 * The center position created by the relative positions of the two particles attached to this
		 * SpringConstraint. You can use this property to in your own painting methods, along with the 
		 * rotation property.
		 * 
		 * @returns A Vector representing the center of this SpringConstraint
		 */			
		public Vector getCenter() {
			return (p1.curr.plus(p2.curr)).divEquals(2);
		}
		
		
		/**
		 * If the <code>collidable</code> property is true, you can set the scale of the collidible area
		 * between the two attached particles. Valid values are from 0 to 1. If you set the value to 1, then
		 * the collision area will extend all the way to the two attached particles. Setting the value lower
		 * will result in an collision area that spans a percentage of that distance.
		 */			
		public double getCollisionRectScale() {
			return _collisionRectScale;
		}
		
		
		/**
		 * @private
		 */			
		public void setCollisionRectScale(double scale) {
			_collisionRectScale = scale;
		}		
		

		/**
		 * If the <code>collidable</code> property is true, you can set the width of the collidible area
		 * between the two attached particles. Valid values are greater than 0. If you set the value to 10, then
		 * the collision area will be 10 pixels wide. The width is perpendicular to a line connecting the two 
		 * particles
		 */				
		public double getCollisionRectWidth() {
			return _collisionRectWidth;
		}
		
		
		/**
		 * @private
		 */			
		public void setCollisionRectWidth(double w) {
			_collisionRectWidth = w;
		}
		
		
		/**
		 * The <code>restLength</code> property sets the length of SpringConstraint. This value will be
		 * the distance between the two particles unless their position is altered by external forces. The
		 * SpringConstraint will always try to keep the particles this distance apart.
		 */			
		public double getRestLength() {
			return restLen;
		}
		
		
		/**
		 * @private
		 */	
		public void setRestLength(double r) {
			restLen = r;
		}
	
	
		/**
		 * Determines if the area between the two particles is tested for collision. If this value is on
		 * you can set the <code>collisionRectScale</code> and <code>collisionRectWidth</code> properties 
		 * to alter the dimensions of the collidable area.
		 */			
		public boolean getCollidable() {
			return _collidable;
		}
	
				
		/**
		 * @private
		 */		
		public void setCollidable(boolean b) {
			_collidable = b;
			if (_collidable) {
				collisionRect = new SpringConstraintParticle(p1, p2);
				orientCollisionRectangle();
			} else {
				collisionRect = null;
			}
		}
		
		
		/**
		 * Returns true if the passed particle is one of the particles specified in the constructor.
		 */		
		public boolean isConnectedTo(AbstractParticle p) {
			return (p == p1 || p == p2);
		}
		
		
		/**
		 * The default paint method for the constraint. Note that you should only use
		 * the default painting methods for quick prototyping. For anything beyond that
		 * you should always write your own classes that either extend one of the
		 * APE particle and constraint classes, or is a composite of them. Then within that 
		 * class you can define your own custom painting method.
		 */			
		public void paint(
				// TG TODO added for offset.
				double offsetX, double offsetY) {
			
			if (dc == null) dc = getDefaultContainer();
			
			if (_collidable) {
				//TG TODO added for offset				
				collisionRect.paint(offsetX,offsetY);
			} else {
				if (! getVisible()) return;
				double X1 = p1.curr.x+offsetX;
				double Y1 = p1.curr.y+offsetY;
				double X2 = p2.curr.x+offsetX;
				double Y2 = p2.curr.y+offsetY;
				Line2D line = new Line2D.Double(X1,Y1,X2,Y2);
				dc.draw(line);
			}
		}		
		
		/**
		 * @private
		 */
		public void resolve() {
			
			if (p1.getFixed() & p2.getFixed()) return;
			
			delta = p1.curr.minus(p2.curr);
			deltaLength = p1.curr.distance(p2.curr);
			if (_collidable) orientCollisionRectangle();
			
			double diff = (deltaLength - restLen) / deltaLength;
			Vector dmd = delta.mult(diff * super.getStiffness());
	
			double invM1 = p1.getInvMass();
			double invM2 = p2.getInvMass();
			double sumInvMass = invM1 + invM2;
			
			// REVIEW TO SEE IF A SINGLE FIXED PARTICLE IS RESOLVED CORRECTLY
			if (! p1.getFixed()) p1.curr.minusEquals(dmd.mult((double)invM1/sumInvMass));
			if (! p2.getFixed()) p2.curr.plusEquals( dmd.mult((double)invM2/sumInvMass));			
		}
		
		
		/**
		 * @private
		 */		
		public RectangleParticle getCollisionRect() {
			return collisionRect;
		}
	
	
		/**
		 * @private
		 */	
		private void orientCollisionRectangle() {
	
			Vector c = getCenter();
			double rot = getRotation();			
			
			collisionRect.curr.setTo(c.x, c.y);
			collisionRect.getExtents().set(0, Double.valueOf((deltaLength / 2) * _collisionRectScale) );
			collisionRect.getExtents().set(1, Double.valueOf((_collisionRectWidth / 2)) );
			collisionRect.setRotation(rot);
		}
	
	
		/**
		 * if the two particles are at the same location warn the user
		 */
		private void checkParticlesLocation() {
			if (p1.curr.x == p2.curr.x && p1.curr.y == p2.curr.y) {
				throw new Error("The two particles specified for a SpringContraint can't be at the same location");
			}
		}
	}
