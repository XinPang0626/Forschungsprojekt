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
  loaded: boolean = false;
  myStyle = {
    "color": "#ff7800",
    "weight": 5,
    "opacity": 0.65
  };
  mypointStyle = {
    "color": "#000080",
    "weight": 5,
    "opacity": 0.9
  };

  url: string;
  nodeString;


  constructor(private mapservice: MapService) { }

  ngAfterViewInit(): void {
    this.initMap();
  }
  //string be parsed into nodearray
  parseNodeString(nodeString: string): number[][] {
    nodeString=nodeString.replace(/\s/g, "");
    nodeString=nodeString.substring(1, nodeString.length-1);
    console.log(nodeString);
    var nodeArray: number[][] = new Array();
    var split = nodeString.split(",");
    for (let i = 0; i < split.length; i += 2) {
      var long: number = Number(split[i].substring(1, split[i].length));
      var lat: number = Number(split[i + 1].substring(0, split[i + 1].length - 1));
      console.log("lat:"+lat+" long:"+long);
      nodeArray.push([long, lat]);
    }
    return nodeArray;
  }

  computeDij(start: number, end: number, alpha?) {
    
      this.mapservice.getDijpath(this.url, start, end, alpha).subscribe(data => {
        var dijpath = data;
        console.log(dijpath);
        var array = this.parseNodeString(dijpath);
       this.makeaLINE(array);
        console.log("dijpath loaded");
      })
   
      //alert('load a path first')
    

  }
  computAstar(start: number, end: number) {

    this.mapservice.getAstarpath(this.url, start, end).subscribe(data => {
      var astar = data;
      console.log(astar);
      var array = this.parseNodeString(astar);
      this.makeaLINE(array);
      console.log("astarpath loaded");
    })
  }

  makeDOTS(nodearray: number[][]) {
    var long = nodearray[0][0];
    var lat = nodearray[0][1];
    this.changeMapp(lat, long);
    var Points = {
      "type": "FeatureCollection",
      "features": [
        {
          "type": "Feature",
          "properties": {},
          "geometry": {
            "type": "MultiPoint",
            "coordinates": nodearray
          }
        }
      ]
    }
    L.geoJSON(Points, {
      style: this.mypointStyle
    }).addTo(this.map);
  }

  makeaLINE(nodearray: number[][]) {
    var long = nodearray[0][0];
    var lat = nodearray[0][1];
    this.changeMapp(lat, long);
    var myLines = {
      "type": "FeatureCollection",
      "features": [
        {
          "type": "Feature",
          "properties": {},
          "geometry": {
            "type": "LineString",
            "coordinates": nodearray
          }
        }
      ]
    }
    L.geoJSON(myLines, {
      style: this.myStyle
    }).addTo(this.map);
  }

  makeeachDot() {
    var array: number[][];
    for (let i = 0; i < array.length, i++;) {
      var long = array[i][0];
      var lat = array[i][1];

      var mydot = {
        "type": "FeatureCollection",
        "features": [
          {
            "type": "Feature",
            "properties": {"value": "Foo"},
            "geometry": {
              "type": "Point",
              "coordinates": [long, lat]
            }
          }
        ]
      }
      L.geoJSON(mydot).addTo(this.map);
    }

  }



  changeMapp(la: number, lng: number) {
    this.map.panTo(new L.LatLng(la, lng));
  }

  onEachFeature(feature, layer): void {
    layer.on('click', function (e) {
      //alert(feature.properties.popupContent);
      //or
      alert(feature.properties.id);
    });
  }

  sendpath(input: string) {
    if (input.endsWith('.graph')) {
      console.log(input);
      this.url = encodeURI(input);
      console.log(this.url);
      this.mapservice.getNodes(this.url).subscribe(data => {
        this.nodeString = data;
        console.log(this.nodeString);
        var array = this.parseNodeString(this.nodeString);
        this.makeDOTS(array);
        console.log("markers loaded");
        this.loaded = true;
      })
    } else {
      alert('this is not a valid path');
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
