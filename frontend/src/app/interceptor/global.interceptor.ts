import {
  HttpRequest,
  HttpHandlerFn,
  HttpEvent,
  HttpHeaders,
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { AppComponent } from '../app.component';

export function getHeaders(contentType: string = 'application/json'): HttpHeaders {
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
    .set('Content-Type', contentType)
    .set('Accept', 'application/json')
    .set('x-api-key', AppComponent.apiKey?AppComponent.apiKey:environment.apiKey);
}

export function globalInterceptor(
  req: HttpRequest<unknown>,
  next: HttpHandlerFn
): Observable<HttpEvent<unknown>> {
  const contentType = req.method === 'PATCH' ? 'application/json-patch+json' : 'application/json';
  const newReq = req.clone({
    headers: getHeaders(contentType),
  });

  return next(newReq);
}
