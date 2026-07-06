import { Routes } from '@angular/router';
import { authGuard } from './auth/auth.guard';
import { Inventory } from './pages/inventory/inventory';
import { Login } from './pages/login/login';
import { ShoppingList } from './pages/shopping-list/shopping-list';

export const routes: Routes = [
  { path: 'login', component: Login },
  { path: '', component: Inventory, canActivate: [authGuard] },
  { path: 'shopping-list', component: ShoppingList, canActivate: [authGuard] },
  { path: '**', redirectTo: '' }
];
