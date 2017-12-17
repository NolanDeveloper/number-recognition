package com.nolan.patrec;

import java.io.*;
import java.util.ArrayList;

public class Perceptron {
    private int inputSize;
    private int outputSize;
    private Matrix weights;
    private double eta;

    public Perceptron(final int inputSize, final int outputSize, final double eta) {
        this.inputSize = inputSize;
        this.outputSize = outputSize;
        this.eta = eta;
        weights = new Matrix(inputSize, outputSize, 0.0);
    }

    public Perceptron(final InputStream inputStream) throws IOException {
        DataInputStream in = new DataInputStream(inputStream);
        inputSize = in.readInt();
        outputSize = in.readInt();
        eta = in.readDouble();
        ArrayList<Double> data = new ArrayList<>(inputSize * outputSize);
        for (int i = 0; i < outputSize; ++i) {
            for (int j = 0; j < inputSize; ++j) {
                data.add(in.readDouble());
            }
        }
        weights = new Matrix(inputSize, outputSize, data);
    }

    public void save(final OutputStream outputStream) throws IOException {
        DataOutputStream out = new DataOutputStream(outputStream);
        out.writeInt(inputSize);
        out.writeInt(outputSize);
        out.writeDouble(eta);
        for (int i = 0; i < outputSize; ++i) {
            for (int j = 0; j < inputSize; ++j) {
                out.writeDouble(weights.get(i, j));
            }
        }
    }

    public Matrix train(final Matrix input, final Matrix expectedOutput) {
        input.assertSingleRow();
        expectedOutput.assertSingleRow();
        Matrix output = run(input);
        weights = weights.map((i, j) -> old ->
                old + eta * (expectedOutput.get(0, i) - output.get(0, i)) * input.get(0, j));
        return output;
    }

    public double activationFunction(final double x) {
        return 1 / (1 + Math.exp(-x));
    }

    public Matrix run(final Matrix input) {
        input.assertSingleRow();
        return input.times(weights).map(this::activationFunction);
    }

    public static int getAnswer(final Matrix output) {
        output.assertSingleRow();
        int maxIndex = 0;
        double maxValue = -Double.MAX_VALUE;
        for (int i = 0; i < output.getNumberOfColumns(); ++i) {
            if (output.get(0, i) < maxValue) continue;
            maxIndex = i;
            maxValue = output.get(0, i);
        }
        return maxIndex;
    }
}
