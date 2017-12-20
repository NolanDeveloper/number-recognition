package com.nolan.patrec;

import java.io.*;
import java.util.Random;

public class Perceptron {
    private int[] layerSizes;
    private int numberOfLayers;
    private double[][][] weights;
    private double eta;

    public Perceptron(final int[] layerSizes, final double eta) {
        this.layerSizes = layerSizes;
        this.numberOfLayers = layerSizes.length;
        this.eta = eta;
        weights = new double[numberOfLayers - 1][][];
        Random random = new Random();
        for (int layer = 0; layer < numberOfLayers - 1; ++layer) {
            final int inputs = layerSizes[layer];
            final int outputs = layerSizes[layer + 1];
            weights[layer] = new double[inputs][outputs];
            for (int i = 0; i < inputs; ++i) {
                for (int j = 0; j < outputs; ++j) {
                    weights[layer][i][j] = random.nextDouble() - 0.5;
                }
            }
        }
    }

    public Perceptron(final InputStream inputStream) throws IOException {
        final DataInputStream in = new DataInputStream(inputStream);
        eta = in.readDouble();
        numberOfLayers = in.readInt();
        layerSizes = new int[numberOfLayers];
        for (int i = 0; i < numberOfLayers; ++i) {
            layerSizes[i] = in.readInt();
        }
        weights = new double[numberOfLayers - 1][][];
        for (int i = 0; i < numberOfLayers - 1; ++i) {
            int inputs = layerSizes[i];
            int outputs = layerSizes[i + 1];
            weights[i] = new double[inputs][outputs];
            for (int j = 0; j < inputs; ++j) {
                for (int k = 0; k < outputs; ++k) {
                    weights[i][j][k] = in.readDouble();
                }
            }
        }
    }

    public void save(final OutputStream outputStream) throws IOException {
        final DataOutputStream out = new DataOutputStream(outputStream);
        out.writeDouble(eta);
        out.writeInt(numberOfLayers);
        for (int size : layerSizes) {
            out.writeInt(size);
        }
        for (int i = 0; i < numberOfLayers - 1; ++i) {
            int inputs = layerSizes[i];
            int outputs = layerSizes[i + 1];
            for (int j = 0; j < inputs; ++j) {
                for (int k = 0; k < outputs; ++k) {
                    out.writeDouble(weights[i][j][k]);
                }
            }
        }
    }

    public double[] train(final double[] input, final double[] expectedOutput) {
        if (input.length != layerSizes[0]) {
            throw new IllegalArgumentException("Length of input doesn't match number of perceptron's inputs.");
        }
        if (expectedOutput.length != layerSizes[numberOfLayers - 1]) {
            throw new IllegalArgumentException(
                    "Length of expectedOutput doesn't match number of perceptron's outputs.");

        }
        // Calculate outputs of every layer.
        double[][] outputs = new double[numberOfLayers][];
        outputs[0] = input;
        for (int layer = 1; layer < numberOfLayers; ++layer) {
            outputs[layer] = new double[layerSizes[layer]];
            for (int i = 0; i < layerSizes[layer - 1]; ++i) {
                for (int j = 0; j < layerSizes[layer]; ++j) {
                    outputs[layer][j] += weights[layer - 1][i][j] * outputs[layer - 1][i];
                }
            }
            for (int i = 0; i < outputs[layer].length; ++i) {
                outputs[layer][i] = activationFunction(outputs[layer][i]);
            }
        }
        // Update weights according to the error i.e. difference between actual and expected output.
        double[] previousDeltas = null;
        for (int layer = numberOfLayers - 2; layer >= 0; --layer) {
            double[] deltas = new double[layerSizes[layer + 1]];
            for (int j = 0; j < layerSizes[layer + 1]; ++j) {
                deltas[j] = 0;
                double y = outputs[layer + 1][j];
                if (numberOfLayers - 2 == layer) {
                    deltas[j] = expectedOutput[j] - y;
                } else {
                    for (int k = 0; k < layerSizes[layer + 2]; ++k) {
                        assert previousDeltas != null;
                        deltas[j] += previousDeltas[k] * weights[layer + 1][j][k];
                    }
                }
                deltas[j] *= y * (1 - y);
                for (int i = 0; i < layerSizes[layer]; ++i) {
                    weights[layer][i][j] += eta * deltas[j] * outputs[layer][i];
                }
            }
            previousDeltas = deltas;
        }
        return outputs[outputs.length - 1];
    }

    public double activationFunction(final double x) {
        return 1 / (1 + Math.exp(-x));
    }

    public double[] run(double[] input) {
        if (input.length != layerSizes[0]) {
            throw new IllegalArgumentException("Length of input doesn't match inputSize.");
        }
        for (int layer = 1; layer < numberOfLayers; ++layer) {
            final double[] output = new double[layerSizes[layer]];
            for (int i = 0; i < layerSizes[layer - 1]; ++i) {
                for (int j = 0; j < layerSizes[layer]; ++j) {
                    output[j] += weights[layer - 1][i][j] * input[i];
                }
            }
            for (int i = 0; i < output.length; ++i) {
                output[i] = activationFunction(output[i]);
            }
            input = output;
        }
        return input;
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
