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
	
	public class Vector {
		
		public double x;
		public double y;
	
	
		public Vector(double px, double py) {
			x = px;
			y = py;
		}
		
		
		public void setTo(double px, double py) {
			x = px;
			y = py;
		}
		
		
		public void copy(Vector v) {
			x = v.x;
			y = v.y;
		}
	
	
		public double dot(Vector v) {
			return x * v.x + y * v.y;
		}
		
		
		public double cross(Vector v) {
			return x * v.y - y * v.x;
		}
		
	
		public Vector plus(Vector v) {
			return new Vector(x + v.x, y + v.y); 
		}
	
		
		public Vector plusEquals(Vector v) {
			x += v.x;
			y += v.y;
			return this;
		}
		
		
		public Vector minus(Vector v) {
			return new Vector(x - v.x, y - v.y);    
		}
	
	
		public Vector minusEquals(Vector v) {
			x -= v.x;
			y -= v.y;
			return this;
		}
	
	
		public Vector mult(double s) {
			return new Vector(x * s, y * s);
		}
	
	
		public Vector multEquals(double s) {
			x *= s;
			y *= s;
			return this;
		}
	
	
		public Vector times(Vector v) {
			return new Vector(x * v.x, y * v.y);
		}
		
		
		public Vector divEquals(double s) {
			if (s == 0) s = 0.0001;
			x /= s;
			y /= s;
			return this;
		}
		
		
		public double magnitude() {
			return Math.sqrt(x * x + y * y);
		}

		
		public double distance(Vector v) {
			Vector delta = this.minus(v);
			return delta.magnitude();
		}

	
		public Vector normalize() {
			 double m = magnitude();
			 if (m == 0) m = 0.0001;
			 return mult(1 / m);
		}
		
				
		public String toString() {
			return (x + " : " + y);
		}
	}
