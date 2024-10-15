package com.proco.io;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import com.willmobile.bee.BeeReader;
import com.willmobile.bee.BeeUtils;
import com.willmobile.bee.BeeWriter;
import com.willmobile.bee.HuffmanCodeEntry;
import com.willmobile.bee.HuffmanTreeNode;
import com.willmobile.io.ByteArrayBitReader;
import com.willmobile.io.ByteArrayBitWriter;
import com.willmobile.io.IBitReader;
import com.willmobile.io.IBitWriter;

public class GeneralPacket {
    final static int start = 0xf0;
    final static int end = 0xf1;
    public boolean ok = false;
    public boolean eof = false;
    private static final String BEE_TABLE_FILE = "beetable.bin";
    private static final String BEE_NODE_FILE = "beenode.bin";

    static HuffmanTreeNode rootNode = null;
    static HuffmanCodeEntry[] huffmanCodes = null;
    static {
      final DataInputStream beeTableIn = new DataInputStream(Thread
          .currentThread()
          .getContextClassLoader()
          .getResourceAsStream(BEE_TABLE_FILE)
          );
      final DataInputStream beeNodeIn = new DataInputStream(Thread
          .currentThread()
          .getContextClassLoader()
          .getResourceAsStream(BEE_NODE_FILE)
          );
      try {
        rootNode = (HuffmanTreeNode) (HuffmanTreeNode) BeeUtils.
            loadHuffmanNode(beeNodeIn);
        huffmanCodes = BeeUtils.loadHuffmanTable(beeTableIn);
      }
      catch (IOException ex) {
      }
    }
    int type = 0;
    int length = 0 ;
    byte[] rawData = null;


    public GeneralPacket(InputStream is) {
        try {
          /*
            final DataInputStream beeTableIn = new DataInputStream(new
                    FileInputStream(
                            BEE_TABLE_FILE));
            final DataInputStream beeNodeIn = new DataInputStream(new
                    FileInputStream(BEE_NODE_FILE));
           */



            //InputStream is = newInputStream(bis);
            DataInputStream dis = new DataInputStream(is);

            while (true) {
                int lf = 0;
                int i = dis.read();
                if (i == 0xf0) {
                    this.type = dis.read();
                    this.length = dis.readShort();
                    //System.out.println(type + " - " + this.length);
                    byte[] buffer = new byte[this.length];
                    if (type == 0) {
                        dis.read(buffer);
                        this.rawData = buffer;
                        //System.out.println(new String(buffer, "UTF-8"));
                        lf = dis.read();
                    } else if (type == 1) {
                        dis.read(buffer);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        IBitReader bitReader = new ByteArrayBitReader(buffer, 0,
                                buffer.length);
                        BeeReader beeReader = new BeeReader(rootNode, bitReader);

                        while (true) {
                            final int b = beeReader.read();
                            if (b == -1 || b == BeeUtils.EOF_BYTE_CODE) {
                                break;
                            } else {
                                baos.write(b);
                            }
                        }
                        baos.flush();
                        baos.close();
                        this.rawData = baos.toByteArray();
                        //System.out.println(new String(baos.toByteArray(),
                        //        "UTF-8"));
                        lf = dis.read();
                    }
                }
                if(lf==0xf1)
                  ok = true;
                if (lf == 0xf1 || i == -1)
                    break;
            }

        } catch (Exception ex) {
          System.out.println("Decode Err: ");
          System.out.println(ex);
        }
    }

