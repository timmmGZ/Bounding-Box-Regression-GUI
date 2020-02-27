package neuralNetwork;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DataSet {

	private double[][] inputsAsMatrix;
	private List<Double> featureVector, inputVector, trueOutputDenormalized, box, trueOuput;
	private String label;

	public DataSet(double[][] in, List<Double> out, String word, Convolution convolution) {
		this(in, convolution);
		trueOuput = out;
		label = word;
	}

	public DataSet(double[][] in, List<Double> out, String word, Convolution convolution, List<Double> b,
			List<Double> outDenormalized) {
		this(in, out, word, convolution);
		box = b;
		trueOutputDenormalized = outDenormalized;
	}

	public DataSet(double[][] in, Convolution convolution) {
		inputsAsMatrix = in;
		convolution.setupAllLayers(inputsAsMatrix);
		featureVector = convolution.getFeatureVector();
		inputVector = Arrays.stream(inputsAsMatrix).map(Arrays::stream).map(e -> e.boxed()).flatMap(s -> s)
				.collect(Collectors.toList());
	}

	public double[][] getInputs() {
		return inputsAsMatrix;
	}

	public List<Double> getBox() {
		return box;
	}

	public List<Double> getInputsVector() {
		return inputVector;
	}// map ->List<int[]> ->List<IntStream> ->List<Stream<Integer>> ->Stream<Integer>

	public List<Double> getFeatureVector() {
		return featureVector;
	}

	public List<Double> getTrueOutputDenormalized() {
		return trueOutputDenormalized;
	}

	public List<Double> getTrueOutput() {
		return trueOuput;
	}

	public String getLabel() {
		return label;
	}
}
