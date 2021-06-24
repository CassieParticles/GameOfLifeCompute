package lifeGaming.main;

public class Preset {
    private int width;
    private int height;

    private float[] data;

    public Preset(int width, int height, float[] data){
        this.width=width;
        this.height=height;

        this.data=data;
    }

    public float[] getData(){
        return data;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    public int[] getSize(){
        return new int[]{width,height};
    }
}
