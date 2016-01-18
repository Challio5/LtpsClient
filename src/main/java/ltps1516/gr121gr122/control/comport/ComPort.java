package ltps1516.gr121gr122.control.comport;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortException;
import jssc.SerialPortList;
import ltps1516.gr121gr122.control.api.ApiController;
import ltps1516.gr121gr122.model.Context;
import ltps1516.gr121gr122.model.Model;
import ltps1516.gr121gr122.model.user.NfcCard;
import ltps1516.gr121gr122.model.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.*;

/**
 * Created by rob on 30-11-15.
 */
public class ComPort {
    // Instance
    private final static ComPort ourInstance = new ComPort();

    // Controller
    private ApiController controller;

    // Field
    private ObjectProperty<SerialPort> vendingSerialPort;
    private ObjectProperty<SerialPort> nfcSerialPort;

    // Logger
    private Logger logger;

    // Boolean interrupt on
    private boolean interruptOn;

    // Instance getter
    public static ComPort getInstance() {
        return ourInstance;
    }

    // Constructor for initialization
    private ComPort() {
        logger = LogManager.getLogger(this.getClass().getName());

        controller = new ApiController();

        nfcSerialPort = new SimpleObjectProperty<>();
        vendingSerialPort = new SimpleObjectProperty<>();

        String vendingPort = Context.getInstance().getProperties().getProperty("vendingPort");
        String nfcPort = Context.getInstance().getProperties().getProperty("nfcPort");

        String[] ports = SerialPortList.getPortNames();

        logger.info("Available Serialports on system");
        for(String port : ports) {
            logger.info("Port: " + port);

            if (port.equals(vendingPort)) {
                vendingSerialPort.set(new SerialPort(vendingPort));
                try {
                    // Config serialport
                    vendingSerialPort.get().openPort();
                    vendingSerialPort.get().setParams(SerialPort.BAUDRATE_9600,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);

                    int mask = SerialPort.MASK_RXCHAR;
                    vendingSerialPort.get().setEventsMask(mask);
                    //vendingSerialPort.get().addEventListener(this::debugInterrupt);

                    // Register shutdownhook for closing serialport
                    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                        try {
                            vendingSerialPort.get().closePort();
                        } catch (SerialPortException e) {
                            e.printStackTrace();
                        }
                    }));
                } catch (SerialPortException e) {
                    e.printStackTrace();
                }
            }

            // Register second port
            else if (port.equals(nfcPort)) {
                nfcSerialPort.set(new SerialPort(nfcPort));
                try {
                    // Config serialport
                    nfcSerialPort.get().openPort();
                    nfcSerialPort.get().setParams(SerialPort.BAUDRATE_115200,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);

                    int mask = SerialPort.MASK_RXCHAR;
                    nfcSerialPort.get().setEventsMask(mask);

                    // Register shutdownhook for closing serialport
                    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                        try {
                            nfcSerialPort.get().closePort();
                        } catch (SerialPortException e) {
                            e.printStackTrace();
                        }
                    }));
                } catch (SerialPortException e) {
                    e.printStackTrace();
                }
                logger.info("NFC port registered");
            }
        }
    }

    private void debugInterrupt(SerialPortEvent event) {
        try {
            for(byte item :vendingSerialPort.get().readBytes()) {
                logger.warn(item);
            }
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    // Write message to MCU
    public boolean writeVendingMessage(Protocol protocol, byte[] data) {
        boolean success = false;

        if(vendingSerialPort.get() != null) {
            try {
                // Allocate buffer
                //ByteBuffer buffer = ByteBuffer.allocate(data.length + 6);
                ByteBuffer buffer = ByteBuffer.allocate(data.length);

                // Build message according protocol
                buffer
                        //.put((byte) 0xFE)
                        //.put(protocol.asByte())
                        //.putInt(data.length)
                        .put(data);

                // Write message
                logger.info("Send message to MCU: " + vendingSerialPort.get().getPortName());

                /*
                for(byte item : data) {
                    logger.info(item);
                }
                */

                vendingSerialPort.get().writeBytes(buffer.array());
                // Wait for response
                ExecutorService executor = Executors.newSingleThreadExecutor();
                Future<Boolean> future = executor.submit(() -> {
                    logger.info("Waiting for response from MCU");
                    boolean response = false;

                    try {
                        // Wait for data
                        while (vendingSerialPort.get().getInputBufferBytesCount() < 2);

                        // Read data and check
                        logger.info("Bytes received from MCU: " + vendingSerialPort.get().getInputBufferBytesCount());
                        /*
                        for(byte item : vendingSerialPort.get().readBytes()) {
                            logger.info(item);
                        }
                        */

                        byte[] test = vendingSerialPort.get().readBytes(3);
                        response = Arrays.equals(test, data);

                        logger.info("Comparison result is: " + response);
                        for(byte item : test) {
                            logger.info("Byte received: " + item);
                        }
                    } catch (SerialPortException e ) {
                        logger.warn(e.getMessage());
                    }

                    return response;
                });

                // Set success
                success = future.get(30, TimeUnit.SECONDS);
            } catch (SerialPortException | InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                logger.warn("Not received response of MCU in time");
            }
        } else {
            logger.warn("No Vending port available");
        }

        return success;
    }


    // Method to turn on NFC interrupt
    public void turnOnNfcInterrupt(int period, TimeUnit timeUnit) {
        if(nfcSerialPort.get() != null) {
            // Clear buffer
            try {
                nfcSerialPort.get().purgePort(SerialPort.PURGE_RXCLEAR);
            } catch (SerialPortException e) {
                e.printStackTrace();
            }

            new Thread(() -> {
                ExecutorService executor = Executors.newSingleThreadExecutor();
                Future<Long> future = executor.submit(() -> {
                    logger.info("Listening for NFC card");
                    Long cardId = 0L;

                    try {
                        // Wait for data
                        logger.info("Waiting for NFC");
                        while (nfcSerialPort.get().getInputBufferBytesCount() < 4);

                        // Read data
                        logger.info("Bytes received for MCU: " + nfcSerialPort.get().getInputBufferBytesCount());
                        BigInteger integer = new BigInteger(nfcSerialPort.get().readBytes());

                        cardId = integer.longValue();
                        logger.info("Got id: " + cardId);
                    } catch (SerialPortException e ) {
                        logger.warn(e.getMessage());
                    }

                    return cardId;
                });

                try {
                    // Get id and add to context
                    Long id = future.get(period, timeUnit);
                    Context.getInstance().setNfcId(id);


                    logger.info("NfcId added to context: " + id);
                } catch (TimeoutException e) {
                    logger.warn("No NfcId read in specified time");
                } catch (InterruptedException | ExecutionException e) {
                    logger.warn(e.getMessage());
                }

                logger.info("End of NFC interrupt");
                executor.shutdownNow();
            }).start();
        } else {
            logger.warn("No available NFC reader");
        }
    }

    public void toggleNfcInlogInterrupt() {
        if(nfcSerialPort.get() != null) {
            try {
                if (!interruptOn) {
                    // Clear buffer
                    nfcSerialPort.get().purgePort(SerialPort.PURGE_RXCLEAR);

                    nfcSerialPort.get().addEventListener(this::nfcInlogInterrupt);
                    logger.info("Listening for NFC inlog cards");
                    interruptOn = true;
                } else {
                    // Clear buffer
                    nfcSerialPort.get().purgePort(SerialPort.PURGE_RXCLEAR);

                    nfcSerialPort.get().removeEventListener();
                    logger.info("Not listening for NFC inlog cards anymore");
                    interruptOn = false;
                }
            } catch (SerialPortException e) {
                logger.warn(e.getMessage());
            }
        } else {
            logger.warn("No Nfc reader available");
        }
    }

    // Read message from MCU (listener)
    private void nfcInlogInterrupt(SerialPortEvent event) {
        if(event.isRXCHAR()) {
            try {
                // Get NFC id of MCU
                logger.info("Bytes received for MCU: " + event.getEventValue());
                BigInteger integer = new BigInteger(nfcSerialPort.get().readBytes());

                // Check if card exists
                Model user  = null;
                Model card = controller.getCrudController().read(NfcCard.class, integer.longValue());

                if(card != null && card instanceof NfcCard) {
                    logger.info("NFC card recognized");

                    // Get user with id coupled to NFC
                    long userId = ((NfcCard) card).getUserId();
                    user = controller.getCrudController().read(User.class, userId);
                }

                if(user instanceof User) {
                    // Set user to context
                    logger.info("User found by NFC");
                    Context.getInstance().setUser((User) user);

                    // Clear buffer
                    nfcSerialPort.get().purgePort(SerialPort.PURGE_RXCLEAR);
                } else {
                    logger.warn("User not found with valid NFC");
                    Context.getInstance().setUser(null);
                }
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
        } else {
            logger.warn("Wrong serial data");
        }
    }

    public SerialPort getNfcSerialPort() {
        return nfcSerialPort.get();
    }

    public ObjectProperty<SerialPort> nfcSerialPortProperty() {
        return nfcSerialPort;
    }

    public SerialPort getVendingSerialPort() {
        return vendingSerialPort.get();
    }

    public ObjectProperty<SerialPort> vendingSerialPortProperty() {
        return vendingSerialPort;
    }
}
