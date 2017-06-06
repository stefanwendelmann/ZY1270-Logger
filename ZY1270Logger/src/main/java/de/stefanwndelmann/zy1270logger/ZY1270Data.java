package de.stefanwndelmann.zy1270logger;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author swendelmann
 */
public class ZY1270Data {
    
    private Date timestamp;
    private Double voltage;
    private Double amp;
    private Double watt;
    private static final DecimalFormat nf = new DecimalFormat("0.0###");
    private final static SimpleDateFormat timestampFormat = new SimpleDateFormat("HH:mm:ss,SSS");
    
    public ZY1270Data(Double voltage, Double amp, Date timestamp) {
        this.timestamp = timestamp;
        this.voltage = voltage;
        this.amp = amp;
        this.watt = BigDecimal.valueOf(this.amp).multiply(BigDecimal.valueOf(this.voltage)).doubleValue();
    }
    
    public Double getVoltage() {
        return voltage;
    }
    
    public Double getAmp() {
        return amp;
    }
    
    public Double getWatt() {
        return watt;
    }

    public Date getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String toString() {
        return "ZY1270Data{" + "voltage=" + nf.format(voltage)
                + ", amp=" + nf.format(amp)
                + ", watt=" + nf.format(watt) + '}';
    }
    
    public static ZY1270Data parseString(String s, Date timestamp) {
        if (s.matches("\\d{6},\\d{6}")) {
            String[] parts = s.split(",");
            return new ZY1270Data(Double.parseDouble(parts[0]) / 10000,
                    Double.parseDouble(parts[1]) / 10000, timestamp);
        } else {
            throw new NumberFormatException();
        }
    }
    
    public Object[] getRowFormat() {
        return new Object[]{timestampFormat.format(timestamp), nf.format(voltage), nf.format(amp), nf.format(watt)};
    }
    
}
