import { Routes } from '@angular/router';
import { Inventory } from './pages/inventory/inventory';
import { ShoppingList } from './pages/shopping-list/shopping-list';

export const routes: Routes = [
  { path: '', component: Inventory },
  { path: 'shopping-list', component: ShoppingList },
  { path: '**', redirectTo: '' }
];
