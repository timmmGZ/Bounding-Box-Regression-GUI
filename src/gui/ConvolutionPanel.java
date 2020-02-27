package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JPanel;

import neuralNetwork.*;

public class ConvolutionPanel extends JPanel {
	/**
	 * Github:timmmGZ 2020/02/25
	 */
	private static final long serialVersionUID = 1L;	public int pixelW = 5, pixelH = 5;
	public List<Double> vector;
	public double[][] firstLayerFeatureMapsSum;
	public List<double[][]> firstLayerFeaturesVector, secondLayerFeaturesVector;
	public Convolution conv = new Convolution();
	public boolean showLines = true, useCNN = false, showWeights = false;
	public List<Neuron> neurons;
	public DataSet currentDataSet;

	public ConvolutionPanel(Network n) {
		setBackground(new Color(156, 206, 209));
		setPreferredSize(new Dimension(1000, 630));
		neurons = n.getNeurons();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (useCNN && !showWeights)
			try {
				conv.setupAllLayers(currentDataSet.getInputs());
				firstLayerFeaturesVector = conv.firstLayerFeaturesVector;
				firstLayerFeatureMapsSum = Convolution.normalization(conv.firstLayerFeatureMapsSum);
				secondLayerFeaturesVector = conv.secondLayerFeaturesVector;
				drawFirstLayerFeatureMapsSum(g);
				drawFirstLayerFeatureMaps(g);
				drawSecondLayerFeatureMaps(g);
				drawFeatureVector(g);
				drawFilters(g);
				g.setColor(Color.BLACK);
				g.drawString(
						"16 Convolution filters(3p*3p)           First Layer: 16 Feature Maps(14p*14p)            Firs"
								+ "t Layer:                            Second Layer:                            <！Feature vector (784p*1p)",
						70, 10);
				g.drawString(
						"16 to 1 (addition)             16 Feature Maps(7p*7p)                     of 16 feature maps",
						490, 25);
				g.drawString("using the same 16 filters", 465, 220);
				g.drawString("in my case", 500, 235);
				g.drawString("Finllay put this feature   ！！！\\", 830, 310);
				g.drawString(" Vector to Neural Network！！！/", 820, 320);
			} catch (Exception e) {
			}
		else if (showWeights)
			drawWeights(g);
	}

	public void drawWeights(Graphics g) {
		for (int i = 0; i < neurons.size(); i++) {
			for (int y = 0; y < 28; y++)
				for (int x = 0; x < 28; x++) {
					int c = colorValue(neurons.get(i).weights[y * 28 + x], 0);
					g.setColor(neurons.get(i).weights[y * 28 + x] > 0 ? new Color(c, c, 255) : new Color(255, c, c));
					g.fillRect(65 + (i % 5) * 185 + x * pixelW, 140 + (i / 5) * 200 + y * pixelH, pixelW, pixelH);
					if (x == 0 && y == 0) {
						g.setColor(Color.BLACK);
						g.drawString("neuron for number " + i, 75 + (i % 5) * 185, 125 + (i / 5) * 200 + y);
					}
				}
		}
	}

	public void drawFilters(Graphics g) {
		for (int y = 0; y < 24; y++)
			for (int x = 0; x < 6; x++) {
				int grey = colorValue(conv.filters.get(y / 3 * 2 + x / 3)[y % 3][x % 3], 1);
				g.setColor(new Color(grey, grey, grey));
				g.fillRect(100 + 60 * (x / 3) + x * pixelW, 58 + 60 * (y / 3) + y * pixelH, pixelW, pixelH);
				if (y % 3 == 0 && x % 3 == 0 && showLines) {
					g.setColor(Color.RED);
					g.drawLine(100 + 60 * (x / 3) + x * pixelW, 65 + 60 * (y / 3) + y * pixelH, 0, 320);
					g.drawLine(630 + 60 * (x / 3) + x * pixelW, 65 + 60 * (y / 3) + y * pixelH, 570, 325);
					g.setColor(Color.YELLOW);
					int a[] = { 110 + 60 * (x / 3) + x * pixelW, 305 + 60 * (x / 3) + x * pixelW, 500 },
							b[] = { 63 + 60 * (y / 3) + y * pixelH, 35 + 60 * (y / 3) + y * pixelH, 325 };
					g.drawPolyline(a, b, 3);
				}
			}
	}

	public void drawFirstLayerFeatureMaps(Graphics g) {
		for (int y = 0; y < 112; y++)
			for (int x = 0; x < 28; x++) {
				int grey = colorValue(firstLayerFeaturesVector.get(y / 14 * 2 + x / 14)[y % 14][x % 14], 1);
				g.setColor(new Color(grey, grey, grey));
				g.fillRect(300 + 5 * (x / 14) + x * pixelW, 30 + 5 * (y / 14) + y * pixelH, pixelW, pixelH);
			}
	}

	public void drawFirstLayerFeatureMapsSum(Graphics g) {
		for (int y = 0; y < firstLayerFeatureMapsSum.length; y++)
			for (int x = 0; x < firstLayerFeatureMapsSum.length; x++) {
				int grey = colorValue(firstLayerFeatureMapsSum[y][x] * 2, 1);
				g.setColor(new Color(grey, grey, grey));
				g.fillRect(500 + 5 * (x / 14) + x * pixelW, 295 + 5 * (y / 14) + y * pixelH, pixelW, pixelH);
			}
	}

	public void drawSecondLayerFeatureMaps(Graphics g) {
		for (int y = 0; y < 56; y++)
			for (int x = 0; x < 14; x++) {
				int grey = colorValue(secondLayerFeaturesVector.get(y / 7 * 2 + x / 7)[y % 7][x % 7] / 10, 1);
				g.setColor(new Color(grey, grey, grey));
				g.fillRect(630 + 40 * (x / 7) + x * pixelW, 48 + 40 * (y / 7) + y * pixelH, pixelW, pixelH);
				g.setColor(Color.GREEN);
				if ((x == 6 || x == 13) && showLines)
					g.drawLine(635 + 40 * (x / 7) + x * pixelW, 48 + 40 * (y / 7) + y * pixelH,
							(x == 6 ? 778 : 700) + 40 * (x / 7) + x * pixelW, (x == 6 ? 0 : 1500) + y * 35);
			}
	}

	public void drawFeatureVector(Graphics g) {
		for (int i = 0; i < vector.size(); i++) {
			int grey = colorValue(vector.get(i) / 10, 1);
			g.setColor(new Color(grey, grey, grey));
			g.fillRect(800 + pixelW, i * pixelH, 5, 5);
		}
	}

	public void setCurrentWordsConvDrawing(DataSet dataSet) {
		currentDataSet = dataSet;
		vector = useCNN ? dataSet.getFeatureVector() : dataSet.getInputsVector();
		repaint();
	}

	public int colorValue(double w, int type) {
		return (int) (type == 0 ? 255 / Math.exp(w * w * (useCNN ? 100000 : 100)) : 255 - 255 / (1 + Math.exp(-w)));
	}

	public List<Double> getVector() {
		return vector;
	}
}
