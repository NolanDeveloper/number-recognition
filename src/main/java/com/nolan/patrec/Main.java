package com.nolan.patrec;

import java.io.IOException;
import java.io.InputStream;

public class Main {

    private static DataSet getTrainDataSet() throws IOException {
        Class clazz = Main.class;
        InputStream images = clazz.getResourceAsStream("/train-images.idx3-ubyte");
        InputStream labels = clazz.getResourceAsStream("/train-labels.idx1-ubyte");
        return new DataSet(images, labels);
    }

    private static DataSet getTestDataSet() throws IOException {
        Class clazz = Main.class;
        InputStream images = clazz.getResourceAsStream("/t10k-images.idx3-ubyte");
        InputStream labels = clazz.getResourceAsStream("/t10k-labels.idx1-ubyte");
        return new DataSet(images, labels);
    }

    public static Perceptron trainPerceptron() throws IOException {
        DataSet trainDataSet = getTrainDataSet();
        DataSet testDataSet = getTestDataSet();
        Perceptron perceptron = new Perceptron(trainDataSet.height * trainDataSet.width, 10, 0.05);
        trainDataSet.train(perceptron, 10);
        testDataSet.test(perceptron);
        return perceptron;
    }

    public static void main(final String... args) throws IOException {
        //new Window();
        trainPerceptron();
    }
}
