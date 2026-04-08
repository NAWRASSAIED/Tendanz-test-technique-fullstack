import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { QuoteService } from '../../services/quote.service';
import { ProductService } from '../../services/product.service';
import { QuoteResponse } from '../../models/quote.model';
import { Product } from '../../models/product.model';

/**
 * Component for displaying a list of all quotes
 *
 * TODO: Candidate must implement the following:
 * 1. Load all quotes on component initialization using QuoteService
 *
 * 2. Implement filtering:
 *    - Filter by product (dropdown with all products)
 *    - Filter by minimum price
 *    - Apply filters by calling QuoteService.getQuotes(filters)
 *
 * 3. Implement sorting:
 *    - Sort by creation date (ascending/descending)
 *    - Sort by final price (ascending/descending)
 *    - Update displayed quotes when sort changes
 *
 * 4. Display quotes in a table with columns:
 *    - ID
 *    - Customer Name
 *    - Product
 *    - Zone
 *    - Final Price
 *    - Created Date
 *
 * 5. Make table rows clickable to navigate to quote detail page
 *
 * 6. Show loading state while data is being fetched
 *
 * 7. Show error message if API call fails
 */
@Component({
  selector: 'app-quote-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './quote-list.component.html',
  styleUrl: './quote-list.component.css'
})
export class QuoteListComponent implements OnInit {
  quotes: QuoteResponse[] = [];
  filteredQuotes: QuoteResponse[] = [];
  products: Product[] = [];
  loading = false;
  errorMessage: string | null = null;

  // Filter state
  selectedProductId: number | null = null;
  minPrice: number | null = null;

  // Sort state
  sortField: 'date' | 'price' = 'date';
  sortDirection: 'asc' | 'desc' = 'desc';

  constructor(
    private quoteService: QuoteService,
    private productService: ProductService
  ) {}

  ngOnInit(): void {
    // TODO: Load products for filter dropdown
    // TODO: Load quotes from QuoteService
    // TODO: Handle loading and error states
  }

  /**
   * Apply filters to the quotes
   *
   * TODO: Implement filtering
   * - Get filter values from component properties
   * - Call quoteService.getQuotes(filters)
   * - Update filteredQuotes with results
   * - Handle errors
   */
  applyFilters(): void {
    // TODO: Implement filtering logic
    console.log('Filters applied (TODO: implement)');
  }

  /**
   * Reset all filters
   *
   * TODO: Implement reset
   * - Clear filter values
   * - Reload all quotes
   */
  resetFilters(): void {
    // TODO: Reset filters and reload
  }

  /**
   * Change sort field
   */
  changeSortField(field: 'date' | 'price'): void {
    // TODO: Implement sorting
    // If clicking the same field, toggle direction
    // Otherwise, set new field and reset to ascending
    console.log('Sort field changed to:', field);
  }

  /**
   * Sort quotes in memory
   *
   * TODO: Implement sorting
   * - Sort this.filteredQuotes based on sortField and sortDirection
   * - For 'date': sort by createdAt
   * - For 'price': sort by finalPrice
   */
  private sortQuotes(): void {
    // TODO: Implement in-memory sorting
  }

  /**
   * Navigate to quote detail page
   */
  viewQuote(id: number): void {
    // Navigation is handled by routerLink in template
  }
}
