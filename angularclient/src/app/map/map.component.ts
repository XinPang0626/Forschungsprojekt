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
  startcor: string;
  endcor: string;
  
  //geoJSON layer
  geoJsonLayer;
  geoJSONdataLine;
  geoJSONdataStart;
  geoJSONdataEnd;

  //label for button 
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
  myStyleAstar = {
    "color": "#000080",
    "weight": 5,
    "opacity": 0.65
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
      this.mapservice.getDijpath(start, end, alpha).subscribe(data => {
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
        console.log('using cordinates to get path ' + this.startcor + ' ' + this.endcor);
        this.mapservice.getDijcorpath(this.startcor, this.endcor, alpha).subscribe(data => {
          dijpath = data;
          console.log(dijpath);
          array = this.parseNodeString(dijpath);
          this.makeaLINE(array, this.myStyleDij);
          console.log("dijpath loaded");
        });
      }
    }
  }

  loadASTAR(alpha: string, landmark: number, candidate: number) {
    this.mapservice.loadAstar(alpha, this.astartype, landmark, candidate).subscribe(data => {
      let astar = data;
      console.log(astar);
    });
  }

  computeASTARPATH(start: number, end: number) {
    var astar: string;
    var array: number[][];

    if (!(start == -1 || end == -1) && this.loaded) {
      this.mapservice.getAstarpath(start, end).subscribe(data => {
        astar = data;
        console.log(astar);
        array = this.parseNodeString(astar);
        this.makeaLINE(array, this.myStyleAstar);
        console.log("astarpath loaded");
      });
    } else {
      if (this.startla == null || this.endla == null) {
        alert("Please enter a start id and end id or chose cordinates for start and end")
      } else {
        console.log('using cordinates to get path ' + this.startcor + ' ' + this.endcor);
        this.mapservice.getAstarcorpath(this.startcor, this.endcor).subscribe(data => {
          astar = data;
          console.log(astar);
          array = this.parseNodeString(astar);
          this.makeaLINE(array, this.myStyleAstar);
          console.log("astarpath loaded");
        });
      }

    }

  }

 

  makeaLINE(nodearray: number[][], myStyle) {
    //remove previous line on map
    this.geoJsonLayer.removeLayer(this.geoJSONdataLine);
    this.geoJsonLayer.removeLayer(this.geoJSONdataEnd);
    this.geoJsonLayer.removeLayer(this.geoJSONdataStart);
    var mypointStylestart = {
      radius: 10,
      fillColor: "#6BBA29",
      color: "#A9EC71",
    };
    var mypointStylesend = {
      radius: 10,
      fillColor: "#FF5733",
      color: "#C70039",
    };

    this.changeMapp(nodearray[0][1],  nodearray[0][0]);
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
    var Pointstart = {
      "type": "FeatureCollection",
      "features": [
        {
          "type": "Feature",
          "properties": {},
          "geometry": {
            "type": "Point",
            "coordinates": [nodearray[0][0], nodearray[0][1]]
          }
        }
      ]
    }
    var Pointend = {
      "type": "FeatureCollection",
      "features": [
        {
          "type": "Feature",
          "properties": {},
          "geometry": {
            "type": "Point",
            "coordinates": [nodearray[nodearray.length-1][0], nodearray[nodearray.length-1][1]]
          }
        }
      ]
    }

    this.geoJSONdataLine= L.geoJSON(myLines, {style: myStyle  }).addTo(this.map);
    this.geoJSONdataEnd=L.geoJSON(Pointend, {
      pointToLayer: function (feature, latlng) {
          return L.circleMarker(latlng, mypointStylesend);
      }
  }).addTo(this.map);
    this.geoJSONdataStart=L.geoJSON(Pointstart, {
      pointToLayer: function (feature, latlng) {
          return L.circleMarker(latlng, mypointStylestart);
      }
  }).addTo(this.map);

    this.geoJsonLayer.addLayer(this.geoJSONdataLine);
    this.geoJsonLayer.addLayer(this.geoJSONdataEnd);
    this.geoJsonLayer.addLayer(this.geoJSONdataStart);
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
        var nodearray = this.parseNodeString(this.nodeString);
     
        this.changeMapp(nodearray[0][1], nodearray[0][0]);
       
        console.log("graph and quadtree are build");
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
            this.startcor = this.startla + ' ' + this.startlong;

          }
          if (this.endchange) {
            this.endla = e.latlng.lat.toFixed(4);
            this.endlong = e.latlng.lng.toFixed(4);
            this.endcor = this.endla + ' ' + this.endlong;
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
    this.geoJsonLayer= new L.LayerGroup();
    this.geoJsonLayer.addTo(this.map);

  }



}
