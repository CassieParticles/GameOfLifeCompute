package GameOfLifeCompute.rendering.meshes;

import GameOfLifeCompute.rendering.Program;
import GameOfLifeCompute.rendering.Texture;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class TextureMesh {

    private int vaoId;
    private int vertexVboId;
    private int indexVboId;
    private int textVboId;

    private int vertexCount;

    public TextureMesh(float[] vertices, int[] indices, float[] textureCoords){
        vertexCount=indices.length;

        FloatBuffer verticesBuffer=null;
        IntBuffer indicesBuffer=null;
        FloatBuffer textureBuffer=null;

        try{
            vaoId= GL46.glGenVertexArrays();
            GL46.glBindVertexArray(vaoId);

            vertexVboId=GL46.glGenBuffers();
            verticesBuffer = MemoryUtil.memAllocFloat(vertices.length);
            verticesBuffer.put(vertices).flip();
            GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, vertexVboId);
            GL46.glBufferData(GL46.GL_ARRAY_BUFFER, verticesBuffer, GL46.GL_STATIC_DRAW);
            GL46.glEnableVertexAttribArray(0);
            GL46.glVertexAttribPointer(0, 2, GL46.GL_FLOAT, false, 0, 0);

            indexVboId=GL46.glGenBuffers();
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            GL46.glBindBuffer(GL46.GL_ELEMENT_ARRAY_BUFFER, indexVboId);
            GL46.glBufferData(GL46.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL46.GL_STATIC_DRAW);

            //Texture VBO
            textVboId=GL46.glGenBuffers();
            textureBuffer=MemoryUtil.memAllocFloat(textureCoords.length);
            textureBuffer.put(textureCoords).flip();
            GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, textVboId);
            GL46.glBufferData(GL46.GL_ARRAY_BUFFER, textureBuffer, GL46.GL_STATIC_DRAW);
            GL46.glEnableVertexAttribArray(1);
            GL46.glVertexAttribPointer(1, 2, GL46.GL_FLOAT, false, 0, 0);


        }finally{
            if(verticesBuffer!=null){
                MemoryUtil.memFree(verticesBuffer);
            }if(indicesBuffer!=null){
                MemoryUtil.memFree(indicesBuffer);
            }if(textureBuffer!=null){
                MemoryUtil.memFree(textureBuffer);
            }
        }
    }

    public void render(Program program, Texture gameTexture, Texture lastRender){
        program.useProgram();

        GL46.glActiveTexture(GL46.GL_TEXTURE0);
        GL46.glBindTexture(GL46.GL_TEXTURE_2D,gameTexture.getId());
        
        GL46.glActiveTexture(GL46.GL_TEXTURE1);
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, lastRender.getId());

        GL46.glBindVertexArray(getVaoId());

        GL46.glEnableVertexAttribArray(0);
        GL46.glEnableVertexAttribArray(1);

        program.setUniform("gameTexture", 0);
        program.setUniform("lastRender", 1);

        GL46.glDrawElements(GL46.GL_TRIANGLES, getVertexCount(), GL46.GL_UNSIGNED_INT, 0);

        GL46.glDisableVertexAttribArray(0);
        GL46.glDisableVertexAttribArray(1);

        GL46.glBindVertexArray(0);

        program.unlinkProgram();
    }


    public void cleanup(){
        GL46.glDisableVertexAttribArray(0);

        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, 0);
        GL46.glDeleteBuffers(vertexVboId);
        GL46.glDeleteBuffers(indexVboId);
        GL46.glDeleteBuffers(textVboId);

        // Delete the VAO
        GL46.glBindVertexArray(0);
        GL46.glDeleteVertexArrays(vaoId);
    }

    public int getVertexCount(){
        return vertexCount;
    }

    public int getVaoId(){
        return vaoId;
    }
}
