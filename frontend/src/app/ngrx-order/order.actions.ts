import { createAction, props } from '@ngrx/store';
import { Order } from '../api-client';

export const loadOrder = createAction(
  '[Order] Load Order',
  props<{ id: number }>()
);

export const loadOrderSuccess = createAction(
  '[Order] Load Orders Success',
  props<{ order: Order }>()
);

export const loadOrderFailure = createAction(
  '[Order] Load Order Failure',
  props<{ error: any }>()
);

export const loadOrders = createAction(
  '[Order] Load Orders',
  props<{ from?: string; to?: string }>()
);

export const loadOrdersSuccess = createAction(
  '[Order] Load Orders Success',
  props<{ orders: Order[] }>()
);

export const loadOrdersFailure = createAction(
  '[Order] Load Orders Failure',
  props<{ error: any }>()
);

export const deleteOrder = createAction(
  '[Order] Delete Order',
  props<{ id: number }>()
);

export const deleteOrderSuccess = createAction(
  '[Order] Delete Order Success',
  props<{ id: number }>()
);

export const deleteOrderFailure = createAction(
  '[Order] Delete Order Failure',
  props<{ error: any }>()
);

export const addOrder = createAction(
  '[Order] Add Order',
  props<{ order: any }>()
);

export const addOrderSuccess = createAction(
  '[Order] Add Order Success',
  props<{ order: Order }>()
);

export const addOrderFailure = createAction(
  '[Order] Add Order Failure',
  props<{ error: any }>()
);

export const triggerUpdateOrder = createAction(
  '[Order] Trigger Update Order',
  props<{ order: Order, localOnly:boolean }>()
);

export const updateOrderSuccess = createAction(
  '[Order] Update Order Success',
  props<{ order: Order }>()
);

export const updateOrderFailure = createAction(
  '[Order] Update Order Failure',
  props<{ error: any }>()
);
