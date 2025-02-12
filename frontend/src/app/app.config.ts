import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideClientHydration } from '@angular/platform-browser';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { ApiModule, Configuration, ConfigurationParameters } from './api-client';
import { HttpClientModule, provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';
import { globalInterceptor } from './interceptor/global.interceptor';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { orderReducer } from './ngrx-order/order.reducer';
import { OrderEffects } from './ngrx-order/order.effects';
import { environment } from '../environments/environment';

const apiConfigFactory = (): Configuration => {
  const params: ConfigurationParameters = {
    basePath: environment.apiUrl+':8080' 
  };
  return new Configuration(params);
}
export const appConfig: ApplicationConfig = {
  providers: [provideRouter(routes),
     provideClientHydration(),
      provideAnimationsAsync(),
      provideHttpClient(
        withFetch(),
        withInterceptors([globalInterceptor]),
      ),
    {
      provide: Configuration,
      useFactory: apiConfigFactory
    },
    importProvidersFrom(ApiModule.forRoot(apiConfigFactory)),
    importProvidersFrom(StoreModule.forRoot({ orders: orderReducer })),
    importProvidersFrom(EffectsModule.forRoot([OrderEffects]))
    ]
};
