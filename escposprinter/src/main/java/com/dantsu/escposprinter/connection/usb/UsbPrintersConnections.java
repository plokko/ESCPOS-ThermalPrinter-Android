package com.dantsu.escposprinter.connection.usb;

import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;

import com.dantsu.escposprinter.exceptions.EscPosConnectionException;

public class UsbPrintersConnections extends UsbConnections {

    /**
     * Create a new instance of UsbPrintersConnections
     *
     * @param context Application context
     */
    public UsbPrintersConnections(Context context) {
        super(context);
    }

    /**
     * Easy way to get the first USB printer paired / connected.
     *
     * @return a UsbConnection instance
     */
    public static UsbConnection selectFirstConnected(Context context) {
        UsbPrintersConnections printers = new UsbPrintersConnections(context);
        UsbConnection[] bluetoothPrinters = printers.getList();
        
        if (bluetoothPrinters != null && bluetoothPrinters.length > 0) {
            for (UsbConnection printer : bluetoothPrinters) {
                try {
                    return printer.connect();
                } catch (EscPosConnectionException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    
    
    /**
     * Get a list of USB printers.
     *
     * @return an array of UsbConnection
     */
    public UsbConnection[] getList() {
        UsbConnection[] usbConnections = super.getList();

        if(usbConnections == null) {
            return null;
        }

        int i = 0;
        UsbConnection[] printersTmp = new UsbConnection[usbConnections.length];
        for (UsbConnection usbConnection : usbConnections) {
            UsbDevice device = usbConnection.getDevice();
            int usbClass = device.getDeviceClass();

            if(usbClass == UsbConstants.USB_CLASS_PER_INTERFACE && UsbConnection.findPrinterInterface(device) != null) {
                usbClass = UsbConstants.USB_CLASS_PRINTER;
            }

            if (usbClass == UsbConstants.USB_CLASS_PRINTER) {
                printersTmp[i++] = new UsbConnection(this.usbManager, device);
            }
        }

        UsbConnection[] usbPrinters = new UsbConnection[i];
        System.arraycopy(printersTmp, 0, usbPrinters, 0, i);
        return usbPrinters;
    }
    
}
