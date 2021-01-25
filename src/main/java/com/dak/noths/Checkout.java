package com.dak.noths;

import com.dak.noths.rules.BulkRule;
import com.dak.noths.rules.DiscountRule;
import com.dak.noths.rules.PromoRules;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.apache.log4j.Logger;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;

public class Checkout {

    private static final Logger log = Logger.getLogger(Checkout.class);
    private static final String CATALOGUE_FILENAME = "/inventory.txt";

    private Map<String, CatalogueItem> catalogue = new HashMap<>();
    private List<CatalogueItem> itemList = new ArrayList<>();
    private List<PromoRules> discountRuleList = new ArrayList<>();
    private List<PromoRules> bulkRuleList = new ArrayList<>();


    public Checkout(String promosFile) {
        this(CATALOGUE_FILENAME, promosFile);
    }

    public Checkout(String catalogueFilename, String promosFile) {
        loadCatalogue(catalogueFilename);
        loadPromotions(promosFile);
        log.info("Checkout initialised");
    }

    private void loadCatalogue(String catalogueFilename) {
        if(!catalogueFilename.startsWith("/")) {
            catalogueFilename = "/" + catalogueFilename;
        }

        URL url = this.getClass().getResource(catalogueFilename);
        log.info("Loading catalogue from " + url);
        File file = new File(url.getFile());
        try (BufferedReader br = new BufferedReader(new FileReader(url.getFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    log.debug("Loading line: " + line);
                    String[] sa = line.split(",");
                    BigDecimal price = new BigDecimal(sa[2]);
                    CatalogueItem item = new CatalogueItem(sa[0], sa[1], price);
                    catalogue.put(item.getId(), item);
                }
            }
        } catch (Exception e) {
            log.error("Could not open catalogue file: " + CATALOGUE_FILENAME, e);
        }
    }

    private void loadPromotions(String promosFilename) {
        if (promosFilename.isEmpty()) {
            log.info("No promotions specified");
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        if(!promosFilename.startsWith("/")) {
            promosFilename = "/" + promosFilename;
        }
        try {
            JsonNode rootNode = mapper.readTree(this.getClass().getResource(promosFilename));
            JsonNode discountRuleList = rootNode.get("total");
            ObjectReader reader = mapper.readerFor(new TypeReference<List<DiscountRule>>() {});
            this.discountRuleList = reader.readValue(discountRuleList);
            log.debug("Loaded " + this.discountRuleList.size() + " discount rules");

            JsonNode bulkRuleList = rootNode.get("bulk");
            reader = mapper.readerFor(new TypeReference<List<BulkRule>>() {});
            this.bulkRuleList = reader.readValue(bulkRuleList);
            log.debug("Loaded " + this.bulkRuleList.size() + " bulk rules");

        } catch (Exception e) {
            log.info("Exception loading promotions from file: " + promosFilename, e);
        }
    }

    public boolean scan(String itemId) {
        CatalogueItem item = catalogue.get(itemId);
        if (item != null) {
            itemList.add(item);
            log.info("Scanned item: " + item);
            return true;
        }
        log.warn("Unknown item ID=" + itemId);
        return false;
    }

    public String total() {
        Basket basket = new Basket(this.itemList);

        // Apply Bulk rules before total discount rules
        basket.apply(bulkRuleList);
        basket.apply(discountRuleList);

        return "£" + basket.getBasketPrice().toPlainString();
    }

    public void reset() {
        itemList.clear();
        log.info("Checkout reset");
    }


    public static void main(String[] args) {
        String filename = args.length > 0 ? args[0] : "";
        Checkout cb = new Checkout(filename);
        cb.scan("001");
        cb.scan("002");
        cb.scan("003");
        System.out.println(cb.total());

        cb.reset();
        cb.scan("001");
        cb.scan("003");
        cb.scan("001");
        System.out.println(cb.total());

        cb.reset();
        cb.scan("001");
        cb.scan("002");
        cb.scan("001");
        cb.scan("003");
        System.out.println(cb.total());
    }
}
