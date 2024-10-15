package com.proco.io;

import com.proco.cache.RedisManager;
import com.proco.codec.StockInboundHandler;
import com.proco.codec.StockStreamCodec;
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
import com.proco.exec.ProcStockCategory;
import com.proco.exec.ProcStockEngName;
import com.proco.exec.ProcStockName;
import com.proco.exec.ProcStockSnapshot;
import com.proco.util.ExecutorManager;
import com.proco.util.Utility;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;


public class Launcher extends Thread {
    private int port;
    boolean epoll = false;
    
    public Launcher(int port,boolean epoll) {
        this.port = port;
        this.epoll = epoll;
    }

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
    	
		//first check epoll
    	if(epoll)
    		epoll = Utility.checkEpoll();
		System.out.println("Enable Epoll :"+ epoll);
    	
		Class serverSocketClass;
		EventLoopGroup bossGroup;
		EventLoopGroup workerGroup;
		if (epoll) {
			serverSocketClass = EpollServerSocketChannel.class;
			bossGroup = new EpollEventLoopGroup();
			workerGroup = new EpollEventLoopGroup(Runtime.getRuntime().availableProcessors());

		} else {
			serverSocketClass = NioServerSocketChannel.class;
			final int BOSS_GROUP_SIZE = Runtime.getRuntime().availableProcessors() * 2;
			bossGroup = new NioEventLoopGroup(BOSS_GROUP_SIZE);
			workerGroup = new NioEventLoopGroup(16);
	  	}
        
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
             .channel(serverSocketClass) // (3)
             .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ch.pipeline().addLast(new StockStreamCodec());
                     ch.pipeline().addLast(new StockInboundHandler());
                 }
                 
                 
             })
             .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
             .option(ChannelOption.SO_BACKLOG, 128)          // (5)
             .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync(); // (7)

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } catch (Exception ex){
        	ex.printStackTrace(System.out);
        	
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }		
		
	}
    
	static long cnt = 0;
    static long nullcnt = 0;
    static long precnt = 0;	

	//static String ip = "127.0.0.1";
    //static int port = 8081;
	
	public static void main0(String[] args){
		System.out.println(Utility.getFullyDateTimeStr()+" StockFeeder2 STG "+InfoDaemon.launcher_version+" " +InfoDaemon.version);
		if(args.length==0) args = new String[]{"s=7001","redis=localhost:10001"};
		if(args.length<1) {
			System.out.println("usage : ");
			System.out.println("s=EX_Local_Port redis=<Host>:<port>");
			System.out.println("Example : ");
			System.out.println("s=7001 redis=localhost:10001");
			return;
		}

		int port = 7001;
		
		for(String perm : args ){
			if(perm.startsWith("s=")){
				int sport = Integer.parseInt(perm.substring(2,perm.length()));			
				port = sport;
				
			}
			if(perm.toLowerCase().startsWith("redis=")){
				String redis = perm.toLowerCase().substring(6, perm.toLowerCase().length());
				if(redis.length()!=0) {
					String[] rediss = redis.split(":");
					if(rediss.length==2)
						RedisManager.redis = rediss[0]+":"+rediss[1];
					else if(rediss.length==3) {
						RedisManager.redis = rediss[0]+":"+rediss[1];
					}
				}
			}
			if(perm.toLowerCase().startsWith("rauth=")){
				String apath = perm.substring(6, perm.length());
				if(apath.length()!=0) {
					apath = Utility.cleanString(apath);
					if(!apath.startsWith("/")) apath = "/tmp/"+apath;
					RedisManager.initAuthPath(apath);
				}
			}				
		}
		
		System.out.println("Port : "+port );	
		System.out.println(Utility.cleanString("Redis : "+RedisManager.redis ));	
		System.out.println(Utility.cleanString("Redis Conf Path: "+RedisManager.authPath ));
		
		RedisManager.init();
		ExecutorManager threadPool = new ExecutorManager(24);
		
        if(ConfigData.timeline) {
      	  ProcL01.init(threadPool);
      	  ProcL20.init(threadPool);
        }  
        if(ConfigData.datafeed) {
      	  	ProcStockCategory.init(1);
      	  	ProcStockName.init(1);
      	  	ProcStockEngName.init(1);
      	  	ProcStockSnapshot.init();
      	  	
    		ProcF01.init(threadPool);
    		ProcF02.init(threadPool);
    		ProcF06.init(threadPool);

            ProcF09.init(threadPool);
            ProcF13.init(threadPool);
            ProcF19.init(threadPool);
    		ProcF20.init(threadPool);
    		ProcF22.init(threadPool);
    		ProcF23.init(threadPool);	       	
        }
        if(ConfigData.datafeed || ConfigData.timeline) {
        	ProcF99.init(threadPool); 
        }
		
		Launcher vs = new Launcher(port,true);
		vs.start();
		
		
    	while(true){
    		try {
				Thread.sleep(10000);
				System.out.println(Utility.getFullyDateTimeStr()+" StockInboundHandler:"+ StockInboundHandler.decodeCount.get()+" RedisCommand:"+ RedisManager.getCommandCount() +" RedisQueue="+RedisManager.size()+" Datafeed:"+ProcF01.uaHash.size()+" Timeline:"+ProcL01.uaHash.size());
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    	}
	}	
	
}
