package com.nolan.patrec;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Window extends JFrame {

    private Webcam webcam = Webcam.getDefault();

    private JLabel label;

    public Window() throws HeadlessException {
        super();
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
                label.setIcon(new ImageIcon(we.getImage()));
            }
        });
        webcam.setViewSize(new Dimension(640, 480));
        webcam.open(true);
        setLayout(new GridBagLayout());
        setSize(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        {
            JButton button = new JButton("Open picture...");
            button.addActionListener(actionEvent -> {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.showOpenDialog(this);
                try {
                    Image image = ImageIO.read(fileChooser.getSelectedFile());
                    label.setIcon(new ImageIcon(image));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
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
