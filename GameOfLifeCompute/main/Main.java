package GameOfLifeCompute.main;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL46;

import GameOfLifeCompute.Input;
import GameOfLifeCompute.rendering.Mesh;
import GameOfLifeCompute.rendering.Program;
import GameOfLifeCompute.rendering.Shader;
import GameOfLifeCompute.rendering.Texture;
import GameOfLifeCompute.utils.FileHandling;
import GameOfLifeCompute.utils.Timer;

public class Main {
	
	public Timer timer;
	public Input input;
	
	private Window window;
	
	private Mesh screen;

	private Presets presets;
	
	private Program screenProgram;
	private Program computeProgram;
	
	private Texture texture0;
	private Texture texture1;

	private int screenWidth;
	private int screenHeight;
	
	private int pixelsPerSquare=4;
	private int textureWidth;
	private int textureHeight;

	private boolean renderTexture0;
	private boolean paused;

	
	public static void main(String[] args){
		new Main().gameLoop();
	}
	
	private void gameLoop(){
		try{
			init();
			loop();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			cleanup();
		}
	}
	
	private void init() throws Exception{
		window=new Window(-1,-1,"gameing");
		screenWidth=window.getWidth();
		screenHeight=window.getHeight();
		input=new Input();
		timer=new Timer(60,60);	//UPS,FPS
		presets=new Presets();
		
		window.init();
		input.init(window);

		textureWidth=screenWidth/pixelsPerSquare;
		textureHeight=screenHeight/pixelsPerSquare;
		
		texture0=new Texture(textureWidth,textureHeight,GL46.GL_R32F,GL46.GL_RED,GL46.GL_FLOAT);
		texture1=new Texture(textureWidth,textureHeight,GL46.GL_R32F,GL46.GL_RED,GL46.GL_FLOAT);

		//Creating the screen
		screen=new Mesh(new float[]{
				-1,1,
				1,1,
				1,-1,
				-1,-1
			},new int[]{
				0,1,2,
				0,2,3
			},
			new float[]{
					0,0,
					1,0,
					1,1,
					0,1
			},new float[]{
					1,0,1,
					0,1,1,
					0,0,1,
					0.5f,0,1
			});
		
		//Generating shaders and programs
		screenProgram=new Program("Screen program");
		computeProgram=new Program("Compute program");
		
		
		screenProgram.attachShaders(new Shader[]{
				new Shader(FileHandling.loadResource("src/GameOfLifeCompute/rendering/screenGLSL/vertex.glsl"),GL46.GL_VERTEX_SHADER),
				new Shader(FileHandling.loadResource("src/GameOfLifeCompute/rendering/screenGLSL/fragment.glsl"),GL46.GL_FRAGMENT_SHADER)
		});
		
		computeProgram.attachShaders(new Shader[] {
				new Shader(FileHandling.loadResource("src/GameOfLifeCompute/main/gameOfLife.glsl"),GL46.GL_COMPUTE_SHADER)
		});
		
		screenProgram.link();
		computeProgram.link();
		
		screenProgram.createUniform("textureSampler");

		computeProgram.createUniform("paused");
		computeProgram.createUniform("texture0");
		computeProgram.createUniform("texture1");
		
		GL46.glClearColor(0.1f, 0.1f, 0.2f, 1.0f);
		
		window.loop();
	}
	
	private void loop(){
		while(!window.shouldClose()){
			timer.update();
			if(timer.getUpdate()){
				update();
			}if(timer.getFrame()){
				render();
			}
		}
		
	}
	
	private void render(){
		window.loop();
		GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
		screen.render(screenProgram, new Texture[]{texture0,texture1},renderTexture0?0:1);
	}
	
	private void update(){
		if(input.isKeyDown(GLFW.GLFW_KEY_ESCAPE)){
    		window.close();
    	}
		
		//Pointers to read-write textures
		Texture readTexture=renderTexture0?texture1:texture0;
		Texture writeTexture=renderTexture0?texture0:texture1;
		
		computeProgram.useProgram();
		
		computeProgram.setUniform("paused",paused?1:0);
		computeProgram.setUniform("texture0", 0);
		computeProgram.setUniform("texture1", 1);
		
		//Binding textures
		GL46.glBindImageTexture(0, readTexture.getId(), 0, false, 0, GL46.GL_READ_ONLY, GL46.GL_R32F);
		GL46.glBindImageTexture(1, writeTexture.getId(), 0, false, 0, GL46.GL_WRITE_ONLY, GL46.GL_R32F);
		
		//Running compute shader
		GL46.glDispatchCompute(textureWidth,textureHeight,1);
		
		//Waits until all accesses to a texture are done to continue
		GL46.glMemoryBarrier(GL46.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);
		
		//Unbinding textures
		GL46.glBindImageTexture(0, 0, 0, false, 0, GL46.GL_READ_ONLY, GL46.GL_R32F);
		GL46.glBindImageTexture(1, 0, 0, false, 0, GL46.GL_WRITE_ONLY, GL46.GL_R32F);
		
		computeProgram.unlinkProgram();
		
		int[] mousePos=input.getMousePos();
		mousePos[0]/=pixelsPerSquare;
		mousePos[1]/=pixelsPerSquare;
		
		//User inputs to draw on
		if(input.isMouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
			writeTexture.writeToTexture(mousePos[0], (mousePos[1]),1,1,new float[]{1});
		}else if(input.isMouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
			writeTexture.writeToTexture(mousePos[0], (mousePos[1]),1,1,new float[]{0});
		}else if(input.isKeyPressed(GLFW.GLFW_KEY_1)){
			writeTexture.writeToTexture(mousePos[0], (mousePos[1]), presets.glider);
		}else if(input.isKeyPressed(GLFW.GLFW_KEY_2)){
			writeTexture.writeToTexture(mousePos[0], (mousePos[1]), presets.gosperGun);
		}else if(input.isKeyPressed(GLFW.GLFW_KEY_3)){
			writeTexture.writeToTexture(mousePos[0], (mousePos[1]), Presets.Pentadecathlon);
		}else if(input.isKeyPressed(GLFW.GLFW_KEY_4)){
			writeTexture.writeToTexture(mousePos[0], (mousePos[1]), Presets.RPentomino);
		}else if(input.isKeyPressed(GLFW.GLFW_KEY_5)){
			writeTexture.writeToTexture(mousePos[0], (mousePos[1]), Presets.DieHard);
		}else if(input.isKeyPressed(GLFW.GLFW_KEY_6)){
			writeTexture.writeToTexture(mousePos[0], (mousePos[1]), Presets.Acorn);
		}else if(input.isKeyPressed(GLFW.GLFW_KEY_7)){
			writeTexture.writeToTexture(mousePos[0], (mousePos[1]), Presets.Infinite);
		}

		renderTexture0=!renderTexture0;
		
		if(input.isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
			paused=!paused;
		}
		
		input.updateInputs();
	}
	
	private void cleanup(){
		screen.cleanup();
		window.cleanup();
		
		screenProgram.cleanup();
		computeProgram.cleanup();
		
		texture0.cleanup();
		texture1.cleanup();
	}
}
