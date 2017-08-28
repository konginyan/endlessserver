package endless.protocol;

public interface Protocol {
    boolean isProtocol(String msg);

    void parse(String msg);

    void afterParse(String result);
}
