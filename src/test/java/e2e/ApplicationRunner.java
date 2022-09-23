package e2e;

import sniper.Main;

public class ApplicationRunner {

    private AuctionSniperDriver sniperDriver;
    public void startBiddingIn(FakeAuctionServer auction) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Main.main(auction.getItemId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.setDaemon(true);
        thread.start();

        sniperDriver = new AuctionSniperDriver(1000);
        sniperDriver.showsSniperStatus("joining");
    }

    public void showsSniperHasLostAuction() {
        sniperDriver.showsSniperStatus("lost");
    }

    public void stop() {
        Main.ui.dispose();
    }

    public void hasShownSniperIsBidding() {
        sniperDriver.showsSniperStatus("bidding");
    }
}
