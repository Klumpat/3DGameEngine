package com.base.engine.core;

import com.base.engine.rendering.RenderUtil;
import com.base.engine.rendering.Window;

public class CoreEngine
{
	private boolean isRunning;
	private Game game;
	private int width;
	private int height;
	private double frameTime;
	
	public CoreEngine(int width, int height, double framerate, Game game)
	{
		this.isRunning = false;
		this.game = game;
		this.width = width;
		this.height = height;
		this.frameTime = 1.0/framerate;
	}

	private void initializeRenderingSystem()
	{
		System.out.println(RenderUtil.getOpenGLVersion());
		RenderUtil.initGraphics();
	}

	public void createWindow(String title)
	{
		Window.createWindow(width, height, title);
		initializeRenderingSystem();
	}

	public void start()
	{
		if(isRunning)
			return;
		
		run();
	}
	
	public void stop()
	{
		if(!isRunning)
			return;
		
		isRunning = false;
	}
	
	private void run()
	{
		isRunning = true;
		
		int frames = 0;
		long frameCounter = 0;

		game.init();

		long lastTime = Time.getTime();
		double unprocessedTime = 0;
		
		while(isRunning)
		{
			boolean render = false;
			
			long startTime = Time.getTime();
			long passedTime = startTime - lastTime;
			lastTime = startTime;
			
			unprocessedTime += passedTime / (double)Time.SECOND;
			frameCounter += passedTime;
			
			while(unprocessedTime > frameTime)
			{
				render = true;
				
				unprocessedTime -= frameTime;
				
				if(Window.isCloseRequested())
					stop();
				
				Time.setDelta(frameTime);
				
				game.input();
				Input.update();
				
				game.update();
				
				if(frameCounter >= Time.SECOND)
				{
					System.out.println(frames);
					frames = 0;
					frameCounter = 0;
				}
			}
			if(render)
			{
				render();
				//System.out.println(1000 * (double)(Time.getTime() - startTime)/((double)Time.SECOND) + "ms");
				frames++;
			}
			else
			{
				try
				{
					Thread.sleep(1);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		cleanUp();
	}
	
	private void render()
	{
		RenderUtil.clearScreen();
		//long startTime = Time.getTime();
		game.render();
		//double millisecond = 1000 * (double)(Time.getTime() - startTime)/((double)Time.SECOND);
		//System.out.println(millisecond + "ms (" + (int)(1.0/(millisecond / 1000)) + " fps)");
		Window.render();

	}
	
	private void cleanUp()
	{
		Window.dispose();
	}
}
