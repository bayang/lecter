package me.bayang.reader.rssmodels;

public class AddResult {
    private String query;
    private int numResults;
    private String streamId;
    private String streamName;

    public AddResult() {
    }

    public AddResult(String query, int numResults, String streamId, String streamName) {
        this.query = query;
        this.numResults = numResults;
        this.streamId = streamId;
        this.streamName = streamName;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getNumResults() {
        return numResults;
    }

    public void setNumResults(int numResults) {
        this.numResults = numResults;
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public String getStreamName() {
        return streamName;
    }

    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }
    
    
}