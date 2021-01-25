package com.dak.noths.rules;

import com.dak.noths.Basket;
import com.dak.noths.CheckoutItem;

import java.math.BigDecimal;
import java.util.List;

public class BulkRule implements PromoRules {

    String item;
    int count;
    BigDecimal price;

    public void setItem(String id) {
        this.item = id;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setPrice(String price) {
        this.price = new BigDecimal(price);
    }

    public void apply(Basket basket) {
        if (basket.getBasketItems().stream().filter(i -> i.getId().equals(item)).count() >= count) {
            basket.getBasketItems().stream().filter(i -> i.getId().equals(item)).forEach(i -> i.setPrice(price));
        }
    }

}
