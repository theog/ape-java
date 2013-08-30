package org.theo.depricated;

import org.cove.ape.AbstractParticle;
import org.cove.ape.Vector;

   public class Propulsion {

	   private Vector propulsionVelocity = new Vector(0,0);	   

	   // object references
	   private AbstractParticle _focusObject;
	   
		public Propulsion() {
		}

		public void setFocusOn(AbstractParticle focusObject) {
			_focusObject = focusObject;
		}	
		
	   public void Step() {
		   _focusObject.setVelocity(_focusObject.getVelocity().plus(propulsionVelocity));
	   }
	   
	   public void Up() {
		   if (propulsionVelocity.y <= 1) {
			   propulsionVelocity.y = propulsionVelocity.y - .05;
			}	
	   }

	   public void Down() {
		   propulsionVelocity.y = propulsionVelocity.y + .05;
	   }
	   
   }