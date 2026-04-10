package com.tendanz.pricing.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
     * 1. Validate and load the Product from productRepository (throw IllegalArgumentException if not found)
     * 2. Validate and load the Zone from zoneRepository by code (throw IllegalArgumentException if not found)
     * 3. Load the PricingRule for the product from pricingRuleRepository
     * 4. Determine the age category using AgeCategory.fromAge(clientAge)
     * 5. Get the appropriate age factor using getAgeFactor() helper below
     * 6. Calculate: finalPrice = baseRate × ageFactor × zoneRiskCoefficient (rounded to 2 decimals)
     * 7. Build an appliedRules list describing each step of the calculation
     * 8. Create and save a Quote entity with all calculated values
     * 9. Return a QuoteResponse using the mapToResponse() helper below
     *
     * @param request the quote request containing productId, zoneCode, clientName, clientAge
     * @return the calculated quote response
     * @throws IllegalArgumentException if product, zone, or pricing rule not found
     */
    @Transactional
    public QuoteResponse calculateQuote(QuoteRequest request) {
        // TODO: Implement this method
        Product product=productRepository.findById(request.getProductId()).orElseThrow(() ->new IllegalArgumentException("Product not found"));
        Zone zone=zoneRepository.findByCode(request.getZoneCode()).orElseThrow(()->new IllegalArgumentException("Zone not found"));
        PricingRule pricingRule= pricingRuleRepository.findByProductId(product.getId()).orElseThrow(() -> new IllegalArgumentException("pricing rule not found"));
        AgeCategory ageCategory=AgeCategory.fromAge(request.getClientAge());
        BigDecimal ageFactor=getAgeFactor(pricingRule,ageCategory);
        BigDecimal finalPrice = pricingRule.getBaseRate().multiply(ageFactor).multiply(zone.getRiskCoefficient()).setScale(2, RoundingMode.HALF_UP);

        List<String> appliedRules = new ArrayList<>();
        appliedRules.add("Base Rate: " + pricingRule.getBaseRate());
        appliedRules.add("Age category: " + ageCategory + " (factor: " + ageFactor + ")");
        appliedRules.add("Zone: " + zone.getName() + " (coefficient: " + zone.getRiskCoefficient() + ")");
        appliedRules.add("Final price = baseRate * ageFactor * zoneRiskCoefficient = " + finalPrice);
        String rulesJson = convertRulesToJson(appliedRules);
        Quote quote = Quote.builder()
                           .product(product)
                           .zone(zone)
                           .clientName(request.getClientName())
                           .clientAge(request.getClientAge())
                           .basePrice(pricingRule.getBaseRate())
                           .finalPrice(finalPrice)
                           .appliedRules(rulesJson)
                           .build();
        quoteRepository.save(quote);
        return mapToResponse(quote, appliedRules);
    }

    /**
     * Get the age factor for a specific age category from a pricing rule.
     * This helper is provided — use it in your calculateQuote implementation.
     *
     * @param pricingRule the pricing rule containing age factors
     * @param ageCategory the age category (YOUNG, ADULT, SENIOR, ELDERLY)
     * @return the appropriate age factor as BigDecimal
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
     * Convert a list of applied rules to a JSON string for storage.
     * This helper is provided — use it in your calculateQuote implementation.
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
     * This helper is provided — use it in your calculateQuote implementation.
     *
     * @param quote the quote entity
     * @param appliedRules the list of applied rules
     * @return the quote response DTO
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
                .createdAt(quote.getCreatedAt() != null
                        ? quote.getCreatedAt().toString()
                        : null)
                .build();
    }

    /**
     * Get a quote by ID.
     * This method is provided as a reference for how to retrieve and return quotes.
     *
     * @param id the quote ID
     * @return the quote response
     * @throws IllegalArgumentException if quote not found
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


    public Page<QuoteResponse> getAllQuotes(
            Long productId,
            BigDecimal minPrice,
            Pageable pageable
    ) {
        Page<Quote> quotePage;
        if (productId != null && minPrice != null) {
            quotePage = quoteRepository.findByProductIdAndFinalPriceGreaterThanEqual(
                    productId,
                    minPrice,
                    pageable
            );
        } else if (productId != null) {
            quotePage = quoteRepository.findByProductId(productId, pageable);

        } else if (minPrice != null) {
            quotePage = quoteRepository.findByFinalPriceAboveThreshold(minPrice, pageable);

        } else {
            quotePage = quoteRepository.findAll(pageable);
        }
        return quotePage.map(q ->
                mapToResponse(q, deserializeRules(q.getAppliedRules()))
        );
    }
    public byte[] exportQuotePdf(Long id) {
        Quote quote = quoteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quote not found"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            Document document = new Document(PageSize.A4, 40, 40, 60, 60);
            PdfWriter writer = PdfWriter.getInstance(document, out);

            writer.setPageEvent(new PdfPageEventHelper() {
                @Override
                public void onEndPage(PdfWriter w, Document doc) {
                    try {
                        PdfContentByte cb = w.getDirectContent();
                        cb.setColorFill(new BaseColor(30, 80, 160));
                        cb.rectangle(doc.left(), doc.top() + 10, doc.right() - doc.left(), 6);
                        cb.fill();
                        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.GRAY);
                        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                                new Phrase("Quote #" + id + "  •  " +
                                        LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                                        "  •  Page " + w.getPageNumber(), footerFont),
                                (doc.right() + doc.left()) / 2, doc.bottom() - 15, 0);
                        cb.setColorFill(new BaseColor(30, 80, 160));
                        cb.rectangle(doc.left(), doc.bottom() - 20, doc.right() - doc.left(), 2);
                        cb.fill();
                    } catch (Exception ignored) {}
                }
            });

            document.open();

            // ── Fonts ────────────────────────────────────────────────────
            Font titleFont   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, new BaseColor(30, 80, 160));
            Font subtitleFont= FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.GRAY);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.WHITE);
            Font labelFont   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.DARK_GRAY);
            Font valueFont   = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
            Font totalFont   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, new BaseColor(30, 80, 160));
            Font thankFont   = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.GRAY);

            // ── Hero Header (titre seul, sans badge) ─────────────────────
            PdfPTable header = new PdfPTable(1);
            header.setWidthPercentage(100);
            header.setSpacingAfter(24);

            PdfPCell titleCell = new PdfPCell();
            titleCell.setBorder(Rectangle.NO_BORDER);
            titleCell.addElement(new Paragraph("DEVIS / QUOTE", titleFont));
            titleCell.addElement(new Paragraph("Référence : #" + id, subtitleFont));
            header.addCell(titleCell);
            document.add(header);

            // ── CLIENT INFORMATION ───────────────────────────────────────
            PdfPTable secClient = new PdfPTable(1);
            secClient.setWidthPercentage(100); secClient.setSpacingBefore(10); secClient.setSpacingAfter(6);
            PdfPCell secClientCell = new PdfPCell(new Phrase("CLIENT INFORMATION", sectionFont));
            secClientCell.setBackgroundColor(new BaseColor(30, 80, 160)); secClientCell.setPadding(6); secClientCell.setBorder(Rectangle.NO_BORDER);
            secClient.addCell(secClientCell);
            document.add(secClient);

            PdfPTable clientTable = new PdfPTable(2);
            clientTable.setWidthPercentage(100); clientTable.setWidths(new float[]{1, 2}); clientTable.setSpacingAfter(6);
            String[][] clientRows = {{"Client Name", quote.getClientName()}, {"Client Age", quote.getClientAge() + " ans"}};
            for (int i = 0; i < clientRows.length; i++) {
                BaseColor bg = i % 2 == 0 ? BaseColor.WHITE : new BaseColor(245, 247, 252);
                PdfPCell lc = new PdfPCell(new Phrase(clientRows[i][0], labelFont));
                lc.setBackgroundColor(bg); lc.setBorderColor(new BaseColor(220, 220, 220)); lc.setPadding(7);
                PdfPCell vc = new PdfPCell(new Phrase(clientRows[i][1], valueFont));
                vc.setBackgroundColor(bg); vc.setBorderColor(new BaseColor(220, 220, 220)); vc.setPadding(7);
                clientTable.addCell(lc); clientTable.addCell(vc);
            }
            document.add(clientTable);

            // ── PRODUCT DETAILS ──────────────────────────────────────────
            PdfPTable secProduct = new PdfPTable(1);
            secProduct.setWidthPercentage(100); secProduct.setSpacingBefore(10); secProduct.setSpacingAfter(6);
            PdfPCell secProductCell = new PdfPCell(new Phrase("PRODUCT DETAILS", sectionFont));
            secProductCell.setBackgroundColor(new BaseColor(30, 80, 160)); secProductCell.setPadding(6); secProductCell.setBorder(Rectangle.NO_BORDER);
            secProduct.addCell(secProductCell);
            document.add(secProduct);

            PdfPTable productTable = new PdfPTable(2);
            productTable.setWidthPercentage(100); productTable.setWidths(new float[]{1, 2}); productTable.setSpacingAfter(6);
            String[][] productRows = {{"Product", quote.getProduct().getName()}, {"Zone", quote.getZone().getName()}};
            for (int i = 0; i < productRows.length; i++) {
                BaseColor bg = i % 2 == 0 ? BaseColor.WHITE : new BaseColor(245, 247, 252);
                PdfPCell lc = new PdfPCell(new Phrase(productRows[i][0], labelFont));
                lc.setBackgroundColor(bg); lc.setBorderColor(new BaseColor(220, 220, 220)); lc.setPadding(7);
                PdfPCell vc = new PdfPCell(new Phrase(productRows[i][1], valueFont));
                vc.setBackgroundColor(bg); vc.setBorderColor(new BaseColor(220, 220, 220)); vc.setPadding(7);
                productTable.addCell(lc); productTable.addCell(vc);
            }
            document.add(productTable);

            // ── PRICING DETAILS ──────────────────────────────────────────
            PdfPTable secPricing = new PdfPTable(1);
            secPricing.setWidthPercentage(100); secPricing.setSpacingBefore(10); secPricing.setSpacingAfter(6);
            PdfPCell secPricingCell = new PdfPCell(new Phrase("PRICING DETAILS", sectionFont));
            secPricingCell.setBackgroundColor(new BaseColor(30, 80, 160)); secPricingCell.setPadding(6); secPricingCell.setBorder(Rectangle.NO_BORDER);
            secPricing.addCell(secPricingCell);
            document.add(secPricing);

            PdfPTable pricingTable = new PdfPTable(2);
            pricingTable.setWidthPercentage(100); pricingTable.setWidths(new float[]{1, 1}); pricingTable.setSpacingAfter(16);

            PdfPCell bpLabel = new PdfPCell(new Phrase("Base Price", labelFont));
            bpLabel.setBackgroundColor(BaseColor.WHITE); bpLabel.setPadding(9); bpLabel.setBorderColor(new BaseColor(200, 200, 200));
            PdfPCell bpValue = new PdfPCell(new Phrase(quote.getBasePrice() + " TND", valueFont));
            bpValue.setBackgroundColor(BaseColor.WHITE); bpValue.setPadding(9); bpValue.setHorizontalAlignment(Element.ALIGN_RIGHT); bpValue.setBorderColor(new BaseColor(200, 200, 200));
            pricingTable.addCell(bpLabel); pricingTable.addCell(bpValue);

            PdfPCell fpLabel = new PdfPCell(new Phrase("Final Price", totalFont));
            fpLabel.setBackgroundColor(new BaseColor(235, 242, 255)); fpLabel.setPadding(9); fpLabel.setBorderColor(new BaseColor(200, 200, 200));
            PdfPCell fpValue = new PdfPCell(new Phrase(quote.getFinalPrice() + " TND", totalFont));
            fpValue.setBackgroundColor(new BaseColor(235, 242, 255)); fpValue.setPadding(9); fpValue.setHorizontalAlignment(Element.ALIGN_RIGHT); fpValue.setBorderColor(new BaseColor(200, 200, 200));
            pricingTable.addCell(fpLabel); pricingTable.addCell(fpValue);

            document.add(pricingTable);

            // ── DATE ─────────────────────────────────────────────────────
            PdfPTable secDate = new PdfPTable(1);
            secDate.setWidthPercentage(100); secDate.setSpacingBefore(10); secDate.setSpacingAfter(6);
            PdfPCell secDateCell = new PdfPCell(new Phrase("DATE", sectionFont));
            secDateCell.setBackgroundColor(new BaseColor(30, 80, 160)); secDateCell.setPadding(6); secDateCell.setBorder(Rectangle.NO_BORDER);
            secDate.addCell(secDateCell);
            document.add(secDate);

            PdfPTable dateTable = new PdfPTable(2);
            dateTable.setWidthPercentage(100); dateTable.setWidths(new float[]{1, 2}); dateTable.setSpacingAfter(20);
            PdfPCell dlc = new PdfPCell(new Phrase("Created At", labelFont));
            dlc.setBackgroundColor(BaseColor.WHITE); dlc.setBorderColor(new BaseColor(220, 220, 220)); dlc.setPadding(7);
            PdfPCell dvc = new PdfPCell(new Phrase(quote.getCreatedAt().toString(), valueFont));
            dvc.setBackgroundColor(BaseColor.WHITE); dvc.setBorderColor(new BaseColor(220, 220, 220)); dvc.setPadding(7);
            dateTable.addCell(dlc); dateTable.addCell(dvc);
            document.add(dateTable);

            // ── Thank you ─────────────────────────────────────────────────
            Paragraph thanks = new Paragraph("Thank you for your trust — we look forward to working with you!", thankFont);
            thanks.setAlignment(Element.ALIGN_CENTER);
            document.add(thanks);

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }

        return out.toByteArray();
    }}

