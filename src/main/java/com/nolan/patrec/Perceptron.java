package com.nolan.patrec;

import java.io.*;

public class Perceptron {
    private int inputSize;
    private int outputSize;
    private double[][] weights;
    private double eta;

    public Perceptron(final int inputSize, final int outputSize, final double eta) {
        this.inputSize = inputSize;
        this.outputSize = outputSize;
        this.eta = eta;
        weights = new double[outputSize][inputSize];
        for (int i = 0; i < outputSize; ++i) {
            for (int j = 0; j < inputSize; ++j) {
                weights[i][j] = 0;
            }
        }
    }

    public Perceptron(final InputStream inputStream) throws IOException {
        DataInputStream in = new DataInputStream(inputStream);
        inputSize = in.readInt();
        outputSize = in.readInt();
        eta = in.readDouble();
        for (int i = 0; i < outputSize; ++i) {
            for (int j = 0; j < inputSize; ++j) {
                weights[i][j] = in.readDouble();
            }
        }
    }

    public void save(final OutputStream outputStream) throws IOException {
        DataOutputStream out = new DataOutputStream(outputStream);
        out.writeInt(inputSize);
        out.writeInt(outputSize);
        out.writeDouble(eta);
        for (int i = 0; i < outputSize; ++i) {
            for (int j = 0; j < inputSize; ++j) {
                out.writeDouble(weights[i][j]);
            }
        }
    }

    public double[] train(final double[] input, final double[] expectedOutput) {
        if (input.length != inputSize) {
            throw new IllegalArgumentException("Length of input doesn't match inputSize.");
        }
        if (expectedOutput.length != outputSize) {
            throw new IllegalArgumentException("Bad expectedOutput length.");
        }
        double[] output = run(input);
        for (int i = 0; i < outputSize; ++i) {
            for (int j = 0; j < inputSize; ++j) {
                weights[i][j] += eta * (expectedOutput[i] - output[i]) * input[j];
            }
        }
        return output;
    }

    public double activationFunction(final double x) {
        return 1 / (1 + Math.exp(-x));
    }

    public double[] run(final double[] input) {
        if (input.length != inputSize) {
            throw new IllegalArgumentException("Length of input doesn't match inputSize.");
        }
        double[] result = new double[outputSize];
        for (int i = 0; i < outputSize; ++i) {
            double s = 0;
            for (int j = 0; j < input.length; ++j) {
                s += input[j] * weights[i][j];
            }
            result[i] = activationFunction(s);
        }
        return result;
    }

    public static int getAnswer(final double[] output) {
        int maxIndex = 0;
        double maxValue = -Double.MAX_VALUE;
        for (int i = 0; i < output.length; ++i) {
            if (output[i] < maxValue) continue;
            maxIndex = i;
            maxValue = output[i];
        }
        return maxIndex;
    }
}
