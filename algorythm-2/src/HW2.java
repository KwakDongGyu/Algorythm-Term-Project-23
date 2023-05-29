import java.io.*;
import java.util.*;

public class HW2 {
	ArrayList<String> stopwords = new ArrayList<String>();
	HashMap<Integer, Double> idfMap = new HashMap<Integer, Double>();
	Map<String, HashMap<Integer, Double>> map = new HashMap<String, HashMap<Integer, Double>>();
	
	public static void main(String[] args) throws IOException {
		Scanner sc = new Scanner(System.in);

		System.out.println("���� �̸�, k, ���� ����: ");
		String fileName = sc.next();
		int k = sc.nextInt();
		String target = sc.nextLine();
		if (target.isEmpty()) {
			target = sc.nextLine();
		}
		target = target.trim();

		HW2 file = new HW2();
		file.readStopwords();
		file.readFile(fileName, sfile.stopwords);

		List<Integer> listSort = new ArrayList<Integer>(file.map.get(target).keySet());
		listSort.sort(new IntComparator());

		for (String str : file.map.keySet()) {
			file.tf(str);
		}
		file.idf();
		
		System.out.println("��� 1. " + target + "�� TF-IDF ����");
		System.out.print("[ ");
		for (Integer key : listSort) {
			System.out.printf("(%d, %.3f) ", key, file.map.get(target).get(key));
		}
		System.out.println("]");
		System.out.println();

		List<TS> similarityList = new ArrayList<TS>();

		for (String key : file.map.keySet()) {
			if (target.compareTo(key) != 0) {
				double t_norm = file.cosineSimilarity(file.map.get(target), file.map.get(key));
				similarityList.add(new TS(key, t_norm));
			}
		}
		similarityList.sort(new TS());

		System.out.println("��� 2. " + target + "��(��) ������ " + k + "���� ����");
		for (int i = 0; i < k; i++) {
			System.out.printf("%d. %s(���絵 = %.5f)\n", i + 1, similarityList.get(i).title,
					similarityList.get(i).similarity);
		}
		sc.close();
	}
	
	void readStopwords() throws IOException {
		File file = new File("stopwords.txt");
		BufferedReader fileReader = new BufferedReader(new FileReader(file));
		String line = null;

		while ((line = fileReader.readLine()) != null) {
			this.stopwords.add(line);
		}
		fileReader.close();
	}

	void readFile(String fileName, ArrayList<String> stopwords) throws IOException {
		File file = new File(fileName);
		BufferedReader fileReader = new BufferedReader(new FileReader(file));
		String titleLine = null;
		String contentLine = null;

		while ((titleLine = fileReader.readLine()) != null) {
			this.map.put(titleLine, new HashMap<Integer, Double>());
			contentLine = fileReader.readLine();
			contentLine = contentLine.toLowerCase();
			String[] contentWords = contentLine.split("[,.?!:\"\\s]+");

			for (String str : contentWords) {
				int hashCode = str.hashCode();

				if (!stopwords.contains(str)) {
					if (this.map.get(titleLine).containsKey(hashCode)) {
						this.map.get(titleLine).put(hashCode, this.map.get(titleLine).get(hashCode) + 1);
					} else {
						this.map.get(titleLine).put(hashCode, (double) 1);
					}
				}
			}
		}
		fileReader.close();
	}
	void tf(String str) {
		double sum = 0;
		for (Map.Entry<Integer, Double> entry : this.map.get(str).entrySet()) {
			sum += entry.getValue();
		}
		for (Map.Entry<Integer, Double> entry : this.map.get(str).entrySet()) {
			entry.setValue(entry.getValue() / sum);
		}
	}

	void idf() {
		for (String str : this.map.keySet()) {
			for (Integer key : this.map.get(str).keySet())
				if (this.idfMap.containsKey(key)) {
					this.idfMap.put(key, this.idfMap.get(key) + 1.0);
				} else {
					this.idfMap.put(key, 1.0);
				}
		}

		for (Integer key : this.idfMap.keySet()) {
			this.idfMap.put(key, Math.log(this.map.size() / this.idfMap.get(key)));
		}

		for (String str : this.map.keySet()) {
			for (Map.Entry<Integer, Double> entry : this.map.get(str).entrySet()) {
				entry.setValue(entry.getValue() * this.idfMap.get(entry.getKey()));
			}
		}
	}

	double cosineSimilarity(Map<Integer, Double> map1, Map<Integer, Double> map2) {
		double vectorProduct = 0.0;
		double lengthOfVector1 = 0.0;
		double lengthOfVector2 = 0.0;

		for (Integer key : map1.keySet()) {
			if (map2.containsKey(key)) {
				vectorProduct += (map1.get(key) * map2.get(key));
			}
		}

		for (Double value : map1.values()) {
			lengthOfVector1 += Math.pow(value, 2);
		}

		for (Double value : map2.values()) {
			lengthOfVector2 += Math.pow(value, 2);
		}
		
		lengthOfVector1 = Math.sqrt(lengthOfVector1);
		lengthOfVector2 = Math.sqrt(lengthOfVector2);

		if (lengthOfVector1 == 0.0 || lengthOfVector2 == 0.0) {
			return 0.0;
		} else {
			double similarity = vectorProduct / (lengthOfVector1 * lengthOfVector2);
			return similarity;
		}
	}
}

class IntComparator implements Comparator<Integer> {
	public int compare(Integer x, Integer y) {
		int result = x.compareTo(y);
		return result;
	}
}

class TS implements Comparator<TS> {
	String title;
	Double similarity;

	TS() {
	}

	TS(String title, Double similarity) {
		this.title = title;
		this.similarity = similarity;
	}

	public int compare(TS ts1, TS ts2) {
		int result = ts2.similarity.compareTo(ts1.similarity);
		return result;
	}
}