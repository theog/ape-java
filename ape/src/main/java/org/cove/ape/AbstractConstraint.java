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
	 * The abstract base class for all constraints. 
	 * 
	 * <p>
	 * You should not instantiate this class directly -- instead use one of the subclasses.
	 * </p>
	 */
	public class AbstractConstraint {
		
		/** @private */
		protected Graphics2D dc;
		
		
		private boolean _visible;
		private double _stiffness;


		/** 
		 * @private
		 */
		public AbstractConstraint (double stiffness) {
			_visible = true;
			_stiffness = stiffness;
		}
			
		
		/**
		 * The stiffness of the constraint. Higher values result in result in 
		 * stiffer constraints. Values should be greater than 0 and less than or 
		 * equal to 1. Depending on the situation, setting constraints to very high 
		 * values may result in instability or unwanted energy.
		 */  
		 
		public double getStiffness() {
			return _stiffness;
		}
		
		
		/**
		 * @private
		 */			
		public void setStiffness(double s) {
			_stiffness = s;
		}
		
		
		/**
		 * The visibility of the constraint. This is only implemented for the default painting
		 * methods of the constraints. When you create your painting methods in subclassed constraints 
		 * or composites you should add a check for this property.
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
		 * @private
		 */
		public void resolve() {
		}
		
		
		/**
		 * @private
		 */
		protected Graphics2D getDefaultContainer() {

			if (APEngine.getDefaultContainer() == null) {
				String err = "";
				err += "You must set the defaultContainer property of the APEngine class ";
				err += "if you wish to use the default paint methods of the constraints";
				throw new Error(err);
			}
			Graphics2D parentContainer = APEngine.getDefaultContainer();
			return parentContainer;
		}
	}
