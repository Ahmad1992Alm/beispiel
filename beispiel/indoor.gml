<?xml version="1.0" encoding="UTF-8"?>
<IndoorGML xmlns="http://www.opengis.net/indoorgml/1.0/core"
           xmlns:gml="http://www.opengis.net/gml/3.2"
           xmlns:xlink="http://www.w3.org/1999/xlink"
           gml:id="IG1">
  <cellSpaceMember>
    <CellSpace gml:id="cs1">
      <gml:name>Room 1</gml:name>
    </CellSpace>
  </cellSpaceMember>
  <cellSpaceMember>
    <CellSpace gml:id="cs2">
      <gml:name>Room 2</gml:name>
    </CellSpace>
  </cellSpaceMember>
  <stateMember>
    <State gml:id="st1">
      <duality xlink:href="#cs1"/>
    </State>
  </stateMember>
  <stateMember>
    <State gml:id="st2">
      <duality xlink:href="#cs2"/>
    </State>
  </stateMember>
  <transitionMember>
    <Transition gml:id="tr1">
      <connects xlink:href="#st1"/>
      <connects xlink:href="#st2"/>
    </Transition>
  </transitionMember>
</IndoorGML>
