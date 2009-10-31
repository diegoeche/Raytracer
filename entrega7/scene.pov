sphere {
  <0.0, 0.0, -11.0>,
  0.3
  pigment {
    color Green
  }
  finish {ambient 0.1}
  finish {diffuse 0.3}
  finish {phong 0.3}
  finish {phong_size 20.0}
  finish {reflection {0.5}}

}

sphere {
  <0.0, -1.0, -10.0>,
  0.5
  pigment {
    color Green
  }
  finish {ambient 0.1}
  finish {diffuse 0.3}
  finish {phong 0.3}
  finish {phong_size 20.0}
  finish {reflection {0.5}}

}
sphere {
  <1.0, 0.0, -10.0>,
  0.5
  pigment {
    color Blue
  }
  finish {ambient 0.1}
  finish {diffuse 0.3}
  finish {phong 0.3}
  finish {phong_size 20.0}
  finish {reflection {0.5}}

}
sphere {
  <-1.0, 0.0, -10.0>,
  0.5
  pigment {
    color White
  }
  finish {ambient 0.1}
  finish {diffuse 0.3}
  finish {phong 0.3}
  finish {phong_size 20.0}
  finish {reflection {0.8}}

}

sphere {
  <0.0, 1.0, -10.0>,
  0.5
  pigment {
    color Red
  }
  finish {ambient 0.1}
  finish {diffuse 0.3}
  finish {phong 0.3}
  finish {phong_size 20.0}
}


light_source {
  <0, 5, 0>
  color Blue
}

light_source {
  <0, -5, 0>
  color Red
}
light_source {
  <0, 0, 0>
  color Green
}

global_settings {ambient_light color White}
