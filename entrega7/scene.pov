sphere {
  <0.5, 0.5, -0.5>,
  0.3
  pigment {
    color Blue
  }
  finish {ambient 0.1}
  finish {diffuse 0.3}
  finish {phong 0.7}
  finish {phong_size 20.0}
}
light_source {
  <-10, 10, -10>
  color White
}

sphere {
  <0.0, 0.0, -0.8>,
  0.1
  pigment {
    color Red
  }
  finish {ambient 0.1}
  finish {diffuse 0.3}
  finish {phong 0.7}
  finish {phong_size 20.0}
}

global_settings {ambient_light color White}