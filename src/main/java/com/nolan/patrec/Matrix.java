package com.nolan.patrec;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Matrix {
    private final int numberOfRows;
    private final int numberOfColumns;
    private final List<Double> data;

    public Matrix(final int numberOfRows, final int numberOfColumns, final List<Double> data) {
        if (numberOfRows * numberOfColumns != data.size()) {
            throw new Error("Data size doesn't match matrix size.");
        }
        this.numberOfRows = numberOfRows;
        this.numberOfColumns = numberOfColumns;
        this.data = data;
    }

    public Matrix(final Matrix other) {
        this(other.numberOfRows, other.numberOfColumns, new ArrayList<>(other.data));
    }

    private static List<Double> generateData(
            final int numberOfRows, final int numberOfColumns,
            final BiFunction<Integer, Integer, Double> generator) {
        ArrayList<Double> result = new ArrayList<>(numberOfRows * numberOfColumns);
        for (int i = 0; i < numberOfRows; ++i) {
            for (int j = 0; j < numberOfColumns; ++j) {
                result.set(i * numberOfColumns + j, generator.apply(i, j));
            }
        }
        return result;
    }

    public Matrix(final int numberOfRows, final int numberOfColumns,
                  final BiFunction<Integer, Integer, Double> generator) {
        this(numberOfRows, numberOfColumns,
                generateData(numberOfRows, numberOfColumns, generator));
    }

    public Matrix(final int numberOfRows, final int numberOfColumns, Double value) {
        this(numberOfRows, numberOfColumns, (i, j) -> value);
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public int getNumberOfColumns() {
        return numberOfColumns;
    }

    public List<Double> getData() {
        return data;
    }

    public Double get(final int row, final int col) {
        return data.get(row * numberOfColumns + col);
    }

    public Matrix times(final Matrix other) {
        if (numberOfColumns != other.numberOfRows) {
            throw new Error("Left matrix numberOfColumns and right matrix numberOfRows don't match.");
        }
        return new Matrix(numberOfRows, other.numberOfColumns, (i, j) -> {
            double t = 0;
            for (int k = 0; k < numberOfColumns; ++k) {
                t += get(i, k) * other.get(k, j);
            }
            return t;
        });
    }

    private static Matrix zip(final Matrix left, final Matrix right,
                              final BiFunction<Double, Double, Double> combinator) {
        if (left.numberOfRows != right.numberOfRows) {
            throw new Error("Right matrix has different number of numberOfRows.");
        }
        if (left.numberOfColumns != right.numberOfColumns) {
            throw new Error("Right matrix has different number of numberOfColumns.");
        }
        return new Matrix(left.numberOfRows, left.numberOfColumns, (i, j) -> {
            Double a = left.get(i, j);
            Double b = right.get(i, j);
            return combinator.apply(a, b);
        });
    }

    public Matrix plus(final Matrix other) {
        return zip(this, other, (x, y) -> x + y);
    }

    public Matrix minus(final Matrix other) {
        return zip(this, other, (x, y) -> x - y);
    }

    public Matrix negate() {
        return map(x -> -x);
    }

    public Matrix map(final Function<Double, Double> updater) {
        return new Matrix(numberOfRows, numberOfColumns, (i, j) -> updater.apply(get(i, j)));
    }

    public Matrix map(final BiFunction<Integer, Integer, Function<Double, Double>> updater) {
        return new Matrix(numberOfRows, numberOfColumns, (i, j) -> updater.apply(i, j).apply(get(i, j)));
    }

    public void assertSingleRow() {
        if (1 != getNumberOfRows()) {
            throw new Error("Output is not single row matrix.");
        }
    }

    public void assertSingleColumn() {
        if (1 != getNumberOfColumns()) {
            throw new Error("Output is not single row matrix.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Matrix matrix = (Matrix) o;
        if (numberOfRows != matrix.numberOfRows) return false;
        if (numberOfColumns != matrix.numberOfColumns) return false;
        return data.equals(matrix.data);
    }

    @Override
    public int hashCode() {
        int result = numberOfRows;
        result = 31 * result + numberOfColumns;
        result = 31 * result + data.hashCode();
        return result;
    }
}
