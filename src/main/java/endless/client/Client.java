package endless.client;

import endless.util.MessageUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Client {
    private Bootstrap bootstrap;
    private Channel clientChannel;

    private boolean isOnline(){
        return clientChannel!=null && clientChannel.isActive();
    }

    public void start(String host, int port){
        EventLoopGroup workgroup = new NioEventLoopGroup();
        try{
            bootstrap = new Bootstrap();
            bootstrap.group(workgroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new ClientHandler());
                        }
                    });
            doConnect(host,port);
        }
        catch (Exception e){
        }
        finally {
            workgroup.shutdownGracefully();
        }
    }

    private void doConnect(String host, int port) throws InterruptedException{
        ChannelFuture future = bootstrap.connect(host, port);
        future.addListener(f->{
            ChannelFuture cf = (ChannelFuture)f;
            if (cf.isSuccess()) {
                clientChannel = cf.channel();
            }
            else {
                System.out.println("connection refused by server");
            }
        });
        future.channel().closeFuture().sync();
    }

    public void sendData(String data){
        if(isOnline()){
            clientChannel.writeAndFlush(MessageUtil.String2ByteBuf(data));
        }
    }

    public static void main(String args[]){
        Client client = new Client();
        client.start("localhost",80);
    }
}
