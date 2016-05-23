package cihw3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Swarm {
	private ArrayList<double[]> inputArray;
	private ArrayList<Particle> particleArray;
	private ArrayList<ArrayList<double[]>> particleInfoArray;
	private ArrayList<ArrayList<double[]>> pool;

	private ArrayList<Particle> tempRankedParticle;
	
	private int looptimes;
	private int groupSize;
	private int iteration;	
	private double phiI ;
	private double phiG ;
	private ArrayList<double[]> swarmBestInfo;
	
	public Swarm(double[] parameters,ArrayList<double[]> inputArray){
		this.inputArray = inputArray;
		
		particleArray = new ArrayList<Particle>();
		particleInfoArray = new ArrayList<ArrayList<double[]>>();
		pool = new ArrayList<ArrayList<double[]>>();

		this.looptimes = (int) parameters[0];
		this.groupSize = (int) parameters[1];
		this.phiI = parameters[2];
		this.phiG = parameters[3];
		
		// generate genes
		for (int i = 0; i < groupSize; i++) {
			Particle tempGene = new Particle();
			particleArray.add(tempGene);
		}

	}
	
	public void startAlgo(int iteration){

		this.iteration = iteration;
		
		particleInfoArray = new ArrayList<ArrayList<double[]>>();
		for (int i = 0; i < particleArray.size(); i++) {
			particleInfoArray.add(particleArray.get(i).getparticleInfo());
		}

		double[] fitnessError = new double[particleArray.size()];
		double[] avgError = new double[particleArray.size()];

		for (int i = 0; i < fitnessError.length; i++) {
			fitnessError[i] = 0;
		}
		for (int i = 0; i < avgError.length; i++) {
			avgError[i] = 0;
		}

		for (int i = 0; i < particleArray.size(); i++) {

			for (int j = 0; j < inputArray.size(); j++) {

				double[] distance = new double[3];
				double desire = inputArray.get(j)[inputArray.get(j).length - 1];

				distance[0] = inputArray.get(j)[0];
				distance[1] = inputArray.get(j)[1];
				distance[2] = inputArray.get(j)[2];

				double output = particleArray.get(i).calOutput(distance);

				double errorTemp = Math.pow((desire - output), 2);
				double avgETemp = Math.abs(desire - output);
				fitnessError[i] += errorTemp;
				avgError[i] += avgETemp;
			}
			fitnessError[i] /= 2;
			avgError[i] /= inputArray.size();
			particleArray.get(i).setFitnessValue(fitnessError[i]);
			particleArray.get(i).setAvgError(avgError[i]);
			particleArray.get(i).setParticleBestInfo(particleInfoArray.get(i), fitnessError[i]);
		}

		// here's fitness function is smaller => better

		rankSort();

		// store sortedGene back to particleArray
		tempRankedParticle = new ArrayList<Particle>();

		for (int i = 0; i < particleArray.size(); i++) {
			for (int j = 0; j < particleArray.size(); j++) {
				if (i == particleArray.get(j).getFitnessRank()) {
					tempRankedParticle.add(particleArray.get(j));
				}
			}
		}
		particleArray.clear();
		for (int i = 0; i < tempRankedParticle.size(); i++) {
			particleArray.add(tempRankedParticle.get(i));
		}
		particleInfoArray = new ArrayList<ArrayList<double[]>>();
		for (int i = 0; i < particleArray.size(); i++) {
			particleInfoArray.add(particleArray.get(i).getparticleInfo());
		}

		this.swarmBestInfo = particleInfoArray.get(0);
		tuneParticles();

		for (int i = 0; i < particleArray.size(); i++) {
			particleArray.get(i).updateparticleInfo(particleInfoArray.get(i));
		}

	}
	public Particle getBestGene(){
		return this.particleArray.get(0);
	}
	
	public double getAfterAlgoFitness(){
		return this.particleArray.get(0).getFitnessValue();
	}
	public double getAfterAlgoAvgError(){
		return this.particleArray.get(0).getAvgError();
	}
	
	public void rankSort() {

		double[] readyToSort = new double[particleArray.size()];
		int[] sortedRank = new int[particleArray.size()];

		for (int i = 0; i < particleArray.size(); i++) {
			readyToSort[i] = particleArray.get(i).getFitnessValue();
		}

		Arrays.sort(readyToSort);

		double[] used = new double[particleArray.size()];
		for (int i = 0; i < used.length; i++) {
			used[i] = Double.MAX_VALUE;
		}

		int count = 0;
		f1: for (int i = 0; i < readyToSort.length; i++) {
			f2: for (int j = 0; j < particleArray.size(); j++) {
				if (count == 0) {
					if (readyToSort[i] == particleArray.get(j).getFitnessValue()) {
						used[count] = j;
						sortedRank[j] = i;
						count++;
						break f2;
					}
				} else {
					if (readyToSort[i] == particleArray.get(j).getFitnessValue()) {
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
			particleArray.get(i).setFitnessRank(sortedRank[i]);
		}

	}
	public void tuneParticles(){
		for(int i=0;i<particleArray.size();i++){
			particleArray.get(i).tuneParticlePosition(swarmBestInfo, this.phiI, this.phiG);
		}
	}

}
