package tool;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;

import detection.BoundingBox;

public class CreateForeOrBackgroundSample {
	static File boxRegressionLable = new File("dataset/standardOutput/foreground_bBoxRegressionLabels.txt"),
			gtBoxLabel = new File("dataset/standardOutput/pictures_groundTruthBoxAndLabel.txt"); //
	static BufferedWriter out;

	static int scales[] = { 28, 56, 112, 224 };
	static double ratios[][] = { { 1, 1 }, { 1, 1.5 }, { 1.5, 1 }, { 1, 1.8 }, { 1.8, 1 }, { 1, 2.1 }, { 2.1, 1 },
			{ 1, 2.4 }, { 2.4, 1 } };
	static int scalars[] = IntStream.range(0, ratios.length * scales.length * 2)
			.map(i -> (int) (scales[i / ratios.length / 2] * ratios[i / 2 % ratios.length][i % 2])).toArray();
	static double W = 28, H = 28, overlapThreshhold = 0.7;
	static int resolutionW = 320, resolutionH = 240, girdLen = 8, numPositiveSample = 2, numNegativeSample = 2,
			countF = 0, countB = 0;
	static Random rand = new Random();
	static BufferedImage bi, biTmp, biNew, biOut;
	static Graphics g;
	static BoundingBox b;

	public static void main(String[] args) throws IOException {
		long l = System.currentTimeMillis();
		out = new BufferedWriter(new FileWriter(boxRegressionLable));
		out.write("id | ground truth box normalized movement | foreground box | ground truth box | label\n");
		List<File> images = Files.walk(Paths.get("dataset/pictures"), 1).map(p -> p.toFile()).filter(File::isFile)
				.sorted((f1, f2) -> Integer.parseInt(f1.getName().toString().replace(".jpg", "")) - Integer.parseInt//
				(f2.getName().toString().replace(".jpg", ""))).collect(Collectors.toList());
		BufferedReader br = new BufferedReader(new FileReader(gtBoxLabel));
		BoundingBox boxGT, boxRand;
		List<String> lineList = br.lines().collect(Collectors.toList());
		br.close();
		List<BoundingBox> foreground, background;
		for (int i = 1; i < lineList.size(); i++) {
			String[] parts = lineList.get(i).split("\\|"), box = parts[1].split(",");
			int id = Integer.parseInt(parts[0]), xL1 = Integer.parseInt(box[0]), yT1 = Integer.parseInt(box[1]),
					w1 = Integer.parseInt(box[2]), h1 = Integer.parseInt(box[3]);
			String label = parts[2]; // "1" for the ground truth box
			boxGT = new BoundingBox(xL1, yT1, w1, h1);
			bi = ImageIO.read(images.get(id));
			biTmp = new BufferedImage(320, 240, BufferedImage.TYPE_INT_RGB);
			biTmp.setData(bi.getData());
			g = bi.createGraphics();
			foreground = new ArrayList<>();
			background = new ArrayList<>();
			for (int x = 0; x < resolutionW; x += girdLen)
				for (int y = 0; y < resolutionH; y += girdLen)
					for (int s = 0; s < scalars.length; s += 2) { // "2" for the bounding boxes with different ratios
						int w2 = scalars[s], h2 = scalars[s + 1], xL2 = x - w2 / 2, yT2 = y - h2 / 2;
						boxRand = new BoundingBox(xL2, yT2, w2, h2);
						double overlapPercent = CreatePictureForObjectDetectionFromMNIST.IOU(boxGT, boxRand);
						if (overlapPercent > overlapThreshhold)
							foreground.add(boxRand);
						else if (0.05 < overlapPercent && overlapPercent < 0.5)
							background.add(boxRand);
					}
			g.setColor(Color.BLUE);
			createSamplePictures(foreground, label, numPositiveSample, "foreground", id, w1, h1, xL1, yT1);
			g.setColor(Color.RED);
			createSamplePictures(background, label, numNegativeSample - (int) Math.round(rand.nextDouble()),
					"background", id, w1, h1, xL1, yT1);
			g.setColor(Color.GREEN);
			g.drawRect(xL1, yT1, w1, h1);
			g.drawString("ground truth", Math.max(0, xL1), Math.max(0, yT1));
			ImageIO.write(bi, "jpg", new File("dataset/groundTruthExamples/" + id + ".jpg"));
			System.out.println(id + 1 + "/" + images.size() + ", count: foreground=" + countF + ",boreground=" + countB
					+ "," + (System.currentTimeMillis() - l) / 1000.0);
		}
		System.out.println((System.currentTimeMillis() - l) / 1000.0);
		out.close();
	}

	public static void createSamplePictures(List<BoundingBox> sample, String label, int num, String dir, int exampleID,
			int w, int h, int xL, int yT) throws IOException {
		int size = Math.min(num, sample.size());
		biNew = new BufferedImage(28, 28, BufferedImage.TYPE_INT_RGB);
		for (int n = 0; n < size; n++) {
			b = sample.get(size == num ? rand.nextInt(sample.size()) : n);
			g.drawRect(b.getX(), b.getY(), b.getW(), b.getH());
			g.drawString(dir, Math.max(0, b.getX()), Math.max(0, b.getY()));
			// above is for drawing pos or neg rect in pictures in ../groundTruthExamples
			double wS = W / b.getW(), hS = H / b.getH();
			biOut = CreatePictureForObjectDetectionFromMNIST.scaleNearest(biTmp, wS, hS);
			for (int x = 0, x1 = (int) (b.getX() * wS); x < 28; x++, x1++)
				for (int y = 0, y1 = (int) (b.getY() * hS); y < 28; y++, y1++)
					biNew.setRGB(x, y, (x1 < 0 || y1 < 0 || x1 > (int) (320 * wS) - 1 || y1 > (int) (240 * hS) - 1) ? //
							-1 : biOut.getRGB(x1, y1));
			ImageIO.write(biNew, "jpg",
					new File("dataset/" + dir + "/" + (dir.equals("foreground") ? countF : countB++) + ".jpg"));
			if (dir.equals("foreground"))
				out.write(countF++ + "|" + exampleID + "|" + sigmoid((b.getX() - xL) / (double) b.getW(), 1) + ","
						+ sigmoid((b.getY() - yT) / (double) b.getH(), 1) + "," + sigmoid((double) w / b.getW(), 1e4)
						+ "," + sigmoid((double) h / b.getH(), 1e4) + "|" + b.getX() + "," + b.getY() + "," + b.getW()
						+ "," + b.getH() + "|" + xL + "," + yT + "," + w + "," + h + "|" + label + "\n");
		}
	}

	public static double sigmoid(Double sum, double leftOrRight) {
		return 1 / (1 + Math.exp(-sum * 10) * leftOrRight);
	}

}
