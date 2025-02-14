import { Order } from '../../api-client/model/order';
import * as jsonpatch from 'fast-json-patch';

export function createPatchOperations(
  currentOrder: Order,
  updatedOrder: Order
) {
  const patch = jsonpatch.compare(currentOrder, updatedOrder);
  return patch.filter(operation => operation.path !== '/id');
}
