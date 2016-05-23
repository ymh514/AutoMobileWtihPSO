package cihw3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class GenePool {
	private ArrayList<double[]> inputArray;
	private ArrayList<Gene> geneArray;
	private ArrayList<ArrayList<double[]>> geneInfoArray;
	private ArrayList<ArrayList<double[]>> pool;

	private ArrayList<Gene> tempRankedGene;
	
	private int looptimes;
	private int groupSize;
	private double crossoverProb;
	private double mutationProb;
	
	private int iteration;	
	
	public GenePool(double[] parameters,ArrayList<double[]> inputArray){
		
		this.inputArray = inputArray;
		
		geneArray = new ArrayList<Gene>();
		geneInfoArray = new ArrayList<ArrayList<double[]>>();
		pool = new ArrayList<ArrayList<double[]>>();

		this.looptimes = (int) parameters[0];
		this.groupSize = (int) parameters[1];
		this.crossoverProb = parameters[2];
		this.mutationProb = parameters[3];
		
		// generate genes
		for (int i = 0; i < groupSize; i++) {
			Gene tempGene = new Gene();
			geneArray.add(tempGene);
		}

	}
	
	public void startAlgo(int iteration){
		this.iteration = iteration;
		
		double[] fitnessError = new double[geneArray.size()];
		double[] avgError = new double[geneArray.size()];

		for (int i = 0; i < fitnessError.length; i++) {
			fitnessError[i] = 0;
		}
		for (int i = 0; i < avgError.length; i++) {
			avgError[i] = 0;
		}


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
				fitnessError[i] += errorTemp;
				avgError[i] += avgETemp;
			}
			fitnessError[i] /= 2;
			avgError[i] /= inputArray.size();
			geneArray.get(i).setFitnessValue(fitnessError[i]);
			geneArray.get(i).setAvgError(avgError[i]);

		}

		// here's fitness function is smaller => better

		rankSort();

		// store sortedGene back to geneArray
		tempRankedGene = new ArrayList<Gene>();

		for (int i = 0; i < geneArray.size(); i++) {
			for (int j = 0; j < geneArray.size(); j++) {
				if (i == geneArray.get(j).getFitnessRank()) {
					tempRankedGene.add(geneArray.get(j));
				}
			}
		}
		geneArray.clear();
		for (int i = 0; i < tempRankedGene.size(); i++) {
			geneArray.add(tempRankedGene.get(i));
		}

		geneInfoArray = new ArrayList<ArrayList<double[]>>();
		for (int i = 0; i < geneArray.size(); i++) {
			geneInfoArray.add(geneArray.get(i).getGeneInfo());
		}

		reproduction();

		crossover();

		mutation();

		for (int i = 0; i < geneArray.size(); i++) {
			geneArray.get(i).updateGeneInfo(geneInfoArray.get(i));
		}

	}
	public Gene getBestGene(){
		return this.geneArray.get(0);
	}
	
	public double getAfterAlgoFitness(){
		return this.geneArray.get(0).getFitnessValue();
	}
	public double getAfterAlgoAvgError(){
		return this.geneArray.get(0).getAvgError();
	}
	
	public void rankSort() {

		double[] readyToSort = new double[geneArray.size()];
		int[] sortedRank = new int[geneArray.size()];

		for (int i = 0; i < geneArray.size(); i++) {
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

		for (int i = 0; i < sortedRank.length; i++) {
			geneArray.get(i).setFitnessRank(sortedRank[i]);
		}

	}
	
	public void reproduction() {

		pool = new ArrayList<ArrayList<double[]>>();

		ArrayList<ArrayList<double[]>> nPool = new ArrayList<ArrayList<double[]>>();

		int getHalf = geneInfoArray.size() / 2;

		for (int i = 0; i < geneInfoArray.size(); i++) {
			nPool.add(geneInfoArray.get(i));
		}

		for (int i = 0; i < getHalf; i++) {
			pool.add(geneInfoArray.get(i));
		}

		int getEighty = geneInfoArray.size() * 75 / 100;
		// int getEighty = geneInfoArray.size();

		for (int i = getHalf; i < getEighty; i++) {
			int amount = geneArray.size() - 1;
			int r1 = i - getHalf; // set back to 0
			int r2 = (int) Math.round(Math.random() * amount);

			if (r1 == r2) {
				r2 = (int) Math.round(Math.random() * amount);
			}
			double rj1 = geneArray.get(r1).getFitnessValue();
			double rj2 = geneArray.get(r2).getFitnessValue();

			if (rj1 <= rj2) {

				for (int j = 0; j < nPool.get(r1).size(); j++) {
					for (int k = 0; k < nPool.get(r1).get(j).length; k++) {
						Random rand = new Random();
						double temp = 0;

						if (Math.random() > 0.5) {
							temp = (rand.nextFloat() + 0f);
						} else {
							temp = (rand.nextFloat() - 1f);
						}

						temp = temp * (1 - iteration / looptimes);

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
			} else {

				for (int j = 0; j < nPool.get(r2).size(); j++) {
					for (int k = 0; k < nPool.get(r2).get(j).length; k++) {
						Random rand = new Random();
						double temp = 0;

						if (Math.random() > 0.5) {
							temp = (rand.nextFloat() + 0f);
						} else {
							temp = (rand.nextFloat() - 1f);
						}

						temp = temp * (1 - iteration / looptimes);

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
		for (int i = getEighty; i < geneInfoArray.size(); i++) {
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
			int amount = pool.size() - 1;
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
						if (distanceDef > 0.5) {
							judge1 = c1 + crossSigma * (c1 - c2);
							judge2 = c2 - crossSigma * (c1 - c2);
						} else {
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

		for (int k = 0; k < mutaLimit; k++) {
			int amount = geneArray.size() - 1;
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
}
