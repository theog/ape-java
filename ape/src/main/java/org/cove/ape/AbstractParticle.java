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
	
	import java.awt.Graphics2D;
	 	
	/**
	 * The abstract base class for all particles.
	 * 
	 * <p>
	 * You should not instantiate this class directly -- instead use one of the subclasses.
	 * </p>
	 */
	public class AbstractParticle {
		
		// internal properties are not hidden from asdoc?
		
		/** @private */
		public Vector curr;
		/** @private */
		public Vector prev;
		/** @private */
		public boolean isColliding;
		/** @private */
		public Interval interval;
		/** @private */
		protected Graphics2D dc;
		
		
		private Vector forces;
		private Vector temp;
		
		private double _kfr;
		private double _mass;
		private double _invMass;
		private boolean _fixed;
		private boolean _visible;
		private double _friction;
		private boolean _collidable;
		private Collision collision;
		
		// TG TODO extend this class instead 
		private double _magnetic;
		
		/** 
		 * @private
		 */
		public AbstractParticle (
				double x, 
				double y, 
				boolean isFixed, 
				double mass, 
				double elasticity,
				double friction) {
		
			interval = new Interval(0,0);
			
			curr = new Vector(x, y);
			prev = new Vector(x, y);
			temp = new Vector(0,0);
			setFixed(isFixed);
			
			forces = new Vector(0,0);
			collision = new Collision(new Vector(0,0), new Vector(0,0));
			isColliding = false;
			
			// TG TODO extend this rather than this.
			_magnetic = 0; // no magnetism
			
			setMass(mass);
			setElasticity(elasticity);
			setFriction(friction);
			
			setCollidable(true);
			setVisible(true);
		}
	
	
		/**
		 * The mass of the particle. Valid values are greater than zero. By default, all particles
		 * have a mass of 1. 
		 * 
		 * <p>
		 * The mass property has no relation to the size of the particle. However it can be
		 * easily simulated when creating particles. A simple example would be to set the 
		 * mass and the size of a particle to same value when you instantiate it.
		 * </p>
		 * @throws flash.errors.Error flash.errors.Error if the mass is set less than zero. 
		 */
		public double getMass() {
			return _mass; 
		}
		
		
		/**
		 * @private
		 */
		public void setMass(double m) {
			if (m <= 0) throw new Error("mass may not be set <= 0"); 
			_mass = m;
			_invMass = 1 / _mass;
		}	
	

		public double getMagnetic() {
			// determine magnetic strength, 0 = no magnetism
			return _magnetic; 
		}
		
		public void setMagnetic(double m) {
			// determine magnetic strength, 0 = no magnetism
			_magnetic = m; 
		}
	
		
		/**
		 * The elasticity of the particle. Standard values are between 0 and 1. 
		 * The higher the value, the greater the elasticity.
		 * 
		 * <p>
		 * During collisions the elasticity values are combined. If one particle's
		 * elasticity is set to 0.4 and the other is set to 0.4 then the collision will
		 * be have a total elasticity of 0.8. The result will be the same if one particle
		 * has an elasticity of 0 and the other 0.8.
		 * </p>
		 * 
		 * <p>
		 * Setting the elasticity to greater than 1 (of a single particle, or in a combined
		 * collision) will cause particles to bounce with energy greater than naturally 
		 * possible. Setting the elasticity to a value less than zero is allowed but may cause 
		 * unexpected results.
		 * </p>
		 */ 
		public double getElasticity() {
			return _kfr; 
		}
		
		
		/**
		 * @private
		 */
		public void  setElasticity(double k) {
			_kfr = k;
		}
		

		/**
		 * The visibility of the particle. This is only implemented for the default painting
		 * methods of the particles. When you create your painting methods in subclassed or
		 * composite particles, you should add a check for this property.
		 */	
		public boolean getVisible() {
			return _visible;
		}
		
		
		/**
		 * @private
		 */			
		public void setVisible(boolean v) {
			_visible = v;
		}
		
				
		/**
		 * The surface friction of the particle. Values must be in the range of 0 to 1.
		 * 
		 * <p>
		 * 0 is no friction (slippery), 1 is full friction (sticky).
		 * </p>
		 * 
		 * <p>
		 * During collisions, the friction values are summed, but are clamped between 1 and 0.
		 * For example, If two particles have 0.7 as their surface friction, then the resulting
		 * friction between the two particles will be 1 (full friction).
		 * </p>
		 * 
		 * <p>
		 * Note: In the current release, only dynamic friction is calculated. Static friction
		 * is planned for a later release.
		 * </p>
		 * 
		 * @throws flash.errors.Error flash.errors.Error if the friction is set less than zero or greater than 1
		 */	
		public double getFriction() {
			return _friction; 
		}
	
		
		/**
		 * @private
		 */
		public void setFriction(double f) {
			if (f < 0 || f > 1) throw new Error("Legal friction must be >= 0 and <=1");
			_friction = f;
		}
		
		
		/**
		 * The fixed state of the particle. If the particle is fixed, it does not move
		 * in response to forces or collisions. Fixed particles are good for surfaces.
		 */
		public boolean getFixed() {
			return _fixed;
		}

 
		/**
		 * @private
		 */
		public void setFixed(boolean f) {
			_fixed = f;
		}
		
		
		/**
		 * The position of the particle. Getting the position of the particle is useful
		 * for drawing it or testing it for some custom purpose. 
		 * 
		 * <p>
		 * When you get the <code>position</code> of a particle you are given a copy of the current
		 * location. Because of this you cannot change the position of a particle by
		 * altering the <code>x</code> and <code>y</code> components of the Vector you have retrieved from the position property.
		 * You have to do something instead like: <code> position = new Vector(100,100)</code>, or
		 * you can use the <code>px</code> and <code>py</code> properties instead.
		 * </p>
		 * 
		 * <p>
		 * You can alter the position of a particle three ways: change its position, set
		 * its velocity, or apply a force to it. Setting the position of a non-fixed particle
		 * is not the same as setting its fixed property to true. A particle held in place by 
		 * its position will behave as if it's attached there by a 0 length sprint constraint. 
		 * </p>
		 */
		public Vector getPosition() {
			return new Vector(curr.x,curr.y);
		}
		
		
		/**
		 * @private
		 */
		public void setPosition(Vector p) {	
			curr.copy(p);
			prev.copy(p);
		}

	
		/**
		 * The x position of this particle
		 */
		public double getpx() {	
			return curr.x;
		}

		
		/**
		 * @private
		 */
		public void setpx(double x) {
			curr.x = x;
			prev.x = x;	
		}


		/**
		 * The y position of this particle
		 */
		public double getpy() {
			return curr.y;
		}


		/**
		 * @private
		 */
		public void setpy(double y) {
			curr.y = y;
			prev.y = y;	
		}


		/**
		 * The velocity of the particle. If you need to change the motion of a particle, 
		 * you should either use this property, or one of the addForce methods. Generally,
		 * the addForce methods are best for slowly altering the motion. The velocity property
		 * is good for instantaneously setting the velocity, e.g., for projectiles.
		 * 
		 */
		public Vector getVelocity() {
			return curr.minus(prev);
		}
		
		
		/**
		 * @private
		 */	
		public void setVelocity(Vector v) {
			prev = curr.minus(v);	
		}
		
		
		/**
		 * Determines if the particle can collide with other particles or constraints.
		 * The default state is true.
		 */
		public boolean getCollidable() {
			return _collidable;
		}
	
				
		/**
		 * @private
		 */		
		public void setCollidable(boolean b) {
			_collidable = b;
		}
		
			
		// NEED REMOVE FORCES METHODS
		/**
		 * Adds a force to the particle. The mass of the particle is taken into 
		 * account when using this method, so it is useful for adding forces 
		 * that simulate effects like wind. Particles with larger masses will
		 * not be affected as greatly as those with smaller masses. Note that the
		 * size (not to be confused with mass) of the particle has no effect 
		 * on its physical behavior.
		 * 
		 * @param f A Vector represeting the force added.
		 */ 
		public void addForce(Vector f) {
			forces.plusEquals(f.multEquals(_invMass));
		}
		
		
		/**
		 * Adds a 'massless' force to the particle. The mass of the particle is 
		 * not taken into account when using this method, so it is useful for
		 * adding forces that simulate effects like gravity. Particles with 
		 * larger masses will be affected the same as those with smaller masses.
		 *
		 * @param f A Vector represeting the force added.
		 */ 	
		public void addMasslessForce(Vector f) {
			forces.plusEquals(f);
		}
		
		
		/**
		 * @private
		 */
		public void update(double dt2) {
			
			
			if (_fixed) return;
			
			// global forces
			addForce(APEngine.force);
			addMasslessForce(APEngine.masslessForce);
	
			// integrate
			temp.copy(curr);
			Vector nv = getVelocity().plus(forces.multEquals(dt2));
			curr.plusEquals(nv.multEquals(APEngine.getDamping()));
			prev.copy(temp);
		
			// clear the forces
			forces.setTo(0,0);
		}
		
		
		/**
		 * @private
		 */		
		public Collision getComponents(Vector collisionNormal) {
			Vector vel = getVelocity();
			double vdotn = collisionNormal.dot(vel);
			collision.vn = collisionNormal.mult(vdotn);
			collision.vt = vel.minus(collision.vn);	
			return collision;
		}
	
	
		/**
		 * @private
		 */	
		public void resolveCollision(Vector mtd, Vector vel, Vector n, double d, double o) {	
		
			curr.plusEquals(mtd);
			
			switch (APEngine.getCollisionResponseMode()) {
				
				case APEngine.STANDARD:
					//velocity = vel;
					setVelocity(vel);
					break;
				
				case APEngine.SELECTIVE:
					if (!isColliding) setVelocity(vel);
					isColliding = true;
					break;
					
				case APEngine.SIMPLE:
					break;
			}
		}
		
		
		/**
		 * @private
		 */		
		public double getInvMass() {
			return _invMass; 
		}
		

		/**
		 * @private
		 */	
		public Graphics2D getDefaultContainer() {
			if (APEngine.getDefaultContainer() == null) {
				String err = "";
				err += "You must set the defaultContainer property of the APEngine class ";
				err += "if you wish to use the default paint methods of the particles";
				throw new Error(err);
			}
			Graphics2D parentContainer = APEngine.getDefaultContainer();
			return parentContainer;
		}
		
		
		/**
		 * @private
		 */		
		public Interval getProjection(Vector axis) {
			return null;
		}
	}	
