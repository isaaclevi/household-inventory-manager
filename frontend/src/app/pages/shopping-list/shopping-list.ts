import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { InventoryApiService } from '../../api/inventory-api.service';
import {
  DEFAULT_CATEGORIES, MemberPermission, MyPermissions, ShoppingList as ListModel, ShoppingListItem
} from '../../api/models';

@Component({
  selector: 'app-shopping-list',
  imports: [FormsModule],
  templateUrl: './shopping-list.html',
  styleUrl: './shopping-list.scss'
})
export class ShoppingList implements OnInit {
  private api = inject(InventoryApiService);

  readonly categories = DEFAULT_CATEGORIES;

  lists = signal<ListModel[]>([]);
  selectedId = signal<number | null>(null);
  items = signal<ShoppingListItem[]>([]);
  perms = signal<MyPermissions | null>(null);
  members = signal<MemberPermission[]>([]);
  loading = signal(true);
  error = signal('');

  newListName = '';
  newName = '';
  newCategory: string = DEFAULT_CATEGORIES[0];
  newQuantity = 1;

  shareUsername = '';
  shareAdd = true;
  shareEdit = true;
  shareDelete = false;

  ngOnInit(): void {
    this.loadLists();
  }

  loadLists(): void {
    this.loading.set(true);
    this.api.getLists().subscribe({
      next: lists => {
        this.lists.set(lists);
        this.loading.set(false);
        if (lists.length && this.selectedId() === null) {
          this.select(lists[0].id);
        }
      },
      error: () => { this.loading.set(false); this.error.set('Could not load your lists'); }
    });
  }

  select(listId: number): void {
    this.selectedId.set(listId);
    this.error.set('');
    this.members.set([]);
    this.api.getListItems(listId).subscribe(items => this.items.set(items));
    this.api.getMyPermissions(listId).subscribe(perms => {
      this.perms.set(perms);
      if (perms.owner) {
        this.api.getMembers(listId).subscribe(members => this.members.set(members));
      }
    });
  }

  createList(): void {
    const name = this.newListName.trim();
    if (!name) return;
    this.api.createList(name).subscribe({
      next: list => {
        this.newListName = '';
        this.lists.update(lists => [...lists, list]);
        this.select(list.id);
      },
      error: err => this.error.set(err.status === 409 ? 'You already own a list with that name' : 'Could not create the list')
    });
  }

  addItem(): void {
    const listId = this.selectedId();
    if (listId === null || !this.newName.trim()) return;
    this.api.addListItem(listId, {
      name: this.newName.trim(),
      category: this.newCategory,
      quantity: this.newQuantity
    }).subscribe({
      next: () => {
        this.newName = '';
        this.newQuantity = 1;
        this.select(listId);
      },
      error: err => this.handleActionError(err, 'add items')
    });
  }

  purchase(item: ShoppingListItem): void {
    const listId = this.selectedId();
    if (listId === null || !item.id) return;
    this.api.markPurchased(listId, item.id).subscribe({
      next: () => this.select(listId),
      error: err => this.handleActionError(err, 'check off items')
    });
  }

  remove(item: ShoppingListItem): void {
    const listId = this.selectedId();
    if (listId === null || !item.id) return;
    this.api.removeListItem(listId, item.id).subscribe({
      next: () => this.select(listId),
      error: err => this.handleActionError(err, 'delete items')
    });
  }

  grant(): void {
    const listId = this.selectedId();
    const username = this.shareUsername.trim().toLowerCase();
    if (listId === null || !username) return;
    this.api.grantPermission(listId, {
      username,
      canAdd: this.shareAdd,
      canEdit: this.shareEdit,
      canDelete: this.shareDelete
    }).subscribe({
      next: () => {
        this.shareUsername = '';
        this.select(listId);
      },
      error: err => this.error.set(err.status === 404 ? 'No user with that username' : 'Could not share the list')
    });
  }

  revoke(member: MemberPermission): void {
    const listId = this.selectedId();
    if (listId === null) return;
    this.api.revokePermission(listId, member.username).subscribe(() => this.select(listId));
  }

  private handleActionError(err: { status: number }, action: string): void {
    this.error.set(err.status === 403 ? `You don't have permission to ${action} on this list` : 'Something went wrong');
  }
}
