package tool;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import neuralNetwork.DataSet;

public class DataInfo {
	private List<String> trainWordNames = new ArrayList<>();
	private static Map<String, List<Double>> trueOutputs, box, trueOutputsdenormalized, exampleID;
	private static List<DataSet> trainSets, testSets;

	public DataInfo(String type) throws IOException {
		switch (type) {
		case "mnist":
			trueOutputs = vectorsParser("dataset/standardOutput/digitLabels.txt", 1);
			trainSets = ReadFile.readFromSingleTxt("MNIST TrainSet.txt");
			testSets = ReadFile.readFromSingleTxt("MNIST TestSet.txt");
			break;
		case "foreOrBackground":
			exampleID = vectorsParser("dataset/standardOutput/foreground_bBoxRegressionLabels.txt", 1);
			trueOutputs = vectorsParser("dataset/standardOutput/foreground_bBoxRegressionLabels.txt", 2);
			box = vectorsParser("dataset/standardOutput/foreground_bBoxRegressionLabels.txt", 3);
			trueOutputsdenormalized = vectorsParser("dataset/standardOutput/foreground_bBoxRegressionLabels.txt", 4);
			trainSets = ReadFile.readFromPictures("foreground", true);
			Collections.shuffle(trainSets);
			testSets = trainSets.subList(trainSets.size() * 6 / 7, trainSets.size());
			trainSets = trainSets.subList(0, trainSets.size() * 6 / 7);
		}

	}

	public static List<DataSet> getTrainSets() {
		return trainSets;
	}

	public static List<DataSet> getTestSets() {
		return testSets;
	}

	public List<String> getTrainWordNames() {
		return trainWordNames;
	}

	public String[] getTrainWordNamesArray() {
		return trainWordNames.toArray(new String[getTrainWordNames().size()]);
	}

	public static List<Double> getTrueOutput(String input) {
		return trueOutputs.get(input);
	}

	public static List<Double> getExampleID(String input) {
		return exampleID.get(input);
	}

	public static List<Double> getTrueOutputDenormalized(String input) {
		return trueOutputsdenormalized.get(input);
	}

	public static List<Double> getBox(String input) {
		return box.get(input);
	}

	public Map<String, List<Double>> vectorsParser(String file, int type) throws IOException {
		Map<String, List<Double>> map = new TreeMap<>();
		BufferedReader br = new BufferedReader(new FileReader(new File(file)));
		for (String line = br.readLine(); (line = br.readLine()) != null;) {
			String[] parts = line.split("\\|");
			map.put(parts[0], Arrays.asList(parts[type].split(",")).stream().map(Double::parseDouble)
					.collect(Collectors.toList()));
		}
		br.close();
		return map;
	}
}
