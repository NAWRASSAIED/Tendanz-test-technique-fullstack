import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { QuoteService } from '../../services/quote.service';
import { ProductService } from '../../services/product.service';
import { QuoteResponse } from '../../models/quote.model';
import { Product } from '../../models/product.model';

/**
 * Component for displaying a list of all quotes
 *
 * TODO: Candidate must implement the following:
 * 1. Load all quotes on component initialization using QuoteService.getQuotes()
 *
 * 2. Load products for filter dropdown using ProductService.getProducts()
 *
 * 3. Implement filtering in applyFilters():
 *    - Build filter object from selectedProductId and minPrice
 *    - Call QuoteService.getQuotes(filters)
 *    - Update filteredQuotes with results
 *
 * 4. Implement sorting in sortQuotes():
 *    - Sort by creation date (ascending/descending)
 *    - Sort by final price (ascending/descending)
 *
 * 5. Implement viewQuote() to navigate to /quotes/:id
 *
 * 6. Handle loading and error states
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
  currentPage = 0;
  pageSize = 5;
  totalPages = 0;
  totalElements = 0;


  constructor(
    private quoteService: QuoteService,
    private productService: ProductService,
    private router: Router
  ) {}

 ngOnInit(): void {
    this.loadQuotes();

    this.productService.getProducts().subscribe({
      next: products => this.products = products,
      error: err => this.errorMessage = err.message || "Failed to load Products"
    });
  }

  private loadQuotes(): void {
    this.loading = true;

    const filters: any = {
      page: this.currentPage,
      size: this.pageSize
    };

    this.quoteService.getQuotes(filters).subscribe({
      next: res => {
        console.log('RAW response:', res);
        this.filteredQuotes = res.content;
        this.totalPages = res.totalPages;
        this.totalElements = res.totalElements;

        this.sortQuotes();
        this.loading = false;
      },
      error: err => {
        this.loading = false;
        this.errorMessage = err.message || "Failed to load quotes";
      }
    });
  }

  /**
   * Apply filters to the quotes
   *
   * TODO: Implement filtering
   * - Get filter values from component properties
   * - Call quoteService.getQuotes(filters)
   * - Update filteredQuotes with results
   * - Call sortQuotes() after receiving results
   * - Handle errors
   */
 applyFilters(): void {
  const filters: {
    productId?: number;
    minPrice?: number;
    page?: number;
    size?: number;
  } = {
    page: 0,       // reset to first page on filter
    size: this.pageSize
  };

  if (this.selectedProductId) {
    filters.productId = this.selectedProductId;
  }
  if (this.minPrice) {
    filters.minPrice = this.minPrice;
  }

  this.loading = true;
  this.currentPage = 0; 

  this.quoteService.getQuotes(filters).subscribe({
    next: res => {                         
      this.filteredQuotes = res.content;  
      this.totalPages = res.totalPages;
      this.totalElements = res.totalElements;
      this.sortQuotes();
      this.loading = false;
    },
    error: (err) => {
      this.loading = false;
      this.errorMessage = err.message || "Failed to apply filters";
    }
  });
}

  /**
   * Reset all filters and reload all quotes
   */
  resetFilters(): void {
    this.selectedProductId = null;
    this.minPrice = null;
    this.applyFilters();
  }


  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadQuotes();
    }
  }

  prevPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadQuotes();
    }
  }

  /**
   * Toggle sort direction or change sort field
   */
  changeSortField(field: 'date' | 'price'): void {
    if (this.sortField === field) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortField = field;
      this.sortDirection = 'asc';
    }
    this.sortQuotes();
  }

  /**
   * Sort filteredQuotes in memory
   *
   * TODO: Implement sorting
   * - For 'date': sort by createdAt
   * - For 'price': sort by finalPrice
   * - Apply sortDirection (asc/desc)
   */
  private sortQuotes(): void {
    this.filteredQuotes.sort((a, b) => {
      let compare = 0;      
      if (this.sortField === 'date') {
        compare = new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime();
      } else if (this.sortField === 'price') {      
        compare = a.finalPrice - b.finalPrice;
      }
      return this.sortDirection === 'asc' ? compare : -compare;
    }
    );
  }

  /**
   * Navigate to quote detail page
   *
   * TODO: Implement navigation to /quotes/:id
   * Hint: use this.router.navigate(['/quotes', id])
   */
  viewQuote(id: number): void {
    this.router.navigate(['/quotes', id]);
  }
  downloadPdf(id: number): void {
  this.quoteService.downloadQuotePdf(id).subscribe(blob => {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `quote_${id}.pdf`;
    a.click();
    window.URL.revokeObjectURL(url);
  });
  }
}
