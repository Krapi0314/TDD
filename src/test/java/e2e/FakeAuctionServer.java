package e2e;

import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import sniper.Main;

import static org.hamcrest.Matchers.*;
public class FakeAuctionServer {

    RedisClient client;
    SingleMessageListener messageListener;
    String itemId;

    public FakeAuctionServer(String itemId) {
        client = RedisClient.create("redis://localhost");
        messageListener = new SingleMessageListener();
        this.itemId = itemId;
    }
    public void startSellingItem() {
        StatefulRedisPubSubConnection<String, String> connection = client.connectPubSub();
        connection.addListener(new RedisPubSubListener<String, String>() {
            @Override
            public void message(String channel, String message) {
                messageListener.processMessage(channel, message);
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

        RedisPubSubAsyncCommands<String, String> async = connection.async();
        async.subscribe("SNIPER-" + itemId);
    }

    public String getItemId() {
        return this.itemId;
    }

    public void hasReceivedJoinRequestFromSniper() throws InterruptedException {
        messageListener.receiveAMessageMatcher(equalTo(Main.JOIN_COMMAND_FORMAT));
    }

    public void announceClose() {
        StatefulRedisPubSubConnection<String, String> connection = client.connectPubSub();
        RedisPubSubCommands<String, String> sync = connection.sync();
        sync.publish("AUCTION-" + itemId, "EVENT : close;");
    }

    public void stop() {
        client.close();
    }

    public void reportPrice(int price, int increment, String bidder) {
        StatefulRedisPubSubConnection<String, String> connection = client.connectPubSub();
        RedisPubSubCommands<String, String> sync = connection.sync();
        sync.publish("AUCTION-" + itemId,
                String.format(Main.PRICE_COMMAND_FORMAT, price, increment, bidder));
    }

    public void hasReceivedBid(int price, String sniper) throws Exception {
        messageListener.receiveAMessageMatcher(
                equalTo(String.format(String.format(Main.BID_COMMAND_FORMAT, price, sniper)))
        );
    }
}
