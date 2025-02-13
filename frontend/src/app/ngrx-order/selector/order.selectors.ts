import { createSelector, createFeatureSelector } from '@ngrx/store';
import { OrderState } from '../order.reducer';

export const selectOrderState = createFeatureSelector<OrderState>('orders');

export const selectOrders = createSelector(
  selectOrderState,
  (state: OrderState) => state.orders
);

export const selectOrderById = (orderId: number) => createSelector(
  selectOrderState,
  (state: OrderState) => state.orders.find(order => order.id === orderId)
);
