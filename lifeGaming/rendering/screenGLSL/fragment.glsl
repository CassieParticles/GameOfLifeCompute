#version 450

in vec2 textureCoords;
in vec3 outColour;

uniform sampler2D textureSampler;

out vec4 fragColour;

void main() {
	float enabled=texture(textureSampler,textureCoords).x;
	fragColour=vec4(outColour*max(0.3,enabled),1);
}
