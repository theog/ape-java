package org.theo.aftokinito;

import java.util.ArrayList;

import org.cove.ape.APEngine;
import org.cove.ape.AbstractConstraint;
import org.cove.ape.AbstractParticle;
import org.cove.ape.CircleParticle;
import org.cove.ape.SpringConstraint;
import org.cove.ape.Vector;

   public class Lasso {

	   	public ArrayList nodes = new ArrayList();
		public ArrayList connections = new ArrayList();
		
		private AbstractParticle _fixedObject;
		
		private double maxDistance = 10;
		private double maxNodes = 12;
		private boolean _ropeStopped = false;
		
		// object references
		public carDemo _gameObject;
		

		public Lasso() {
		}
		
		public void delete() {
			for (int i=0; i < nodes.size(); i++ ) {
				APEngine.removeParticle((AbstractParticle)(nodes.get(i)));
			}		
			for (int i=0; i < connections.size(); i++ ) {
				APEngine.removeConstraint((AbstractConstraint)(connections.get(i)));
			}						
			nodes.clear();
			connections.clear();
			_gameObject.updatePaintQueue();
			_ropeStopped = false;
		}
		
		public void setStopped(boolean ropeStopped) {
			_ropeStopped = ropeStopped;
		}	

		public void setGameObject(carDemo gameObject) {
			_gameObject = gameObject;
		}	
		
		public void setFixedObject(AbstractParticle fixedObject) {
			_fixedObject = fixedObject;
		}
		
		public void fire(Vector vel) {
			
			nodes = new ArrayList(0);
			connections = new ArrayList(0);
			
			nodes.add(0,new CircleParticle(_fixedObject.getpx(), _fixedObject.getpy(),3,false,1,0.3,0));
			APEngine.addParticle((CircleParticle)nodes.get(0));

			((CircleParticle)nodes.get(0)).setVelocity(vel);
			// set it as a magnetic property
			((CircleParticle)nodes.get(0)).setMagnetic(10);
			
			
			/*nodes.add(0,new WheelParticle(_fixedObject.getpx(), _fixedObject.getpy(),3,false,3,1,0.3,0));
			APEngine.addParticle((WheelParticle)nodes.get(0));

			((WheelParticle)nodes.get(0)).setVelocity(vel);
			((WheelParticle)nodes.get(0)).setAngularVelocity(1);
			
			// set it as a magnetic property
			((WheelParticle)nodes.get(0)).setMagnetic(10);*/

			_gameObject.updatePaintQueue();
			System.out.println("Created partice." + nodes);
		}
		
		
		public void step() {
			double distance = 0;

			// determine if last join is past x start pos
			if (nodes.size() > 0) {
				// TODO : could cache these values in memory and check if modes has changed
				double x0 = ((CircleParticle)nodes.get(nodes.size()-1)).getpx();
				double y0 = ((CircleParticle)nodes.get(nodes.size()-1)).getpy();
				
				double x1 = _fixedObject.getpx();
				double y1 = _fixedObject.getpy();
		
				if (nodes.size() < maxNodes) {
					distance = Geometry.length (x0, y0, x1, y1);
				
					System.out.println("length" + distance);
		
					if (distance >= maxDistance) {
						nodes.add(nodes.size(),new CircleParticle(_fixedObject.getpx(), _fixedObject.getpy(),3,false,0.1,0.3,0));
						APEngine.addParticle((CircleParticle)nodes.get(nodes.size()-1));
						System.out.println("Created partice." + (10+((nodes.size())*30)));
						
						connections.add(connections.size(),new SpringConstraint((CircleParticle)nodes.get(nodes.size()-2), (CircleParticle)nodes.get(nodes.size()-1),1));
						APEngine.addConstraint((SpringConstraint)connections.get(connections.size()-1));
						System.out.println("Created Constraint.");

						_gameObject.updatePaintQueue();
					}
				} else if (nodes.size() == maxNodes && !_ropeStopped) {	
					
					connections.add(connections.size(),new SpringConstraint((CircleParticle)nodes.get(nodes.size()-1), (CircleParticle)_fixedObject,0.5));
					APEngine.addConstraint((SpringConstraint)connections.get(connections.size()-1));
					System.out.println("Created Constraint.");
					
					_gameObject.updatePaintQueue();
					
					_ropeStopped = true;
					
				}	

			}

		}
	   
   }