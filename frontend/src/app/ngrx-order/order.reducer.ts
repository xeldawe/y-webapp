// order.reducer.ts
import { createReducer, on } from '@ngrx/store';
import { loadOrdersSuccess, loadOrdersFailure, deleteOrderSuccess, deleteOrderFailure, addOrderSuccess, addOrderFailure } from './order.actions';
import { Order } from '../api-client';

export interface OrderState {
  orders: Order[];
  error: any;
}

export const initialState: OrderState = {
  orders: [],
  error: null,
};

export const orderReducer = createReducer(
  initialState,
  on(loadOrdersSuccess, (state, { orders }) => ({ ...state, orders })),
  on(loadOrdersFailure, (state, { error }) => ({ ...state, error })),
  on(deleteOrderSuccess, (state, { id }) => ({
    ...state,
    orders: state.orders.filter(order => order.id !== id),
  })),
  on(deleteOrderFailure, (state, { error }) => ({ ...state, error })),
  on(addOrderSuccess, (state, { order }) => ({ ...state, orders: [...state.orders, order] })),
  on(addOrderFailure, (state, { error }) => ({ ...state, error }))
);
