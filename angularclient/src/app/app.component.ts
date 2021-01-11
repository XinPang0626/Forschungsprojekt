import { ChangeDetectorRef, Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'angularclient';
  nothinguploaded: boolean = false;
  test: string = 'changed';
  fileToUpload: File = null;
  url:string;

  readonly SpringURL = 'http://localhost:8080/';

  posts: any;
  constructor(private http: HttpClient) { }


  sendpath(input:string){
    this.url=input;
  }

  getPosts() {
    this.http.get(this.SpringURL).subscribe((res: Response) => {
      console.log(res);
    })

  }
  uploadFile() {
    if (!(this.nothinguploaded)) {
      alert('you have to upload a file first');
    }


  }

}
