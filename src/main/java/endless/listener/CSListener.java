package endless.listener;

import io.netty.util.concurrent.Future;

@FunctionalInterface
public interface CSListener {

    void run(Future<?> f);
}
