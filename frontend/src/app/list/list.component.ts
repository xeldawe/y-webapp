import { Component, inject, signal, OnInit } from '@angular/core';
import { OrderService } from '../api-client/api/order.service';
import { Order } from '../api-client';
import { OrderComponent } from '../order/order.component';
import { HttpClient } from '@angular/common/http';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatRadioModule } from '@angular/material/radio';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { OrderDialog } from './dialogs/order/order-dialog';
import { catchError, of } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { CommonModule } from '@angular/common';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { OrderState } from '../ngrx-order/order.reducer';
import { deleteOrder, loadOrders } from '../ngrx-order/order.actions';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-list',
  standalone: true,
  imports: [
    MatExpansionModule,
    MatRadioModule,
    FormsModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatIconModule,
    CommonModule,
    NgbModule,
  ],
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.scss'],
})
export class ListComponent implements OnInit {
  readonly panelOpenState = signal(false);
  orders$: Observable<Order[]>;
  readonly dialog = inject(MatDialog);

  constructor(
    private store: Store<{ orders: OrderState }>,
    private ordersService: OrderService,
    private httpClient: HttpClient
  ) {
    this.orders$ = store.select((state) => state.orders.orders);
  }

  ngOnInit(): void {
    this.store.dispatch(loadOrders());
  }

  delete(order: Order): void {
    if (order.id) {
      this.store.dispatch(deleteOrder({ id: order.id }));
    }
  }

  addOrderDialog(
    enterAnimationDuration: string,
    exitAnimationDuration: string
  ): void {
    this.dialog.open(OrderDialog, {
      enterAnimationDuration,
      exitAnimationDuration,
      data: this.orders$,
    });
  }
}
