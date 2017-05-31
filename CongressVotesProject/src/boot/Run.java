package boot;

import java.io.FileInputStream;

import data.Data;

public class Run {

	public static void main(String[] args) {
		try {
			// Parsing data given to the ArrayList matrix
			Data data = new Data(new FileInputStream("./resources/candidatesData.txt"));
			// Calculating entropy for total candidates
			data.entropy(data.getTotalDemocrats(), data.getTotalRepublicans());
			// Printing all the features information gain and the attribute with the maximum infogain
			System.out.println("Attribute with maximum infogain is: "+ data.findMaxEnthropyPerAttribute());
			// explaining how to construct a 1 layer decision tree
			Integer[][] mat = data.getMaxTypeMatrix();
			System.out.println("How to construct the tree per Ai{'y','n','?'}");
			System.out.println("for 'y': "+mat[3][0] + " for 'n': " + mat[3][1] + " for '?': " + mat[3][2]);
			System.out.println("Meaning, 'y' = Republican; 'n' and '?' = Democrat");
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	

}
