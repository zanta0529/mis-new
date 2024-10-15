package com.proco.codec;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import com.proco.exec.ProcF01;
import com.proco.exec.ProcF02;
import com.proco.exec.ProcF06;
import com.proco.exec.ProcF09;
import com.proco.exec.ProcF13;
import com.proco.exec.ProcF19;
import com.proco.exec.ProcF20;
import com.proco.exec.ProcF22;
import com.proco.exec.ProcF23;
import com.proco.exec.ProcF99;
import com.proco.exec.ProcL01;
import com.proco.exec.ProcL20;
import com.proco.io.ConfigData;
import com.proco.util.ExecutorManager;
import com.willmobile.bee.BeeReader;
import com.willmobile.bee.BeeUtils;
import com.willmobile.bee.HuffmanCodeEntry;
import com.willmobile.bee.HuffmanTreeNode;
import com.willmobile.io.ByteArrayBitReader;
import com.willmobile.io.IBitReader;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;


public class StockInboundHandler extends ChannelInboundHandlerAdapter { // (1)
	public static java.util.concurrent.atomic.AtomicInteger clientCount = new java.util.concurrent.atomic.AtomicInteger(0);
	public static java.util.concurrent.ConcurrentHashMap<String, Integer> clientMap = new java.util.concurrent.ConcurrentHashMap<String, Integer>();
	public static java.util.concurrent.ConcurrentHashMap<String, String> dateMap = new java.util.concurrent.ConcurrentHashMap<String, String>();
	 
	public static java.util.concurrent.atomic.AtomicLong decodeCount = new java.util.concurrent.atomic.AtomicLong(0);
	static ExecutorManager executor_detail = new ExecutorManager(2);
	
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
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
	}
	
	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		ctx.fireChannelUnregistered();

	}	
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
	    final ByteBuf data = ((ByteBuf) msg);
	    int length = data.readableBytes();
	    final byte[] rawdata = new byte[length];
	    data.readBytes(rawdata);
	    data.release();
	    
	    String cmark = ctx.toString();

	    Integer cc = clientMap.get(cmark);
	    if(cc==null) cc=clientCount.getAndAdd(1);
	    clientMap.put(cmark, cc);

	    decodeBeeData(cc,rawdata);
	}
	
	public static void decodeBeeData(final Integer cc,final byte[] rawdata) {
	    Runnable decode = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				//w:35.1000;^:20200330;u:42.8000;p:0;@:1101.tw;n:台泥;%:07:50:00;#:1.tse.tw|15685;it:12;i:01;c:1101;y:38.9500;
				
				final ConcurrentHashMap<String,String> dataSet = new ConcurrentHashMap<String,String>();
				
		        ByteArrayOutputStream baos = new ByteArrayOutputStream();
		        IBitReader bitReader = new ByteArrayBitReader(rawdata, 0,
		        		rawdata.length);
		        BeeReader beeReader = new BeeReader(rootNode, bitReader);
		        try {
		        	String k = "";
		        	String v = "";
		        	boolean fk = false;
			        while (true) {
			            final int b = beeReader.read();
			            if (b == -1 || b == BeeUtils.EOF_BYTE_CODE) {
			            	decodeCount.addAndGet(1);
			                break;
			            } else {
			            	if(b==(char)':' && !fk) {
			            		k = new String(baos.toByteArray());
			            		baos.reset();
			            		fk = true;
			            	} else if(b==(char)';' && fk) {
			            		v  = new String(baos.toByteArray());
			            		baos.reset();
			            		dataSet.put(k, v);
			            		fk = false;
			            	} else {
			            		baos.write(b);
			            	}
			                
			            }
			        }
			        //baos.flush();
			        //baos.close();		        	
		        } catch (Exception ex) {
		        }
		        //String a = new String(baos.toByteArray());
		        //System.out.println(dataSet.toString());
		        if(!dataSet.containsKey("#")) return;
		        String[] hash = dataSet.get("#").split("\\.");
		        if(hash.length!=3) return;
		   
		        String date = dateMap.get(hash[1]);
		        if(date==null) {
		        	String date0 = dataSet.get("^");
		        	if(!date0.equals("")) {
		        		dateMap.put(hash[1], date0);
		        		date = date0;
		        	}
		        }

		        if(ConfigData.timeline) {
		        	if(dataSet.containsKey("@") && hash[1].equals("tse")) { //Timeline 僅處理 tse
			        	if(hash[0].equals("22")||hash[0].equals("23")){ //Odd 盤中零股不處理
			        	} else {
			        		ProcL01.proc(hash[1],date, dataSet);
			        		if(hash[0].equals("6")||hash[0].equals("3")||hash[0].equals("2")) ProcL20.proc(hash[1],date, dataSet);
			        		if(hash[0].equals("20")) ProcL20.proc(hash[1], date,dataSet);
			        		if(hash[0].equals("10")) ProcL20.proc(hash[1], date,dataSet); 
			        	}
		        	}
		        } 
		        //System.out.println("------>"+dataSet.size());
		        if(ConfigData.datafeed) {
			        if(dataSet.containsKey("@")) {
			        	if(hash[0].equals("22")||hash[0].equals("23")){ //Odd 盤中零股
			        		ProcF22.proc(hash[1],date, dataSet);	
			        		ProcF23.proc(hash[1], date,dataSet); 
			        		return;
			        	}		        

			        	ProcF01.proc(hash[1],date, dataSet);
			        	if(hash[0].equals("6")||hash[0].equals("3")||hash[0].equals("2")) ProcF06.proc(hash[1], date,dataSet);  
				        else if(hash[0].equals("10")) ProcF06.proc(hash[1], date,dataSet);  		        	
				        else if(hash[0].equals("20")) ProcF20.proc(hash[1], date,dataSet);   		        	
				        else if(hash[0].equals("9")) ProcF09.proc(hash[1], date,dataSet);    		        	
				        else if(hash[0].equals("19")) ProcF19.proc(hash[1], date,dataSet);   		        	
				        else if(hash[0].equals("13")) ProcF13.proc(hash[1], date,dataSet);  	
			        } else {
			        	if(hash[0].equals("2")||hash[0].equals("4")) ProcF02.proc(hash[1], date,dataSet); 
			        }		        	
		        }	
		        if(ConfigData.datafeed || ConfigData.timeline) {
		        	if(!dataSet.containsKey("@")) {
		        		if(hash[0].equals("99")) ProcF99.proc(hash[1], date,dataSet);
		        	}
		        } 		        
			}
	    };
	    executor_detail.submit(decode,cc);
	}
    
}
