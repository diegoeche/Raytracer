sphere{<-5,0, -10>,1 pigment{color Yellow}}
sphere{< 5,0, -10>,1 pigment{color Blue}}
plane {< 0,0,  -1>,5 pigment{color White}}

light_source {<0,10,-10> color White}
background {color Black}
camera {location <0,0,4> look_at <0,0,1>}
      
