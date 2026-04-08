/**
 * Product interface - represents an insurance product
 * Matches the backend Product entity
 */
export interface Product {
  id: number;
  name: string;
  description: string;
  basePrice: number; // base price per month
  minAge: number;
  maxAge: number;
  active: boolean;
  createdAt: string; // ISO timestamp
  updatedAt: string; // ISO timestamp
}