    public GeneralPacket(byte[] decData) {
        try {
          /*
            final DataInputStream beeTableIn = new DataInputStream(new
                    FileInputStream(
                            BEE_TABLE_FILE));
            final DataInputStream beeNodeIn = new DataInputStream(new
                    FileInputStream(BEE_NODE_FILE));
*/
          final DataInputStream beeTableIn = new DataInputStream(Thread
              .currentThread()
              .getContextClassLoader()
              .getResourceAsStream(BEE_TABLE_FILE)
              );
          final DataInputStream beeNodeIn = new DataInputStream(Thread
              .currentThread()
              .getContextClassLoader()
              .getResourceAsStream(BEE_NODE_FILE)
              );

            rootNode = (HuffmanTreeNode) (HuffmanTreeNode) BeeUtils.
                       loadHuffmanNode(beeNodeIn);
            huffmanCodes = BeeUtils.loadHuffmanTable(beeTableIn);

            java.io.ByteArrayInputStream is = new java.io.ByteArrayInputStream(decData);
            DataInputStream dis = new DataInputStream(is);

            while (true) {
                int lf = 0;
                int i = dis.read();
                if (i == 0xf0) {
                    this.type = dis.read();
                    this.length = dis.readShort();

                    byte[] buffer = new byte[this.length];
                    if (type == 0) {
                        dis.read(buffer);
                        this.rawData = buffer;
                        //System.out.println(new String(buffer, "UTF-8"));
                        lf = dis.read();
                    } else if (type == 1) {
                        dis.read(buffer);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        IBitReader bitReader = new ByteArrayBitReader(buffer, 0,
                                buffer.length);
                        BeeReader beeReader = new BeeReader(rootNode, bitReader);

                        while (true) {
                            final int b = beeReader.read();
                            if (b == -1 || b == BeeUtils.EOF_BYTE_CODE) {
                                break;
                            } else {
                                baos.write(b);
                            }
                        }
                        baos.flush();
                        baos.close();
                        this.rawData = baos.toByteArray();
                        //System.out.println(new String(baos.toByteArray(),
                        //        "UTF-8"));
                        lf = dis.read();
                    }
                    System.out.println(type + " - " +lf+" - " + this.length+"/"+decData.length);
                    if(lf==0xf1)
                      ok = true;
                    if (lf == 0xf1 || i == -1)
                      break;

                }
                if(i==-1) break;

            }

        } catch (Exception ex) {
        }
    }

    public GeneralPacket(int type , byte[] rawData) {
        this.type = type;
        this.rawData = rawData;
    }

    public byte[] getPacket() throws IOException {
        java.io.ByteArrayOutputStream baos = new ByteArrayOutputStream();
        java.io.DataOutputStream dos = new java.io.DataOutputStream(baos);
        if(type==0){

            baos.write(start);
            baos.write(type);
            dos.writeShort(this.rawData.length);
            baos.write(this.rawData);
            baos.write(end);
        } else {
          /*
            final DataInputStream beeTableIn = new DataInputStream(new
                    FileInputStream(
                            BEE_TABLE_FILE));
            final DataInputStream beeNodeIn = new DataInputStream(new
                    FileInputStream(BEE_NODE_FILE));
*/
          final DataInputStream beeTableIn = new DataInputStream(Thread
              .currentThread()
              .getContextClassLoader()
              .getResourceAsStream(BEE_TABLE_FILE)
              );
          final DataInputStream beeNodeIn = new DataInputStream(Thread
              .currentThread()
              .getContextClassLoader()
              .getResourceAsStream(BEE_NODE_FILE)
              );

            rootNode = (HuffmanTreeNode) (HuffmanTreeNode) BeeUtils.
                       loadHuffmanNode(beeNodeIn);
            huffmanCodes = BeeUtils.loadHuffmanTable(beeTableIn);

            byte[] buffer = new byte[4096];
            IBitWriter ibw = new ByteArrayBitWriter(buffer);
            BeeWriter beeWriter = new BeeWriter(huffmanCodes, ibw);

            for (int i = 0; i < this.rawData.length; i++) {
                beeWriter.write(this.rawData[i]);
            }
            beeWriter.flush();
            ibw.flush();

            ByteBuffer bb = ByteBuffer.wrap(buffer);
            bb.flip();

            byte[] buf = bb.array();

            baos.write(start);
            baos.write(type);
            dos.writeShort(buf.length);
            baos.write(buf);
            baos.write(end);

        }
        dos.flush();
        baos.close();
        return baos.toByteArray();
    }

    public byte[] getRawData() {
      if(rawData==null) return new byte[0];
        return rawData;
    }

    /*
    public static void main(String[] args) {
        GeneralPacket generalpacket = new GeneralPacket(1,"!:L;M:886988033455|wm|9.1.2|slimquote|nokia_6233_ver1|cht;".getBytes());
        try {
            System.out.println(new String(generalpacket.getPacket()));
            GeneralPacket generalpacket2 = new GeneralPacket(generalpacket.getPacket());
            System.out.println(new String(generalpacket2.getRawData(),"UTF-8"));

        } catch (IOException ex) {
        }

    }*/

    public static InputStream newInputStream(final InputStream is) {
        return new InputStream() {
            public synchronized int read() throws IOException {
                while(is.available()==0){
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ex) {
                    }
                }
                return is.read();
            }

        };
    }
}