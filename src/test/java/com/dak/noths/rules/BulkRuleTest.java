package com.dak.noths.rules;

import com.dak.noths.Basket;
import com.dak.noths.CatalogueItem;
import com.dak.noths.CheckoutItem;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class BulkRuleTest {

    private final String ID1 = "ID1";
    private final String ID2 = "ID2";
    private final String PRICE = "10.25";
    private final BigDecimal FULL_PRICE = new BigDecimal("12.50");
    private final BigDecimal LOW_PRICE = new BigDecimal(PRICE);

    @Test
    public void testBulkRule() {
        BulkRule rule = new BulkRule();
        rule.setItem(ID1);
        rule.setCount(3);
        rule.setPrice(PRICE);

        Basket basket = new Basket(new ArrayList<CatalogueItem>());
        List<CheckoutItem> itemList = basket.getBasketItems();
        // Test with no items - check no exception thrown
        rule.apply(basket);

        // Test with one matching item
        itemList.add(new CheckoutItem(ID1, FULL_PRICE));
        rule.apply(basket);
        assertEquals(FULL_PRICE, itemList.get(0).getPrice());

        // Test with two matching items
        itemList.add(new CheckoutItem(ID1, FULL_PRICE));
        assertEquals(2, basket.getBasketItems().size());
        rule.apply(basket);
        basket.getBasketItems().forEach(i ->  assertEquals(FULL_PRICE, i.getPrice()));

        // Test with two matching items and two non-matching items
        itemList.add(new CheckoutItem(ID2, LOW_PRICE));
        itemList.add(new CheckoutItem(ID2, LOW_PRICE));
        assertEquals(4, basket.getBasketItems().size());
        rule.apply(basket);
        basket.getBasketItems().stream().filter(i -> i.getId().equals(ID1)).forEach(i ->  assertEquals(FULL_PRICE, i.getPrice()));

        // Test with third matching item
        itemList.add(new CheckoutItem(ID1, FULL_PRICE));
        assertEquals(5, basket.getBasketItems().size());
        rule.apply(basket);
        basket.getBasketItems().forEach(i ->  assertEquals(LOW_PRICE, i.getPrice()));

        // Test with new matching item
        itemList.add(new CheckoutItem(ID1, FULL_PRICE));
        assertEquals(6, basket.getBasketItems().size());
        rule.apply(basket);
        basket.getBasketItems().forEach(i ->  assertEquals(LOW_PRICE, i.getPrice()));

    }
}
