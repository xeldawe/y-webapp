import { Order } from '../../api-client/model/order';

export function createPatchOperations(
  currentOrder: Order,
  updatedOrder: Order
) {
  const operations: Array<{ op: string; path: string; value?: any }> = [];

  Object.keys(updatedOrder).forEach((key) => {
    if (key !== 'id') {
      const newValue = updatedOrder[key as keyof Order];
      const oldValue = currentOrder[key as keyof Order];

      if (newValue !== oldValue) {
        operations.push({ op: 'replace', path: `/${key}`, value: newValue });
      }
    }
  });

  return operations;
}
