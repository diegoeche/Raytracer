sphere { 
  <0, 0, 3>, 
  2.5
  pigment { 
    color Blue 
  } 
}
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

