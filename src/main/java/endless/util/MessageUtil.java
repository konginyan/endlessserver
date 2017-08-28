package endless.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class MessageUtil {

    public static String ByteBuf2String(ByteBuf buf){
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        String receive = new String(bytes);
        buf.release();
        return receive;
    }

    public static ByteBuf String2ByteBuf(String str){
        ByteBuf buf = Unpooled.buffer(str.length());
        buf.writeBytes(str.getBytes());
        return buf;
    }
}
