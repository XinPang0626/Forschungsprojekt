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
 
  myStyle = {
    "color": "#ff7800",
    "weight": 5,
    "opacity": 0.65
  };


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
  url: string;
  nodeString;


  constructor(private mapservice: MapService) { }

  ngAfterViewInit(): void {
    this.initMap();
  }
  //string be parsed into
  parseNodeString(nodeString: string): number[][] {
    var nodeArray: number[][] = new Array();
    var split = nodeString.split(",");
    for (let i = 0; i < split.length; i += 2) {

      var lat: number = Number(split[i].substring(1, split[i].length));
      var longt: number = Number(split[i + 1].substring(0, split[i + 1].length - 1));
      nodeArray.push([lat, longt]);

    }
    return nodeArray;
  }

  computeDij(path: string, start: number, end: number, alpha?) {
    //insert http method later
  }

  makeaLINE() {
    var array = this.parseNodeString("(-104.98809814453125, 39.76632525654491),(-104.9359130859375, 39.751017451967144),(-104.974365234375, 39.720919782725545)");
    console.log("(-104.98809814453125, 39.76632525654491),(-104.9359130859375, 39.751017451967144),(-104.974365234375, 39.720919782725545)");
    //console.log(array[0][0]);
    var myLines = {
      "type": "FeatureCollection",
      "features": [
       
        {
          "type": "Feature",
          "properties": {},
          "geometry": {
            "type": "LineString",
            "coordinates": array
          }
        }
      ]
    }
    L.geoJSON(myLines, {
      style: this.myStyle
  }).addTo(this.map);
  }


  changeMapp(la: number, lng: number) {
    this.map.panTo(new L.LatLng(la, lng));
  }

  sendpath(input: string) {
    if (input.endsWith('.graph')) {
      console.log(input);
      this.url = encodeURI(input);
      console.log(this.url);
      this.mapservice.getNodes(this.url).subscribe(data => {
        this.nodeString = data;
        console.log(this.nodeString);
      })
      this.uploaded = true;

    } else {
      alert('this is not a valid path');
    }

  }



  //empty method which be adding geoJson to map later
  readGeoJson(): void {
    if (!(this.uploaded)) {
      alert('you have to upload a file first');
      //L.marker([this.defaultla, this.defaultlng]).addTo(this.map);
      console.log(this.cordinates);
    } else {
      console.log('Mapping loaded');
      var array= this.parseNodeString(this.nodeString);






      
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
