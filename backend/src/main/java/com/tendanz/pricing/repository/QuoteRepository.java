package com.tendanz.pricing.repository;

import com.tendanz.pricing.entity.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

/**
 * Repository for Quote entity.
 * Provides database operations for quotes.
 *
 * TODO: Implement custom query methods as needed:
 * - findByClientName(String clientName)
 * - findByProductId(Long productId)
 * - Query to find quotes with final_price above a threshold
 */
@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {

    /**
     * TODO: Find all quotes by client name.
     *
     * @param clientName the client name to search for
     * @return list of quotes matching the client name
     */
    List<Quote> findByClientName(String clientName);

    /**
     * TODO: Find all quotes for a specific product.
     *
     * @param productId the product ID
     * @return list of quotes for the product
     */
    List<Quote> findByProductId(Long productId);

    /**
     * TODO: Find quotes with final price above a threshold.
     * Consider using @Query annotation with JPQL or SQL.
     *
     * @param minPrice the minimum price threshold
     * @return list of quotes with final_price greater than or equal to minPrice
     */
    @Query("SELECT q FROM Quote q WHERE q.finalPrice >= :minPrice ORDER BY q.finalPrice DESC")
    List<Quote> findQuotesAboveThreshold(@Param("minPrice") BigDecimal minPrice);
}
