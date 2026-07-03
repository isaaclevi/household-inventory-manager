import { Component, OnInit, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InventoryApiService } from '../../api/inventory-api.service';
import { DEFAULT_CATEGORIES, ItemInstance } from '../../api/models';

@Component({
  selector: 'app-inventory',
  imports: [DatePipe, FormsModule],
  templateUrl: './inventory.html',
  styleUrl: './inventory.scss'
})
export class Inventory implements OnInit {
  private api = inject(InventoryApiService);

  readonly categories = DEFAULT_CATEGORIES;
  items = signal<ItemInstance[]>([]);
  loading = signal(true);

  // simple add-item form model
  newName = '';
  newCategory: string = DEFAULT_CATEGORIES[0];
  newQuantity = 1;
  newExpiration = '';

  ngOnInit(): void {
    this.refresh();
  }

  refresh(): void {
    this.loading.set(true);
    this.api.getInventory().subscribe({
      next: items => { this.items.set(items); this.loading.set(false); },
      error: () => this.loading.set(false)
    });
  }

  add(): void {
    if (!this.newName.trim()) return;
    this.api.createItemType({ name: this.newName.trim(), category: this.newCategory })
      .subscribe(itemType => {
        this.api.addItem({
          itemType,
          quantity: this.newQuantity,
          expirationDate: this.newExpiration || undefined
        }).subscribe(() => {
          this.newName = '';
          this.newQuantity = 1;
          this.newExpiration = '';
          this.refresh();
        });
      });
  }

  consume(item: ItemInstance): void {
    if (item.id == null) return;
    this.api.consumeItem(item.id).subscribe(() => this.refresh());
  }

  daysLeft(item: ItemInstance): number | null {
    if (!item.expirationDate) return null;
    const ms = new Date(item.expirationDate).getTime() - Date.now();
    return Math.ceil(ms / 86_400_000);
  }
}
