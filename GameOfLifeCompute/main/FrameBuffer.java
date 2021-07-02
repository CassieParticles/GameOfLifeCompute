package GameOfLifeCompute.main;

import org.lwjgl.opengl.GL46;

import GameOfLifeCompute.rendering.Texture;

public class FrameBuffer {
    private int id;

    private int width;
    private int height;

    private Texture texture0;
    private Texture texture1;

    public FrameBuffer(){

    }

    public void init(int width, int height){
        id= GL46.glGenFramebuffers();
        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER,id);

        this.width=width;
        this.height=height;
        this.texture0=new Texture(width,height,GL46.GL_R32F,GL46.GL_RED,GL46.GL_FLOAT);
        this.texture1=new Texture(width,height,GL46.GL_R32F,GL46.GL_RED,GL46.GL_FLOAT);

        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER,0);
    }

    public int getId(){
        return id;
    }

    public Texture getTexture(int texture){
        return texture==0?texture0:texture1;
    }

    public int getAttachment() {
        return GL46.GL_COLOR_ATTACHMENT0;
    }

    public void bindFrameBuffer(){
        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER,id);
    }

    public void unbindFrameBuffer(){
        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER,0);
    }
    
    public void bindTexture(int texture){
    	GL46.glFramebufferTexture2D(GL46.GL_FRAMEBUFFER,GL46.GL_COLOR_ATTACHMENT0,GL46.GL_TEXTURE_2D,0,0);//Unbinds the current texture
    	
    	GL46.glFramebufferTexture2D(GL46.GL_FRAMEBUFFER,GL46.GL_COLOR_ATTACHMENT0,GL46.GL_TEXTURE_2D,texture==0?texture0.getId():texture1.getId(),0);
    }

    public void cleanup(){
        texture0.cleanup();
        texture1.cleanup();
        GL46.glDeleteFramebuffers(id);
    }
}
