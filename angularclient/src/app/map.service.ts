import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import {  retry } from 'rxjs/operators';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MapService {
  private cordinatesURL: string;
  private dijURL:string;
  private astarURL:string;
   HTTPOptions:Object = {

    headers: new HttpHeaders({
        'Content-Type': 'application/json'
    }),
    responseType: 'text'
 }

  constructor(private http: HttpClient) {
    this.cordinatesURL = 'http://localhost:8080/api?path=';
    this.dijURL='http://localhost:8080/dij?path=';
    this.astarURL='http://localhost:8080/astar?path=';
  }

  public getNodes(path:string){
    return this.http.get(this.cordinatesURL+path, this.HTTPOptions );
  }

  public getDijpath(path:string, start:number, end:number, alpha:string): Observable<string> {
    return this.http.get<string>(this.dijURL+path+"&start="+start+"&end="+end+"&alpha="+alpha, this.HTTPOptions);
  }
  public getAstarpath(path:string, start:number, end:number): Observable<string> {
    return this.http.get<string>(this.dijURL+path+"&start="+start+"&end="+end, this.HTTPOptions);}

}
