package tool;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import detection.BoundingBox;
import neuralNetwork.DataSet;

public class CreatePictureForObjectDetectionFromMNIST {
	static Random rand = new Random();
	static File groundTruthBoxAndLabel = new File("dataset/standardOutput/pictures_groundTruthBoxAndLabel.txt"); //

	static int mnistW = 28, mnistH = 28, outputW = 320, outputH = 240;

	public static void main(String[] args) throws IOException {
		long l = System.currentTimeMillis();
		new DataInfo("mnist");
		DataInfo.getTrainSets().addAll(DataInfo.getTestSets());
		createPicturesAndLabels(DataInfo.getTrainSets());
		System.out.println((System.currentTimeMillis() - l) / 1000.0);
	}

	public static void createPicturesAndLabels(List<DataSet> dataSet) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(groundTruthBoxAndLabel));
		out.write("id | left edge X, top edge Y, W, H | Label\n");
		BufferedImage bi, biNew;
		for (int i = 0; i < dataSet.size(); i++) {
			double[][] map = dataSet.get(i).getInputs();
			bi = new BufferedImage(mnistW, mnistH, BufferedImage.TYPE_INT_RGB);
			biNew = new BufferedImage(outputW, outputH, BufferedImage.TYPE_INT_RGB);
			biNew.getGraphics().fillRect(0, 0, outputW, outputH);
			for (int x = 0; x < mnistW; x++)
				for (int y = 0; y < mnistH; y++)
					bi.setRGB(x, y, (int) ((1 - map[y][x]) * 255) << 16 | (int) ((1 - map[y][x]) * 255) << 8
							| (int) ((1 - map[y][x]) * 255));
			double wScalar = rand.nextDouble() * 10 + 1,
					hScalar = Math.max(1, wScalar + rand.nextDouble() * (rand.nextDouble() > 0.5 ? 3 : -3));
			int w = (int) (mnistW * wScalar), h = (int) (mnistH * hScalar), xL = (int) (rand.nextInt(outputW) - w / 2),
					yT = (int) (rand.nextInt(outputH) - h / 2);
			if (IOU(new BoundingBox(0, 0, outputW, outputH), new BoundingBox(xL, yT, w, h)) < 0.7
					&& !(xL > 0 && yT > 0 && xL + w < outputW && yT + h < outputH)) {
				i--;
				continue;
			}
			biNew.getGraphics().drawImage(scaleNearest(bi, wScalar, hScalar), xL, yT, null);
			ImageIO.write(biNew, "jpg", new File("dataset/pictures/" + i + ".jpg"));
			out.write(i + "|" + xL + "," + yT + "," + w + "," + h + "|" + dataSet.get(i).getLabel() + "\n");
			System.out.println(i + "/" + dataSet.size());

		}
		out.close();

	}

	public static BufferedImage scaleNearest(BufferedImage img, double wScalar, double hScalar) {
		BufferedImage newImage = new BufferedImage((int) (img.getWidth() * wScalar), (int) (img.getHeight() * hScalar),
				img.getType());
		AffineTransform scaleInstance = AffineTransform.getScaleInstance(wScalar, hScalar);
		AffineTransformOp scaleOp = new AffineTransformOp(scaleInstance, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		scaleOp.filter(img, newImage);
		return newImage;
	}

	public static double IOU(BoundingBox a, BoundingBox b) {
		int areaA = a.getH() * a.getW(), areaB = b.getH() * b.getW();
		int wTotal = Math.max(a.getX() + a.getW(), b.getX() + b.getW()) - Math.min(a.getX(), b.getX()),
				hTotal = Math.max(a.getY() + a.getH(), b.getY() + b.getH()) - Math.min(a.getY(), b.getY()),
				wOverlap = wTotal - a.getW() - b.getW(), hOverlap = hTotal - a.getH() - b.getH(),
				areaOverlap = (wOverlap >= 0 || hOverlap >= 0) ? 0 : wOverlap * hOverlap;
		return (double) areaOverlap / (areaA + areaB - areaOverlap);
	}
}
