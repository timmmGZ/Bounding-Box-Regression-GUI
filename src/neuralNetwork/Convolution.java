package neuralNetwork;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Convolution {
	public List<double[][]> filters = Arrays.asList(//
			new double[][] { { 1, 0, -1 }, { 2, 0, -2 }, { 1, 0, 1 } }, // |
			new double[][] { { 1, 2, 1 }, { 0, 0, 0 }, { -1, -2, -1 } }, // ——
			new double[][] { { 0, -1, 1 }, { 0, -1, 1 }, { 0, -1, 1 } }, // |
			new double[][] { { 0, 1, -1 }, { 0, 1, -1 }, { 0, 1, -1 } }, // |
			new double[][] { { 1, 0, -1 }, { 1, 0, -1 }, { 1, 0, -1 } }, // |
			// new double[][] { { -1, 0, 1 }, { -1, 0, 1 }, { -1, 0, 1 } }, // |
			new double[][] { { 1, -1, 0 }, { 1, -1, 0 }, { 1, -1, 0 } }, // |
			new double[][] { { -1, 1, 0 }, { -1, 1, 0 }, { -1, 1, 0 } }, // |
			new double[][] { { 0, 0, 0 }, { 1, 1, 1 }, { -1, -1, -1 } }, // ——
			new double[][] { { 0, 0, 0 }, { -1, -1, -1 }, { 1, 1, 1 } }, // ——
			// new double[][] { { 1, 1, 1 }, { 0, 0, 0 }, { -1, -1, -1 } }, // ——
			new double[][] { { -1, -1, -1 }, { 0, 0, 0 }, { 1, 1, 1 } }, // ——
			new double[][] { { 1, 1, 1 }, { -1, -1, -1 }, { 0, 0, 0 } }, // ——
			new double[][] { { -1, -1, -1 }, { 1, 1, 1 }, { 0, 0, 0 } }, // ——
			new double[][] { { 1, 1, 0 }, { 1, 0, -1 }, { 0, -1, -1 } }, ///
			new double[][] { { 0, 1, 1 }, { -1, 0, 1 }, { -1, -1, 0 } }, // \
			new double[][] { { 0, -1, -1 }, { 1, 0, -1 }, { 1, 1, 0 } }, // \
			new double[][] { { -1, -1, 0 }, { -1, 0, 1 }, { 0, 1, 1 } }); ///

	public double[][] firstLayerFeatureMapsSum;
	public List<double[][]> firstLayerFeaturesVector, secondLayerFeaturesVector, nFeaturesVector;

	public void setupAllLayers(double[][] inputs) {
		firstLayerFeaturesVector = features(inputs, filters, 1, true, true, 2, 2);
		firstLayerFeatureMapsSum = matrixSum(firstLayerFeaturesVector);
		nFeaturesVector = secondLayerFeaturesVector = features(firstLayerFeatureMapsSum, filters, 1, true, true, 2, 2);
	}

	public void setupAllLayers(double[][] inputs, int i) {
		setupAllLayers(inputs);
		for (int n = 2; n < i; n++)
			nFeaturesVector = features(matrixSum(nFeaturesVector), filters, 1, true, true, 2, 2);
		for (int ii = 0; ii < nFeaturesVector.size(); ii++)
			nFeaturesVector.set(ii, normalization(nFeaturesVector.get(ii)));
	}

	public List<Double> getFeatureVector() {
		return nFeaturesVector.stream().map(f -> Arrays.stream(f).map(Arrays::stream)//
				.map(e -> e.boxed()).flatMap(s -> s)).flatMap(s -> s).collect(Collectors.toList());
	}

	public double[][] matrixSum(List<double[][]> features) {
		double[][] outputs = new double[features.get(0).length][features.get(0)[0].length];
		for (double[][] feature : features)
			for (int y = 0; y < features.get(0).length; y++)
				for (int x = 0; x < features.get(0)[0].length; x++)
					outputs[y][x] += feature[y][x];
		return outputs;
	}

	public List<double[][]> features(double[][] inputs, List<double[][]> filters, int stride, boolean padding,
			boolean relu, int poolingFilterSize, int poolingStride) {
		return filters.stream().map(f -> pooling(convolution(inputs, f, stride, padding, relu)//
				, poolingFilterSize, poolingStride)).collect(Collectors.toList());
	}

	public static double[][] normalization(double[][] input) {
		double max = 0;
		for (int y = 0; y < input.length; y++)
			for (int x = 0; x < input[0].length; x++)
				max = input[y][x] > max ? input[y][x] : max;
		double scale = max == 0 ? 1 : max;
		return Arrays.stream(input).map(a -> Arrays.stream(a).map(d -> d / scale).toArray()).toArray(double[][]::new);
	}

	public double[][] createDrawing(double[][] input) {
		return matrixSum(filters.stream().map(f -> convolution(input, f, 1, true, true)).collect(Collectors.toList()));
	}

	public double[][] pooling(double[][] inputs, int filterSize, int stride) {
		int sizeH = (int) Math.floor(inputs.length / stride), sizeW = (int) Math.floor(inputs[0].length / stride);
		double[][] output = new double[sizeH][sizeW];
		for (int y = 0; y < output.length; y++)
			for (int x = 0; x < output[0].length; x++)
				output[y][x] = max(inputs, filterSize, x * stride, y * stride);
		return output;
	}

	public double max(double[][] inputs, int filterSize, int X, int Y) {
		double output = 0;
		for (int y = 0; y < filterSize; y++)
			for (int x = 0; x < filterSize; x++)
				output = output > inputs[Y + y][X + x] ? output : inputs[Y + y][X + x];
		return output;
	}

	public double[][] convolution(double[][] inputs, double[][] filter, int stride, boolean padding, boolean relu) {
		inputs = padding ? pad(inputs, (filter.length - 1) / 2) : inputs;
		int sizeH = (int) Math.ceil((inputs.length - filter.length + 1) / stride);
		int sizeW = (int) Math.ceil((inputs[0].length - filter.length + 1) / stride);
		double[][] output = new double[sizeH][sizeW];
		for (int y = 0; y < output.length; y++)
			for (int x = 0; x < output[0].length; x++) {
				double conv = convolveOnePixel(inputs, filter, x * stride, y * stride);
				output[y][x] = relu ? Math.max(0, conv) : conv;
			}
		return output;
	}

	public double convolveOnePixel(double[][] inputs, double[][] filter, int X, int Y) {
		double output = 0;
		for (int y = 0; y < filter.length; y++)
			for (int x = 0; x < filter[0].length; x++)
				output += filter[y][x] * inputs[Y + y][X + x];
		return output;

	}

	public double[][] pad(double[][] inputs, int size) {
		double[][] output = new double[inputs.length + size * 2][inputs[0].length + size * 2];
		for (int y = 0; y < inputs.length; y++)
			System.arraycopy(inputs[y], 0, output[y + size], size, inputs[y].length);
		return output;
	}
}
