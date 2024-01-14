package main.utilityclasses.getmessageclasses;

import fileio.input.UserInput;
import main.commandhelper.Command;
import main.creatorclasses.artistclasses.Management;
import main.creatorclasses.artistclasses.Merch;
import main.creatorclasses.subscription.CreatorChannel;
import main.monetization.PremiumUser;
import main.pagingclasses.Page;
import main.playlistclasses.UserData;

import java.util.ArrayList;

public final class GetMessages3 {

    private GetMessages3() {
    }

    /**
     * This method buys a premium subscription
     *
     * @param crtCommand Current command with all its data
     * @param premiumUsers List of all premium users
     * @param cancelledPremiumUsers List of all users who used to have premium
     * @return ObjectNode of the final JSON
     */
    public static String getBuyPremiumMessage(final ArrayList<PremiumUser> premiumUsers,
                                              final ArrayList<PremiumUser>
                                                      cancelledPremiumUsers,
                                              final Command crtCommand) {
        String message;

        //  First check if the user is already premium
        boolean isPremium = false;
        for (PremiumUser user : premiumUsers) {
            if (user.getUser()
                    .equals(crtCommand.getUsername())) {
                isPremium = true;
                break;
            }
        }
        //  Then check whether the user has a cancelled subscription
        PremiumUser cancelledUser = null;
        for (PremiumUser user : cancelledPremiumUsers) {
            if (user.getUser()
                    .equals(crtCommand.getUsername())) {
                cancelledUser = user;
                break;
            }
        }

        if (isPremium) {
            message = crtCommand.getUsername() + " is already a premium user.";
        } else {
            if (cancelledUser == null) {
                PremiumUser newUser = new PremiumUser(crtCommand.getUsername());
                premiumUsers.add(newUser);
            } else {
                premiumUsers.add(cancelledUser);
                cancelledPremiumUsers.remove(cancelledUser);
            }

            message = crtCommand.getUsername()
                    + " bought the subscription successfully.";
        }

        return message;
    }

    /**
     * This method cancels a premium subscription
     *
     * @param crtCommand Current command with all its data
     * @param premiumUsers List of all premium users
     * @param cancelledPremiumUsers List of all users who used to have premium
     * @return ObjectNode of the final JSON
     */
    public static String getCancelPremiumMessage(final ArrayList<PremiumUser> premiumUsers,
                                                 final ArrayList<PremiumUser>
                                                         cancelledPremiumUsers,
                                                 final Command crtCommand) {
        String message;

        //  First check if the user is already premium
        PremiumUser existingUser = null;
        for (PremiumUser user : premiumUsers) {
            if (user.getUser()
                    .equals(crtCommand.getUsername())) {
                existingUser = user;
                break;
            }
        }

        if (existingUser == null) {
            message = crtCommand.getUsername() + " is not a premium user.";
        } else {
            cancelledPremiumUsers.add(existingUser);
            premiumUsers.remove(existingUser);
            message = crtCommand.getUsername()
                    + " cancelled the subscription successfully.";
        }

        return message;
    }

    /**
     * This method subscribes/unsubscribes a user to a creator channel
     *
     * @param pageSystem All user pages
     * @param channels All creator channels
     * @param crtCommand Current command with all the data
     * @return ObjectNode of the final JSON
     */
    public static String getSubscribeMessage(final ArrayList<Page> pageSystem,
                                             final ArrayList<CreatorChannel> channels,
                                             final Command crtCommand) {
        //  Find the user's page
        Page crtPage = null;
        for (Page page : pageSystem) {
            if (page.getPageOwner().getUsername()
                    .equals(crtCommand.getUsername())) {
                crtPage = page;
                break;
            }
        }

        String message = null;
        if (crtPage == null) {
            message = "The username " + crtCommand.getUsername()
                    + " doesn't exist.";
        } else if (!crtPage.getCurrentPage().equals("ArtistPage")
                && !crtPage.getCurrentPage().equals("HostPage")) {
            message = "To subscribe you need to be on the page of"
                    + "an artist or host.";
        } else {
            UserInput creator = crtPage.getUserData().getUser();

            //  Add subscriber to creator channel
            for (CreatorChannel channel : channels) {
                if (channel.getCreator().equals(creator)) {
                    //  Find subscriber
                    if (channel.getSubscribers()
                            .contains(crtCommand.getUsername())) {
                        channel.getSubscribers().remove(crtCommand.getUsername());
                        message = crtCommand.getUsername() + " unsubscribed from "
                                + channel.getCreator().getUsername()
                                + " successfully.";
                    } else {
                        channel.getSubscribers().add(crtCommand.getUsername());
                        message = crtCommand.getUsername() + " subscribed to "
                                + channel.getCreator().getUsername()
                                + " successfully.";
                    }
                    break;
                }
            }
        }

        return message;
    }

    /**
     * This method buys merch from an artist
     *
     * @param pageSystem All user pages
     * @param crtCommand Current command with all the data
     * @param managements All artist extras
     * @param merchSellers List of artists who have sold merch
     * @param usersData All user data (playlist and merches)
     * @return ObjectNode of the final JSON
     */
    public static String getBuyMerchMessage(final ArrayList<Page> pageSystem,
                                            final Command crtCommand,
                                            final ArrayList<Management> managements,
                                            final ArrayList<String> merchSellers,
                                            final ArrayList<UserData> usersData) {
        //  Find the user's page
        Page crtPage = null;
        for (Page page : pageSystem) {
            if (page.getPageOwner().getUsername()
                    .equals(crtCommand.getUsername())) {
                crtPage = page;
                break;
            }
        }

        String message = null;
        if (crtPage == null) {
            message = "The username " + crtCommand.getUsername()
                    + " doesn't exist.";
        } else if (!crtPage.getCurrentPage().equals("ArtistPage")) {
            message = "Cannot buy merch from this page.";
        } else {

            //  Check for merch
            Merch wantedMerch = null;
            for (Management management : managements) {
                if (management.getArtist().getUsername()
                        .equals(crtPage.getUserData().getUser().getUsername())) {
                    for (Merch merch : management.getMerches()) {
                        if (merch.getName().equals(crtCommand.getName())) {
                            wantedMerch = merch;
                            break;
                        }
                    }

                    if (wantedMerch != null) {
                        break;
                    }
                }
            }

            if (wantedMerch == null) {
                message = "The merch " + crtCommand.getName()
                        + " doesn't exist.";
            } else {
                //  Add merch to user data
                for (UserData crtData : usersData) {
                    if (crtData.getUser().getUsername()
                            .equals(crtCommand.getUsername())) {
                        //  Add merch to inventory
                        crtData.getMerches().put(wantedMerch,
                                crtPage.getUserData().getUser().getUsername());
                        //  Add merch seller
                        if (!merchSellers.contains(crtPage.getUserData()
                                .getUser().getUsername())) {
                            merchSellers.add(crtPage.getUserData()
                                    .getUser().getUsername());
                        }

                        message = crtCommand.getUsername()
                                + " has added new merch successfully.";
                        break;
                    }
                }
            }
        }

        return message;
    }
}
