package ltps1516.gr121gr122.control;

import jssc.SerialPort;
import jssc.SerialPortException;
import ltps1516.gr121gr122.control.comport.ComPort;

/**
 * Created by rob on 02-12-15.
 */
public class SerialController {

    private SerialPort serialPort;

    public SerialController() {
        // Singleton serialport
        serialPort = ComPort.getInstance().getSerialPort();
    }

    public void test() {
        // Check serial connection
        if(serialPort != null) {
            // Thread for sending serial data
            new Thread(() -> {
                while(true) {
                    try {
                        serialPort.writeBytes("Java\\n".getBytes());
                    } catch (SerialPortException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
