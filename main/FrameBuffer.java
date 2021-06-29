package GameOfLifeCompute.main;

import GameOfLifeCompute.rendering.Texture;
import org.lwjgl.opengl.GL46;

public class FrameBuffer {
    private int id;

    private int width;
    private int height;

    private Texture texture;
    private int attachment;

    public FrameBuffer(){

    }

    public void init(int width, int height, Texture texture, int attachment){
        id= GL46.glGenFramebuffers();
        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER,id);

        this.width=width;
        this.height=height;
        this.texture=texture;
        this.attachment=attachment;

        GL46.glFramebufferTexture2D(GL46.GL_FRAMEBUFFER,attachment,GL46.GL_TEXTURE_2D,texture.getId(),0);

        GL46.glDrawBuffers(new int[]{attachment});

        if(GL46.glCheckFramebufferStatus(GL46.GL_FRAMEBUFFER)!=GL46.GL_FRAMEBUFFER_COMPLETE){
            System.err.println("ERROR: frame buffer failed to complete");
        }

        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER,0);
    }

    public int getId(){
        return id;
    }

    public Texture getTexture(){
        return texture;
    }

    public int getAttachment() {
        return attachment;
    }

    public void bindFrameBuffer(){
        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER,id);
    }

    public void unbindFrameBuffer(){
        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER,0);
    }

    public void cleanup(){
        texture.cleanup();
        GL46.glDeleteFramebuffers(id);
    }
}
