package de.stefanwndelmann.zy1270logger.Test;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import com.fazecast.jSerialComm.SerialPort;
import de.stefanwndelmann.zy1270logger.ZY1270Data;
import java.util.Date;

/**
 *
 * @author swendelmann
 */
public class SerialChartTest {

    static SerialPort chosenPort;
    static int x=0;

    public static void main(String[] args) {

        // create and configure the window
        JFrame window = new JFrame();
        window.setTitle("Sensor Graph GUI");
        window.setSize(600, 400);
        window.setLayout(new BorderLayout());
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // create a drop-down box and connect button, then place them at the top of the window
        JComboBox<String> portList = new JComboBox<String>();
        JButton connectButton = new JButton("Connect");
        JPanel topPanel = new JPanel();
        topPanel.add(portList);
        topPanel.add(connectButton);
        window.add(topPanel, BorderLayout.NORTH);

        // populate the drop-down box
        SerialPort[] portNames = SerialPort.getCommPorts();
        for (int i = 0; i < portNames.length; i++) {
            portList.addItem(portNames[i].getSystemPortName());
        }

        // create the line graph
        XYSeries series = new XYSeries("YZ1270 Logger");
        XYSeriesCollection dataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYLineChart("YZ1270 Reading", "Time (seconds)", "U/I", dataset);
        window.add(new ChartPanel(chart), BorderLayout.CENTER);

        // configure the connect button and use another thread to listen for data
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (connectButton.getText().equals("Connect")) {
                    // attempt to connect to the serial port
                    chosenPort = SerialPort.getCommPort(portList.getSelectedItem().toString());
                    chosenPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
                    if (chosenPort.openPort()) {
                        connectButton.setText("Disconnect");
                        portList.setEnabled(false);
                    }

                    // create a new thread that listens for incoming text and populates the graph
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            Scanner scanner = new Scanner(chosenPort.getInputStream());
                            while (true) {
                                while (scanner.hasNextLine()) {
                                    try {
                                        String line = scanner.nextLine();
                                        ZY1270Data data = ZY1270Data.parseString(line,new Date());
                                        series.add(x, data.getAmp());
                                        x++;
                                        window.repaint();
                                    } catch (Exception e) {
                                    }
                                }
                            }
                        }
                    };
                    thread.start();
                } else {
                    // disconnect from the serial port
                    chosenPort.closePort();
                    portList.setEnabled(true);
                    connectButton.setText("Connect");
                    series.clear();
                    x = 0;
                }
            }
        });

        // show the window
        window.setVisible(true);
    }

}
