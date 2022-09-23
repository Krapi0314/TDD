package sniper;

import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class Main {

    public static final String MAIN_WINDOW = "MAIN WINDOW";

    public static final String BID_COMMAND_FORMAT = "EVENT : bid;PRICE : %d;BIDDER: %s;";
    public static final String JOIN_COMMAND_FORMAT = "EVENT : join;";
    public static final String PRICE_COMMAND_FORMAT = "EVENT : price;CURRENT : %d;INCREMENT : %d;BIDDER : %s;";

    public static MainWindow ui;
    RedisClient client;

    public Main(String itemId) throws InterruptedException, InvocationTargetException {
        startUserInterface();
        client = RedisClient.create("redis://localhost");
        StatefulRedisPubSubConnection<String, String> connection = client.connectPubSub();
        StatefulRedisPubSubConnection<String, String> connection2 = client.connectPubSub();

        RedisPubSubCommands<String, String> sync = connection.sync();
        sync.publish("SNIPER-" + itemId, JOIN_COMMAND_FORMAT);

        connection2.addListener(new RedisPubSubListener<String, String>() {
            @Override
            public void message(String channel, String message) {
                if( message.startsWith("EVENT : price;") ) {
                    ui.showsStatus("bidding");

                    String current = Arrays.stream(message.split(";")).filter(item -> item.startsWith("CURRENT")).findAny().orElse("");
                    // "CURRENT : 1000" -> 1000?
                    int cur = Integer.parseInt(current.substring(current.indexOf(":") + 2));
                    String increment = Arrays.stream(message.split(";")).filter(item -> item.startsWith("INCREMENT")).findAny().orElse("");
                    int inc = Integer.parseInt(increment.substring(increment.indexOf(":") + 2));

                    sync.publish("SNIPER-" + itemId, String.format(BID_COMMAND_FORMAT, cur + inc, "sniper"));
                }
                else if( message.startsWith("EVENT : close;") )
                    ui.showsStatus("lost");
            }

            @Override
            public void message(String pattern, String channel, String message) {

            }

            @Override
            public void subscribed(String channel, long count) {

            }

            @Override
            public void psubscribed(String pattern, long count) {

            }

            @Override
            public void unsubscribed(String channel, long count) {

            }

            @Override
            public void punsubscribed(String pattern, long count) {

            }
        });
        RedisPubSubAsyncCommands<String, String> async = connection2.async();
        async.subscribe("AUCTION-" + itemId);
    }

    private void startUserInterface() throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                ui = new MainWindow();
            }
        });
    }

    public static void main(String... args) throws InterruptedException, InvocationTargetException {
        Main main = new Main(args[0]);
    }
}
