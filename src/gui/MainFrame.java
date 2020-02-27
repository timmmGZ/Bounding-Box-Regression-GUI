package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javax.swing.*;
import detection.BoundingBox;
import neuralNetwork.*;
import tool.*;

public class MainFrame extends JFrame {
	/**
	 * Github:timmmGZ 2020/02/25
	 */
	private static final long serialVersionUID = 1L;
	public int trainSize, testSize, outputSize = 4;
	Trainer trainer;
	DataInfo info;
	PredictPanel probPanel;
	public DrawingPanel drawPanel;
	public Network network = new Network(this);
	private JComboBox<String> falseWordsBox;
	public JTextArea lossTextArea = new JTextArea();
	private JTextField trainingTimes = new JTextField("K epoches"), testingTimes = new JTextField("K times"),
			lRate = new JTextField("0.00008");
	public JLabel accuracy = new JLabel("?", SwingConstants.CENTER), predict = new JLabel("?", SwingConstants.CENTER);
	private JButton clear = new JButton("Clear to be default"), train = new JButton("Train train sets"),
			showLines = new JButton("Show lines for CNN"), test = new JButton("Test random drawing"),
			showFalse = new JButton("Show false prediction of"), lR = new JButton("Learning rate"),
			clearDrawing = new JButton("Clear Drawing");;
	private JCheckBox testSet = new JCheckBox("Test test sets"), trainSet = new JCheckBox("Test train sets"),
			cnnOrAnn = new JCheckBox("use CNN model"), testEachOnce = new JCheckBox("Test each data only once"),
			keepBestWeights = new JCheckBox("Keep best weights"), bP = new JCheckBox("Use back Propagation"),
			drawWeights = new JCheckBox("Draw weights");
	private static Map<String, List<DataSet>> falseSet = new TreeMap<>();
	public static Color darkBlue = new Color(0, 206, 209), lightBlue = new Color(156, 206, 209);
	private JMenuItem example = new JMenuItem("example"), read = new JMenuItem("Read weights"),
			save = new JMenuItem("Save weights");
	public static DecimalFormat df = new DecimalFormat("0.0000");
	public String currentExampleID;
	private ExamplePanel examplePanel;

	public static void main(String[] args) throws IOException, InterruptedException {
		new MainFrame();
	}

	public MainFrame() throws IOException {
		super("Github:TimmmGZ ¡ª¡ªBounding Box Regression (CNN and ANN)");
		setVisible(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setReadFilePanel();
		info = new DataInfo("foreOrBackground");
		falseSet.put("less IOU", new ArrayList<DataSet>());
		trainSize = DataInfo.getTrainSets().size();
		testSize = DataInfo.getTestSets().size();
		drawPanel = new DrawingPanel(new ConvolutionPanel(network), this);
		trainer = new Trainer(this);
		probPanel = new PredictPanel(this);
		falseWordsBox = new JComboBox<>();
		falseWordsBox.addItem("less IOU");
		setBottuns();
		setCheckBoxs();
		setLeft();
		setRight();
		setMenu();
		getRootPane().getGlassPane().setVisible(true);
		pack();
		setLocationRelativeTo(null);
	}

	private void setReadFilePanel() {
		JPanel panel = new JPanel(new BorderLayout());
		JProgressBar jpb = new JProgressBar();
		jpb.setStringPainted(true);
		panel.add(jpb);
		addWithColor(panel, new JLabel("Creating data sets...                         "), darkBlue, BorderLayout.NORTH);
		addWithColor(this, panel, darkBlue, null);
		pack();
		setLocationRelativeTo(null);
		new Thread(() -> {
			while (ReadFile.i < ReadFile.dataSetAmount) {
				jpb.setValue(ReadFile.i / (ReadFile.dataSetAmount / 100));
				jpb.repaint();
			}
			remove(panel);
		}).start();
	}

	private void setMenu() {
		JMenu menu = new JMenu("Menu");
		menu.add(example);
		menu.add(read);
		JMenuBar jmb = new JMenuBar();
		jmb.add(menu).add(save);
		setJMenuBar(jmb);
	}

	private void setLeft() {
		JPanel panel = new JPanel(new BorderLayout());
		setLeftUp(panel);
		setLeftDown(panel);
		lossTextArea.setEditable(false);
		lossTextArea.setBounds(125, 575, 350, 110);
		addWithColor(this, lossTextArea, lightBlue, null);
		add(panel);
	}

	private void setLeftUp(JPanel jPanel) {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(33, 100, 33, 0));
		panel.add(drawPanel, BorderLayout.EAST);
		panel.add(drawPanel.convPanel, BorderLayout.WEST);
		addWithColor(jPanel, panel, lightBlue, BorderLayout.NORTH);
	}

