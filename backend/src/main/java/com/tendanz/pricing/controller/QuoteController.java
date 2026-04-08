package com.tendanz.pricing.controller;

import com.tendanz.pricing.dto.QuoteRequest;
import com.tendanz.pricing.dto.QuoteResponse;
import com.tendanz.pricing.service.PricingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing quotes.
 * Handles all quote-related API endpoints.
 *
 * TODO: Implement the following endpoints:
 * - POST /api/quotes - Create a new quote
 * - GET /api/quotes/{id} - Get a quote by ID
 * - GET /api/quotes - Get all quotes with optional filters
 * - Use proper HTTP status codes
 * - Implement error handling
 */
@RestController
@RequestMapping("/api/quotes")
@RequiredArgsConstructor
@Slf4j
public class QuoteController {

    private final PricingService pricingService;

    /**
     * Create a new quote.
     *
     * TODO: Implement POST endpoint
     * - Accept QuoteRequest with @Valid annotation
     * - Call PricingService.calculateQuote()
     * - Return 201 CREATED with QuoteResponse
     * - Handle validation errors with 400 BAD_REQUEST
     *
     * @param request the quote request
     * @return the created quote response
     */
    @PostMapping
    public ResponseEntity<QuoteResponse> createQuote(@Valid @RequestBody QuoteRequest request) {
        log.info("Creating new quote for product ID: {}", request.getProductId());
        try {
            QuoteResponse response = pricingService.calculateQuote(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.error("Error creating quote: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Get a quote by ID.
     *
     * TODO: Implement GET endpoint with path variable
     * - Accept quote ID from path
     * - Call PricingService.getQuote(id)
     * - Return 200 OK with QuoteResponse
     * - Return 404 NOT_FOUND if quote doesn't exist
     *
     * @param id the quote ID
     * @return the quote response
     */
    @GetMapping("/{id}")
    public ResponseEntity<QuoteResponse> getQuote(@PathVariable Long id) {
        log.info("Fetching quote with ID: {}", id);
        try {
            QuoteResponse response = pricingService.getQuote(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Quote not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get all quotes with optional filters.
     *
     * TODO: Implement GET endpoint without path variable
     * - Support optional query parameters: productId, minPrice
     * - Implement filtering logic
     * - Return 200 OK with list of QuoteResponse
     *
     * Query examples:
     * - GET /api/quotes
     * - GET /api/quotes?productId=1
     * - GET /api/quotes?minPrice=500
     * - GET /api/quotes?productId=1&minPrice=500
     *
     * @param productId optional product ID filter
     * @param minPrice optional minimum price filter
     * @return list of quotes matching filters
     */
    @GetMapping
    public ResponseEntity<List<QuoteResponse>> getAllQuotes(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Double minPrice) {
        log.info("Fetching all quotes with filters - productId: {}, minPrice: {}", productId, minPrice);
        // TODO: Implement filtering and retrieval logic
        // TODO: Use QuoteRepository methods to query data
        // TODO: Convert entities to response DTOs
        // TODO: Return appropriate HTTP status codes
        return ResponseEntity.ok(List.of());
    }
}
