import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ItemInstance, ItemType, ShoppingListItem } from './models';

const API = 'http://localhost:8080/api';

@Injectable({ providedIn: 'root' })
export class InventoryApiService {
  private http = inject(HttpClient);

  // --- Item types ---
  getItemTypes(search?: string): Observable<ItemType[]> {
    return this.http.get<ItemType[]>(`${API}/item-types`, {
      params: search ? { search } : {}
    });
  }

  createItemType(itemType: ItemType): Observable<ItemType> {
    return this.http.post<ItemType>(`${API}/item-types`, itemType);
  }

  // --- Inventory (item instances) ---
  getInventory(): Observable<ItemInstance[]> {
    return this.http.get<ItemInstance[]>(`${API}/items`);
  }

  getExpiringSoon(days = 3): Observable<ItemInstance[]> {
    return this.http.get<ItemInstance[]>(`${API}/items/expiring-soon`, {
      params: { days }
    });
  }

  addItem(instance: ItemInstance): Observable<ItemInstance> {
    return this.http.post<ItemInstance>(`${API}/items`, instance);
  }

  consumeItem(id: number): Observable<ItemInstance> {
    return this.http.post<ItemInstance>(`${API}/items/${id}/consume`, {});
  }

  // --- Shopping list ---
  getShoppingList(): Observable<ShoppingListItem[]> {
    return this.http.get<ShoppingListItem[]>(`${API}/shopping-list`);
  }

  addToShoppingList(item: ShoppingListItem): Observable<ShoppingListItem> {
    return this.http.post<ShoppingListItem>(`${API}/shopping-list`, item);
  }

  markPurchased(id: number): Observable<ShoppingListItem> {
    return this.http.post<ShoppingListItem>(`${API}/shopping-list/${id}/purchase`, {});
  }
}
