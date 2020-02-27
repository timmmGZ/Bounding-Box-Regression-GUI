package tool;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import neuralNetwork.*;

public class ReadFile {
	public static int i = 0, resolutionW = 28, resolutionH = 28, dataSetAmount = 10000;

	public static List<DataSet> readFromSingleTxt(String filename) throws IOException {
		long t = System.currentTimeMillis();
		Convolution conv = new Convolution();
		List<DataSet> trainingSets = new ArrayList<>();
		for (Scanner s = new Scanner(new FileInputStream(new File("dataset/" + filename))); s.hasNextLine();) {
			String[] line = s.nextLine().split("\\|");
			String word = String.valueOf(line[line.length - 1]);
			double[][] trainSetInputs = new double[28][28];
			for (int y = 0; y < 28; y++)
				for (int x = 0; x < 28; x++)
					trainSetInputs[x][y] = Integer.parseInt(line[y * 28 + x]) / 255.0;
			trainingSets.add(new DataSet(trainSetInputs, DataInfo.getTrueOutput(word), word, conv));

			System.out.println(i++);
		}
		System.out.println((System.currentTimeMillis() - t) / 1000.0 + " seconds");
		return trainingSets;
	}

	public static List<DataSet> readFromPictures(String dir, boolean isBBoxRegression) throws IOException {
		Convolution conv = new Convolution();
		List<DataSet> trainingSets = new ArrayList<>();
		List<File> files = Files.walk(Paths.get("dataset/" + dir), 1).map(p -> p.toFile()).filter(File::isFile)
				.limit(dataSetAmount).collect(Collectors.toList());

		for (File f : files) {
			System.out.println(i++);
			// System.out.println(f);
			BufferedImage bi = ImageIO.read(f);
			double[][] input = new double[bi.getHeight()][bi.getWidth()];

			Raster r = bi.getData();
			for (int y = 0; y < bi.getHeight(); y++)
				for (int x = 0; x < bi.getWidth(); x++) {
					int[] tmp = r.getPixel(x, y, (int[]) null);
					input[y][x] = 1.0 - (tmp[0] + tmp[1] + tmp[2]) / 765.0;
				}
			String id = f.getName().replace(".jpg", ""), exampleID = ", example ID: "
					+ DataInfo.getExampleID(id).get(0).intValue() + "(in ../groundTruthExamples/)";
			List<Double> output = new ArrayList<>(isBBoxRegression ? DataInfo.getTrueOutput(f.getName().replace(//
					".jpg", "")) : dir.contains("foreground") ? Arrays.asList(1.0, 0.0) : Arrays.asList(0.0, 1.0));
			trainingSets.add(new DataSet(input, output, id + exampleID, conv, isBBoxRegression ? //
					DataInfo.getBox(id) : null, isBBoxRegression ? DataInfo.getTrueOutputDenormalized(id) : null));
		}
		return trainingSets;
	}

}
