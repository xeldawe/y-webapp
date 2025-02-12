import { Component, effect, input } from '@angular/core';
import {MatButtonModule} from '@angular/material/button';
import {MatCardModule} from '@angular/material/card';
import { Order } from '../api-client/model/order';

@Component({
  selector: 'app-order',
  standalone: true,
  imports: [MatCardModule, MatButtonModule],
  templateUrl: './order.component.html',
  styleUrl: './order.component.scss'
})
export class OrderComponent {

 // Required input
 order = input.required<Order>(); // InputSignal<string>

 // Derived value using computed
//  ageMultiplied = computed(() => this.age() * 2);

 constructor() {
   // Monitoring changes with effect
   effect(() => {
     console.log(this.order());
   });
 }

}
