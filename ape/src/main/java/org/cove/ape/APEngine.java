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
//package org.cove.ape {
package org.cove.ape;
	
import java.util.ArrayList;
import java.awt.Graphics2D;
	
	
	/**
	 * The main engine class. All particles and constraints should be added and removed
	 * through this class.
	 * 
	 */
	public final class APEngine {
		public static final int STANDARD = 100;
		public static final int SELECTIVE = 200;
		public static final int SIMPLE = 300;
		
		/**@private */
		public static Vector force = new Vector(0,0);
		
		/**@private */
		public static Vector masslessForce = new Vector(0,0);
			
		private static double timeStep;
		private static ArrayList particles = new ArrayList();
		
		private static ArrayList constraints = new ArrayList();
		
		private static double _damping;
		private static Graphics2D _defaultContainer;
		private static int _collisionResponseMode = STANDARD;
		
		/**
		 * Initializes the engine. You must call this method prior to adding
		 * any particles or constraints.
		 * 
		 * @param dt The delta time value for the engine. This parameter can be used -- in 
		 * conjunction with speed at which <code>APEngine.step()</code> is called -- to change the speed
		 * of the simulation. Typical values are 1/3 or 1/4. Lower values result in slower,
		 * but more accurate simulations, and higher ones result in faster, less accurate ones.
		 * Note that this only applies to the forces added to particles. If you do not add any
		 * forces, the <code>dt</code> value won't matter.
		 */
		public static void init(double dt) {
			timeStep = dt * dt;
			
			_damping = 1;
		}


		/**
		 * The global damping. Values should be between 0 and 1. Higher numbers
		 * result in less damping. A value of 1 is no damping. A value of 0 will
		 * not allow any particles to move. The default is 1.
		 * 
		 * <p>
		 * Damping will slow down your simulation and make it more stable. If you find
		 * that your sim is "blowing up', try applying more damping. 
		 * </p>
		 * 
		 * @param d The damping value. Values should be >=0 and <=1.
		 */
		public static double getDamping() {
			return _damping;
		}
		
		
		/**
		 * @private
		 */
		public static void setDamping(double d) {
			_damping = d;
		}
		
		
		/**
		 * The default container used by the default painting methods of the particles and
		 * constraints. If you wish to use to the built in painting methods you must set 
		 * this first.
		 * 
		 * <p>
		 * For simple prototyping, a default painting method is included in the engine. For
		 * any serious development, you should either subclass or make a composite of the constraints
		 * and particles, and write your own painting methods. If you do that, it is not necessary
		 * to call this function, although you can use it for you own painting methods if you need
		 * a container.
		 * </p>
		 * 
		 * @param s An instance of the Sprite class that will be used as the default container.
		 * 
		 */
		public static Graphics2D getDefaultContainer() {
			return _defaultContainer;
		}
			
		
		/**
		 * @private
		 */
		public static void setDefaultContainer(Graphics2D s) {
			_defaultContainer = s;
		}
		
		
		/**
		 * The collision response mode for the engine. The engine has three different possible
		 * settings for the collisionResponseMode property. Valid values are APEngine.STANDARD, 
		 * APEngine.SELECTIVE, and APEngine.SIMPLE. Those settings go in order from slower and
		 * more accurate to faster and less accurate. In all cases it's worth experimenting to
		 * see what mode suits your sim best.  
		 *
		 * <ul>
		 * <li>
		 * <b>APEngine.STANDARD</b>&mdash;Particles are moved out of collision and then velocity is 
		 * applied. Momentum is conserved and the mass of the particles is properly calculated. This
		 * is the default and most physically accurate setting.<br/><br/>
		 * </li>
		 * 
		 * <li>
		 * <b>APEngine.SELECTIVE</b>&mdash;Similar to the APEngine.STANDARD setting, except only 
		 * previously non-colliding particles have their velocity set. In otherwords, if there are 
		 * multiple collisions on a particle, only the first collision on that particle causes a 
		 * change in its velocity. Both this and the APEngine.SIMPLE setting may give better results
		 * than APEngine.STANDARD when using a large number of colliding particles.<br/><br/>
		 * </li>
		 * 
		 * <li>
		 * <b>APEngine.SIMPLE</b>&mdash;Particles do not have their velocity set after colliding. This
		 * is faster than the other two modes but is the least accurate. Mass is not calculated, and 
		 * there is no conservation of momentum. <br/><br/>
		 * </li>
		 * </ul>
		 */
		public static int getCollisionResponseMode() {	
			return _collisionResponseMode;
		}
		
		
		/**
		 * @private
		 */			
		public static void setCollisionResponseMode(int m) {
			_collisionResponseMode = m;	
		}
		
			
		/**
		 * Adds a force to all particles in the system. The mass of the particle is taken into 
		 * account when using this method, so it is useful for adding forces that simulate effects
		 * like wind. Particles with larger masses will not be affected as greatly as those with
		 * smaller masses. Note that the size (not to be confused with mass) of the particle has
		 * no effect on its physical behavior.
		 * 
		 * @param f A Vector represeting the force added.
		 */ 
		public static void addForce(Vector v) {
			force.plusEquals(v);
		}
		
		/**
		 * Adds a 'massless' force to all particles in the system. The mass of the particle is 
		 * not taken into account when using this method, so it is useful for adding forces that
		 * simulate effects like gravity. Particles with larger masses will be affected the same
		 * as those with smaller masses. Note that the size (not to be confused with mass) of 
		 * the particle has no effect on its physical behavior.
		 * 
		 * @param f A Vector represeting the force added.
		 */ 	
		public static void addMasslessForce(Vector v) {
			masslessForce.plusEquals(v);
		}
		
		/**
		 * Adds a particle to the engine.
		 * 
		 * @param p The particle to be added.
		 */
		public static void addParticle(AbstractParticle p) {
			particles.add(p); // adds to the end of the list http://java.sun.com/j2se/1.4.2/docs/api/java/util/ArrayList.html		
		}
		
		
		/**
		 * Removes a particle to the engine.
		 * 
		 * @param p The particle to be removed.
		 */
		public static void removeParticle(AbstractParticle p) {
			int ppos = particles.indexOf(p);
			if (ppos == -1) return;
			particles.remove(ppos);
		}
		
		
		/**
		 * Adds a constraint to the engine.
		 * 
		 * @param c The constraint to be added.
		 */
		public static void addConstraint(AbstractConstraint c) {
			constraints.add(c); // adds to the end of the list http://java.sun.com/j2se/1.4.2/docs/api/java/util/ArrayList.html
		}


		/**
		 * Removes a constraint from the engine.
		 * 
		 * @param c The constraint to be removed.
		 */
		public static void removeConstraint(AbstractConstraint c) {
			int cpos = constraints.indexOf(c);
			if (cpos == -1) return;
			constraints.remove(cpos);
		}
	
	
		/**
		 * Returns an array of every item added to the engine. This includes all particles and
		 * constraints.
		 */
		public static ArrayList getAll() {
			ArrayList a = (ArrayList)particles.clone(); // I have added this line
			a.addAll(constraints);
			return a;
		}
	
		
		/**
		 * Returns an array of every particle added to the engine.
		 */
		public static ArrayList getAllParticles() {
			return particles;
		}	
		
	
		/**
		 * Returns an array of every custom particle added to the engine. A custom
		 * particle is defined as one that is not an instance of the included particle
		 * classes. If you create subclasses of any of the included particle classes, and
		 * add them to the engine using <code>addParticle(...)</code> then they will be
		 * returned by this method. This way you can keep a list of particles you have
		 * created, if you need to distinguish them from the built in particles.
		 */	
		public static ArrayList getCustomParticles() {
			ArrayList customParticles = new ArrayList();
			for (int i = 0; i < particles.size(); i++) {
				AbstractParticle p = (AbstractParticle)particles.get(i);
				if (isCustomParticle(p)) customParticles.add(p);
			}
			return customParticles;
		}
		
		
		/**
		 * Returns an array of particles added to the engine whose type is one of the built-in
		 * particle types in the APE. This includes the CircleParticle, WheelParticle, and
		 * RectangleParticle.
		 */			
		public static ArrayList getAPEParticles() {
			ArrayList apeParticles = new ArrayList();
			for (int i = 0; i < particles.size(); i++) {
				AbstractParticle p = (AbstractParticle)particles.get(i);
				if (! isCustomParticle(p)) apeParticles.add(p);
			}
			return apeParticles;
		}
		
	
		/**
		 * Returns an array of all the constraints added to the engine
		 */						
		public static ArrayList getAllConstraints() {
			return constraints;
		}	
	

		/**
		 * The main step function of the engine. This method should be called
		 * continously to advance the simulation. The faster this method is 
		 * called, the faster the simulation will run. Usually you would call
		 * this in your main program loop. 
		 */			
		public static void step() {
			integrate();
			satisfyConstraints();
			checkCollisions();
		}
		
		
		private static boolean isCustomParticle(AbstractParticle p) {
			boolean isWP = false;
			boolean isCP = false;
			boolean isRP = false;
			//TG TODO this needs to be tested : p.getClass().getName()
			String className = p.getClass().getName();
			if (className == "org.cove.ape::WheelParticle") isWP=true;
			if (className == "org.cove.ape::CircleParticle") isCP=true;
			if (className == "org.cove.ape::RectangleParticle") isRP=true;

			if (! (isWP || isCP || isRP)) return true;
			return false;		
		}


		private static void integrate() {
			for (int i = 0; i < particles.size(); i++) {
				if (particles.get(i) instanceof RectangleParticle)  
					((RectangleParticle)particles.get(i)).update(timeStep);
				else if (particles.get(i) instanceof CircleParticle) 
					((CircleParticle)particles.get(i)).update(timeStep);
			}
		}
	
		
		private static void satisfyConstraints() {
			for (int n = 0; n < constraints.size(); n++) {
				((AbstractConstraint)constraints.get(n)).resolve();
			}
		}
	
	
		/**
		 * Checks all collisions between particles and constraints. The following rules apply: 
		 * Particles vs Particles are tested unless either collidable property is set to false.
		 * Particles vs Constraints are not tested by default unless collidable is true.
		 * is called on a SpringConstraint. AngularConstraints are not tested for collision,
		 * but their component SpringConstraints are -- with the previous rule in effect. If
		 * a Particle is attached to a SpringConstraint it is never tested against that 
		 * SpringConstraint for collision
		 */
		private static void checkCollisions() {
			for (int j = 0; j < particles.size(); j++) {
				
				AbstractParticle pa = (AbstractParticle)particles.get(j);
				
				for (int i = j + 1; i < particles.size(); i++) {
					AbstractParticle pb = (AbstractParticle)particles.get(i);
					if (pa.getCollidable() && pb.getCollidable()) {
						CollisionDetector.test(pa, pb);
					}
				}
				
				for (int n = 0; n < constraints.size(); n++) {
					if (constraints.get(n) instanceof AngularConstraint) continue;
					SpringConstraint c = (SpringConstraint)constraints.get(n);
					if (pa.getCollidable() && c.getCollidable() && ! c.isConnectedTo(pa)) {
						CollisionDetector.test(pa, c.getCollisionRect());
					}
				}
				pa.isColliding = false;	
			}
		}
	}	
