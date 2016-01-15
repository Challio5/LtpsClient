package ltps1516.gr121gr122.control.comport;

/**
 * Created by rob on 05-12-15.
 */
public enum Protocol {
    LOG,
    LOCATION,
    NFC;

    public byte asByte() {
        return (byte) this.ordinal();
    }
}
