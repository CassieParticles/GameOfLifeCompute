#version 450

in vec2 textureCoords;

uniform sampler2D gameTexture;
uniform sampler2D lastRender;
uniform float fadeRate;
uniform int paused;

out vec4 fragColour;

void main() {
    float val=texture(gameTexture,vec2(textureCoords.x,1-textureCoords.y)).x;
    float lastVal=texture(lastRender,vec2(textureCoords.x,1-textureCoords.y)).x;
    fragColour=vec4(max(val,(lastVal-(fadeRate*(1-paused)))),0,0,1);
}
