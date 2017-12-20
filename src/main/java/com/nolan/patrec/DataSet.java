package com.nolan.patrec;

import java.io.*;

class DataSet {
    private final double[][] images;
    private final int[] labels;
    public final int size;
    public final int width;
    public final int height;

    public DataSet(final InputStream imagesInputStream, final InputStream labelsInputStream) throws IOException {
        DataInputStream imagesDataInputStream = new DataInputStream(imagesInputStream);
        DataInputStream labelsDataInputStream = new DataInputStream(labelsInputStream);
        if (0x00000803 != imagesDataInputStream.readInt()) {
            throw new Error("Magic number doesn't match.");
        }
        if (0x00000801 != labelsDataInputStream.readInt()) {
            throw new Error("Magic number doesn't match.");
        }
        int numberOfImages = imagesDataInputStream.readInt();
        int numberOfLabels = labelsDataInputStream.readInt();
        if (numberOfImages != numberOfLabels) {
            throw new Error("Number of labels is not same as number of images.");
        }
        size = numberOfImages;
        images = new double[size][];
        labels = new int[size];
        height = imagesDataInputStream.readInt();
        width = imagesDataInputStream.readInt();
        for (int i = 0; i < numberOfImages; ++i) {
            images[i] = new double[height * width];
            for (int j = 0; j < height * width; ++j) {
                images[i][j] = imagesDataInputStream.readUnsignedByte() / 256.;
            }
            labels[i] = labelsDataInputStream.readUnsignedByte();
        }
    }

    public void train(final Perceptron perceptron, final int numberOfEpochs) {
        double previousErrorRate = 0;
        for (int k = 0; k < numberOfEpochs; ++k) {
            int numberOfErrors = 0;
            for (int i = 0; i < size; ++i) {
                double[] image = images[i];
                int label = labels[i];
                double[] expectedOutput = new double[10];
                for (int j = 0; j < expectedOutput.length; ++j) {
                    expectedOutput[j] = j == label ? 1 : 0;
                }
                double[] outputs = perceptron.train(image, expectedOutput);
                if (Perceptron.getAnswer(outputs) != label) ++numberOfErrors;
            }
            double errorRate = (double) numberOfErrors / size * 100;
            double delta = errorRate - previousErrorRate;
            previousErrorRate = errorRate;
            System.out.println("" + k + ") Training error rate(%): " + errorRate + " (" + delta + ")");
        }
    }

    public void test(final Perceptron perceptron) {
        int numberOfErrors = 0;
        for (int i = 0; i < size; ++i) {
            double[] image = images[i];
            int label = labels[i];
            double[] output = perceptron.run(image);
            if (Perceptron.getAnswer(output) != label) ++numberOfErrors;
        }
        System.out.println("Error rate(%): " + (double)numberOfErrors / size * 100);
    }
}
