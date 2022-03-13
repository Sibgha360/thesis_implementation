package de.tudarmstadt;

public class Main {
	public static void main(String[] args) {
		try {
			ExtractPDFReports.extactDataToJSON();
			ParseAndStore.main(null);
			CleanData.main(null);
			NormalizeData.main(null);
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

}
