package endless.protocol;

public abstract class EndlessProtocol extends AbstractProtocol{

    public EndlessProtocol(String msg) {
        super(msg);
    }

    @Override
    public boolean isProtocol(String msg) {
        return msg.trim().startsWith("kongin-protocol:endless1.0") && msg.split(":").length==3;
    }

    @Override
    public void parse(String msg) {
        order = msg.split(":")[2];
    }

    public abstract void afterParse(String result);
}
