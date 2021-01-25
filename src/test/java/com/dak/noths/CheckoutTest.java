package com.dak.noths;

import com.dak.noths.rules.PromoRules;
import org.junit.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class CheckoutTest {

    @Test
    public void testConfigFileLoad() throws Exception {
        Checkout checkout = new Checkout("testInventory.txt", "testPromotions.txt");

        Field catalogueField = Checkout.class.getDeclaredField("catalogue");
        catalogueField.setAccessible(true);
        Map<String, CatalogueItem> catalogue = (Map) catalogueField.get(checkout);
        assertEquals(5, catalogue.size());

        Field discountField = Checkout.class.getDeclaredField("discountRuleList");
        discountField.setAccessible(true);
        List<PromoRules> discountRuleList = (List) discountField.get(checkout);
        assertEquals(2, discountRuleList.size());

        Field bulkField = Checkout.class.getDeclaredField("bulkRuleList");
        bulkField.setAccessible(true);
        List<PromoRules> bulkRuleList = (List) bulkField.get(checkout);
        assertEquals(3, bulkRuleList.size());
    }

    @Test
    public void testScanFunction() throws Exception {
        Checkout checkout = new Checkout("testInventory.txt", "");

        Field itemsField = Checkout.class.getDeclaredField("itemList");
        itemsField.setAccessible(true);
        List<CatalogueItem> itemList = (List) itemsField.get(checkout);
        assertEquals(0, itemList.size());

        for (int i = 1; i <= 5; ++i) {
            checkout.scan("ID_" + i);
            assertEquals(i, itemList.size());
            CatalogueItem item = itemList.get(i - 1);
            assertEquals("ID_" + i, item.getId());
            assertEquals("Name_" + i, item.getName());
            StringBuilder sb = new StringBuilder();
            sb.append(i).append(i).append(".").append(i).append(i);
            assertEquals(new BigDecimal(sb.toString()), item.getPrice());
        }

        checkout.scan("ID_6");
        assertEquals(5, itemList.size());
        checkout.scan("ID_1");
        assertEquals(6, itemList.size());
        checkout.scan("BOGUS");
        assertEquals(6, itemList.size());
        checkout.scan("ID_3");
        assertEquals(7, itemList.size());
        checkout.scan("ID_5");
        assertEquals(8, itemList.size());
        checkout.scan("ID_0005");
        assertEquals(8, itemList.size());

    }

    @Test
    public void testTotalFunction() throws Exception {
        Checkout checkout = new Checkout("testInventory.txt", "");
        checkout.scan("ID_1");
        assertEquals("£11.11", checkout.total());
        checkout.scan("ID_2");
        assertEquals("£33.33", checkout.total());
        checkout.scan("ID_3");
        assertEquals("£66.66", checkout.total());
        checkout.scan("ID_3");
        assertEquals("£99.99", checkout.total());
        checkout.scan("ID_1");
        assertEquals("£111.10", checkout.total());
        checkout.scan("ID_5");
        assertEquals("£166.65", checkout.total());
        checkout.scan("ID_4");
        assertEquals("£211.09", checkout.total());
    }

    @Test
    public void testApplicationOfPromotion() throws Exception {
        Checkout checkout = new Checkout("testInventory.txt", "testPromotions.txt");
        checkout.scan("ID_1");
        assertEquals("£11.11", checkout.total());
        checkout.scan("ID_1");
        assertEquals("£17.00", checkout.total());
        checkout.scan("ID_1");
        assertEquals("£25.50", checkout.total());
        checkout.scan("ID_2");
        assertEquals("£47.72", checkout.total());
        checkout.scan("ID_2");
        assertEquals("£62.95", checkout.total());

    }
}
