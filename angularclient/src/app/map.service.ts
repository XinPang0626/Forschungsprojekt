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
  private dijCorURL:string;
  private astarCorURL:string;
  private pointURL:string;
   HTTPOptions:Object = {

    headers: new HttpHeaders({
        'Content-Type': 'application/json'
    }),
    responseType: 'text'
 }
  astarpathURL: string;

  constructor(private http: HttpClient) {
    this.cordinatesURL = 'http://localhost:8080/api?path=';
    this.dijURL='http://localhost:8080/dij?';
    this.astarURL='http://localhost:8080/astarload?';
    this.astarpathURL='http://localhost:8080/astarpath?';
    this.dijCorURL='http://localhost:8080/dijcor?';
    this.astarCorURL='http://localhost:8080/astarcor?'
    this.pointURL='http://localhost:8080/apicor?point=';
  }

  public getNodes(path:string){
    return this.http.get(this.cordinatesURL+path, this.HTTPOptions );
  }

  public getDijpath( start:number, end:number, alpha:string): Observable<string> {
    return this.http.get<string>(this.dijURL+"start="+start+"&end="+end+"&alpha="+alpha, this.HTTPOptions);
  }
  public getDijcorpath( start:string, end:string, alpha:string): Observable<string> {
    return this.http.get<string>(this.dijCorURL+"start="+start+"&end="+end+"&alpha="+alpha, this.HTTPOptions);
  }
  public loadAstar(type:string): Observable<string> {
    return this.http.get<string>(this.astarURL+"type="+type, this.HTTPOptions);}
    
  public getAstarpath( start:number, end:number, alpha:string): Observable<string> {
      return this.http.get<string>(this.astarpathURL+"start="+start+"&end="+end+"&alpha="+alpha, this.HTTPOptions);}

  public getPoint( cordinatespoint:string, startorend:string): Observable<string> {
        return this.http.get<string>(this.pointURL+cordinatespoint+"&startOrend="+startorend, this.HTTPOptions);}
        
   
  public getAstarcorpath( start:string, end:string, alpha:string): Observable<string> {
    return this.http.get<string>(this.astarCorURL+"start="+start+"&end="+end+"&alpha="+alpha, this.HTTPOptions);} 

}
