import { Component, inject, Inject, OnInit } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
} from '@angular/material/dialog';
import {
  FormBuilder,
  Validators,
  FormsModule,
  ReactiveFormsModule,
} from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatStepperModule } from '@angular/material/stepper';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { HttpClient } from '@angular/common/http';

import { OrderService } from '../../../api-client/api/order.service';
import moment from 'moment';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { OrderState } from '../../../ngrx-order/order.reducer';
import {
  addOrder,
  triggerUpdateOrder,
} from '../../../ngrx-order/order.actions';
import { Order } from '../../../api-client/model/order';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'add-order-dialog',
  templateUrl: 'order-dialog.html',
  styleUrls: ['order-dialog.scss'],
  standalone: true,
  imports: [
    MatButtonModule,
    MatStepperModule,
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatCheckboxModule,
    MatDialogActions,
    MatDialogContent,
  ],
})
export class OrderDialog implements OnInit {
  readonly dialogRef = inject(MatDialogRef<OrderDialog>);
  private _formBuilder = inject(FormBuilder);
  private store = inject(Store<{ orders: OrderState }>);

  firstFormGroup = this._formBuilder.group({
    firstCtrl: [null as number | null, Validators.required],
  });
  secondFormGroup = this._formBuilder.group({
    secondCtrl: [new Date().toLocaleDateString(), Validators.required],
    quantityCtrl: [1, [Validators.required, Validators.min(1)]],
    orderStatusCtrl: ['PLACED' as Order.OrderStatusEnum],
    completeCtrl: [false],
  });

  pets: any[] = [];
  orders$: Observable<Order[]>;
  isUpdate: boolean = false;
  orderToUpdate: Order | null = null;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    private httpClient: HttpClient,
    private ordersService: OrderService
  ) {
    this.orders$ = this.store.select((state) => state.orders.orders);
    if (data && data.order) {
      this.isUpdate = true;
      this.orderToUpdate = data.order;
      this.firstFormGroup.patchValue({ firstCtrl: data.order.petId });
      this.secondFormGroup.patchValue({
        secondCtrl: data.order.shipDate,
        quantityCtrl: data.order.quantity,
        orderStatusCtrl: data.order.orderStatus,
        completeCtrl: data.order.complete,
      });
    }
  }

  ngOnInit(): void {
    this.ninjaPetSelect();
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  onSubmit(): void {
    if (this.secondFormGroup.invalid) {
      return;
    }
    const formattedDate = moment(this.secondFormGroup.value.secondCtrl)
      .utc()
      .format();
    const order: Order = {
      id: this.isUpdate ? this.orderToUpdate!.id : undefined,
      petId: this.firstFormGroup.value.firstCtrl!,
      quantity: this.secondFormGroup.value.quantityCtrl!,
      shipDate: formattedDate,
      orderStatus: this.isUpdate
        ? (this.secondFormGroup.value.orderStatusCtrl as Order.OrderStatusEnum)
        : 'PLACED',
      complete: this.isUpdate
        ? this.secondFormGroup.value.completeCtrl!
        : false,
    };

    if (this.isUpdate) {
      this.store.dispatch(triggerUpdateOrder({ order, localOnly:false }));
    } else {
      this.store.dispatch(addOrder({ order }));
    }
    this.dialogRef.close(order);
  }

  ninjaPetSelect() {
    this.httpClient.get<any[]>(environment.apiUrl + ':8092/pets').subscribe(
      (data: any) => {
        this.pets = data;
        console.log(data);
      },
      (error) => {
        console.error('Error fetching pets', error);
      }
    );
  }
}
