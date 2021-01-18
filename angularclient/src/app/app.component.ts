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
  uploaded: boolean = false;
  test;
  fileToUpload: File = null;
  url: string;
  posts: any;
  constructor(private http: HttpClient, private mapservice: MapService) { }

 
  sendpath(input: string) {
    if (input.endsWith('.graph')) {
      console.log(input);
      this.url = encodeURI(input);
      console.log(this.url);
      this.mapservice.getNodes(this.url).subscribe(data => {
        this.test = data;
        console.log(this.test);
      })
      this.uploaded = true;

    } else {
      alert('this is not a valid path');
    }

  }

}
