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
import java.awt.geom.*; // For Rectangle2D

	/**
	 * A rectangular shaped particle. 
	 */ 
	public class RectangleParticle extends AbstractParticle {
		
		/** @private */
		public ArrayList _cornerPositions = new ArrayList();
		
		private ArrayList _cornerParticles = new ArrayList();
		private ArrayList _extents = new ArrayList();
		private ArrayList _axes = new ArrayList();
		private double _rotation;
		
		
		/**
		 * @param x The initial x position.
		 * @param y The initial y position.
		 * @param width The width of this particle.
		 * @param height The height of this particle.
		 * @param rotation The rotation of this particle in radians.
		 * @param fixed Determines if the particle is fixed or not. Fixed particles
		 * are not affected by forces or collisions and are good to use as surfaces.
		 * Non-fixed particles move freely in response to collision and forces.
		 * @param mass The mass of the particle
		 * @param elasticity The elasticity of the particle. Higher values mean more elasticity.
		 * @param friction The surface friction of the particle. 
		 * <p>
		 * Note that RectangleParticles can be fixed but still have their rotation property 
		 * changed.
		 * </p>
		 */
		//TG TODO check defaults
		public RectangleParticle (
				double x, 
				double y, 
				double width, 
				double height, 
				double rotation, 
				boolean fixed,
				double mass, 
				double elasticity,
				double friction) {

			super(x, y, fixed, mass, elasticity, friction);
			
			_extents.add(Double.valueOf(width/2));
			_extents.add(Double.valueOf(height/2));
			
			_axes.add(new Vector(0,0));
			_axes.add(new Vector(0,0));
			setRotation(rotation);

			//TG TODO I have added these lines as in actionscript3 these are called when initialized
			_cornerPositions = getCornerPositions();
			_cornerParticles = getCornerParticles();
		}
		
		
		/**
		 * The rotation of the RectangleParticle in radians. For drawing methods you may 
		 * want to use the <code>angle</code> property which gives the rotation in
		 * degrees from 0 to 360.
		 * 
		 * <p>
		 * Note that while the RectangleParticle can be rotated, it does not have angular
		 * velocity. In otherwords, during collisions, the rotation is not altered, 
		 * and the energy of the rotation is not applied to other colliding particles.
		 * A true rigid body is planned for a later release.
		 * </p>
		 */
		public double getRotation() {
			return _rotation;
		}
		
		
		/**
		 * @private
		 */		
		public void setRotation(double t) {
			_rotation = t;
			setAxes(t);
		}
			
		/**
		 * An Array of 4 contact particles at the corners of the RectangleParticle. You can attach
		 * other particles or constraints to these particles. Note this is a one-way effect, meaning the
		 * RectangleParticle's motion will move objects attached to the corner particles, but the 
		 * reverse is not true. 
		 * 
		 * <p>
		 * In order to access one of the 4 corner particles, you can use array notation 
		 * e.g., <code>myRectangleParticle.cornerParticles[0]</code>
		 * </p>
		 */					
		public ArrayList getCornerParticles() {
			
			//TG TODO not sure exactly what the diff is with the _
			if (_cornerPositions.size() == 0) getCornerPositions();
			
			if (_cornerParticles.size() == 0) {
				//TG TODO default ??
				CircleParticle cp1 = new CircleParticle(0.0,0.0,1.0,false,1.0,0.3,0.0);
				cp1.setCollidable(false);
				cp1.setVisible(false);
				APEngine.addParticle(cp1);
				
				CircleParticle cp2 = new CircleParticle(0.0,0.0,1.0,false,1.0,0.3,0.0);
				cp2.setCollidable(false);
				cp2.setVisible(false);
				APEngine.addParticle(cp2);
	
				CircleParticle cp3 = new CircleParticle(0.0,0.0,1.0,false,1.0,0.3,0.0);
				cp3.setCollidable(false);
				cp3.setVisible(false);
				APEngine.addParticle(cp3);
	
				CircleParticle cp4 = new CircleParticle(0.0,0.0,1.0,false,1.0,0.3,0.0);
				cp4.setCollidable(false);
				cp4.setVisible(false);
				APEngine.addParticle(cp4);
				
				_cornerParticles.add(cp1);
				_cornerParticles.add(cp2);
				_cornerParticles.add(cp3);
				_cornerParticles.add(cp4);
				
				updateCornerParticles();
			}
			return _cornerParticles;
		}
		
		
		/**
		 * An Array of <code>Vector</code> objects storing the location of the 4
		 * corners of this RectangleParticle. This method would usually be called
		 * in a painting method if the locations of the corners were needed. If the
		 * RectangleParticle is being drawn using its position and angle properties 
		 * then you don't need to access this property.
		 */
		//public function get cornerPositions():Array {
		public ArrayList getCornerPositions() {
					
			if (_cornerPositions.size() == 0) {
				_cornerPositions.add(new Vector(0,0));
				_cornerPositions.add(new Vector(0,0));
				_cornerPositions.add(new Vector(0,0));
				_cornerPositions.add(new Vector(0,0));
						
				updateCornerPositions();
			}
			return _cornerPositions;
		}
		
		
		/**
		 * The default paint method for the particle. Note that you should only use
		 * the default painting methods for quick prototyping. For anything beyond that
		 * you should always write your own particle classes that extend one of the
		 * APE particle classes. Then within that class you can define your own custom
		 * painting method.
		 */
		public void paint(
				// TG TODO added for offset.
				double offsetX, double offsetY) {
			
			if (dc == null) dc = getDefaultContainer();
			
			//TG TODO should I be clearing this?
			//dc.graphics.clear();
			if (!getVisible()) return;
			
			for (int  j = 0; j < 4; j++) {
				int i = j;
				
				double X1 = ((Vector)_cornerPositions.get(i)).x;
				double Y1 = ((Vector)_cornerPositions.get(i)).y;
				
				// point back to first element 
				if (j == 3) i = -1;
					
				double X2 = ((Vector)_cornerPositions.get(i+1)).x;
				double Y2 = ((Vector)_cornerPositions.get(i+1)).y;

				//TG TODO added for offset 
				Line2D line = new Line2D.Double(offsetX+X1,offsetY+Y1,offsetX+X2,offsetY+Y2);
				dc.draw(line);
			}
			
		}


		/**
		 * @private
		 */
		public void update(double dt2) {
			super.update(dt2);
			if (_cornerPositions.size() != 0) updateCornerPositions();
			if (_cornerParticles.size() != 0) updateCornerParticles();
		}
		
		
		/**
		 * @private
		 */	
		public ArrayList getAxes() {
			return _axes;
		}
		

		/**
		 * @private
		 */	
		public ArrayList getExtents() {
			return _extents;
		}
		
		
		// REVIEW FOR ANY POSSIBILITY OF PRECOMPUTING
		/**
		 * @private
		 */	
		public Interval getProjection(Vector axis) {
			
			double radius =
				((Double)_extents.get(0)).doubleValue() * Math.abs((double)axis.dot((Vector)_axes.get(0)))+
				((Double)_extents.get(1)).doubleValue() * Math.abs((double)axis.dot((Vector)_axes.get(1)));
			
			double c = curr.dot(axis);
			
			interval.min = c - radius;
			interval.max = c + radius;
			return interval;
		}


		/**
		 * @private
		 */	
		public void updateCornerPositions() {
		
			double ae0_x = ((Vector)_axes.get(0)).x * ((Double)_extents.get(0)).doubleValue();
			double ae0_y = ((Vector)_axes.get(0)).y * ((Double)_extents.get(0)).doubleValue();
			double ae1_x = ((Vector)_axes.get(1)).x * ((Double)_extents.get(1)).doubleValue();
			double ae1_y = ((Vector)_axes.get(1)).y * ((Double)_extents.get(1)).doubleValue();
			
			
			double emx = ae0_x - ae1_x;
			double emy = ae0_y - ae1_y;
			double epx = ae0_x + ae1_x;
			double epy = ae0_y + ae1_y;
			
			Vector cornerPosition1 = new Vector(0,0);
			Vector cornerPosition2 = new Vector(0,0);
			Vector cornerPosition3 = new Vector(0,0);
			Vector cornerPosition4 = new Vector(0,0);
			
			cornerPosition1.x = curr.x - epx;
			cornerPosition1.y = curr.y - epy;
			_cornerPositions.set(0,cornerPosition1);
			
			cornerPosition2.x = curr.x + emx;
			cornerPosition2.y = curr.y + emy;
			_cornerPositions.set(1,cornerPosition2);
			
			cornerPosition3.x = curr.x + epx;
			cornerPosition3.y = curr.y + epy;
			_cornerPositions.set(2,cornerPosition3);
			
			cornerPosition4.x = curr.x - emx;
			cornerPosition4.y = curr.y - emy;
			_cornerPositions.set(3,cornerPosition4);
		
		}
		
		
		/**
		 * 
		 */
		private void updateCornerParticles() {
			for (int i = 0; i < 4; i++) {
				((AbstractParticle)getCornerParticles().get(i)).setpx( ((Vector)_cornerPositions.get(i)).x );
				((AbstractParticle)getCornerParticles().get(i)).setpy( ((Vector)_cornerPositions.get(i)).y );
			}	
		}


		/**
		 * 
		 */					
		private void setAxes(double t) {
			double s = Math.sin(t);
			double c = Math.cos(t);

			((Vector)_axes.get(0)).x = c;
			((Vector)_axes.get(0)).y = s;
			((Vector)_axes.get(1)).x = -s;
			((Vector)_axes.get(1)).y = c;
		}
	}
