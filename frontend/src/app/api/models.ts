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

export interface ShoppingList {
  id: number;
  name: string;
  owner: { username: string; displayName?: string };
  createdAt?: string;
}

export interface ShoppingListItem {
  id?: string; // UUID, device-generated during offline sync (Phase 1.5)
  name: string;
  category?: string;
  quantity: number;
  purchased?: boolean;
  deleted?: boolean;
  addedBy?: string;
  deviceId?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface MyPermissions {
  owner: boolean;
  canAdd: boolean;
  canEdit: boolean;
  canDelete: boolean;
}

export interface MemberPermission {
  username: string;
  displayName?: string;
  canAdd: boolean;
  canEdit: boolean;
  canDelete: boolean;
}

export const DEFAULT_CATEGORIES = [
  'Dairy', 'Produce', 'Meat', 'Pantry', 'Frozen', 'Beverages',
  'Cleaning', 'Toiletries', 'Pet', 'Medicine'
] as const;