	private void setLeftDown(JPanel jPanel) {
		JPanel panel = new JPanel(new GridLayout(0, 9, 3, 0));
		JPanel labels = new JPanel(new GridLayout(0, 1, 0, 0));
		predict.setFont(new Font("MS Song", Font.BOLD, 30));
		accuracy.setFont(new Font("MS Song", Font.BOLD, 30));
		labels.add(predict);
		labels.add(accuracy);
		panel.add(train);
		panel.add(test);
		panel.add(lR);
		panel.add(showFalse);
		panel.add(clearDrawing);
		addWithColor(panel, cnnOrAnn, darkBlue, null);
		addWithColor(panel, testSet, darkBlue, null);
		addWithColor(panel, keepBestWeights, darkBlue, null);
		addWithColor(panel, drawWeights, darkBlue, null);
		panel.add(save);
		panel.add(trainingTimes);
		panel.add(testingTimes);
		panel.add(lRate);
		panel.add(falseWordsBox);
		panel.add(clear);
		addWithColor(panel, testEachOnce, darkBlue, null);
		addWithColor(panel, trainSet, darkBlue, null);
		addWithColor(panel, bP, darkBlue, null);
		panel.add(showLines);
		panel.add(read);
		addWithColor(jPanel, labels, darkBlue, BorderLayout.CENTER);
		addWithColor(jPanel, panel, darkBlue, BorderLayout.SOUTH);
	}

	private void setRight() {
		add(probPanel, BorderLayout.EAST);
	}

	private void setCheckBoxs() {
		testSet.setSelected(true);
		testSet.addActionListener(e -> trainSet.setSelected(true));
		trainSet.addActionListener(e -> testSet.setSelected(true));
		bP.addActionListener(e -> Neuron.useBP = bP.isSelected() ? true : false);
		testEachOnce.addActionListener(e -> {
			testingTimes.setText("1");
			testingTimes.setEditable(testEachOnce.isSelected() ? false : true);
		});
		keepBestWeights.addActionListener(e -> Neuron.keepBestWeights = keepBestWeights.isSelected() ? true : false);
		drawWeights.addActionListener(e -> {
			drawPanel.convPanel.showWeights = drawWeights.isSelected() ? true : false;
			drawPanel.convPanel.repaint();
		});
		cnnOrAnn.addActionListener(e -> {
			drawPanel.convPanel.useCNN = cnnOrAnn.isSelected() ? true : false;
			// Neuron.normalNN = cnnOrAnn.isSelected() ? false : true;
			drawPanel.convPanel.repaint();
			JOptionPane.showMessageDialog(null, "Remember to click Clear and Train again weights", "Attention",
					JOptionPane.WARNING_MESSAGE);
		});
	}

	private void setBottuns() {
		MyActionListener m = new MyActionListener();
		setButton(m, "example", example);
		setButton(m, "rate", lR);
		setButton(m, "test", test);
		setButton(m, "save", save);
		setButton(m, "read", read);
		setButton(m, "clear", clear);
		setButton(m, "train", train);
		setButton(m, "line", showLines);
		setButton(m, "showFalse", showFalse);
		setButton(m, "clearDrawing", clearDrawing);
	}

	private void setButton(ActionListener m, String action, AbstractButton b) {
		b.setActionCommand(action);
		b.addActionListener(m);
	}

