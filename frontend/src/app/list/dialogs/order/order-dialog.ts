import { Component, inject, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogContent, MatDialogRef } from '@angular/material/dialog';
import { FormBuilder, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatStepperModule } from '@angular/material/stepper';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { HttpClient } from '@angular/common/http';
import { OrderService } from '../../../api-client/api/order.service';
import moment from 'moment';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { OrderState } from '../../../ngrx-order/order.reducer';
import { addOrder } from '../../../ngrx-order/order.actions';
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
    MatDialogActions,
    MatDialogContent
  ],
})
export class OrderDialog implements OnInit{
  readonly dialogRef = inject(MatDialogRef<OrderDialog>);
  private _formBuilder = inject(FormBuilder);
  private store = inject(Store<{ orders: OrderState }>);

  firstFormGroup = this._formBuilder.group({
    firstCtrl: ['', Validators.required],
  });
  secondFormGroup = this._formBuilder.group({
    secondCtrl: [new Date().toLocaleDateString(), Validators.required],
    quantityCtrl: [1, [Validators.required, Validators.min(1)]],
  });

  pets: any = [];
  orders$: Observable<Order[]>;

  constructor(@Inject(MAT_DIALOG_DATA) public data: any, private httpClient: HttpClient, private ordersService: OrderService) {
    this.orders$ = this.store.select(state => state.orders.orders);
  }

  ngOnInit(): void {
    this.ninjaPetSelect();
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  onSubmit(): void {
    if (this.secondFormGroup.invalid) {
      // Handle form invalid case
      return;
    }    
    const formattedDate = moment(this.secondFormGroup.value.secondCtrl).utc().format();
    const newOrder = {
      id: null, // or provide a unique id if needed
      petId: this.firstFormGroup.value.firstCtrl,
      quantity: this.secondFormGroup.value.quantityCtrl,
      shipDate: formattedDate,
      orderStatus: 'PLACED', // Set default status or get from a form control
      complete: false // Set default completion status or get from a form control
    };
    this.store.dispatch(addOrder({ order: newOrder }));
    this.dialogRef.close(newOrder);
  }

  ninjaPetSelect() {
    this.httpClient.get<any[]>(environment.apiUrl+":8092/pets").subscribe(
      (data:any) => { // Explicitly type data
        this.pets = data;
        console.log(data);
      },
      error => {
        console.error('Error fetching stock orders', error);
      }
    );
  }
}
