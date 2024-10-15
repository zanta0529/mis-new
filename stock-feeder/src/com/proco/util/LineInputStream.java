package com.proco.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Vector;
import java.io.IOException;

public class LineInputStream extends java.io.InputStream {
    java.io.InputStream is;
    public LineInputStream(java.io.InputStream is) {
        this.is = is;
    }

    public LineInputStream(byte[] rawData) {
        this.is = new ByteArrayInputStream(rawData);
    }

    public int read() throws IOException {
        return is.read();
    }

    public byte[] readLineUNICODE() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        boolean first = true;
        while (true) {
          if(first){
            baos.write(0xff);
            baos.write(0xfe);

          }
            int i = is.read();
            if (i == -1){
                if(first) return null;
                break;
            }
            else if (i == 0x0d) {
                int ii = is.read();
                int iii = is.read();
                int iiii = is.read();
                if (ii == 0x00 && iii == 0x0a && iiii== 0x00) {
                    break;
                } else if (ii == -1) {
                    baos.write(i);
                    break;
                } else {
                    baos.write(i);
                    baos.write(ii);
                    baos.write(iii);
                    baos.write(iiii);
                }
            } else if (i == 0x0a) {
                break;
            } else {
                baos.write(i);
            }
            first = false;
        }
        baos.flush();
        baos.close();
        return baos.toByteArray();
    }

    public byte[] readLine() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        boolean first = true;
        while (true) {
            int i = is.read();
            if (i == -1){
                if(first) return null;
                break;
            }
            else if (i == 0x0d) {
                int ii = is.read();
                if (ii == 0x0a) {
                    break;
                } else if (ii == -1) {
                    baos.write(i);
                    break;
                } else {
                    baos.write(i);
                    baos.write(ii);
                }
            } else if (i == 0x0a) {
                break;
            } else {
                baos.write(i);
            }
            first = false;
        }
        baos.flush();
        baos.close();
        return baos.toByteArray();
    }

    public String readLine(String enc) throws IOException {
      byte[] rt = null;
      if(enc.toLowerCase().equals("unicode")){
        rt = readLineUNICODE();
      } else
        rt = readLine();
        if (rt == null)
            return null;
        else {
            return new String(rt, enc);
        }
    }

    public Vector getLineSplitList(String enc, int minSize) {
        Vector rt = new Vector();
        try {
            byte[] dat = null;
            while ((dat = this.readLine())!= null) {
                if(dat.length != 0){
                    String sStr = new String(dat, enc);
                    sStr = sStr.replace('|', '\t');
                    if (sStr.startsWith("\t")) {
                        sStr = sStr.substring(1, sStr.length());
                    }
                    if (sStr.endsWith("\t")) {
                        sStr = sStr.substring(0, sStr.length() - 1);
                    }
                    sStr = sStr.trim();
                    if (!sStr.equals("")) {
                        String[] rtArray = sStr.split("\t");
                        if(rtArray.length >= minSize)
                            rt.add(rtArray);
                    }
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }
        return rt;
    }

    public Vector getLineSplitList(String enc) {
        Vector rt = new Vector();
        try {
            byte[] dat = null;
            while ((dat = this.readLine())!= null) {
                if(dat.length != 0){
                    String sStr = new String(dat, enc);
                    sStr = sStr.replace('|', '\t');
                    if (sStr.startsWith("\t")) {
                        sStr = sStr.substring(1, sStr.length());
                    }
                    if (sStr.endsWith("\t")) {
                        sStr = sStr.substring(0, sStr.length() - 1);
                    }
                    sStr = sStr.trim();
                    if (!sStr.equals("")) {
                        rt.add(sStr.split("\t"));
                    }
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }
        return rt;
    }

    public Vector getLineSplitList(Vector rt,String enc) {
        if(rt==null)
            rt= new Vector();
        try {
            byte[] dat = null;
            while ((dat = this.readLine())!= null) {
                if(dat.length != 0){
                    String sStr = new String(dat, enc);
                    sStr = sStr.replace('|', '\t');
                    if (sStr.startsWith("\t")) {
                        sStr = sStr.substring(1, sStr.length());
                    }
                    if (sStr.endsWith("\t")) {
                        sStr = sStr.substring(0, sStr.length() - 1);
                    }
                    sStr = sStr.trim();
                    if (!sStr.equals("")) {
                        rt.add(sStr.split("\t"));
                    }
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }
        return rt;
    }

}
