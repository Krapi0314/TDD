package e2e;

import org.hamcrest.Matcher;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
public class SingleMessageListener {

    private final ArrayBlockingQueue<String> messages = new ArrayBlockingQueue<>(1);

    public void processMessage(String channel, String message) {
        messages.add(message);
    }

    public void receiveAMessage() throws InterruptedException {
        assertThat("메세지 확인", messages.poll(5, TimeUnit.SECONDS), is(notNullValue()));
    }

    public void receiveAMessageMatcher(Matcher<? super String> matcher) throws InterruptedException {
        assertThat(messages.poll(5, TimeUnit.SECONDS), matcher);
    }
}
