/*
 * Copyright 2012 Alex Usachev, thothbot@gmail.com
 * 
 * This file is part of Parallax project.
 * 
 * Parallax is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by the 
 * Free Software Foundation, either version 3 of the License, or (at your 
 * option) any later version.
 * 
 * Parallax is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with 
 * Squirrel. If not, see http://www.gnu.org/licenses/.
 */

package thothbot.parallax.demo.client.content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import thothbot.parallax.core.client.AnimationReadyEvent;
import thothbot.parallax.core.client.RenderingPanel;
import thothbot.parallax.core.client.context.Canvas3d;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.core.Color3f;
import thothbot.parallax.core.shared.core.Geometry;
import thothbot.parallax.core.shared.core.Vector3f;
import thothbot.parallax.core.shared.materials.LineBasicMaterial;
import thothbot.parallax.core.shared.materials.Material.COLORS;
import thothbot.parallax.core.shared.objects.DimensionalObject;
import thothbot.parallax.core.shared.objects.Line;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;
import thothbot.parallax.postprocessing.client.BloomPass;
import thothbot.parallax.postprocessing.client.EffectComposer;
import thothbot.parallax.postprocessing.client.RenderPass;
import thothbot.parallax.postprocessing.client.ShaderPass;
import thothbot.parallax.postprocessing.client.shader.ShaderFxaa;
import thothbot.parallax.postprocessing.client.shader.ShaderScreen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class GeometryLinesColors extends ContentWidget 
{
	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{
		public int mouseX;
		public int mouseY;
		
		EffectComposer composer;
		
		@Override
		protected void loadCamera()
		{
			setCamera(
					new PerspectiveCamera(
							33, // fov
							getRenderer().getCanvas().getAspectRation(), // aspect 
							1, // near
							10000 // far 
					)); 
		}

		@Override
		protected void onStart()
		{
			getCamera().getPosition().setZ(700);
			getScene().addChild(getCamera());

			Geometry geometry = new Geometry();
			Geometry geometry2 = new Geometry();
			Geometry geometry3 = new Geometry();
			List<Vector3f> points = hilbert3D( new Vector3f( 0,0,0 ), 200.0f, 2, 0, 1, 2, 3, 4, 5, 6, 7 );

			List<Color3f> colors = new ArrayList<Color3f>(); 
			List<Color3f> colors2 = new ArrayList<Color3f>();
			List<Color3f> colors3 = new ArrayList<Color3f>();

			for ( int i = 0; i < points.size(); i ++ ) 
			{
				geometry.getVertices().add( points.get( i ) );

				colors.add( new Color3f( 0xffffff ) );
				colors.get( i ).setHSV( 0.6f, ( 200 + points.get( i ).getX() ) / 400f, 1.0f );

				colors2.add( new Color3f( 0xffffff ) );
				colors2.get( i ).setHSV( 0.3f, 1.0f, ( 200 + points.get( i ).getX() ) / 400f );

				colors3.add( new Color3f( 0xffffff ));
				colors3.get( i ).setHSV( i / points.size() * 1.0f, 1.0f, 1.0f );
			}

			geometry2.setVertices(geometry.getVertices()); 
			geometry3.setVertices(geometry.getVertices());

			geometry.setColors(colors);
			geometry2.setColors(colors2);
			geometry3.setColors(colors3);

			// lines

			LineBasicMaterial material = new LineBasicMaterial();
			material.setColor(new Color3f(0xffffff));
			material.setLinewidth(3);
			material.setOpacity(1);

			float scale = 0.3f;
			float d = 225;

			material.setVertexColors(COLORS.VERTEX);

			Line line = new Line( geometry,  material );
			line.getScale().set(scale * 1.5f);
			line.getPosition().set(-d, 0, 0);
			getScene().addChild( line );
			
			Line line2 = new Line( geometry2,  material );
			line2.getScale().set(scale * 1.5f);
			line2.getPosition().set(0, 0, 0);
			getScene().addChild( line2 );
			
			Line line3 = new Line( geometry2,  material );
			line3.getScale().set(scale * 1.5f);
			line3.getPosition().set(d, 0, 0);
			getScene().addChild( line3 );
			
			//

			RenderPass renderModel = new RenderPass( getScene(), getCamera() );
			BloomPass effectBloom = new BloomPass( 1.3f );
			
			ShaderPass effectScreen = new ShaderPass( new ShaderScreen() );
			effectScreen.setRenderToScreen(true);
			
			ShaderPass effectFXAA = new ShaderPass( new ShaderFxaa() );
//			effectFXAA.getUniforms().get("resolution").value.set( 1 / 500f, 1 / 500f);

			composer = new EffectComposer( getRenderer() );

			composer.addPass( renderModel );
			composer.addPass( effectFXAA );
			composer.addPass( effectBloom );
			composer.addPass( effectScreen );

			getRenderer().setAutoClear(false);
		}
		
		/**
		 * Port of Processing Java code by Thomas Diewald
		 * <a href="http://www.openprocessing.org/visuals/?visualID=15599">openprocessing.org</a>
		 */
		private List<Vector3f> hilbert3D( Vector3f center, float side, int iterations, 
				int v0, int v1, int v2, int v3, int v4, int v5, int v6, int v7 ) 
		{

			float half = side / 2.0f;

			List<Vector3f> vec_s = Arrays.asList(
					new Vector3f( center.getX() - half, center.getY() + half, center.getZ() - half ),
					new Vector3f( center.getX() - half, center.getY() + half, center.getZ() + half ),
					new Vector3f( center.getX() - half, center.getY() - half, center.getZ() + half ),
					new Vector3f( center.getX() - half, center.getY() - half, center.getZ() - half ),
					new Vector3f( center.getX() + half, center.getY() - half, center.getZ() - half ),
					new Vector3f( center.getX() + half, center.getY() - half, center.getZ() + half ),
					new Vector3f( center.getX() + half, center.getY() + half, center.getZ() + half ),
					new Vector3f( center.getX() + half, center.getY() + half, center.getZ() - half )
			);

			List<Vector3f> vec = Arrays.asList( 
					vec_s.get( v0 ), vec_s.get( v1 ), vec_s.get( v2 ), vec_s.get( v3 ), 
					vec_s.get( v4 ), vec_s.get( v5 ), vec_s.get( v6 ), vec_s.get( v7 ) );

			if( --iterations >= 0 ) 
			{
				List<Vector3f> tmp = new ArrayList<Vector3f>();
				tmp.addAll(hilbert3D ( vec.get( 0 ), half, iterations, v0, v3, v4, v7, v6, v5, v2, v1 ));
				tmp.addAll(hilbert3D ( vec.get( 1 ), half, iterations, v0, v7, v6, v1, v2, v5, v4, v3 ) );
				tmp.addAll(hilbert3D ( vec.get( 2 ), half, iterations, v0, v7, v6, v1, v2, v5, v4, v3 ) );
				tmp.addAll(hilbert3D ( vec.get( 3 ), half, iterations, v2, v3, v0, v1, v6, v7, v4, v5 ) );
				tmp.addAll(hilbert3D ( vec.get( 4 ), half, iterations, v2, v3, v0, v1, v6, v7, v4, v5 ) );
				tmp.addAll(hilbert3D ( vec.get( 5 ), half, iterations, v4, v3, v2, v5, v6, v1, v0, v7 ) );
				tmp.addAll(hilbert3D ( vec.get( 6 ), half, iterations, v4, v3, v2, v5, v6, v1, v0, v7 ) );
				tmp.addAll(hilbert3D ( vec.get( 7 ), half, iterations, v6, v5, v2, v1, v0, v3, v4, v7 ) );

				return tmp;
			}

			return vec;
		}
		
		@Override
		protected void onStop()
		{			
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			getCamera().getPosition().addX( ( mouseX - getCamera().getPosition().getX() ) * .05f );
			getCamera().getPosition().addY( ( - mouseY + 200.0f - getCamera().getPosition().getY() ) * .05f );

			getCamera().lookAt( getScene().getPosition() );

			for ( int i = 0; i < getScene().getChildren().size(); i ++ ) 
			{
				DimensionalObject object = getScene().getChildren().get(i);
				if ( object instanceof Line ) 
					object.getRotation().setY( (float) (duration * 0.0005 * ( (i % 2 > 0) ? 1 : -1 )) );
			}

			getRenderer().clear(false, false, false);
			composer.render(getRenderer(), 0);
		}
	}
		
	public GeometryLinesColors() 
	{
		super("Hilbert curves", "Drag mouse to move. This example based on the three.js example.");
	}
	
	@Override
	protected void loadRenderingPanelAttributes(RenderingPanel renderingPanel) 
	{
		super.loadRenderingPanelAttributes(renderingPanel);
		renderingPanel.getCanvas3dAttributes().setAntialiasEnable(false);
	}
	
	@Override
	public void onAnimationReady(AnimationReadyEvent event)
	{
		super.onAnimationReady(event);

		this.renderingPanel.getRenderer().getCanvas().addMouseMoveHandler(new MouseMoveHandler() {
		      @Override
		      public void onMouseMove(MouseMoveEvent event)
		      {
		    	  	DemoScene rs = (DemoScene) renderingPanel.getAnimatedScene();
		    	  	Canvas3d canvas = renderingPanel.getRenderer().getCanvas();
		    	  	rs.mouseX = (event.getX() - canvas.getWidth() / 2 ) * 3; 
		    	  	rs.mouseY = (event.getY() - canvas.getHeight() / 2) * 3;
		      }
		});
	}
	
	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}

	@Override
	public ImageResource getIcon()
	{
		return Demo.resources.exampleGeometryLinesColors();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(GeometryLinesColors.class, new RunAsyncCallback() 
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