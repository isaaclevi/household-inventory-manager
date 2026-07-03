import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { InventoryApiService } from '../../api/inventory-api.service';
import { DEFAULT_CATEGORIES, ShoppingListItem } from '../../api/models';

@Component({
  selector: 'app-shopping-list',
  imports: [FormsModule],
  templateUrl: './shopping-list.html',
  styleUrl: './shopping-list.scss'
})
export class ShoppingList implements OnInit {
  private api = inject(InventoryApiService);

  readonly categories = DEFAULT_CATEGORIES;
  items = signal<ShoppingListItem[]>([]);
  loading = signal(true);

  newName = '';
  newCategory: string = DEFAULT_CATEGORIES[0];
  newQuantity = 1;

  ngOnInit(): void {
    this.refresh();
  }

  refresh(): void {
    this.loading.set(true);
    this.api.getShoppingList().subscribe({
      next: items => { this.items.set(items); this.loading.set(false); },
      error: () => this.loading.set(false)
    });
  }

  add(): void {
    if (!this.newName.trim()) return;
    this.api.addToShoppingList({
      name: this.newName.trim(),
      category: this.newCategory,
      quantity: this.newQuantity
    }).subscribe(() => {
      this.newName = '';
      this.newQuantity = 1;
      this.refresh();
    });
  }

  purchase(item: ShoppingListItem): void {
    if (item.id == null) return;
    this.api.markPurchased(item.id).subscribe(() => this.refresh());
  }
}
