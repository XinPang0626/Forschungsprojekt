import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MapService {
  private cordinatesURL: string;

  constructor(private http: HttpClient) {
    this.cordinatesURL = 'http://localhost:8080/api?path=';
  }

  public findAll(path:string): Observable<string> {
    return this.http.get<string>(this.cordinatesURL+path);
  }
  

}
