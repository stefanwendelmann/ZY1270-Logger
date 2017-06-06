package de.stefanwndelmann.zy1270logger.Test;

import de.stefanwndelmann.zy1270logger.ZY1270Data;
import com.fazecast.jSerialComm.*;
import java.util.Date;
import java.util.Scanner;

/**
 *
 * @author swendelmann
 */
public class TestSerial {

    public static void main(String[] args) {
        SerialPort[] commPorts = SerialPort.getCommPorts();
        for (SerialPort commPort : commPorts) {
            System.out.println(commPort.getSystemPortName());
        }
        SerialPort commPort = SerialPort.getCommPort("COM18");
        commPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
        if (commPort.openPort()) {
            Scanner scanner = new Scanner(commPort.getInputStream());
            while (true) {
                while (scanner.hasNextLine()) {
                    try {
                        String line = scanner.nextLine();
                        System.out.println(line);
                        System.out.println(ZY1270Data.parseString(line,new Date()));
                    } catch (Exception e) {
                       
                    }
                }
            }
        }
    }

}
