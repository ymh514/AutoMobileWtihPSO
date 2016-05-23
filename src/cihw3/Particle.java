package cihw3;

import java.util.ArrayList;
import java.util.Random;

public class Particle {
	private int neuronNumber = 9;
	private double theta[];
	private double[] weight;
	private double[] fi;
	private double[] sigma;
	private ArrayList<double[]> mean;
	private ArrayList<double[]> particleInfo;

	private double fitnessValue;
	private double avgError;
	private int fitnessRank = Integer.MAX_VALUE;
	private ArrayList<double[]> particleBestInfo;
	private double bestFitnessValue;
	private ArrayList<double[]> velocity;

	// 0:theta 1:weight 2:mean 3:sigma
	public Particle() {
		this.particleInfo = new ArrayList<double[]>();

		this.mean = new ArrayList<double[]>();
		this.weight = new double[neuronNumber];
		this.theta = new double[1];
		this.theta[0] = Math.random();

		this.fi = new double[neuronNumber + 1];
		this.sigma = new double[neuronNumber];

		for (int i = 0; i < neuronNumber; i++) {
			Random rand = new Random();
			double temp = 0;

			if (Math.random() > 0.5) {
				temp = (rand.nextFloat() + 0f) * 40;
			} else {
				temp = (rand.nextFloat() - 1f) * 40;
			}

			weight[i] = temp;
			sigma[i] = Math.random() * 10;
		}
		for (int j = 0; j < neuronNumber; j++) {
			double[] tempM = new double[3];
			for (int k = 0; k < tempM.length; k++) {
				tempM[k] = Math.random() * 30;
			}
			mean.add(tempM);
		}
		bestFitnessValue = Double.MAX_VALUE;
		generateVelocity();
	}

	public int getFitnessRank() {
		return this.fitnessRank;
	}

	public void setFitnessRank(int rank) {
		this.fitnessRank = rank;
	}

	public double getAvgError() {
		return this.avgError;
	}

	public void setAvgError(double inputAvgError) {
		this.avgError = inputAvgError;
	}

	public double getFitnessValue() {
		return this.fitnessValue;
	}

	public void setFitnessValue(double input) {
		this.fitnessValue = input;
	}

	public double calOutput(double[] distanceInput) {

		for (int i = 0; i < fi.length; i++) {
			if (i == 0) { // fi0 = 1
				fi[i] = 1;
			} else {
				int calCount = i - 1;
				fi[i] = calGaussianBasis(distanceInput, this.mean.get(calCount), this.sigma[calCount]);// **
																										// must
																										// -1
			}
			// System.out.print(" f"+i+": "+fi[i]);
		}
		// System.out.println();

		double output = 0;
		for (int i = 0; i < fi.length; i++) {
			if (i == 0) {
				output += fi[i] * theta[0];
			} else {
				int calCount = i - 1;
				output += fi[i] * weight[calCount];
			}
		}
		return output;
	}

	public double calGaussianBasis(double[] x, double[] m2, double sigma2) {
		double returnValue = 0;
		double temp = 0;

		for (int i = 0; i < x.length; i++) {
			temp += Math.pow((x[i] - m2[i]), 2);
		}
		returnValue = Math.exp(-0.5 * (temp / (sigma2 * sigma2)));
		return returnValue;
	}

	public void updateparticleInfo(ArrayList<double[]> newparticleInfo) {

		for (int i = 0; i < newparticleInfo.size(); i++) {
			for (int j = 0; j < newparticleInfo.get(i).length; j++) {
				if (i == 0) {
					this.theta[j] = newparticleInfo.get(i)[j];
				} else if (i == 1) {
					this.weight[j] = newparticleInfo.get(i)[j];
				} else if (i == 2) {
					int gi = j / 3;
					int gj = j % 3;
					this.mean.get(gi)[gj] = newparticleInfo.get(i)[j];
				} else {
					this.sigma[j] = newparticleInfo.get(i)[j];
				}
			}
		}

	}

	public ArrayList<double[]> getparticleInfo() {

		thisSetInfo();
		return this.particleInfo;
	}

	public void thisSetInfo() {

		this.particleInfo.clear();
		this.particleInfo.add(theta);
		this.particleInfo.add(weight);

		double[] temp = new double[mean.size() * mean.get(0).length];
		int count = 0;
		for (int i = 0; i < mean.size(); i++) {
			for (int j = 0; j < mean.get(i).length; j++) {
				temp[count] = mean.get(i)[j];
				count++;
			}
		}
		this.particleInfo.add(temp);
		this.particleInfo.add(sigma);
	}

	public void generateVelocity() {
		// ArrayList<double[]> tempArray = new ArrayList<double[]>();
		velocity = new ArrayList<double[]>();

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

		velocity.add(theta);
		velocity.add(weight);
		velocity.add(mean);
		velocity.add(sigma);
	}

	public void setParticleBestInfo(ArrayList<double[]> particleBestInfo, double fitnessValue) {
		if (fitnessValue < this.bestFitnessValue) {
			this.particleBestInfo = particleBestInfo;
		}
	}

	public void tuneParticlePosition(ArrayList<double[]> swarmBestInfo, double phiI, double phiG) {
		thisSetInfo();

		for (int i = 0; i < this.particleInfo.size(); i++) {
			for (int j = 0; j < this.particleInfo.get(i).length; j++) {
				double phi = phiI + phiG;
				double p = (phiI * this.particleBestInfo.get(i)[j] + phiG * swarmBestInfo.get(i)[j]) / phi;
				double a = Math.random();
				this.velocity.get(i)[j] = a * (this.velocity.get(i)[j]) + phi * (p - this.particleInfo.get(i)[j]);

				double judge = this.particleInfo.get(i)[j] + this.velocity.get(i)[j];

				if (i == 0) {
					if (judge > 1 || judge < 0) {
						// complete reproduction
					} else {
						// add some noise
						this.particleInfo.get(i)[j] = judge;
					}

				} else if (i == 1) {
					if (judge > 40 || judge < -40) {
						// complete reproduction
					} else {
						// add some noise
						this.particleInfo.get(i)[j] = judge;
					}
				} else if (i == 2) {
					if (judge > 30 || judge < 0) {
					} else {
						this.particleInfo.get(i)[j] = judge;
					}

				} else {
					if (judge > 10 || judge < 0) {
					} else {
						this.particleInfo.get(i)[j] = judge;
					}
				}

			}
		}
	}

}
