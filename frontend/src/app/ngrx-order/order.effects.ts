// order.effects.ts
import { Injectable } from '@angular/core';
import { Actions, ofType, createEffect } from '@ngrx/effects';
import { catchError, map, mergeMap } from 'rxjs/operators';
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
} from './order.actions';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable()
export class OrderEffects {
  private apiUrl = environment.apiUrl+':8091';
  loadOrders$ = createEffect(() =>
    this.actions$.pipe(
      ofType(loadOrders),
      mergeMap(() => {
        // VERTX
        return this.httpClient.get<any[]>(this.apiUrl + '/store/orders').pipe(
          map((orders: any) => loadOrdersSuccess({ orders })),
          catchError((error) => of(loadOrdersFailure({ error })))
        );
        // SERLET ASYNC
        // return this.orderService.getOrders().pipe(
        //     map((orders:any) => loadOrdersSuccess({ orders })),
        //     catchError(error => of(loadOrdersFailure({ error })))
        //   )
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

  constructor(
    private actions$: Actions,
    private orderService: OrderService,
    private httpClient: HttpClient
  ) {}
}
