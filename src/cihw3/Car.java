package cihw3;

import java.util.ArrayList;

import cihw3.Sensor;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Car extends Circle {

	private int ratio = 10;
	private Canvas canvasPane;
	protected Sensor sensor1;
	protected Sensor sensor2;
	protected Sensor sensor3;
	protected double startPointX = 0;
	protected double startPointY = 0;
	protected double angle = 90;
	protected double turnAngle = 0;
	private int startFlag = 0;
	private double x;
	private double y;
	private int count = 0;
	public Car(Canvas canvasPane) {
		this.canvasPane = canvasPane;

		// initial
		this.setX(startPointX);
		this.setY(startPointY);
		
		this.setCenterX(transToCanvasX(this.getX()));
		this.setCenterY(transToCanvasY(this.getY()));
		this.setRadius(3 * ratio);
		this.setStroke(Color.INDIANRED);
		this.setFill(Color.TRANSPARENT);
		this.setStrokeWidth(2);

		// Set sensors
		// Set sensors
		sensor1 = new Sensor((this.getX())+3*Math.cos(Math.toRadians(angle)), this.getY()+3*Math.sin(Math.toRadians(angle)),this.getX(),this.getY());
		sensor2 = new Sensor(this.getX()+(3*Math.cos(Math.toRadians(angle-45))), this.getY()+(3*Math.sin(Math.toRadians(angle-45))),this.getX(),this.getY());
		// - means right
		sensor3 = new Sensor(this.getX()+(3*Math.cos(Math.toRadians(angle+45))), this.getY()+(3*Math.sin(Math.toRadians(angle+45))),this.getX(),this.getY());
		// + means left


	}

	public void tuneCar(Canvas canvasPane, Particle bestParticle) {

		// Calculate turn angle

		double[] distance = { (this.sensor1.getDist()),
				(this.sensor2.getDist()) ,
				(this.sensor3.getDist())};
		
		for(int i=0;i<distance.length;i++){
			if(distance[i] > 33){
				distance[i] = 33;
			}
		}
		
		if (startFlag != 0) {
			double output = bestParticle.calOutput(distance);
//			System.out.println("output :"+output);
			turnAngle = output;
		} else {
//			turnAngle = -2.3;
			startFlag = 1;
		}

//		if(turnAngle < -1){
//			turnAngle -= 2;
//		}

		//
//		 turnAngle *= -1;

		if(count < 7){
			turnAngle= -1.8;
		}
		count++;

		//

		
//		 turnAngle = fuzzy.getTurnAngle();
		this.setX(this.getX()+Math.cos(Math.toRadians(angle + turnAngle)+Math.sin(Math.toRadians(turnAngle)*Math.sin(Math.toRadians(angle)))));
		this.setY(this.getY()+Math.sin(Math.toRadians(angle + turnAngle)-Math.sin(Math.toRadians(turnAngle)*Math.cos(Math.toRadians(angle)))));
		
		// Tune car's coordinate
		this.setCenterX(transToCanvasX(this.getX()));
		this.setCenterY(transToCanvasY(this.getY()));

		// Let sensors know car's coordinate
		setSensorsCarCoordinate(this.getX(), this.getY());

		// Calculate angle with x-axis
		angle = angle - (180 / Math.PI) * Math.asin((2 * Math.sin(Math.toRadians(-1*turnAngle)) / (6)));
		System.out.println(" left : " + distance[2] + " middle :" + distance[0] + " right : " + distance[1]);
		System.out.println("turnAngle : "+turnAngle);
		System.out.println( " now's Angle :"+angle);
		System.out.println("==========================================");

//		if (angle < 0) {
//			angle += 360;
//		}
//		if (angle > 360) {
//			angle %= 360;
//		}

		// Set sensors X and Y
		this.sensor1.setX((this.getX())+3*Math.cos(Math.toRadians(angle)));
		this.sensor1.setY(this.getY()+3*Math.sin(Math.toRadians(angle)));
		Circle r1 = new Circle();
		r1.setCenterX(transToCanvasX(this.sensor1.getX()));
		r1.setCenterY(transToCanvasY(this.sensor1.getY()));
		r1.setRadius(3);
		r1.setStroke(Color.DARKGRAY);
		r1.setFill(Color.RED);

		double angleForS2 = angle-45;
//		if(angleForS2 >360){
//			angleForS2 %= 360;
//		}
		this.sensor2.setX(this.getX()+(3*Math.cos(Math.toRadians(angleForS2))));
		this.sensor2.setY(this.getY()+(3*Math.sin(Math.toRadians(angleForS2))));
		Circle r2 = new Circle();
		r2.setCenterX(transToCanvasX(this.sensor2.getX()));
		r2.setCenterY(transToCanvasY(this.sensor2.getY()));
		r2.setRadius(3);
		r2.setStroke(Color.DARKGRAY);
		r2.setFill(Color.BLUE);

		double angleForS3 = angle+45;
//		if(angleForS3 <0){
//			angleForS3 += 360;
//		}
		this.sensor3.setX(this.getX()+(3*Math.cos(Math.toRadians(angleForS3))));
		this.sensor3.setY(this.getY()+(3*Math.sin(Math.toRadians(angleForS3))));
		Circle r3 = new Circle();
		r3.setCenterX(transToCanvasX(this.sensor3.getX()));
		r3.setCenterY(transToCanvasY(this.sensor3.getY()));
		r3.setRadius(3);
		r3.setStroke(Color.DARKGRAY);
		r3.setFill(Color.GREEN);

		
//		canvasPane.getChildren().add(r1);
//		canvasPane.getChildren().add(r2);
//		canvasPane.getChildren().add(r3);

		
		// Add path on canvas
		addPathOnCanvas(canvasPane);

	}
	public void sliderTuneCar(){
		this.sensor1.setX((this.getX())+3*Math.cos(Math.toRadians(angle)));
		this.sensor1.setY(this.getY()+3*Math.sin(Math.toRadians(angle)));

		double angleForS2 = angle-45;
//		if(angleForS2 <0){
//			angleForS2 += 360;
//		}
		this.sensor2.setX(this.getX()+(3*Math.cos(Math.toRadians(angleForS2))));
		this.sensor2.setY(this.getY()+(3*Math.sin(Math.toRadians(angleForS2))));

		double angleForS3 = angle+45;
//		if(angleForS3 > 360){
//			angleForS3 %= 360;
//		}
		this.sensor3.setX(this.getX()+(3*Math.cos(Math.toRadians(angleForS3))));
		this.sensor3.setY(this.getY()+(3*Math.sin(Math.toRadians(angleForS3))));

		
		setSensorsCarCoordinate(this.getX(),this.getY());
	}

	public void initialSetCar(double coordinateX, double coordinateY) {

		// Tune car's coordinate
		this.setX(transBackX(coordinateX));
		this.setY(transBackY(coordinateY));

		this.setCenterX(transToCanvasX(this.getX()));
		this.setCenterY(transToCanvasY(this.getY()));

		// Let sensors know car's coordinate
		setSensorsCarCoordinate(this.getX(), this.getY());

		// Set sensors X and Y
		this.sensor1.setX((this.getX()) + 3 * Math.cos(Math.toRadians(angle)));
		this.sensor1.setY(this.getY() + 3 * Math.sin(Math.toRadians(angle)));
		double angleForS2 = angle - 45;
		// if(angleForS2 <0){
		// angleForS2 += 360;
		// }
		this.sensor2.setX(this.getX() + (3 * Math.cos(Math.toRadians(angleForS2))));
		this.sensor2.setY(this.getY() + (3 * Math.sin(Math.toRadians(angleForS2))));
		double angleForS3 = angle + 45;
		// if(angleForS3 > 360){
		// angleForS3 %= 360;
		// }
		this.sensor3.setX(this.getX() + (3 * Math.cos(Math.toRadians(angleForS3))));
		this.sensor3.setY(this.getY() + (3 * Math.sin(Math.toRadians(angleForS3))));

		setSensorsCarCoordinate(this.getX(), this.getY());

	}

	public void setSensorsCarCoordinate(double x, double y) {
		this.sensor1.setCarX(x);
		this.sensor2.setCarX(x);
		this.sensor3.setCarX(x);
		this.sensor1.setCarY(y);
		this.sensor2.setCarY(y);
		this.sensor3.setCarY(y);
	}

	public void addPathOnCanvas(Canvas canvasPane) {

		Circle path = new Circle();
		path.setCenterX(transToCanvasX(this.getX()));
		path.setCenterY(transToCanvasY(this.getY()));
		path.setRadius(3);
		path.setStroke(Color.DARKGRAY);
		path.setFill(Color.DARKGRAY);
		canvasPane.getChildren().add(path);

	}

	public void finalTune() {

		// Just move
		this.setCenterY(this.getCenterX() - ratio * 3);
		addPathOnCanvas(canvasPane);
	}

	public double transToCanvasX(double x) {
		double value = (x + 30) * ratio;
		return value;
	}

	public double transToCanvasY(double y) {
		double value = (-y + 52) * ratio;
		return value;
	}

	public double transBackX(double x) {
		double value = (x / ratio) - 30;
		return value;
	}

	public double transBackY(double y) {
		double value = -1 * ((y / ratio) - 52);
		return value;
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

}
