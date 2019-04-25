#version 310 es

precision mediump float;

const vec2 vertex_offset[] = vec2[6](
    vec2(1, 1),	    // BR
    vec2(1, -1),	// TR
    vec2(-1, -1),	// TL

    vec2(1, 1),	    // BR
    vec2(-1, -1),	// TL
    vec2(-1, 1)	    // BL
);

const vec2 texture_offset[] = vec2[6](
    vec2(1, 1),	// BR
    vec2(1, 0),	// TR
    vec2(0, 0),	// TL

    vec2(1, 1),	// BR
    vec2(0, 0),	// TL
    vec2(0, 1)	// BL
);

out vec2 texCoords;

// Calculate vertex for this shader call
void emit_vertex()
{
    vec2 pos =  vertex_offset[gl_VertexID];

    gl_Position = vec4(pos, 0.f, 1.f);
}

void emit_tex_coords()
{
    // Write value to output interface block
    texCoords = texture_offset[gl_VertexID];
}

void main()
{
    emit_vertex();
    emit_tex_coords();
}