import { ChangeDetectorRef, Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { MapService } from './map.service';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'angularclient';
  nothinguploaded: boolean = false;
  test: string = 'unchanged';
  fileToUpload: File = null;
  url:string;
  geoJson:JSON;

  readonly SpringURL = 'http://localhost:8080/';

  posts: any;
  constructor(private http: HttpClient, private mapservice:MapService ) { }


  sendpath(input:string){
    if(input.endsWith('.graph')){
      console.log(input);
    this.url=input;
    this.mapservice.findAll(input).subscribe(data =>{
      this.test=data;
    })
    this.nothinguploaded=true;

    }else{
      alert('this is not a valid path');
    }
    
  }

}
