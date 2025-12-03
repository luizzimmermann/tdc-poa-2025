import { provideRouter } from '@angular/router';
import {ApplicationConfig, inject, provideZoneChangeDetection} from '@angular/core';
import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandlerFn,
  HttpRequest,
  provideHttpClient,
  withInterceptors
} from '@angular/common/http';
import {
  createInterceptorCondition,
  INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG, IncludeBearerTokenCondition, includeBearerTokenInterceptor
} from 'keycloak-angular';
import Keycloak from 'keycloak-js';

import { provideKeycloakAngular } from './keycloak.config';
import { routes } from './app.routes';
import {catchError, Observable, throwError} from "rxjs";


export const insufficientUserAuthenticationErrorInterceptor = (
  req: HttpRequest<unknown>,
  next: HttpHandlerFn
): Observable<HttpEvent<unknown>> => {
  const keycloak = inject(Keycloak);

  // check if response status is 401 and if www-authenticate header is present
  // if the WWW-Authenticate header is present, it should be in the following format
  // Bearer error="insufficient_user_authentication", error_description="A different authentication level is required", acr_values="2"
  // the interceptor should check if the error is insufficient_user_authentication and then retrieve the acr_values
  // after that it should call keycloak.login passing the extract acr_values
  return next(req).pipe(
    catchError((err: HttpErrorResponse) => {
      console.log(err);
      if (err.status === 401 && err.headers.has('www-authenticate')) {
        const wwwAuthenticateHeader = err.headers.get('www-authenticate');
        const error = wwwAuthenticateHeader?.match(/Bearer error="([^"]*)"/)?.[1];
        if (error === 'insufficient_user_authentication') {
          const acrValues = wwwAuthenticateHeader?.match(/acr_values="([^"]*)"/)?.[1];
          if (acrValues) {
            keycloak.login({ acrValues });
          }
        }
      }
      return throwError(() => err);
    })
  );

};

const urlCondition = createInterceptorCondition<IncludeBearerTokenCondition>({
  urlPattern: /^(https:\/\/localhost\/api)(\/.*)?$/i,
  bearerPrefix: 'Bearer'
});

export const appConfig: ApplicationConfig = {
  providers: [
    provideKeycloakAngular(),
    {
      provide: INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG,
      useValue: [urlCondition]
    },
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptors([includeBearerTokenInterceptor, insufficientUserAuthenticationErrorInterceptor]))
  ]
};
