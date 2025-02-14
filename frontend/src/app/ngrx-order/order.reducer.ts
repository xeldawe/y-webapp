import { createReducer, on } from '@ngrx/store';
import {
  loadOrders,
  loadOrdersSuccess,
  loadOrdersFailure,
  deleteOrderSuccess,
  deleteOrderFailure,
  addOrderSuccess,
  addOrderFailure,
  triggerUpdateOrder,
  updateOrderSuccess,
  updateOrderFailure,
} from './order.actions';
import { Order } from '../api-client';

export interface OrderState {
  orders: Order[];
  error: any;
  from?: string;
  to?: string;
}

export const initialState: OrderState = {
  orders: [],
  error: null,
  from: undefined,
  to: undefined,
};

export const orderReducer = createReducer(
  initialState,
  on(loadOrders, (state, { from, to }) => ({
    ...state,
    from,
    to,
  })),
  on(loadOrdersSuccess, (state, { orders }) => ({
    ...state,
    orders,
  })),
  on(loadOrdersFailure, (state, { error }) => ({
    ...state,
    error,
  })),

  on(deleteOrderSuccess, (state, { id }) => ({
    ...state,
    orders: state.orders.filter((order) => order.id !== id),
  })),
  on(deleteOrderFailure, (state, { error }) => ({
    ...state,
    error,
  })),

  on(addOrderSuccess, (state, { order }) => ({
    ...state,
    orders: [...state.orders, order],
  })),
  on(addOrderFailure, (state, { error }) => ({
    ...state,
    error,
  })),

  on(updateOrderSuccess, (state, { order }) => ({
    ...state,
    orders: state.orders.map((o) => (o.id === order.id ? order : o)),
  })),
  on(updateOrderFailure, (state, { error }) => ({
    ...state,
    error,
  }))
);
