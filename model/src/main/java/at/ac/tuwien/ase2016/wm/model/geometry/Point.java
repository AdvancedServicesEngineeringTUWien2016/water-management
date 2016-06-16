package at.ac.tuwien.ase2016.wm.model.geometry;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.validation.constraints.NotNull;

public class Point implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final double x;    // longitude
	private final double y;    // latitude

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Point(Point p) {
		this.x = p.x;
		this.y = p.y;
	}

	@NotNull
	public double getX() {
		return x;
	}

	@NotNull
	public double getY() {
		return y;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}
	
	public static Point fromString(String s) {
		// The format is '(' '-'? [0-9]+ ('.' [0-9]+)? ',' '-'? [0-9]+ ('.' [0-9]+)? ')'
		char[] chars = s.toCharArray();
		int end = chars.length - 1;
		
		if (chars.length < 5 || chars[0] != '(' || chars[end] != ')') {
			// Shortest possible is (0,0)
			throw new IllegalArgumentException("Invalid format");
		}
		
		StringBuilder sb = new StringBuilder();
		double x = 0, y = 0;
		
		ParserMode m = ParserMode.START;
		for (int i = 1; i < end; i++) {
			final char c = chars[i];
			switch (m) {
			case START:
				if (Character.isDigit(c)) {
					sb.append(c);
					m = ParserMode.X_BEFORE_DOT;
				} else if (c == '-') {
					sb.append(c);
					m = ParserMode.MINUS_BEFORE_X;
				} else {
					bail(c, i, Arrays.asList("DIGIT", "MINUS"));
				}
				break;
			case MINUS_BEFORE_X:
				if (Character.isDigit(c)) {
					sb.append(c);
					m = ParserMode.X_BEFORE_DOT;
				} else {
					bail(c, i, Arrays.asList("DIGIT"));
				}
				break;
			case X_BEFORE_DOT:
				if (Character.isDigit(c)) {
					sb.append(c);
				} else if (c == '.') {
					sb.append(c);
					m = ParserMode.X_DOT;
				} else if (c == ',') {
					x = Double.parseDouble(sb.toString());
					sb.setLength(0);
					m = ParserMode.SEPARATOR;
				} else {
					bail(c, i, Arrays.asList("DIGIT", "DOT"));
				}
				break;
			case X_DOT:
				if (Character.isDigit(c)) {
					sb.append(c);
					m = ParserMode.X_AFTER_DOT;
				} else {
					bail(c, i, Arrays.asList("DIGIT"));
				}
				break;
			case X_AFTER_DOT:
				if (Character.isDigit(c)) {
					sb.append(c);
				} else if (c == ',') {
					x = Double.parseDouble(sb.toString());
					sb.setLength(0);
					m = ParserMode.SEPARATOR;
				} else {
					bail(c, i, Arrays.asList("DIGIT", "SEPARATOR"));
				}
				break;
			case SEPARATOR:
				if (Character.isDigit(c)) {
					sb.append(c);
					m = ParserMode.Y_BEFORE_DOT;
				} else if (c == '-') {
					sb.append(c);
					m = ParserMode.MINUS_BEFORE_Y;
				} else if (Character.isWhitespace(c)) {
					// skip
				} else {
					bail(c, i, Arrays.asList("DIGIT", "MINUS", "SPACE"));
				}
				break;
			case MINUS_BEFORE_Y:
				if (Character.isDigit(c)) {
					sb.append(c);
					m = ParserMode.Y_BEFORE_DOT;
				} else {
					bail(c, i, Arrays.asList("DIGIT"));
				}
				break;
			case Y_BEFORE_DOT:
				if (Character.isDigit(c)) {
					sb.append(c);
				} else if (c == '.') {
					sb.append(c);
					m = ParserMode.Y_DOT;
				} else {
					bail(c, i, Arrays.asList("DIGIT", "DOT"));
				}
				break;
			case Y_DOT:
				if (Character.isDigit(c)) {
					sb.append(c);
					m = ParserMode.Y_AFTER_DOT;
				} else {
					bail(c, i, Arrays.asList("DIGIT"));
				}
				break;
			case Y_AFTER_DOT:
				if (Character.isDigit(c)) {
					sb.append(c);
				} else {
					bail(c, i, Arrays.asList("DIGIT"));
				}
				break;
			}
		}
		
		if (m != ParserMode.Y_AFTER_DOT && m != ParserMode.Y_BEFORE_DOT) {
			throw new IllegalArgumentException("Invalid format");
		}
		
		y = Double.parseDouble(sb.toString());
		return new Point(x, y);
	}
	
	private static void bail(char c, int i, List<String> expected) {
		throw new IllegalArgumentException("Invalid character '" + c + "' at position " + i + "! Expected " + expected);
	}
	
	private static enum ParserMode {
		START,
		MINUS_BEFORE_X,
		X_BEFORE_DOT,
		X_DOT,
		X_AFTER_DOT,
		SEPARATOR,
		MINUS_BEFORE_Y,
		Y_BEFORE_DOT,
		Y_DOT,
		Y_AFTER_DOT
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
	
	
}
