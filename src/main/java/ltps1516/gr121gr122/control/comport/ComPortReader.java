package ltps1516.gr121gr122.control.comport;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

/**
 * Created by rob on 30-11-15.
 */
public class ComPortReader implements SerialPortEventListener {

    private SerialPort serialPort;

    public ComPortReader(SerialPort serialPort) {
        this.serialPort = serialPort;
    }

    public void serialEvent(SerialPortEvent event) {
        if(event.isRXCHAR()) {
            try {
                System.out.println(serialPort.readString());
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Wrong serial data");
        }
    }
}
