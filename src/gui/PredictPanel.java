package gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

public class PredictPanel extends JPanel {
	/**
	 * Github:timmmGZ 2020/02/25
	 */
	private static final long serialVersionUID = 1L;
	MainFrame mainFrame;
	List<JTextArea> texts = new ArrayList<>();

	public PredictPanel(MainFrame frame) {
		setLayout(new BorderLayout());
		mainFrame = frame;
		for (int i = 0; i < frame.outputSize; i++)
			texts.add(new JTextArea());
		setLeft();
		setRight();
	}

	public void setLeft() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 1));
		panel.setBackground(new Color(0, 206, 209));
		String[] classes = { "x:  ", "y:  ", "w:  ", "h:  " };
		for (String word : classes)
			panel.add(new JLabel(word, SwingConstants.CENTER));
		add(panel, BorderLayout.WEST);
	}

	public void setRight() {
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(100, 0));
		panel.setLayout(new GridLayout(0, 1));
		panel.setBackground(new Color(0, 206, 209));
		for (JTextArea text : texts) {
			text.setBackground(new Color(0, 206, 209));
			panel.add(text);
		}
		add(panel, BorderLayout.CENTER);
	}

	public void updateProbs(List<Integer> b, List<Integer> p, List<Integer> gt) {
		for (int i = 0; i < b.size(); i++) {
			texts.get(i).setText("\n\n\n\n\nInput: " + b.get(i) + "\nPrediction: " + p.get(i)
					+ (gt == null ? "" : "\nGround truth: " + gt.get(i)));
		}

	}
}
