package bjzhou.coolapk.app.exceptions;

import bjzhou.coolapk.app.model.Result;

public class ClientException extends Exception {
    private int statusCode;

    public ClientException(Result result) {
        this(result.getStatusCode(), result.getMessage());
    }

    public ClientException(int i, String str) {
        super(str);
        this.statusCode = i;
    }

    public int getStatusCode() {
        return this.statusCode;
    }
}
