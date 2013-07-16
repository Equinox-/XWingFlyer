package com.pi.collision;

import java.util.Arrays;

public class Matrix {
	protected float[][] data;

	public Matrix(int rows, int cols) {
		data = new float[rows][cols];
	}

	public void set(int row, int col, float d) {
		data[row][col] = d;
	}

	public void set(int idx, float d) {
		data[idx / data.length][idx % data.length] = d;
	}

	public int getRows() {
		return data.length;
	}

	public int getCols() {
		return data[0].length;
	}

	public float get(int row, int col) {
		return data[row][col];
	}

	public float get(int idx) {
		return data[idx / data.length][idx % data.length];
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Matrix) {
			Matrix m = (Matrix) o;
			if (m.getRows() == getRows() && m.getCols() == getCols()) {
				return Arrays.deepEquals(m.data, data);
			}
		}
		return false;
	}

	@Override
	public Object clone() {
		Matrix mat = new Matrix(data.length, data[0].length);
		for (int i = 0; i < data.length; i++) {
			System.arraycopy(data[i], 0, mat.data[i], 0, data[i].length);
		}
		return mat;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (int y = 0; y < data.length; y++) {
			s.append("[");
			for (int x = 0; x < data[0].length; x++) {
				s.append(data[y][x]);
				if (x < data[0].length - 1)
					s.append(", ");
			}
			s.append("]");
			if (y < data.length - 1)
				s.append(", \n");
		}
		return s.toString();
	}

	public void add(float scalar) {
		for (int x = 0; x < getRows(); x++) {
			for (int y = 0; y < getCols(); y++) {
				data[x][y] += scalar;
			}
		}
	}

	public void subtract(float scalar) {
		add(-scalar);
	}

	public void multiply(float scalar) {
		for (int x = 0; x < getRows(); x++) {
			for (int y = 0; y < getCols(); y++) {
				data[x][y] *= scalar;
			}
		}
	}

	public void divide(float scalar) {
		multiply(1 / scalar);
	}

	public Matrix add(Matrix m) {
		if (m.getRows() != getRows() || m.getCols() != getCols())
			throw new ArrayIndexOutOfBoundsException("Matrix size mismatch!");
		for (int x = 0; x < getRows(); x++) {
			for (int y = 0; y < getCols(); y++) {
				data[x][y] += m.data[x][y];
			}
		}
		return this;
	}

	public Matrix subtract(Matrix m) {
		if (m.getRows() != getRows() || m.getCols() != getCols())
			throw new ArrayIndexOutOfBoundsException("Matrix size mismatch!");
		for (int x = 0; x < getRows(); x++) {
			for (int y = 0; y < getCols(); y++) {
				data[x][y] -= m.data[x][y];
			}
		}
		return this;
	}

	public static Matrix add(Matrix a, Matrix b) {
		return ((Matrix) a.clone()).add(b);
	}

	public static Matrix subtract(Matrix a, Matrix b) {
		return ((Matrix) a.clone()).subtract(b);
	}

	public Matrix transpose() {
		Matrix mat = new Matrix(getCols(), getRows());
		for (int x = 0; x < getRows(); x++) {
			for (int y = 0; y < getCols(); y++) {
				mat.data[y][x] = data[x][y];
			}
		}
		return mat;
	}

	public static Matrix createIdentityMatrix(int size) {
		Matrix mat = new Matrix(size, size);
		for (int i = 0; i < size; i++) {
			mat.data[i][i] = 1;
		}
		return mat;
	}

	public static Matrix multiply(Matrix a, Matrix b) {
		if (a.getRows() != b.getCols())
			throw new ArrayIndexOutOfBoundsException("Matrix size mismatch!");
		Matrix result = new Matrix(a.getRows(), b.getCols());
		for (int r = 0; r < result.getRows(); r++) {
			for (int c = 0; c < result.getCols(); c++) {
				for (int i = 0; i < a.getCols(); i++) {
					result.data[r][c] += (a.data[r][i] * b.data[i][c]);
				}
			}
		}
		return result;
	}
}
