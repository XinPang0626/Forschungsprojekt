import { AfterViewInit, Component, Input } from '@angular/core';
import * as L from 'leaflet';
import { MapService } from '../map.service';

const iconRetinaUrl = './assets/marker-icon-2x.png';
const iconUrl = './assets/marker-icon.png';
const shadowUrl = './assets/marker-shadow.png';
const iconDefault = L.icon({
  iconRetinaUrl,
  iconUrl,
  shadowUrl,
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  tooltipAnchor: [16, -28],
  shadowSize: [41, 41]
});
L.Marker.prototype.options.icon = iconDefault;
@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss']
})
export class MapComponent implements AfterViewInit {
  private map;
  defaultla: number = 39.75621;
  defaultlng: number = -104.99404;
  cordinates: string;
  json;
  testarray = [[-100, 40], [-105, 45], [-110, 55]];//array can be formed first
  myStyle = {
    "color": "#ff7800",
    "weight": 5,
    "opacity": 0.65
  };

  myLines = [{
    "type": "LineString",
    "coordinates": this.testarray// and added to geoJSON
  }, {
    "type": "LineString",
    "coordinates": [[-105, 40], [-110, 45], [-115, 55]]
  }];

  geojsonFeature = {
    "type": "FeatureCollection",
    "features": [
      {
        "type": "Feature",
        "properties": {},
        "geometry": {
          "type": "Polygon",
          "coordinates": [
            [
              [
                -105.00457763671874,
                39.753129075575174
              ],
              [
                -104.99290466308594,
                39.720919782725545
              ],
              [
                -104.95513916015625,
                39.72303232864369
              ],
              [
                -104.94621276855469,
                39.74626606218367
              ],
              [
                -105.00457763671874,
                39.753129075575174
              ]
            ]
          ]
        }
      }
    ]
  }
  @Input() uploaded: boolean;//this variable receives information from appcomponent


  constructor(private mapservice: MapService) { }

  ngAfterViewInit(): void {
    this.initMap();
  }
  //string be returned in the form of (1,2),(1,3)
  parseNodeString(nodeString: string):number[][] {
    nodeString="(2,2),(3,3)"    
    var nodeArray: number[][];
    var split= nodeString.split(",");
    for (let i = 0; i < split.length; i++) {
      console.log(i+" "+split[i]);
      console.log(split[i+1]);
     if(split[i].startsWith("(")){
       var lang= split[i].substring(1, split[i].length);
       //var longt= split[i+1].substring(0, split[i+1].length-2);
       //console.log(""+lang);
     }
    }
    return nodeArray;
  }

  computeDij(path:string, start:number, end:number, alpha?){
    //insert http method later

  }
  

  changeMapp(la: number, lng: number) {
    this.map.panTo(new L.LatLng(la, lng));
  }



  //empty method which be adding geoJson to map later
  readGeoJson(): void {
    if (!(this.uploaded)) {
      alert('you have to upload a file first');
      //L.marker([this.defaultla, this.defaultlng]).addTo(this.map);



      console.log(this.cordinates);
    } else {
      console.log('Mapping loaded');
      L.geoJSON(this.geojsonFeature).addTo(this.map);

    }
  }

  private initMap(): void {
    this.map = L.map('map', {
      center: [this.defaultla, this.defaultlng],
      zoom: 20
    });

    const tiles = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    });

    tiles.addTo(this.map);
  }


}
