package com.dak.noths.rules;

import com.dak.noths.Basket;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DiscountRule implements PromoRules {

    enum DiscountType {
        PERCENT,ABSOLUTE;
    }

    private static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

    BigDecimal discount;
    DiscountType type;
    BigDecimal price;

    public void setDiscount(String discount) {
        this.discount = new BigDecimal(discount);
    }

    public void setType(String type) {
        this.type = DiscountType.valueOf(type);
    }

    public void setPrice(String price) {
        this.price = new BigDecimal(price);
    }

    public void apply(Basket basket) {
        BigDecimal basketPrice = basket.getBasketPrice();
        if (basketPrice.compareTo(price) > 0) {
            BigDecimal discountValue = discount;
            if (type == DiscountType.PERCENT) {
                discountValue = basketPrice.multiply(discount).divide(ONE_HUNDRED).setScale(2, RoundingMode.DOWN);
            }
            basket.applyDiscount(discountValue);
        }
    }
}
