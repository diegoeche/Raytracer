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
  <75, 75, -75>,
  45
  pigment {
    color Blue
  }
}
sphere {
  <75, 30, -135>,
  45
  pigment {
    color Red
  }
}
