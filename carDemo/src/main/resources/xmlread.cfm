<!---
	Basic rule of thumn 1 inch = 100 pixels
--->
<cfparam name="url.mode" default="">

<cfif url.mode eq "visio">
    <cfsetting enablecfoutputonly="Yes" showdebugoutput="No">
<cfelse>
    <cfsetting enablecfoutputonly="Yes">
</cfif>

<!---<cfset newLine = chr(10)>--->
<cfset newLine = "">

<cffunction name="inchToPixels" returntype="numeric">
	<cfargument name="inch" type="numeric" default="1" hint="1 represents 1 inch(2.5 cm) = 100 pixels">
	<cfreturn round(arguments.inch*10)>
</cffunction>

<cffunction name="flipCoord" returntype="numeric">
	<cfargument name="Coordinate" type="numeric">
	<cfreturn arguments.Coordinate*-1>
</cffunction>

<cffunction name="convertName" returntype="string">
	<cfargument name="name" type="string">
	<cfreturn ReplaceList(arguments.name," ,.","_,_")>
</cffunction>

<cffunction name="getNameFromConnectionString" returntype="string" hint="PAR(PNT(WheelParticle.69!Connections.X1,WheelParticle.69!Connections.Y1))">
	<cfargument name="string" type="string">
	
	<cfset var local = structNew()>
	<cfset local.returnString = arguments.string>
	<cfset local.returnString = listFirst(local.returnString)>
	<cfset local.returnString = listFirst(local.returnString,"!")>
	<cfset local.returnString = listLast(local.returnString,"(")>
	
	<cfreturn convertName(local.returnString)>
</cffunction>

<cffunction name="getProperty" returntype="string" hint="">
	<cfargument name="objectProperties" type="struct">
	<cfargument name="key" type="string">
	
	<cfset var local = structNew()>
	<cfset local.i = 0>	
	<cfset local.returnValue = "">	
	
	<cfset local.foundProperty = false>
	<cfloop index="local.i" from="1" to="#arrayLen(arguments.objectProperties.properties)#">
		<cfif arguments.objectProperties.properties[local.i].label eq arguments.key>
			
            <cfif arguments.objectProperties.properties[local.i].type eq 3>
            
                <!--- if type 3 boolean convert to true false string value, visio saves as 1, 0 --->        
                <cfif arguments.objectProperties.properties[local.i].value eq 1>
                    <cfset local.returnValue = "true">		
                <cfelse>
                    <cfset local.returnValue = "false">		
                </cfif>
                
            <cfelse>
                <cfset local.returnValue = arguments.objectProperties.properties[local.i].value>		
            </cfif>
            
			<cfset local.foundProperty = true>
			<cfbreak>
		</cfif>
	</cfloop>
	<!--- TG TODO look up master properties --->
	<cfif not local.foundProperty>
        <!--- property not found. --->
        <cfset local.returnValue = "null">
	</cfif>
	
	<cfreturn local.returnValue>
</cffunction>