	class MyActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("line")) {
				drawPanel.convPanel.showLines = drawPanel.convPanel.showLines ? false : true;
				drawPanel.convPanel.repaint();
				return;
			} else if (e.getActionCommand().equals("clear")) {
				Trainer.epoch = 1;
				falseSet.entrySet().forEach(w -> w.getValue().clear());
				lossTextArea.setText("");
				network.getNeurons().forEach(n -> {
					n.weights = new double[785];
					n.oldWeights = new double[785];
				});
				repaint();
			} else if (e.getActionCommand().equals("clearDrawing")) {
				drawPanel.clear();
			} else if (e.getActionCommand().equals("save")) {
				network.saveWeights("BoundingBoxRegression" + (cnnOrAnn.isSelected() ? "Cnn" : "Ann"));
			} else if (e.getActionCommand().equals("read")) {
				network.readWeights("BoundingBoxRegression" + (cnnOrAnn.isSelected() ? "Cnn" : "Ann"));
				drawPanel.convPanel.repaint();
			}
			try {
				if (e.getActionCommand().equals("example")) {
					examplePanel = new ExamplePanel(MainFrame.this);
				} else if (e.getActionCommand().equals("train")) {
					trainer.train(Integer.parseInt(trainingTimes.getText()) * trainSize, cnnOrAnn.isSelected() ? 0 : 1);
				} else if (e.getActionCommand().equals("rate")) {
					Neuron.learningRate = Double.parseDouble(lRate.getText());
				} else if (e.getActionCommand().equals("test")) {
					int number = Integer.parseInt(testingTimes.getText());
					falseSet.entrySet().forEach(w -> w.getValue().clear());
					number = testEachOnce.isSelected() ? (testSet.isSelected() ? (trainSet.isSelected() ? //
							trainSize + testSize : testSize) : trainSize) : number;
					int testCount = 0;
					for (int i = 0; i < number; i++) {
						DataSet rand = drawPanel.drawRandomWord(testSet.isSelected() ? //
								(trainSet.isSelected() ? 2 : 0) : 1, testEachOnce.isSelected() ? i : -1);
						double ious[] = regressionProcessing(rand, i == number - 1 ? true : false);
						testCount += ious[1] > ious[0] ? 1 : 0;
						predict.setText("ID: " + rand.getLabel() + ", IOU=" + df.format(ious[1]));
						accuracy.setText("Accuracy: " + ((int) ((double) testCount / number * 10000)) / 100.0 + "% ("
								+ testCount + "/" + number + ")");
						if (ious[1] <= ious[0])
							falseSet.get("less IOU").add(rand);
					}
				} else if (e.getActionCommand().equals("showFalse")) {
					DataSet rand = drawPanel.drawFalseWord(falseSet.get(falseWordsBox.getSelectedItem()));
					double ious[] = regressionProcessing(rand, true);
					predict.setText("ID " + rand.getLabel() + ", old IOU=" + df.format(ious[0]) + ", predict IOU="
							+ df.format(ious[1]));
					accuracy.setText("Number of false: " + falseSet.get("less IOU").size());
				}
			} catch (Exception exc) {
				JOptionPane.showMessageDialog(null, "Input or something wrong!", "Error", JOptionPane.WARNING_MESSAGE);
				exc.printStackTrace();
			}
		}
	}

	public double[] regressionProcessing(DataSet input, boolean isLast) throws IOException {
		network.setAllNeuronsInputs(drawPanel.convPanel.getVector());
		List<Integer> box = input.getBox().stream().map(Double::intValue).collect(Collectors.toList()),
				pred = boundingBoxDenormalization(network.getOutputs(), box), groundTruth = input
						.getTrueOutputDenormalized().stream().map(Double::intValue).collect(Collectors.toList());
		if (isLast) {
			probPanel.updateProbs(box, pred, groundTruth);
			drawPanel.setBoundingBox(box, pred, groundTruth);
			currentExampleID = input.getLabel().substring(input.getLabel().indexOf("ID") + 4,
					input.getLabel().indexOf("("));
			if (examplePanel != null)
				examplePanel.setImage(currentExampleID);
		}
		double ious[] = { // [0] is box-GroundTruth IOU,[1] is prediction-GroundTruth IOU
				CreatePictureForObjectDetectionFromMNIST.IOU(new BoundingBox(box), new BoundingBox(groundTruth)),
				CreatePictureForObjectDetectionFromMNIST.IOU(new BoundingBox(pred), new BoundingBox(groundTruth)) };
		return ious;
	}

	public List<Integer> boundingBoxDenormalization(List<Double> pred, List<Integer> box) {
		pred.set(0, box.get(0) + Math.log((1 / pred.get(0) - 1)) / 10 * box.get(2));
		pred.set(1, box.get(1) + Math.log((1 / pred.get(1) - 1)) / 10 * box.get(3));
		pred.set(2, -Math.log((1 / pred.get(2) - 1) / 1e4) / 10 * box.get(2));
		pred.set(3, -Math.log((1 / pred.get(3) - 1) / 1e4) / 10 * box.get(3));
		return pred.stream().map(d -> (int) Math.round(d)).collect(Collectors.toList());
	}

	public void addWithColor(Container container, JComponent jC, Color color, Object constraints) {
		container.add(jC, constraints);
		jC.setBackground(color);
	}
}
