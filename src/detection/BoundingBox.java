package detection;

import java.util.List;

public class BoundingBox {
	private int x, y, w, h;

	public BoundingBox(int cX, int cY, int W, int H) {
		x = cX;
		y = cY;
		w = W;
		h = H;
	}

	public BoundingBox(List<Integer> pred) {
		this(pred.get(0), pred.get(1), pred.get(2), pred.get(3));
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getW() {
		return w;
	}

	public int getH() {
		return h;
	}

	public double getWScalar() {
		return w;
	}

	public double getHScalar() {
		return h;
	}

	public String toString() {
		return x + "," + y + "," + w + "," + h;
	}

}
