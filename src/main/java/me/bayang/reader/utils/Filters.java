package me.bayang.reader.utils;

import java.util.function.Predicate;

import javafx.beans.Observable;
import javafx.util.Callback;
import me.bayang.reader.rssmodels.Item;

public class Filters {
    
    public static final Predicate<Item> NO_FILTER_PREDICATE  = item -> {
        // display all
        return true;
    }; 
    
    public static final Predicate<Item> FILTER_READ_PREDICATE = item -> {
        if (item == null) {
            return false;
        }
        if (! item.isRead()) {
            return true;
        }
        return false;
    };
    
    public static final Predicate<Item> FILTER_UNREAD_PREDICATE  = item -> {
        if (item == null) {
            return false;
        }
        if (item.isRead()) {
            return true;
        }
        return false;
    };
    
    public static Callback<Item, Observable[]> itemExtractor() {
        Callback<Item, Observable[]> itemExtractor = (Item i) -> {
            return new Observable[]{i.readProperty()};
        };
        return itemExtractor;
    }
    
}
