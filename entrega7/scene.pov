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
  <0.0, 0.3, -8.0>,
  0.5
  pigment {
    color White
  }
  finish {ambient 0.0}
  finish {diffuse 0.1}
  finish {phong 0.3}
  finish {phong_size 20.}
  finish {refraction_index 0.98}
  finish {kr 0.9}


}

light_source {
  <10, 0, 0>
  color White
}

background { color Black }
global_settings {ambient_light color White}
