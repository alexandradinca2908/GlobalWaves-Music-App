package main.utilityclasses.doclasses;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.commandhelper.Command;
import main.creatorclasses.artistclasses.Management;
import main.creatorclasses.subscription.CreatorChannel;
import main.monetization.PremiumUser;
import main.pagingclasses.Page;
import main.playlistclasses.UserData;

import java.util.ArrayList;

import static main.utilityclasses.getmessageclasses.GetMessages3.getBuyMerchMessage;
import static main.utilityclasses.getmessageclasses.GetMessages3.getBuyPremiumMessage;
import static main.utilityclasses.getmessageclasses.GetMessages3.getSubscribeMessage;
import static main.utilityclasses.getmessageclasses.GetMessages3.getCancelPremiumMessage;

public final class DoCommandsMessage3 {

    private DoCommandsMessage3() {
    }

    /**
     * Main method call for buyPremiumCommand
     *
     * @param objectMapper Object Mapper
     * @param crtCommand Current command with all its data
     * @param premiumUsers List of all premium users
     * @param cancelledPremiumUsers List of all users who used to have premium
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doBuyPremium(final ObjectMapper objectMapper,
                                          final Command crtCommand,
                                          final ArrayList<PremiumUser> premiumUsers,
                                          final ArrayList<PremiumUser>
                                                  cancelledPremiumUsers) {
        ObjectNode buyPremiumOutput = objectMapper.createObjectNode();

        buyPremiumOutput.put("command", "buyPremium");
        buyPremiumOutput.put("user", crtCommand.getUsername());
        buyPremiumOutput.put("timestamp", crtCommand.getTimestamp());

        String message = getBuyPremiumMessage(premiumUsers,
                cancelledPremiumUsers, crtCommand);

        buyPremiumOutput.put("message", message);

        return buyPremiumOutput;
    }

    /**
     * Main method call for cancelPremium command
     *
     * @param objectMapper Object Mapper
     * @param crtCommand Current command with all its data
     * @param premiumUsers List of all premium users
     * @param cancelledPremiumUsers List of all users who used to have premium
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doCancelPremium(final ObjectMapper objectMapper,
                                             final Command crtCommand,
                                             final ArrayList<PremiumUser> premiumUsers,
                                             final ArrayList<PremiumUser>
                                                     cancelledPremiumUsers) {
        ObjectNode cancelPremiumOutput = objectMapper.createObjectNode();

        cancelPremiumOutput.put("command", "cancelPremium");
        cancelPremiumOutput.put("user", crtCommand.getUsername());
        cancelPremiumOutput.put("timestamp", crtCommand.getTimestamp());

        String message = getCancelPremiumMessage(premiumUsers,
                cancelledPremiumUsers, crtCommand);

        cancelPremiumOutput.put("message", message);

        return cancelPremiumOutput;
    }

    /**
     * Main method call for subscribe command
     *
     * @param objectMapper Object Mapper
     * @param pageSystem All user pages
     * @param channels All creator channels
     * @param crtCommand Current command with all the data
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doSubscribe(final ObjectMapper objectMapper,
                                         final ArrayList<Page> pageSystem,
                                         final ArrayList<CreatorChannel> channels,
                                         final Command crtCommand) {
        ObjectNode subscribeOutput = objectMapper.createObjectNode();

        subscribeOutput.put("command", "subscribe");
        subscribeOutput.put("user", crtCommand.getUsername());
        subscribeOutput.put("timestamp", crtCommand.getTimestamp());

        String message = getSubscribeMessage(pageSystem, channels,
                crtCommand);

        subscribeOutput.put("message", message);

        return subscribeOutput;
    }

    /**
     * Main method call for buyMerch command
     *
     * @param objectMapper Object Mapper
     * @param pageSystem All user pages
     * @param crtCommand Current command with all the data
     * @param managements All artist extras
     * @param merchSellers List of artists who have sold merch
     * @param usersData All user data (playlist and merches)
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doBuyMerch(final ObjectMapper objectMapper,
                                        final ArrayList<Page> pageSystem,
                                        final Command crtCommand,
                                        final ArrayList<Management> managements,
                                        final ArrayList<String> merchSellers,
                                        final ArrayList<UserData> usersData) {
        ObjectNode buyMerchOutput = objectMapper.createObjectNode();

        buyMerchOutput.put("command", "buyMerch");
        buyMerchOutput.put("user", crtCommand.getUsername());
        buyMerchOutput.put("timestamp", crtCommand.getTimestamp());

        String message = getBuyMerchMessage(pageSystem, crtCommand,
                managements, merchSellers, usersData);

        buyMerchOutput.put("message", message);

        return buyMerchOutput;
    }
}
