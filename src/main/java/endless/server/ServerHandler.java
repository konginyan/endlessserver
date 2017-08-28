package endless.server;

import endless.util.MessageUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@ChannelHandler.Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter{
    private Server server;

    public ServerHandler(Server server) {
        super();
        this.server = server;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client "+ctx.channel().id()+" join server.");
        server.addChannel(ctx.channel());
        ctx.writeAndFlush(MessageUtil.String2ByteBuf(" connected success!"));
        server.broadcast(ctx.name()+" connected success!");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        server.removeChannel(ctx.channel());
        System.out.println("client "+ctx.channel().id()+" leave server.");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("has received from client "+ctx.name()+": "+MessageUtil.ByteBuf2String((ByteBuf) msg));
    }
}
