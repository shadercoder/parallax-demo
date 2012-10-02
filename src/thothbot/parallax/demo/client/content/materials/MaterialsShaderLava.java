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
 * Parallax. If not, see http://www.gnu.org/licenses/.
 */

package thothbot.parallax.demo.client.content.materials;

import java.util.Map;

import thothbot.parallax.core.client.gl2.enums.TextureWrapMode;
import thothbot.parallax.core.client.shaders.Uniform;
import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.cameras.PerspectiveCamera;
import thothbot.parallax.core.shared.geometries.TorusGeometry;
import thothbot.parallax.core.shared.materials.ShaderMaterial;
import thothbot.parallax.core.shared.objects.Mesh;
import thothbot.parallax.demo.client.ContentWidget;
import thothbot.parallax.demo.client.Demo;
import thothbot.parallax.demo.client.DemoAnnotations.DemoSource;
import thothbot.parallax.demo.resources.LavaShader;
import thothbot.parallax.plugin.postprocessing.client.BloomPass;
import thothbot.parallax.plugin.postprocessing.client.FilmPass;
import thothbot.parallax.plugin.postprocessing.client.Postprocessing;
import thothbot.parallax.plugin.postprocessing.client.RenderPass;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class MaterialsShaderLava extends ContentWidget 
{
	/*
	 * Prepare Rendering Scene
	 */
	@DemoSource
	class DemoScene extends DemoAnimatedScene 
	{

		private static final String img1 = "./static/textures/lava/cloud.png";
		private static final String img2 = "./static/textures/lava/lavatile.jpg";
		
		Map<String, Uniform> uniforms;
		Mesh mesh;
		
		@Override
		protected void loadCamera()
		{
			setCamera(
					new PerspectiveCamera(
							35, // fov
							getRenderer().getCanvas().getAspectRation(), // aspect 
							1, // near
							3000 // far 
					)); 
		}

		@Override
		protected void onStart()
		{
			getCamera().getPosition().setZ(4);
			getScene().add(getCamera());
			
			ShaderMaterial material = new ShaderMaterial(new LavaShader());
			uniforms = material.getShader().getUniforms();
			
			Texture texture1 = new Texture(img1);
			texture1.setWrapS(TextureWrapMode.REPEAT);
			texture1.setWrapT(TextureWrapMode.REPEAT);
			uniforms.get("texture1").setValue(texture1);
			
			Texture texture2 = new Texture(img2);
			texture2.setWrapS(TextureWrapMode.REPEAT);
			texture2.setWrapT(TextureWrapMode.REPEAT);

			uniforms.get("texture2").setValue(texture2);

			double size = 0.65;

			mesh = new Mesh( new TorusGeometry( size, 0.3, 30, 30 ), material );
			mesh.getRotation().setX( 0.3 );
			getScene().add( mesh );
	
			//

			Postprocessing composer = new Postprocessing( getRenderer(), getScene() );

			RenderPass renderModel = new RenderPass( getScene(), getCamera() );
			BloomPass effectBloom = new BloomPass( 1.25 );

			FilmPass effectFilm = new FilmPass( 0.35, 0.95, 2048, false );
			effectFilm.setRenderToScreen(true);

			composer.addPass( renderModel );
			composer.addPass( effectBloom );
			composer.addPass( effectFilm );
			
			getRenderer().setAutoClear(false);
		}
		
		@Override
		protected void onUpdate(double duration)
		{
			double delta = 5 * duration * 0.001;

			uniforms.get("time").setValue((Double)uniforms.get("time").getValue() + 0.2 * delta );

			mesh.getRotation().addX( 0.05 * delta );
			mesh.getRotation().addY( 0.0125 * delta );

			getRenderer().clear();
		}
	}
		
	public MaterialsShaderLava() 
	{
		super("Lava shader", "This example based on the three.js example.");
	}
	
	@Override
	public DemoScene onInitialize()
	{
		return new DemoScene();
	}

	@Override
	public ImageResource getIcon()
	{
		return Demo.resources.exampleMaterialsShaderLava();
	}
	
	@Override
	protected void asyncOnInitialize(final AsyncCallback<DemoAnimatedScene> callback)
	{
		GWT.runAsync(MaterialsShaderLava.class, new RunAsyncCallback() 
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
