package com.forschungsprojekt.spring_backend.localization;

public class Circle {
    private double locationX;
	private double locationY;
	private double radius;
	
	public Circle(double x,double y, double r) {
		this.locationX = x;
		this.locationY = y;
		this.radius = r;
	}
	
	double getX() {
		return locationX;
	}
	double getY() {
		return locationY;
	}
	double getRadius() {
		return radius;
	}
	
	void setRadius(double r) {
		this.radius =r;
	}
	
	boolean intersect(Boundary b) {
		double boundaryX = (b.getxMax() + b.getxMin())/2;
		double boundaryY = (b.getyMax() + b.getyMin())/2;
		double boundaryWidth = b.getxMax() - b.getxMin();
		double boundaryHeight = b.getyMax() - b.getyMin();
		double circleDistanceX = Math.abs(this.locationX - boundaryX);
		double circleDistanceY = Math.abs(this.locationY - boundaryY);
		
		if(circleDistanceX > this.radius + boundaryWidth/2) {
			return false;
		}
		if(circleDistanceY > this.radius + boundaryHeight/2) {
			return false;
		}
		
		if(circleDistanceX <= boundaryWidth/2) {
			return true;
		}
		if(circleDistanceY <=boundaryHeight/2) {
			return true;
		}
		
		double cornerDistance_sq = (circleDistanceX - boundaryWidth/2)*(circleDistanceX - boundaryWidth/2) + 
				(circleDistanceY - boundaryHeight/2)*(circleDistanceY - boundaryHeight/2);
		return (cornerDistance_sq <= this.radius*this.radius);
	}
}
