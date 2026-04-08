/**
 * Quote Request DTO - sent to backend when creating a new quote
 * Matches the backend QuoteRequestDTO
 */
export interface QuoteRequest {
  customerName: string;
  email: string;
  phone: string;
  productId: number;
  zone: string;
  age: number;
  insuredAmount: number;
  startDate: string; // ISO date format: YYYY-MM-DD
  duration: number; // in months
}

/**
 * Quote Response DTO - returned from backend
 * Matches the backend QuoteResponseDTO
 */
export interface QuoteResponse {
  id: number;
  customerName: string;
  email: string;
  phone: string;
  productId: number;
  productName: string;
  zone: string;
  age: number;
  insuredAmount: number;
  startDate: string; // ISO date format
  duration: number; // in months
  basePrice: number;
  ageModifier: number;
  zoneModifier: number;
  appliedRules: string[]; // list of rule names that were applied
  finalPrice: number;
  createdAt: string; // ISO timestamp
  updatedAt: string; // ISO timestamp
}
