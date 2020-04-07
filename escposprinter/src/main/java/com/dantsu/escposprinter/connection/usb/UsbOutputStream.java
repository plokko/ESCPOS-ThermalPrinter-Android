package com.dantsu.escposprinter.connection.usb;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class UsbOutputStream extends OutputStream {
    private UsbDeviceConnection usbConnection;
    private UsbInterface usbInterface;
    private UsbEndpoint usbEndpoint;

    public UsbOutputStream(UsbManager usbManager, UsbDevice usbDevice) throws IOException {
        this.usbInterface = usbDevice.getInterface(0);
        this.usbEndpoint = this.usbInterface.getEndpoint(1);
        this.usbConnection = usbManager.openDevice(usbDevice);
    }

    @Override
    public void write(int i) throws IOException {
        this.write(new byte[]{(byte) i});
    }

    @Override
    public void write(@NonNull byte[] bytes) throws IOException {
        this.write(bytes, 0, bytes.length);
    }

    @Override
    public void write(final @NonNull byte[] bytes, final int offset, final int length) throws IOException {
        if (this.usbInterface != null && this.usbEndpoint != null && this.usbConnection != null) {
            if (this.usbConnection.claimInterface(this.usbInterface, true)) {
                ByteBuffer buffer = ByteBuffer.wrap(bytes);
                UsbRequest usbRequest = new UsbRequest();
                try {
                    usbRequest.initialize(this.usbConnection, this.usbEndpoint);
                    if (!usbRequest.queue(buffer, bytes.length)) {
                        throw new IOException("Error queueing request.");
                    }
                    this.usbConnection.requestWait();
                } finally {
                    usbRequest.close();
                }
            }
        }
    }

    @Override
    public void flush() throws IOException {

    }

    @Override
    public void close() throws IOException {
        if (this.usbConnection != null) {
            this.usbConnection.close();
            this.usbInterface = null;
            this.usbEndpoint = null;
            this.usbConnection = null;
        }
    }
}