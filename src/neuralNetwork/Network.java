package neuralNetwork;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import gui.MainFrame;

public class Network {
	private List<Neuron> neurons = new ArrayList<>();

	public Network(MainFrame mf) {
		for (int i = 0; i < mf.outputSize; i++)
			neurons.add(new Neuron());
	}

	public void setAllNeuronsInputs(List<Double> inputvector) {
		neurons.forEach(n -> n.setInputs(inputvector));
	}

	public void modifyAllNeuronsWeights(List<Double> label) {
		neurons.forEach(n -> n.totalCost += n.modifyWeights(label.get(neurons.indexOf(n)), n.sum()));
	}

	public List<Neuron> getNeurons() {
		return neurons;
	}

	public List<Double> getOutputs() {
		return neurons.stream().map(n -> n.sum()).collect(Collectors.toList());

	}

	public void saveWeights(String type) {
		DataOutputStream out = null;
		try {
			out = new DataOutputStream(new FileOutputStream(new File("dataset/weights/" + type + ".w")));
			for (Neuron n : neurons)
				for (int i = 0; i < n.weights.length; i++)
					out.writeDouble(n.weights[i]);
			out.flush();
			out.close();
		} catch (IOException e) {
		}
	}

	@SuppressWarnings("deprecation")
	public void readWeights(String type) {
		DataInputStream in = null;
		try {
			in = new DataInputStream(
					new BufferedInputStream(new File("dataset/weights/" + type + ".w").toURL().openStream()));
			for (Neuron n : neurons)
				for (int i = 0; i < n.weights.length; i++)
					n.weights[i] = in.readDouble();
			in.close();
		} catch (IOException e) {
		}
	}
}
// 150 1.5, 50 1, 50 0.5, 50 0.25, 1 0.1
// 100 2, 50 1.5, 50 1, 50 0.5, 25 0.25
