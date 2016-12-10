package com.stefanie20.ReadDay.RSSModels;

import java.util.ArrayList;

/**
 * A SubscriptionsList represents a object get from Inoreader's <a href="https://www.inoreader.com/developers/subscription-list">Subscriptions List</a>
 */
public class SubscriptionsList {
    private ArrayList<Subscription> subscriptions;

    public SubscriptionsList(ArrayList<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public ArrayList<Subscription> getSubscriptions() {
        return subscriptions;
    }
}

class Categories{
    private String id;
    private String label;

    public Categories(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }
}
