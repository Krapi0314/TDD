package e2e;

import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JLabelDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;
import sniper.Main;

import static org.hamcrest.Matchers.*;
public class AuctionSniperDriver extends JFrameDriver {

    public AuctionSniperDriver(int timeout) {
        super(new GesturePerformer(), JFrameDriver.topLevelFrame(named(Main.MAIN_WINDOW), showingOnScreen()),
                new AWTEventQueueProber(timeout, 100));
    }

    public void showsSniperStatus(String status) {
        new JLabelDriver(this, named("sniperStatus")).hasText(equalTo(status));
    }
}
