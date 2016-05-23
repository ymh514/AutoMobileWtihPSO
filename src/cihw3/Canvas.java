package cihw3;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

public class Canvas extends Pane {

	protected int ratio = 10;
	protected Line line1;
	protected Line line2;
	protected Line line3;
	protected Line line4;
	protected Line line5;
	protected Line line6;
	protected Line line7;
	protected Line line8;

	public Canvas() {

		this.setBackground(new Background(new BackgroundFill(Color.GAINSBORO, null, null)));
		this.setPrefSize(800, 800);

		line1 = new Line();
		line1.setStartX(transToCanvasX(-6));
		line1.setStartY(transToCanvasY(0));
		line1.setEndX(transToCanvasX(6));
		line1.setEndY(transToCanvasY(0));
		line1.setStroke(Color.BLACK);
		line1.setStrokeWidth(3);
		this.getChildren().add(line1);

		line2 = new Line();
		line2.setStartX(transToCanvasX(-6));
		line2.setStartY(transToCanvasY(0));
		line2.setEndX(transToCanvasX(-6));
		line2.setEndY(transToCanvasY(22));
		line2.setStroke(Color.GRAY);
		line2.setStrokeWidth(3);
		this.getChildren().add(line2);

		line3 = new Line();
		line3.setStartX(transToCanvasX(6));
		line3.setStartY(transToCanvasY(0));
		line3.setEndX(transToCanvasX(6));
		line3.setEndY(transToCanvasY(10));
		line3.setStroke(Color.GRAY);
		line3.setStrokeWidth(3);

		this.getChildren().add(line3);

		line4 = new Line();
		line4.setStartX(transToCanvasX(-6));
		line4.setStartY(transToCanvasY(22));
		line4.setEndX(transToCanvasX(18));
		line4.setEndY(transToCanvasY(22));
		line4.setStroke(Color.GRAY);
		line4.setStrokeWidth(3);

		this.getChildren().add(line4);

		line5 = new Line();
		line5.setStartX(transToCanvasX(6));
		line5.setStartY(transToCanvasY(10));
		line5.setEndX(transToCanvasX(30));
		line5.setEndY(transToCanvasY(10));
		line5.setStroke(Color.GRAY);
		line5.setStrokeWidth(3);

		this.getChildren().add(line5);

		line6 = new Line();
		line6.setStartX(transToCanvasX(18));
		line6.setStartY(transToCanvasY(22));
		line6.setEndX(transToCanvasX(18));
		line6.setEndY(transToCanvasY(37));
		line6.setStroke(Color.GRAY);
		line6.setStrokeWidth(3);

		this.getChildren().add(line6);

		line7 = new Line();
		line7.setStartX(transToCanvasX(30));
		line7.setStartY(transToCanvasY(10));
		line7.setEndX(transToCanvasX(30));
		line7.setEndY(transToCanvasY(37));
		line7.setStroke(Color.GRAY);
		line7.setStrokeWidth(3);
		this.getChildren().add(line7);

		line8 = new Line();
		line8.setStartX(transToCanvasX(18));
		line8.setStartY(transToCanvasY(37));
		line8.setEndX(transToCanvasX(30));
		line8.setEndY(transToCanvasY(37));
		line8.setStroke(Color.GRAY);
		line8.setStrokeWidth(3);
		this.getChildren().add(line8);
	}

	public void rePaint() {

		this.getChildren().clear();

		line1 = new Line();
		line1.setStartX(transToCanvasX(-6));
		line1.setStartY(transToCanvasY(0));
		line1.setEndX(transToCanvasX(6));
		line1.setEndY(transToCanvasY(0));
		line1.setStroke(Color.BLACK);
		line1.setStrokeWidth(3);
		this.getChildren().add(line1);

		line2 = new Line();
		line2.setStartX(transToCanvasX(-6));
		line2.setStartY(transToCanvasY(0));
		line2.setEndX(transToCanvasX(-6));
		line2.setEndY(transToCanvasY(22));
		line2.setStroke(Color.GRAY);
		line2.setStrokeWidth(3);
		this.getChildren().add(line2);

		line3 = new Line();
		line3.setStartX(transToCanvasX(6));
		line3.setStartY(transToCanvasY(0));
		line3.setEndX(transToCanvasX(6));
		line3.setEndY(transToCanvasY(10));
		line3.setStroke(Color.GRAY);
		line3.setStrokeWidth(3);

		this.getChildren().add(line3);

		line4 = new Line();
		line4.setStartX(transToCanvasX(-6));
		line4.setStartY(transToCanvasY(22));
		line4.setEndX(transToCanvasX(18));
		line4.setEndY(transToCanvasY(22));
		line4.setStroke(Color.GRAY);
		line4.setStrokeWidth(3);

		this.getChildren().add(line4);

		line5 = new Line();
		line5.setStartX(transToCanvasX(6));
		line5.setStartY(transToCanvasY(10));
		line5.setEndX(transToCanvasX(30));
		line5.setEndY(transToCanvasY(10));
		line5.setStroke(Color.GRAY);
		line5.setStrokeWidth(3);

		this.getChildren().add(line5);

		line6 = new Line();
		line6.setStartX(transToCanvasX(18));
		line6.setStartY(transToCanvasY(22));
		line6.setEndX(transToCanvasX(18));
		line6.setEndY(transToCanvasY(37));
		line6.setStroke(Color.GRAY);
		line6.setStrokeWidth(3);

		this.getChildren().add(line6);

		line7 = new Line();
		line7.setStartX(transToCanvasX(30));
		line7.setStartY(transToCanvasY(10));
		line7.setEndX(transToCanvasX(30));
		line7.setEndY(transToCanvasY(37));
		line7.setStroke(Color.GRAY);
		line7.setStrokeWidth(3);
		this.getChildren().add(line7);

		line8 = new Line();
		line8.setStartX(transToCanvasX(18));
		line8.setStartY(transToCanvasY(37));
		line8.setEndX(transToCanvasX(30));
		line8.setEndY(transToCanvasY(37));
		line8.setStroke(Color.GRAY);
		line8.setStrokeWidth(3);
		this.getChildren().add(line8);
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

