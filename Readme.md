# OpenWayback configuration

## wayback.xml

  <bean id="resourcefilelocationdb" class="org.archive.wayback.resourcestore.locationdb.RemoteResourceFileLocationDB">
	<constructor-arg index="0" value="http://localhost:8080/resourcestore"/>
  </bean>

  <bean name="8080:locationdb" class="org.archive.wayback.resourcestore.locationdb.ResourceFileLocationDBServlet">
    <property name="locationDB" ref="resourcefilelocationdb" />
  </bean>

  <import resource="RemoteCollection.xml"/>

  <property name="collection" ref="remotecollection" />


## RemoteCollection.xml

  <property name="resourceStore">
      <bean class="org.archive.wayback.resourcestore.SimpleResourceStore">
        <property name="prefix" value="http://localhost:8080/OWResourceStore/" />
      </bean>
  </property>


### For CDX server

    <property name="resourceIndex">
      <bean class="org.archive.wayback.resourceindex.RemoteResourceIndex">
        <property name="searchUrlBase" value="http://192.168.127.135:8080/oversixty" />
      </bean>
    </property>