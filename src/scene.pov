background {
  color Black 
}

light_source { 
  <10, 10, -10> 
  color White
}  

camera {
  location <0, 4, -8>
  look_at  <0, 0, 4>
}

sphere {
  <0, 0, -75>,
  0.5
  pigment {
    color Blue
  }
}
sphere {
  <0, 0, -500>,
  0.9
  pigment {
    color Red
  }
}
