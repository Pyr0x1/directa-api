package it.pyrox.directa.api;

public class DatafeedApi extends DirectaApi {

    private static final Integer DEFAULT_DATAFEED_PORT = 10001;

    public DatafeedApi(String accountId) {
        super(accountId);
    }

    @Override
    protected Integer getPort() {
        return DEFAULT_DATAFEED_PORT;
    }
}
