package com.dak.noths;

import com.dak.noths.rules.PromoRules;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class Basket {

    private BigDecimal discount = BigDecimal.ZERO;
    private List<CheckoutItem> basketItems;

    public Basket(List<CatalogueItem> items) {
        basketItems = items.stream().map(i -> new CheckoutItem(i.getId(), i.getPrice())).collect(Collectors.toList());
    }

    void apply(List<PromoRules> promotions) {
        promotions.forEach(p -> p.apply(this));
    }

    public void applyDiscount(BigDecimal amount) {
        discount = discount.add(amount);
    }

    public BigDecimal getBasketDiscount() {
        return discount;
    }

    public List<CheckoutItem> getBasketItems() {
        return basketItems;
    }

    public BigDecimal getBasketPrice() {
        return basketItems.stream().map(i -> i.getPrice()).reduce(BigDecimal.ZERO, BigDecimal::add).subtract(discount);
    }
}
