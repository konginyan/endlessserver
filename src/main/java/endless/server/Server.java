package endless.server;

import endless.listener.CSListener;
import endless.util.MessageUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.concurrent.TimeUnit;

public class Server {
    private ServerBootstrap bootstrap;
    private ChannelGroup channelGroup;
    private ServerHandler defaultServerHandler;
    private CSListener serverStartListener;
    public Server(){
        channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        defaultServerHandler = new ServerHandler(this);
        serverStartListener = f->System.out.println("server listening on 80...");
    }

    public void setServerStartListener(CSListener listener){
        serverStartListener = listener;
    }

    public void setServerStartListener(Runnable job, int interval){
        serverStartListener = f-> ((ChannelFuture)f).channel().eventLoop().schedule(job,interval, TimeUnit.SECONDS);
    }

    public void addChannel(Channel channel){
        channelGroup.add(channel);
    }

    public void removeChannel(Channel channel){
        channelGroup.remove(channel);
    }

    public void start(int port, ChannelInboundHandlerAdapter adapter){
        EventLoopGroup bossgroup = new NioEventLoopGroup();
        EventLoopGroup workgroup = new NioEventLoopGroup();
        try{
            bootstrap = new ServerBootstrap();
            bootstrap.group(bossgroup,workgroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(adapter);
                        }
                    });
            doConnect(port);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            bossgroup.shutdownGracefully();
            workgroup.shutdownGracefully();
        }
    }

    public void start(int port){
        start(port,defaultServerHandler);
    }

    private void doConnect(int port) throws InterruptedException{
        ChannelFuture future = bootstrap.bind(port);
        future.addListener(f->serverStartListener.run(f));
        future.channel().closeFuture().sync();
    }

    public void broadcast(String data){
        channelGroup.writeAndFlush(MessageUtil.String2ByteBuf(data));
    }

    public static void main(String args[]){
        Server server = new Server();
        server.start(80);
    }
}
