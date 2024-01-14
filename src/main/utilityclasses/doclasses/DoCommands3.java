package main.utilityclasses.doclasses;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.commandhelper.Command;
import main.creatorclasses.artistclasses.Merch;
import main.creatorclasses.subscription.NotificationBar;
import main.playlistclasses.UserData;

import java.util.ArrayList;
import java.util.Map;

public final class DoCommands3 {

    private DoCommands3() {
    }

    /**
     * Main method call for getNotifications command
     *
     * @param objectMapper Object Mapper
     * @param crtCommand Current command with all its data
     * @param notificationBars The notification bars of all users
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doGetNotifications(final ObjectMapper objectMapper,
                                                final Command crtCommand,
                                                final ArrayList<NotificationBar>
                                                        notificationBars) {
        ObjectNode getNotificationsOutput = objectMapper.createObjectNode();

        getNotificationsOutput.put("command", "getNotifications");
        getNotificationsOutput.put("user", crtCommand.getUsername());
        getNotificationsOutput.put("timestamp", crtCommand.getTimestamp());

        ArrayList<ObjectNode> notifications = new ArrayList<>();

        //  Find user notifications
        NotificationBar crtBar = null;
        for (NotificationBar bar : notificationBars) {
            if (bar.getSubscriber().equals(crtCommand.getUsername())) {
                crtBar = bar;
                break;
            }
        }

        //  Add notifications to display
        for (String notification : crtBar.getNotifications()) {
            ObjectNode node = objectMapper.createObjectNode();
            String[] info = notification.split("/");

            node.put("name", "New " + info[0]);
            node.put("description", "New " + info[0] + " from " + info[1] + ".");

            notifications.add(node);
        }

        //  Clear notifications
        crtBar.getNotifications().clear();

        getNotificationsOutput.putPOJO("notifications", notifications);

        return getNotificationsOutput;
    }

    /**
     * Main method call for getNotifications command
     *
     * @param objectMapper Object Mapper
     * @param crtCommand Current command with all its data
     * @param usersData The data (playlist, merches) of all users
     * @return ObjectNode of the final JSON
     */
    public static ObjectNode doSeeMerch(final ObjectMapper objectMapper,
                                        final Command crtCommand,
                                        final ArrayList<UserData> usersData) {
        ObjectNode seeMerchOutput = objectMapper.createObjectNode();

        seeMerchOutput.put("command", "seeMerch");
        seeMerchOutput.put("user", crtCommand.getUsername());
        seeMerchOutput.put("timestamp", crtCommand.getTimestamp());

        //  Find user data
        UserData crtData = null;
        for (UserData userData : usersData) {
            if (userData.getUser().getUsername()
                    .equals(crtCommand.getUsername())) {
                crtData = userData;
                break;
            }
        }

        if (crtData == null) {
            seeMerchOutput.put("message",
                    "The username " + crtCommand.getUsername() + " doesn't exist.");
        } else {
            ArrayList<String> result = new ArrayList<>();
            for (Map.Entry<Merch, String> merch
                    : crtData.getMerches().entrySet()) {
                result.add(merch.getKey().getName());
            }
            seeMerchOutput.putPOJO("result", result);
        }

        return seeMerchOutput;
    }
}
