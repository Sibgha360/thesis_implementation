package de.tudarmstadt;

/*
 * One: 
        normalize the data by removing the duplications and unit information must be reflected by the indicator value. For example:  
		sales: 13M
		sales: 13M
		
		Normalized form: 
		sales: 13000000
		
 * Two:
		
		remove indicators with numbers and symbols
		
		e.g. 
		25%
		2019 - 2020
		()
 */
public class CleanData {
	public static void main(String[] args) {
		DbUtil.getAlphaIndicators();
	}
}
