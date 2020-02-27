package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javax.swing.*;
import neuralNetwork.*;
import tool.DataInfo;

public class DrawingPanel extends JPanel implements MouseMotionListener, MouseListener {
	/**
	 * Github:timmmGZ 2020/02/25
	 */
	private static final long serialVersionUID = 1L;	public static int pixelW = 13, pixelH = 13, resolution = 28;
	protected double[][] map, mapDrawing = new double[resolution][resolution];
	public ConvolutionPanel convPanel;
	MainFrame mainFrame;
	boolean hasGrountruth = false;
	Convolution conv = new Convolution();
	List<Integer> pred, box, gT;

	public DrawingPanel(ConvolutionPanel cP, MainFrame m) throws IOException {
		map = new double[resolution][resolution];
		convPanel = cP;
		setPreferredSize(new Dimension(364, 364));
		setBackground(Color.WHITE);
		addMouseMotionListener(this);
		addMouseListener(this);
		mainFrame = m;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (int y = 0; y < resolution; y++)
			for (int x = 0; x < resolution; x++) {
				int grey = 255 - (int) (map[y][x] * 255);
				g.setColor(new Color(grey, grey, grey));
				g.fillRect(x * pixelW, y * pixelH, pixelW, pixelH);
			}
		if (pred != null)
			drawBoundingBox();
	}

	public DataSet drawFalseWord(List<DataSet> falseSet) {
		DataSet random = falseSet.get((int) (Math.random() * falseSet.size()));
		map = random.getInputs();
		convPanel.setCurrentWordsConvDrawing(random);
		repaint();
		return random;
	}

	public DataSet drawRandomWord(int type, int i) {// type=0->testSet, type=1->trainSet, type=2->both
		Random r = new Random();
		hasGrountruth = true;
		DataSet random = i == -1 ? (type == 0 ? DataInfo.getTestSets().get(r.nextInt(mainFrame.testSize))//
				: (type == 1 ? DataInfo.getTrainSets().get(r.nextInt(mainFrame.trainSize))
						: r.nextDouble() > 0.5 ? DataInfo.getTestSets().get(r.nextInt(mainFrame.testSize))
								: DataInfo.getTrainSets().get(r.nextInt(mainFrame.trainSize))))
				: (type == 0 ? DataInfo.getTestSets().get(i)
						: (type == 1 ? DataInfo.getTrainSets().get(i)
								: i < mainFrame.trainSize ? DataInfo.getTrainSets().get(i)
										: DataInfo.getTestSets().get(i - mainFrame.trainSize)));
		map = random.getInputs();
		convPanel.setCurrentWordsConvDrawing(random);
		repaint();
		return random;
	}

	void drawBoundingBox() {
		mainFrame.repaint();
		int x = 145 + (int) (13.0 * ((pred.get(0) - box.get(0)) / (box.get(2) / 28.0))),
				y = 195 + (int) (13.0 * ((pred.get(1) - box.get(1)) / (box.get(3) / 28.0))),
				w = (int) (13.0 * (pred.get(2) / (box.get(2) / 28.0))),
				h = (int) (13.0 * (pred.get(3) / (box.get(3) / 28.0)));
		Graphics2D g = (Graphics2D) getRootPane().getGraphics();
		g.setStroke(new BasicStroke(3.0f));
		g.setFont(new Font("MS Song", Font.BOLD, 30));
		if (hasGrountruth) {
			int xGT = 145 + (int) (13.0 * ((gT.get(0) - box.get(0)) / (box.get(2) / 28.0))),
					yGT = 195 + (int) (13.0 * ((gT.get(1) - box.get(1)) / (box.get(3) / 28.0))),
					wGT = (int) (13.0 * (gT.get(2) / (box.get(2) / 28.0))),
					hGT = (int) (13.0 * (gT.get(3) / (box.get(3) / 28.0)));
			g.setColor(new Color(0, 220, 0));
			g.drawRect(xGT, yGT, wGT, hGT);
			g.drawString("Ground Truth box", xGT, yGT);
		}
		g.setColor(Color.ORANGE);
		g.drawRect(x, y, w, h);
		g.drawString("Predicted box", x, y);
		g.setColor(Color.BLUE);
		for (int i = 0; i < 1; i++) {
			g.drawLine(145, 195, 145+364, 195);
			g.drawLine(145, 195+364, 145+364, 195+364);
			g.drawLine(145, 195, 145, 195+364);
			g.drawLine(145+364, 195, 145+364, 195+364);
			g.drawRect(145, 195, 364, 364);
			g.drawString("Forehead box", 145, 195);
		}
	}

	private void setPixel(MouseEvent e) {
		try {
			hasGrountruth = false;
			mapDrawing[e.getY() / pixelH][e.getX() / pixelW] = SwingUtilities.isLeftMouseButton(e) ? 0.8 : 0;
			double[][] input = conv.createDrawing(mapDrawing);
			map = Convolution.normalization(input);
			for (int y = 0; y < map.length; y++)
				for (int x = 0; x < map[0].length; x++)
					input[y][x] = 1 / (1 + Math.exp(-input[y][x] * 2.5) * 255);
			convPanel.setCurrentWordsConvDrawing(new DataSet(input, conv));
			repaint();
			mainFrame.trainer.network.setAllNeuronsInputs(convPanel.getVector());
			mainFrame.predict.setText("Prediction: ");
			mainFrame.accuracy.setText("Prediction of your drawing doesn't have true label");
			box = Arrays.asList(0, 0, 28, 28);
			pred = mainFrame.boundingBoxDenormalization(mainFrame.network.getOutputs(), box);
			mainFrame.probPanel.updateProbs(box, pred, null);
		} catch (Exception exc) {
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		setPixel(e);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		setPixel(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	public void clear() {
		map = new double[28][28];
		mapDrawing = new double[28][28];
		repaint();
		convPanel.setCurrentWordsConvDrawing(new DataSet(map, conv));
	}

	public void setBoundingBox(List<Integer> b, List<Integer> p, List<Integer> gt) {
		pred = p;
		box = b;
		gT = gt;

	}

}
