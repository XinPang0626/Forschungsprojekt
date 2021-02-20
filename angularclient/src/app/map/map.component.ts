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
  astartype = 'Standard';


  startla: number;
  startlong: number;
  endla: number;
  endlong: number;
  startcor:string;
  endcor:string; 

//label for button like a light switch
  startchange = false;
  endchange = false;  
  startbutton: string = 'change start';
  endbutton: string = 'change end';

  loaded: boolean = false;
  myStyleDij = {
    "color": "#f321b4",
    "weight": 7,
    "opacity": 0.65
  };
  myStyle = {
    "color": "#000080",
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
    nodeString = nodeString.replace(/\s/g, "");
    nodeString = nodeString.substring(1, nodeString.length - 1);
    console.log(nodeString);
    var nodeArray: number[][] = new Array();
    var split = nodeString.split(",");
    for (let i = 0; i < split.length; i += 2) {
      var long: number = Number(split[i].substring(1, split[i].length));
      var lat: number = Number(split[i + 1].substring(0, split[i + 1].length - 1));
      console.log("lat:" + lat + " long:" + long);
      nodeArray.push([long, lat]);
    }
    return nodeArray;
  }

  havestart() {
    if (this.startchange) {
      this.startbutton = 'change start';
      this.startchange = false;
    } else {
      this.startbutton = 'save start';
      this.startchange = true;
    }
  }
  haveend() {
    if (this.endchange) {
      this.endchange = false;
      this.endbutton = 'change end';
    } else {
      this.endchange = true;
      this.endbutton = 'save end';
    }
  }

  radiochangeEvent(event: any) {
    this.astartype = event.target.value;
    console.log(this.astartype);
  }

  computeDij(start: number, end: number, alpha) {
    var dijpath;
    var array: number[][];
    if (!(start == -1 || end == -1)) {
      this.mapservice.getDijpath(this.url, start, end, alpha).subscribe(data => {
         dijpath = data;
        console.log(dijpath);
         array = this.parseNodeString(dijpath);
        this.makeaLINE(array, this.myStyleDij);
        console.log("dijpath loaded");
      });

    } else {
      if (this.startla == null || this.endla == null) {
        alert("Please enter a start id and end id or chose cordinates for start and end")
      } else {
        console.log('using cordinates to get path '+this.startcor+ ' '+ this.endcor);
        this.mapservice.getDijcorpath(this.url, this.startcor, this.endcor, alpha).subscribe(data => {
          dijpath = data;
          console.log(dijpath);
          array = this.parseNodeString(dijpath);
          this.makeaLINE(array, this.myStyle);
          console.log("dijpath loaded");
        });
      }
    }
  }
  computAstar(start: number, end: number, alpha: string, landmark: number, candidate:number) {
    var astar:string;
    var array: number[][];
    if (!(start == -1 || end == -1)) {
      this.mapservice.getAstarpath(this.url, start, end, alpha, this.astartype, landmark, candidate).subscribe(data => {
         astar = data;
        console.log(astar);
         array = this.parseNodeString(astar);
        this.makeaLINE(array, this.myStyle);
        console.log("astarpath loaded");
      });
    } else {
      if (this.startla == null || this.endla == null) {
        alert("Please enter a start id and end id or chose cordinates for start and end")
      } else {
        console.log('using cordinates to get path '+this.startcor+ ' '+ this.endcor);
        this.mapservice.getAstarcorpath(this.url, this.startcor, this.endcor, alpha, this.astartype, landmark, candidate).subscribe(data => {
           astar = data;
          console.log(astar);
          array = this.parseNodeString(astar);
          this.makeaLINE(array, this.myStyle);
          console.log("astarpath loaded");
        });
      }

    }

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

  makeaLINE(nodearray: number[][], myStyle) {
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
      style: myStyle
    }).addTo(this.map);
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
      center: [39.75621, -104.99404],
      zoom: 20
    });
    const tiles = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    });
    tiles.addTo(this.map);
    const Coordinates = L.Control.extend({
      onAdd: map => {
        const container = L.DomUtil.create("div");
        map.addEventListener("click", e => {
          if (this.startchange) {
            this.startla = e.latlng.lat.toFixed(4);
            this.startlong = e.latlng.lng.toFixed(4);
            this.startcor=this.startla+' '+this.startlong;
            
          }
          if (this.endchange) {
            this.endla = e.latlng.lat.toFixed(4);
            this.endlong = e.latlng.lng.toFixed(4);
            this.endcor=this.endla+' '+this.endlong;
          }
          container.innerHTML = `
          <h2>Latitude is 
            ${e.latlng.lat.toFixed(4)} <br> and Longitude is  ${e.latlng.lng.toFixed(4)}
            </h2>
          `;
        });
        return container;
      }
    });
    this.map.addControl(new Coordinates({ position: "bottomleft" }));
  }



}
