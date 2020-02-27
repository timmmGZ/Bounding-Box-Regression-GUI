package gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import tool.ReadFile;

public class ExamplePanel extends JPanel {
	/**
	 * Github:timmmGZ 2020/02/25
	 */
	private static final long serialVersionUID = 1L;
	BufferedImage bi;

	public ExamplePanel(MainFrame m) throws IOException {
		ReadFile.resolutionW = 320;
		ReadFile.resolutionH = 240;
		setLayout(null);
		setPreferredSize(new Dimension(340, 260));
		setBackground(MainFrame.lightBlue);
		setVisible(true);
		this.setFocusable(true);
		JFrame jf = new JFrame("Example picture in ../groundTruthExamples");
		jf.add(this);
		jf.pack();
		jf.setVisible(true);
		jf.setResizable(false);
		jf.setAlwaysOnTop(true);
		jf.setLocationRelativeTo(null);
		if (m.currentExampleID != null)
			setImage(m.currentExampleID);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(bi, 15, 15, null);
	}

	public void setImage(String index) throws IOException {
		bi = ImageIO.read(new File("dataset/groundTruthExamples/" + index + ".jpg"));
		repaint();
	}
}
