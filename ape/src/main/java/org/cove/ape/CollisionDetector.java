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
	
	public final class CollisionDetector {
		
		/**
		 * Tests the collision between two objects. If there is a collision it is passed off
		 * to the CollisionResolver class to resolve the collision.
		 */	
		public static void test(AbstractParticle objA, AbstractParticle objB) {
			boolean collision = false;
			
			if (objA.getFixed() && objB.getFixed()) return;
		
			// rectangle to rectangle
			if (objA instanceof RectangleParticle && objB instanceof RectangleParticle) {
				collision = testOBBvsOBB((RectangleParticle)objA, (RectangleParticle)objB);
			
			// circle to circle
			} else if (objA instanceof CircleParticle && objB instanceof CircleParticle) {
				collision = testCirclevsCircle((CircleParticle)objA, (CircleParticle)objB);
	
			// rectangle to circle - two ways
			} else if (objA instanceof RectangleParticle && objB instanceof CircleParticle) {
				collision = testOBBvsCircle((RectangleParticle)objA, (CircleParticle)objB);
				
			} else if (objA instanceof CircleParticle && objB instanceof RectangleParticle)  {
				collision = testOBBvsCircle((RectangleParticle)objB, (CircleParticle)objA);		
			}
			
			// hack, object becomes fixes when collision detection has occured, 
			// this will be replaced with a magnetic/gravity algorithm.   
			if ((collision) & (objA.getMagnetic() == -10 && objB.getMagnetic() == 10)) {
				objB.setFixed(true);	
			}	
			else if ((collision) & (objA.getMagnetic() == 10 && objB.getMagnetic() == -10)) {
				objA.setFixed(true);	
			}			
		}
	
	
		/**
		 * Tests the collision between two RectangleParticles (aka OBBs). If there is a collision it
		 * determines its axis and depth, and then passes it off to the CollisionResolver for handling.
		 */
		private static boolean testOBBvsOBB(RectangleParticle ra, RectangleParticle rb) {
			
			// TG TODO i had to init this however it could have an effect on the functionality.
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
			    }
			}
			CollisionResolver.resolveParticleParticle(ra, rb, collisionNormal, collisionDepth);
			return true;
		}		
	
	
		/**
		 * Tests the collision between a RectangleParticle (aka an OBB) and a CircleParticle. 
		 * If there is a collision it determines its axis and depth, and then passes it off 
		 * to the CollisionResolver for handling.
		 */
		private static boolean testOBBvsCircle(RectangleParticle ra, CircleParticle ca) {
			
			// TG TODO i had to init this however it could have an effect on the functionality.
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
				} else {
					// ra is in vertex region, but is not colliding
					return false;
				}
			}
			CollisionResolver.resolveParticleParticle(ra, ca, collisionNormal, collisionDepth);
			return true;
		}
	
	
		/**
		 * Tests the collision between two CircleParticles. If there is a collision it 
		 * determines its axis and depth, and then passes it off to the CollisionResolver
		 * for handling.
		 */	
		private static boolean testCirclevsCircle(CircleParticle ca, CircleParticle cb) {
			
			double depthX = testIntervals(ca.getIntervalX(), cb.getIntervalX());
			if (depthX == 0) return false;
			
			double depthY = testIntervals(ca.getIntervalY(), cb.getIntervalY());
			if (depthY == 0) return false;
			
			Vector collisionNormal = ca.curr.minus(cb.curr);
			double mag = collisionNormal.magnitude();
			double collisionDepth = (ca.getRadius() + cb.getRadius()) - mag;
			
			if (collisionDepth > 0) {
				collisionNormal.divEquals(mag);
				CollisionResolver.resolveParticleParticle(ca, cb, collisionNormal, collisionDepth);
				return true;
			} else {
				return false;
			}
		}
	
	
		/**
		 * Returns 0 if intervals do not overlap. Returns smallest depth if they do.
		 */
		private static double testIntervals(Interval intervalA, Interval intervalB) {
			
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
	}
