package ltps1516.gr121gr122.control.comport;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

/**
 * Created by rob on 30-11-15.
 */
public class ComPort {
    // Instance
    private static ComPort ourInstance = null;

    // Field
    private SerialPort serialPort;

    // Instance getter
    public static ComPort getInstance() {
        // Lazy initialization
        if(ourInstance == null) {
            ourInstance = new ComPort();
        }

        return ourInstance;
    }

    // Constructor for initialization
    private ComPort() {
        String[] ports = SerialPortList.getPortNames();

        if(ports.length > 0) {
            serialPort = new SerialPort(ports[0]);
            try {
                // Config serialport
                serialPort.openPort();
                serialPort.setParams(SerialPort.BAUDRATE_9600,
                        SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);

                //int mask = SerialPort.MASK_RXCHAR;
                //serialPort.setEventsMask(mask);

                // Register readerlistener
                serialPort.addEventListener(new ComPortReader(serialPort));

                // Register shutdownhook for closing serialport
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        serialPort.closePort();
                    } catch (SerialPortException e) {
                        e.printStackTrace();
                    }
                }));
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
        } else {
            serialPort = null;
        }
    }

    // Getter
    public SerialPort getSerialPort() {
        return serialPort;
    }
}
