package com.forschungsprojekt.spring_backend.localization;
public class Boundary {
	private double xMin;
	private double yMin;
	private double xMax;
	private double yMax;

	public Boundary(double xMin, double yMin, double xMax, double yMax) {
		super();
		this.xMin = xMin;
		this.yMin = yMin;
		this.xMax = xMax;
		this.yMax = yMax;
	}
	
	/**
	 * Test if given point (x,y) in Range of the Boundary
	 * 
	 * @param latitude
	 * @param longtiude
	 * @return
	 */
	boolean inRange(double latitude, double longtiude) {
		return (latitude >= this.getxMin() && latitude <= this.getxMax()
				&& longtiude >= this.getyMin() && longtiude <= this.getyMax());
	}

	/**
	 * @return the xMin
	 */
	double getxMin() {
		return xMin;
	}

	/**
	 * @return the yMin
	 */
	double getyMin() {
		return yMin;
	}

	/**
	 * @return the xMax
	 */
	double getxMax() {
		return xMax;
	}

	/**
	 * @return the yMax
	 */
	double getyMax() {
		return yMax;
	}
	
	

}