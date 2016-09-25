package com.archer.toppr_c.data_model;

import java.util.List;

/**
 * Created by Swastik on 25-09-2016.
 */
public class ResponseDO
{
    private List<EventDO> websites;
    private Long quote_max;
    private Long quote_available;

    public List<EventDO> getWebsites() {
        return websites;
    }

    public void setWebsites(List<EventDO> websites) {
        this.websites = websites;
    }

    public Long getQuote_max() {
        return quote_max;
    }

    public void setQuote_max(Long quote_max) {
        this.quote_max = quote_max;
    }

    public Long getQuote_available() {
        return quote_available;
    }

    public void setQuote_available(Long quote_available) {
        this.quote_available = quote_available;
    }
}
