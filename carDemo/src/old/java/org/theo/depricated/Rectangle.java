package org.theo.depricated;

import java.util.ArrayList;

import org.cove.ape.APEngine;
import org.cove.ape.CircleParticle;
import org.cove.ape.SpringConstraint;
	
	public class Rectangle  {
	
		public ArrayList<SpringConstraint> _edgeConstraints = new ArrayList<SpringConstraint>();
		public ArrayList<CircleParticle> _edgeParticles = new ArrayList<CircleParticle>();

		private double circleRadius = 1;
		private double constraintWidth = 2;

		public Rectangle(
		double x, 
		double y,
		double width,
		double height,
		double mass, 
		double elasticity,
		double friction,
		double traction) {

			CircleParticle cp1 = new CircleParticle(x,y,circleRadius,false,(double)mass/4,elasticity,friction);
			CircleParticle cp2 = new CircleParticle(x,y+width,circleRadius,false,(double)mass/4,elasticity,friction);
			CircleParticle cp3 = new CircleParticle(x+width,y,circleRadius,false,(double)mass/4,elasticity,friction);
			CircleParticle cp4 = new CircleParticle(x+width,y+width,circleRadius,false,(double)mass/4,elasticity,friction);
			APEngine.addParticle(cp1);
			APEngine.addParticle(cp2);
			APEngine.addParticle(cp3);
			APEngine.addParticle(cp4);
			
			_edgeParticles.add(cp1);
			_edgeParticles.add(cp2);
			_edgeParticles.add(cp3);
			_edgeParticles.add(cp4);

			//left
			SpringConstraint con1 = new SpringConstraint(cp1, cp2,1);
			con1.setCollidable(true);
			con1.setCollisionRectWidth((double)constraintWidth);
			con1.setCollisionRectScale((double)0.9);
			APEngine.addConstraint(con1);
			
			// right
			SpringConstraint con3 = new SpringConstraint(cp3, cp4,1);
			con3.setCollidable(true);
			con3.setCollisionRectWidth((double)constraintWidth);
			con3.setCollisionRectScale((double)0.9);
			APEngine.addConstraint(con3);

			SpringConstraint con5 = new SpringConstraint(cp1, cp3,1);
			con5.setCollidable(true);
			con5.setCollisionRectWidth((double)constraintWidth);
			con5.setCollisionRectScale((double)0.9);
			APEngine.addConstraint(con5);

			SpringConstraint con6 = new SpringConstraint(cp2, cp4,0.9);
			con6.setCollidable(true);
			con6.setCollisionRectWidth((double)constraintWidth);
			con6.setCollisionRectScale((double)0.9);
			APEngine.addConstraint(con6);
			
			//inner constraint
			SpringConstraint con2 = new SpringConstraint(cp2, cp3,1);
			con2.setCollidable(false);
			APEngine.addConstraint(con2);

			//inner constraint
			SpringConstraint con4 = new SpringConstraint(cp4, cp1,1);
			con4.setCollidable(false);
			APEngine.addConstraint(con4);

			_edgeConstraints.add(con1);
			_edgeConstraints.add(con2);
			_edgeConstraints.add(con3);
			_edgeConstraints.add(con4);
			_edgeConstraints.add(con5);
			_edgeConstraints.add(con6);
			
		}	
	
		
	}


