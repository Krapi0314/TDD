package e2e;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Random;

public class AuctionSniperEndToEndTest {

    FakeAuctionServer auction;
    ApplicationRunner application;

    @BeforeEach public void tearUp() {
        auction = new FakeAuctionServer("item-54321");
        application = new ApplicationRunner();
    }


    @Test
    void sniperJoinsAuctionUntilAuctionCloses() throws Exception {
        auction.startSellingItem();
        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFromSniper();
        auction.announceClose();
        application.showsSniperHasLostAuction();
    }

    @AfterEach
    public void stop() {
        auction.stop();
        application.stop();
    }

    @Test
    public void sniperMakesAHigherRandomBidButLose() throws Exception {
        auction.startSellingItem();
        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFromSniper();
        int inc = 98;
        auction.reportPrice(1000, inc, "other");
        application.hasShownSniperIsBidding();
        auction.hasReceivedBid(1000 + inc, "sniper");
        auction.announceClose();
        application.showsSniperHasLostAuction();
    }
}
