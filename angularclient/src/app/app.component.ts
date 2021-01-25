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
  test;
  fileToUpload: File = null;
  url: string;
  posts: any;
  constructor(private http: HttpClient, private mapservice: MapService) { }
}
