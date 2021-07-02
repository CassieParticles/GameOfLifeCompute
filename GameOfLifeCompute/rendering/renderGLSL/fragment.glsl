#version 450

in vec2 textureCoords;

uniform sampler2D gameTexture;
uniform sampler2D lastRender;

out vec4 fragColour;

void main() {
    float val=texture(gameTexture,vec2(textureCoords.x,1-textureCoords.y)).x;
    float lastVal=texture(lastRender,vec2(textureCoords.x,1-textureCoords.y)).x;
    fragColour=vec4(max(val,lastVal-0.01),0,0,1);
}
