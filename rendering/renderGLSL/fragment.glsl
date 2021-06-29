#version 450

in vec2 textureCoords;

uniform sampler2D textureSampler;

out vec4 fragColour;

void main() {
    float frag=texture(textureSampler,textureCoords).x;
    fragColour=vec4(frag,0,0,1);
}
