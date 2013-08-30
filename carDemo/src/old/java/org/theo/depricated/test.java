package org.theo.depricated;

import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;

import org.cove.ape.APEngine;
import org.cove.ape.RectangleParticle;
import org.cove.ape.Vector;
import org.cove.ape.WheelParticle;
import org.theo.aftokinito.carDemo;

public class test extends carDemo {
	/**
	 * 
	 */
	private BufferStrategy strategy;
	
	private static final long serialVersionUID = 1L;

	private WheelParticle wheelParticleA;
	private WheelParticle wheelParticleB;

	public test() {
		
	}

	public void initWorld2() {
        
		APEngine.init((double)1/3);
		APEngine.setCollisionResponseMode(APEngine.SELECTIVE);
		APEngine.addMasslessForce(new Vector(0,3));
		
		// surfaces
		//RectangleParticle floor = new RectangleParticle(400,400,300,50,0,true,1,0.3,0);
		//APEngine.addParticle(floor);

		RectangleParticle square = new RectangleParticle(250,250,20,20,0,false,1,0.3,0);
		square.setMagnetic(30);
		APEngine.addParticle(square);

		/*RectangleParticle square2 = new RectangleParticle(260,330,20,60,0,false,1,0.3,0);
		APEngine.addParticle(square2);

		RectangleParticle square3 = new RectangleParticle(100,400,400,20,0,true,1,0.3,0);
		APEngine.addParticle(square3);
		
		// car 
		wheelParticleA = new WheelParticle(260,10,20,false,1,0.3,0,1);
		wheelParticleA.setMass(1);
		APEngine.addParticle(wheelParticleA);
		
		wheelParticleB = new WheelParticle(340,10,20,false,1,0.3,0,1);
		wheelParticleB.setMass(1);
		APEngine.addParticle(wheelParticleB);*/
		
		paintQueue = APEngine.getAll();
	}
	
	
	public void updateWorld(Graphics2D g) {
		APEngine.step();
	}      
	
	public static void main(String[] args) {
		test inv = new test();
		inv.game();
	}	
}


