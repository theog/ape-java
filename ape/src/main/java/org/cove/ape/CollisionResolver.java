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
	
	// NEED TO EXCLUDE VELOCITY CALCS BASED ON collisionResponseMode
	public final class CollisionResolver {
		
		public static void resolveParticleParticle(
				AbstractParticle pa, 
				AbstractParticle pb, 
				Vector normal, 
				double depth) {
			
			Vector mtd = normal.mult(depth);
			double te = pa.getElasticity() + pb.getElasticity();
			
			// the total friction in a collision is combined but clamped to [0,1]
			double tf = 1 - (pa.getFriction() + pb.getFriction());
			if (tf > 1) tf = 1;
			else if (tf < 0) tf = 0;
		
			// get the total mass, and assign giant mass to fixed particles
			double ma = (pa.getFixed()) ? 100000 : pa.getMass();
			double mb = (pb.getFixed()) ? 100000 : pb.getMass();
			double tm = ma + mb;
			
			// get the collision components, vn and vt
			Collision ca = pa.getComponents(normal);
			Collision cb = pb.getComponents(normal);
		 
		 	// calculate the coefficient of restitution based on the mass
			Vector vnA = (cb.vn.mult((te + 1) * mb).plus(ca.vn.mult(ma - te * mb))).divEquals(tm);		
			Vector vnB = (ca.vn.mult((te + 1) * ma).plus(cb.vn.mult(mb - te * ma))).divEquals(tm);
			ca.vt.multEquals(tf);
			cb.vt.multEquals(tf);
			
			// scale the mtd by the ratio of the masses. heavier particles move less
			Vector mtdA = mtd.mult( mb / tm);
			Vector mtdB = mtd.mult(-ma / tm);
			
			if (! pa.getFixed()) pa.resolveCollision(mtdA, vnA.plusEquals(ca.vt), normal, depth, -1);
			if (! pb.getFixed()) pb.resolveCollision(mtdB, vnB.plusEquals(cb.vt), normal, depth,  1);
		}
	}

