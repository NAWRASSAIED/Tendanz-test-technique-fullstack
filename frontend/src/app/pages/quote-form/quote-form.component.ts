import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { QuoteService } from '../../services/quote.service';
import { ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';

const ZONES = ['Tunis', 'Sfax', 'Sousse'];

/**
 * Component for creating a new quote
 *
 * TODO: Candidate must implement the following:
 * 1. Initialize reactive form with FormGroup containing:
 *    - customerName: required string
 *    - email: required string, email format
 *    - phone: required string
 *    - productId: required number
 *    - zone: required string
 *    - age: required number, min 18, max 100
 *    - insuredAmount: required number, min 1000
 *    - startDate: required date
 *    - duration: required number, min 1 month
 *
 * 2. Load products from ProductService and populate product dropdown
 *
 * 3. Implement form submission:
 *    - Validate form before submission
 *    - Call QuoteService.createQuote()
 *    - Show success/error message
 *    - Navigate to quote detail page on success
 *
 * 4. Display validation errors for each field
 *
 * 5. Show loading state while API request is in progress
 */
@Component({
  selector: 'app-quote-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './quote-form.component.html',
  styleUrl: './quote-form.component.css'
})
export class QuoteFormComponent implements OnInit {
  form: FormGroup;
  products: Product[] = [];
  zones = ZONES;
  loading = false;
  submitted = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private quoteService: QuoteService,
    private productService: ProductService,
    private router: Router
  ) {
    this.form = this.fb.group({
      customerName: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required]],
      productId: ['', [Validators.required]],
      zone: ['', [Validators.required]],
      age: ['', [Validators.required, Validators.min(18), Validators.max(100)]],
      insuredAmount: ['', [Validators.required, Validators.min(1000)]],
      startDate: ['', [Validators.required]],
      duration: ['', [Validators.required, Validators.min(1)]]
    });
  }

  ngOnInit(): void {
    // TODO: Load products from ProductService
    // TODO: Populate this.products array
    // TODO: Handle loading and error states
  }

  /**
   * Submit the form
   *
   * TODO: Implement form submission
   * - Check if form is valid
   * - Set loading state
   * - Call quoteService.createQuote()
   * - Handle success: show message and navigate to detail page
   * - Handle error: show error message
   */
  onSubmit(): void {
    // TODO: Implement form submission
    console.log('Form submitted (TODO: implement)');
  }

  /**
   * Check if a form field has an error
   */
  hasError(fieldName: string, errorType: string): boolean {
    const field = this.form.get(fieldName);
    return !!(field && field.hasError(errorType) && (field.dirty || field.touched || this.submitted));
  }

  /**
   * Check if a form field is invalid
   */
  isFieldInvalid(fieldName: string): boolean {
    const field = this.form.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched || this.submitted));
  }

  /**
   * Get error message for a field
   */
  getErrorMessage(fieldName: string): string {
    const field = this.form.get(fieldName);
    if (!field || !field.errors) {
      return '';
    }

    if (field.hasError('required')) {
      return `${fieldName} is required`;
    }
    if (field.hasError('email')) {
      return 'Please enter a valid email address';
    }
    if (field.hasError('min')) {
      const min = field.errors['min'].min;
      return `${fieldName} must be at least ${min}`;
    }
    if (field.hasError('max')) {
      const max = field.errors['max'].max;
      return `${fieldName} cannot exceed ${max}`;
    }

    return 'Invalid input';
  }
}
