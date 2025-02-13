import {
  HttpRequest,
  HttpHandlerFn,
  HttpEvent,
  HttpHeaders,
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export function getHeaders(): HttpHeaders {
  return new HttpHeaders()
    .set('Access-Control-Allow-Origin', '*')
    .set(
      'Access-Control-Allow-Methods',
      'GET, POST, DELETE, PUT, OPTIONS, PATCH'
    )
    .set(
      'Access-Control-Allow-Headers',
      'Origin, Content-Type, Accept, Access-Control-Request-Method, Access-Control-Allow-Methods, Access-Control-Request-Headers, x-api-key, Access-Control-Allow-Origin'
    )
    .set('Content-Type', 'application/json')
    .set('Accept', 'application/json')
    .set('x-api-key', environment.apiKey);
}

export function globalInterceptor(
  req: HttpRequest<unknown>,
  next: HttpHandlerFn
): Observable<HttpEvent<unknown>> {
  const newReq = req.clone({
    headers: getHeaders(),
  });

  return next(newReq);
}
