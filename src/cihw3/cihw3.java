package cihw3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import cihw3.Canvas;
import cihw3.Gene;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class cihw3 extends Application {

	private Canvas canvasPane;
	private Car car;
	private Line sensorLine1;
	private Line sensorLine2;
	private Line sensorLine3;
	private double startPointX = 0;
	private double startPointY = 0;
	private int ratio = 10;
	public ArrayList<double[]> inputArray;
	private ArrayList<Gene> geneArray;
	private int looptimes;
	private int groupSize;
	private double crossoverProb;
	private double mutationProb;
	private ArrayList<ArrayList<double[]>> geneInfoArray;
	private ArrayList<ArrayList<double[]>> pool;
	private int iteration;
	private double avgError;
	private int bstErrorNo;
	private double bstErrorValue;
	private double errorLimit = 1.5;
	private int drawAcelerate = 100;
	private double initialAngleValue = 90;
	private Label line1Dist = new Label("Red");
	private Label line2Dist = new Label("Blue");
	private Label line3Dist = new Label("Green");
	private Label angleInfo = new Label("");
	
	private ArrayList<Gene> bestGene;
	private double bstGeneFit = Double.MAX_VALUE;
	private double bstGeneAvge = Double.MAX_VALUE;
	
	private ArrayList<Gene> storeBstGene;
	private ArrayList<ArrayList<double[]>> bestInfoArray;

	private ArrayList<double[]> weightArray;

	private int finalFlag = 0;

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
		
		bestGene = new ArrayList<Gene>();
		
		BorderPane ciPane = new BorderPane();
		VBox infoBox = new VBox(10);
		Button loadFile = new Button("Load File");
		Button reset = new Button("Reset");
		Button start = new Button("Start");
		Button go = new Button("Go");
		Button writeTest = new Button("write");
		Button loadWeight = new Button("Load");
		Label looptimesLabel = new Label("Looptimes :");
		Label groupSizeLabel = new Label("Group size :");
		Label crossoverProbLabel = new Label("Crossover Probability");
		Label mutationProbLabel = new Label("Mutation Probability");
		TextField looptimesText = new TextField("1000");
		TextField groupSizeText = new TextField("500");
		TextField crossoverProbText = new TextField("0.6");
		TextField mutationProbText = new TextField("0.2");

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

		Label initialAngle = new Label("Angle value : 90º");
		Label initialAngleSign = new Label("Please slide to start angle :");

		ciPane.setRight(canvasPane);
		ciPane.setLeft(infoBox);
		infoBox.getChildren().addAll(loadFile, reset,loadWeight, looptimesLabel, looptimesText, groupSizeLabel, groupSizeText,
				crossoverProbLabel, crossoverProbText, mutationProbLabel, mutationProbText, start, go, initialAngleSign,
				slider, initialAngle, line3Dist, line1Dist, line2Dist, angleInfo,writeTest);

		/*
		 * Set sensor lines
		 */

		sensorLinesSetting();

		
		
		writeTest.setOnMouseClicked(evnet ->{
//			String pathName = "/Users/Terry/Desktop/train.txt"; 
			String pathName = "src/train/train.txt"; 

            File output = new File(pathName); // 要读取以上路径的input。txt文件  
            try {
            	output.createNewFile();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("write error");
				e.printStackTrace();
			}
            try {
				BufferedWriter out = new BufferedWriter(new FileWriter(output));
				
								
				int halfBest = bestInfoArray.size() / 2;
//				int halfBest = 50;
				for(int i=0;i<halfBest;i++){
					for(int x=0;x<bestInfoArray.get(i).size();x++){
						for(int y=0;y<bestInfoArray.get(i).get(x).length;y++){
							double temp = bestInfoArray.get(i).get(x)[y];
							out.write((double)Math.round(temp*10000000)/10000000+"	");
						}
					}
					out.newLine();
				}
				
	            out.flush();   
	            out.close();   
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		});
		
		
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
			for(int i=0;i<inputArray.size();i++){
//				for(int j=0;j<inputArray.get(i).length;j++){
					inputArray.get(i)[inputArray.get(0).length-1] = inputArray.get(i)[inputArray.get(0).length-1]*-1;
//				}
			}

			geneArray = new ArrayList<Gene>();
			geneInfoArray = new ArrayList<ArrayList<double[]>>();
			pool = new ArrayList<ArrayList<double[]>>();

			this.looptimes = Integer.parseInt(looptimesText.getText());
			this.groupSize = Integer.parseInt(groupSizeText.getText());
			this.crossoverProb = Double.parseDouble(crossoverProbText.getText());
			this.mutationProb = Double.parseDouble(mutationProbText.getText());


			for (int i = 0; i < groupSize; i++) {
				Gene tempGene = new Gene();
				geneArray.add(tempGene);
			}

			// inputDataNormalize();
			// drawPath(inputArray);
		});
		loadWeight.setOnMouseClicked(event ->{
			weightArray = new ArrayList<double[]>();
			try {
				weightChoose(null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("Cancel");
				e.printStackTrace();
			}
			for(int i=0;i<weightArray.size();i++){
				ArrayList<double[]> tempArray = new ArrayList<double[]>();
				int neuron = 9;
				double[] weight = new double[neuron];
				double[] theta = new double[1];
				double[] sigma = new double[neuron];
				double[] mean = new double[3 * neuron];
				
				int count = 0;
				theta[0] = weightArray.get(i)[count];
				count++;
				for (int a = 0; a < neuron; a++) {
					weight[a] = weightArray.get(i)[count];
					count++;
				}
				
				for (int b = 0; b < mean.length; b++) {
					mean[b] = weightArray.get(i)[count];
					count++;
				}
				for (int a = 0; a < neuron; a++) {
					sigma[a] = weightArray.get(i)[count];
					count++;
				}

				tempArray.add(theta);
				tempArray.add(weight);
				tempArray.add(mean);
				tempArray.add(sigma);
				
				geneArray.get(i).updateGeneInfo(tempArray);
			}
			

			
//			printArrayData(weightArray);
		});
		start.setOnMouseClicked(event -> {
			iteration = 0;
			avgError = Double.MAX_VALUE;
			
			// generate gene
			
			double[] fitnessFunc = new double[geneArray.size()];
			double[] bstError = new double[geneArray.size()];

			while (true) {

				for(int i=0;i<fitnessFunc.length;i++){
					fitnessFunc[i] = 0;
				}
				for(int i=0;i<bstError.length;i++){
					bstError[i] = 0;
				}

				bstErrorNo = 0;
				bstErrorValue = Double.MAX_VALUE;
				avgError = 0;

				for (int i = 0; i < geneArray.size(); i++) {

					for (int j = 0; j < inputArray.size(); j++) {
						double[] distance = new double[3];
						double desire = inputArray.get(j)[inputArray.get(j).length - 1];

						distance[0] = inputArray.get(j)[0];
						distance[1] = inputArray.get(j)[1];
						distance[2] = inputArray.get(j)[2];

						
						double output = geneArray.get(i).calOutput(distance);

						double errorTemp = Math.pow((desire - output), 2);
						double avgETemp = Math.abs(desire - output);
						fitnessFunc[i] += errorTemp;
						bstError[i] += avgETemp;
					}

					fitnessFunc[i] /= 2;
					bstError[i] /= inputArray.size();
					geneArray.get(i).setFitnessValue(fitnessFunc[i]);
					geneArray.get(i).setAvgError(bstError[i]);

				}
				
				// here's fitness function is smaller => better
				// seems wrong;
				
				
				rankSort();
				
				// store sortedGene back to geneArray
				storeBstGene = new ArrayList<Gene>();
				
				for(int i=0;i<geneArray.size();i++){
					for(int j=0;j<geneArray.size();j++){
						if(i == geneArray.get(j).getFitnessRank()){
							storeBstGene.add(geneArray.get(j));
						}
					}					
				}
				bestInfoArray = new ArrayList<ArrayList<double[]>>();
				for(int i=0;i<storeBstGene.size();i++){
					bestInfoArray.add(storeBstGene.get(i).getGeneInfo());
				}
				
				geneArray.clear();
				for(int i=0;i<storeBstGene.size();i++){
					geneArray.add(storeBstGene.get(i));
				}

				geneInfoArray = new ArrayList<ArrayList<double[]>>();
				for(int i=0;i<geneArray.size();i++){
					geneInfoArray.add(geneArray.get(i).getGeneInfo());
				}


				int bestFitnessIndex = 0;
				double bestFitness = geneArray.get(bestFitnessIndex).getFitnessValue();
				double bestAvgError = geneArray.get(bestFitnessIndex).getAvgError();
				
				avgError = bestAvgError;
				bstErrorNo = bestFitnessIndex;
				
				
//				System.out.println(geneArray.size());
//				System.out.println("-------------------------------------");
//				for(int i=0;i<geneArray.size();i++){
//					System.out.println(geneArray.get(i).getFitnessValue());
//				}
//				System.out.println("-------------------------------------");

				
								
				// one ast
				if(bestAvgError < bstGeneAvge ){
					bestGene.add(storeBstGene.get(bestFitnessIndex));
					bstGeneFit = bestGene.get(bestGene.size()-1).getFitnessValue();
					bstGeneAvge = bestGene.get(bestGene.size()-1).getAvgError();
				}
				
				System.out.println(iteration+" avg: "+(double)Math.round(bestAvgError*1000)/1000+" BstG now :#"+bestFitnessIndex+" fitness :"+(double)Math.round(bestFitness*1000)/1000);
				System.out.println("Now Best Gene avg :"+bstGeneAvge+" fit:"+bstGeneFit);
				System.out.println("-------------------------------------");

				reproduction();

				crossover();

				mutation();

				for (int i = 0; i < geneArray.size(); i++) {
					geneArray.get(i).updateGeneInfo(geneInfoArray.get(i));
				}
				

				if (iteration > looptimes) {
					System.out.println("looptimes break loop");
					break;
				}
				if (bstGeneAvge < errorLimit) {
					System.out.println("good error break");
					break;
				}

				iteration++;
			}
		});

		go.setOnMouseClicked(event -> {
			new Thread() {
				public void run() {
					while (true) {
						try {
							// Main thread sleep
							Thread.sleep(drawAcelerate);

							Platform.runLater(new Runnable() {
								// GUI update by javafx thread
								@Override
								public void run() {
									// The function for the final round
									if(car.getX()>18 && car.getY()>37){
										System.out.println("!!!!!!!!!!!");
//										sensorLine1.setVisible(false);
//										sensorLine2.setVisible(false);
//										sensorLine3.setVisible(false);

										finalFlag = 1;
									}

									if(finalFlag == 1){
										
									}
									else{
										// Tune car's position and angle
										car.tuneCar(canvasPane, bestGene.get(bestGene.size()-1));
//										car.tuneCar(canvasPane, storeBstGene.get(0));

										initialSetSensorsLine();
									}

								}
							});

						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						if(finalFlag == 1){
//							sensorLine1.setVisible(false);
//							sensorLine2.setVisible(false);
//							sensorLine3.setVisible(false);

							break;
						}
					}
				}
			}.start();

		});
		reset.setOnMouseClicked(event -> {
			canvasPane.rePaint();
			loadFile.setText("Load File");
			inputArray.clear();
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

	public void rankSort() {
		
		double[] readyToSort = new double[geneArray.size()];
		int[] sortedRank = new int[geneArray.size()];
		
		for(int i=0;i<geneArray.size();i++){
			readyToSort[i] = geneArray.get(i).getFitnessValue();
		}

		Arrays.sort(readyToSort);
		
		double[] used = new double[geneArray.size()];
		for (int i = 0; i < used.length; i++) {
			used[i] = Double.MAX_VALUE;
		}
		
		int count = 0;
		f1: for (int i = 0; i < readyToSort.length; i++) {
			f2: for (int j = 0; j < geneArray.size(); j++) {
				if (count == 0) {
					if (readyToSort[i] == geneArray.get(j).getFitnessValue()) {
						used[count] = j;
						sortedRank[j] = i;
						count++;
						break f2;
					}
				} else {
					if (readyToSort[i] == geneArray.get(j).getFitnessValue()) {
						int findFlag = 0;
						f3: for (int k = 0; k < used.length; k++) {
							if (used[k] == j) {
								findFlag = 1;
								break f3;
							}
						}
						if (findFlag != 1) {
							used[count] = j;
							sortedRank[j] = i;
							count++;
							break f2;
						}
					}
				}
			}
		}
		
		for(int i=0;i<sortedRank.length;i++){
			geneArray.get(i).setFitnessRank(sortedRank[i]);
		}
		
	}

	public void reproduction() {

		pool = new ArrayList<ArrayList<double[]>>();
				
		ArrayList<ArrayList<double[]>> nPool = new ArrayList<ArrayList<double[]>>();
		
		int getHalf = geneInfoArray.size() /2;

		for(int i=0;i<geneInfoArray.size();i++){
			nPool.add(geneInfoArray.get(i));
		}
		
		for(int i=0;i<getHalf;i++){
			pool.add(geneInfoArray.get(i));
		}
		
		int getEighty = geneInfoArray.size() *  75 / 100; 
//		int getEighty = geneInfoArray.size(); 

		for(int i=getHalf;i<getEighty;i++){
			int amount = geneArray.size() -1;
			int r1 = i-getHalf; // set back to 0
			int r2 = (int) Math.round(Math.random() * amount);
			
			if (r1 == r2) {
				r2 = (int) Math.round(Math.random() * amount);
			}
			double rj1 = geneArray.get(r1).getFitnessValue();
			double rj2 = geneArray.get(r2).getFitnessValue();

			if(rj1 <= rj2){

				for (int j = 0; j < nPool.get(r1).size(); j++) {
					for (int k = 0; k < nPool.get(r1).get(j).length; k++) {
						Random rand = new Random();
						double temp = 0;

						if (Math.random() > 0.5) {
							temp = (rand.nextFloat() + 0f);
						} else {
							temp = (rand.nextFloat() - 1f);
						}

						temp = temp * (1-iteration/looptimes);

						if (j == 0) {

							double judge = nPool.get(r1).get(j)[k] + temp;
							if (judge > 1 || judge < 0) {
								// complete reproduction
							} else {
								// add some noise
								nPool.get(r1).get(j)[k] = judge;
							}

						} else if (j == 1) {
							double judge = nPool.get(r1).get(j)[k] + temp;
							if (judge > 40 || judge < -40) {
								// complete reproduction
							} else {
								// add some noise
								nPool.get(r1).get(j)[k] = judge;
							}

						} else if (j == 2) {
							double judge = nPool.get(r1).get(j)[k] + temp;
							if (judge > 30 || judge < 0) {
								// complete reproduction
							} else {
								// add some noise
								nPool.get(r1).get(j)[k] = judge;
							}

						} else {
							double judge = nPool.get(r1).get(j)[k] + temp;
							if (judge > 10 || judge < 0) {
								// complete reproduction
							} else {
								// add some noise
								nPool.get(r1).get(j)[k] = judge;
							}

						}
					}
				}

				pool.add(nPool.get(r1));
			}
			else{

				for (int j = 0; j < nPool.get(r2).size(); j++) {
					for (int k = 0; k < nPool.get(r2).get(j).length; k++) {
						Random rand = new Random();
						double temp = 0;

						if (Math.random() > 0.5) {
							temp = (rand.nextFloat() + 0f);
						} else {
							temp = (rand.nextFloat() - 1f);
						}

						temp = temp * (1-iteration/looptimes);

						if (j == 0) {

							double judge = nPool.get(r2).get(j)[k] + temp;
							if (judge > 1 || judge < 0) {
								// complete reproduction
							} else {
								// add some noise
								nPool.get(r2).get(j)[k] = judge;
							}

						} else if (j == 1) {
							double judge = nPool.get(r2).get(j)[k] + temp;
							if (judge > 40 || judge < -40) {
								// complete reproduction
							} else {
								// add some noise
								nPool.get(r2).get(j)[k] = judge;
							}

						} else if (j == 2) {
							double judge = nPool.get(r2).get(j)[k] + temp;
							if (judge > 30 || judge < 0) {
								// complete reproduction
							} else {
								// add some noise
								nPool.get(r2).get(j)[k] = judge;
							}

						} else {
							double judge = nPool.get(r2).get(j)[k] + temp;
							if (judge > 10 || judge < 0) {
								// complete reproduction
							} else {
								// add some noise
								nPool.get(r2).get(j)[k] = judge;
							}

						}
					}
				}
				pool.add(nPool.get(r2));
			}
		}
		for(int i=getEighty;i<geneInfoArray.size();i++){
			ArrayList<double[]> tempArray = new ArrayList<double[]>();
			int neuron = 9;
			double[] weight = new double[neuron];
			double[] theta = new double[1];
			double[] sigma = new double[neuron];
			double[] mean = new double[3 * neuron];
			
			theta[0] = Math.random();
			for (int a = 0; a < neuron; a++) {
				Random rand = new Random();
				double temp = 0;

				double jj = Math.random();
				if (jj > 0.5) {
					temp = (rand.nextFloat() + 0f) * 40;
				} else {
					temp = (rand.nextFloat() - 1f) * 40;
				}

				weight[a] = temp;
				sigma[a] = Math.random() * 10;
			}
			
			for (int b = 0; b < mean.length; b++) {
				mean[b] = Math.random() * 30;
			}

			tempArray.add(theta);
			tempArray.add(weight);
			tempArray.add(mean);
			tempArray.add(sigma);
			
			pool.add(tempArray);

		}
		
	}

	public void crossover() {
		// crossover
		for (int i = 0; i < pool.size(); i++) {
			int crossNo1 = i;
			int amount =pool.size()-1;
			int crossNo2 = (int) Math.round(Math.random() * amount);
			
			if (crossNo2 == crossNo1) {
				crossNo2 = (int) Math.round(Math.random() * amount);
			}

			double distanceDef = Math.random();
			double crossSigma = 0.2;
			double doCrossoverProb = Math.random();

			if (doCrossoverProb < crossoverProb) {
				// do crossover
					for (int j = 0; j < pool.get(crossNo1).size(); j++) {
						for (int k = 0; k < pool.get(crossNo1).get(j).length; k++) {
							double c1 = pool.get(crossNo1).get(j)[k];
							double c2 = pool.get(crossNo2).get(j)[k];
							
							double judge1;
							double judge2;
							if(distanceDef > 0.5){
								 judge1 = c1 + crossSigma * (c1 - c2);
								 judge2 = c2 - crossSigma * (c1 - c2);
							}
							else{
								 judge1 = c1 + crossSigma * (c2 - c1);
								 judge2 = c2 - crossSigma * (c2 - c1);
							}
							
							if (j == 0) {
								if (judge1 > 1 || judge1 < 0) {
									// complete reproduction
								} else {
									// add some noise
									pool.get(crossNo1).get(j)[k] = judge1;
								}
								if (judge2 > 1 || judge2 < 0) {
								} else {
									pool.get(crossNo2).get(j)[k] = judge2;
								}
							} else if (j == 1) {
								if (judge1 > 40 || judge1 < -40) {
									// complete reproduction
								} else {
									// add some noise
									pool.get(crossNo1).get(j)[k] = judge1;
								}
								if (judge2 > 40 || judge2 < -40) {
								} else {
									pool.get(crossNo2).get(j)[k] = judge2;
								}

							} else if (j == 2) {
								if (judge1 > 30 || judge1 < 0) {
								} else {
									pool.get(crossNo1).get(j)[k] = judge1;
								}
								if (judge2 > 30 || judge2 < 0) {
								} else {
									pool.get(crossNo2).get(j)[k] = judge2;
								}
							} else {
								if (judge1 > 10 || judge1 < 0) {
								} else {
									pool.get(crossNo1).get(j)[k] = judge1;
								}
								if (judge2 > 10 || judge2 < 0) {
								} else {
									pool.get(crossNo2).get(j)[k] = judge2;
								}
							}
						}
					}
			} 
		}

		geneInfoArray.clear();
		geneInfoArray = pool;
	}

	public void mutation() {
		// mutation
		Random rand = new Random();
		double randomNois = 0;
		if (Math.random() > 0.5) {
			randomNois = (rand.nextFloat() + 0f);
		} else {
			randomNois = (rand.nextFloat() - 1f);
		}

		int s = 1;

		int mutaLimit = (int) (geneArray.size() * mutationProb) - 1;
		
		for(int k=0;k<mutaLimit;k++){
			int amount = geneArray.size()-1;
			int mutationNo = (int) Math.round(Math.random() * amount);

			for (int i = 0; i < geneInfoArray.get(mutationNo).size(); i++) {
				for (int j = 0; j < geneInfoArray.get(mutationNo).get(i).length; j++) {
					double judge = geneInfoArray.get(mutationNo).get(i)[j] + s * randomNois;
					if (i == 0) {
						if (judge > 1 || judge < 0) {
						} else {
							geneInfoArray.get(mutationNo).get(i)[j] = judge;
						}
					} else if (i == 1) {
						if (judge > 40 || judge < -40) {
						} else {
							geneInfoArray.get(mutationNo).get(i)[j] = judge;
						}
					} else if (i == 2) {
						if (judge > 30 || judge < 0) {
						} else {
							geneInfoArray.get(mutationNo).get(i)[j] = judge;
						}
					} else {
						if (judge > 10 || judge < 0) {
						} else {
							geneInfoArray.get(mutationNo).get(i)[j] = judge;
						}
					}
				}
			}

		}
		
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
		line1Dist.setText("Red Line :" + car.sensor1.getDist());
		line2Dist.setText("Blue Line :" + car.sensor2.getDist());
		line3Dist.setText("Green Line :" + car.sensor3.getDist());
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
	public void weightChoose(String[] args) throws IOException {
		/*
		 * show a file stage for choose file
		 */

		Stage fileStage = new Stage();
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.setInitialDirectory(new File("src/train"));

		File file = fileChooser.showOpenDialog(fileStage);
		// System.out.println(file);

		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);// 在br.ready反查輸入串流的狀況是否有資料

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
				weightArray.add(token2);// 把txt裡面內容先切割過在都讀進array內
			} catch (NumberFormatException ex) {
				System.out.println("Sorry Error...");
			}
		}
		fr.close();// 關閉檔案
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
