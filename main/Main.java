package GameOfLifeCompute.main;

import GameOfLifeCompute.rendering.Mesh;
import GameOfLifeCompute.rendering.Program;
import GameOfLifeCompute.rendering.Shader;
import GameOfLifeCompute.rendering.Texture;
import GameOfLifeCompute.utils.FileHandling;
import GameOfLifeCompute.utils.Timer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL46;

import GameOfLifeCompute.Input;

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

	private int screenSize=512;
	private int pixelsPerSquare=4;
	private int textureSize;

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
		window=new Window(screenSize,screenSize,"gameing");
		input=new Input();
		timer=new Timer(60,60);	//UPS,FPS
		presets=new Presets();
		
		window.init();
		input.init(window);

		textureSize=screenSize/pixelsPerSquare;
		
		texture0=new Texture(textureSize,textureSize,GL46.GL_R32F,GL46.GL_RED,GL46.GL_FLOAT);
		texture1=new Texture(textureSize,textureSize,GL46.GL_R32F,GL46.GL_RED,GL46.GL_FLOAT);

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
		
		screenProgram=new Program("Screen program");
		computeProgram=new Program("Compute program");
		
		
		screenProgram.attachShaders(new Shader[]{
				new Shader(FileHandling.loadResource("src/lifeGaming/rendering/screenGLSL/vertex.glsl"),GL46.GL_VERTEX_SHADER),
				new Shader(FileHandling.loadResource("src/lifeGaming/rendering/screenGLSL/fragment.glsl"),GL46.GL_FRAGMENT_SHADER)
		});
		
		computeProgram.attachShaders(new Shader[] {
				new Shader(FileHandling.loadResource("src/lifeGaming/main/gameOfLife.glsl"),GL46.GL_COMPUTE_SHADER)
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
		input();
		
		Texture writeTexture=renderTexture0?texture0:texture1;
		
		computeProgram.useProgram();
		
		computeProgram.setUniform("paused",paused?1:0);
		computeProgram.setUniform("texture0", 0);
		computeProgram.setUniform("texture1", 1);
		
		GL46.glBindImageTexture(0, renderTexture0?texture1.getId():texture0.getId(), 0, false, 0, GL46.GL_READ_ONLY, GL46.GL_R32F);
		GL46.glBindImageTexture(1, renderTexture0?texture0.getId():texture1.getId(), 0, false, 0, GL46.GL_WRITE_ONLY, GL46.GL_R32F);
		
		GL46.glDispatchCompute(textureSize,textureSize,1);
		
		GL46.glMemoryBarrier(GL46.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);
		
		GL46.glBindImageTexture(0, 0, 0, false, 0, GL46.GL_READ_ONLY, GL46.GL_R32F);
		GL46.glBindImageTexture(1, 0, 0, false, 0, GL46.GL_WRITE_ONLY, GL46.GL_R32F);
		
		computeProgram.unlinkProgram();
		
		if(input.isMouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
			writeTexture.writeToTexture(input.getMousePos()[0]/pixelsPerSquare, (input.getMousePos()[1]/pixelsPerSquare),1,1,new float[]{1});
		}else if(input.isMouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
			writeTexture.writeToTexture(input.getMousePos()[0]/pixelsPerSquare, (input.getMousePos()[1]/pixelsPerSquare),1,1,new float[]{0});
		}else if(input.isKeyDownThisUpdate(GLFW.GLFW_KEY_1)){
			writeTexture.writeToTexture(input.getMousePos()[0]/pixelsPerSquare, (input.getMousePos()[1]/pixelsPerSquare), presets.glider);
		}else if(input.isKeyDownThisUpdate(GLFW.GLFW_KEY_2)){
			writeTexture.writeToTexture(input.getMousePos()[0]/pixelsPerSquare, (input.getMousePos()[1]/pixelsPerSquare), presets.gosperGun);
		}

		renderTexture0=!renderTexture0;
		if(input.isKeyDownThisUpdate(GLFW.GLFW_KEY_SPACE)) {
			paused=!paused;
		}
		
		input.updateInputs();
	}
	
	private void input(){
		if(input.isKeyDown(GLFW.GLFW_KEY_ESCAPE)){
    		window.close();
    	}
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
