package endless.protocol;

public abstract class AbstractProtocol implements Protocol{
    protected String order;

    public AbstractProtocol(String msg){
        if(isProtocol(msg)){
            parse(msg);
            afterParse(order);
        }
    }

    public boolean isProtocol(String msg) {
        return msg.trim().startsWith("kongin-protocol:");
    }

    public abstract void parse(String msg);

    public abstract void afterParse(String result);
}
