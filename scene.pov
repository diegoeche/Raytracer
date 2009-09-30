background { 
  color Black 
}
sphere { 
  <2, -2, -3>, 
  2.5
  pigment { 
    color Blue 
  } 
}
sphere { 
  <-2, -2, -5>, 
  1.5 
  pigment { 
    color Red 
  } 
}
light_source { 
  <10, -10, 10> 
  color White
}  
plane { 
  <0, -1, 0>, 
  0
  pigment { 
    color Green 
  }
}

camera {
  location <0, -4, 8>
  look_at  <0, 0, -4>
}
