package com.proco.codec;

import java.net.SocketAddress;
import java.util.List;

import com.proco.util.Utility;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.ReplayingDecoder;

public class StockStreamCodec extends ReplayingDecoder<StockStreamCodec.Status> implements ChannelOutboundHandler { 

	public static long waitSize = 0;
	public StockStreamCodec() {
		checkpoint(Status.HEAD);
	}
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		
	    while (true) {
	    	int type = 0;
	    	ByteBuf readData = null;
	    	switch (state()) {
	        	case HEAD:
			        waitSize = in.readableBytes();
			        //System.out.println("waitSize----->"+waitSize);
			        byte start = in.readByte();
			        //System.out.println("HEAD----->"+Byte.toUnsignedInt(start)  +"  ---- "+Byte.toUnsignedInt((byte)0xf0));
			        if(Byte.toUnsignedInt(start)==0xf0) {
			        	checkpoint(Status.CONTENT);
			        }
			        else {
			        	checkpoint(Status.HEAD);
			        	continue;
			        }
	        	case CONTENT:
	        		type = Byte.toUnsignedInt(in.readByte());
	        		long length = in.readShort();
	        		//if(StockInboundHandler.decodeCount.get()<1000000)
	        		//	System.out.println(StockInboundHandler.decodeCount.get()+" CONTENT----->type:"+ type +"  ---- length:"+length)
	        		int readable = in.readableBytes();
	        		while(readable<length) Utility.sleep(1);
	        		
	        		readData = in.readBytes((int) length);
	        		//System.out.println("CONTENT----->readableBytes:"+ readData.readableBytes());
	        		checkpoint(Status.END);
	        	case END:
	        		byte end = in.readByte();
	        		 if(Byte.toUnsignedInt(end)==0xf1) {
	        			if(type==1) {
			        		if(readData!=null) {
			        			out.add(readData);	   
			        			//System.out.println("continue: add:"+out.size());
			        		}
	        			}
	        		}
	        		checkpoint(Status.HEAD);
	        	 continue;
	    	}
	    	//System.out.println("end:");
	    }
	}
	
	
	public enum Status {
		HEAD, CONTENT, END
	}


	@Override
	public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
		ctx.bind(localAddress, promise);
	}

	@Override
	public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress,
			ChannelPromise promise) throws Exception {
		ctx.connect(remoteAddress, localAddress, promise);
	}

	@Override
	public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
		ctx.disconnect(promise);
	}

	@Override
	public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
		ctx.close(promise);
	}

	@Override
	public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
		ctx.deregister(promise);
	}

	@Override
	public void read(ChannelHandlerContext ctx) throws Exception {
		ctx.read();
	}

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		ctx.write(msg, promise);
	}

	@Override
	public void flush(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

}
