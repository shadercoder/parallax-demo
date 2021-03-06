/*
 * Copyright 2012 Alex Usachev, thothbot@gmail.com
 * 
 * This file is part of Parallax project.
 * 
 * Parallax is free software: you can redistribute it and/or modify it 
 * under the terms of the Creative Commons Attribution 3.0 Unported License.
 * 
 * Parallax is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the Creative Commons Attribution 
 * 3.0 Unported License. for more details.
 * 
 * You should have received a copy of the the Creative Commons Attribution 
 * 3.0 Unported License along with Parallax. 
 * If not, see http://creativecommons.org/licenses/by/3.0/.
 */

package thothbot.parallax.demo.client.content.geometries;

import java.util.ArrayList;
import java.util.List;

import thothbot.parallax.core.client.RenderingPanel;
import thothbot.parallax.core.client.events.AnimationReadyEvent;
import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Face3;
import thothbot.parallax.core.shared.core.Geometry;
import thothbot.parallax.core.shared.core.Object3D;
import thothbot.parallax.core.shared.geometries.IcosahedronGeometry;
import thothbot.parallax.core.shared.geometries.PlaneBufferGeometry;
import thothbot.parallax.core.shared.lights.DirectionalLight;
import thothbot.parallax.core.shared.materials.Material;
import thothbot.parallax.core.shared.materials.MeshBasicMaterial;
import thothbot.parallax.core.shared.materials.MeshLambertMaterial;
import thothbot.parallax.core.shared.math.Color;
import thothbot.parallax.core.shared.math.Vector3;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.core.shared.utils.SceneUtils;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class GeometryColors extends ContentWidget
{
	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{	
		private static final String texture = "./static/textures/shadow.png";
		
		PerspectiveCamera camera;
		
		public int mouseX;
		public int mouseY;

		@Override
		protected void onStart()
		{
			camera = new PerspectiveCamera( 20,
					getRenderer().getAbsoluteAspectRation(), 
					1, 
					10000 
			);
			
			camera.getPosition().setZ(1800);
			
			DirectionalLight light = new DirectionalLight( 0xffffff );
			light.getPosition().set( 0, 0, 1 );
			getScene().add( light );
			
			MeshBasicMaterial shadowMaterial = new MeshBasicMaterial();
			shadowMaterial.setMap( new Texture(texture) );
			PlaneBufferGeometry shadowGeo = new PlaneBufferGeometry( 300, 300, 1, 1 );
			
			Mesh mesh1 = new Mesh( shadowGeo, shadowMaterial );
			mesh1.getPosition().setY(-250);
			mesh1.getRotation().setX(- Math.PI / 2.0);
			getScene().add( mesh1 );

			Mesh mesh2 = new Mesh( shadowGeo, shadowMaterial );
			mesh2.getPosition().setY(-250);
			mesh2.getPosition().setX(-400);
			mesh2.getRotation().setX(- Math.PI / 2.0);
			getScene().add( mesh2 );

			Mesh mesh3 = new Mesh( shadowGeo, shadowMaterial );
			mesh3.getPosition().setY(-250);
			mesh3.getPosition().setX(400);
			mesh3.getRotation().setX(- Math.PI / 2.0);
			getScene().add( mesh3 );
			
			int radius = 200;
			
			Geometry geometry  = new IcosahedronGeometry( radius, 1 );
			Geometry geometry2 = new IcosahedronGeometry( radius, 1 );
			Geometry geometry3 = new IcosahedronGeometry( radius, 1 );
			
			for ( int i = 0; i < geometry.getFaces().size(); i ++ ) 
			{
				Face3 f  = geometry.getFaces().get( i );

				int n = 3;

				for( int j = 0; j < n; j++ ) 
				{
					int vertexIndex = (j == 0 ) ? f.getA()
							: (j == 1 ) ? f.getB()
							: (j == 2 ) ? f.getC() : 0;

					Vector3 p = geometry.getVertices().get( vertexIndex );

					Color color = new Color( 0xffffff );
					color.setHSL( ( p.getY() / (double)radius + 1.0 ) / 2.0, 1.0, 0.5 );

					geometry.getFaces().get( i ).getVertexColors().add(color);

					Color color2 = new Color( 0xffffff );
					color2.setHSL( 0.0, ( p.getY() / (double)radius + 1.0 ) / 2.0, 0.5 );

					geometry2.getFaces().get( i ).getVertexColors().add(color2);

					Color color3 = new Color( 0xffffff );
					color3.setHSL( (0.125 * vertexIndex / (double)geometry.getVertices().size()), 1.0, 0.5 );

					geometry3.getFaces().get( i ).getVertexColors().add(color3);
				}
			}

			List<Material> materials = new ArrayList<Material>();
			MeshLambertMaterial lmaterial = new MeshLambertMaterial();
			lmaterial.setColor( new Color(0xffffff) );
			lmaterial.setShading( Material.SHADING.FLAT );
			lmaterial.setVertexColors( Material.COLORS.VERTEX );
			materials.add(lmaterial);

			MeshBasicMaterial bmaterial = new MeshBasicMaterial();
			bmaterial.setColor( new Color(0x000000) );
			bmaterial.setShading( Material.SHADING.FLAT );
			bmaterial.setWireframe(true);
			bmaterial.setTransparent( true );
			materials.add(bmaterial);

			Object3D group1 = SceneUtils.createMultiMaterialObject( geometry, materials );
			group1.getPosition().setX(-400);
			group1.getRotation().setX(-1.87);
			getScene().add( group1 );

			Object3D group2 = SceneUtils.createMultiMaterialObject( geometry2, materials );
			group2.getPosition().setX(400);
			group2.getRotation().setX(0);
			getScene().add( group2 );

			Object3D group3 = SceneUtils.createMultiMaterialObject( geometry3, materials );
			group3.getPosition().setX(0);
			group3.getRotation().setX(0);
			getScene().add( group3 );
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			camera.getPosition().addX(( - mouseX - camera.getPosition().getX()) * 0.05 );
			camera.getPosition().addY(( mouseY - camera.getPosition().getY()) * 0.05 );

			camera.lookAt( getScene().getPosition());
			
			getRenderer().render(getScene(), camera);
		}
	}
		
	public GeometryColors()
	{
		super("Vertices colors", "Here are shown Icosahedrons and different vertex colors. At the bottom located shadow texture. Drag mouse to move. This example based on the three.js example.");
	}
	
	@Override
	protected void loadRenderingPanelAttributes(RenderingPanel renderingPanel) 
	{
		super.loadRenderingPanelAttributes(renderingPanel);
		renderingPanel.setBackground(0xDDDDDD);
	}

	@Override
	public void onAnimationReady(AnimationReadyEvent event)
	{
		super.onAnimationReady(event);

		this.renderingPanel.getCanvas().addMouseMoveHandler(new MouseMoveHandler() {
		      @Override
		      public void onMouseMove(MouseMoveEvent event)
		      {
		    	  	DemoScene rs = (DemoScene) renderingPanel.getAnimatedScene();
		    	  	rs.mouseX = event.getX(); 
		    	  	rs.mouseY = event.getY();
		      }
	      
		});
				
		this.renderingPanel.getCanvas().addTouchMoveHandler(new TouchMoveHandler() {
			
			@Override
			public void onTouchMove(TouchMoveEvent event) {
				
					DemoScene rs = (DemoScene) renderingPanel.getAnimatedScene();
					rs.mouseX = event.getTouches().get(0).getPageX(); 
		    	  	rs.mouseY = event.getTouches().get(0).getPageY();
				
			}
		});
	}

	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(GeometryColors.class, new RunAsyncCallback() 
		{
			public void onFailure(Throwable caught)
			{
				callback.onFailure(caught);
			}

			public void onSuccess()
			{
				callback.onSuccess(onInitialize());
			}
		});
	}

}
