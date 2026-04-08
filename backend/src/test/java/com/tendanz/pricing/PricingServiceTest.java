package com.tendanz.pricing;

import com.tendanz.pricing.dto.QuoteRequest;
import com.tendanz.pricing.dto.QuoteResponse;
import com.tendanz.pricing.entity.PricingRule;
import com.tendanz.pricing.entity.Product;
import com.tendanz.pricing.entity.Quote;
import com.tendanz.pricing.entity.Zone;
import com.tendanz.pricing.repository.PricingRuleRepository;
import com.tendanz.pricing.repository.ProductRepository;
import com.tendanz.pricing.repository.QuoteRepository;
import com.tendanz.pricing.repository.ZoneRepository;
import com.tendanz.pricing.service.PricingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for PricingService.
 *
 * TODO: Add more comprehensive tests covering:
 * - Different age categories
 * - Different zones and their risk coefficients
 * - Edge cases (minimum and maximum ages)
 * - Validation of pricing calculations
 * - Error handling scenarios
 */
@DataJpaTest
@Import({PricingService.class, ObjectMapper.class})
class PricingServiceTest {

    @Autowired
    private PricingService pricingService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ZoneRepository zoneRepository;

    @Autowired
    private PricingRuleRepository pricingRuleRepository;

    @Autowired
    private QuoteRepository quoteRepository;

    private Product product;
    private Zone zone;
    private PricingRule pricingRule;

    @BeforeEach
    void setUp() {
        // Create test data
        product = Product.builder()
                .name("Test Auto Insurance")
                .description("Test Description")
                .createdAt(LocalDateTime.now())
                .build();
        productRepository.save(product);

        zone = Zone.builder()
                .code("TEST-ZONE")
                .name("Test Zone")
                .riskCoefficient(BigDecimal.valueOf(1.0))
                .build();
        zoneRepository.save(zone);

        pricingRule = PricingRule.builder()
                .product(product)
                .baseRate(BigDecimal.valueOf(500.00))
                .ageFactorYoung(BigDecimal.valueOf(1.3))
                .ageFactorAdult(BigDecimal.valueOf(1.0))
                .ageFactorSenior(BigDecimal.valueOf(1.2))
                .ageFactorElderly(BigDecimal.valueOf(1.5))
                .createdAt(LocalDateTime.now())
                .build();
        pricingRuleRepository.save(pricingRule);
    }

    /**
     * Test quote calculation for an adult client.
     *
     * TODO: Implement and expand this test:
     * - Verify the calculated final price
     * - Check that base price is correctly set
     * - Validate that age factor is applied correctly
     * - Ensure zone risk coefficient is applied
     */
    @Test
    void testCalculateQuoteForAdult() {
        QuoteRequest request = QuoteRequest.builder()
                .productId(product.getId())
                .zoneCode(zone.getCode())
                .clientName("John Doe")
                .clientAge(30)
                .build();

        QuoteResponse response = pricingService.calculateQuote(request);

        assertNotNull(response);
        assertNotNull(response.getQuoteId());
        assertEquals("Test Auto Insurance", response.getProductName());
        assertEquals("Test Zone", response.getZoneName());
        assertEquals("John Doe", response.getClientName());
        assertEquals(30, response.getClientAge());
        assertEquals(BigDecimal.valueOf(500.00), response.getBasePrice());
        // Adult factor is 1.0, zone coefficient is 1.0, so final should be 500.00
        assertEquals(BigDecimal.valueOf(500.00), response.getFinalPrice());
        assertNotNull(response.getAppliedRules());
        assertFalse(response.getAppliedRules().isEmpty());
    }

    /**
     * Test quote calculation for a young client with higher age factor.
     *
     * TODO: Implement test for:
     * - YOUNG age category (18-24)
     * - Age factor of 1.3 (30% increase)
     * - Verify final price calculation: 500 * 1.3 * 1.0 = 650
     */
    @Test
    void testCalculateQuoteForYoungClient() {
        QuoteRequest request = QuoteRequest.builder()
                .productId(product.getId())
                .zoneCode(zone.getCode())
                .clientName("Jane Smith")
                .clientAge(22)
                .build();

        QuoteResponse response = pricingService.calculateQuote(request);

        assertNotNull(response);
        assertEquals(22, response.getClientAge());
        assertEquals(BigDecimal.valueOf(500.00), response.getBasePrice());
        // Young factor is 1.3, zone coefficient is 1.0, so final should be 650.00
        assertEquals(BigDecimal.valueOf(650.00), response.getFinalPrice());
        assertNotNull(response.getAppliedRules());
    }

    /**
     * TODO: Add test for zone risk coefficient calculation
     * - Test with different zone risk coefficients
     * - Verify that zone coefficient is properly applied to final price
     */

    /**
     * TODO: Add test for invalid product ID
     * - Should throw IllegalArgumentException
     * - Should not create a quote
     */

    /**
     * TODO: Add test for invalid zone code
     * - Should throw IllegalArgumentException
     * - Should not create a quote
     */

    /**
     * TODO: Add test for edge cases
     * - Minimum age (18)
     * - Maximum age (99)
     * - Boundary between age categories
     */

    /**
     * TODO: Add test for quote retrieval
     * - Create a quote
     * - Retrieve it by ID
     * - Verify all fields are correctly returned
     */

    /**
     * TODO: Add test for applied rules storage
     * - Verify that applied rules are correctly stored as JSON
     * - Verify that rules can be deserialized properly
     */
}
