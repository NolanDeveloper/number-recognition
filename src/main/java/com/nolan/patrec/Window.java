package com.nolan.patrec;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

public class Window extends JFrame {

    private JLabel label;
    private Perceptron perceptron;

    public Window() throws HeadlessException, IOException {
        super();
        perceptron = new Perceptron(new FileInputStream("perceptron.data"));
        Webcam webcam = Webcam.getDefault();
        webcam.addWebcamListener(new WebcamListener() {
            @Override
            public void webcamOpen(WebcamEvent we) {
            }

            @Override
            public void webcamClosed(WebcamEvent we) {
            }

            @Override
            public void webcamDisposed(WebcamEvent we) {
            }

            @Override
            public void webcamImageObtained(WebcamEvent we) {
                Image image = we.getImage();
                BufferedImage scaledVersion = new BufferedImage(28, 28, BufferedImage.TYPE_BYTE_GRAY);
                Graphics g = scaledVersion.getGraphics();
                g.drawImage(image.getScaledInstance(28, 28, Image.SCALE_AREA_AVERAGING), 0, 0, null);
                label.setIcon(new ImageIcon(image));
                double[] input = new double[28 * 28];
                for (int i = 0; i < 28 * 28; ++i) {
                    input[i] = 1 - (scaledVersion.getData().getDataBuffer().getElem(0, i) / 256.) < 0.5 ? 0 : 1;
                }
                int answer = Perceptron.getAnswer(perceptron.run(input));
                label.setText("" + answer);
            }
        });
        webcam.open(true);
        setLayout(new GridBagLayout());
        setSize(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        {
            label = new JLabel();
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 1;
            add(label, c);
        }
        setVisible(true);
    }
}
