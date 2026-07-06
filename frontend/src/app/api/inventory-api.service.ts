import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import {
  ItemInstance, ItemType, MemberPermission, MyPermissions, ShoppingList, ShoppingListItem
} from './models';

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

  // --- Shopping lists ---
  getLists(): Observable<ShoppingList[]> {
    return this.http.get<ShoppingList[]>(`${API}/lists`);
  }

  createList(name: string): Observable<ShoppingList> {
    return this.http.post<ShoppingList>(`${API}/lists`, { name });
  }

  getMyPermissions(listId: number): Observable<MyPermissions> {
    return this.http.get<MyPermissions>(`${API}/lists/${listId}/my-permissions`);
  }

  // --- Permissions (owner only) ---
  getMembers(listId: number): Observable<MemberPermission[]> {
    return this.http.get<MemberPermission[]>(`${API}/lists/${listId}/permissions`);
  }

  grantPermission(listId: number, grant: MemberPermission): Observable<MemberPermission> {
    return this.http.post<MemberPermission>(`${API}/lists/${listId}/permissions`, grant);
  }

  revokePermission(listId: number, username: string): Observable<void> {
    return this.http.delete<void>(`${API}/lists/${listId}/permissions/${username}`);
  }

  // --- List items ---
  getListItems(listId: number): Observable<ShoppingListItem[]> {
    return this.http.get<ShoppingListItem[]>(`${API}/lists/${listId}/items`);
  }

  addListItem(listId: number, item: ShoppingListItem): Observable<ShoppingListItem> {
    return this.http.post<ShoppingListItem>(`${API}/lists/${listId}/items`, item);
  }

  markPurchased(listId: number, itemId: string): Observable<ShoppingListItem> {
    return this.http.post<ShoppingListItem>(`${API}/lists/${listId}/items/${itemId}/purchase`, {});
  }

  removeListItem(listId: number, itemId: string): Observable<ShoppingListItem> {
    return this.http.delete<ShoppingListItem>(`${API}/lists/${listId}/items/${itemId}`);
  }
}
