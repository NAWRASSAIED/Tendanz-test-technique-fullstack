package com.tendanz.pricing.service;

import com.tendanz.pricing.dto.QuoteRequest;
import com.tendanz.pricing.dto.QuoteResponse;
import com.tendanz.pricing.entity.PricingRule;
import com.tendanz.pricing.entity.Product;
import com.tendanz.pricing.entity.Quote;
import com.tendanz.pricing.entity.Zone;
import com.tendanz.pricing.enums.AgeCategory;
import com.tendanz.pricing.repository.PricingRuleRepository;
import com.tendanz.pricing.repository.ProductRepository;
import com.tendanz.pricing.repository.QuoteRepository;
import com.tendanz.pricing.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Service for handling pricing and quote calculations.
 * Manages the business logic for pricing rules and quote generation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PricingService {

    private final ProductRepository productRepository;
    private final ZoneRepository zoneRepository;
    private final PricingRuleRepository pricingRuleRepository;
    private final QuoteRepository quoteRepository;
    private final ObjectMapper objectMapper;

    /**
     * Calculate a quote based on the provided request.
     *
     * TODO: Implement the calculateQuote method with the following logic:
     * 1. Validate and load the Product and Zone from repositories
     * 2. Load the PricingRule for the product
     * 3. Determine the age category based on clientAge
     * 4. Calculate base price using the rule's base rate
     * 5. Apply age factor multiplier based on age category
     * 6. Apply zone risk coefficient
     * 7. Create a Quote entity with calculated prices
     * 8. Store applied rules as a JSON string
     * 9. Save the quote and return QuoteResponse
     *
     * Age factor mapping:
     * - YOUNG (18-24): ageFactorYoung
     * - ADULT (25-45): ageFactorAdult
     * - SENIOR (46-65): ageFactorSenior
     * - ELDERLY (66-99): ageFactorElderly
     *
     * Final Price Calculation:
     * finalPrice = baseRate × ageFactor × zoneRiskCoefficient
     *
     * @param request the quote request containing product, zone, and client info
     * @return the calculated quote response
     * @throws IllegalArgumentException if product or zone not found
     */
    @Transactional
    public QuoteResponse calculateQuote(QuoteRequest request) {
        log.info("Calculating quote for product ID: {}, zone: {}, age: {}",
                request.getProductId(), request.getZoneCode(), request.getClientAge());

        // TODO: Load Product
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + request.getProductId()));

        // TODO: Load Zone
        Zone zone = zoneRepository.findByCode(request.getZoneCode())
                .orElseThrow(() -> new IllegalArgumentException("Zone not found with code: " + request.getZoneCode()));

        // TODO: Load PricingRule
        PricingRule pricingRule = pricingRuleRepository.findByProductId(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Pricing rule not found for product ID: " + request.getProductId()));

        // TODO: Determine age category and get appropriate age factor
        AgeCategory ageCategory = AgeCategory.fromAge(request.getClientAge());
        BigDecimal ageFactor = getAgeFactor(pricingRule, ageCategory);

        // TODO: Calculate base price
        BigDecimal basePrice = pricingRule.getBaseRate();

        // TODO: Apply age factor and zone coefficient
        BigDecimal finalPrice = basePrice
                .multiply(ageFactor)
                .multiply(zone.getRiskCoefficient())
                .setScale(2, RoundingMode.HALF_UP);

        // TODO: Create applied rules list
        List<String> appliedRules = new ArrayList<>();
        appliedRules.add("Base Rate: " + basePrice);
        appliedRules.add("Age Factor (" + ageCategory + "): " + ageFactor);
        appliedRules.add("Zone Risk (" + zone.getName() + "): " + zone.getRiskCoefficient());
        appliedRules.add("Final Price: " + finalPrice);

        // TODO: Convert rules to JSON string
        String rulesJson = convertRulesToJson(appliedRules);

        // TODO: Create and save Quote
        Quote quote = Quote.builder()
                .product(product)
                .zone(zone)
                .clientName(request.getClientName())
                .clientAge(request.getClientAge())
                .basePrice(basePrice)
                .finalPrice(finalPrice)
                .appliedRules(rulesJson)
                .build();

        Quote savedQuote = quoteRepository.save(quote);
        log.info("Quote saved successfully with ID: {}", savedQuote.getId());

        // TODO: Convert to response
        return mapToResponse(savedQuote, appliedRules);
    }

    /**
     * Get the age factor for a specific age category.
     *
     * TODO: Implement the mapping logic
     *
     * @param pricingRule the pricing rule
     * @param ageCategory the age category
     * @return the appropriate age factor
     */
    private BigDecimal getAgeFactor(PricingRule pricingRule, AgeCategory ageCategory) {
        return switch (ageCategory) {
            case YOUNG -> pricingRule.getAgeFactorYoung();
            case ADULT -> pricingRule.getAgeFactorAdult();
            case SENIOR -> pricingRule.getAgeFactorSenior();
            case ELDERLY -> pricingRule.getAgeFactorElderly();
        };
    }

    /**
     * Convert the list of applied rules to a JSON string.
     *
     * TODO: Implement JSON serialization
     *
     * @param rules the list of rule descriptions
     * @return the JSON string representation
     */
    private String convertRulesToJson(List<String> rules) {
        try {
            return objectMapper.writeValueAsString(rules);
        } catch (Exception e) {
            log.error("Error converting rules to JSON", e);
            return "[]";
        }
    }

    /**
     * Convert a Quote entity to a QuoteResponse DTO.
     *
     * TODO: Implement the mapping
     *
     * @param quote the quote entity
     * @param appliedRules the list of applied rules
     * @return the quote response
     */
    private QuoteResponse mapToResponse(Quote quote, List<String> appliedRules) {
        return QuoteResponse.builder()
                .quoteId(quote.getId())
                .productName(quote.getProduct().getName())
                .zoneName(quote.getZone().getName())
                .clientName(quote.getClientName())
                .clientAge(quote.getClientAge())
                .basePrice(quote.getBasePrice())
                .finalPrice(quote.getFinalPrice())
                .appliedRules(appliedRules)
                .createdAt(quote.getCreatedAt())
                .build();
    }

    /**
     * Get a quote by ID.
     *
     * @param id the quote ID
     * @return the quote response
     */
    public QuoteResponse getQuote(Long id) {
        Quote quote = quoteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quote not found with ID: " + id));

        List<String> appliedRules = deserializeRules(quote.getAppliedRules());
        return mapToResponse(quote, appliedRules);
    }

    /**
     * Deserialize the rules JSON string back to a list.
     *
     * @param rulesJson the JSON string
     * @return the list of rules
     */
    private List<String> deserializeRules(String rulesJson) {
        try {
            return objectMapper.readValue(rulesJson,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
        } catch (Exception e) {
            log.error("Error deserializing rules from JSON", e);
            return new ArrayList<>();
        }
    }
}
