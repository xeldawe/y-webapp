import { Component, OnInit, ViewChild, inject, signal } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
} from '@angular/forms';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatDialog } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import { HttpClient } from '@angular/common/http';
import { OrderService } from '../api-client/api/order.service';
import { Order } from '../api-client';
import {
  deleteOrder,
  loadOrders,
  triggerUpdateOrder,
} from '../ngrx-order/order.actions';
import { environment } from '../../environments/environment';
import { OrderState } from '../ngrx-order/order.reducer';
import { OrderDialog } from './dialogs/order/order-dialog';
import { CommonModule } from '@angular/common';
import { MatNativeDateModule } from '@angular/material/core';
import { MatTooltip, MatTooltipModule } from '@angular/material/tooltip';
import { AppComponent } from '../app.component';

@Component({
  selector: 'app-list',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    MatExpansionModule,
    MatNativeDateModule,
    CommonModule,
    FormsModule,
    MatTooltipModule,
  ],
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.scss'],
})
export class ListComponent implements OnInit {
  @ViewChild('tooltip') tooltip!: MatTooltip;
  tooltipMessage: string = '';
  readonly panelOpenState = signal(false);
  orders$: Observable<Order[]>;
  petsMap: { [key: number]: string } = {};
  readonly dialog = inject(MatDialog);
  filterForm: FormGroup;
  private hoverTimers: { [key: number]: any } = {};
  private hoverActive: { [key: number]: boolean } = {};
  expanded: number = 0;
  apiKey = '';
  private maxFilterIntervalDays = this.parseIntervalToDays(
    environment.filterInterval
  );

  constructor(
    private store: Store<{ orders: OrderState }>,
    private ordersService: OrderService,
    private httpClient: HttpClient,
    private fb: FormBuilder
  ) {
    this.orders$ = store.select((state) => state.orders.orders);
    this.filterForm = this.fb.group({
      startDate: [null],
      endDate: [null],
    });
  }

  ngOnInit(): void {
    this.store.dispatch(loadOrders({ from: undefined, to: undefined }));
    this.loadPets();
  }

  ngAfterViewInit(): void {
    if (this.tooltipMessage != '') {
      this.showTooltip(this.tooltipMessage);
    }
  }

  loadPets(): void {
    this.httpClient.get<any[]>(`${environment.apiUrl}:8092/pets`).subscribe(
      (pets) => {
        pets.forEach((pet) => {
          this.petsMap[pet.id] = pet.name;
        });
      },
      (error) => {
        console.error('Error fetching pets', error);
      }
    );
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
      data: {},
    });
  }

  editOrderDialog(
    order: Order,
    enterAnimationDuration: string,
    exitAnimationDuration: string
  ): void {
    this.expanded = order.id!;
    this.dialog.open(OrderDialog, {
      enterAnimationDuration,
      exitAnimationDuration,
      data: { order },
    });
  }

  getPetName(petId: number): string {
    return this.petsMap[petId] || 'Unknown';
  }

  onPanelHover(order: Order): void {
    const panelElement = document.getElementById(`order-panel-${order.id}`);
    if (panelElement) {
      panelElement.addEventListener('mouseenter', () =>
        this.startHoverTimer(order)
      );
      panelElement.addEventListener('mouseleave', () =>
        this.clearHoverTimer(order)
      );
    }
  }

  startHoverTimer(order: Order): void {
    if (order.id) {
      if (this.hoverActive[order.id]) return;
      this.hoverActive[order.id] = true;

      this.hoverTimers[order.id] = setTimeout(() => {
        this.httpClient
          .get<Order>(`${environment.apiUrl}:8091/store/orders/${order.id}`)
          .subscribe(
            (updatedOrder) => {
              this.expanded = order.id!;
              this.store.dispatch(triggerUpdateOrder({ order: updatedOrder }));
              this.showTooltip(updatedOrder.shipDate!);
              return;
            },
            (error) => {
              console.error('Error fetching order', error);
              return;
            }
          );
      }, 500);
    }
  }

  clearHoverTimer(order: Order): void {
    if (order.id) {
      if (this.hoverTimers[order.id]) {
        clearTimeout(this.hoverTimers[order.id]);
        delete this.hoverTimers[order.id];
      }
      this.hoverActive[order.id] = false;
      if (this.tooltipMessage) {
        this.tooltip.hide();
        this.tooltipMessage = '';
      }
    }
  }

  applyFilter(): void {
    const { startDate, endDate } = this.filterForm.value;
    if (startDate && endDate) {
      const from = new Date(startDate);
      from.setHours(0, 0, 0, 0);

      const to = new Date(endDate);
      to.setHours(23, 59, 59, 999);

      if (this.isDateRangeValid(from, to)) {
        const fromISOString = from.toISOString().slice(0, -5) + 'Z';
        const toISOString = to.toISOString().slice(0, -5) + 'Z';

        this.store.dispatch(
          loadOrders({ from: fromISOString, to: toISOString })
        );
      }
    }
  }

  checkAndApplyFilter(): void {
    const { startDate, endDate } = this.filterForm.value;
    if (startDate && endDate) {
      if (this.isDateRangeValid(new Date(startDate), new Date(endDate))) {
        this.applyFilter();
        this.updateMaxDate();
      } else {
        this.clearDates();
      }
    }
  }

  clearDates(): void {
    this.filterForm.patchValue({
      startDate: null,
      endDate: null,
    });
    this.store.dispatch(loadOrders({ from: undefined, to: undefined }));
  }

  private parseIntervalToDays(interval: string): number {
    const unit = interval.charAt(interval.length - 1);
    const value = parseInt(interval.substring(0, interval.length - 1), 10);
    switch (unit) {
      case 'm':
        return value * 30;
      case 'd':
        return value;
      default:
        throw new Error('Invalid interval unit');
    }
  }

  private isDateRangeValid(from: Date, to: Date): boolean {
    const fromDate = new Date(
      from.getFullYear(),
      from.getMonth(),
      from.getDate()
    );
    const toDate = new Date(to.getFullYear(), to.getMonth(), to.getDate());
    const differenceInDays =
      (toDate.getTime() - fromDate.getTime()) / (1000 * 60 * 60 * 24);
    return differenceInDays <= this.maxFilterIntervalDays;
  }

  updateMaxDate(): void {
    const { startDate, endDate } = this.filterForm.value;
    if (startDate && endDate) {
      const from = new Date(startDate);
      const to = new Date(endDate);
      if (this.isDateRangeValid(from, to)) {
        return;
      } else {
        this.clearDates();
      }
    }
    if (startDate) {
      const maxDate = new Date(startDate);
      maxDate.setDate(maxDate.getDate() + this.maxFilterIntervalDays);
      this.filterForm.patchValue({
        endDate: maxDate,
      });
    }
  }

  overrideApiKey(value: string) {
    const val = value.trim();
    if (val === '') {
      AppComponent.apiKey = undefined;
    } else {
      AppComponent.apiKey = val;
    }
  }

  showTooltip(message: string): void {
    this.tooltipMessage = message;
    if (this.tooltip && this.tooltip._isTooltipVisible()) {
      this.tooltip.hide(0);
      setTimeout(() => this.tooltip.show(), 0);
    } else {
      this.tooltip.show();
    }
  }
}
