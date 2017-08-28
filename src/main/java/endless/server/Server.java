package endless.server;

import endless.util.MessageUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;

public class Server {
    private ServerBootstrap bootstrap;
    private ChannelGroup channelGroup;

    public Server(){
        channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }

    public void addChannel(Channel channel){
        channelGroup.add(channel);
    }

    public void removeChannel(Channel channel){
        channelGroup.remove(channel);
    }

    public void start(int port){
        EventLoopGroup bossgroup = new NioEventLoopGroup();
        EventLoopGroup workgroup = new NioEventLoopGroup();
        try{
            bootstrap = new ServerBootstrap();
            ServerHandler serverHandler = new ServerHandler(this);
            bootstrap.group(bossgroup,workgroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(serverHandler);
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

    private void doConnect(int port) throws InterruptedException{
        ChannelFuture future = bootstrap.bind(port);
        future.addListener(f->System.out.println("server listening on 80..."));
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
