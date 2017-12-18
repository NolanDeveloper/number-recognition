package com.nolan.patrec;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class Window extends JFrame {

    private static class WebcamToLabelStreamer implements WebcamListener {
        private JLabel label;
        private Perceptron perceptron;

        public WebcamToLabelStreamer(JLabel label, Perceptron perceptron) {
            this.label = label;
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
    }

    private JLabel label;
    private JComboBox<Webcam> webcamComboBox;
    private Perceptron perceptron;
    private Optional<Webcam> activeWebcam = Optional.empty();

    public Window() throws HeadlessException, IOException {
        super();
        perceptron = new Perceptron(Window.class.getResourceAsStream("/perceptron.data"));
        setLayout(new GridBagLayout());
        setSize(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        {
            List<Webcam> webcams;
            try {
                webcams = Webcam.getWebcams(500);
            } catch (TimeoutException e) {
                webcams = new ArrayList<>();
            }
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
                activeWebcam.ifPresent(Webcam::close);
                activeWebcam = Optional.of(selectedWebcam);
                selectedWebcam.addWebcamListener(new WebcamToLabelStreamer(label, perceptron));
                selectedWebcam.open(true);
            });
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 1;
            c.gridy = 0;
            add(button, c);
        }
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
