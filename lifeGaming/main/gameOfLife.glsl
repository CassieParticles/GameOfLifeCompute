#version 450
layout(local_size_x=1,local_size_y=1,local_size_z=1) in;

layout(location=0, r32f) uniform image2D texture0;	//I tried to use integer textures, please ask how to attach integer textures to compute shaders in the glsl discord
layout(location=1, r32f) uniform image2D texture1;

uniform int pixelsPerSquare;

uniform int paused;

float neighbors;

void main(){
	ivec2 pixelCoord=ivec2(gl_WorkGroupID.xy);

	vec4 pixel=imageLoad(texture0,pixelCoord);

	neighbors+=imageLoad(texture0,ivec2(pixelCoord+ivec2(-1,-1))).x;
	neighbors+=imageLoad(texture0,ivec2(pixelCoord+ivec2(-1, 0))).x;
	neighbors+=imageLoad(texture0,ivec2(pixelCoord+ivec2(-1, 1))).x;
	neighbors+=imageLoad(texture0,ivec2(pixelCoord+ivec2( 0,-1))).x;
	neighbors+=imageLoad(texture0,ivec2(pixelCoord+ivec2( 0, 1))).x;
	neighbors+=imageLoad(texture0,ivec2(pixelCoord+ivec2( 1,-1))).x;
	neighbors+=imageLoad(texture0,ivec2(pixelCoord+ivec2( 1, 0))).x;
	neighbors+=imageLoad(texture0,ivec2(pixelCoord+ivec2( 1, 1))).x;
		
	
	if(paused==0){
		if (neighbors==2){
			imageStore(texture1, pixelCoord, pixel);
		} else if (neighbors==3){
			imageStore(texture1, pixelCoord, vec4(1, 0, 0, 1));
		}else {
			imageStore(texture1, pixelCoord, vec4(0, 0, 0, 1));
		}
	}else{
		imageStore(texture1, pixelCoord, pixel);
	}
}