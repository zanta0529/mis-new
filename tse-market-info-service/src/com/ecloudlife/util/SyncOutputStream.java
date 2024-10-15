package com.ecloudlife.util;

import java.io.IOException;
import java.io.OutputStream;

public class SyncOutputStream extends java.io.OutputStream {
    OutputStream os;
    int identCode = 0;
    public boolean lock = false ;
    public SyncOutputStream(OutputStream os) {
        this.os = os ;
    }

    public synchronized void write(int i) throws IOException{
        os.write(i);
    }

    public synchronized void write(byte[] data) throws IOException {
        os.write(data);
    }

    public int getIdentCode() {
        return identCode;
    }

    public void setIdentCode(int identCode) {
        this.identCode = identCode;
    }
}
