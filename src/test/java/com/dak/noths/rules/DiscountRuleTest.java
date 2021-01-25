package com.dak.noths.rules;

import com.dak.noths.Basket;
import com.dak.noths.CatalogueItem;
import com.dak.noths.CheckoutItem;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DiscountRuleTest {

    private final String ID1 = "ID1";
    private final String ID2 = "ID2";
    private final String PRICE = "21.00";
    private final String DISCOUNT = "10.00";
    private final BigDecimal UNIT_PRICE_1 = new BigDecimal("10.50");
    private final BigDecimal UNIT_PRICE_2 = new BigDecimal("9.00");
    private final BigDecimal FULL_PRICE = new BigDecimal("30.00");

    @Test
    public void testDiscountRulePercent() {
        DiscountRule rule = new DiscountRule();
        rule.setPrice(PRICE);
        rule.setDiscount("10");
        rule.setType("PERCENT");

        Basket basket = new Basket(new ArrayList<CatalogueItem>());
        List<CheckoutItem> itemList = basket.getBasketItems();
        // Test with no items - check no exception thrown
        rule.apply(basket);

        // Test total below threshold
        itemList.add(new CheckoutItem(ID1, UNIT_PRICE_1));
        rule.apply(basket);
        assertEquals(BigDecimal.ZERO, basket.getBasketDiscount());
        assertEquals(UNIT_PRICE_1, getItemListTotal(itemList));

        // Test total exactly at threshold
        itemList.add(new CheckoutItem(ID1, UNIT_PRICE_1));
        rule.apply(basket);
        assertEquals(BigDecimal.ZERO, basket.getBasketDiscount());
        assertEquals(new BigDecimal(PRICE), getItemListTotal(itemList));

        // Test total over threshold
        itemList.add(new CheckoutItem(ID2, UNIT_PRICE_2));
        rule.apply(basket);
        assertEquals(new BigDecimal("3.00"), basket.getBasketDiscount());
        assertEquals(FULL_PRICE, getItemListTotal(itemList));
    }

    @Test
    public void testDiscountRuleAbsolute() {
        DiscountRule rule = new DiscountRule();
        rule.setPrice(PRICE);
        rule.setDiscount(DISCOUNT);
        rule.setType("ABSOLUTE");

        Basket basket = new Basket(new ArrayList<CatalogueItem>());
        List<CheckoutItem> itemList = basket.getBasketItems();
        itemList.add(new CheckoutItem(ID1, UNIT_PRICE_1));
        itemList.add(new CheckoutItem(ID1, UNIT_PRICE_1));
        rule.apply(basket);
        assertEquals(BigDecimal.ZERO, basket.getBasketDiscount());
        assertEquals(new BigDecimal(PRICE), getItemListTotal(itemList));

        // Test total over threshold
        itemList.add(new CheckoutItem(ID2, UNIT_PRICE_2));
        rule.apply(basket);
        assertEquals(new BigDecimal(DISCOUNT), basket.getBasketDiscount());
        assertEquals(FULL_PRICE, getItemListTotal(itemList));
    }



    private BigDecimal getItemListTotal(List<CheckoutItem> itemList) {
        return itemList.stream().map(i -> i.getPrice()).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
