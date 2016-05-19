package cihw3;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class Gene {
	private int neuronNumber = 9;
	private double theta[];
	private double[] weight;
	private double[] fi;
	private double[] sigma;
	private ArrayList<double[]> mean;
	private ArrayList<double[]> geneInfo;
	
	private double fitnessValue;
	private double avgError;
	private int fitnessRank = Integer.MAX_VALUE;
	
	// 0:theta 1:weight 2:mean 3:sigma
	public Gene() {
		this.geneInfo = new ArrayList<double[]>();
		
		this.mean = new ArrayList<double[]>();
		this.weight = new double[neuronNumber];
		this.theta = new double[1];
		this.theta[0] = Math.random();
		
		this.fi = new double[neuronNumber+1];
		this.sigma = new double[neuronNumber]; 
		

		for (int i = 0; i < neuronNumber; i++) {
			Random rand = new Random();
			double temp = 0;

			if (Math.random() > 0.5) {
				temp = (rand.nextFloat() + 0f)*40;
			} else {
				temp = (rand.nextFloat() - 1f)*40;
			}

			weight[i] = temp;
			sigma[i] = Math.random() * 10;
		}
		for (int j = 0; j < neuronNumber; j++) {
			double[] tempM = new double[3];
			for(int k=0;k<tempM.length;k++){
				tempM[k] = Math.random() * 30;
			}
			mean.add(tempM);
		}

	}
	public int getFitnessRank(){
		return this.fitnessRank;
	}
	public void setFitnessRank(int rank){
		this.fitnessRank = rank;
	}
	public double getAvgError(){
		return this.avgError;
	}
	public void setAvgError(double inputAvgError){
		this.avgError = inputAvgError;
	}
	public double getFitnessValue(){
		return this.fitnessValue;
	}
	public void setFitnessValue(double input){
		this.fitnessValue = input;
	}
	public double calOutput(double[] distanceInput) {

		for (int i = 0; i < fi.length; i++) {
			if(i == 0){ // fi0 = 1
				fi[i] = 1;
			}
			else{
				int calCount = i-1;
				fi[i] = calGaussianBasis(distanceInput, this.mean.get(calCount), this.sigma[calCount]);// ** must -1
			}
//			System.out.print(" f"+i+": "+fi[i]);
		}
//		System.out.println();
		
		double output = 0;
		for(int i=0;i<fi.length;i++){
			if(i == 0){
				output += fi[i]*theta[0];
			}
			else{
				int calCount = i-1;
				output += fi[i]*weight[calCount];
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
		returnValue = Math.exp(-0.5 *( temp / (sigma2 * sigma2)));
		return returnValue;
	}


	public void updateGeneInfo(ArrayList<double[]> newGeneInfo){
		
		for(int i=0;i<newGeneInfo.size();i++){
			for(int j=0;j<newGeneInfo.get(i).length;j++){
				if (i == 0) {
					this.theta[j] = newGeneInfo.get(i)[j];
				} else if (i == 1) {
					this.weight[j] = newGeneInfo.get(i)[j];
				} else if (i ==2 ){
					int gi = j/3;
					int gj = j%3;
					this.mean.get(gi)[gj] = newGeneInfo.get(i)[j];
				}else{
					this.sigma[j] = newGeneInfo.get(i)[j];
				}
			}
		}
		
	}
	
	public ArrayList<double[]> getGeneInfo(){
		this.geneInfo.clear();
		this.geneInfo.add(theta);
		this.geneInfo.add(weight);
		
		double[] temp = new double[mean.size()*mean.get(0).length];
		int count =0;
		for(int i=0;i<mean.size();i++){
			for(int j=0;j<mean.get(i).length;j++){
				temp[count] = mean.get(i)[j];
				count ++;
			}
		}
		this.geneInfo.add(temp);
		this.geneInfo.add(sigma);

		return this.geneInfo;
	}
		

}
