package me.bayang.reader.rssmodels;

import java.util.ArrayList;

/**
 * A SubscriptionsList represents a object get from Inoreader's <a href="https://www.inoreader.com/developers/subscription-list">Subscriptions List</a>
 */
public class SubscriptionsList {
    
    private ArrayList<Subscription> subscriptions;
    
    public SubscriptionsList() {
        super();
    }

    public SubscriptionsList(ArrayList<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public ArrayList<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(ArrayList<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SubscriptionsList [subscriptions=")
                .append(subscriptions).append("]");
        return builder.toString();
    }
    
    
}
