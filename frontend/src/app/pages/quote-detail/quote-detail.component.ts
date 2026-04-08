import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { QuoteService } from '../../services/quote.service';
import { QuoteResponse } from '../../models/quote.model';

/**
 * Component for displaying the details of a single quote
 *
 * TODO: Candidate must implement the following:
 * 1. Get quote ID from route parameters
 *
 * 2. Load quote details from QuoteService using the ID
 *
 * 3. Display complete quote information:
 *    - Customer details (name, email, phone)
 *    - Insurance details (product, zone, amount, dates)
 *    - Age and pricing information
 *    - Applied pricing rules
 *    - Final price calculation breakdown
 *
 * 4. Show loading state while fetching data
 *
 * 5. Show error message if quote cannot be loaded
 *
 * 6. Provide back button to return to quote list
 */
@Component({
  selector: 'app-quote-detail',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './quote-detail.component.html',
  styleUrl: './quote-detail.component.css'
})
export class QuoteDetailComponent implements OnInit {
  quote: QuoteResponse | null = null;
  loading = false;
  errorMessage: string | null = null;

  constructor(
    private quoteService: QuoteService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    // TODO: Get quote ID from route parameters
    // TODO: Load quote from QuoteService
    // TODO: Handle loading and error states
    console.log('Quote detail component initialized (TODO: implement)');
  }

  /**
   * Calculate the breakdown of the pricing
   *
   * TODO: This is helper method to display pricing details
   * Calculate:
   * - Base price (basePrice * duration)
   * - Age modifier impact
   * - Zone modifier impact
   * - Final price
   */
  getBaseTotal(): number {
    if (!this.quote) return 0;
    // TODO: Calculate base price * duration
    return 0;
  }

  /**
   * Get the age modifier impact on price
   */
  getAgeModifierImpact(): number {
    if (!this.quote) return 0;
    // TODO: Calculate impact of age modifier
    return 0;
  }

  /**
   * Get the zone modifier impact on price
   */
  getZoneModifierImpact(): number {
    if (!this.quote) return 0;
    // TODO: Calculate impact of zone modifier
    return 0;
  }
}
