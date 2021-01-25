package com.dak.noths;

import java.math.BigDecimal;

public class CheckoutItem {

    private final String id;
    private BigDecimal price;

    public CheckoutItem (String id, BigDecimal price)
    {
        this.id = id;
        this.price = price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
