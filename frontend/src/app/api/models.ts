export interface ItemType {
  id?: number;
  name: string;
  brand?: string;
  category: string;
  defaultUnit?: string;
}

export interface ItemInstance {
  id?: number;
  itemType: ItemType;
  quantity: number;
  expirationDate?: string; // ISO date (yyyy-MM-dd), absent for produce/bulk
  addedAt?: string;
  addedBy?: string;
  consumed?: boolean;
}

export interface ShoppingListItem {
  id?: number;
  name: string;
  category?: string;
  quantity: number;
  purchased?: boolean;
  createdAt?: string;
  addedBy?: string;
}

export const DEFAULT_CATEGORIES = [
  'Dairy', 'Produce', 'Meat', 'Pantry', 'Frozen', 'Beverages',
  'Cleaning', 'Toiletries', 'Pet', 'Medicine'
] as const;
