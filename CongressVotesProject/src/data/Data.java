package data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

public class Data implements Serializable {
	/**	default serializable version */
	private static final long serialVersionUID = 1L;
	private int totalDemocrats, totalRepublicans;
	private InputStream pathToText;
	private ArrayList<ArrayList<Integer>> candidatesMatrix;	
	private Integer[][] maxTypeClassifier; // gets maximum type (republican, democrat) for attribute max infogain.
	
	/**
	 * Parsing a given text with a specific input into a matrix.
	 * Each line in the text starts with "republican" or "democrat",
	 * then follows a text with a single char out of the following: "y","n","?";
	 * I am representing the text in the candidatesMatrix with the following values:
	 * 1, 2, 3, 4, 5 respectively.
	 * 
	 * @param in
	 * @throws Exception 
	 */
	public Data(InputStream in) throws Exception {
		totalDemocrats = 0; totalRepublicans = 0;
		maxTypeClassifier = new Integer[16][3];
		setPathToText(in);
		candidatesMatrix = new ArrayList<>();
		setMatrix();
	}
	
	public void setMatrix() throws Exception {
		int x = 0, y = 0;
		Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(pathToText)));
		while(scanner.hasNextLine()) {
			String[] line = scanner.nextLine().split(",");
			for (x=0;x<line.length;x++) {
				candidatesMatrix.add(new ArrayList<Integer>());
				switch (line[x]) {
				case "republican":
					candidatesMatrix.get(y).add(1);
					totalRepublicans++;
					break;
				case "democrat":
					candidatesMatrix.get(y).add(2);
					totalDemocrats++;
					break;
				case "y":
					candidatesMatrix.get(y).add(3);
					break;
				case "n":
					candidatesMatrix.get(y).add(4);
					break;
				case "?":
					candidatesMatrix.get(y).add(5);
					break;
				default:
					throw new Exception("Text given is not in the format excpected.");
				}
			}
			y++;
		}
		scanner.close();
	}
	
	//returning the log(number) in base 2
	public double log2(double num) {
		return ((Math.log(num))/(Math.log(2)));
	}
	
	//getting a total of value a and value b and calculating their entropy
	public double entropy(int a, int b) {
		double log2_p_a, log2_p_b;
		double p_a, p_b;
		//if argument given is 0, it's entropy is 0 as well.
		if (a == 0) {
			p_a = 0;
			log2_p_a = 0;
		}
		else {
			p_a = ((double)a/(double)(a+b));
			log2_p_a = log2(p_a);
		}
		if (b == 0) {
			p_b = 0;
			log2_p_b = 0;
		}
		else {
			p_b = ((double)b/(double)(a+b));
			log2_p_b = log2(p_b);
		}
		return -log2_p_a*p_a-log2_p_b*p_b;		
	}
	
	public void swap(int a,int b) {
		a = a^b;
		b = a^b;
		a = a^b;
	}
	
	//calling this function will initialize the maxTypeClassifier
	public int findMaxEnthropyPerAttribute() throws Exception {
		double maxAttr = infoGainPerAttribute(1);
		System.out.println("Infogain for feature number 1 is: " +maxAttr);
		double temp;
		int attribute = -1;
		for (int i = 2; i<17; i++) {
			temp = infoGainPerAttribute(i);
			System.out.println("Infogain for feature number "+ i +" is: "+temp);
			if (temp > maxAttr) {
				maxAttr = temp;
				attribute = i;
			}
		}
		return attribute;
	}
	
	//calculating information gain per a given attribute
	public double infoGainPerAttribute(int attribute) throws Exception {
		if (attribute < 1 || attribute > 16) {
			throw new Exception("Attributes are between [1,16]");
		}
		int yesAttr=0, noAttr=0,noneAttr=0;
		int repAndYes=0, repAndNo=0, repAndNone=0, demAndYes=0, demAndNo=0, demAndNone=0;
		double p_yes,p_no,p_none;
		/* 
		 * counting per a given attribute, for all the rows in the text,
		 * whether a republican or a democrat used that attribute.
		 */
		for (int y = 0;y < 435;y++) {
			if (candidatesMatrix.get(y).get(0) == 1) {
				switch (candidatesMatrix.get(y).get(attribute)) {
				case 3:
					repAndYes++;
					yesAttr++;
					break;
				case 4:
					repAndNo++;
					noAttr++;
					break;
				case 5:
					repAndNone++;
					noneAttr++;
					break;
				default:
					break;
				}
				
			}
			else {
				switch (candidatesMatrix.get(y).get(attribute)) {
				case 3:
					demAndYes++;
					yesAttr++;
					break;
				case 4:
					demAndNo++;
					noAttr++;
					break;
				case 5:
					demAndNone++;
					noneAttr++;
					break;
				default:
					break;
				}
			}
		}
		if (yesAttr != 0)
			p_yes = ((double)yesAttr)/((double)candidatesMatrix.size()*17 - candidatesMatrix.size());
		else p_yes = 0;
		if (noAttr != 0)
			p_no = ((double)noAttr)/((double)candidatesMatrix.size()*17 - candidatesMatrix.size());
		else p_no = 0;
		if(noneAttr != 0)
			p_none = ((double)noneAttr)/((double)candidatesMatrix.size()*17 - candidatesMatrix.size());
		else p_none = 0;
		/*
		 * checking for attribute what type chose max Ai {y,n,?}
		 * and placing in the maxTypePerAttr
		 */
		maxTypeClassifier[attribute-1][0] = repAndYes > demAndYes? 1 : 2;
		maxTypeClassifier[attribute-1][1] = repAndNo > demAndNo? 1 : 2;
		maxTypeClassifier[attribute-1][2] = repAndNone > demAndNone? 1 : 2;
		return ((entropy(totalDemocrats, totalRepublicans) - (p_yes*entropy(repAndYes, demAndYes)+
					p_no*entropy(repAndNo, demAndNo)+p_none*entropy(repAndNone, demAndNone))));
		
	}
	//*******Getters & Setters**********//
	public Integer[][] getMaxTypeMatrix(){
		return maxTypeClassifier;
	}
	public int getTotalDemocrats() {
		return totalDemocrats;
	}

	public void setTotalDemocrats(int totalDemocrats) {
		this.totalDemocrats = totalDemocrats;
	}

	public int getTotalRepublicans() {
		return totalRepublicans;
	}

	public void setTotalRepublicans(int totalRepublicans) {
		this.totalRepublicans = totalRepublicans;
	}

	public ArrayList<ArrayList<Integer>> getCandidateMatrix() {
		return candidatesMatrix;
	}

	public InputStream getPathToText() {
		return pathToText;
	}

	public void setPathToText(InputStream pathToText) {
		this.pathToText = pathToText;
	}
}