<cffunction name="Rectangle">
	<cfargument name="objectProperties" type="struct">

	<cfset var local = structNew()>
	<cfset local.objectName = convertName(arguments.objectProperties.name)>
    <cfset local.fixed = getProperty(arguments.objectProperties,"fixed")>
    <cfset local.magnetic = getProperty(arguments.objectProperties,"magnetic")>

	<cfsavecontent variable="local.returnValue">
	<cfoutput>
		RectangleParticle #local.objectName# = new RectangleParticle(#arguments.objectProperties.x#,#flipCoord(arguments.objectProperties.y)#,#arguments.objectProperties.width#,#arguments.objectProperties.height#,#flipCoord(arguments.objectProperties.angle)#,#arguments.objectProperties.fixed#,1,0.3,0);
	</cfoutput>
	<cfif local.magnetic neq "null">
	<cfoutput>
		#local.objectName#.setMagnetic(#local.magnetic#);#newLine#
	</cfoutput>
    </cfif>
	<cfoutput>
    	APEngine.addParticle(#local.objectName#);#newLine#
    </cfoutput>
    </cfsavecontent>
    <cfreturn local.returnValue>
</cffunction>

<cffunction name="Circle">
	<cfargument name="objectProperties" type="struct">

	<cfset var local = structNew()>
	<cfset local.objectName = convertName(arguments.objectProperties.name)>
    <cfset local.angularVelocity = getProperty(arguments.objectProperties,"angularVelocity")>
    <cfset local.mass = getProperty(arguments.objectProperties,"mass")>
    <cfset local.fixed = getProperty(arguments.objectProperties,"fixed")>
    
    <!--- TG TODO, should inherit from master object. --->
    <cfif local.fixed eq "null">
        <cfset local.fixed = "true">
    </cfif>
    <cfset local.y = flipCoord(arguments.objectProperties.y)>

	<cfsavecontent variable="local.returnValue">
    <cfoutput>
        CircleParticle #local.objectName# = new CircleParticle(#arguments.objectProperties.x#,#local.y#,#round(arguments.objectProperties.width/2)#,#local.fixed#,1,0.3,0);#newLine#
	</cfoutput>
	<cfif local.mass neq "null">
	<cfoutput>
		#local.objectName#.setMass(#local.mass#);#newLine#
	</cfoutput>
    </cfif>
	<cfoutput>
    	APEngine.addParticle(#local.objectName#);#newLine#
    </cfoutput>
    </cfsavecontent>
    <cfreturn local.returnValue>
</cffunction>

<cffunction name="SpringConstraint">
	<cfargument name="objectProperties" type="struct">

	<cfset var local = structNew()>
	<cfset local.objectName = convertName(arguments.objectProperties.name)>
    <cfset local.stiffness = getProperty(arguments.objectProperties,"stiffness")>
    <cfset local.visible = getProperty(arguments.objectProperties,"visible")>
    <cfset local.collidable = getProperty(arguments.objectProperties,"collidable")>
    <cfset local.collisionRectWidth = getProperty(arguments.objectProperties,"collisionRectWidth")>
    <cfset local.collisionRectScale = getProperty(arguments.objectProperties,"collisionRectScale")>

	<cfsavecontent variable="local.returnValue">
	<cfoutput>
		SpringConstraint #local.objectName# = new SpringConstraint(#arguments.objectProperties.Connection.Object1#, #arguments.objectProperties.Connection.Object2#,1);#newLine#
	</cfoutput>
	<cfif local.stiffness neq "null">
	<cfoutput>
		#local.objectName#.setSiffness(#local.stiffness#);#newLine#
	</cfoutput>
    </cfif>
	<cfif local.visible neq "null">
	<cfoutput>
		#local.objectName#.setVisible(#local.visible#);#newLine#
	</cfoutput>
    </cfif>
	<cfif local.collidable neq "null">
	<cfoutput>
		#local.objectName#.setCollidable(#local.collidable#);#newLine#
	</cfoutput>
    </cfif>
	<cfif local.collisionRectWidth neq "null">
	<cfoutput>
		#local.objectName#.setCollisionRectWidth(#local.collisionRectWidth#);#newLine#
	</cfoutput>
    </cfif>
	<cfif local.collisionRectScale neq "null">
	<cfoutput>
		#local.objectName#.setCollisionRectScale(#local.collisionRectScale#);#newLine#
	</cfoutput>
    </cfif>
	<cfoutput>
		APEngine.addConstraint(#local.objectName#);#newLine#
	</cfoutput>
    </cfsavecontent>
    <cfreturn local.returnValue>
</cffunction>

<cffunction name="Wheel">
	<cfargument name="objectProperties" type="struct">

	<cfset var local = structNew()>
	<cfset local.objectName = convertName(arguments.objectProperties.name)>
    <cfset local.fixed = getProperty(arguments.objectProperties,"fixed")>
    <cfset local.angularVelocity = getProperty(arguments.objectProperties,"angularVelocity")>
    <cfset local.mass = getProperty(arguments.objectProperties,"mass")>
    <cfset local.friction = getProperty(arguments.objectProperties,"friction")>
    <cfset local.traction = getProperty(arguments.objectProperties,"traction")>
    
    <cfset local.y = flipCoord(arguments.objectProperties.y)>
    
	<cfsavecontent variable="local.returnValue">
	<cfoutput>
		WheelParticle #local.objectName# = new WheelParticle(#arguments.objectProperties.x#,#local.y#,#round(arguments.objectProperties.width/2)#,#local.fixed#,1,0.3,0,1);#newLine#
	</cfoutput>
	<cfif local.angularVelocity neq "null">
	<cfoutput>
		#local.objectName#.setAngularVelocity(#local.angularVelocity#);#newLine#
	</cfoutput>
    </cfif>
	<cfif local.mass neq "null">
	<cfoutput>
		#local.objectName#.setMass(#local.mass#);#newLine#
	</cfoutput>
    </cfif>
	<cfif local.friction neq "null">
	<cfoutput>
		#local.objectName#.setFriction(#local.friction#);#newLine#
	</cfoutput>
    </cfif>
	<cfif local.traction neq "null">
	<cfoutput>
		#local.objectName#.setTraction(#local.traction#);#newLine#
	</cfoutput>
    </cfif>
	<cfoutput>
		APEngine.addParticle(#local.objectName#);#newLine#
	</cfoutput>
    </cfsavecontent>
    <cfreturn local.returnValue>
</cffunction>

<cfset finalOutputContent = "">
<cffile action="READ" file="#GetDirectoryFromPath(GetCurrentTemplatePath())#sample.vdx" variable="aFile">
<cfset xml = xmlParse(aFile)>

<!---<cfdump var="#xml#"><cfabort>--->
<!---<cfdump var="#xml.VisioDocument.Masters#">--->

<!---<cfloop index="j" from="1" to="#arrayLen(xml.VisioDocument.Pages.xmlChildren)#">--->
<cfloop index="j" from="1" to="1">

	<!---<cfoutput>Pages : #j#</br></cfoutput>--->
		
	<!--- Get Page Name --->
	<cfset local.page.id = xml.VisioDocument.Pages.xmlChildren[j].XmlAttributes.ID>
	<cfset local.page.name = xml.VisioDocument.Pages.xmlChildren[j].XmlAttributes.NameU>

	
	<cfloop index="masterIterator" from="1" to="#arrayLen(xml.VisioDocument.Masters.Master)#">
		<!---<cfdump var="#xml.VisioDocument.Masters.Master[masterIterator].xmlAttributes.ID#">--->
	
		<!--- <cfloop index="propIterator" from="1" to="#arrayLen(xml.VisioDocument.Masters.Master[masterIterator].XmlChildren[2].Shape[1].xmlChildren)#">
			<cfdump var="#xml.VisioDocument.Masters.Master[masterIterator].XmlChildren[2].Shape[1].xmlChildren[propIterator].XmlName#">
			<cfif xml.VisioDocument.Masters.Master[masterIterator].XmlChildren[2].Shape[1].xmlChildren[propIterator].XmlName eq "Prop">
				<cfset property = structNew()>
				<cfset property.masterId = xml.VisioDocument.Masters.Master[masterIterator]>
				<cfset property.label = xml.VisioDocument.Pages.xmlChildren[j].Shapes.Shape[i].Prop[k].Label.xmlText>
				<cfset property.value = xml.VisioDocument.Pages.xmlChildren[j].Shapes.Shape[i].Prop[k].Value.xmlText>
				<!--- TG TODO need to translate this. --->
				<cfset property.type = xml.VisioDocument.Pages.xmlChildren[j].Shapes.Shape[i].Prop[k].Type.xmlText>
				
				<cfset master[property.masterId].properties[k] = structCopy(property)>
			</cfif>
		</cfloop> --->
		<!--- <cfif xml.VisioDocument.Masters.Master[masterIterator].XmlAttributes.id eq 22>
			<cfdump	var="#xml.VisioDocument.Masters.Master[masterIterator].XmlChildren[2].Shape[1].xmlChildren#">
			<cfdump	var="#xml.VisioDocument.Masters.Master[masterIterator].XmlChildren[2].Shapes.Shape[1].Prop#">
		</cfif>	 --->

		<!--- <cfset masters.master[masterIterator].id = xml.VisioDocument.Masters.Master[masterIterator].XmlAttributes.id> --->

		<!--- hard code the fact that there may be multiple objects that define a templated master object and look at specific properties. --->
				
		
		<!--->xml.VisioDocument.Masters.Master[masterIterator].xmlChildren[2].xmlChildren[1].xmlChildren[13]
		
		<cfset property = structNew()>
		<cfset property.label = xml.VisioDocument.Pages.xmlChildren[j].Shapes.Shape[i].Prop[k].Label.xmlText>
		<cfset property.value = xml.VisioDocument.Pages.xmlChildren[j].Shapes.Shape[i].Prop[k].Value.xmlText>
		<!--- TG TODO need to translate this. --->
		<cfset property.type = xml.VisioDocument.Pages.xmlChildren[j].Shapes.Shape[i].Prop[k].Type.xmlText>

		<cfset local.properties[k] = structCopy(property)>


		<!--- <cfdump	var="#xml.VisioDocument.Masters.Master[masterIterator].Prop#"> --->
		<cfif masters.master.id eq 22>
			<cfdump	var="#xml.VisioDocument.Masters.Master[masterIterator].xmlChildren[2].xmlChildren[1].xmlChildren[13]#"><cfabort>
		</cfif>	--->
	</cfloop>

	<cfloop index="i" from="1" to="#arrayLen(xml.VisioDocument.Pages.xmlChildren[j].Shapes.xmlChildren)#">

		<!---<cfoutput>Shapes : #i#</br></cfoutput>--->

		<cfset local = structNew()>
		<cfset local.id = xml.VisioDocument.Pages.xmlChildren[j].Shapes.xmlChildren[i].xmlAttributes.id>
		<cfset local.master = xml.VisioDocument.Pages.xmlChildren[j].Shapes.xmlChildren[i].xmlAttributes.master>
	
		<cfif structKeyExists(xml.VisioDocument.Pages.xmlChildren[j].Shapes.xmlChildren[i].xmlAttributes, "NameU")>
			<cfset local.name = xml.VisioDocument.Pages.xmlChildren[j].Shapes.xmlChildren[i].xmlAttributes.NameU>
		<cfelse>
			<cfset local.name = "unknown" & "." & local.id>

			<cfloop index="masterIterator" from="1" to="#arrayLen(xml.VisioDocument.Masters.Master)#">

				<cfif xml.VisioDocument.Masters.Master[masterIterator].XmlAttributes.id eq local.master>
					<cfset local.name = xml.VisioDocument.Masters.Master[masterIterator].XmlAttributes.NameU & "." & local.id>
					<cfbreak>
				</cfif>
			</cfloop>
		</cfif>
	
		<cfset local.x = inchToPixels(xml.VisioDocument.Pages.xmlChildren[j].Shapes.Shape[i].XForm.PinX.xmlText)>
		<cfset local.y = inchToPixels(xml.VisioDocument.Pages.xmlChildren[j].Shapes.Shape[i].XForm.PinY.xmlText)>
		<cfset local.width = inchToPixels(xml.VisioDocument.Pages.xmlChildren[j].Shapes.Shape[i].XForm.Width.xmlText)>
		<cfset local.height = inchToPixels(xml.VisioDocument.Pages.xmlChildren[j].Shapes.Shape[i].XForm.Height.xmlText)>
		<cfset local.angle = xml.VisioDocument.Pages.xmlChildren[j].Shapes.Shape[i].XForm.Angle.xmlText>
		<cfset local.LocPinX = inchToPixels(xml.VisioDocument.Pages.xmlChildren[j].Shapes.Shape[i].XForm.LocPinX.xmlText)>
		<cfset local.LocPinY = inchToPixels(xml.VisioDocument.Pages.xmlChildren[j].Shapes.Shape[i].XForm.LocPinY.xmlText)>
		<cfset local.FlipX = inchToPixels(xml.VisioDocument.Pages.xmlChildren[j].Shapes.Shape[i].XForm.FlipX.xmlText)>
		<cfset local.FlipY = inchToPixels(xml.VisioDocument.Pages.xmlChildren[j].Shapes.Shape[i].XForm.FlipY.xmlText)>
		<cfset local.Fixed = "true">
		
		<!---<cfdump var="#xml.VisioDocument.Pages.xmlChildren[j].Shapes.Shape[i]#">--->
		
		<!--- Properties have been defined.--->
		<cfif structKeyExists(xml.VisioDocument.Pages.xmlChildren[j].Shapes.Shape[i], "Connection")>
			<cfloop index="k" from="1" to="#arrayLen(xml.VisioDocument.Pages.xmlChildren[j].Shapes.Shape[i].Connection)#">

				<cfset Connection = structNew()>
				<cfset Connection.IX = xml.VisioDocument.Pages.xmlChildren[j].Shapes.Shape[i].Connection[k].xmlAttributes.IX>
				<!--- TG TODO need to translate this. --->
				<cfset Connection.type = xml.VisioDocument.Pages.xmlChildren[j].Shapes.Shape[i].Connection[k].Type.xmlText>

				<cfset local.Connection[k] = structCopy(Connection)>
			</cfloop>
		</cfif>

		<!--- Properties have been defined.--->
        <cfset local.properties = arrayNew(1)>
		<cfif structKeyExists(xml.VisioDocument.Pages.xmlChildren[j].Shapes.Shape[i], "Prop")>
			<cfloop index="k" from="1" to="#arrayLen(xml.VisioDocument.Pages.xmlChildren[j].Shapes.Shape[i].Prop)#">

				<cfset property = structNew()>
				<cfset property.label = xml.VisioDocument.Pages.xmlChildren[j].Shapes.Shape[i].Prop[k].Label.xmlText>
				<cfset property.value = xml.VisioDocument.Pages.xmlChildren[j].Shapes.Shape[i].Prop[k].Value.xmlText>
				<!--- TG TODO need to translate this. --->
				<cfset property.type = xml.VisioDocument.Pages.xmlChildren[j].Shapes.Shape[i].Prop[k].Type.xmlText>

                <!---<cfset local.properties[k] = structCopy(property)>--->
				<cfset ArrayAppend(local.properties, structCopy(property))>
			</cfloop>
		</cfif>
        
		<!--- get the connections too --->
		<!---<cfdump var="#local#">--->
		<!---<cfsavecontent variable="outputContent">--->
        
        <cfif listFind("9,23,26", local.master)>
			<cfset content = Rectangle(local)>
		<cfelseif local.master eq 12>
			<cfset content = Circle(local)>
		<cfelseif local.master eq 17>
			<!--- Connection Properties, should only exist if a Spring Constraint.  --->
			<cfif structKeyExists(xml.VisioDocument.Pages.xmlChildren[j].Shapes.Shape[i], "XForm1D")>
				<cfset local.Connection.BeginX = xml.VisioDocument.Pages.xmlChildren[j].Shapes.Shape[i].XForm1D.BeginX.xmlAttributes.F>
				<cfset local.Connection.BeginY = xml.VisioDocument.Pages.xmlChildren[j].Shapes.Shape[i].XForm1D.BeginY.xmlAttributes.F>
				<cfset local.Connection.EndX = xml.VisioDocument.Pages.xmlChildren[j].Shapes.Shape[i].XForm1D.EndX.xmlAttributes.F>
				<cfset local.Connection.EndY = xml.VisioDocument.Pages.xmlChildren[j].Shapes.Shape[i].XForm1D.EndY.xmlAttributes.F>
				<cfset local.Connection.Object1 = getNameFromConnectionString(local.Connection.BeginX)>
				<cfset local.Connection.Object2 = getNameFromConnectionString(local.Connection.EndX)>
			<cfelse>
				<!---<cfdump var="#local#">--->
			</cfif>
			<cfset content = SpringConstraint(local)>
		<cfelseif local.master eq 22>
			<cfset content = Wheel(local)>
			<!---<cfdump var="#local#">--->
		<cfelse>
			<cfdump var="#local#">
		</cfif>
		<!---<cfdump var="#local#">--->
		<!---<cfoutput><br></cfoutput>--->
        
        <!---</cfsavecontent>--->
        
        <cfset finalOutputContent = finalOutputContent & content>
	</cfloop>
</cfloop>

<cffile action="READ" file="#GetDirectoryFromPath(GetCurrentTemplatePath())#..\java\org\theo\aftokinito\World.java" variable="fileContents">


<!--- Replace content within start and end tags --->
<cfset startMarker = "// Start of Code-Gen">
<cfset endMarker = "// End of Code-Gen">
<cfset startPos = find(startMarker, fileContents)>
<cfset endPos = find(endMarker, fileContents, startPos)>
<cfset newContents = left(fileContents, startPos-1) 
    & startMarker
    & finalOutputContent 
    & right(fileContents, len(fileContents)-endPos+1)>
    
<!---<cfdump var="#startPos#">
<cfdump var="#endPos#">
<pre><cfoutput>#newContents#</cfoutput></pre>--->
<cfoutput>Done.</cfoutput>

<!---<cfdump var="#fileContents#">--->
 
<cffile action="WRITE" file="#GetDirectoryFromPath(GetCurrentTemplatePath())#..\java\org\theo\aftokinito\World.java" output="#newContents#">


