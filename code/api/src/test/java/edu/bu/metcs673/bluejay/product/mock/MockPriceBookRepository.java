// AI-ASSISTED: NO
// Tool: NO
// Prompt Summary: "N/A"
// AI Contribution: 0%
// Confidence: High

package edu.bu.metcs673.bluejay.product.mock;

import edu.bu.metcs673.bluejay.product.domain.PriceBook;
import edu.bu.metcs673.bluejay.product.repository.PriceBookRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MockPriceBookRepository implements PriceBookRepository {
    private final List<PriceBook> _priceBooks = new ArrayList<>();

    public  MockPriceBookRepository() {
        PriceBook pb1 = new  PriceBook();
        pb1.setProductId(UUID.fromString("12345678-1234-1234-1234-123456789abc"));
        pb1.setCost(1);
        pb1.setMargin(1.5f);
        pb1.setPrice(2.5);
        pb1.setEffectiveAt(createDate("2026-08-01 23:59:59"));

        PriceBook pb2 = new  PriceBook();
        pb2.setProductId(UUID.fromString("12345678-1234-1234-1234-123456789abc"));
        pb2.setCost(1.5);
        pb2.setMargin(1.5f);
        pb2.setPrice(3.75);
        pb2.setEffectiveAt(createDate("2026-09-30 06:00:59"));

        _priceBooks.add(pb1);
        _priceBooks.add(pb2);
    }

    @Override
    public List<PriceBook> getProductPricesBy(UUID productId) {
        return _priceBooks.stream()
                .filter(p -> p.getProductId().equals(productId))
                .toList();
    }

    public void reset() {
        _priceBooks.clear();
    }

    private Date createDate(String dateTime) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return simpleDateFormat.parse(dateTime);
        } catch (ParseException ex) {
            return new Date();
        }
    }
}
