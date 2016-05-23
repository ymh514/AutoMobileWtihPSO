package cihw3;

import java.util.ArrayList;

import cihw3.Canvas;
import javafx.geometry.Point2D;
import javafx.scene.shape.Line;

public class Sensor {

	private double carX;
	private double carY;
	private double x;
	private double y;
	private int closestLineId;
	private double closestLineDist;
	private Point2D[] lineIntersection = new Point2D[8];
	private int ratio = 10;

	public Sensor(double x, double y, double carX, double carY) {
		this.x = x;
		this.y = y;
		this.carX = carX;
		this.carY = carY;
		// TODO Auto-generated constructor stub
	}
	public void setDist(double dist){
		this.closestLineDist = dist;
	}
	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public void setCarX(double carX) {
		this.carX = carX;
	}

	public void setCarY(double carY) {
		this.carY = carY;
	}

	public double getCarX() {
		return this.carX;
	}

	public double getCarY() {
		return this.carY;
	}

	public void findIntersection(ArrayList<Line> tempLine, double[] tempDist, int i) {

		double startX = transBackX(tempLine.get(i).getStartX());
		double startY = transBackY(tempLine.get(i).getStartY());
		double endX = transBackX(tempLine.get(i).getEndX());
		double endY = transBackY(tempLine.get(i).getEndY());

		double intersectionX = ((this.carX - this.getX()) * (startX * endY - endX * startY)
				- (startX - endX) * (this.carX * this.getY() - this.getX() * this.carY))
				/ ((startX - endX) * (this.carY - this.getY()) - (this.carX - this.getX()) * (startY - endY));

		double intersectionY = ((this.carY - this.getY()) * (startX * endY - endX * startY)
				- (this.carX * this.getY() - this.getX() * this.carY) * (startY - endY))
				/ ((this.carY - this.getY()) * (startX - endX) - (this.carX - this.getX()) * (startY - endY));

		this.lineIntersection[i] = new Point2D(intersectionX, intersectionY);

		// Check intersection point in the line range or not
		if (checkLineRange(startX, endX, startY, endY, intersectionX, intersectionY) > 0) {
			double itsVectorX = this.getX() - this.carX;
			double itsVectorY = this.getY() - this.carY;
			double vectorX = intersectionX - this.getX();
			double vectorY = intersectionY - this.getY();

			// 計算向量內積，如為正，則代表此交點為sensor面對方向
			double ans = itsVectorX * vectorX + itsVectorY * vectorY;
			double a = this.x - intersectionX;
			double b = this.y - intersectionY;

			if (ans > 0) {
				tempDist[i] = Math.sqrt(a * a + b * b);
			} else {
				tempDist[i] = 50;
			}

		} else {
			tempDist[i] = 50;
		}

	}

	public int checkLineRange(double lineSX, double lineEX, double lineSY, double lineEY, double x, double y) {

		if (lineSX == lineEX) {
			if (y <= lineEY && y >= lineSY) {
				return 1;
			} else {
				return -1;
			}
		} else {
			if (x >= lineSX && x <= lineEX) {
				return 1;
			} else {
				return -1;
			}

		}
	}

	public void calDistance(Canvas canvasPane) {
		ArrayList<Line> tempLine = new ArrayList<Line>();
		tempLine.add(canvasPane.line1);
		tempLine.add(canvasPane.line2);
		tempLine.add(canvasPane.line3);
		tempLine.add(canvasPane.line4);
		tempLine.add(canvasPane.line5);
		tempLine.add(canvasPane.line6);
		tempLine.add(canvasPane.line7);
		tempLine.add(canvasPane.line8);

		double[] tempDist = new double[8];
		// all line

		for (int i = 0; i < tempLine.size(); i++) {
			findIntersection(tempLine, tempDist, i);
		}

		double smallestDist = Double.MAX_VALUE;
		int smallestId = 0;
		for (int i = 0; i < tempDist.length; i++) {
			if (tempDist[i] < smallestDist) {
				smallestDist = tempDist[i];
				smallestId = i;
			}
		}
		if(smallestId == 7){
			smallestDist = 30;
		}
		this.closestLineId = smallestId;
		this.closestLineDist = smallestDist;

	}
	public int getClosestLineId(){
		return this.closestLineId;
	}
	public double getClosestLineDistance(){
		return this.closestLineDist;
	}
	public double getIntersectionPointX(int i){
		return this.lineIntersection[i].getX();
	}
	public double getIntersectionPointY(int i){
		return this.lineIntersection[i].getY();
	}

	public double getDist() {
		double distInfo = Math.round(this.closestLineDist * 1000.0) / 1000.0;
		double dx = this.carX-this.x;
		double dy = this.carY-this.y;
		double distToCenter=Math.sqrt(dx*dx + dy*dy);
		distInfo += distToCenter;
		return distInfo;
	}
	public double transToCanvasX(double x) {
		double value = (x + 30) * ratio;
		return value;
	}

	public double transToCanvasY(double y) {
		double value = (-y + 52) * ratio;
		return value;
	}
	public double transBackX(double x){
		double value = (x /ratio)-30;
		return value;
	}
	public double transBackY(double y){
		double value = -1*((y /ratio)-52);
		return value;
	}

}
