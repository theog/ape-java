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
	
	public class SpringConstraintParticle extends RectangleParticle {
		
		private AbstractParticle p1;
		private AbstractParticle p2;
		private Vector avgVelocity;
		
		public SpringConstraintParticle(AbstractParticle p1, AbstractParticle p2) {
			//TG TODO check the defaults to super class
			super(0,0,0,0,0,false,1,0.3,0);
			this.p1 = p1;
			this.p2 = p2;
			avgVelocity = new Vector(0,0);
		}
		
		
		/**
		 * returns the average mass of the two connected particles
		 */
		public double getMass() {
			return (p1.getMass() + p2.getMass()) / 2;
		}
		
		
		/**
		 * returns the average velocity of the two connected particles
		 */
		public Vector getVelocity() {
			Vector p1v =  p1.getVelocity();
			Vector p2v =  p2.getVelocity();
			
			avgVelocity.setTo(((p1v.x + p2v.x) / 2), ((p1v.y + p2v.y) / 2));
			return avgVelocity;
		}	
		
		
		public void paint(double offsetX,double offsetY) {
			if (_cornerPositions != null) updateCornerPositions();
			//TG TODO added offset
			super.paint(offsetX,offsetY);
		}
		
		
		public void resolveCollision(Vector mtd, Vector vel, Vector n, double d, double o) {
		
			if (! p1.getFixed()) {
				p1.curr.plusEquals(mtd);
				p1.setVelocity(vel);
			}
			
			if (! p2.getFixed()) {
				p2.curr.plusEquals(mtd);
				p2.setVelocity(vel);
			}
		}
	}
