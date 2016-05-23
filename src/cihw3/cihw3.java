package cihw3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import cihw3.Canvas;
import cihw3.Particle;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class cihw3 extends Application {

	private Canvas canvasPane;
	private Car car;
	private Line sensorLine1;
	private Line sensorLine2;
	private Line sensorLine3;
	private int ratio = 10;
	
	public ArrayList<double[]> inputArray;
	
	
	private int looptimes;
	private int groupSize;
	private double phiIProb;
	private double phiGProb;	
	private double errorLimit;

	private double[] parameters;
	
	private int iteration;
	private Swarm particleSwarm;
		
	private int drawAcelerate = 100;
	
	private double initialAngleValue = 90;
	private Label line1Dist = new Label("Red");
	private Label line2Dist = new Label("Blue");
	private Label line3Dist = new Label("Green");
	private Label angleInfo = new Label("");

	private ArrayList<Particle> bestParticleArray;
	private double bstGeneArrayFitness = Double.MAX_VALUE;
	private double bestParticleArrayAvgError = Double.MAX_VALUE;

	private int finalFlag = 0;
    private ProgressIndicator pi;
    
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("A battle with Computational Intelligence.");

		/*
		 * Initial setting
		 */
		inputArray = new ArrayList<double[]>();
		canvasPane = new Canvas();
		car = new Car(this.canvasPane);
		bestParticleArray = new ArrayList<Particle>();

		BorderPane ciPane = new BorderPane();
		VBox infoBox = new VBox(10);
		Button loadFile = new Button("Load File");
		Button start = new Button("Start");
		Button go = new Button("Go");
		Label looptimesLabel = new Label("Looptimes :");
		Label groupSizeLabel = new Label("Group size :");
		Label phiIProbLabel = new Label("phiI Probability");
		Label phiGProbLabel = new Label("phiG Probability");
		Label errorLimitLabel = new Label("Error Limit");

		TextField looptimesText = new TextField("300");
		TextField groupSizeText = new TextField("500");
		TextField phiIProbText = new TextField("0.3");
		TextField phiGProbText = new TextField("0.7");
		TextField errorLimitText = new TextField("1.2");

		infoBox.setPadding(new Insets(15, 50, 15, 15));
		canvasPane.getChildren().add(car);
		Slider slider = new Slider();
		slider.setPrefSize(180, 30);
		slider.setMin(-270);
		slider.setMax(90);
		slider.setValue(90);
		slider.setShowTickLabels(true);
		slider.setShowTickMarks(true);
		slider.setMajorTickUnit(50);
		slider.setMinorTickCount(5);
		slider.setBlockIncrement(10);
		pi = new ProgressIndicator();
		pi.setPrefSize(60, 60);
		
		Label initialAngle = new Label("Angle value : 90º");
		Label initialAngleSign = new Label("Please slide to start angle :");

		ciPane.setRight(canvasPane);
		ciPane.setLeft(infoBox);
		infoBox.getChildren().addAll(looptimesLabel, looptimesText, groupSizeLabel, groupSizeText, phiIProbLabel,
				phiIProbText, phiGProbLabel, phiGProbText,errorLimitLabel,errorLimitText, loadFile, start, go, initialAngleSign, slider,
				initialAngle, line3Dist, line1Dist, line2Dist, angleInfo,pi);

		
		/*
		 * Set sensor lines
		 */

		sensorLinesSetting();

		slider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				// TODO Auto-generated method stub
				initialAngle.setText("Angle value : " + Math.round((double) newValue * 100.0) / 100.0 + "º");
				initialAngleValue = (double) Math.round((double) newValue * 100.0) / 100.0;
				car.angle = (double) newValue;
				car.sliderTuneCar();
				initialSetSensorsLine();
			}
		});

		loadFile.setOnMouseClicked(event -> {
			inputArray.clear();

			try {
				inputFileChoose(null, loadFile);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("Cancel");
				e.printStackTrace();

			}
			
			// Set all desire angle to negtive, cause in our system turn right is negtive.
			for (int i = 0; i < inputArray.size(); i++) {
				inputArray.get(i)[inputArray.get(0).length - 1] = inputArray.get(i)[inputArray.get(0).length - 1] * -1;
			}
			
		});
		
		Task algo = new Task<Void>() {
		    @Override public Void call() {
		    	
				iteration = 0;

		    	while(true){
		    		
		    		particleSwarm.startAlgo(iteration);

					double afterAlgoFitness = particleSwarm.getAfterAlgoFitness();
					double afterAlgoAvgError = particleSwarm.getAfterAlgoAvgError();

					// one ast
					if (afterAlgoAvgError < bestParticleArrayAvgError) {
						bestParticleArray.add(particleSwarm.getBestGene());
						bstGeneArrayFitness = bestParticleArray.get(bestParticleArray.size() - 1).getFitnessValue();
						bestParticleArrayAvgError = bestParticleArray.get(bestParticleArray.size() - 1).getAvgError();
					}


					String debug = "Iteration : "+iteration+
							"\nAfter Algo Avg Error : "+(double) Math.round(afterAlgoAvgError * 1000) / 1000+
							"\nAfter Algo Fitness : "+(double) Math.round(afterAlgoFitness * 1000) / 1000+
							"\nNow Best Gene Avg Error : "+bestParticleArrayAvgError+
							"\nNow Best Gene Fitness : "+bstGeneArrayFitness;
					System.out.println(debug);
					System.out.println("-------------------------------------");

					if (iteration > looptimes) {
						System.out.println("looptimes break loop");
						iteration = looptimes;
				        updateProgress(iteration, looptimes);
						break;
					}
					if (bestParticleArrayAvgError < errorLimit) {
						System.out.println("good error break");
						iteration = looptimes;
				        updateProgress(iteration, looptimes);
						break;
					}

					iteration++;
			        updateProgress(iteration, looptimes);

		    	}
		        return null;
		    }
		};
		
		
		pi.progressProperty().bind(algo.progressProperty());

		start.setOnMouseClicked(event -> {
			
			this.looptimes = Integer.parseInt(looptimesText.getText());
			this.groupSize = Integer.parseInt(groupSizeText.getText());
			this.phiIProb = Double.parseDouble(phiIProbText.getText());
			this.phiGProb = Double.parseDouble(phiGProbText.getText());
			this.errorLimit = Double.parseDouble(errorLimitText.getText());

			this.parameters = new double[4];
			this.parameters[0] = this.looptimes;
			this.parameters[1] = this.groupSize;
			this.parameters[2] = this.phiIProb;
			this.parameters[3] = this.phiGProb;
			
			this.particleSwarm = new Swarm(this.parameters,this.inputArray);
						
			new Thread(algo).start();			

		});
		
		
		Task moveCar = new Task<Void>() {
		    @Override public Void call() {
		    
		    	while(true){
					if (car.getX() > 18 && car.getY() > 37) {
						finalFlag = 1;
					}
		
					try {
						Thread.sleep(drawAcelerate);
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								
//								printCurrentThread();
								
								if(finalFlag == 1){
									line1Dist.setText("done");
									line2Dist.setText("done");
									line3Dist.setText("done");
									angleInfo.setText("done");
									sensorLine1.setVisible(false);
									sensorLine2.setVisible(false);
									sensorLine3.setVisible(false);
								}
								else{
									car.tuneCar(canvasPane, bestParticleArray.get(bestParticleArray.size() - 1));
									initialSetSensorsLine();
								}
							}
						});
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (finalFlag == 1) {
						break;
					} 	
		    	}
		        return null;
		    }
		};
		
		go.setOnMouseClicked(event -> {
			new Thread(moveCar).start();			


		});

		canvasPane.setOnMouseClicked(event -> {
			car.initialSetCar(event.getX(), event.getY());
			initialSetSensorsLine();
		});

		Scene primaryScene = new Scene(ciPane);
		primaryStage.setScene(primaryScene);
		primaryStage.setResizable(false);
		primaryStage.show();

	}

	public void initialSetSensorsLine() {
		sensorLine1.setStartX(transToCanvasX(car.getX()));
		sensorLine1.setStartY(transToCanvasY(car.getY()));
		sensorLine1.setEndX(transToCanvasX(car.sensor1.getX()));
		sensorLine1.setEndY(transToCanvasY(car.sensor1.getY()));

		sensorLine2.setStartX(transToCanvasX(car.getX()));
		sensorLine2.setStartY(transToCanvasY(car.getY()));
		sensorLine2.setEndX(transToCanvasX(car.sensor2.getX()));
		sensorLine2.setEndY(transToCanvasY(car.sensor2.getY()));

		sensorLine3.setStartX(transToCanvasX(car.getX()));
		sensorLine3.setStartY(transToCanvasY(car.getY()));
		sensorLine3.setEndX(transToCanvasX(car.sensor3.getX()));
		sensorLine3.setEndY(transToCanvasY(car.sensor3.getY()));

		// Calculate the distance with walls
		car.sensor1.calDistance(canvasPane);
		car.sensor2.calDistance(canvasPane);
		car.sensor3.calDistance(canvasPane);

		// Set showing information
		line1Dist.setText("Red Line :" + (double)Math.round((double)car.sensor1.getDist()*1000)/1000);
		line2Dist.setText("Blue Line :" + (double)Math.round((double)car.sensor2.getDist()*1000)/1000);
		line3Dist.setText("Green Line :" + (double)Math.round((double)car.sensor3.getDist()*1000)/1000);
		angleInfo.setText("Angle with x-axis : " + Math.round(car.angle * 1000.0) / 1000.0 + "º");

		// Set sensor lines
		int sensor1ClosetId = car.sensor1.getClosestLineId();
		int sensor2ClosetId = car.sensor2.getClosestLineId();
		int sensor3ClosetId = car.sensor3.getClosestLineId();

		sensorLine1.setEndX(transToCanvasX(car.sensor1.getIntersectionPointX(sensor1ClosetId)));
		sensorLine1.setEndY(transToCanvasY(car.sensor1.getIntersectionPointY(sensor1ClosetId)));
		sensorLine2.setEndX(transToCanvasX(car.sensor2.getIntersectionPointX(sensor2ClosetId)));
		sensorLine2.setEndY(transToCanvasY(car.sensor2.getIntersectionPointY(sensor2ClosetId)));
		sensorLine3.setEndX(transToCanvasX(car.sensor3.getIntersectionPointX(sensor3ClosetId)));
		sensorLine3.setEndY(transToCanvasY(car.sensor3.getIntersectionPointY(sensor3ClosetId)));
	}

	public void printCurrentThread() {
		System.out.println("************************");
		System.out.println(Thread.currentThread());
		System.out.println("************************");

	}

	public void sensorLinesSetting() {
		sensorLine1 = new Line();
		sensorLine1.setStartX(transToCanvasX(car.getX()));
		sensorLine1.setStartY(transToCanvasY(car.getY()));
		sensorLine1.setEndX(transToCanvasX(car.sensor1.getX()));
		sensorLine1.setEndY(transToCanvasY(car.sensor1.getY()));
		// sensorLine1.startXProperty().bind(car.centerXProperty());
		// sensorLine1.startYProperty().bind(car.centerYProperty());
		sensorLine1.setStroke(Color.DARKRED);

		sensorLine2 = new Line();
		sensorLine2.setStartX(transToCanvasX(car.getX()));
		sensorLine2.setStartY(transToCanvasY(car.getY()));
		sensorLine2.setEndX(transToCanvasX(car.sensor2.getX()));
		sensorLine2.setEndY(transToCanvasY(car.sensor2.getY()));
		// sensorLine2.startXProperty().bind(car.centerXProperty());
		// sensorLine2.startYProperty().bind(car.centerYProperty());
		sensorLine2.setStroke(Color.DARKBLUE);

		sensorLine3 = new Line();
		sensorLine3.setStartX(transToCanvasX(car.getX()));
		sensorLine3.setStartY(transToCanvasY(car.getY()));
		sensorLine3.setEndX(transToCanvasX(car.sensor3.getX()));
		sensorLine3.setEndY(transToCanvasY(car.sensor3.getY()));
		// sensorLine3.startXProperty().bind(car.centerXProperty());
		// sensorLine3.startYProperty().bind(car.centerYProperty());
		sensorLine3.setStroke(Color.DARKGREEN);

		sensorLine1.setVisible(true);
		sensorLine2.setVisible(true);
		sensorLine3.setVisible(true);

		canvasPane.getChildren().addAll(sensorLine1, sensorLine2, sensorLine3);

	}


	public void inputFileChoose(String[] args, Button loadFile) throws IOException {
		/*
		 * show a file stage for choose file
		 */

		Stage fileStage = new Stage();
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.setInitialDirectory(new File("src/datasetWithoutPosition"));

		File file = fileChooser.showOpenDialog(fileStage);
		// System.out.println(file);

		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);// 在br.ready反查輸入串流的狀況是否有資料

		loadFile.setText(file.getName());

		String txt;
		while ((txt = br.readLine()) != null) {
			/*
			 * If there is space before split(), it will cause the error So, we
			 * could to use trim() to remove the space at the beginning and the
			 * end. Then split the result, which doesn't include the space at
			 * the beginning and the end. "\\s+" would match any of space, as
			 * you don't have to consider the number of space in the string
			 */
			String[] token = txt.trim().split("\\s+");// <-----背起來
			// String[] token = txt.split(" ");//<-----original split
			double[] token2 = new double[token.length];// 宣告float[]

			try {
				for (int i = 0; i < token.length; i++) {
					token2[i] = Float.parseFloat(token[i]);
				} // 把token(string)轉乘token2(float)
				inputArray.add(token2);// 把txt裡面內容先切割過在都讀進array內
			} catch (NumberFormatException ex) {
				System.out.println("Sorry Error...");
			}
		}
		fr.close();// 關閉檔案
	}

	public void printArrayData(ArrayList<double[]> showArray) {

		for (int i = 0; i < showArray.size(); i++) {
			for (int j = 0; j < showArray.get(i).length; j++) {
				System.out.print(showArray.get(i)[j] + "\t");
			}
			System.out.println("");
		}
		System.out.println("");
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

	public static void main(String[] args) {
		launch(args);
	}

}
