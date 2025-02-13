import { Injectable } from '@angular/core';
import { Actions, ofType, createEffect } from '@ngrx/effects';
import { catchError, map, mergeMap, take } from 'rxjs/operators';
import { of } from 'rxjs';
import { OrderService } from '../api-client/api/order.service';
import {
  loadOrders,
  loadOrdersSuccess,
  loadOrdersFailure,
  deleteOrder,
  deleteOrderSuccess,
  deleteOrderFailure,
  addOrder,
  addOrderSuccess,
  addOrderFailure,
  triggerUpdateOrder,
  updateOrderSuccess,
  updateOrderFailure,
} from './order.actions';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { createPatchOperations } from './util/patch-utils';
import { selectOrderById } from './selector/order.selectors';
import { Store } from '@ngrx/store';
import { OrderState } from './order.reducer';

@Injectable()
export class OrderEffects {
  private apiUrl = environment.apiUrl + ':8091';

  loadOrders$ = createEffect(() =>
    this.actions$.pipe(
      ofType(loadOrders),
      mergeMap((action) => {
        let url = `${this.apiUrl}/store/orders`;
        console.log("From: " + action.from)
        if (action.from && action.to) {
          return this.orderService.getOrders(action.from, action.to).pipe(
            map((orders: any) => loadOrdersSuccess({ orders })),
            catchError((error) => of(loadOrdersFailure({ error })))
          );
        } else {
          return this.httpClient.get<any[]>(url).pipe(
            map((orders: any) => loadOrdersSuccess({ orders })),
            catchError((error) => of(loadOrdersFailure({ error })))
          );
        }
      })
    )
  );

  deleteOrder$ = createEffect(() =>
    this.actions$.pipe(
      ofType(deleteOrder),
      mergeMap((action) =>
        this.orderService.deleteOrder(action.id).pipe(
          map(() => deleteOrderSuccess({ id: action.id })),
          catchError((error) => of(deleteOrderFailure({ error })))
        )
      )
    )
  );

  addOrder$ = createEffect(() =>
    this.actions$.pipe(
      ofType(addOrder),
      mergeMap((action) =>
        this.orderService.createOrder(action.order).pipe(
          map((order) => addOrderSuccess({ order })),
          catchError((error) => of(addOrderFailure({ error })))
        )
      )
    )
  );

  updateOrder$ = createEffect(() =>
    this.actions$.pipe(
      ofType(triggerUpdateOrder),
      mergeMap((action) => {
        if (!action.order.id) {
          return of(updateOrderFailure({ error: 'Order ID is missing' }));
        }
        return this.store.select(selectOrderById(action.order.id)).pipe(
          take(1), 
          mergeMap((currentOrder) => {
            if (!currentOrder) {
              return of(
                updateOrderFailure({ error: 'Current order not found' })
              );
            }
            const patchOperations = createPatchOperations(
              currentOrder,
              action.order
            ) as unknown as Array<{ [key: string]: object }>;
            return this.orderService
              .updateOrder(action.order.id!, patchOperations)
              .pipe(
                map((order) => updateOrderSuccess({ order })),
                catchError((error) => of(updateOrderFailure({ error })))
              );
          })
        );
      })
    )
  );
  

  constructor(
    private actions$: Actions,
    private orderService: OrderService,
    private httpClient: HttpClient,
    private store: Store<OrderState>
  ) {}
}
