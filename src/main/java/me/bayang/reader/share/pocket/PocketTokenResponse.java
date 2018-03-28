package me.bayang.reader.share.pocket;

public class PocketTokenResponse {
    
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PocketTokenResponse [code=").append(code).append("]");
        return builder.toString();
    }
    
    
}
