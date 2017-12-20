package com.nolan.patrec;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class Window extends JFrame {

    private static class WebcamToLabelStreamer implements WebcamListener {
        private JLabel webcamImageLabel;
        private JLabel greyscaleImageLabel;
        private Perceptron perceptron;

        public WebcamToLabelStreamer(JLabel webcamImageLabel, JLabel greyscaleImageLabel, Perceptron perceptron) {
            this.webcamImageLabel = webcamImageLabel;
            this.greyscaleImageLabel = greyscaleImageLabel;
            this.perceptron = perceptron;
        }

        @Override
        public void webcamOpen(WebcamEvent we) {
        }

        @Override
        public void webcamClosed(WebcamEvent we) {
        }

        @Override
        public void webcamDisposed(WebcamEvent we) {
        }

        private static BufferedImage scaleImage(final BufferedImage image, final int width, final int height) {
            final BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
            final Graphics graphics = scaled.getGraphics();
            graphics.drawImage(image.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, width, height, null);
            graphics.dispose();
            return scaled;
        }

        @Override
        public void webcamImageObtained(WebcamEvent we) {
            BufferedImage webcamImage = we.getImage();
            webcamImageLabel.setIcon(new ImageIcon(webcamImage));
            BufferedImage scaledImage = scaleImage(webcamImage, 28, 28);
            greyscaleImageLabel.setIcon(new ImageIcon(scaledImage));
            double[] input = new double[28 * 28];
            scaledImage.getRaster().getPixels(0, 0, 28, 28, input);
            for (int i = 0; i < input.length; ++i) {
                input[i] = 1 - input[i];
            }
            double[] output = perceptron.run(input);
            int answer = Perceptron.getAnswer(output);
            webcamImageLabel.setText("" + answer);
            greyscaleImageLabel.setText(String.format("%.3f", output[answer]));
        }
    }

    private JLabel webcamImageLabel;
    private JLabel greyscaleImageLabel;
    private JComboBox<Webcam> webcamComboBox;
    private Perceptron perceptron;
    private Webcam activeWebcam;

    private static DataSet getTestDataSet() throws IOException {
        Class clazz = Main.class;
        InputStream images = clazz.getResourceAsStream("/t10k-images.idx3-ubyte");
        InputStream labels = clazz.getResourceAsStream("/t10k-labels.idx1-ubyte");
        return new DataSet(images, labels);
    }

    public Window() throws HeadlessException, IOException {
        super();
        perceptron = new Perceptron(Window.class.getResourceAsStream("/perceptron.data"));
        DataSet testDataSet = getTestDataSet();
        testDataSet.test(perceptron);
        setLayout(new GridBagLayout());
        setSize(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        {
            List<Webcam> webcams;
            webcams = Webcam.getWebcams();
            webcamComboBox = new JComboBox<>(new Vector<>(webcams));
            webcamComboBox.setRenderer((jList, webcam, i, b, b1) -> new JLabel(Objects.toString(webcam, "")));
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            add(webcamComboBox, c);
        }
        {
            JButton button = new JButton("Подключиться");
            button.addActionListener(actionEvent -> {
                Webcam selectedWebcam = (Webcam)webcamComboBox.getSelectedItem();
                if (null == selectedWebcam) return;
                if (null != activeWebcam) {
                    activeWebcam.close();
                }
                activeWebcam = selectedWebcam;
                selectedWebcam.addWebcamListener(
                        new WebcamToLabelStreamer(webcamImageLabel, greyscaleImageLabel, perceptron));
                selectedWebcam.open(true);
            });
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 1;
            c.gridy = 0;
            add(button, c);
        }
        {
            webcamImageLabel = new JLabel();
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 1;
            add(webcamImageLabel, c);
        }
        {
            greyscaleImageLabel = new JLabel();
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 2;
            add(greyscaleImageLabel, c);
        }
        setVisible(true);
    }
}
